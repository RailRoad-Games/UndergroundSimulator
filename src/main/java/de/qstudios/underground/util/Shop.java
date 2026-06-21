// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package de.qstudios.underground.util;

import java.util.Scanner;

import de.qstudios.underground.Main;
@Deprecated(since = "v0.5.0-SNAPSHOT", forRemoval = true)
public class Shop {
   @Deprecated
   public Shop() {
   }
   @Deprecated
   public static void shop(String wahl2, Scanner scan) {
      int fahrergekauft = 0;
      int zuggekauft120 = 0;
      int zuggekauft240 = 0;
      int streckegebaut = 0;
      System.out.println("Produkte:");
      int fahrergeld = 0;
      int zug120geld = 0;
      int zug240geld = 0;
      int streckegeld = 0;
      int kostenzug120 = 1000;
      int kostenzug240 = 2000;
      System.out.println("Fahrer(600€ / 8h) Fahrer einstellen: F | Weiter: Enter");
      wahl2 = scan.nextLine().trim();
      Scanner scan2 = new Scanner(System.in);
      int wahl3;
      if (wahl2.equalsIgnoreCase("F")) {
         System.out.println("Wie viele wollen sie einstellen? WARNUNG! WENN SIE KEINE ZAHL EINGEBEN, GIBT ES EINEN FEHLER!");
         wahl3 = scan2.nextInt();
         System.out.println("OK, du hast jetzt " + wahl3 + " Fahrer eingestellt!");
         if (Main.firma.getFahrer() == 4) {
            System.out.println("Du hast deinen fünften Fahrer eingestellt! Du hast jetzt eine Verwaltung! Jetzt bekommst du bei jedem 5ten eingestelleten Fahrer einen 'Manager'. Eigentlich sitzt er nur rum und arbeitet, aber wenn du weniger Fahrer als Züge hast, springt er ein.");
         }

         fahrergeld = 600 * wahl3;
         Main.firma.addFahrer(wahl3);
         fahrergekauft += wahl3;
      }

      System.out.println("Zug mit 120 Sitzen(" + kostenzug120 + "€): Z1 | Zug mit 240 Sitzen(" + kostenzug240 + "€): Z2 | Weiter: Enter");
      wahl2 = scan.nextLine().trim();
      if (wahl2.equalsIgnoreCase("Z1")) {
         System.out.println("Wie viele wollen sie kaufen? WARNUNG! WENN SIE KEINE ZAHL EINGEBEN, GIBT ES EINEN FEHLER!");
         wahl3 = scan2.nextInt();
         System.out.println("OK, du hast jetzt " + wahl3 + " neue/n Zug/Züge mit 120 Sitzen gekauft!");
         kostenzug120 *= wahl3;
                           for (int i = wahl3; i > 0; i--){
         //Main.firma.addZug(new Zug(120 * wahl3, "Stadler", 60));
         }

         Main.firma.setGeld(Main.firma.getGeld() - kostenzug120);
         zuggekauft120 += wahl3;
      }

      if (wahl2.equalsIgnoreCase("Z2")) {
         System.out.println("Wie viele wollen sie kaufen? WARNUNG! WENN SIE KEINE ZAHL EINGEBEN, GIBT ES EINEN FEHLER!");
         wahl3 = scan2.nextInt();
         System.out.println("OK, du hast jetzt " + wahl3 + " neue/n Zug/Züge mit 240 Sitzen gekauft!");
                  for (int i = wahl3; i > 0; i--){
         //Main.firma.addZug(new Zug(240 * wahl3, "Stadler", 60));
         }
         Main.firma.setGeld(Main.firma.getGeld() -  (kostenzug240*wahl3));
         zuggekauft240 += wahl3;
         zug240geld = kostenzug240 * wahl3;
      }

      System.out.println("Strecke(3500€), drücken sie S, wenn sie neue Stecken bauen möchten!");
      wahl2 = scan.nextLine().trim();
      if (wahl2.equalsIgnoreCase("S")) {
         System.out.println("Wie viele wollen sie kaufen? WARNUNG! WENN SIE KEINE ZAHL EINGEBEN, GIBT ES EINEN FEHLER!");
         wahl3 = scan2.nextInt();
         System.out.println("OK, du hast jetzt " + wahl3 + " neue Strecke/n  gekauft!");
         for (int i = wahl3; i > 0; i--){
         Main.firma.addStrecke(new Strecke());
         }
         streckegeld = 3500 * wahl3;
         Main.firma.setGeld(Main.firma.getGeld() - streckegeld);
         streckegebaut += wahl3;
      }

      System.out.println("*BONG*");
      if (fahrergekauft > 0) {
         System.out.println();
      }

      if (zuggekauft120 > 0) {
         System.out.println("Züge gekauft " + zuggekauft120 + "x  |   " + zug120geld + "€");
      }

      if (zuggekauft240 > 0) {
         System.out.println("Züge gekauft " + zuggekauft240 + "x  |   " + zug240geld + "€");
      }

      if (streckegebaut > 0) {
         System.out.println("Strecken gebaut " + streckegebaut + "x  |   " + streckegeld + "€");
      }

      int endsumme = fahrergeld + zug120geld + zug240geld + streckegeld;
      System.out.println("*Transaktion Erfolgreich* - Endsumme: " + endsumme + " | Aktueller Kontostand: " + Main.firma.getGeld());
   }
}
