// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package org.rrgames.underground.dev;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class SimulatorAPI {
   private final HttpClient client = HttpClient.newHttpClient();

   public SimulatorAPI() {
   }

   public String startSession(String username) throws Exception {
      System.out.println("Session gestartet");
      String json = String.format("{\"username\":\"%s\"}", username);
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://mrquatsch.pythonanywhere.com/us-api/start")).header("Content-Type", "application/json").POST(BodyPublishers.ofString(json)).build();
      HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
      return (String)response.body();
   }

   public String getStatus(String gameId) throws Exception {
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://mrquatsch.pythonanywhere.com/us-api/status/" + gameId)).GET().build();
      HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
      return (String)response.body();
   }

   public String validateSession(String gameId) throws Exception {
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://mrquatsch.pythonanywhere.com/us-api/validate/" + gameId)).POST(BodyPublishers.noBody()).build();
      HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
      return (String)response.body();
   }
}
