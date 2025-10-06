//package za.ac.cput.model;
//
//
//import com.google.gson.annotations.SerializedName;
//
//public class CustomerOrder {
//
//    @SerializedName("orderId")
//    private Long orderId;
//
//    @SerializedName("userId")
//    private Long userId;
//
//    @SerializedName("totalAmount")
//    private double totalAmount;
//
//    @SerializedName("status")
//    private String status;
//
//    @SerializedName("paymentMethod")
//    private String paymentMethod;
//
//    @SerializedName("deliveryAddress")
//    private String deliveryAddress;
//
//    @SerializedName("orderDate")
//    private String orderDate; // keep as String for easy parsing in RecyclerView
//
//    @SerializedName("deliveryDate")
//    private String deliveryDate;
//
//    // Getters
//    public Long getOrderId() { return orderId; }
//    public Long getUserId() { return userId; }
//    public double getTotalAmount() { return totalAmount; }
//    public String getStatus() { return status; }
//    public String getPaymentMethod() { return paymentMethod; }
//    public String getDeliveryAddress() { return deliveryAddress; }
//    public String getOrderDate() { return orderDate; }
//    public String getDeliveryDate() { return deliveryDate; }
//}


package za.ac.cput.model;

import com.google.gson.annotations.SerializedName;

public class CustomerOrder {

    @SerializedName("orderId")
    private Long orderId;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("status")
    private String status;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("deliveryAddress")
    private String deliveryAddress;

    @SerializedName("orderDate")
    private String orderDate; // keep as String for easy parsing in RecyclerView

    @SerializedName("deliveryDate")
    private String deliveryDate;

    private CustomerOrder(Builder builder) {
        this.orderId = builder.orderId;
        this.userId = builder.userId;
        this.totalAmount = builder.totalAmount;
        this.status = builder.status;
        this.paymentMethod = builder.paymentMethod;
        this.deliveryAddress = builder.deliveryAddress;
        this.orderDate = builder.orderDate;
        this.deliveryDate = builder.deliveryDate;
    }

    // Getters
    public Long getOrderId() { return orderId; }
    public Long getUserId() { return userId; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getOrderDate() { return orderDate; }
    public String getDeliveryDate() { return deliveryDate; }

    // Builder
    public static class Builder {
        private Long orderId;
        private Long userId;
        private double totalAmount;
        private String status;
        private String paymentMethod;
        private String deliveryAddress;
        private String orderDate;
        private String deliveryDate;

        public Builder setOrderId(Long orderId) { this.orderId = orderId; return this; }
        public Builder setUserId(Long userId) { this.userId = userId; return this; }
        public Builder setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder setStatus(String status) { this.status = status; return this; }
        public Builder setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public Builder setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; return this; }
        public Builder setOrderDate(String orderDate) { this.orderDate = orderDate; return this; }
        public Builder setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; return this; }

        public CustomerOrder build() {
            return new CustomerOrder(this);
        }
    }
}
