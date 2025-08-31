package za.ac.cput.ui.auth;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.ac.cput.R;
import za.ac.cput.adapters.CartAdapter;
import za.ac.cput.model.CartItem;
import za.ac.cput.services.CartApi;
import za.ac.cput.services.CartApiService;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartChangeListener {

    private RecyclerView recyclerView;
    private TextView tvTotalPrice, emptyCartText;
    private Button btnCheckout;

    private List<CartItem> cartItems = new ArrayList<>();
    private CartAdapter adapter;

    private CartApiService apiService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.cart_activity);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.cartToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Cart");
        }

        recyclerView = findViewById(R.id.recyclerViewCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        emptyCartText = findViewById(R.id.emptyCartText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(cartItems, this);
        recyclerView.setAdapter(adapter);

        // Initialize Retrofit service
        apiService = CartApi.getClient().create(CartApiService.class);

        // Load cart items from backend
        loadCartItems();

        btnCheckout.setOnClickListener(v ->
                Toast.makeText(this, "Checkout clicked!", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadCartItems() {
        apiService.getCartItems().enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartItems.clear();
                    cartItems.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    updateTotal();
                    toggleEmptyView();
                } else {
                    Toast.makeText(CartActivity.this, "Failed to load cart", Toast.LENGTH_SHORT).show();
                    toggleEmptyView();
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Failed to load cart", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                toggleEmptyView();
            }
        });
    }

    private void updateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        tvTotalPrice.setText("Total: R" + String.format("%.2f", total));
    }

    private void toggleEmptyView() {
        if (cartItems.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyCartText.setVisibility(View.VISIBLE);
            btnCheckout.setVisibility(View.GONE);
            tvTotalPrice.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyCartText.setVisibility(View.GONE);
            btnCheckout.setVisibility(View.VISIBLE);
            tvTotalPrice.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCartUpdated() {
        updateTotal();
        toggleEmptyView();
        // Optionally call API to sync cart item changes with backend
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Back button
        return true;
    }
}
