package org.rrgames.underground.dev;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.rrgames.underground.Main;
import org.rrgames.underground.util.API;

public class ShopV3 {
    public static void shop(String wahl2, Scanner scan) {
        System.out.println("Produkte:");
        for (ShopItem si : ShopItemRegistry.getItems()) {
            System.out.println(si.getName() + " (" + si.getPrice() + "): H (wie hinzufügen) | Weiter: Enter");
            wahl2 = scan.nextLine().trim();
            int input = 0;
            try {
        input = System.in.read(); // liest ein Byte
            } catch (IOException e) {
                FehlerReporter.report(UUID.fromString(API.uuidStart), e.getMessage(), e.getClass().toString(), Main.getStackTraceAsString(e));
                }
        char c = (char) input;
            int wahl3;
            if (wahl2.equalsIgnoreCase("H")) {
                System.out.println(
                        "Wie viele wollen sie hinzufügen? WARNUNG! WENN SIE KEINE ZAHL EINGEBEN, GIBT ES EINEN FEHLER!");
                wahl3 = (int) c;
                System.out.println("OK, du hast jetzt " + wahl3 + " mal " + si.getName() + " hinzugefügt!");
                si.setAmount(wahl3);
                ShopItemRegistry.handleBuy(si);
            }
        }
        System.out.println("*BONG*");
        int endsumme = 0;
        // Wir gehen über das "entrySet()", das Paare aus Key und Value enthält
        for (Map.Entry<ShopItem, Integer> entry : ShopItemRegistry.getBuyed().entrySet()) {

            ShopItem item = entry.getKey(); // Hier holst du das ShopItem
            Integer amount = entry.getValue(); // Hier holst du die Anzahl (Integer)
            if (amount > 0) {
                System.out.println(
                        item.getName() + " hinzugefügt " + amount + "x  |   " + item.getPrice() * amount + "€");
                endsumme += item.getPrice() * amount;
            }
        }
        System.out.println("*Transaktion Erfolgreich* - Endsumme: " + endsumme + " | Aktueller Kontostand: "
                + API.firma.getGeld());
    }
}
