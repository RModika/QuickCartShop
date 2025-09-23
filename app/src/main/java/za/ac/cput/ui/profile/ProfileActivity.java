package za.ac.cput.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.ac.cput.R;
import za.ac.cput.model.Address;
import za.ac.cput.model.AddressDTO;
import za.ac.cput.model.User;
import za.ac.cput.services.AddressApiService;
import za.ac.cput.services.ApiClient;
import za.ac.cput.services.UsersApi;
import za.ac.cput.ui.auth.CartActivity;
import za.ac.cput.ui.auth.LoginActivity;
import za.ac.cput.ui.home.HomeActivity;
import za.ac.cput.ui.home.OrdersActivity;

public class ProfileActivity extends AppCompatActivity {

    private EditText etFirstName, etSurname, etEmail;
    private EditText etOldPassword, etNewPassword, etConfirmPassword;

    private Button btnEditProfile, btnUpdateProfile;
    private ProgressBar progressBar;
    private SharedPreferences prefs;
    private LinearLayout addressesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        addressesContainer = findViewById(R.id.addressesContainer);
        etFirstName = findViewById(R.id.editFirstName);
        etSurname = findViewById(R.id.editSurname);
        etEmail = findViewById(R.id.editEmail);
        etOldPassword = findViewById(R.id.editOldPassword);
        etNewPassword = findViewById(R.id.editNewPassword);
        etConfirmPassword = findViewById(R.id.editConfirmPassword);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        progressBar = findViewById(R.id.progressBar);

        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Load user info and addresses
        preloadUserInfo();
        loadUserAddresses();

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

        Button btnAddAddress = findViewById(R.id.btnAddAddress);
        btnAddAddress.setOnClickListener(v -> showAddAddressDialog());
    }

    private void setEditingEnabled(boolean enabled) {
        etFirstName.setEnabled(enabled);
        etSurname.setEnabled(enabled);
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

        UsersApi usersApi = ApiClient.getUsersApi(this);

        Call<User> call = usersApi.getUserById(userId);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    etFirstName.setText(user.getFirstName() != null ? user.getFirstName() : "");
                    etSurname.setText(user.getSurname() != null ? user.getSurname() : "");
                    etEmail.setText(user.getEmail() != null ? user.getEmail() : "");

                    SharedPreferences.Editor editor = prefs.edit();
                    if (user.getFirstName() != null) editor.putString("firstName", user.getFirstName());
                    if (user.getSurname() != null) editor.putString("surname", user.getSurname());
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

        String firstNameInput = etFirstName.getText().toString().trim();
        String surnameInput = etSurname.getText().toString().trim();
        String emailInput = etEmail.getText().toString().trim();

        String oldPasswordInput = etOldPassword.getText().toString().trim();
        String newPasswordInput = etNewPassword.getText().toString().trim();
        String confirmPasswordInput = etConfirmPassword.getText().toString().trim();

        if (userId == -1 || TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(ProfileActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(firstNameInput) || TextUtils.isEmpty(surnameInput) || TextUtils.isEmpty(emailInput)) {
            Toast.makeText(ProfileActivity.this, "First name, surname, and email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

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
        updatedUser.setFirstName(firstNameInput);
        updatedUser.setSurname(surnameInput);
        updatedUser.setEmail(emailInput);
        updatedUser.setPhoneNumber(phoneNumber);
        updatedUser.setPassword(passwordToSave);

        UsersApi usersApi = ApiClient.getUsersApi(this);

        Call<User> call = usersApi.updateUser(updatedUser);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    etFirstName.setText(user.getFirstName());
                    etSurname.setText(user.getSurname());
                    etEmail.setText(user.getEmail());

                    etOldPassword.setText("");
                    etNewPassword.setText("");
                    etConfirmPassword.setText("");

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("firstName", user.getFirstName());
                    editor.putString("surname", user.getSurname());
                    editor.putString("email", user.getEmail());
                    editor.putString("phoneNumber", user.getPhoneNumber());
                    editor.putString("password", passwordToSave);
                    editor.apply();

                    Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

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

    // Load addresses linked to the user WITHOUT adapter usage
    private void loadUserAddresses() {
        Long userId = prefs.getLong("userId", -1L);
        if (userId == -1L) return;

        AddressApiService addressApiService = ApiClient.getAddressApiService(this);

        Call<List<Address>> call = addressApiService.getUserAddresses(userId);
        call.enqueue(new Callback<List<Address>>() {
            @Override
            public void onResponse(Call<List<Address>> call, Response<List<Address>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Address> addressList = response.body();

                    // Clear any existing views
                    addressesContainer.removeAllViews();

                    for (Address address : addressList) {
                        // Create a LinearLayout for each address
                        LinearLayout addressLayout = new LinearLayout(ProfileActivity.this);
                        addressLayout.setOrientation(LinearLayout.VERTICAL);
                        addressLayout.setPadding(24, 24, 24, 24);
                        addressLayout.setBackgroundResource(android.R.drawable.dialog_holo_light_frame); // or any bg you want

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, 0, 0, 16);
                        addressLayout.setLayoutParams(layoutParams);
                        addressLayout.setClickable(true);
                        addressLayout.setFocusable(true);

                        // Street TextView
                        TextView tvStreet = new TextView(ProfileActivity.this);
                        tvStreet.setTextSize(16);
                        tvStreet.setTypeface(null, android.graphics.Typeface.BOLD);

                        String street = (address.getStreetNumber() != null ? address.getStreetNumber() + " " : "") +
                                (address.getStreetName() != null ? address.getStreetName() : "");
                        tvStreet.setText(street);

                        // City, Province, PostalCode TextView
                        TextView tvCityProvincePostal = new TextView(ProfileActivity.this);
                        tvCityProvincePostal.setTextSize(14);
                        tvCityProvincePostal.setPadding(0, 8, 0, 0);

                        String cityProvincePostal = (address.getCity() != null ? address.getCity() + ", " : "") +
                                (address.getProvince() != null ? address.getProvince() + " " : "") +
                                (address.getPostalCode() != null ? address.getPostalCode() : "");
                        tvCityProvincePostal.setText(cityProvincePostal);

                        // Country TextView
                        TextView tvCountry = new TextView(ProfileActivity.this);
                        tvCountry.setTextSize(14);
                        tvCountry.setPadding(0, 4, 0, 0);
                        tvCountry.setText(address.getCountry() != null ? address.getCountry() : "");

                        // Add TextViews to addressLayout
                        addressLayout.addView(tvStreet);
                        addressLayout.addView(tvCityProvincePostal);
                        addressLayout.addView(tvCountry);

                        // On click listener to open edit dialog
                        addressLayout.setOnClickListener(v -> showEditAddressDialog(address));

                        // Add this address layout to the container
                        addressesContainer.addView(addressLayout);
                    }

                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to load addresses", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Address>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error loading addresses: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- Show dialog to edit an address ---
    private void showEditAddressDialog(Address address) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Address");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_address, null);

        EditText etStreet = view.findViewById(R.id.etStreet);
        EditText etCity = view.findViewById(R.id.etCity);
        EditText etProvince = view.findViewById(R.id.etProvince);
        EditText etPostalCode = view.findViewById(R.id.etPostalCode);
        EditText etCountry = view.findViewById(R.id.etCountry);

        etStreet.setText(address.getStreetNumber() + " " + address.getStreetName());
        etCity.setText(address.getCity());
        etProvince.setText(address.getProvince());
        etPostalCode.setText(address.getPostalCode());
        etCountry.setText(address.getCountry());

        builder.setView(view);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String streetInput = etStreet.getText().toString().trim();
            String city = etCity.getText().toString().trim();
            String province = etProvince.getText().toString().trim();
            String postalCode = etPostalCode.getText().toString().trim();
            String country = etCountry.getText().toString().trim();

            if (streetInput.isEmpty() || city.isEmpty() || province.isEmpty() || postalCode.isEmpty() || country.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] parts = streetInput.split("\\s+", 2);
            String streetNumber = parts.length == 2 ? parts[0] : "";
            String streetName = parts.length == 2 ? parts[1] : parts[0];

            Long userId = prefs.getLong("userId", -1L);
            address.setUserId(userId); // ✅ Set userId

            address.setStreetNumber(streetNumber);
            address.setStreetName(streetName);
            address.setCity(city);
            address.setProvince(province);
            address.setPostalCode(postalCode);
            address.setCountry(country);
            updateAddressOnServer(address);

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    // --- Show dialog to add a new address ---
    private void showAddAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Address");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_address, null);

        EditText etStreet = view.findViewById(R.id.etStreet);
        EditText etCity = view.findViewById(R.id.etCity);
        EditText etProvince = view.findViewById(R.id.etProvince);
        EditText etPostalCode = view.findViewById(R.id.etPostalCode);
        EditText etCountry = view.findViewById(R.id.etCountry);

        builder.setView(view);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String streetInput = etStreet.getText().toString().trim();
            String city = etCity.getText().toString().trim();
            String province = etProvince.getText().toString().trim();
            String postalCode = etPostalCode.getText().toString().trim();
            String country = etCountry.getText().toString().trim();

            if (streetInput.isEmpty() || city.isEmpty() || province.isEmpty() || postalCode.isEmpty() || country.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] parts = streetInput.split("\\s+", 2);
            String streetNumber = parts.length == 2 ? parts[0] : "";
            String streetName = parts.length == 2 ? parts[1] : parts[0];

            Long userId = prefs.getLong("userId", -1L);
            if (userId == -1L) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            Address newAddress = new Address();  // <-- Use setters, no constructor with params
            newAddress.setUserId(userId);
            newAddress.setStreetNumber(streetNumber);
            newAddress.setStreetName(streetName);
            newAddress.setCity(city);
            newAddress.setProvince(province);
            newAddress.setPostalCode(postalCode);
            newAddress.setCountry(country);

            saveAddressToServer(newAddress);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void saveAddressToServer(Address newAddress) {
        progressBar.setVisibility(View.VISIBLE);

        AddressApiService addressApiService = ApiClient.getAddressApiService(this);

        Call<Address> call = addressApiService.createAddress(newAddress);

        call.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ProfileActivity.this, "Address saved successfully", Toast.LENGTH_SHORT).show();
                    loadUserAddresses();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to save address", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAddressOnServer(Address address) {
        progressBar.setVisibility(View.VISIBLE);

        AddressApiService addressApiService = ApiClient.getAddressApiService(this);
        AddressDTO dto = convertToDTO(address);


        Call<Address> call = addressApiService.updateAddress(dto);
        call.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ProfileActivity.this, "Address updated successfully", Toast.LENGTH_SHORT).show();
                    loadUserAddresses();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to update address", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private AddressDTO convertToDTO(Address address) {
        Log.d("UpdateAddress", "userId: " + address.getUserId());
        AddressDTO dto = new AddressDTO(
                address.getId(),
                address.getStreetNumber(),
                address.getStreetName(),
                address.getSuburb(),
                address.getCity(),
                address.getProvince(),
                address.getCountry(),
                address.getPostalCode(),
                address.getUserId() // 🚨 Ensure this is included
        );
        return dto;
    }

}
