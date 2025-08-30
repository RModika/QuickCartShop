package za.ac.cput.ui.auth;
//
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import za.ac.cput.R;

import java.io.IOException;

public class SignUpActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private TextView btnGoToLogin;
    private ProgressBar progressBar;

    private OkHttpClient client = new OkHttpClient();

    // TODO: Replace with your actual backend URL for registration API
    private static final String REGISTER_URL = "http://192.168.10.125:8080/mobileApp/users/create";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etName = findViewById(R.id.editFullName);
        etEmail = findViewById(R.id.editEmail);
        etPassword = findViewById(R.id.editPassword);
        etConfirmPassword = findViewById(R.id.editConfirmPassword);
        btnSignUp = findViewById(R.id.btnRegister);
        btnGoToLogin = findViewById(R.id.textBackToLogin);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);

        btnSignUp.setOnClickListener(v -> registerUser());

        btnGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Build POST request to backend
        FormBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(REGISTER_URL)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Back to UI thread to update UI
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));

                if (response.isSuccessful()) {
                    String responseBody = "";
                    try {
                        responseBody = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // You can parse your response if JSON, or just check success string
                    runOnUiThread(() -> {
                        Toast.makeText(SignUpActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        finish();
                    });
                } else {
                    final String errorMsg = response.message();
                    runOnUiThread(() -> Toast.makeText(SignUpActivity.this, "Registration failed: " + errorMsg, Toast.LENGTH_LONG).show());
                }
            }
        });
    }

}