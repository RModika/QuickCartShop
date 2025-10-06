package za.ac.cput.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import za.ac.cput.R;
import za.ac.cput.adapters.CartAdapter;
import za.ac.cput.model.CartItem;
import za.ac.cput.util.LocalCart;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartChangeListener {

    private RecyclerView recyclerView;
    private TextView tvTotalPrice, emptyCartText;
    private Button btnCheckout;
    private List<CartItem> cartItems;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_activity);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.cartToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Cart");
        }

        recyclerView = findViewById(R.id.recyclerViewCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        emptyCartText = findViewById(R.id.emptyCartText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartItems = LocalCart.getCart();
        adapter = new CartAdapter(cartItems, this);
        recyclerView.setAdapter(adapter);

        updateUI();

        btnCheckout.setOnClickListener(view -> {
            if (cartItems.isEmpty()) {
                // Show popup: cart is empty
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(CartActivity.this);
                builder.setTitle("Cart Empty")
                        .setMessage("Your cart is empty. Please add items before checkout.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                // Proceed to AddressActivity
                Intent intent = new Intent(CartActivity.this, AddressActivity.class);
                startActivity(intent);
            }
//            Intent intent = new Intent(CartActivity.this, AddressActivity.class);
//            startActivity(intent);
        });

    }

    private void updateUI() {
        if (cartItems.isEmpty()) {
            recyclerView.setVisibility(RecyclerView.GONE);
            emptyCartText.setVisibility(TextView.VISIBLE);
            tvTotalPrice.setText("Total: R0.00");
        } else {
            recyclerView.setVisibility(RecyclerView.VISIBLE);
            emptyCartText.setVisibility(TextView.GONE);
            updateTotal();
        }
    }

    private void updateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        tvTotalPrice.setText("Total: R" + String.format("%.2f", total));
    }

    @Override
    public void onCartUpdated() {
        updateTotal();
        updateUI();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartItems = LocalCart.getCart(); // Refresh cart
        adapter.setCartItems(cartItems);
        adapter.notifyDataSetChanged();
        updateUI();
    }


}