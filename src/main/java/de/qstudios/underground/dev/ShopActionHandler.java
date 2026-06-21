package de.qstudios.underground.dev;

public interface ShopActionHandler {
    boolean supports(String action);
    void execute(ShopItem item) throws Exception;
}
