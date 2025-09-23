package za.ac.cput.ui.product;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.ac.cput.R;
import za.ac.cput.model.Product;
import za.ac.cput.services.ProductApiService;
import za.ac.cput.services.ApiClient;
import za.ac.cput.adapters.ProductAdapter;

public class ProductsActivity extends AppCompatActivity {

    private static final String TAG = "ProductsActivity";
    private ProductApiService productApiService;
    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private Long categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        // Initialize service
        productApiService = ApiClient.getProductApiService(this);

        productsRecyclerView = findViewById(R.id.productsRecyclerView);


        categoryId = getIntent().getLongExtra("CATEGORY_ID", -1);
        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(categoryName);
        }


        productsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter();
        productsRecyclerView.setAdapter(productAdapter);


        loadProducts();
    }

    private void loadProducts() {
        if (categoryId == -1) {
            Toast.makeText(this, "Invalid category", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Call<List<Product>> call = productApiService.getProductsByCategory(categoryId);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    Log.d(TAG, "Loaded " + products.size() + " products");

                    productAdapter.setProducts(products);
                    productAdapter.notifyDataSetChanged();

                    if (products.isEmpty()) {
                        Toast.makeText(ProductsActivity.this,
                                "No products found in this category",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "API Error: " + response.code());
                    Toast.makeText(ProductsActivity.this,
                            "Failed to load products",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage());
                Toast.makeText(ProductsActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
