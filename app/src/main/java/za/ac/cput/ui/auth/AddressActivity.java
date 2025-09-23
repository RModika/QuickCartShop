package za.ac.cput.ui.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import za.ac.cput.R;
import za.ac.cput.model.Address;
import za.ac.cput.services.AddressService;

public class AddressActivity extends AppCompatActivity {

    private EditText streetNumber, streetName, suburb, city, province, country, postalCode;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        // Bind views
        streetNumber = findViewById(R.id.etStreetNumber);
        streetName = findViewById(R.id.etStreetName);
        suburb = findViewById(R.id.etSuburb);
        city = findViewById(R.id.etCity);
        province = findViewById(R.id.etProvince);
        country = findViewById(R.id.etCountry);
        postalCode = findViewById(R.id.etPostalCode);
        btnSave = findViewById(R.id.btnSaveAddress);

        // On save button click
        btnSave.setOnClickListener(view -> {
            // Retrieve userId from SharedPreferences
            SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            long userId = prefs.getLong("userId", -1);

            if (userId == -1) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            // Build address object
            Address address = new Address(
                    streetNumber.getText().toString(),
                    streetName.getText().toString(),
                    suburb.getText().toString(),
                    city.getText().toString(),
                    province.getText().toString(),
                    country.getText().toString(),
                    postalCode.getText().toString(),
                    userId // ✅ Important: pass user ID
            );

            // Save address via Retrofit
            AddressService.saveAddress(this, address, new AddressService.AddressCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(AddressActivity.this, "Address saved!", Toast.LENGTH_SHORT).show();
                    finish(); // or go to PaymentActivity, etc.
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(AddressActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
