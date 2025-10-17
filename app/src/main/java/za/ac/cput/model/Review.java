package za.ac.cput.model;

import com.google.gson.annotations.SerializedName;

public class Review {

    private Long reviewId;
    private Long categoryId;
    private String categoryNameIfOther;
    private String content;
    private String createdAt; // Use String if you're parsing datetime from JSON
    private User user;

    // Getters and Setters

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryNameIfOther() {
        return categoryNameIfOther;
    }

    public void setCategoryNameIfOther(String categoryNameIfOther) {
        this.categoryNameIfOther = categoryNameIfOther;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
