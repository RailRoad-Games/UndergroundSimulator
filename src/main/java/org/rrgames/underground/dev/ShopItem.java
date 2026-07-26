package org.rrgames.underground.dev;

public class ShopItem {
    private String id;
    private String name;
    private  double price;
    private  String action;
    private  String value;
    private  int amount;

    public ShopItem(String id, String name, double price, String action, String value, int amount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.action = action;
        this.value = value;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getAction() {
        return action;
    }

    public String getValue() {
        return value;
    }

    public int getAmount() {
        return amount;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

}
