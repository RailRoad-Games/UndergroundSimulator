package de.qstudios.underground.dev;

import de.qstudios.underground.util.*;

public class BaseShopActionHandler implements ShopActionHandler {
    public boolean supports(String str) {
        if (str.equalsIgnoreCase("addFahrer") || str.startsWith("addZug") || str.equalsIgnoreCase("addStrecke")) {
            return true;
        }
        return false;
    }

    public void execute(ShopItem si) {
        if (si.getAction().equalsIgnoreCase("addFahrer")) {
            API.firma.addFahrer(si.getAmount());
        } else if (si.getAction().equalsIgnoreCase("addZug120")) {
            for (int i = si.getAmount(); i > 0; i--) {
                API.firma.addZug(new Zug(120, "Stadler", 80));
            }
        } else if (si.getAction().equalsIgnoreCase("addStrecke")) {
            for (int i = si.getAmount(); i > 0; i--) {
                API.firma.addStrecke(new Strecke());
            }
        } else if (si.getAction().equalsIgnoreCase("addZug240")) {
            for (int i = si.getAmount(); i > 0; i--) {
                API.firma.addZug(new Zug(240, "Stadler", 80));
            }
        }
    }
}
