// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package de.qstudios.underground.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;

import de.qstudios.underground.usr.User;
/**Klasse zum speichern und laden von Spielerstatistiken. Gespeichert wird etwas wie die Anzahl der gespielten Spiele, gewonnene und verlorene Spiele. */
public class Statistics {
   /** Das Datum und die Uhrzeit des ersten Logins des Spielers. */
   private static ZonedDateTime signuptimeT = ZonedDateTime.now(ZoneId.systemDefault());
   /** Das Datum und die Uhrzeit des ersten Logins des Spielers. */
   public String signuptime;
/** Die Gesamtzahl der gespielten Runden. */
   public int playtimes;

   /** Das Format für Datum und Uhrzeit des ersten Logins. */
   private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

   /**
    * Wie viele der {@link #playtimes} gewonnen wurden.
    */
   public int won;

   /**
    * Wie viele der {@link #playtimes} verloren wurden.
    */
   public int lost;
/**@hidden */
   public static void main(String[] args) {
      Statistics stat = new Statistics();
      stat.signuptime = signuptimeT.format(FORMATTER);
      System.out.println(stat.signuptime);
      Gson gson = new Gson();
      Statistics stats = null;

      try {
         stats = (Statistics)gson.fromJson(Files.readString((new File("/home/felix/.config/us/stats.json")).toPath()), Statistics.class);
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      System.out.println("Json geladen: Playtime = " + stats.playtimes + ", Spiele gewonnen = " + stats.won + ", Spiele verloren = " + stats.lost);
   }
   /**Methode zum Laden der Spielerstatistiken.
    * @param user Der Benutzer, dessen Statistiken geladen werden sollen.
    * @return Ein {@link Statistics}-Objekt mit den geladenen Statistiken.
    */
   public Statistics getPlayerStatistics(User user) {
      Gson gson = new Gson();
      Path path = Path.of(System.getProperty("user.home"), ".config", "us", user.getUsername() + ".json");
      path.toFile().getParentFile().mkdirs();

      try {
         path.toFile().createNewFile();
      } catch (IOException var7) {
         var7.printStackTrace();
      }

      if (!Files.exists(path, new LinkOption[0])) {
         return new Statistics();
      } else {
         try {
            String json = Files.readString(path, StandardCharsets.UTF_8);
            if (json.isBlank()) {
               return new Statistics();
            } else {
               Statistics stats = (Statistics)gson.fromJson(json, Statistics.class);
               return stats != null ? stats : new Statistics();
            }
         } catch (IOException var6) {
            var6.printStackTrace();
            return new Statistics();
         }
      }
   }
   /**Methode zum Schreiben der Spielerstatistiken.
    * @param toWrite Das Statistikobjekt, das gespeichert werden soll.
    * @param user Der Benutzer, dessen Statistiken gespeichert werden sollen.
    */
   public void writePlayerStatistics(Statistics toWrite, User user) {
      Gson gson = new Gson();

      try {
         String jsonString = gson.toJson(toWrite);
         String var10004 = System.getProperty("user.home");
         BufferedWriter writer = new BufferedWriter(new FileWriter(var10004 + File.separator + ".config" + File.separator + "us" + File.separator + user.getUsername() + ".json"));
         writer.write(jsonString);
         writer.close();
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }
   /**Konstruktor zum Erstellen eines neuen Statistikobjekts.
    * @param signuptime Das Datum und die Uhrzeit des ersten Logins des Spielers.
    * @param playtimes Wie oft der Spieler gespielt hat.
    * @param won Wie viele Spiele der Spieler gewonnen hat.
    * @param lost Wie viele Spiele der Spieler verloren hat.
    */
   public Statistics(ZonedDateTime signuptime, int playtimes, int won, int lost) {
      this.signuptime = signuptimeT.toString();
      this.signuptime = signuptimeT.format(FORMATTER);
      this.playtimes = playtimes;
      this.won = won;
      this.lost = lost;
   }
   /**@hidden */
   public Statistics() {
      this.signuptime = signuptimeT.toString();
   }
}
