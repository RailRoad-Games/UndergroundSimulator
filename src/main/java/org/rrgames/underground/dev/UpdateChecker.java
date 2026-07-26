package org.rrgames.underground.dev;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.rrgames.underground.Main;
/**Experimentelle (!) Klasse um herauszufinden, ob eine Klasse neuer ist als eine andere. Sie kann sie auch herunterladen.
 * @author FeGmbH
 * @since v0.4.2-alpha.dev
 */
public class UpdateChecker {
    // Datenmodell für die JSON-Struktur
    /**Datenmodell für die JSON-Struktur.
     * @hidden
     */
    public static class ChangeEntry {
        /**Zeitstempel um herauszufinden, z.B. wann das Update rauskam */
        public String timestamp;
        /**Wer das Update geschrieben hat */
        public String author;
        /**Kommentar des Uploaders, was neu ist */
        public String changes;
        /**Grund, um Update zu programmieren. */
        public String reason;
        /**Interne ID */
        public String id;
        /**Version des Updates. */
        public String v;
        /**kleines auto-generiertes toString(). */
        @Override
        public String toString() {
            return "ChangeEntry{" +
                    "timestamp='" + timestamp + '\'' +
                    ", author='" + author + '\'' +
                    ", changes='" + changes + '\'' +
                    ", reason='" + reason + '\'' +
                    ", id='" + id + '\'' +
                    ", v='" + v + '\'' +
                    '}';
        }
    }
    
    static class Version implements Comparable<Version> {
        static Version parse(String v) {
            v = v.startsWith("v") ? v.substring(1) : v;

            String[] mainAndPre = v.split("-", 2);
            String[] nums = mainAndPre[0].split("\\.");

            Version ver = new Version();
            ver.major = nums.length > 0 ? Integer.parseInt(nums[0]) : 0;
            ver.minor = nums.length > 1 ? Integer.parseInt(nums[1]) : 0;
            ver.patch = nums.length > 2 ? Integer.parseInt(nums[2]) : 0;

            ver.pre = mainAndPre.length > 1
                    ? PreRelease.parse(mainAndPre[1])
                    : PreRelease.RELEASE;

            return ver;
        }

        int major, minor, patch;

        PreRelease pre;

        @Override
        public int compareTo(Version o) {
            if (major != o.major)
                return Integer.compare(major, o.major);
            if (minor != o.minor)
                return Integer.compare(minor, o.minor);
            if (patch != o.patch)
                return Integer.compare(patch, o.patch);
            return pre.compareTo(o.pre);
        }
    }

    enum PreRelease {
        ALPHA(0), BETA(1), RC(2), RELEASE(3), DEV(-1);

        static PreRelease parse(String s) {
            s = s.toLowerCase();
            if (s.startsWith("alpha"))
                return ALPHA;
            if (s.startsWith("beta"))
                return BETA;
            if (s.startsWith("rc"))
                return RC;
            return RELEASE;
        }

        final int weight;

        PreRelease(int weight) {
            this.weight = weight;
        }
    }

    private static List<ChangeEntry> changes;
    private static final Logger LOG = LoggerFactory.getLogger(UpdateChecker.class);

    public static void checkForUpdates() {
        try {
            String url = "https://us-data.deepslatesmp.de/updates.json";

            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS) // sehr wichtig
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Fehler: HTTP " + response.statusCode());
                return;
            }

            String json = response.body();

            Gson gson = new Gson();
            changes = gson.fromJson(json, new TypeToken<List<ChangeEntry>>() {
            }.getType());
            for (ChangeEntry entry : changes) {
                System.out.println("Änderung ID: " + entry.id);
                System.out.println("Version: " + entry.v);
                System.out.println("Autor: " + entry.author);
                System.out.println("Datum: " + entry.timestamp);
                System.out.println("Grund: " + entry.reason);
                System.out.println("Beschreibung: " + entry.changes);
                System.out.println("-------------------------------------------------");
            }
        } catch (JsonSyntaxException | IOException | InterruptedException e) {
            LOG.error(Main.getStackTraceAsString(e));

        }

    }

    /**
     * Prüft, ob updateVersion neuer ist als currentVersion
     * 
     * @param current z.B. "0.4.1-alpha.dev"
     * @param update  z.B. "0.4.2-alpha.dev"
     * @return true, wenn updateVersion neuer ist, false sonst
     */

    // Versionvergleichsmethode
    public static boolean isUpdateNewer(String current, String update) {
        Version c = Version.parse(current);
        Version u = Version.parse(update);
        return u.compareTo(c) > 0;
    }

    /**
     * Zählt, wie viele der neuesten n Updates neuer sind als currentVersion
     * 
     * @param currentVersion z.B. "0.4.1-alpha.dev"
     * @param updates        Liste der ChangeEntry-Objekte, bereits nach neuster
     *                       zuerst sortiert
     * @param n              Anzahl der neuesten Updates, die geprüft werden sollen
     * @return Anzahl Updates, die neuer sind als currentVersion
     */
    public static int countNewerUpdates(String currentVersion, List<ChangeEntry> updates, int n) {
        int count = 0;
        for (int i = 0; i < updates.size() && i < n; i++) {
            ChangeEntry entry = updates.get(i);
            if (isUpdateNewer(currentVersion, entry.v)) {
                count++;
            }
        }
        return count;
    }

    /**
     * @param args
     */
    // Test
    public static void main(String[] args) {
        System.out.println(isUpdateNewer("0.4.1-alpha.dev", "0.4.2-alpha.dev")); // true
        System.out.println(isUpdateNewer("0.4.2-alpha.dev", "0.4.2-beta.dev")); // true
        System.out.println(isUpdateNewer("0.4.2", "0.4.2-alpha.dev")); // false
        System.out.println(isUpdateNewer("0.4.2", "0.4.2")); // false
    }

    /**
     * @return List<ChangeEntry>
     */
    public static List<ChangeEntry> getChangeList() {
        try {
            String url = "http://us-data.deepslatesmp.de/updates.json";

            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS) // sehr wichtig
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Fehler: HTTP " + response.statusCode());
                return null;
            }

            String json = response.body();

            Gson gson = new Gson();
            return changes = gson.fromJson(json, new TypeToken<List<ChangeEntry>>() {
            }.getType());
        } catch (Exception ignored) {
            return null;
        }
    }

    public static void downloadUpdateNewest() {
        // HTTP-Client erstellen
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS) // Weiterleitungen folgen
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://us-data.deepslatesmp.de/bin/underground-" + Main.VERSION + ".jar"))
                .header("User-Agent", "qstudios/" + Main.VERSION + " (Underground-Updater)")
                .header("Accept", "*/*")
                .GET()
                .build();

        // Binärdaten in Byte-Array speichern
        try {
            // Anfrage senden und Byte-Array als Antwort erhalten
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            // Prüfen, ob die Antwort erfolgreich war
            if (response.statusCode() == 200) {
                // Zielpfad für die Datei
                Path path = Paths.get(System.getProperty("user.home") + File.separator + ".config" + File.separator
                        + Main.VERSION + ".jar");

                // Byte-Array in die Datei schreiben
                Files.write(path, response.body());

                System.out.println("Update erfolgreich heruntergeladen und gespeichert: " + path.toAbsolutePath());
            } else {
                System.err.println("Fehler: " + response.statusCode() + " beim Download der Datei. " + response.body());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
