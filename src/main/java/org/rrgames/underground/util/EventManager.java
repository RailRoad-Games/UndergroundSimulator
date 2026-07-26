package org.rrgames.underground.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.Gson;

public class EventManager {

    // Liste mit Standard-Events
    List<Event> karten = new ArrayList<>();

    /*
     * public EventManager getInstance() {
     * return instance;
     * }
     */
    public void addEvent(Event event) {
        karten.add(event);
        karten.add(new Event(0, 0, 0, 0, "Normaler Betrieb. Es passiert nichts."));
    }

    public void removeEvent(Event event) {
        karten.remove(event);
        karten.remove(new Event(0, 0, 0, 0, "Normaler Betrieb. Es passiert nichts."));
    }

    // public EventManager instance = new EventManager();
    public EventManager() {
        // Standard-Events hinzufügen
        karten.add(new Event(-100, 0, 0, -1,
                "Überschwemmung! Eine Ihrer Strecken steht Ihnen aktuell nicht zur Verfügung. Die Reparaturkosten belaufen sich auf 100€!"));
        karten.add(new Event(0, -1, 0, 0,
                "Fahrer erkrankt! Einer Ihrer Züge kann heute nicht fahren, da der zugewiesene Fahrer erkrankt ist."));
        karten.add(new Event(-20, 0, -1, 0,
                "Verdammte Teenager! Laut Zeugen wurde gestern einer unserer Züge mit Graffiti besprüht, das bedeutet, dass er für 20€ gesäubert werden muss."));
        karten.add(new Event(-30, 0, -1, 0,
                "Es war Volksfest! Einer deiner Züge ist durch betrunkene Menschen verschmutzt, ein Tag lang werden diese für 10€ pro Zug gereinigt."));
        karten.add(new Event(0, -1, 0, 1,
                "Ferien! Deine Firma verdient mehr Geld, da viele Leute mitfahren wollen. Du verlierst für die Runde einen Fahrer."));
        karten.add(new Event(0, 0, 0, 0, "Normaler Betrieb. Es passiert nichts."));
        karten.add(new Event(0, 0, 0, 0, "Normaler Betrieb. Es passiert nichts."));
        karten.add(new Event(0, 0, 0, 0, "Normaler Betrieb. Es passiert nichts."));
        karten.add(new Event(0, 0, 0, 0, "Normaler Betrieb. Es passiert nichts."));
        karten.add(new Event(0, 0, 0, 0, "Normaler Betrieb. Es passiert nichts."));

        // Versuch, Events aus JSON-Datei zu laden
        try {
            String filePath = System.getProperty("user.home")
                    + File.separator + "FnF-Apps"
                    + File.separator + "US"
                    + File.separator + "stable"
                    + File.separator + "resources"
                    + File.separator + "events.json";

            if (Files.exists(Paths.get(filePath), new LinkOption[0]) && Files.isReadable(Paths.get(filePath))) {
                Event[] loadedEvents = new Gson().fromJson(Files.readString(Paths.get(filePath)), Event[].class);
                karten.clear();
                for (Event e : loadedEvents) {
                    karten.add(e);
                }
            } else {
                System.out.println(
                        "Die Datei 'events.json' wurde nicht gefunden. Es wird mit Standardkarten gearbeitet.");
            }
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Karte-Datei: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Zufälliges Event zurückgeben
    public Event getEvent() {
        int randomNummer = ThreadLocalRandom.current().nextInt(karten.size());
        return karten.get(randomNummer);
    }
}