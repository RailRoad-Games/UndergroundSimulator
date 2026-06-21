package de.qstudios.underground.util;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Startet den Underground-Simulator ueber einen einfachen VERSIONs-Launcher.
 *
 * <p>Der Launcher liest verfuegbare VERSIONen aus dem Maven-Repository,
 * bietet dem Benutzer eine Auswahl an, laedt bei Bedarf das passende JAR
 * herunter und startet anschliessend die enthaltene Hauptklasse.</p>
 */
public class Launcher {
    /** Basis-URL des Maven-Repositories fuer Release- und Snapshot-Artefakte. */
    private static final String REPO_BASE = "https://mvn.deepslatesmp.de/repository/maven-public/de/qstudios/underground/underground/";
    /** Lokales Zielverzeichnis fuer heruntergeladene Launcher-Artefakte. */
    private final Path binFolder = Path.of(System.getProperty("user.home"), ".config", "us", "bin");

    /**
     * Einstiegspunkt fuer den VERSIONs-Launcher.
     *
     * @param args aktuell ungenutzte Startargumente
     */
    public static void main(String[] args) {
        new Launcher().run();
    }

    /**
     * Fuehrt den interaktiven Ablauf des Launchers aus.
     *
     * <p>Dabei werden verfuegbare VERSIONen geladen, optional Snapshot-Builds
     * aufgeloest, fehlende JAR-Dateien heruntergeladen und anschliessend
     * gestartet.</p>
     */
    public void run() {
        System.out.println("=== Underground Launcher v0.5 ===");
        try {
            if (!Files.exists(binFolder))
                Files.createDirectories(binFolder);
            Scanner scanner = new Scanner(System.in);

            // 1. Haupt-Metadaten laden (Alle VERSIONsnummern)
            byte[] mainXml = Downloader.download(new URI(REPO_BASE + "maven-metadata.xml").toURL());
            List<String> availableVERSIONs = parseMainMetadata(mainXml);

            System.out.println("\nWähle eine VERSION:");
            for (int i = 0; i < availableVERSIONs.size(); i++) {
                boolean exists = Files.exists(binFolder.resolve(availableVERSIONs.get(i) + ".jar"));
                System.out.printf("[%d] %s %s%n", i, availableVERSIONs.get(i), exists ? "(Lokal vorhanden)" : "(Download erforderlich)");
            }
            System.out.print("Nummer wählen: ");
            String selectedBaseVERSION = availableVERSIONs.get(scanner.nextInt());

            String downloadUrl;
            String localFileName;
            String snapshotMajorName = null;
            // 2. SNAPSHOT-Handling mit Build-Auswahl
            if (selectedBaseVERSION.endsWith("-SNAPSHOT")) {
                URL url = new URI(
                        "https://mvn.deepslatesmp.de/service/rest/repository/browse/maven-public/de/qstudios/underground/underground/"
                                + selectedBaseVERSION + "/")
                        .toURL();
                String html = new String(Downloader.download(url), StandardCharsets.UTF_8);

                List<VERSION> snapshotBuilds = parseAllSnapshotBuildsFromHTML(selectedBaseVERSION, html);

                // Auswahl anzeigen...
                System.out.println("\nVerfügbare Builds für " + selectedBaseVERSION + ":");
                for (int i = 0; i < snapshotBuilds.size(); i++) {
                    System.out.printf("[%d] %s%n", i, snapshotBuilds.get(i));
                }
                snapshotMajorName = selectedBaseVERSION;
                System.out.print("Wähle einen Build: ");
                VERSION selectedBuild = snapshotBuilds.get(scanner.nextInt());

                // WICHTIG: Die Datei liegt im Unterordner des Builds!
                // Pfad-Schema: .../SNAPSHOT-ORDNER/BUILD-ORDNER/artifact-BUILD.jar
                // Entferne den mittleren Teil "/ + selectedBuild.value() + /"
                // Richtig: Wir springen direkt vom selectedBaseVERSION (SNAPSHOT-Ordner) zur
                // Datei
                downloadUrl = REPO_BASE + selectedBaseVERSION + "/underground-" + selectedBuild.value() + ".jar";
                localFileName = selectedBuild.value() + ".jar";
                snapshotMajorName = binFolder.resolve(snapshotMajorName).toString();

            } else {
                // Normales Release
                downloadUrl = REPO_BASE + selectedBaseVERSION + "/underground-" + selectedBaseVERSION + ".jar";
                localFileName = selectedBaseVERSION + ".jar";
            }

            // 3. Download & Launch
            Path jarPath = binFolder.resolve(localFileName);
            if (!Files.exists(jarPath)) {
                System.out.println("Downloade: " + downloadUrl);
                Files.write(jarPath, Downloader.download(new URI(downloadUrl).toURL()));
                if (selectedBaseVERSION.endsWith("-SNAPSHOT")) {
                    Files.write(Path.of(binFolder.resolve(selectedBaseVERSION+".jar").toAbsolutePath().toUri()), "this is a placeholder".getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                }
            }
            boolean newer = false;
            if (!selectedBaseVERSION.startsWith("v0")) newer = true;
            launchJar(jarPath, newer);
            scanner.close();
            System.exit(0);
        } catch (Exception e) {
            System.err.println("\n[FEHLER] " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Laedt ein heruntergeladenes Spiel-JAR in einen separaten ClassLoader und
     * fuehrt dessen {@code main}-Methode aus.
     *
     * @param jarPath Pfad zur zu startenden JAR-Datei
     * @throws Exception wenn das Laden oder Starten der Anwendung fehlschlaegt
     */
    private void launchJar(Path jarPath, boolean newerThan1_0) throws Exception {
        System.out.println("Starte " + jarPath.getFileName() + "...\n---");

        URL[] urls = { jarPath.toUri().toURL() };
        // Nutzt PlatformClassLoader, damit HttpClient etc. gefunden werden
        try (URLClassLoader loader = new URLClassLoader(urls, ClassLoader.getPlatformClassLoader())) {
            Class<?> mainClass = Class.forName("de.qstudios.underground.Main", true, loader);
            Method mainMethod;
            Object[] params;

            try {
                mainMethod = mainClass.getDeclaredMethod("main", String[].class);
                params = new Object[] { new String[] {} };
                if (newerThan1_0) {
                    // Neuere Versionen erwarten zusätzlich die Build-Info als Argument
                    params = new Object[] { new String[] {"--LauncherEmulation"}};
                }
            } catch (NoSuchMethodException e) {
                mainMethod = mainClass.getDeclaredMethod("main");
                params = new Object[] {};
            }

            mainMethod.setAccessible(true);
            Object instance = null;
            if (!java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers())) {
                instance = mainClass.getDeclaredConstructor().newInstance();
            }

            try {
                mainMethod.invoke(instance, params);
            } catch (InvocationTargetException e) {
                System.err.println("Die App ist abgestürzt:");
                e.getCause().printStackTrace();
            }
        }
    }

    /**
     * Liest alle verfuegbaren VERSIONsnummern aus der Haupt-Metadaten-Datei des
     * Repositories.
     *
     * @param xml Inhalt von {@code maven-metadata.xml}
     * @return Liste aller enthaltenen VERSIONseintraege
     * @throws Exception wenn das XML nicht geparst werden kann
     */
    private List<String> parseMainMetadata(byte[] xml) throws Exception {
        List<String> list = new ArrayList<>();
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml));
        NodeList nodes = doc.getElementsByTagName("version");
        for (int i = 0; i < nodes.getLength(); i++)
            list.add(nodes.item(i).getTextContent());
        return list;
    }
    /**
     * Extrahiert Snapshot-Builds aus der HTML-Verzeichnisansicht des Repositories.
     *
     * @param selectedBaseVERSION gewaehltes Snapshot-Präfix, zum Beispiel
     *        {@code v0.5.2-SNAPSHOT}
     * @param html HTML-Inhalt der Repository-Ansicht
     * @return gefundene Builds, absteigend nach Buildkennung sortiert
     * @throws Exception wenn keine passenden Builds gefunden werden
     */
    private List<VERSION> parseAllSnapshotBuildsFromHTML(String selectedBaseVERSION, String html) throws Exception {
        List<VERSION> builds = new ArrayList<>();

        // Wir suchen nach Links, die mit dem VERSIONsnamen beginnen und auf / enden
        // Beispiel: v0.5.0-alpha.dev-20260403.173504-4/
        String prefix = selectedBaseVERSION.replace("-SNAPSHOT", "");

        // Regex: sucht nach href="PRÄFIX-DATUM.ZEIT-NUMMER/"
        Pattern pattern = Pattern.compile("href=\"(" + Pattern.quote(prefix) + "-\\d{8}\\.\\d{6}-\\d+)/\"");
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            String buildValue = matcher.group(1); // Das ist z.B. v0.5.0-alpha.dev-20260403.173504-4

            // Timestamp für die Anzeige extrahieren (der Teil vor dem letzten Bindestrich)
            // Wir nehmen die 15 Zeichen vor dem letzten Bindestrich (20260403.173504)
            String rawTimestamp = buildValue.substring(buildValue.lastIndexOf("-") - 15, buildValue.lastIndexOf("-"));
            String cleanTimestamp = rawTimestamp.replace(".", "");

            builds.add(new VERSION(buildValue, cleanTimestamp));
        }

        if (builds.isEmpty()) {
            throw new Exception("Keine Builds in der HTML-Übersicht gefunden.");
        }

        // Sortieren: Neueste VERSION (höchste Nummer/Zeitstempel) nach oben
        builds.sort((v1, v2) -> v2.value().compareTo(v1.value()));

        return builds;
    }

    /**
     * Beschreibt einen konkreten Artefakt-Build samt technischer Kennung und
     * Zeitstempel.
     *
     * @param value vollstaendige Buildkennung
     * @param timestamp Zeitstempel im Format {@code yyyyMMddHHmmss} ohne Trennzeichen
     */
    public record VERSION(String value, String timestamp) {
        /**
         * Formatiert die Build-Information benutzerfreundlich fuer die Auswahl im
         * Launcher.
         *
         * @return formatierte Darstellung des Builds
         */
        @Override
        public String toString() {
            if (timestamp == null)
                return value;

            // Die Build-Nummer steht nach dem letzten "-" im "value" (z.B. ...-4)
            String buildNumber = value.substring(value.lastIndexOf("-") + 1);

            return String.format("%s (Build: #%s | %s.%s.%s %s:%s)",
                    value, // 1. Ganzer Name
                    buildNumber, // 2. Die Nummer nach dem letzten Bindestrich
                    timestamp.substring(6, 8), // 3. Tag
                    timestamp.substring(4, 6), // 4. Monat
                    timestamp.substring(0, 4), // 5. Jahr
                    timestamp.substring(8, 10), // 6. Stunde
                    timestamp.substring(10, 12) // 7. Minute
            );
        }
    }
}
