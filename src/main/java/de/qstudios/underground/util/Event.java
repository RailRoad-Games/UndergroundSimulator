// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package de.qstudios.underground.util;

public class Event {
   public int geld;
   public int fahrer;
   public int zuege;
   public int strecken;
   public String nachricht;

   public Event(int geld, int fahrer, int zuege, int strecken, String nachricht) {
      this.geld = geld;
      this.fahrer = fahrer;
      this.zuege = zuege;
      this.strecken = strecken;
      this.nachricht = nachricht;
   }

   public int getGeld() {
      return this.geld;
   }

   public int getFahrer() {
      return this.fahrer;
   }

   public int getZuege() {
      return this.zuege;
   }

   public int getStrecken() {
      return this.strecken;
   }

   public String toString() {
      return this.nachricht;
   }
}
