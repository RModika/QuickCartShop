package za.ac.cput.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import za.ac.cput.R;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView productDetailImage;
    private TextView productDetailName, productDetailPrice, productDetailDescription, productStockStatus;
    private Button addToCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);


        productDetailImage = findViewById(R.id.productDetailImage);
        productDetailName = findViewById(R.id.productDetailName);
        productDetailPrice = findViewById(R.id.productDetailPrice);
        productDetailDescription = findViewById(R.id.productDetailDescription);
        productStockStatus = findViewById(R.id.productStockStatus);
        addToCartButton = findViewById(R.id.addToCartButton);


        Intent intent = getIntent();
        Long productId = intent.getLongExtra("PRODUCT_ID", -1);
        String productName = intent.getStringExtra("PRODUCT_NAME");
        double productPrice = intent.getDoubleExtra("PRODUCT_PRICE", 0.0);
        String productDescription = intent.getStringExtra("PRODUCT_DESCRIPTION");
        String stockAvailability = intent.getStringExtra("PRODUCT_STOCK"); // received as string

        if (productId == -1) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        productDetailName.setText(productName);
        productDetailPrice.setText(String.format("R%.2f", productPrice));
        productDetailDescription.setText(productDescription);

        if (stockAvailability != null) {
            productStockStatus.setText("Stock: " + stockAvailability.replace("_", " "));
        } else {
            productStockStatus.setText("Stock: Unknown");
        }

        String imageUrl = "http://10.0.2.2:8080/Product/image/" + productId;
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(productDetailImage);

        addToCartButton.setOnClickListener(v -> {
            Toast.makeText(this, productName + " added to cart", Toast.LENGTH_SHORT).show();
        });
    }
}
