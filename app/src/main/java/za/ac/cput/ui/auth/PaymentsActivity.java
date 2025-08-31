package za.ac.cput.ui.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import za.ac.cput.R;
import za.ac.cput.model.PaymentRequest;
import za.ac.cput.model.PaymentResponse;
import za.ac.cput.services.ApiClient;
import za.ac.cput.services.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentsActivity extends AppCompatActivity {

    LinearLayout cardFields, paypalFields, walletFields;
    Button btnCard, btnPayPal, btnWallet, btnPayNow;
    EditText etCardNumber, etExpiry, etCVV, etPaypalEmail, etWalletId;
    String selectedPaymentMethod = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        // Initialize views
        cardFields = findViewById(R.id.cardFields);
        paypalFields = findViewById(R.id.paypalFields);
        walletFields = findViewById(R.id.walletFields);

        btnCard = findViewById(R.id.creditCardPayment);
        btnPayPal = findViewById(R.id.paypalPayment);
        btnWallet = findViewById(R.id.walletPayment);
        btnPayNow = findViewById(R.id.payNowButton);

        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiry = findViewById(R.id.etExpiry);
        etCVV = findViewById(R.id.etCVV);
        etPaypalEmail = findViewById(R.id.etPaypalEmail);
        etWalletId = findViewById(R.id.etWalletId);

        // Payment method selection
        btnCard.setOnClickListener(v -> showPaymentFields("CARD"));
        btnPayPal.setOnClickListener(v -> showPaymentFields("PAYPAL"));
        btnWallet.setOnClickListener(v -> showPaymentFields("WALLET"));

        // Pay Now click
        btnPayNow.setOnClickListener(v -> processPayment());
    }

    private void showPaymentFields(String method) {
        selectedPaymentMethod = method;
        cardFields.setVisibility(method.equals("CARD") ? View.VISIBLE : View.GONE);
        paypalFields.setVisibility(method.equals("PAYPAL") ? View.VISIBLE : View.GONE);
        walletFields.setVisibility(method.equals("WALLET") ? View.VISIBLE : View.GONE);
    }

    private void processPayment() {
        double amount = 100.00; // Replace with actual amount
        Long orderId = 1L;      // Replace with actual order ID

        PaymentRequest request = null;

        switch (selectedPaymentMethod) {
            case "CARD":
                if (etCardNumber.getText().toString().isEmpty() ||
                        etExpiry.getText().toString().isEmpty() ||
                        etCVV.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Fill all card details", Toast.LENGTH_SHORT).show();
                    return;
                }
                request = new PaymentRequest(orderId, "CARD",
                        etCardNumber.getText().toString(),
                        etExpiry.getText().toString(),
                        etCVV.getText().toString(),
                        null, null, amount);
                break;

            case "PAYPAL":
                if (etPaypalEmail.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Enter PayPal email", Toast.LENGTH_SHORT).show();
                    return;
                }
                request = new PaymentRequest(orderId, "PAYPAL",
                        null, null, null,
                        etPaypalEmail.getText().toString(),
                        null, amount);
                break;

            case "WALLET":
                if (etWalletId.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Enter Wallet ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                request = new PaymentRequest(orderId, "WALLET",
                        null, null, null, null,
                        etWalletId.getText().toString(), amount);
                break;

            default:
                Toast.makeText(this, "Select a payment method", Toast.LENGTH_SHORT).show();
                return;
        }

        // Send request to backend
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.processPayment(request).enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(PaymentsActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PaymentsActivity.this, "Payment failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                Toast.makeText(PaymentsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
