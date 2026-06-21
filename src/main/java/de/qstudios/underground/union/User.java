// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package de.qstudios.underground.union;

import java.util.UUID;

public class User {
   public static UUID uuid;
   public static de.qstudios.underground.usr.User user = new de.qstudios.underground.usr.User("", "", "");
   public static String name;

   public User(de.qstudios.underground.usr.User user) {
      this.user=user;
      uuid = UUID.randomUUID();
      name = user.getRealname();
   }

   static {
      name = user.getUsername();
   }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
