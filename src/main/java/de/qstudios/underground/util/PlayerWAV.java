// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package de.qstudios.underground.util;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class PlayerWAV {
   public PlayerWAV() {
   }

   public static void main(String[] args) {
      newSoundThread("sound.wav");
   }

   public static Thread newSoundThread(String path) {
      return (new Thread(() -> {
         File audioFile = new File(path);

         try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            try {
               Clip clip = AudioSystem.getClip();
               clip.open(audioStream);
               clip.start();

               while(!clip.isRunning()) {
                  Thread.sleep(10L);
               }

               while(true) {
                  if (!clip.isRunning()) {
                     clip.close();
                     break;
                  }

                  Thread.sleep(10L);
               }
            } catch (Throwable var6) {
               if (audioStream != null) {
                  try {
                     audioStream.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (audioStream != null) {
               audioStream.close();
            }
         } catch (IOException | LineUnavailableException | InterruptedException | UnsupportedAudioFileException var7) {
            var7.printStackTrace();
         }

      }));
   }
}
