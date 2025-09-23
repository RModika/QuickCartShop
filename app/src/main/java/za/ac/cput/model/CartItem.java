package za.ac.cput.model;

public class CartItem {

    private Long productId;
    private String productName;
    private String productImageUrl;
    private int quantity;
    private double price;
    private double totalPrice;

    private CartItem(Builder builder) {
        this.productId = builder.productId;
        this.productName = builder.productName;
        this.productImageUrl = builder.productImageUrl;
        this.quantity = builder.quantity;
        this.price = builder.price;
        this.totalPrice = builder.totalPrice;
    }

    // Add this setter
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    // Getters
    public double getTotalPrice() {
        return totalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public Long getProductId() {
        return productId;
    }

    // Builder class remains the same
    public static class Builder {
        private Long productId;
        private String productName;
        private String productImageUrl;
        private int quantity;
        private double price;
        private double totalPrice;

        public Builder setProductId(Long productId) { this.productId = productId; return this; }
        public Builder setProductName(String productName) { this.productName = productName; return this; }
        public Builder setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; return this; }
        public Builder setQuantity(int quantity) { this.quantity = quantity; return this; }
        public Builder setPrice(double price) { this.price = price; return this; }
        public Builder setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; return this; }

        public CartItem build() { return new CartItem(this); }
    }
}