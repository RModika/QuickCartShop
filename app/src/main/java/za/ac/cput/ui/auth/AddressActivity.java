package za.ac.cput.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import za.ac.cput.R;
import za.ac.cput.model.Address;
import za.ac.cput.model.AddressDTO;  // ✅ Make sure this is the correct import
import za.ac.cput.services.AddressService;
import za.ac.cput.ui.home.OrdersActivity;
import za.ac.cput.util.LocalCart;

public class AddressActivity extends AppCompatActivity {

    private EditText streetNumber, streetName, suburb, city, postalCode;
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

            // Basic validation
            if (streetNumber.getText().toString().isEmpty() ||
                    streetName.getText().toString().isEmpty() ||
                    suburb.getText().toString().isEmpty() ||
                    city.getText().toString().isEmpty() ||
                    postalCode.getText().toString().isEmpty()) {

                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Build Address object (domain model)
            Address address = new Address();
            address.setStreetNumber(streetNumber.getText().toString());
            address.setStreetName(streetName.getText().toString());
            address.setSuburb(suburb.getText().toString());
            address.setCity(city.getText().toString());
            address.setPostalCode(postalCode.getText().toString());
            address.setUserId(userId);


            AddressService.saveAddress(this, address, new AddressService.AddressCallback() {
                @Override
                public void onSuccess() {
//                    Toast.makeText(AddressActivity.this, "Address saved!", Toast.LENGTH_SHORT).show();
//                    finish(); // or navigate to next activity

                    Toast.makeText(AddressActivity.this, "Address saved! Checkout complete.", Toast.LENGTH_SHORT).show();

                    // ✅ Clear the cart after "checkout"
                    LocalCart.clearCart();

                    // ✅ Navigate to OrdersActivity
                    Intent intent = new Intent(AddressActivity.this, OrdersActivity.class);
                    intent.putExtra("userId", userId); // optional if you want to filter orders
                    startActivity(intent);

                    finish(); // Close activity or go back to home
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(AddressActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
