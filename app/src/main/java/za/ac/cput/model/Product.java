package za.ac.cput.model;

import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("productId")
    private Long productId;

    @SerializedName("productName")
    private String productName;

    @SerializedName("productDescription")
    private String productDescription;

    @SerializedName("productPrice")
    private Double productPrice;

    @SerializedName("stockAvailability")
    private String stockAvailability;

    @SerializedName("category")
    private Category category;

    public Product() {}

    public Product(Long productId, String productName, String productDescription,
                   Double productPrice, String stockAvailability, byte[] image, Category category) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.stockAvailability = stockAvailability;
        this.category = category;
    }

    // Getters and setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductDescription() { return productDescription; }
    public void setProductDescription(String productDescription) { this.productDescription = productDescription; }

    public Double getProductPrice() { return productPrice; }
    public void setProductPrice(Double productPrice) { this.productPrice = productPrice; }

    public String getStockAvailability() { return stockAvailability; }
    public void setStockAvailability(String stockAvailability) { this.stockAvailability = stockAvailability; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    // === Helper methods for stock states ===
    public boolean isAvailable() {
        return "AVAILABLE".equalsIgnoreCase(stockAvailability);
    }

    public boolean isLowStock() {
        return "LOW_STOCK".equalsIgnoreCase(stockAvailability);
    }

    public boolean isOutOfStock() {
        return "OUT_OF_STOCK".equalsIgnoreCase(stockAvailability);
    }

    @Override
    public String toString() {
        return productName + " - R" + productPrice;
    }
}
