// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package org.rrgames.underground.usr;

public class User {
   private String username;
   private String passwd;
   private String realname;
   public String getRealname() {
      return realname;
   }

   public void setRealname(String realname) {
      this.realname = realname;
   }

   private static int counter = 0;

   public User(String username, String passwd, String realname) {
      this.username = username;
      this.passwd = passwd;
      this.realname= realname;
      ++counter;
   }

   public String getUsername() {
      return this.username;
   }

   public String getPasswd() {
      return this.passwd;
   }

   public static int getCounter() {
      return counter;
   }
   
}
