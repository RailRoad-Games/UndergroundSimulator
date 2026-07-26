package org.rrgames.underground;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import java.util.UUID;
import org.slf4j.*;
import org.slf4j.event.Level;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;

import org.rrgames.underground.bm.BenchmarkToBGPS;
import org.rrgames.underground.core.Firma;
import org.rrgames.underground.dev.Bahnhof;
import org.rrgames.underground.dev.BaseShopActionHandler;
import org.rrgames.underground.dev.Credits;
import org.rrgames.underground.dev.EasyBrigadier;
import org.rrgames.underground.dev.FehlerReporter;
import org.rrgames.underground.dev.Fehlercodes;
import org.rrgames.underground.dev.GenericShopActionHandler;
import org.rrgames.underground.dev.ShopItem;
import org.rrgames.underground.dev.ShopItemRegistry;
import org.rrgames.underground.dev.ShopV3;
import org.rrgames.underground.dev.SimulatorAPI;
import org.rrgames.underground.dev.UpdateChecker;
import org.rrgames.underground.dev.UpdateChecker.ChangeEntry;
import org.rrgames.underground.misc.CrashReportLogger;
import org.rrgames.underground.misc.FakeSystemBoot;
import de.qstudios.extmgr.ExtensionJson;
import de.qstudios.extmgr.ExtManager;
import de.qstudios.extmgr.ModLauncher;
import org.rrgames.underground.sg.SaveGame;
import org.rrgames.underground.sg.SaveGameAssistant;
import org.rrgames.underground.sg.ZonedDateTimeAdapter;
import org.rrgames.underground.usr.User;
import org.rrgames.underground.usr.Userverwaltung;
import org.rrgames.underground.util.Downloader;
import org.rrgames.underground.util.Eula;
import org.rrgames.underground.util.Event;
import org.rrgames.underground.util.EventManager;
import org.rrgames.underground.util.Language;
import org.rrgames.underground.util.Launcher;
import org.rrgames.underground.util.PlayerMP3;
import org.rrgames.underground.util.PlayerWAV;
import org.rrgames.underground.util.Statistics;
import org.rrgames.underground.util.Strecke;
import org.rrgames.underground.util.Zug;
import org.rrgames.underground.util.Language.Lang;

/**
 * Hauptklasse des Underground-Simulators.
 * 
 * Diese Klasse steuert den gesamten Spielablauf, einschließlich:
 * <ul>
 * <li>Starten des Spiels und Initialisierung</li>
 * <li>Benutzeranmeldung und -erstellung</li>
 * <li>Simulationsrunden</li>
 * <li>Speichern und Laden von Spielständen</li>
 * </ul>
 * 
 * VERSION: {@value #VERSION}
 * 
 * @author FeGmbH
 */
public class Main {
    /** Autosavet das aktuelle Spiel usw. */
    public static SaveGame current;
    // public static JexeServer server = new JexeServer(2119);

    /** Für Logging z.B. Neuer User. */
    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    /** Standardwert für aktive Sitzplätze. */
    public static int aktiveSitzeDefault = 0;

    /** Standard-Fahrerlohn in Euro. */
    public static int fahrerlohnDefault = 25;

    /** Standard-Ausgabewert. */
    public static int kosten2Default = 0;

    /** Standard-Einnahmewert. */
    public static int einnahmen2Default = 1;
    /** Standardwert für aktive Popularität. */
    public static int aktivePopDefault = 0;

    /** Standard-Fahrpreis pro Passagier. */
    public static int fahrpreisDefault = 1;

    /** Standardanzahl an Runden. */
    public static int rundenDefault = 10;

    /** Verwaltung aller Benutzerkonten. */
    public static Userverwaltung user = new Userverwaltung();

    /** Der aktuell eingeloggte Benutzer. null: kein Benutzer eingeloggt */
    public static User userobj;

    /** SpielVERSIONsnummer. */
    public static final String VERSION = "v1.0-preview-SNAPSHOT";

    /** Aktuelle Firma des Spielers. */
    public static Firma firma;

    /** Aktuelle Zeit in Berlin. */
    public static ZonedDateTime jetzt = ZonedDateTime.now(ZoneId.systemDefault());

    /** Datumsformatierer. */
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.YYYY z");

    /** Formatierte Zeitangabe. */
    private static String formatiert = jetzt.format(formatter);

    /** Geladene Erweiterung (Extension). */
    public static ExtensionJson extensionJson;

    /** Eindeutige ID für die aktuelle Spielsession. */
    public static String uuidStart;

    /** Debug-Modus-Status. */
    public static Boolean debug = false;

    /** API-Instanz für Simulation. */
    public static SimulatorAPI api = new SimulatorAPI();

    /** Unterbrechungsausnahme (Platzhalter). */
    public static InterruptedException ie = new InterruptedException();

    /** Musikmodus (0 = aus, 1 = MP3, 2 = WAV). */
    private static int music = 0;
    // Savegame-relevante Felder -----------------------------
    /** Liste der aktuel der Firma im besitz befindlichen Züge. */
    public static List<Zug> aktiveZuege = new LinkedList<>();
    /** Objekt zur Eventgenerierung */
    public static EventManager eventManager = new EventManager();
    /**
     * Objekt zur Speicherung von Rundenereignissen. Wird bei neuer Runde
     * überschrieben.
     */
    public static Event k = eventManager.getEvent();
    /**
     * Objekt zum speichern des Aktienkurses.
     * 
     * @deprecated
     */
    @Deprecated
    public static String aktienkurs = "0";
    /**
     * Objekt zum speichern, ob der Aktienkurs aktiv ist oder nicht.
     * <i>mir ist bewusst, dass man auch einfach einen Boolean benutzen kann. NA
     * UND?</i>
     * 
     * @deprecated
     */
    @Deprecated
    public static int aktienkursja = 1;
    /** Aktueller Rundenwert. */
    public static int i = 0;
    /**
     * Aktuell nutzbare und benutzte Sitze.
     */
    public static int aktiveSitze = 0;
    /**
     * Wert für den Fahrerlohn. Standard:
     * 25
     */
    public static int fahrerlohn = 25;
    /** Wert zur internen Kostenerrechnung. */
    public static int kosten2 = 0;
    /** Wert zur internen Einnahmenerrechnung. */
    public static int einnahmen2 = 0;
    /**
     * Aktuell fahrende Leute auf allen Strecken addiert. <i>firma.getStrecke() gibt
     * eine List<Strecke> zurück. dort .getPop() ergibt diesen Wert.</i>
     */
    public static int aktivePop = 0;
    /** Thread zum Abspielen von Musik. {@link Main#music Siehe hier} */
    private static Thread soundThread;
    /** Gibt an, ob das Radio aktiviert ist. */
    public static boolean radio = false;
    public static boolean baldiMode = false;

    /**
     * Einstiegspunkt des Underground-Simulators.
     * 
     * @param args Startparameter, z. B. "debug" oder "sound1"
     * @throws Exception falls waehrend Initialisierung, Laden oder Spielstart ein
     *                   Fehler auftritt
     */
    void main(String[] args) throws Exception {

        System.setProperty("org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY", "Warn");
        ShopItemRegistry.register(new ShopItem("fahrer", "Fahrer", 600.00, "addFahrer", "?", 0));
        ShopItemRegistry.register(new ShopItem("zug120", "Zug (120 Sitze)", 5000.00, "addZug120", "?", 0));
        ShopItemRegistry.register(new ShopItem("zug240", "Zug (240 Sitze)", 10000.00, "addZug240", "?", 0));
        ShopItemRegistry
                .register(new ShopItem("strecke", "Strecke (10 Haltestellen)", 200000.00, "addStrecke", "?", 0));
        ShopItemRegistry.registerHandler(new BaseShopActionHandler());
        ModLauncher.launchAllModsJSON("onEnable");
        ShopItemRegistry.registerHandler(new GenericShopActionHandler());
        CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();
        EasyBrigadier.init(dispatcher);
        File tmp = new File(
                System.getProperty("user.home") + File.separator + "FnF-Apps" + File.separator + "US" + File.separator
                        + "stable" + File.separator + "resources" + File.separator + "events.json");
        if (!tmp.exists()) {
            Files.write(tmp.toPath(),
                    Downloader.download(new URI("https://us-data.deepslatesmp.de/events.json").toURL()),
                    StandardOpenOption.CREATE);
        }
        EasyBrigadier.registerCommand(
                "version",
                ctx -> System.out.println(Main.VERSION),
                String.class);
        /*
         * new Thread(()-> {while (true) {
         * String input = scanner.nextLine();
         * 
         * if (input.startsWith("/")) {
         * try {
         * dispatcher.execute(input.substring(1), source);
         * } catch (CommandSyntaxException e) {
         * e.printStackTrace();
         * }
         * continue;
         * }
         * 
         * // normales Spiel-Handling
         * }}).start();
         */

        logger.atLevel(Level.WARN);
        File f = new File(System.getProperty("user.home") + File.separator + "FnF-Apps" + File.separator + "US"
                + File.separator + "stable");
        File f2 = new File(System.getProperty("user.home") + File.separator + ".config" + File.separator + "us"
                + File.separator + "bin");
        File f3 = Path.of(System.getProperty("user.home"), ".config", "us", "logs").toFile();
        System.setProperty("US_VERSION", VERSION);
        // EnvironmentSetup.main();
        // EnvironmentSetup.createDesktopFile();
        // EnvironmentSetup.createBashScript();
        if (!f.exists()) {
            f.mkdirs();
        }
        if (!f2.exists()) {
            f2.mkdirs();
        }
        if (!f3.exists()) {
            f3.mkdirs();
        }
        checkEula();
        for (String str : args) {
            if (str.equalsIgnoreCase("radio-off")) {
                radio = false;
            }
        }
        /*
         * Thread radioThread =
         * Thread.ofVirtual().start(() -> {
         * Radio.startRadio();
         * });
         */
        // checkUpdates(VERSION);

        /*
         * server.start();
         * new Timer().scheduleAtFixedRate(new TimerTask() {
         * 
         * @Override
         * public void run() {
         * server.send("PING:MyGame");
         * }
         * }, 0, 1000);
         */
        boolean disableAutoLangDetect = false;
        for (String s : args) {
            logger.info("Argument übergeben: " + s);
            if (s.equals("--debig")) {
                System.out.println("Debug fast aktiviert... fast 😏");
            }
            if (s.equals("--lang=ger")) {
                System.out.println("Sprache erkannt: Deutsch (aber falsch geschrieben 👀)");
            }
            if (s.equals("--enable-debug")) {
                debug = true;
            } else if (s.equals("--enableDebug")) {
                System.out.println("Java-Style erkannt… aber nope 😄");
            }
            if (s.equals("--analysis")) {
                FakeSystemBoot.startService("install-virus");
            }
            if (s.equals("--benchmark")) {
                BenchmarkToBGPS.main(null);
            }
            if (s.equals("--LauncherEmulation")) {
                System.out.println("Achtung! Dieses System wird via den Underground Simulator Launcher (v0.5) gestartet. Mögliche Probleme sind zu entschuldigen.");
            }
            System.out.println(s + " wurde als Argument übergeben.");
            if (s.contains("debug")) {
                debug = true;
                System.out.println("\u001B[31m Debug-Modus aktiviert!\u001B[0m");
            } else {
                debug = false;
            }
            if (s.contains("sound")) {
                if (s.contains("0")) {
                    music = 0;
                } else if (s.contains("1")) {
                    music = 1;
                } else if (s.contains("2")) {
                    music = 2;
                } else {
                    music = 0;
                }
            }
            if (s.equalsIgnoreCase("-l") || s.equalsIgnoreCase("--launcher")) {
                Launcher.main(args);
                System.exit(0);
            }
            if (s.equals("--enableForeground=true")) {
                System.out.println("        ##        ");
                System.out.println("       ###        ");
                System.out.println("      ####        ");
                System.out.println("     ## ##        ");
                System.out.println("    ##  ##        ");
                System.out.println("   ##   ##        ");
                System.out.println("  ###########     ");
                System.out.println("         ##       ");
                System.out.println("         ##       ");
                System.out.println("         ##       ");
                System.out.println("         ##       ");
                System.exit(0);
            }
            if (s.equals("--disableAutoLangDetect") || s.equals("--disableAutoLangDetection")) {
                disableAutoLangDetect = true;
                // Do nothing, this is just to prevent auto language detection
            }
            if (s.startsWith("--lang=")) {
                IO.println("Sprachoption erkannt: " + s);
                String langCode = s.substring("--lang=".length()).toLowerCase();
                switch (langCode) {
                    case "de" -> Language.loadLang(Lang.DE);
                    case "fr" -> Language.loadLang(Lang.FR);
                    case "en" -> Language.loadLang(Lang.EN);
                    default -> {
                        System.out.println("Unsupported language code: " + langCode);
                        System.exit(1);
                    }
                }
                IO.println("Config -> debug=" + debug + ", lang=" + langCode + ", autoLangDetect="
                        + !disableAutoLangDetect);
            }
        }
        if (music == 1) {

            soundThread = PlayerMP3.newSoundThread("sound.mp3");
            if (soundThread != null) {
                soundThread.start();
            }
        } else if (music == 2) {
            soundThread = PlayerWAV.newSoundThread("sound.wav");
            if (soundThread != null) {
                soundThread.start();
            }
        }
        uuidStart = UUID.randomUUID().toString();
        // try{
        int aktienkursja = 1;
        try {
            // ModManager.readMods();
        } catch (Exception e1) {
            FehlerReporter.report(UUID.fromString(uuidStart), e1.toString(), e1.getClass().getSimpleName(),
                    getStackTraceAsString(e1));
            e1.printStackTrace();
            CrashReportLogger.logException(e1, Thread.currentThread(), false);
        }
        // System.out.println("Als XML geladene Mods:");
        // for (Mod m : ModManager.mods) {
        // System.out.println("Mod geladen: " + m.name + " erstellt von " + m.author + "
        // (" + m.VERSION + ")");
        // }

        if (!disableAutoLangDetect) {
            String lang = Locale.getDefault().getLanguage();

            switch (lang) {
                case "de" -> Language.loadLang(Lang.DE);
                case "fr" -> Language.loadLang(Lang.FR);
                default -> Language.loadLang(Lang.EN);
            }
        }
        System.out.println(Language.get("loaded_extensions_header"));

        List<ExtensionJson> extensions = ExtManager.get(ExtManager.getModsDir());

        if (extensions != null) {
            for (ExtensionJson extensionJson : extensions) {
                if (extensionJson != null) {
                    System.out.println(Language.get("extension_loaded") + extensionJson.getName()
                            + Language.get("extension_by")
                            + extensionJson.getAuthor() + Language.get("extension_version_prefix")
                            + extensionJson.getVersion() + Language.get("extension_version_suffix"));
                } else {
                    System.out.println(Language.get("extension_load_error"));
                }
            }
        }
        System.out.println(Language.get("welcome1") + VERSION + Language.get("welcome2")
                + java.time.Year.now()
                + Language.get("welcome3"));
        Scanner scan = new Scanner(System.in);

        // String antwort = scan.nextLine().trim();
        /*
         * if (antwort.equalsIgnoreCase("J") || antwort.equalsIgnoreCase("Ja") ||
         * antwort.equalsIgnoreCase("Y")
         * || antwort.equalsIgnoreCase("Yes") || antwort.equalsIgnoreCase("")) {
         * if (radio == false) {
         * radio = true;
         * Radio.startRadio();
         * }
         * System.out.println("Das Radio wurde aktiviert! Viel Spaß beim Spielen!");
         * } else {
         * System.out.println("Das Radio wurde deaktiviert. Viel Spaß beim Spielen!");
         * }
         */

        user.liesAccounts();
        ModLauncher.launchAllModsJSON("onLaunch");
        // Ausgabe
        if (debug) {
            System.out.println(Language.get("debug_time") + formatiert);
            System.out.println(Language.get("debug_game_id") + uuidStart);
            Fehlercodes.listAll();
        }
        System.out.println(
                Language.get("create_account_prompt"));
        scan.reset();
        System.out.println(IO.class.getSimpleName());
        String wahl = scan.nextLine().trim();
        switch (wahl) {
            case "l":
            case "L":
                System.out.println("Choose between: DE, FR, EN");
                String langChoice = scan.nextLine().trim().toLowerCase();
                switch (langChoice) {
                    case "de":
                        Language.loadLang(Lang.DE);
                        break;
                    case "fr":
                        Language.loadLang(Lang.FR);
                        break;
                    default:
                        Language.loadLang(Lang.EN);
                        break;
                }
                soundThread.interrupt();
                main(new String[] { "--disableAutoLangDetect" });
                System.exit(0);
                break;
            case "a":
            case "A":
                System.out.println(
                        Language.get("account_choice_create"));
                System.out.println();
                wahl = scan.nextLine();

                String username = wahl;
                String userpswrt = readPassword(Language.get("password_prompt"));
                String realname = "Neuer Chef";
                realname = readstring(Language.get("realname_prompt"));
                userobj = user.neuerUser(username, userpswrt, realname);
                Statistics stats = new Statistics(ZonedDateTime.now(), 0, 0, 0);
                if (new File(System.getProperty("user.home") + File.separator + ".config" + File.separator + "us"
                        + File.separator + userobj.getUsername() + ".json").exists()) {
                    System.out.println(
                            Language.get("user_exists_warning"));
                    logger.error(
                            Language.get("user_exists_error") + username + " | "
                                    + Language.get("user_exists_detail"));
                    return;
                } else {
                    try {
                        Path userDir = Path.of(
                                System.getProperty("user.home"),
                                ".config",
                                "us");

                        Files.createDirectories(userDir);

                        Path userFile = userDir.resolve(userobj.getUsername() + ".json");

                        if (Files.exists(userFile)) {
                            System.out.println("Account existiert bereits!");
                            return;
                        }

                        Files.write(
                                userFile,
                                "".getBytes(StandardCharsets.UTF_8));
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                        CrashReportLogger.logException(ignored, Thread.currentThread(), false);
                    }
                }
                stats.writePlayerStatistics(stats, userobj);
                user.schreibeAccounts();
                System.out.println(Language.get("account_created_done"));
                logger.info("Neuer User erstellt: " + username);
                /*
                 * try {
                 * api.startSession(username);
                 * } catch (Exception e) {
                 * e.printStackTrace();
                 * String code = Fehlercodes.getCodeFromException(e.getClass().getSimpleName());
                 * System.out.println("Es gab ein Problem beim Erstellen ihres Accounts. [" +
                 * code + "]");
                 * logger.error("Fehler beim Starten der Session für neuen User: " + username +
                 * " | "
                 * + getStackTraceAsString(e));
                 * FehlerReporter.report(
                 * UUID.fromString(uuidStart),
                 * e.toString(),
                 * e.getClass().getSimpleName(),
                 * getStackTraceAsString(e));
                 * }
                 */
                break;
            case "s":
            case "S":
                loadGame();
                break;
            case "exit":
                scan.close();
                System.exit(0);
            default:
                System.out.print(Language.get("enter_username"));
                System.out.println("\n");
                scan.reset();
                String nutzername = scan.nextLine();
                String passwort = readPassword(Language.get("enter_password"));

                if (!user.userExistiert(nutzername, passwort)) {
                    System.out.println(Language.get("user_not_found"));
                    main(args);
                    scan.close();
                }
                userobj = user.usersuchen(nutzername, passwort);
                break;
        }
        Statistics stats = new Statistics();
        stats = stats.getPlayerStatistics(userobj);
        Strecke.main(null);
        stats.playtimes++;
        stats.writePlayerStatistics(stats, userobj);
        /*
         * System.out.
         * println("\n\n\n\n Piep, Piep, Piep... Der Wecker klingelt! Zeit zum Aufstehen!\n"
         * );
         * Thread.sleep(1500);
         * System.out.
         * println("Nicht schon wieder aufstehen... Das mus doch nicht immer so sein!\n"
         * );
         * Thread.sleep(2500);
         * System.out.
         * println("Pling! \n \u001B[3m \t Eine neue Nachricht \u001B[0m \n zeigt dir das Display an."
         * );
         * Thread.sleep(2000);
         * System.out.println(
         * "\n Du nimmst dir noch ganz schläfrig dein Telefon und lässt dir genauere Informationen zu der Nachricht anzeigen. Absender:\n \u001B[3m \t BeRufsBank GmbH & Co. KG \u001B[0m \n"
         * );
         * Thread.sleep(2500);
         * System.out.println(
         * "\n Du springst sofort auf und schnappst dir dein Handy. Du öffnest die Nachricht und liest folgendes:\n \u001B[3m \t Lieber "
         * + userobj.getUsername()
         * +
         * ",\n \t Wir freuen uns, Ihnen mitteilen zu können, dass Ihr Antrag auf ein Darlehn von 1000 🚇©  genehmigt wurde. \n \t Sie erhalten einen Betrag von 50.000 €, \num Ihre Firma zu gründen und Ihre unternehmerischen Ziele zu verfolgen. \n \t Mit freundlichen Grüßen,\n \t Maximilian Rufs\n \t CEO BeRufsBank GmbH & Co. KG \u001B[0m \n"
         * );
         */
        System.out.println("\n\n\n\n\n\n\n\n");
        System.out.println(Language.get("start_prompt"));
        wahl = scan.nextLine();
        if (wahl.equalsIgnoreCase("won")) {
            i = 11;
        }
        firma = new Firma(wahl);
        logger.info("Neue Firma erstellt: " + wahl + " für User: " + userobj.getUsername());
        System.out.println(
                Language.get("company_created") + wahl + Language.get("company_created_suffix") + firma.toString() + "!");
        ModLauncher.launchAllModsJSON("onGameStart");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

            e.printStackTrace();
           CrashReportLogger.logException(e, Thread.currentThread(), false);
        }

        scan.reset();
        schleife(userobj, firma, scan, rundenDefault, aktienkursja);
        // } catch (InterruptedException e) {}

    }

    /**
     * Gibt an, ob sich das Programm im Debug-Modus befindet.
     * 
     * @return {@code true}, wenn Debug-Modus aktiv ist, sonst {@code false}.
     */
    public static Boolean getDebug() {
        return debug;
    }

    /**
     * Hauptschleife.
     * 
     * @param firma        Die Firma des Spielers, die in jeder Runde aktualisiert
     *                     wird.
     * @param scan         Scanner-Objekt für die Benutzereingabe.
     * @param dauer        Anzahl der Runden, die gespielt werden sollen.
     * @param aktienkursja Gibt an, ob der Aktienkurs aktiv ist (1) oder nicht (0).
     *                     (Deprecated!)
     * @throws InterruptedException falls die Schleife unterbrochen wird.
     */
    // Anfang der Schleife
    public static void schleife(User userobj, Firma firma, Scanner scan, int dauer, int aktienkursja)
            throws InterruptedException {
        eventManager = new EventManager();
        k = eventManager.getEvent();
        // 10 Runden spielen, oder bis die Firma bankrott ist
        while (firma.getGeld() > 0 && i < dauer) {
            Event k = eventManager.getEvent();
            System.out.println(k);
            int kosten2 = kosten2Default;
            int einnahmen2 = einnahmen2Default;

            // =========================
            // AKTIVE ZÜGE
            // =========================
            int arbeitstag = 60 * 8; // 8 Stunden Arbeitstag in Minuten
            int zeitZwischenHaltestellen = 5; // Zeit in Minuten zwischen den Haltestellen
            int haltedauer = 2; // Zeit in Minuten, die der Zug an jeder Haltestelle hält

            int fahrtzeit = 0;
            int fahrtenProZug = 0;

            for (Strecke str : firma.getStrecke()) {
                fahrtzeit = str.StreckenLängeZeit;
                for (int i = str.haltestellen.size(); i > 0; i--) {
                    fahrtzeit += zeitZwischenHaltestellen + haltedauer;
                }

                fahrtenProZug = arbeitstag / fahrtzeit;

                System.out.println(Language.get("route_info_1")
                        + str.haltestellen.getLast().getName()
                        + Language.get("route_info_2")
                        + str.haltestellen.getFirst().getName());

                System.out
                        .println(Language.get("fahrtzeit") + fahrtzeit + Language.get("fahrtzeit_unit"));
            }
            Thread.sleep(2000);
            int passengersAllDay = 0;

            for (Zug zug : firma.getZug()) {
                int durschnittsGeschwindigkeit = (int) (zug.getV_max() * (2.0 / 6.0));

                System.out.println(Language.get("avg_speed") + durschnittsGeschwindigkeit
                        + Language.get("speed_unit"));

                passengersAllDay += (int) (zug.getSitze() * (2.0 / 3.0) * fahrtenProZug);
            }
            if (passengersAllDay > firma.getCity().getPeopleWithTicket()) {
                passengersAllDay = (int) firma.getCity().getPeopleWithTicket();
            }
            Thread.sleep(2000);
            System.out.println(Language.get("passengers_day")
                    + passengersAllDay + Language.get("passengers_day_2"));
            einnahmen2 = passengersAllDay * fahrpreisDefault;
            System.out.println(
                    Language.get("fahrten_info_1")
                            + fahrtenProZug
                            + Language.get("fahrten_info_2")
                            + fahrtenProZug / 2
                            + Language.get("fahrten_info_3"));
            firma.setGeld(einnahmen2 - kosten2 + firma.getGeld());
            Thread.sleep(2000);
            List<Zug> aktiveZuegeListe = new ArrayList<>(firma.getZug());

            SaveGame sg = new SaveGame();

            // --- Pflichtfelder ---
            if (i == 0) {
                sg.name = "initial-save";
            } else {
                sg.name = "round-" + (i + 1);
            }
            sg.username = userobj.getUsername();
            sg.userpswrt = userobj.getPasswd();
            sg.userobj = userobj;
            sg.user = user;

            // --- SpielVERSION ---
            sg.version = VERSION;

            // --- Firma ---
            sg.firma = new Firma(firma.getName());

            // --- Haltestellen ---

            // --- Datum ---
            sg.jetzt = jetzt; // falls du ein bestimmtes Startdatum willst
            // sg.jetzt = ZonedDateTime.now(); // sonst automatisch

            // --- API / Debug-Dinge ---
            sg.debug = debug;
            sg.api = api;

            // --- Aktienkurs ---
            sg.aktienkurs = "0";
            sg.aktienkursja = aktienkursja;

            // --- Allgemeine Werte ---
            sg.i = i;
            sg.aktiveSitze = aktiveSitzeDefault;
            sg.fahrerlohn = fahrerlohnDefault;
            sg.kosten2 = kosten2;
            sg.einnahmen2 = einnahmen2;
            sg.aktivePop = aktivePopDefault;

            // --- Züge ---
            sg.aktiveZuege = new LinkedList<>(aktiveZuegeListe);

            // --- Monopoly ---
            sg.eventManager = eventManager;
            sg.k = eventManager.getEvent();

            // ⚠️ WICHTIG: finalisieren, damit formatter, formatiert, API usw. gesetzt
            // werden
            sg.finalizeLoad();

            // 💾 speichern mit der neuen Methode
            if (!VERSION.contains("SNAPSHOT")) {
                SaveGame.save(sg);
            } else {
                System.out.println(
                        Language.get("napshot_warning"));
            }
            System.out.println(
                    Language.get("day_end") + firma.toString());

            System.out.println(
                    Language.get("menu"));
            String wahl2 = scan.nextLine().trim();
            if (wahl2.equalsIgnoreCase("!_my-name-is-trump!_")) {
                firma.setGeld(5100000);

            }
            if (wahl2.equalsIgnoreCase("won")) {
                i = 11;
            }
            // Shop v2.1
            if (wahl2.equalsIgnoreCase("s")) {
                ShopV3.shop(wahl2, scan);
            }
            ModLauncher.launchAllModsJSON("activity");
            // Nacht
            System.out.println();
            System.out.println();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

                e.printStackTrace();
                CrashReportLogger.logException(e, Thread.currentThread(), false);
            }
            System.out.print(Language.get("night") + " ");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //
                CrashReportLogger.logException(e, Thread.currentThread(), false);
            }
            System.out.print(".");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

                e.printStackTrace();
                CrashReportLogger.logException(e, Thread.currentThread(), false);
            }
            System.out.print(".");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

                e.printStackTrace();
                CrashReportLogger.logException(e, Thread.currentThread(), false);
            }
            System.out.println(".");
            System.out.println();
            System.out.println();
            i++;
            aktiveSitze = 0;
        }
        fahrpreisDefault = fahrpreisDefault + 1;
        user.schreibeAccounts();
        if (firma.getGeld() < 1) {
            System.err
                    .println(Language.get("game_lost_1") + firma.getName() + Language.get("game_lost_2"));
            System.out.println(Language.get("thanks") + VERSION + "! ");
            Credits.printCredits();
            Statistics stats = new Statistics();
            stats = stats.getPlayerStatistics(userobj);
            stats.lost++;
            stats.writePlayerStatistics(stats, userobj);
        }
        if (firma.getGeld() > 0) {
            if (debug == true) {
                System.out.println("DEBUG: Geld nach angegebenen Runden > 0, spiel gewonnen.");
            }
            System.out.println(Language.get("game_won") + firma.toString());
            System.out.println(Language.get("thanks") + VERSION + "! ");
            Credits.printCredits();
            Statistics stats = new Statistics();
            stats = stats.getPlayerStatistics(userobj);
            stats.won++;
            stats.writePlayerStatistics(stats, userobj);
        }
    }

    /**
     * Hilfswariable zum Umwandeln des Stacktraces zum String.
     * 
     * @param e Geworfener Fehler
     * @return String Stacktrace
     */
    public static String getStackTraceAsString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement elem : e.getStackTrace()) {
            sb.append(elem.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Methode zum sicheren Passworteingeben
     * 
     * @param prompt Was man als Aufforderung nimmt. z.B. "<i>Bitte geben sie ihr
     *               Passwort ein!</i>"
     * @return String Passwort
     */
    public static String readPassword(String prompt) {
        try {
            Terminal terminal = TerminalBuilder.builder()
                    .system(true) // echtes System-Terminal
                    .build();

            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .build();

            return reader.readLine(prompt, '*'); // '*' maskiert die Eingabe

        } catch (Exception e) {
            e.printStackTrace();
            CrashReportLogger.logException(e, Thread.currentThread(), false);
            return null; // Falls Terminal nicht erstellt werden kann
        }
    }

    /**
     * @param prompt
     * @return String
     */
    public static String readstring(String prompt) {
        try {
            Terminal terminal = TerminalBuilder.builder()
                    .system(true) // echtes System-Terminal
                    .build();

            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .build();

            return reader.readLine(prompt); // '*' maskiert die Eingabe

        } catch (Exception e) {
            e.printStackTrace();
            CrashReportLogger.logException(e, Thread.currentThread(), false);
            return null; // Falls Terminal nicht erstellt werden kann
        }
    }

    /**
     * Lädt Haltestellen aus entweder einem InputStream oder einem Dateipfad.
     * Beispiele:
     * <ul>
     * <li>ladeHaltestellen(System.in, null)</li>
     * <li>ladeHaltestellen(null, "src/main/resources/lib/haltestellen.txt")</li>
     * <li>ladeHaltestellen(null, "https://.../haltestellen.txt")</li>
     * <li>ladeHaltestellen(null, "haltestellen.txt") // als
     * Classpath-Ressource</li>
     * </ul>
     * 
     * @return List<String> Liste mit geladenen Haltestellen.
     **/

    public static List<String> ladeHaltestellen(String dateipfad) throws IOException {
        Path p = Paths.get(dateipfad);

        if (!Files.exists(p)) {
            throw new FileNotFoundException("Datei existiert nicht: " + p.toAbsolutePath());
        }

        return Files.readAllLines(p, StandardCharsets.UTF_8)
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    /** Methode zum Aufrufen von Spielspeicherungen. */
    public static void loadGame() {
        System.out.println("Hallo! Willkommen zum Spielspeicherungs-Assistent!");
        SaveGame sg = SaveGameAssistant.listAndSelectSavegames();
        try {
            schleife(sg.userobj, sg.firma, new Scanner(System.in), sg.i, sg.aktienkursja);
        } catch (InterruptedException e) {
            e.printStackTrace();
            CrashReportLogger.logException(e, Thread.currentThread(), true);
        }
    }

    /**
     * @hidden
     * @return UUID Spiel-UUID
     */
    public static UUID getUUID() {
        return UUID.fromString(uuidStart);
    }

    /**
     * Methode zum anwenden von Spielständen. Überschreibt alle lokalen Variablen.
     * 
     * @param sg das passende SaveGame.
     */
    public static void applySG(SaveGame sg) {
        try {
            current = sg;

            firma = sg.firma;
            user = sg.user;
            userobj = sg.userobj;

            aktiveZuege = sg.aktiveZuege;

            eventManager = sg.eventManager;
            k = sg.k;

            aktienkurs = sg.aktienkurs;
            aktienkursja = sg.aktienkursja;

            i = sg.i;
            aktiveSitze = sg.aktiveSitze;
            fahrerlohn = sg.fahrerlohn;

            kosten2 = sg.kosten2;
            einnahmen2 = sg.einnahmen2;

            aktivePop = sg.aktivePop;

            jetzt = sg.jetzt;
            formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss z");

            formatiert = sg.formatiert;

            uuidStart = sg.uuidStart;

            api = new SimulatorAPI(); // transient → immer neu

            System.out.println("✔ Spielstand erfolgreich angewendet: " + sg.name);

        } catch (Exception e) {
            e.printStackTrace();
            CrashReportLogger.logException(e, Thread.currentThread(), false);
            System.out.println("❌ Fehler beim Anwenden des Savegames!");
        }
    }

    /** @hidden */
    public Main() {
    }

    /**
     * @param eulaFile
     */
    public static void checkEula(File eulaFile) {
        try {

            if (!eulaFile.exists()) {
                eulaFile.getParentFile().mkdirs();
                Files.writeString(
                        eulaFile.toPath(),
                        "{\"accepted\":false}",
                        StandardCharsets.UTF_8);
            }

            String json = Files.readString(eulaFile.toPath());
            Eula eula = new GsonBuilder().setPrettyPrinting()
                    .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter()).create()
                    .fromJson(json, Eula.class);

            if (eula != null && eula.accepted) {
                // EULA akzeptiert → Spiel starten
                return;
            }

            System.out.println(
                    "Hey! Willkommen zum Underground-Simulator!\n" +
                            "Anscheinend hast du die UndergroundSimulator EULA noch nicht akzeptiert.\n Dies kann auch daran liegen, dass du den UndergroundSimulator vor VERSION 0.4.1-alpha.dev genutzt hast."
                            +
                            "Bitte lese sie unter:\n" +
                            "https://mr-quatsch.de/eula\n\n" +
                            "Lizenz:\nhttps://www.mr-quatsch.de/LICENSE\n");

            String accepted = readstring(
                    "Tippe 'accept' zum Akzeptieren oder 'decline' zum Ablehnen: ");

            if (accepted.equalsIgnoreCase("accept")) {
                eula.accepted = true;
                Files.writeString(
                        eulaFile.toPath(),
                        new GsonBuilder().setPrettyPrinting()
                                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter()).create()
                                .toJson(eula),
                        StandardCharsets.UTF_8);
                System.out.println("Danke! EULA akzeptiert.");
            } else {
                System.out.println("EULA nicht akzeptiert. Spiel wird beendet.");
                System.exit(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            CrashReportLogger.logException(e, Thread.currentThread(), false);
            System.exit(1);
        }
    }

    public static void checkEula() {
        try {
            File eulaFile = new File(
                    System.getProperty("user.home")
                            + File.separator + ".config"
                            + File.separator + "us"
                            + File.separator + "eula.json");

            if (!eulaFile.exists()) {
                eulaFile.getParentFile().mkdirs();
                Files.writeString(
                        eulaFile.toPath(),
                        "{\"accepted\":false}",
                        StandardCharsets.UTF_8);
            }

            String json = Files.readString(eulaFile.toPath());
            Eula eula = new GsonBuilder().setPrettyPrinting()
                    .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter()).create()
                    .fromJson(json, Eula.class);

            if (eula != null && eula.accepted) {
                // EULA akzeptiert → Spiel starten
                return;
            }

            System.out.println(
                    "Hey! Willkommen zum Underground-Simulator!\n" +
                            "Anscheinend hast du die UndergroundSimulator EULA noch nicht akzeptiert.\n Dies kann auch daran liegen, dass du den UndergroundSimulator vor VERSION 0.4.1-alpha.dev genutzt hast."
                            +
                            "Bitte lese sie unter:\n" +
                            "https://mr-quatsch.de/eula\n\n" +
                            "Lizenz:\nhttps://www.mr-quatsch.de/LICENSE\n");

            String accepted = readPassword(
                    "Tippe 'accept' zum Akzeptieren oder 'decline' zum Ablehnen: ");

            if (accepted.equalsIgnoreCase("accept")) {
                eula.accepted = true;
                Files.writeString(
                        eulaFile.toPath(),
                        new GsonBuilder().setPrettyPrinting()
                                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter()).create()
                                .toJson(eula),
                        StandardCharsets.UTF_8);
                System.out.println("Danke! EULA akzeptiert.");
            } else {
                System.out.println("EULA nicht akzeptiert. Spiel wird beendet.");
                System.exit(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            CrashReportLogger.logException(e, Thread.currentThread(), false);
            System.exit(1);
        }
    }

    /**
     * @param VERSION
     */
    public static void checkUpdates(String VERSION) {
        try {
            List<ChangeEntry> ch = UpdateChecker.getChangeList();
            String latestVERSION = ch.getFirst().v;
            if (latestVERSION != null && !latestVERSION.equals(VERSION)) {
                System.out.println("Eine neue VERSION des Underground-Simulators ist verfügbar: " + latestVERSION);
                String r = readstring("Willst du die neueste VERSION herunterladen? (J/n)");
                if (r.equalsIgnoreCase("J") || r.equalsIgnoreCase("")) {
                    UpdateChecker.downloadUpdateNewest();
                } else {
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("Fehler beim Überprüfen auf Updates: " + e.getMessage());
        }
    }

    /**
     * @param firma
     * @return int
     */
    public static int calculate(Firma firma) {
        int arbeitstag = 60 * 8;
        int streckenÜbrig = firma.getStrecke().size();
        int zügePerStrecke = firma.getZug().size() / firma.getStrecke().size();
        int zügePerStreckeR = (int) Math.floor(zügePerStrecke);
        int streckePerZüge = firma.getZug().size() / firma.getStrecke().size();
        int streckePerZügeR = (int) Math.floor(streckePerZüge);
        System.out.println("Es fahren " + zügePerStreckeR + " Züge pro Strecke.");
        System.out.println("Es gibt " + streckePerZügeR + " Strecken pro Zug.");
        int zeitZwischenHaltestellen = 5;
        int haltedauer = 2;
        int i = 0;
        int i2 = 1;
        if (streckePerZüge - streckePerZügeR > 0.99) {
            System.out.println("Es gibt ungenutzte Züge!");

        }
        for (Strecke str : firma.getStrecke()) {
            int fahrtzeit = str.StreckenLängeZeit;
            for (Bahnhof bf : str.haltestellen) {
                fahrtzeit = fahrtzeit + zeitZwischenHaltestellen + haltedauer;
                bf.getAnzahlGleise();

            }
            System.out.println("Strecke von " + str.haltestellen.getLast().getName() + " nach "
                    + str.haltestellen.getFirst().getName() + " mit " + str.haltestellen.size() + " Haltestellen.");
            System.out.println("Die gesamte Fahrzeit beträgt: " + fahrtzeit + " Minuten.");

            i2 = arbeitstag / fahrtzeit;
            i = i2 * fahrtzeit;

        }
        int passengersAllDay = 0;
        for (Zug zug : firma.getZug()) {
            streckenÜbrig = firma.getStrecke().size();
            int durschnittsGeschwindigkeit = (int) Math.floor(zug.getV_max() * (2.0 / 6.0));
            System.out.println("DurschnittsGeschwindigkeit: " + durschnittsGeschwindigkeit + " km/h");
            for (Bahnhof bf : firma.getStrecke().get(streckenÜbrig - 1).haltestellen) {
                System.out.println("Zug mit " + zug.getSitze() + " Sitzen hält am Bahnhof: " + bf.getName());
                streckenÜbrig--;
            }
            passengersAllDay = (int) Math.floor(passengersAllDay + zug.getSitze() * (2.0 / 3.0) * i2);
        }
        System.out.println("An einem Arbeitstag von 8 Stunden können mit diesem Zug ca. " + passengersAllDay
                + " Passagiere befördert werden.");
        System.out.println(
                "In 8 Stunden passen " + i2 + " Fahrten auf diese Strecke. (also " + i2 / 2
                        + " hin und zurück) und damit fährt der Zug insgesamt " + i + " Minuten.");
        return passengersAllDay;
    }
}

// ACHTUNG: langf. Ziele! Linien-Namen, Linien-zuteilung, versch. züge,
// versch. strecken, Haltestellen, Börse(halbfertig), Stromverbrauch,
// Sauberkeit, gen. Einstellungen
// Zug-Modell-Editor, endbaujahr, rebrand der züge z.b. alte ist 1 neue
// ist 1neo und dann gibt es noch 2 usw. wenn es 2 schon gibt, dann halt 3 usw.
