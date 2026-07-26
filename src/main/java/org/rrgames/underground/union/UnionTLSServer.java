package org.rrgames.underground.union;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.*;

import at.favre.lib.crypto.bcrypt.BCrypt;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.KeyStore;

public class UnionTLSServer {

    private static final int PORT = 2121;
    private static final String KEYSTORE = "keystore.jks";
    private static final char[] PASSWORD = "password".toCharArray();

    public static void main(String[] args) throws Exception {

        // --- SSL Context bauen ---
        KeyStore ks = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(KEYSTORE)) {
            ks.load(fis, PASSWORD);
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, PASSWORD);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);

        // --- HTTPS Server ---
        HttpsServer server = HttpsServer.create(new InetSocketAddress(PORT), 0);
        server.setHttpsConfigurator(new HttpsConfigurator(sslContext));
        server.createContext("/", UnionTLSServer::handleRequest);
        server.setExecutor(null);
        server.start();

        System.out.println("TLS-Union-Server läuft auf https://"+InetAddress.getLocalHost()+":" + PORT);
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {

    if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
        send(exchange, 405, "Method Not Allowed");
        return;
    }

    String unionId = exchange.getRequestURI().getPath().substring(1);
    
    Path unionFile = Path.of(
            System.getProperty("user.home"),
            ".us-union-server",
            unionId + ".json"
    );

    if (!Files.exists(unionFile)) {
        send(exchange, 404, "Union nicht gefunden");
        return;
    }

    String json = Files.readString(unionFile);
    JsonObject obj = new Gson().fromJson(json, JsonObject.class);

    String storedPassword = obj.get("password").getAsString();
    String providedPassword = exchange.getRequestHeaders()
            .getFirst("X-Union-Password");

    if (providedPassword == null || !providedPassword.equals(storedPassword)) {
        send(exchange, 401, "Unauthorized");
        return;
    }

    // Passwort entfernen bevor Response rausgeht
    obj.remove("password");
String hash = BCrypt.withDefaults()
        .hashToString(12, storedPassword.toCharArray());
    obj.addProperty("password", hash);
    exchange.getResponseHeaders().add("Content-Type", "application/json");
    send(exchange, 200, new Gson().toJson(obj));
}

    private static void send(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
