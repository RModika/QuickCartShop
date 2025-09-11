package za.ac.cput.model;

import com.google.gson.annotations.SerializedName;

public class PaymentRequest {

    @SerializedName("orderId")
    private Long orderId;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    // Card fields
    @SerializedName("cardNumber")
    private String cardNumber;
    @SerializedName("expiryDate")
    private String expiryDate;
    @SerializedName("cvv")
    private String cvv;

    // PayPal fields
    @SerializedName("paypalEmail")
    private String paypalEmail;

    // Wallet fields
    @SerializedName("walletId")
    private String walletId;

    @SerializedName("amount")
    private double amount;

    public PaymentRequest(Long orderId, String paymentMethod, String cardNumber, String expiryDate, String cvv, String paypalEmail, String walletId, double amount) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.paypalEmail = paypalEmail;
        this.walletId = walletId;
        this.amount = amount;
    }

    // Getters and setters omitted for brevity
}
