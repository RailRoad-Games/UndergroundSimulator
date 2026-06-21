// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package de.qstudios.underground.sg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.qstudios.underground.Main;
import de.qstudios.underground.core.Firma;
import de.qstudios.underground.dev.SimulatorAPI;
import de.qstudios.extmgr.ExtensionJson;
import de.qstudios.underground.usr.User;
import de.qstudios.underground.usr.Userverwaltung;
import de.qstudios.underground.util.Event;
import de.qstudios.underground.util.EventManager;
import de.qstudios.underground.util.Zug;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SaveGame {
   public String name;
   public Userverwaltung user = new Userverwaltung();
   public User userobj;
   public String version = Main.VERSION;
   public Firma firma;
   public LinkedList<String> haltestelle = new LinkedList<>();
   public ArrayList<String> gebrauchtehaltestellen = new ArrayList<>();
   public ZonedDateTime jetzt = ZonedDateTime.now(ZoneId.of("Europe/Berlin"));
   public transient DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy z");
   public String formatiert;
   public ExtensionJson extensionJson;
   public String uuidStart;
   public Boolean debug = false;
   public transient SimulatorAPI api = new SimulatorAPI();
   public String aktienkurs = "0";
   public int aktienkursja = 1;
   public String username;
   public String userpswrt;
   public int i;
   public EventManager eventManager = new EventManager();
   public Event k;
   public int aktiveSitze;
   public int fahrerlohn;
   public int kosten2;
   public int einnahmen2;
   public int aktivePop;
   public List<Zug> aktiveZuege = new LinkedList<>();
   public int haltestellenanzahl;

   public SaveGame() {
   }

   public void finalizeLoad() {
      if (this.formatter == null) {
         this.formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy z");
      }

      if (this.jetzt == null) {
         this.jetzt = ZonedDateTime.now(ZoneId.of("Europe/Berlin"));
      }

      this.formatiert = this.jetzt.format(this.formatter);
      if (this.api == null) {
         this.api = new SimulatorAPI();
      }

      if (this.eventManager == null) {
         this.eventManager = new EventManager();
      }

      if (this.k == null) {
         this.k = this.eventManager.getEvent();
      }

      if (this.haltestelle == null) {
         this.haltestelle = new LinkedList<>();
      }

      if (this.gebrauchtehaltestellen == null) {
         this.gebrauchtehaltestellen = new ArrayList<>();
      }

      if (this.aktiveZuege == null) {
         this.aktiveZuege = new LinkedList<>();
      }

      if (this.firma == null) {
         this.firma = new Firma("Unbenannt");
      }

      if (this.name == null) {
         this.name = "unnamed-save";
      }

      if (this.version == null) {
         this.version = "v0.4.5-alpha.dev";
      }

   }

   public static void save(SaveGame sg) {
      try {
         String base = System.getProperty("user.home") + "/FnF-Apps/US/stable/resources/savegame/";
         Path dir = Paths.get(base);
         Files.createDirectories(dir);
         String filename = base + sg.name + ".ussg";
         Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter()).create();
         String json = gson.toJson(sg);
         Files.writeString(Paths.get(filename), json);
         System.out.println("✔ Save gespeichert: " + filename);
      } catch (Exception var6) {
         System.out.println("❌ Fehler beim Speichern!");
         var6.printStackTrace();
      }

   }

   public static SaveGame load(File file) {
      try {
         String json = Files.readString(file.toPath());
         Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter()).create();
         SaveGame sg = (SaveGame)gson.fromJson(json, SaveGame.class);
         sg.finalizeLoad();
         System.out.println("✔ Save geladen: " + sg.name);
         return sg;
      } catch (Exception var4) {
         System.out.println("❌ Fehler beim Laden von: " + file.getName());
         var4.printStackTrace();
         return null;
      }
   }
}
