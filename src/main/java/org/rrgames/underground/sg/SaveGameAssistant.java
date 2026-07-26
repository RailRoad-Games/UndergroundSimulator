// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package org.rrgames.underground.sg;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

import org.rrgames.underground.Main;
import org.rrgames.underground.usr.Userverwaltung;

public class SaveGameAssistant {
   private static final String SAVEGAME_DIR = System.getProperty("user.home") + "/FnF-Apps/US/stable/resources/savegame";

   public SaveGameAssistant() {
   }

   public static SaveGame listAndSelectSavegames() {
      File dir = new File(SAVEGAME_DIR);
      if (dir.exists() && dir.isDirectory()) {
         File[] saves = dir.listFiles((fx) -> {
            return fx.getName().endsWith(".ussg");
         });
         if (saves != null && saves.length != 0) {
            Arrays.sort(saves);
            System.out.println("\n\ud83d\uddc2 Gefundene Spielstände:\n");
            int index = 1;
            File[] var3 = saves;
            int choice = saves.length;

            for(int var5 = 0; var5 < choice; ++var5) {
               File f = var3[var5];
               int var10001 = index++;
               System.out.println("" + var10001 + ") " + f.getName());
            }

            System.out.print("\nNummer wählen: ");
            @SuppressWarnings("resource")
            Scanner sc = new Scanner(System.in);
            choice = sc.nextInt();
            if (choice >= 1 && choice <= saves.length) {
               File selectedFile = saves[choice - 1];
               System.out.println("Save wird geladen...");
               SaveGame sg = SaveGame.load(selectedFile);
               if (sg == null) {
                  System.out.println("❌ Fehler beim Laden!");
                  return null;
               } else {
                  String pw = Main.readPassword("Passwort eingeben:");

                  try {
                     sg.userobj = new Userverwaltung().usersuchen(sg.username, pw);
                  } catch (Exception var9) {
                     System.out.println("❌ Passwort falsch!");
                     return null;
                  }

                  System.out.println("✔ Spielstand geladen: " + sg.name);
                  System.out.println("   Spieler: " + sg.username);
                  System.out.println("   Datum:   " + sg.formatiert);
                  Main.applySG(sg);
                  return sg;
               }
            } else {
               System.out.println("❌ Ungültige Eingabe!");
               return null;
            }
         } else {
            System.out.println("⚠️ Keine Savegames gefunden!");
            return null;
         }
      } else {
         System.out.println("❌ Savegame-Verzeichnis nicht gefunden: " + SAVEGAME_DIR);
         return null;
      }
   }
}
