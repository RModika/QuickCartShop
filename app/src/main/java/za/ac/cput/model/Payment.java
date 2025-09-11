package za.ac.cput.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class Payment {
    @SerializedName("paymentId")
    private Long paymentId;

    @SerializedName("orderId")
    private Long orderId;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("paymentDate")
    private String paymentDate; // send as ISO string

    @SerializedName("paymentStatus")
    private String paymentStatus;

    // Getters and setters
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
