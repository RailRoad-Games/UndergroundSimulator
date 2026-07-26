// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package org.rrgames.underground.dev;

import java.util.Random;

/** @deprecated */
@Deprecated
public class AktienkurseKarrieremodus {
   private static Random rng = new Random();

   /** @deprecated */
   @Deprecated
   public static int updateaktienkurs() {
      int aktakt = 1;
      int zufallszahl = rng.nextInt(101);
      if (zufallszahl > 50) {
         ++aktakt;
      } else if (zufallszahl < 50) {
         --aktakt;
      }

      return aktakt;
   }

   /** @deprecated */
   @Deprecated
   public AktienkurseKarrieremodus() {
   }
}
