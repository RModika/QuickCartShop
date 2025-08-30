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

    // Getters
    public Long getOrderId() { return orderId; }
    public Long getUserId() { return userId; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getOrderDate() { return orderDate; }
    public String getDeliveryDate() { return deliveryDate; }
}
