package za.ac.cput.util;

import java.util.ArrayList;
import java.util.List;

import za.ac.cput.model.CartItem;

public class LocalCart {
    private static final List<CartItem> cart = new ArrayList<>();

    public static void addItem(CartItem newItem) {
        for (CartItem item : cart) {
            if (item.getProductId().equals(newItem.getProductId())) {
                // ✅ Product already exists → just increase quantity
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                item.setTotalPrice(item.getQuantity() * item.getPrice());
                return; // don’t add new duplicate
            }
        }
        // ✅ Otherwise add as a new product
        cart.add(newItem);
    }

    public static List<CartItem> getCart() {
        return cart;
    }

    public static void clearCart() {
        cart.clear();
    }
}