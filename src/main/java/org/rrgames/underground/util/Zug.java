// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package org.rrgames.underground.util;

import org.rrgames.underground.dev.TrainInterface;

/**
 * Hauptklasse für Züge.
 * 
 * @author FeGmbH
 */
public class Zug implements TrainInterface {
   /** Anzahl der Plätze im Zug. */
   private int sitze;
   /** Hersteller des Zugs. */
   private String hersteller;
   /** Kosten des Zugs. */
   private int kosten;
   /** Maximale Geschwindigkeit des Zugs. */
   private int v_max;
   /** Anzahl der Türen des Zugs. Aktuell nicht verwerwendet. */
   private int doors;

   /**
    * Konstruktor für die Erstellung eines Zuges.
    * 
    * @param sitze      Anzahl der Plätze im Zug
    * @param hersteller Hersteller des Zugs
    * @param v_max      Maximale Geschwindigkeit des Zugs
    */
   public Zug(int sitze, String hersteller, int v_max) {
      this.sitze = sitze;
      this.hersteller = hersteller;
      this.kosten = sitze == 120 ? 100 : 200;
      this.v_max = v_max;
      this.doors = sitze == 120 ? 4 : 6;
   }
   /**@hidden */
   public int getSitze() {
      return this.sitze;
   }
   /**@hidden */
   public int getDoors() {
      return this.doors;
   }
 /**@hidden */
   public String getHersteller() {
      return this.hersteller;
   }
      /**@hidden */
   public int getKosten() {
      return this.kosten;
   }
      /**@hidden */
   public int getV_max() {
      return this.v_max;
   }
}
