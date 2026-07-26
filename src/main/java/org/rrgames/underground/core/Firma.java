package org.rrgames.underground.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.rrgames.underground.dev.City;
import org.rrgames.underground.util.Language;
import org.rrgames.underground.util.Strecke;
import org.rrgames.underground.util.Zug;

public class Firma {
   private String name;
   private int geld;
   private int fahrer;
   private List<Zug> zuege;
   private List<Strecke> strecken;
   private int manager;
   private City city;

   private static final String[] STAEDTE = {
         "Lviv", "Braga", "Plovdiv", "Tartu", "Umeå", "Groningen", "Innsbruck",
         "Cluj-Napoca", "Timișoara", "Kaunas", "Skopje", "Varna", "Bilbao",
         "Turku", "Debrecen", "Aarhus", "Trieste", "Linz", "Ghent", "Odense",
         "Cork", "Tallinn", "Zadar", "Pécs", "Szeged"
   };

   public Firma(String name) {
      this.name = name;
      this.geld = 500;
      this.fahrer = 1;
      this.manager = 0;
      this.zuege = new LinkedList<>();
      this.zuege.add(new Zug(120, "Stadler", 60));
      this.strecken = new LinkedList<>();
      this.strecken.add(new Strecke());

      Random random = new Random();
      int index = random.nextInt(STAEDTE.length);
      this.city = new City(STAEDTE[index], 100000, 0.05);
   }

   // Getter & Setter
   public String getName() {
      return name;
   }

   public int getGeld() {
      return geld;
   }

   public void setGeld(int betrag) {
      this.geld = betrag;
   }

   public void addGeld(int betrag) {
      this.geld += betrag;
   }

   public int getFahrer() {
      return fahrer;
   }

   public void setFahrer(int fahrer) {
      this.fahrer = fahrer;
   }

   public void addFahrer(int anzahl) {
      this.fahrer += anzahl;
   }

   public int getManager() {
      return manager;
   }

   public void setManager(int manager) {
      this.manager = manager;
   }

   public List<Zug> getZug() {
      return zuege;
   }

   public void addZug(Zug z) {
      this.zuege.add(z);
   }

   public List<Strecke> getStrecke() {
      return strecken;
   }

   public void addStrecke(Strecke s) {
      this.strecken.add(s);
   }

   public City getCity() {
      return city;
   }

   public void setCity(City city) {
      this.city = city;
   }

   @Override
   public String toString() {
      int haltestellenZahl = 0;
      for (Strecke s : strecken) {
         haltestellenZahl += s.haltestellen.size();
      }

      return String.format("%s%s%s%d €, %s%d %s%d %s%d %s%d %s",
            Language.get("firma1"), name,
            Language.get("firma2"), geld,
            Language.get("firma3"), fahrer,
            Language.get("firma4"), zuege.size(),
            Language.get("firma5"), strecken.size(),
            Language.get("firma6"), haltestellenZahl,
            Language.get("firma7"));
   }
}