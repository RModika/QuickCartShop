package za.ac.cput.model;

import com.google.gson.annotations.SerializedName;

public class PaymentDetails {
    @SerializedName("paymentDetailsId")
    private Long paymentDetailsId;

    @SerializedName("paymentId")
    private Long paymentId;

    @SerializedName("transactionId")
    private String transactionId;

    @SerializedName("cardLast4Digits")
    private String cardLast4Digits;

    @SerializedName("bankName")
    private String bankName;

    @SerializedName("receiptUrl")
    private String receiptUrl;

    @SerializedName("gatewayResponse")
    private String gatewayResponse;

    // Getters and setters
    public Long getPaymentDetailsId() { return paymentDetailsId; }
    public void setPaymentDetailsId(Long paymentDetailsId) { this.paymentDetailsId = paymentDetailsId; }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public String getCardLast4Digits() { return cardLast4Digits; }
    public void setCardLast4Digits(String cardLast4Digits) { this.cardLast4Digits = cardLast4Digits; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getGatewayResponse() { return gatewayResponse; }
    public void setGatewayResponse(String gatewayResponse) { this.gatewayResponse = gatewayResponse; }
}

