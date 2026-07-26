// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package org.rrgames.underground.dev;

public class Bahnhof {
   private String name;
   private String bahnhofsCode;
   private boolean barrierefrei;
   private boolean aufzug;
   private boolean rollstuhlZugang;
   private boolean blindentastLeitsystem;
   private int anzahlGleise;
   private boolean parkplatz;
   private boolean fahrradstellplaetze;
   private boolean wc;
   private boolean wlan;
   private boolean gepaeckaufbewahrung;
   private boolean busAnbindung;
   private boolean uBahn;
   private boolean sBahn;
   private boolean taxiStand;
   private int baujahr;
   private boolean denkmalgeschuetzt;
   private String oeffnungszeiten;
   private Bahnhofstyp bahnhofstyp;

   public String toString() {
      String var10000 = this.name;
      return var10000 + " (Bahnhofskürzel=" + this.bahnhofsCode + ", Barrierefrei=" + this.barrierefrei + ", hat Aufzug=" + this.aufzug + ", hat Rollstuhlzugang=" + this.rollstuhlZugang + ", hat Blindentastleitsystem=" + this.blindentastLeitsystem + ", Anzahl Gleise=" + this.anzahlGleise + ", hat Parkplatz=" + this.parkplatz + ", hat Fahrradstellplaetze=" + this.fahrradstellplaetze + ", hat WC=" + this.wc + ", hat WLAN=" + this.wlan + ", hat Gepäckaufbewahrung=" + this.gepaeckaufbewahrung + ", hat Busanbindung=" + this.busAnbindung + ", U-Bahn-Anbindung=" + this.uBahn + ", S-Bahn-Anbindung=" + this.sBahn + ", hat Taxistand=" + this.taxiStand + ", Baujahr=" + this.baujahr + ", ist Denkmalgeschuetzt=" + this.denkmalgeschuetzt + ", Öffnungszeiten=" + this.oeffnungszeiten + ", Bahnhofstyp=" + String.valueOf(this.bahnhofstyp) + ")";
   }

   public String getName() {
      return this.name;
   }

   public Bahnhofstyp getBahnhofstyp() {
      return this.bahnhofstyp;
   }

   public void setBahnhofstyp(Bahnhofstyp bahnhofstyp) {
      this.bahnhofstyp = bahnhofstyp;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getBahnhofsCode() {
      return this.bahnhofsCode;
   }

   public void setBahnhofsCode(String bahnhofsCode) {
      this.bahnhofsCode = bahnhofsCode;
   }

   public boolean isBarrierefrei() {
      return this.barrierefrei;
   }

   public void setBarrierefrei(boolean barrierefrei) {
      this.barrierefrei = barrierefrei;
   }

   public boolean isAufzug() {
      return this.aufzug;
   }

   public void setAufzug(boolean aufzug) {
      this.aufzug = aufzug;
   }

   public boolean isRollstuhlZugang() {
      return this.rollstuhlZugang;
   }

   public void setRollstuhlZugang(boolean rollstuhlZugang) {
      this.rollstuhlZugang = rollstuhlZugang;
   }

   public boolean isBlindentastLeitsystem() {
      return this.blindentastLeitsystem;
   }

   public void setBlindentastLeitsystem(boolean blindentastLeitsystem) {
      this.blindentastLeitsystem = blindentastLeitsystem;
   }

   public int getAnzahlGleise() {
      return this.anzahlGleise;
   }

   public void setAnzahlGleise(int anzahlGleise) {
      this.anzahlGleise = anzahlGleise;
   }

   public boolean isParkplatz() {
      return this.parkplatz;
   }

   public void setParkplatz(boolean parkplatz) {
      this.parkplatz = parkplatz;
   }

   public boolean isFahrradstellplaetze() {
      return this.fahrradstellplaetze;
   }

   public void setFahrradstellplaetze(boolean fahrradstellplaetze) {
      this.fahrradstellplaetze = fahrradstellplaetze;
   }

   public boolean isWc() {
      return this.wc;
   }

   public void setWc(boolean wc) {
      this.wc = wc;
   }

   public boolean isWlan() {
      return this.wlan;
   }

   public void setWlan(boolean wlan) {
      this.wlan = wlan;
   }

   public boolean isGepaeckaufbewahrung() {
      return this.gepaeckaufbewahrung;
   }

   public void setGepaeckaufbewahrung(boolean gepaeckaufbewahrung) {
      this.gepaeckaufbewahrung = gepaeckaufbewahrung;
   }

   public boolean isBusAnbindung() {
      return this.busAnbindung;
   }

   public void setBusAnbindung(boolean busAnbindung) {
      this.busAnbindung = busAnbindung;
   }

   public boolean isuBahn() {
      return this.uBahn;
   }

   public void setuBahn(boolean uBahn) {
      this.uBahn = uBahn;
   }

   public boolean issBahn() {
      return this.sBahn;
   }

   public void setsBahn(boolean sBahn) {
      this.sBahn = sBahn;
   }

   public boolean isTaxiStand() {
      return this.taxiStand;
   }

   public void setTaxiStand(boolean taxiStand) {
      this.taxiStand = taxiStand;
   }

   public int getBaujahr() {
      return this.baujahr;
   }

   public void setBaujahr(int baujahr) {
      this.baujahr = baujahr;
   }

   public boolean isDenkmalgeschuetzt() {
      return this.denkmalgeschuetzt;
   }

   public void setDenkmalgeschuetzt(boolean denkmalgeschuetzt) {
      this.denkmalgeschuetzt = denkmalgeschuetzt;
   }

   public String getOeffnungszeiten() {
      return this.oeffnungszeiten;
   }

   public void setOeffnungszeiten(String oeffnungszeiten) {
      this.oeffnungszeiten = oeffnungszeiten;
   }

   public Bahnhof(String name, String bahnhofsCode, Bahnhofstyp typ, int baujahr) {
      this.name = name;
      this.bahnhofsCode = bahnhofsCode;
      this.bahnhofstyp = typ;
      this.baujahr = baujahr;
   }

   public Bahnhof(String name, String bahnhofsCode, Bahnhofstyp bahnhofstyp, boolean barrierefrei, boolean aufzug, boolean rollstuhlZugang, boolean blindentastLeitsystem, int anzahlGleise, boolean parkplatz, boolean fahrradstellplaetze, boolean wc, boolean wlan, boolean gepaeckaufbewahrung, boolean busAnbindung, boolean uBahn, boolean sBahn, boolean taxiStand, int baujahr, boolean denkmalgeschuetzt, String oeffnungszeiten) {
      this.name = name;
      this.bahnhofsCode = bahnhofsCode;
      this.bahnhofstyp = bahnhofstyp;
      this.barrierefrei = barrierefrei;
      this.aufzug = aufzug;
      this.rollstuhlZugang = rollstuhlZugang;
      this.blindentastLeitsystem = blindentastLeitsystem;
      this.anzahlGleise = anzahlGleise;
      this.parkplatz = parkplatz;
      this.fahrradstellplaetze = fahrradstellplaetze;
      this.wc = wc;
      this.wlan = wlan;
      this.gepaeckaufbewahrung = gepaeckaufbewahrung;
      this.busAnbindung = busAnbindung;
      this.uBahn = uBahn;
      this.sBahn = sBahn;
      this.taxiStand = taxiStand;
      this.baujahr = baujahr;
      this.denkmalgeschuetzt = denkmalgeschuetzt;
      this.oeffnungszeiten = oeffnungszeiten;
   }

   public static enum Bahnhofstyp {
      VORORT,
      WOHNSIEDLUNG,
      INNENSTADT,
      FLUGHAFEN,
      INDUSTRIEGEBIET,
      TOURISTIK,
      HAUPTBAHNHOF;

      private Bahnhofstyp() {
      }
   }
}
