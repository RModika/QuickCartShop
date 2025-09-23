//package za.ac.cput.ui.product;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.bumptech.glide.Glide;
//
//import za.ac.cput.R;
//import za.ac.cput.model.CartItem;
//import za.ac.cput.util.LocalCart;
//import za.ac.cput.ui.auth.CartActivity;
//
//public class ProductDetailsActivity extends AppCompatActivity {
//
//    private ImageView productDetailImage;
//    private TextView productDetailName, productDetailPrice, productDetailDescription, productStockStatus, tvQuantity;
//    private Button addToCartButton, btnPlus, btnMinus;
//    private int quantity = 1;
//
//    private long productId;
//    private String productName;
//    private double productPrice;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_product_details);
//
//        // Find views
//        productDetailImage = findViewById(R.id.productDetailImage);
//        productDetailName = findViewById(R.id.productDetailName);
//        productDetailPrice = findViewById(R.id.productDetailPrice);
//        productDetailDescription = findViewById(R.id.productDetailDescription);
//        productStockStatus = findViewById(R.id.productStockStatus);
//        addToCartButton = findViewById(R.id.addToCartButton);
//
//        tvQuantity = findViewById(R.id.tvQuantity);
//        btnPlus = findViewById(R.id.btnPlus);
//        btnMinus = findViewById(R.id.btnMinus);
//
//        // Get product info from intent
//        Intent intent = getIntent();
//        productId = intent.getLongExtra("PRODUCT_ID", -1);
//        productName = intent.getStringExtra("PRODUCT_NAME");
//        productPrice = intent.getDoubleExtra("PRODUCT_PRICE", 0.0);
//        String productDescriptionStr = intent.getStringExtra("PRODUCT_DESCRIPTION");
//        String stockAvailability = intent.getStringExtra("PRODUCT_STOCK");
//
//        if (productId == -1) {
//            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        // Set data
//        productDetailName.setText(productName);
//        productDetailPrice.setText(String.format("R%.2f", productPrice));
//        productDetailDescription.setText(productDescriptionStr);
//        productStockStatus.setText(stockAvailability != null ? "Stock: " + stockAvailability : "Stock: Unknown");
//        tvQuantity.setText(String.valueOf(quantity));
//
//        // Load image
//        String imageUrl = "http://10.0.2.2:8080/mobileApp/Product/image/" + productId;
//        Glide.with(this)
//                .load(imageUrl)
//                .placeholder(R.drawable.placeholder_image)
//                .error(R.drawable.placeholder_image)
//                .into(productDetailImage);
//
//        // Quantity buttons
//        btnPlus.setOnClickListener(v -> {
//            quantity++;
//            tvQuantity.setText(String.valueOf(quantity));
//        });
//
//        btnMinus.setOnClickListener(v -> {
//            if (quantity > 1) {
//                quantity--;
//                tvQuantity.setText(String.valueOf(quantity));
//            }
//        });
//
//        // Add to cart
//        addToCartButton.setOnClickListener(v -> {
//            CartItem cartItem = new CartItem.Builder()
//                    .setProductId(productId)
//                    .setQuantity(quantity)
//                    .setPrice(productPrice)
//                    .setTotalPrice(productPrice * quantity)
//                    .build();
//
//            LocalCart.addItem(cartItem);
//
//            Toast.makeText(this, productName + " added to cart", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(ProductDetailsActivity.this, CartActivity.class));
//        });
//    }
//}


package za.ac.cput.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.ac.cput.R;
import za.ac.cput.model.CartItem;
import za.ac.cput.services.ApiClient;
import za.ac.cput.services.CartApiService;
import za.ac.cput.ui.auth.CartActivity;
import za.ac.cput.util.LocalCart;

import android.content.SharedPreferences;


public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView productDetailImage;
    private TextView productDetailName, productDetailPrice, productDetailDescription, productStockStatus, tvQuantity;
    private Button addToCartButton, btnPlus, btnMinus;
    private int quantity = 1;

    private long productId;
    private String productName;
    private double productPrice;

    private CartApiService cartApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

//        cartApiService = ApiClient.getCartApiService();
        cartApiService = ApiClient.getCartApiService(this);


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
        String stockAvailability = intent.getStringExtra("PRODUCT_STOCK");

        if (productId == -1) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set data
        productDetailName.setText(productName);
        productDetailPrice.setText(String.format("R%.2f", productPrice));
        productDetailDescription.setText(productDescriptionStr);
        productStockStatus.setText(stockAvailability != null ? "Stock: " + stockAvailability : "Stock: Unknown");
        tvQuantity.setText(String.valueOf(quantity));

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
            int quantity = 1;

            // Use your CartItem model (not just strings!)
            CartItem cartItem = new CartItem.Builder()
                    .setProductId(productId)
                    .setQuantity(quantity)
                    .setPrice(productPrice)
                    .setTotalPrice(productPrice * quantity)
                    .build();

            // Store item in LocalCart
            LocalCart.addItem(cartItem);

            Toast.makeText(ProductDetailsActivity.this,
                    productName + " added to cart",
                    Toast.LENGTH_SHORT).show();

            // Navigate to CartActivity
            startActivity(new Intent(ProductDetailsActivity.this, CartActivity.class));
        });
    }

//    private void addToCart() {
//        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        long userId = prefs.getLong("userId", -1);
//        String email = prefs.getString("email", null);
//        String password = prefs.getString("password", null);
//
//        if (userId == -1) {
//            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (email == null || password == null) {
//            Toast.makeText(this, "User credentials missing", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Create a Retrofit client with Basic Auth headers
//        CartApiService cartApiServiceWithAuth = ApiClient.getCartApiService(this);
//
//        cartApiServiceWithAuth.addCartItem(userId, productId, quantity).enqueue(new Callback<CartItem>() {
//            @Override
//            public void onResponse(Call<CartItem> call, Response<CartItem> response) {
//                if (response.isSuccessful()) {
//                    Toast.makeText(ProductDetailsActivity.this, productName + " added to cart", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(ProductDetailsActivity.this, CartActivity.class));
//                } else {
//                    Toast.makeText(ProductDetailsActivity.this, "Failed to add item", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CartItem> call, Throwable t) {
//                Toast.makeText(ProductDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
