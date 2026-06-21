// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package de.qstudios.underground.union;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import de.qstudios.underground.Main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Union {
   private static final Logger LOG = LoggerFactory.getLogger(Union.class);
   public Union(de.qstudios.underground.usr.User usr) {
        String unionID;
        String URL;
      System.out.println("Willkommen, "+usr.getRealname()+", zum Unionseinrichtungsassistenten!");
      String str = Main.readstring("Bitte gebe die ID deiner Union ein! | !!E: Erweitert");
      switch (str) {
        case "!!E": 
        URL = Main.readstring("Bitte geb die URL deiner Union ein! ");
        break;
        default: 
        unionID = str;
        URL = "https://us-data.deepslatesmp.de:211921/"+unionID;
      }
   }
   public static UnionModifiers fetch(String host) {
 try {

            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS) // sehr wichtig
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(host))
        .header("Accept", "QStudioGames/Union")
        .header("X-Union-Password", "super-secure-password-usus")
        .build();


            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Fehler: HTTP " + response.statusCode());
                return null;
            }

            String json = response.body();

            Gson gson = new Gson();
         UnionModifiers got = gson.fromJson(json, UnionModifiers.class);
         return got;
        } catch (JsonSyntaxException | IOException | InterruptedException e) {
            LOG.error(Main.getStackTraceAsString(e));
            
        }
        return null;
   
}
public Union() {}
public static void main(String[] args) {
        //System.out.println(java.time.LocalTime.now().format(new DateTimeFormatterBuilder().toFormatter(Locale.GERMANY).ofPattern("DD MM YYYY z")));
        UnionModifiers union = fetch("http://localhost:2121/creator_union");
        System.out.println(union.toString());
}
@Override
public String toString() {
        return "Union []";
}

}
