package de.qstudios.underground.union;

import java.time.LocalTime;

public class ChatMessage {

    private String at;
    private User from;
    private User to;
    private String message;

    public ChatMessage(User from, User to, String message) {
        this.at = LocalTime.now().toString();
        this.from = from;
        this.to = to;
        this.message = message;
    }

    @Override
    public String toString() {
        return "[" + at + "] " + from.getName() +
                " -> " + to.getName() +
                ": " + message;
    }
}
