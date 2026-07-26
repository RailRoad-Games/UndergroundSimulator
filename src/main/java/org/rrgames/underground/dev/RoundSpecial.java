package org.rrgames.underground.dev;

import java.util.HashMap;
import java.util.Map;

import org.rrgames.underground.core.Firma;

public class RoundSpecial {

    private static Map<Integer, RoundEvent> events = new HashMap<>();

    public interface RoundEvent {
        void run(Firma firma);
    }

    public static void register(int round, RoundEvent event) {
        events.put(round, event);
    }

    public static void execute(int round, Firma firma) {
        RoundEvent event = events.get(round);
        if (event != null) {
            event.run(firma);
        }
    }
}