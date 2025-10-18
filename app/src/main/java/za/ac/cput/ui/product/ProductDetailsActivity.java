package za.ac.cput.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import za.ac.cput.R;
import za.ac.cput.model.CartItem;
import za.ac.cput.util.FooterNavigationHelper;
import za.ac.cput.util.LocalCart;
import za.ac.cput.ui.auth.CartActivity;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView productDetailImage;
    private TextView productDetailName, productDetailPrice, productDetailDescription, productStockStatus, tvQuantity;
    private Button addToCartButton, btnPlus, btnMinus;
    private int quantity = 1;

    private long productId;
    private String productName;
    private double productPrice;
    private String stockAvailability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        FooterNavigationHelper.setupFooterNavigation(this);

        // Find views
        productDetailImage = findViewById(R.id.productDetailImage);
        productDetailName = findViewById(R.id.productDetailName);
        productDetailPrice = findViewById(R.id.productDetailPrice);
        productDetailDescription = findViewById(R.id.productDetailDescription);
        productStockStatus = findViewById(R.id.productStockStatus);
        addToCartButton = findViewById(R.id.addToCartButton);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);

        // Get product info from intent
        Intent intent = getIntent();
        productId = intent.getLongExtra("PRODUCT_ID", -1);
        productName = intent.getStringExtra("PRODUCT_NAME");
        productPrice = intent.getDoubleExtra("PRODUCT_PRICE", 0.0);
        String productDescriptionStr = intent.getStringExtra("PRODUCT_DESCRIPTION");
        stockAvailability = intent.getStringExtra("PRODUCT_STOCK");

        if (productId == -1) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set product data
        productDetailName.setText(productName);
        productDetailPrice.setText(String.format("R%.2f", productPrice));
        productDetailDescription.setText(productDescriptionStr);
        tvQuantity.setText(String.valueOf(quantity));

        // 🟢 Handle stock availability
        if (stockAvailability != null) {
            switch (stockAvailability.toUpperCase()) {
                case "AVAILABLE":
                    productStockStatus.setText("Stock: Available");
                    productStockStatus.setTextColor(ContextCompat.getColor(this, R.color.green));
                    addToCartButton.setEnabled(true);
                    addToCartButton.setBackgroundTintList(
                            ContextCompat.getColorStateList(this, R.color.accent_pink)
                    );
                    break;

                case "LOW_STOCK":
                    productStockStatus.setText("Stock: Low");
                    productStockStatus.setTextColor(ContextCompat.getColor(this, R.color.orange));
                    addToCartButton.setEnabled(true);
                    addToCartButton.setBackgroundTintList(
                            ContextCompat.getColorStateList(this, R.color.accent_pink)
                    );
                    break;

                case "OUT_OF_STOCK":
                    productStockStatus.setText("Stock: Out of Stock");
                    productStockStatus.setTextColor(ContextCompat.getColor(this, R.color.red));
                    addToCartButton.setEnabled(false);
                    addToCartButton.setBackgroundTintList(
                            ContextCompat.getColorStateList(this, R.color.gray)
                    );
                    break;

                default:
                    productStockStatus.setText("Stock: Unknown");
                    productStockStatus.setTextColor(ContextCompat.getColor(this, R.color.gray));
                    addToCartButton.setEnabled(false);
                    addToCartButton.setBackgroundTintList(
                            ContextCompat.getColorStateList(this, R.color.gray)
                    );
                    break;
            }
        }

        // Load image
        String imageUrl = "http://10.0.2.2:8080/mobileApp/Product/image/" + productId;
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(productDetailImage);

        // Quantity buttons
        btnPlus.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
        });

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        // Add to cart button
        addToCartButton.setOnClickListener(v -> {
            CartItem cartItem = new CartItem.Builder()
                    .setProductId(productId)
                    .setQuantity(quantity)
                    .setPrice(productPrice)
                    .setTotalPrice(productPrice * quantity)
                    .build();

            LocalCart.addItem(cartItem);

            Toast.makeText(this, productName + " added to cart", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProductDetailsActivity.this, CartActivity.class));
        });
    }
}
