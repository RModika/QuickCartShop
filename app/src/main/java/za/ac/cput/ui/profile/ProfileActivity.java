package za.ac.cput.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import za.ac.cput.R;
import za.ac.cput.ui.auth.LoginActivity;
import za.ac.cput.ui.home.HomeActivity;
import za.ac.cput.ui.home.OrdersActivity;
import za.ac.cput.ui.auth.CartActivity;

public class ProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnUpdate;
    private ProgressBar progressBar;

    private static final String UPDATE_URL = "http://10.0.2.2:8080/mobileApp/users/update";
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etName = findViewById(R.id.editFullName);
        etEmail = findViewById(R.id.editEmail);
        etPassword = findViewById(R.id.editPassword);
        btnUpdate = findViewById(R.id.btnUpdate);
        progressBar = findViewById(R.id.progressBar);

        preloadUserInfo();

        btnUpdate.setOnClickListener(v -> updateProfile());

        setBottomNavigationClicks();
    }

    private void preloadUserInfo() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String name = prefs.getString("name", "");
        String email = prefs.getString("email", "");
        String password = prefs.getString("password", "");

        etName.setText(name);
        etEmail.setText(email);
        etPassword.setText(password);
    }

    private void updateProfile() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        Long userId = prefs.getLong("userId", -1L);
        String phoneNumber = prefs.getString("phoneNumber", "");

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (userId == -1 || TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "User not logged in. Please login again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

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

        progressBar.setVisibility(View.VISIBLE);

        String json = "{"
                + "\"userId\":" + userId + ","
                + "\"name\":\"" + name + "\","
                + "\"email\":\"" + email + "\","
                + "\"password\":\"" + password + "\","
                + "\"phoneNumber\":\"" + phoneNumber + "\""
                + "}";

        Request request = new Request.Builder()
                .url(UPDATE_URL)
                .put(okhttp3.RequestBody.create(
                        okhttp3.MediaType.parse("application/json; charset=utf-8"),
                        json
                ))
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));

                if (response.isSuccessful()) {
                    // Save updated values
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("name", name);
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.apply();

                    runOnUiThread(() -> {
                        Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ProfileActivity.this, HomeActivity.class)); // or LoginActivity if that’s your flow
                        finish();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(ProfileActivity.this, "Update failed: " + response.message(), Toast.LENGTH_LONG).show()
                    );
                }
            }
        });
    }



    private void setBottomNavigationClicks() {
        ImageView homeIcon = findViewById(R.id.homeIcon);
        ImageView profileIcon = findViewById(R.id.profileIcon);
        ImageView ordersIcon = findViewById(R.id.ordersIcon);
        ImageView cartIcon = findViewById(R.id.cartIcon);

        homeIcon.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            finish();
        });

        profileIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show();
        });

        ordersIcon.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, OrdersActivity.class));
            finish();
        });

        cartIcon.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, CartActivity.class));
            finish();
        });
    }
}
