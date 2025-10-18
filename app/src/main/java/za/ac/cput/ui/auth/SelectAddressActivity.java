package za.ac.cput.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import za.ac.cput.R;
import za.ac.cput.adapters.AddressAdapter;
import za.ac.cput.model.Address;
import za.ac.cput.ui.home.OrdersActivity;
import za.ac.cput.util.LocalCart;


public class SelectAddressActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnAddNewAddress;
    private List<Address> addresses;
    private AddressAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_address);

        recyclerView = findViewById(R.id.recyclerViewAddresses);
        btnAddNewAddress = findViewById(R.id.btnAddNewAddress);

        addresses = getIntent().getParcelableArrayListExtra("addresses");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddressAdapter(addresses, address -> {
            // User selected an address - proceed with checkout
            proceedWithSelectedAddress(address);
        });
        recyclerView.setAdapter(adapter);

        btnAddNewAddress.setOnClickListener(v -> {
            Intent intent = new Intent(SelectAddressActivity.this, AddressActivity.class);
            startActivity(intent);
            finish(); // close this activity to avoid back stack issues
        });
    }

    private void proceedWithSelectedAddress(Address address) {
        // Save the selected address or pass it to next screen
        // For example, clear cart and go to OrdersActivity:

        LocalCart.clearCart();

        Intent intent = new Intent(this, OrdersActivity.class);
        intent.putExtra("selectedAddress", address); // optional, if needed downstream
        startActivity(intent);
        finish();
    }
}

