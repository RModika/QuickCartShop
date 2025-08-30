package za.ac.cput.model;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("categoryId")
    private Long categoryId;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    // Constructors
    public Category() {}

    public Category(Long categoryId, String name, String description) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return name; // This will be displayed in Spinner/ListView
    }
}