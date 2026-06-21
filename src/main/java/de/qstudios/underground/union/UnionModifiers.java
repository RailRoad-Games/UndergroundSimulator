package de.qstudios.underground.union;

import java.util.ArrayList;
import java.util.List;

public class UnionModifiers {

    private List<User> users = new ArrayList<>();
    private String creator;
    private String password;
    private String cc;
    private List<String> bel = new ArrayList<>();
    private List<ChatMessage> chat = new ArrayList<>();

    public UnionModifiers(String creator, String password, String cc) {
        this.creator = creator;
        this.password = password;
        this.cc = cc;
    }

    public void addUser(User user) {
        users.add(user);
    }

    @Override
   public String toString() {
      return "UnionModifiers [users=" + users + ", creator=" + creator + ", password=" + password + ", cc=" + cc
            + ", bel=" + bel + ", chat=" + chat + "]";
   }

    public void sendMessage(User from, User to, String msg) {
        ChatMessage message = new ChatMessage(from, to, msg);
        chat.add(message);
        System.out.println(message);
    }

    public List<User> getUsers() {
        return users;
    }

    public String getCreator() {
        return creator;
    }

    public String getPassword() {
        return password;
    }

    public String getCc() {
        return cc;
    }

    public List<String> getBel() {
        return bel;
    }

    public List<ChatMessage> getChat() {
        return chat;
    }
}

