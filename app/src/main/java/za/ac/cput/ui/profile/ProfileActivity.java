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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.ac.cput.R;
import za.ac.cput.model.User;
import za.ac.cput.services.ApiClient;
import za.ac.cput.services.UsersApi;
import za.ac.cput.ui.auth.CartActivity;
import za.ac.cput.ui.auth.LoginActivity;
import za.ac.cput.ui.home.HomeActivity;
import za.ac.cput.ui.home.OrdersActivity;

public class ProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail;
    private EditText etOldPassword, etNewPassword, etConfirmPassword;

    private Button btnEditProfile, btnUpdateProfile;
    private ProgressBar progressBar;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etName = findViewById(R.id.editFullName);
        etEmail = findViewById(R.id.editEmail);
        etOldPassword = findViewById(R.id.editOldPassword);
        etNewPassword = findViewById(R.id.editNewPassword);
        etConfirmPassword = findViewById(R.id.editConfirmPassword);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        progressBar = findViewById(R.id.progressBar);

        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        preloadUserInfo();

        // Start with editing disabled and password fields hidden
        setEditingEnabled(false);
        togglePasswordFields(false);
        btnUpdateProfile.setVisibility(View.GONE);

        btnEditProfile.setOnClickListener(v -> {
            setEditingEnabled(true);
            togglePasswordFields(true);
            btnEditProfile.setVisibility(View.GONE);
            btnUpdateProfile.setVisibility(View.VISIBLE);
        });

        btnUpdateProfile.setOnClickListener(v -> updateProfile());

        setBottomNavigationClicks();
    }

    private void setEditingEnabled(boolean enabled) {
        etName.setEnabled(enabled);
        etEmail.setEnabled(enabled);
    }

    private void togglePasswordFields(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        etOldPassword.setVisibility(visibility);
        etNewPassword.setVisibility(visibility);
        etConfirmPassword.setVisibility(visibility);

        if (!visible) {
            etOldPassword.setText("");
            etNewPassword.setText("");
            etConfirmPassword.setText("");
        }
    }

    private void preloadUserInfo() {
        Long userId = prefs.getLong("userId", -1);
        String email = prefs.getString("email", "");
        String password = prefs.getString("password", "");

        if (userId == -1 || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(ProfileActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        UsersApi usersApi = ApiClient.getClientWithBasicAuth(email, password).create(UsersApi.class);
        Call<User> call = usersApi.getUserById(userId);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    etName.setText(user.getName() != null ? user.getName() : "");
                    etEmail.setText(user.getEmail() != null ? user.getEmail() : "");

                    SharedPreferences.Editor editor = prefs.edit();
                    if (user.getName() != null) editor.putString("name", user.getName());
                    if (user.getEmail() != null) editor.putString("email", user.getEmail());
                    if (user.getPhoneNumber() != null) editor.putString("phoneNumber", user.getPhoneNumber());
                    editor.apply();

                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to load user info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        Long userId = prefs.getLong("userId", -1L);
        String phoneNumber = prefs.getString("phoneNumber", "");
        String storedEmail = prefs.getString("email", "");
        String storedPassword = prefs.getString("password", "");

        String nameInput = etName.getText().toString().trim();
        String emailInput = etEmail.getText().toString().trim();

        String oldPasswordInput = etOldPassword.getText().toString().trim();
        String newPasswordInput = etNewPassword.getText().toString().trim();
        String confirmPasswordInput = etConfirmPassword.getText().toString().trim();

        if (userId == -1 || TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(ProfileActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(nameInput) || TextUtils.isEmpty(emailInput)) {
            Toast.makeText(ProfileActivity.this, "Name and Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if password change is intended
        boolean changingPassword = !TextUtils.isEmpty(oldPasswordInput) ||
                !TextUtils.isEmpty(newPasswordInput) ||
                !TextUtils.isEmpty(confirmPasswordInput);

        if (changingPassword) {
            if (TextUtils.isEmpty(oldPasswordInput) ||
                    TextUtils.isEmpty(newPasswordInput) ||
                    TextUtils.isEmpty(confirmPasswordInput)) {
                Toast.makeText(this, "Fill in all password fields to change password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!oldPasswordInput.equals(storedPassword)) {
                Toast.makeText(this, "Old password is incorrect", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPasswordInput.equals(confirmPasswordInput)) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPasswordInput.length() < 6) {
                Toast.makeText(this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        final String passwordToSave = changingPassword ? newPasswordInput : storedPassword;

        progressBar.setVisibility(View.VISIBLE);

        User updatedUser = new User();
        updatedUser.setUserId(userId);
        updatedUser.setName(nameInput);
        updatedUser.setEmail(emailInput);
        updatedUser.setPhoneNumber(phoneNumber);
        updatedUser.setPassword(passwordToSave);

        UsersApi usersApi = ApiClient.getClientWithBasicAuth(storedEmail, storedPassword).create(UsersApi.class);
        Call<User> call = usersApi.updateUser(updatedUser);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    etName.setText(user.getName());
                    etEmail.setText(user.getEmail());

                    // Clear password fields
                    etOldPassword.setText("");
                    etNewPassword.setText("");
                    etConfirmPassword.setText("");

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("name", user.getName());
                    editor.putString("email", user.getEmail());
                    editor.putString("phoneNumber", user.getPhoneNumber());
                    editor.putString("password", passwordToSave);
                    editor.apply();

                    Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                    // After success, disable editing & hide password fields
                    setEditingEnabled(false);
                    togglePasswordFields(false);
                    btnEditProfile.setVisibility(View.VISIBLE);
                    btnUpdateProfile.setVisibility(View.GONE);

                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

        profileIcon.setOnClickListener(v ->
                Toast.makeText(ProfileActivity.this, "Profile clicked", Toast.LENGTH_SHORT).show());

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
