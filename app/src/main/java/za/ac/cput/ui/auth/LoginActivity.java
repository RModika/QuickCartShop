package za.ac.cput.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.ac.cput.R;
import za.ac.cput.model.AdminLoginResponse;
import za.ac.cput.model.LoginResponse;
import za.ac.cput.model.User;
import za.ac.cput.model.UserAuth;
import za.ac.cput.services.ApiClient;
import za.ac.cput.services.AdminApiService;
import za.ac.cput.services.UsersApi;
import za.ac.cput.ui.home.HomeActivity;
import za.ac.cput.ui.product.AdminProductManagementActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private Button btnLogin;

    private UsersApi usersApi;
    private AdminApiService adminApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String role = prefs.getString("role", null);

        if (token != null && role != null) {
            if ("ADMIN".equalsIgnoreCase(role)) {
                startActivity(new Intent(this, AdminProductManagementActivity.class));
            } else {
                startActivity(new Intent(this, HomeActivity.class));
            }
            finish();
            return;
        }

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        TextView registerText = findViewById(R.id.textRegister);
        TextView forgotPasswordText = findViewById(R.id.textForgotPassword);

        usersApi = ApiClient.getClient().create(UsersApi.class);
        adminApi = ApiClient.getClient().create(AdminApiService.class);

        registerText.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
        forgotPasswordText.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));

        btnLogin.setOnClickListener(view -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            } else {
                performLogin(email, password);
            }
        });
    }

    private void performLogin(String email, String password) {
        UserAuth loginRequest = new UserAuth(email, password);

        usersApi.loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();

                    // Now fetch user details using token
                    UsersApi authorizedApi = ApiClient.getClientWithToken(token).create(UsersApi.class);
                    authorizedApi.getCurrentUser().enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> userResponse) {
                            if (userResponse.isSuccessful() && userResponse.body() != null) {
                                User user = userResponse.body();

                                // ✅ Save everything as before
                                SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                                editor.putLong("userId", user.getUserId());
                                editor.putString("firstName", user.getFirstName());
                                editor.putString("surname", user.getSurname());
                                editor.putString("email", user.getEmail());
                                editor.putString("password", password);
                                editor.putString("phoneNumber", user.getPhoneNumber());
                                editor.putString("token", token);
                                editor.putString("role", "USER");
                                editor.apply();

                                Toast.makeText(LoginActivity.this, "Welcome " + user.getFirstName(), Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.putExtra("firstName", user.getFirstName());
                                intent.putExtra("surname", user.getSurname());
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    // Fallback: try admin login
                    tryAdminLogin(loginRequest, password);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                tryAdminLogin(loginRequest, password);
            }
        });
    }

    private void tryAdminLogin(UserAuth loginRequest, String password) {
        Log.d("AdminLoginDebug", "Email: " + loginRequest.getEmail() + ", Password: [" + loginRequest.getPassword() + "]");
        adminApi.loginAdmin(loginRequest).enqueue(new Callback<AdminLoginResponse>() {
            @Override
            public void onResponse(Call<AdminLoginResponse> call, Response<AdminLoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AdminLoginResponse admin = response.body();

                    // ✅ Save admin values
                    // ✅ CORRECT: Use a separate "AdminPrefs" file
                    SharedPreferences.Editor editor = getSharedPreferences("AdminPrefs", MODE_PRIVATE).edit();
                    editor.putLong("adminId", admin.getUserId());
                    editor.putString("email", admin.getEmail());
                    editor.putString("role", admin.getRole());
                    editor.putString("admin_token", admin.getToken());  // different key name
                    editor.putString("password", password);
                    editor.apply();

                    Toast.makeText(LoginActivity.this, "Welcome ADMIN", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(LoginActivity.this, AdminProductManagementActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminLoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Login error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}


//package za.ac.cput.ui.auth;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.google.gson.Gson;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import za.ac.cput.R;
//import za.ac.cput.model.LoginResponse;
//import za.ac.cput.model.User;
//import za.ac.cput.model.UserAuth;
//import za.ac.cput.services.ApiClient;
//import za.ac.cput.services.UsersApi;
//import za.ac.cput.ui.home.HomeActivity;
//
//public class LoginActivity extends AppCompatActivity {
//
//    private EditText editEmail, editPassword;
//    private Button btnLogin;
//    private UsersApi usersApi;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
//        setContentView(R.layout.activity_main); // ✅ Make sure this layout exists
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        TextView registerText = findViewById(R.id.textRegister);
//        editEmail = findViewById(R.id.editEmail);
//        editPassword = findViewById(R.id.editPassword);
//        btnLogin = findViewById(R.id.btnLogin);
//
//        usersApi = ApiClient.getClientWithTokenFromPrefs(this).create(UsersApi.class);
//
//        registerText.setOnClickListener(v -> {
//            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
//            startActivity(intent);
//        });
//        TextView forgotPasswordText = findViewById(R.id.textForgotPassword);
//        forgotPasswordText.setOnClickListener(v -> {
//            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
//            startActivity(intent);
//        });
//
//        btnLogin.setOnClickListener(view -> {
//            String email = editEmail.getText().toString().trim();
//            String password = editPassword.getText().toString().trim();
//
//            if (email.isEmpty() || password.isEmpty()) {
//                Toast.makeText(LoginActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
//            } else {
//                performLogin(email, password);
//            }
//        });
//    }
//
//    private void performLogin(String email, String password) {
//        UserAuth loginRequest = new UserAuth(email, password);
//
//        Call<LoginResponse> call = usersApi.loginUser(loginRequest);
//        call.enqueue(new Callback<LoginResponse>() {
//            @Override
//            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    String token = response.body().getToken();
//
//                    // Use token to fetch user details
//                    UsersApi authorizedApi = ApiClient.getClientWithToken(token).create(UsersApi.class);
//                    authorizedApi.getCurrentUser().enqueue(new Callback<User>() {
//                        @Override
//                        public void onResponse(Call<User> call, Response<User> userResponse) {
//                            if (userResponse.isSuccessful() && userResponse.body() != null) {
//                                User user = userResponse.body();
//
//                                // ✅ Save user + token to SharedPreferences
//                                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//                                SharedPreferences.Editor editor = prefs.edit();
//                                editor.putLong("userId", user.getUserId());
//                                editor.putString("firstName", user.getFirstName());
//                                editor.putString("surname", user.getSurname());
//                                editor.putString("email", user.getEmail());
//                                editor.putString("password", password);  // Used for Basic Auth if needed
//                                editor.putString("phoneNumber", user.getPhoneNumber());
//                                editor.putString("token", token);
//                                editor.apply();
//
//                                Toast.makeText(LoginActivity.this, "Welcome " + user.getFirstName(), Toast.LENGTH_LONG).show();
//
//                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                                intent.putExtra("firstName", user.getFirstName());
//                                intent.putExtra("surname", user.getSurname());
//                                startActivity(intent);
//                                finish();
//                            } else {
//                                Toast.makeText(LoginActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<User> call, Throwable t) {
//                            Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    });
//                } else {
//                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginResponse> call, Throwable t) {
//                Toast.makeText(LoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//
//    }
//}
//
