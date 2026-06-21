package de.qstudios.underground.dev;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopItemRegistry {
    private static List<ShopItem> items = new ArrayList<>();
    private static List<ShopActionHandler> handlers = new ArrayList<>();
    private static final ShopItemRegistry sir = new ShopItemRegistry();
    private static HashMap<ShopItem, Integer> buyed = new HashMap<>();

    public static void register(ShopItem item) {
        items.add(item);
    }
    public static void registerHandler(ShopActionHandler sah) {
        handlers.add(sah);
    }

    public static List<ShopItem> getItems() {
        return items;
    }

    public static List<ShopActionHandler> getHandlers() {
        return handlers;
    }
    public static void unregister(ShopItem si) {
        items.remove(si);
    }
    public static void unregisterHandler(ShopActionHandler sah) {
        handlers.remove(sah);
    }
    private ShopItemRegistry() {
        items = new ArrayList<>();
        handlers = new ArrayList<>();
    }
    public static ShopItemRegistry getInstance() {
        return sir;
    }
    public static void handleBuy(ShopItem si) {
        buyed.put(si, si.getAmount());
        for (ShopActionHandler handler : ShopItemRegistry.getHandlers()) {
    if (handler.supports(si.getAction())) {
        try {
            handler.execute(si);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
}

System.out.println("Keine Mod kann diese Action ausführen: " + si.getAction());

    }
    public static HashMap<ShopItem, Integer> getBuyed() {
        return buyed;
    }
}
