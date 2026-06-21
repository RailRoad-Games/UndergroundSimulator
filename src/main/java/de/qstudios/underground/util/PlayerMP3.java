// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package de.qstudios.underground.util;

import java.io.File;
import java.io.FileInputStream;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import javazoom.jl.player.Player;

public class PlayerMP3 {
   public PlayerMP3() {
   }

   public static Thread newSoundThread(String path) {
    return new Thread(() -> {
        try (FileInputStream fis = new FileInputStream(path)) {
            System.out.println("Starte Player...");
            Player player = new Player(fis);
            player.play();
            System.out.println("Player fertig.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
}public static void main(String[] args) {

    Thread t = newSoundThread("/home/felix/FnF-Apps/US/stable/resources/music/Taylor_Swift- Welcome_To_New_York_(Taylors_Version).mp3");
    t.start();   // <-- wichtig!

    try {
        File mp3File = new File("Taylor_Swift- Welcome_To_New_York_(Taylors_Version).mp3");
        Mp3File mp3 = new Mp3File(mp3File);

        if (mp3.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3.getId3v2Tag();
            System.out.println("Titel: " + id3v2Tag.getTitle());
            System.out.println("Künstler: " + id3v2Tag.getArtist());
            System.out.println("Album: " + id3v2Tag.getAlbum());
        } else {
            System.out.println("Keine ID3v2 Tags gefunden.");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}

}
