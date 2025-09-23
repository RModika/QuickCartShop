package za.ac.cput.model;

import java.time.LocalDate;
import java.util.List;

public class Cart {
    private Long cartId;
    private Long userId;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String status;

    // 🔹 Add these fields
    private List<CartItem> items;
    private double totalAmount;

    // ===== Getters =====
    public Long getCartId() { return cartId; }
    public Long getUserId() { return userId; }
    public LocalDate getCreatedAt() { return createdAt; }
    public LocalDate getUpdatedAt() { return updatedAt; }
    public String getStatus() { return status; }
    public List<CartItem> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }

    // ===== Setters (needed for Retrofit/Gson) =====
    public void setCartId(Long cartId) { this.cartId = cartId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }
    public void setStatus(String status) { this.status = status; }
    public void setItems(List<CartItem> items) { this.items = items; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    // ===== Builder Pattern =====
    public static class Builder {
        private Long cartId;
        private Long userId;
        private LocalDate createdAt;
        private LocalDate updatedAt;
        private String status;
        private List<CartItem> items;
        private double totalAmount;

        public Builder setCartId(Long cartId) {
            this.cartId = cartId;
            return this;
        }

        public Builder setUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder setCreatedAt(LocalDate createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setUpdatedAt(LocalDate updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setItems(List<CartItem> items) {
            this.items = items;
            return this;
        }

        public Builder setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Cart build() {
            Cart cart = new Cart();
            cart.cartId = this.cartId;
            cart.userId = this.userId;
            cart.createdAt = this.createdAt;
            cart.updatedAt = this.updatedAt;
            cart.status = this.status;
            cart.items = this.items;
            cart.totalAmount = this.totalAmount;
            return cart;
        }
    }
}
