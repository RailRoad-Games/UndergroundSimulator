package de.qstudios.underground.util;
import java.util.List;

import org.slf4j.Logger;

import de.qstudios.underground.Main;
import de.qstudios.underground.core.Firma;
import de.qstudios.extmgr.ExtensionJson;
import de.qstudios.underground.sg.SaveGame;
import de.qstudios.underground.usr.User;
import de.qstudios.underground.usr.Userverwaltung;

public class API {
        /** Autosavet das aktuelle Spiel usw. */
    public static SaveGame current = Main.current;
    // public static JexeServer server = new JexeServer(2119);

    /** Für Logging z.B. Neuer User. */
    public static final Logger logger = Main.logger;

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

    /** Der aktuell eingeloggte Benutzer. */
    public static User userobj;

    /** SpielVERSIONsnummer. */
    public static final String VERSION = Main.VERSION;

    /** Aktuelle Firma des Spielers. */
    public static Firma firma = Main.firma;

    /** Geladene Erweiterung (Extension). */
    public static ExtensionJson extensionJson  =  Main.extensionJson;

    /** Eindeutige ID für die aktuelle Spielsession. */
    public static String uuidStart = Main.uuidStart;

    /** Debug-Modus-Status. */
    public static Boolean debug = Main.debug;
    /** Musikmodus (0 = aus, 1 = MP3, 2 = WAV). */
    private static int music = 0;
    // Savegame-relevante Felder -----------------------------
    /** Liste der aktuel der Firma im besitz befindlichen Züge. */
    public static List<Zug> aktiveZuege = Main.aktiveZuege;
    /** Objekt zur Eventgenerierung */
    public static EventManager eventManager = Main.eventManager;
    /**
     * Objekt zur Speicherung von Rundenereignissen. Wird bei neuer Runde
     * überschrieben.
     */
    public static Event k =Main.k;
    /** Aktueller Rundenwert. */
    public static int i = Main.i;
    /**
     * Aktuell nutzbare und benutzte Sitze.
     */
    public static int aktiveSitze = Main.aktiveSitze;
    /**
     * Wert für den Fahrerlohn. Standard:
     * 25 USC
     */
    public static int fahrerlohn = Main.fahrerlohn;
    /** Ausgaben &amp; Kosten der aktuellen Runde; wird danach überschrieben. */
    public static int kosten = Main.kosten2;
    /** Einnahmen der aktuellen Runde. Wird danach überschrieben. */
    public static int einnahmen = Main.einnahmen2;
    /**
     * Aktuell fahrende Leute auf allen Strecken addiert. <i>firma.getStrecke() gibt
     * eine List<Strecke> zurück. dort .getPop() ergibt diesen Wert.</i>
     */
    public static int aktivePop = Main.aktivePop;
    /** Thread zum Abspielen von Musik. {@link Main#music Siehe hier} */
    /** Gibt an, ob das Radio aktiviert ist. */
    public static boolean radio = Main.radio;
    public static boolean baldiMode = Main.baldiMode;
    public static SaveGame getCurrentSaveGame() {
        return current;
    }
    public static Logger getLogger() {
        return logger;
    }
    public static int getAktiveSitzeDefault() {
        return aktiveSitzeDefault;
    }
    public static int getFahrerlohnDefault() {
        return fahrerlohnDefault;
    }
    public static int getKosten2Default() {
        return kosten2Default;
    }
    public static int getEinnahmen2Default() {
        return einnahmen2Default;
    }
    public static int getAktivePopDefault() {
        return aktivePopDefault;
    }
    public static int getFahrpreisDefault() {
        return fahrpreisDefault;
    }
    public static int getRundenDefault() {
        return rundenDefault;
    }
    public static Userverwaltung getUser() {
        return user;
    }
    public static User getUserobj() {
        return userobj;
    }
    public static String getVersion() {
        return VERSION;
    }
    public static Firma getFirma() {
        return firma;
    }
    public static ExtensionJson getExtensionJson() {
        return extensionJson;
    }
    public static String getUuidStart() {
        return uuidStart;
    }
    public static Boolean getDebug() {
        return debug;
    }
    public static int getMusic() {
        return music;
    }
    public static List<Zug> getAktiveZuege() {
        return aktiveZuege;
    }
    public static EventManager getEventManager() {
        return eventManager;
    }
    public static Event getK() {
        return k;
    }
    public static int getI() {
        return i;
    }
    public static int getAktiveSitze() {
        return aktiveSitze;
    }
    public static int getFahrerlohn() {
        return fahrerlohn;
    }
    public static int getKosten() {
        return kosten;
    }
    public static int getEinnahmen() {
        return einnahmen;
    }
    public static int getAktivePop() {
        return aktivePop;
    }
    public static boolean isRadio() {
        return radio;
    }
    public static boolean isBaldiMode() {
        return baldiMode;
    }

}
