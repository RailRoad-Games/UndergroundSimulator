package org.rrgames.underground.util;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Downloader {
    public static byte[] download(URL link) throws Exception {
        

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS) // sehr wichtig
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(link.toURI())
                .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            System.out.println("Fehler: HTTP " + response.statusCode());
            return null;
        }
        return response.body();
    }
}
