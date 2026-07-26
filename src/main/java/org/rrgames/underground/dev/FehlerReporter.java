// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package org.rrgames.underground.dev;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;

import org.rrgames.underground.Main;

public class FehlerReporter {
   private static final String SERVER_URL = "https://deepslatesmp.de/api/report";
   private static final Gson gson = new Gson();

   public FehlerReporter() {
   }

   public static boolean report(UUID uuid, String errorMessage, String exceptionClass, String stacktrace) {
      System.out.println();
      System.err.println("DEBUG: FehlerReporter.report() gestartet");
      System.out.println("Ein Fehler ist aufgetreten. Der Fehlerbericht wird an den Server gesendet...");

      try {
         Map<String, Object> report = new HashMap<>();
         report.put("uuid", uuid.toString());
         report.put("error", errorMessage);
         report.put("exceptionType", exceptionClass);
         report.put("stacktrace", stacktrace);
         report.put("timestamp", Instant.now().getEpochSecond());
         String json = gson.toJson(report);
         HttpURLConnection conn = (HttpURLConnection)new URI(SERVER_URL).toURL().openConnection();
         conn.setRequestMethod("POST");
         conn.setDoOutput(true);
         conn.setRequestProperty("Content-Type", "application/json");
         OutputStream os = conn.getOutputStream();

         try {
            os.write(json.getBytes());
            os.flush();
         } catch (Throwable var11) {
            if (os != null) {
               try {
                  os.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (os != null) {
            os.close();
         }

         System.out.println("Versenden abgeschlossen. Um Hilfe zu erhalten, bitte wenden sie sich an info@mr-quatsch.de innerhalb von 14 Tagen und hängen sie folgende ID an: " + uuid.toString());
         int responseCode = conn.getResponseCode();
         return responseCode >= 200 && responseCode < 300;
      } catch (Exception var12) {
         System.err.println("Fehler beim Senden des Fehlerberichts: " + var12.getMessage());
         report(uuid, Main.getStackTraceAsString(var12));
         return false;
      }
   }

   public static boolean report(UUID uuid, String errorMessage) {
      return report(uuid, errorMessage, (String)null, (String)null);
   }
}
