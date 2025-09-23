package za.ac.cput.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import za.ac.cput.R;
import za.ac.cput.model.CustomerOrder;
import za.ac.cput.model.Payment;
import za.ac.cput.model.PaymentDetails;
import za.ac.cput.services.OrderApiService;
import za.ac.cput.services.PaymentApiService;

public class PaymentsActivity extends AppCompatActivity {

    private Spinner spinnerMethod;
    private EditText edtOrderId, edtCardDigits, edtBankName;
    private TextView txtResult, txtTotalAmount;
    private Button btnPay;

    private PaymentApiService paymentApiService;
    private OrderApiService orderApiService;

    private double totalAmount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        Toolbar toolbar = findViewById(R.id.paymentToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button
            getSupportActionBar().setTitle("Payment");
        }

        toolbar.setNavigationOnClickListener(v -> {
            // Navigate back to CartActivity
            Intent intent = new Intent(PaymentsActivity.this, CartActivity.class);
            startActivity(intent);
            finish();
        });

        spinnerMethod = findViewById(R.id.spinnerPaymentMethod);
        edtOrderId = findViewById(R.id.edtOrderId);
        edtCardDigits = findViewById(R.id.edtCardDigits);
        edtBankName = findViewById(R.id.edtBankName);
        txtResult = findViewById(R.id.txtResult);
        txtTotalAmount = findViewById(R.id.txtTotalAmount);
        btnPay = findViewById(R.id.btnPay);

        String[] methods = {"Credit Card", "Debit Card"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, methods);
        spinnerMethod.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        paymentApiService = retrofit.create(PaymentApiService.class);
        orderApiService = retrofit.create(OrderApiService.class);

        fetchOrderDetails();

        btnPay.setOnClickListener(v -> processPayment());
    }

    private void fetchOrderDetails() {
        orderApiService.getOrders().enqueue(new Callback<List<CustomerOrder>>() {
            @Override
            public void onResponse(Call<List<CustomerOrder>> call, Response<List<CustomerOrder>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    CustomerOrder order = response.body().get(0);
                    edtOrderId.setText(String.valueOf(order.getOrderId()));
                    totalAmount = order.getTotalAmount();
                    txtTotalAmount.setText("Total Amount: R" + String.format("%.2f", totalAmount));
                } else {
                    Toast.makeText(PaymentsActivity.this, "Failed to fetch order details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CustomerOrder>> call, Throwable t) {
                Toast.makeText(PaymentsActivity.this, "Failed to connect to backend", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processPayment() {
        String orderIdStr = edtOrderId.getText().toString().trim();
        String method = spinnerMethod.getSelectedItem().toString();
        String cardDigits = edtCardDigits.getText().toString().trim();
        String bank = edtBankName.getText().toString().trim();

        if (orderIdStr.isEmpty()) {
            Toast.makeText(this, "Order ID required", Toast.LENGTH_SHORT).show();
            return;
        }

        long orderId = Long.parseLong(orderIdStr);

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setPaymentMethod(method);
        payment.setPaymentDate(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        payment.setPaymentStatus("PENDING");

        paymentApiService.createPayment(payment).enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Payment createdPayment = response.body();
                    PaymentDetails details = new PaymentDetails();
                    details.setPaymentId(createdPayment.getPaymentId());
                    details.setCardLast4Digits(cardDigits);
                    details.setBankName(bank);
                    details.setTransactionId("TXN" + System.currentTimeMillis());
                    details.setGatewayResponse("SUCCESS");

                    paymentApiService.createPaymentDetails(details).enqueue(new Callback<PaymentDetails>() {
                        @Override
                        public void onResponse(Call<PaymentDetails> call, Response<PaymentDetails> resp) {
                            if (resp.isSuccessful() && resp.body() != null) {
                                txtResult.setText("Payment Successful!\nPayment ID: " +
                                        createdPayment.getPaymentId() + "\nTransaction ID: " +
                                        resp.body().getTransactionId());
                            } else {
                                txtResult.setText("Payment created, but details failed.");
                            }
                        }

                        @Override
                        public void onFailure(Call<PaymentDetails> call, Throwable t) {
                            txtResult.setText("Error saving details: " + t.getMessage());
                        }
                    });

                } else {
                    txtResult.setText("Payment creation failed.");
                }
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {
                txtResult.setText("Error: " + t.getMessage());
            }
        });
    }
}
