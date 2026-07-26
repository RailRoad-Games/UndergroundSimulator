// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package org.rrgames.underground.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.rrgames.underground.dev.Bahnhof;
import org.rrgames.underground.dev.Bahnhof.Bahnhofstyp;
/**Hauptklasse für Strecken des Underground Simulator, mit Werten, wichtig für die Ausrechnung der Einnahmen, wie z.B. {@code StreckenLängeZeit} */
public class Strecke {
    public int StreckenLängeZeit;
   private int laenge = 10000;
   private int pop = 100;
   public List<Bahnhof> haltestellen = new ArrayList<>();

   public Strecke() {
      for(int i = 0; i < 11; ++i) {
         if (i != 1 && i != 10) {
            if (i == 2 || i == 9) {
               this.haltestellen.add(new Bahnhof("Bahnhof " + i, "DB-" + i, Bahnhofstyp.VORORT, true, true, true, false, 2, true, true, false, true, false, false, false, false, false, 2000 + i, false, "8:00-23:59"));
            }
         } else {
            this.haltestellen.add(new Bahnhof("Bahnhof " + i, "DB-" + i, Bahnhofstyp.VORORT, false, true, true, false, 2, true, true, false, true, false, false, false, false, false, 2000 + i, false, "8:00-23:59"));
         }
      }

   }

   public int getLaenge() {
      return this.laenge;
   }

   public int getPop() {
      return this.pop;
   }

   public static int randomZahl(int nochübrigehaltestellen) {
      Random randomzahl = new Random();
      int randomZahl = randomzahl.nextInt(nochübrigehaltestellen + 1);
      return randomZahl;
   }

   public static void main(String[] args) {
      new Strecke();
   }
}
