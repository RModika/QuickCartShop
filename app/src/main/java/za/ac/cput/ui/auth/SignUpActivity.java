package za.ac.cput.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.ac.cput.R;
import za.ac.cput.model.User;
import za.ac.cput.services.ApiClient;
import za.ac.cput.services.UsersApi;

public class SignUpActivity extends AppCompatActivity {

    private EditText etFirstName, etSurname, etEmail, etPassword, etConfirmPassword, etPhoneNumber;
    private Button btnSignUp;
    private TextView btnGoToLogin;
    private ProgressBar progressBar;
    private UsersApi usersApi;

    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etFirstName = findViewById(R.id.editFirstName);
        etSurname = findViewById(R.id.editSurname);
        etEmail = findViewById(R.id.editEmail);
        etPassword = findViewById(R.id.editPassword);
        etConfirmPassword = findViewById(R.id.editConfirmPassword);
        etPhoneNumber = findViewById(R.id.editPhoneNumber);
        btnSignUp = findViewById(R.id.btnRegister);
        btnGoToLogin = findViewById(R.id.textBackToLogin);
        progressBar = findViewById(R.id.progressBar);

        usersApi = ApiClient.getUsersApi(this);

        progressBar.setVisibility(View.GONE);

        btnSignUp.setOnClickListener(v -> registerUser());

        btnGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String firstName = etFirstName.getText().toString().trim();
        String surname = etSurname.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        // === Validations ===
        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("First name is required");
            return;
        }

        if (TextUtils.isEmpty(surname)) {
            etSurname.setError("Surname is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
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

        if (!TextUtils.isEmpty(phoneNumber) && phoneNumber.length() < 10) {
            etPhoneNumber.setError("Enter a valid phone number");
            return;
        }

        // === Prepare UI ===
        btnSignUp.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        // === Create User Object ===
        User user = new User();
        user.setFirstName(firstName);
        user.setSurname(surname);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhoneNumber(phoneNumber);

        // === API Call ===
        Call<User> call = usersApi.registerUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressBar.setVisibility(View.GONE);
                btnSignUp.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SignUpActivity.this,
                            "Registration successful! Please check your email to verify your account.",
                            Toast.LENGTH_LONG).show();

                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    finish();
                } else {
                    String errorBody = "Registration failed. Please try again.";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                    }

                    Toast.makeText(SignUpActivity.this,
                            "Registration failed: " + response.code() + "\n" + errorBody,
                            Toast.LENGTH_LONG).show();

                    Log.e(TAG, "Registration failed: " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSignUp.setEnabled(true);
                Toast.makeText(SignUpActivity.this,
                        "Registration error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();

                Log.e(TAG, "Registration error", t);
            }
        });
    }
}

//package za.ac.cput.ui.auth;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import za.ac.cput.R;
//import za.ac.cput.model.User;
//import za.ac.cput.services.ApiClient;
//import za.ac.cput.services.UsersApi;
//
//public class SignUpActivity extends AppCompatActivity {
//
//    private EditText etFirstName, etSurname, etEmail, etPassword, etConfirmPassword, etPhoneNumber;
//    private Button btnSignUp;
//    private TextView btnGoToLogin;
//    private ProgressBar progressBar;
//    private UsersApi usersApi;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sign_up);
//
//        etFirstName = findViewById(R.id.editFirstName);
//        etSurname = findViewById(R.id.editSurname);
//        etEmail = findViewById(R.id.editEmail);
//        etPassword = findViewById(R.id.editPassword);
//        etConfirmPassword = findViewById(R.id.editConfirmPassword);
//        etPhoneNumber = findViewById(R.id.editPhoneNumber);
//        btnSignUp = findViewById(R.id.btnRegister);
//        btnGoToLogin = findViewById(R.id.textBackToLogin);
//        progressBar = findViewById(R.id.progressBar);
//
//        usersApi = ApiClient.getUsersApi(this);
//
//        progressBar.setVisibility(View.GONE);
//
//        btnSignUp.setOnClickListener(v -> registerUser());
//
//        btnGoToLogin.setOnClickListener(v -> {
//            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
//            finish();
//        });
//    }
//
//    private void registerUser() {
//        String firstName = etFirstName.getText().toString().trim();
//        String surname = etSurname.getText().toString().trim();
//        String email = etEmail.getText().toString().trim();
//        String password = etPassword.getText().toString().trim();
//        String confirmPassword = etConfirmPassword.getText().toString().trim();
//        String phoneNumber = etPhoneNumber.getText().toString().trim();
//
//        if (TextUtils.isEmpty(firstName)) {
//            etFirstName.setError("First name is required");
//            return;
//        }
//        if (TextUtils.isEmpty(surname)) {
//            etSurname.setError("Surname is required");
//            return;
//        }
//        if (TextUtils.isEmpty(email)) {
//            etEmail.setError("Email is required");
//            return;
//        }
//        if (TextUtils.isEmpty(password)) {
//            etPassword.setError("Password is required");
//            return;
//        }
//        if (password.length() < 6) {
//            etPassword.setError("Password must be at least 6 characters");
//            return;
//        }
//        if (!password.equals(confirmPassword)) {
//            etConfirmPassword.setError("Passwords do not match");
//            return;
//        }
//
//        progressBar.setVisibility(View.VISIBLE);
//
//        User user = new User();
//        user.setFirstName(firstName);
//        user.setSurname(surname);
//        user.setEmail(email);
//        user.setPassword(password);
//        user.setPhoneNumber(phoneNumber);
//
//        Call<User> call = usersApi.registerUser(user);
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                progressBar.setVisibility(View.GONE);
//
//                if (response.isSuccessful() && response.body() != null) {
//                    Toast.makeText(SignUpActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
//                    finish();
//                } else {
//                    String errorBody = "";
//                    try {
//                        errorBody = response.errorBody().string();
//                    } catch (Exception e) {
//                        errorBody = "Unable to parse error: " + e.getMessage();
//                    }
//
//                    Toast.makeText(SignUpActivity.this,
//                            "Registration failed: " + response.code() + "\n" + errorBody,
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
//                Toast.makeText(SignUpActivity.this, "Registration failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//}
