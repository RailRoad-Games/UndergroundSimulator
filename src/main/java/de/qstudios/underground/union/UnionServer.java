package de.qstudios.underground.union;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class UnionServer {

    private static final int PORT = 2121;

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", UnionServer::handleRequest);

        server.setExecutor(null);
        server.start();

        System.out.println("Union-Server läuft auf Port " + PORT);
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            send(exchange, 405, "Method Not Allowed");
            return;
        }

        String path = exchange.getRequestURI().getPath();
        // z.B. /myunion
        if (path.length() <= 1) {
            send(exchange, 400, "Union-ID fehlt");
            return;
        }

        String unionId = path.substring(1); // "/" entfernen

        Path unionFile = Path.of(
                System.getProperty("user.home"),
                ".us-union-server",
                unionId + ".json"
        );

        if (!Files.exists(unionFile)) {
            send(exchange, 404, "Union nicht gefunden");
            return;
        }

        String json = Files.readString(unionFile, StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        send(exchange, 200, json);
    }

    private static void send(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
