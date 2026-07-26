package org.rrgames.underground.union;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class UnionClient {

    public static void main(String[] args) throws Exception {

        String url = "https://localhost:2121/creator_union";  // TLS!
        String password = "super-secure-password-usus";       // Klartext beim Client, wird im Header geschickt
        KeyStore ks = KeyStore.getInstance("JKS");
        try (var fis = java.nio.file.Files.newInputStream(Paths.get("keystore.jks"))) {
            ks.load(fis, "password".toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        HttpClient client = HttpClient.newBuilder()
            .sslContext(sslContext)
            .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("X-Union-Password", password) // Passwort-Header
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Union-Response:");
            String json = response.body();

            // Gson parsen
            Gson gson = new Gson();
            UnionModifiers union = gson.fromJson(json, UnionModifiers.class);

            System.out.println("Creator: " + union.getCreator());
            System.out.println("Users:");
            union.getUsers().forEach(u -> System.out.println(" - " + u.getName()));

        } else {
            System.out.println("Fehler: HTTP " + response.statusCode());
            System.out.println(response.body());
        }
    }
}
