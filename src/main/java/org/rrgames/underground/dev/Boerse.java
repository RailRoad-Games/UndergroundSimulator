package org.rrgames.underground.dev;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.rrgames.underground.core.Firma;

public class Boerse {
    public enum FirmenTyp { KONKURRENT, ALLGEMEIN }

    private String name;
    private double aktuellerKurs;
    private double kaufKurs; // 0, wenn man sie nicht besitzt
    private int besitzAnzahl; // Wie viele Aktien hast du?
    private FirmenTyp typ;

    public static List<Boerse> markt = new ArrayList<>();
    private static final Random random = new Random();

    public Boerse(String name, double startKurs, FirmenTyp typ) {
        this.name = name;
        this.aktuellerKurs = startKurs;
        this.typ = typ;
        this.kaufKurs = 0;
        this.besitzAnzahl = 0;
    }

    public static void initialisiereMarkt() {
        if (!markt.isEmpty()) return;

        // Kategorie: Konkurrenten (Zuggesellschaften)
        markt.add(new Boerse("SBB Cargo", 320.0, FirmenTyp.KONKURRENT));
        markt.add(new Boerse("DB Regio", 150.0, FirmenTyp.KONKURRENT));
        markt.add(new Boerse("ÖBB Alpha", 280.0, FirmenTyp.KONKURRENT));
        markt.add(new Boerse("SNCF Express", 210.0, FirmenTyp.KONKURRENT));

        // Kategorie: Andere Firmen
        markt.add(new Boerse("Googol LLC", 1200.0, FirmenTyp.ALLGEMEIN));
        markt.add(new Boerse("Apfel Inc.", 950.0, FirmenTyp.ALLGEMEIN));
        markt.add(new Boerse("McRon Corp", 45.0, FirmenTyp.ALLGEMEIN));
        markt.add(new Boerse("NVIDA Inc.", 600.0, FirmenTyp.ALLGEMEIN));
    }

    // Hilfsmethode: Berechnet den Gewinn/Verlust in Prozent
    public double getProfitProzent() {
        if (besitzAnzahl == 0 || kaufKurs == 0) return 0;
        return ((aktuellerKurs - kaufKurs) / kaufKurs) * 100;
    }

    // Getter & Setter (Wichtig für die Anzeige)
    public String getName() { return name; }
    public double getKurs() { return aktuellerKurs; }
    public FirmenTyp getTyp() { return typ; }
    public int getBesitzAnzahl() { return besitzAnzahl; }
    public void setBesitz(int anzahl, double kurs) { 
        this.besitzAnzahl = anzahl; 
        this.kaufKurs = kurs; 
    }
    public static String aktienKauf(int index, int menge, Firma spielerFirma) {
    // 1. Sicherheitcheck: Existiert die Firma an diesem Index?
    if (index < 0 || index >= markt.size()) {
        return "FEHLER: Ungültiger Firmen-Index!";
    }

    Boerse zielAktie = markt.get(index);
    double gesamtPreis = zielAktie.getKurs() * menge;

    // 2. Finanzcheck: Hat die Firma genug Geld?
    if (spielerFirma.getGeld() < gesamtPreis) {
        return "FEHLER: Nicht genug Geld! Benötigt: " + gesamtPreis + " €";
    }

    // 3. Transaktion durchführen
    spielerFirma.setGeld(spielerFirma.getGeld() - (int) gesamtPreis);
    
    // Durchschnittskurs berechnen (falls man schon Aktien hatte)
    if (zielAktie.getBesitzAnzahl() > 0) {
        // Logik für Mischkurs: (Alter Wert + Neuer Wert) / Gesamtanzahl
        double alterWert = zielAktie.getKaufKurs() * zielAktie.getBesitzAnzahl();
        double neuerKaufWert = gesamtPreis;
        double neuerDurchschnittsKurs = (alterWert + neuerKaufWert) / (zielAktie.getBesitzAnzahl() + menge);
        zielAktie.setKaufKurs(neuerDurchschnittsKurs);
    } else {
        zielAktie.setKaufKurs(zielAktie.getKurs());
    }

    zielAktie.setBesitzAnzahl(zielAktie.getBesitzAnzahl() + menge);

    return "ERFOLG: " + menge + "x " + zielAktie.getName() + " gekauft für " + gesamtPreis + " €.";
}

    public void setName(String name) {
       this.name = name;
    }

    public double getAktuellerKurs() {
       return aktuellerKurs;
    }

    public void setAktuellerKurs(double aktuellerKurs) {
       this.aktuellerKurs = aktuellerKurs;
    }

    public double getKaufKurs() {
       return kaufKurs;
    }

    public void setKaufKurs(double kaufKurs) {
       this.kaufKurs = kaufKurs;
    }

    public void setBesitzAnzahl(int besitzAnzahl) {
       this.besitzAnzahl = besitzAnzahl;
    }

    public void setTyp(FirmenTyp typ) {
       this.typ = typ;
    }

    public static List<Boerse> getMarkt() {
       return markt;
    }

    public static void setMarkt(List<Boerse> markt) {
       Boerse.markt = markt;
    }

    public static Random getRandom() {
       return random;
    }
}