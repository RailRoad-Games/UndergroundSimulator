package de.qstudios.underground.usr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import de.qstudios.underground.Main;

public class Userverwaltung {
    public static final String VERSION = Main.VERSION;
    public static List<User> userlist = new LinkedList<>();
    public static String filename;

    static {
        String userHome = System.getProperty("user.home");

        filename = userHome + File.separator + "F&F-Apps"
                + File.separator + "US" + File.separator + "stable" + File.separator + "resources"
                + File.separator + "usr" + File.separator + "accounts-version-" + VERSION + ".usdb";

        File ordner = new File(filename).getParentFile();
        if (!ordner.exists()) {
            if (ordner.mkdirs()) {
                System.out.println("Verzeichnis wurde erstellt: " + ordner.getAbsolutePath());
            } else {
                System.err.println("Konnte Verzeichnis nicht erstellen!");
            }
        }
    }

    // 🔽 Ab hier public machen!
    public User neuerUser(String username, String userpswrt, String realname) {
        User user = new User(username, userpswrt, realname);
        userlist.add(user);
        return user;
    }

    public boolean userExistiert(String nutzername, String passwort) {
        return userlist.stream()
                .anyMatch(u -> u.getUsername().equals(nutzername) && u.getPasswd().equals(passwort));
    }

    public User usersuchen(String nutzername, String passwort) {
        return userlist.stream()
                .filter(u -> u.getUsername().equals(nutzername) && u.getPasswd().equals(passwort))
                .findFirst()
                .orElse(null);
    }

    public void liesAccounts() {
        File ordner = new File(filename).getParentFile();
        if (!ordner.exists()) {
            System.out.println("Der Ordner existiert nicht!");
        }
        File[] dateien = ordner
                .listFiles((dir, name) -> name.startsWith("accounts-version-") && name.endsWith(".usdb"));
        if (dateien != null) {
            Arrays.sort(dateien);
            int wieviele = dateien.length;
            if (wieviele > 0 && !dateien[wieviele - 1].equals(new File(filename))) {
                System.out.println("Achtung! Alte Accounts gefunden, möglicherweise inkompatibel!");
            }
        }
        try (Scanner scan = new Scanner(new File(filename))) {
            while (scan.hasNextLine()) {
                String[] infos = scan.nextLine().split("\t");
                infos  = ifNew(infos);
                userlist.add(new User(infos[0], infos[1], infos[2]));
            }
        } catch (Exception e) {
            System.out.println("Es gab ein Problem beim Einlesen der bekannten User!");
        }
    }

    public void schreibeAccounts() {
        try (FileWriter writer = new FileWriter(filename)) {
            for (User u : userlist) {
                writer.write(u.getUsername() + "\t" + u.getPasswd() + "\t" + u.getRealname() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Fehler!");
        }
    }
    public String[] ifNew(String[] infos) {
                try {
                    Integer.parseInt(infos[2]);
                    return infos = new String[]{infos[0], infos[1], Main.readstring("Veraltete Accounts erkannt! Bitte geben sie ihren Spielernamen für den Account "+infos[1]+"! (Bitte achten sie auf Groß- & Kleinschreibung)")};
                }
                catch (Exception e) {
                    return infos;
                }
    }
}
