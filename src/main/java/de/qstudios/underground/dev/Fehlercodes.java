// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package de.qstudios.underground.dev;

import java.util.HashMap;
import java.util.Map;

public class Fehlercodes {
   private static final Map<String, String> FEHLER = new HashMap<>();
   private static final Map<String, String> EXCEPTION_MAP = new HashMap<>();

   public Fehlercodes() {
   }

   public static String getDescription(String code) {
      return (String)FEHLER.getOrDefault(code, "Unbekannter Fehlercode: " + code);
   }

   public static void listAll() {
      System.out.println("=== Underground-Simulator Fehlercodes ===");
      FEHLER.forEach((key, value) -> {
         System.out.println(key + " - " + value);
      });
   }

   public static String getCodeFromException(String exceptionSimpleName) {
      return (String)EXCEPTION_MAP.getOrDefault(exceptionSimpleName, "UG-999");
   }

   static {
      FEHLER.put("UG-101", "Fehler beim Laden der Mods");
      FEHLER.put("UG-102", "Fehler beim Laden oder Parsen der Extension-Datei");
      FEHLER.put("UG-103", "Terminal konnte nicht initialisiert werden");
      FEHLER.put("UG-104", "Fehler beim Einlesen des Passworts (JLine)");
      FEHLER.put("UG-105", "Fehler beim Laden der Haltestellen-Datei");
      FEHLER.put("UG-106", "Thread wurde unterbrochen (InterruptedException)");
      FEHLER.put("UG-107", "Fehler beim Start der Simulationsschleife");
      FEHLER.put("UG-108", "MP3 konnte nicht geladen oder abgespielt werden");
      FEHLER.put("UG-109", "WAV konnte nicht geladen oder abgespielt werden");
      FEHLER.put("UG-201", "Fehler beim Laden der Benutzerkonten");
      FEHLER.put("UG-202", "Fehler beim Speichern der Benutzerkonten");
      FEHLER.put("UG-203", "Benutzer nicht gefunden oder Datei beschädigt");
      FEHLER.put("UG-204", "Fehler beim Erstellen eines neuen Benutzers");
      FEHLER.put("UG-205", "Fehler beim Login-Vorgang");
      FEHLER.put("UG-301", "Fehler beim Speichern des Spielstands");
      FEHLER.put("UG-302", "Fehler beim Laden oder Auflisten der Spielstände");
      FEHLER.put("UG-401", "Fehler beim Initialisieren der API-Session");
      FEHLER.put("UG-402", "Fehler im Shop-System");
      FEHLER.put("UG-403", "Fehler beim Laden der Mods oder Addons");
      FEHLER.put("UG-404", "Fehler in ExtensionJson (ungültige Datei oder Format)");
      FEHLER.put("UG-999", "Unbekannter oder nicht klassifizierter Fehler");
      EXCEPTION_MAP.put("IOException", "UG-105");
      EXCEPTION_MAP.put("FileNotFoundException", "UG-105");
      EXCEPTION_MAP.put("InterruptedException", "UG-106");
      EXCEPTION_MAP.put("IllegalStateException", "UG-107");
      EXCEPTION_MAP.put("UnsupportedAudioFileException", "UG-109");
      EXCEPTION_MAP.put("LineUnavailableException", "UG-108");
      EXCEPTION_MAP.put("IOException", "UG-105");
      EXCEPTION_MAP.put("JsonParseException", "UG-201");
      EXCEPTION_MAP.put("JsonIOException", "UG-202");
      EXCEPTION_MAP.put("NullPointerException", "UG-204");
      EXCEPTION_MAP.put("SecurityException", "UG-205");
      EXCEPTION_MAP.put("InvalidPathException", "UG-301");
      EXCEPTION_MAP.put("ClassNotFoundException", "UG-302");
      EXCEPTION_MAP.put("MalformedURLException", "UG-404");
      EXCEPTION_MAP.put("JSONException", "UG-404");
   }
}
