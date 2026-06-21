package de.qstudios.underground.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import de.qstudios.underground.Main;
import org.slf4j.*;
import javazoom.jl.player.Player;

/**
 * Die Klasse {@code Radio} verwaltet die Wiedergabe von Musiktiteln,
 * den Download fehlender Dateien und die Deserialisierung der Playlist aus einer JSON-Datei.
 * 
 * <p>Die Wiedergabe erfolgt asynchron in einem eigenen Thread, um den Hauptfluss
 * der Anwendung nicht zu blockieren.</p>
 * 
 * @author QStudios
 * @version 1.0
 */
public class Radio {
    private static final Logger LOG = LoggerFactory.getLogger(Radio.class);
    
    /** Pfad zur zentralen Konfigurationsdatei des Radios. */
    private static final Path RADIO_JSON = Path.of(
            System.getProperty("user.home"),
            "FnF-Apps",
            "US",
            "stable",
            "resources",
            "radio.json");

    /** Liste der geladenen Musiktitel. */
    private static final List<RadioSong> songs = new ArrayList<>();
    
    /** Index des aktuell gespielten oder nächsten Titels. */
    private static int currentIndex = 0;

    /**
     * Initialisiert das Radio, lädt die Songliste und startet die Wiedergabe
     * in einem neuen Thread.
     */
    public static void startRadio() {
        testRadioJson();
        loadSongs();
        System.out.println(">>> startRadio() wurde aufgerufen");

        if (songs.isEmpty()) {
            System.out.println("Keine Songs gefunden.");
            return;
        }

        new Thread(() -> {
            try {
                RadioSong song = playNext();

                if (song != null) {
                    try (FileInputStream fis = new FileInputStream(song.getFile().getAbsoluteFile())) {
                        LOG.info(">>> startRadio() versucht, Song zu spielen");
                        LOG.info("Spiele Datei: " + song.getFile().getAbsolutePath());
                        Player player = new Player(fis);
                        LOG.info("Player erstellt");
                        player.play(); // blockiert bis Song fertig ist
                        LOG.info("Player fertiggespielt");
                    }
                }
            } catch (Exception e) {
                LOG.error("Fehler bei der Radiowiedergabe", e);
            }
        }).start();
    }

    /**
     * Lädt die Songliste aus der lokalen {@code radio.json}.
     * Sollten Dateien fehlen, wird der Benutzer gefragt, ob diese heruntergeladen werden sollen.
     */
    private static void loadSongs() {
        try {
            String json = Files.readString(RADIO_JSON);
            List<RadioSong> loaded = new Gson().fromJson(
                    json,
                    new TypeToken<List<RadioSong>>() {
                    }.getType());

            songs.clear();
            boolean downloadErlaubt = false;
            
            if (loaded != null) {
                for (RadioSong rs : loaded) {
                    if (!rs.getFile().getAbsoluteFile().exists()) {
                        if (downloadErlaubt) {
                            downloadMusic(rs.file);
                        } else {
                            String akzeptiert = Main.readstring("Es wurden einige Radiodateien lokal nicht gefunden, downloaden? (Y/N)");
                            if (akzeptiert.equalsIgnoreCase("N")) {
                                return;
                            } else {
                                downloadErlaubt = true;
                                downloadMusic(rs.file);
                            }
                        }
                    }
                }
                songs.addAll(loaded);
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Laden von radio.json:");
            e.printStackTrace();
        }
    }

    /**
     * Wählt den nächsten Song in der Liste aus und inkrementiert den {@link #currentIndex}.
     * Bei Erreichen des Endes wird die Liste von vorne begonnen.
     * 
     * @return Der nächste {@link RadioSong} oder {@code null}, falls die Datei nicht existiert.
     */
    private static RadioSong playNext() {
        if (songs.isEmpty()) return null;
        
        currentIndex++;
        if (currentIndex >= songs.size()) {
            currentIndex = 0;
        }

        RadioSong song = songs.get(currentIndex);
        if (!song.exists()) {
            System.out.println("Datei nicht gefunden: " + song.getFile().getAbsolutePath());
            return null;
        }
        return song;
    }

    /**
     * Startpunkt für den manuellen Test des Radios.
     * @param args Befehlszeilenargumente.
     */
    public static void main(String[] args) {
        startRadio();
    }

    /**
     * Repräsentiert ein einzelnes Musikstück mit Metadaten.
     * Diese Klasse wird primär für die JSON-Deserialisierung verwendet.
     */
    public class RadioSong {

        public String name;
        public String artist;
        public String album;
        public String genre;
        public String year;
        public String file;
        public long length;

        /**
         * Standard-Konstruktor für die GSON-Deserialisierung.
         */
        public RadioSong() {
        }

        /**
         * Erstellt einen neuen Song und versucht, die Länge der MP3-Datei zu ermitteln.
         * 
         * @param name Titel des Songs.
         * @param artist Name des Künstlers.
         * @param album Name des Albums.
         * @param genre Genre des Songs.
         * @param year Erscheinungsjahr.
         * @param file Dateiname der MP3 (relativ zum Musikordner).
         */
        public RadioSong(String name, String artist, String album,
                String genre, String year, String file) {
            this.name = name;
            this.artist = artist;
            this.album = album;
            this.genre = genre;
            this.year = year;
            this.file = file;
            try {
                Mp3File mp3file = new Mp3File(file);
                length = mp3file.getLength();
            } catch (InvalidDataException | UnsupportedTagException | IOException e) {
                length = 0;
            }
        }

        /**
         * Ermittelt das {@link File}-Objekt basierend auf dem globalen Musikpfad der Anwendung.
         * 
         * @return Das Dateiobjekt der MP3.
         */
        public File getFile() {
            return new File(
                    System.getProperty("user.home")
                            + File.separator + "FnF-Apps"
                            + File.separator + "US"
                            + File.separator + "stable"
                            + File.separator + "resources"
                            + File.separator + "music"
                            + File.separator + file);
        }

        /**
         * Prüft, ob die physische Datei auf der Festplatte existiert.
         * 
         * @return {@code true}, wenn die Datei vorhanden ist.
         */
        public boolean exists() {
            return getFile().exists();
        }

        /**
         * Erstellt eine formatierte Zeichenkette für die Konsolenausgabe.
         * Format: "Künstler – Titel (Album, Jahr)"
         */
        @Override
        public String toString() {
            return artist + " – " + name + " (" + album + ", " + year + ")";
        }
    }

    /**
     * Lädt eine Datei vom zentralen Server herunter und speichert sie im lokalen Ressourcenpfad.
     * Unterstützt automatische Redirects und binäre Dateiformate.
     * 
     * @param music Der Name der herunterzuladenden Datei (z.B. "song.mp3" oder "radio.json").
     */
    public static void downloadMusic(String music) {
        try {
            String url = "https://us-data.deepslatesmp.de/" + music;
            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            System.out.println("Downloading " + music + " from " + url);

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200) {
                System.out.println("Fehler: HTTP " + response.statusCode());
                return;
            }

            Path path;
            if (music.equalsIgnoreCase("radio.json")) {
                path = Path.of(System.getProperty("user.home"), "FnF-Apps", "US", "stable", "resources", music);
            } else if (music.equalsIgnoreCase("radio-baldi.json")) {
                path = Path.of(System.getProperty("user.home"), "FnF-Apps", "US", "stable", "resources", "radio.json");
            } else {
                path = Path.of(System.getProperty("user.home"), "FnF-Apps", "US", "stable", "resources", "music", music);
            }
            
            Files.createDirectories(path.getParent());
            Files.write(path, response.body(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

            System.out.println("Download fertig: " + path);

        } catch (IOException | InterruptedException e) {
            LOG.error("Download-Fehler", e);
        }
    }

    /**
     * Überprüft, ob die Konfigurationsdatei {@code radio.json} existiert, 
     * und lädt sie bei Bedarf herunter.
     */
    public static void testRadioJson() {
        File radioJson = RADIO_JSON.toFile();
        if (!radioJson.exists()) {
            if (Main.baldiMode) {
                // downloadMusic("radio-baldi.json"); // Falls implementiert
            } else {
                downloadMusic("radio.json");
            }
        }
    }
}