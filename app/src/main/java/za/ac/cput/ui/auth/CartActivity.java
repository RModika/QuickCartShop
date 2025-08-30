package za.ac.cput.ui.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import za.ac.cput.R;
import za.ac.cput.adapters.CartAdapter;
import za.ac.cput.model.CartItem;

//
public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartChangeListener {

    RecyclerView recyclerView;
    TextView tvTotalPrice;
    Button btnCheckout;
    List<CartItem> cartItems;
    CartAdapter adapter;

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

        recyclerView = findViewById(R.id.recyclerViewCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);

        cartItems = new ArrayList<>();
        cartItems.add(new CartItem("Burger", 50.0, 1, R.drawable.ic_launcher_foreground));
        cartItems.add(new CartItem("Pizza", 120.0, 2, R.drawable.ic_launcher_foreground));

        adapter = new CartAdapter(cartItems, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        updateTotal();

        btnCheckout.setOnClickListener(v ->
                Toast.makeText(this, "Checkout clicked!", Toast.LENGTH_SHORT).show()
        );
    }

    private void updateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        tvTotalPrice.setText("Total: R" + String.format("%.2f", total));
    }

    @Override
    public void onCartUpdated() {
        updateTotal();
    }
}
