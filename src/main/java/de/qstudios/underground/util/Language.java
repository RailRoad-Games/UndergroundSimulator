package de.qstudios.underground.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Language {
    public static HashMap<String, String> transl = new HashMap<>();
    private static File baseDir = Paths.get(System.getProperty("user.home"), File.separator, "FnF-Apps", File.separator,
            "US", File.separator, "stable" + File.separator, "resources", File.separator, "lang").toFile();

    public static void loadLang(Lang lang) {
        boolean exsists = new File(baseDir, lang.name() + ".json").exists();
        if (!exsists) {
            try {
                File file = new File(baseDir, lang.name() + ".json");
                file.getParentFile().mkdirs(); // sicherstellen, dass Ordner existiert

                byte[] data = Downloader
                        .download(URI.create("https://us-data.deepslatesmp.de/" + lang.name() + "-"+API.VERSION+".json").toURL());
                Files.write(file.toPath(), data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Gson gson = new Gson();

        // Typ für HashMap<String, String>
        Type mapType = new TypeToken<HashMap<String, String>>() {
        }.getType();

        try (FileReader reader = new FileReader(new File(baseDir, lang.name() + ".json"))) {
            // JSON einlesen und in HashMap laden
            transl = gson.fromJson(reader, mapType);
            /* System.out.println(transl.getOrDefault("hello", "FEHLER"));
             *System.out.println(transl.getOrDefault("good_morning", "FEHLER"));
            */
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static enum Lang {
        DE,
        FR,
        EN
    }

    public static void main(String[] args) {
        loadLang(Lang.DE);

    }
    public static String get(String key) {
        return transl.get(key);
    }
}
