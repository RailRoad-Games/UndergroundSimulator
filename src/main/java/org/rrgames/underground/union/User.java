// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package org.rrgames.underground.union;

import java.util.UUID;

public class User {
   public static UUID uuid;
   public static org.rrgames.underground.usr.User user = new org.rrgames.underground.usr.User("", "", "");
   public static String name;

   @SuppressWarnings("static-access")
public User(org.rrgames.underground.usr.User user) {
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
