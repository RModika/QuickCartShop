package za.ac.cput.ui.product;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.ac.cput.R;
import za.ac.cput.adapters.ProductAdapter;
import za.ac.cput.model.Product;
import za.ac.cput.services.ApiClient;
import za.ac.cput.services.ProductApiService;
import za.ac.cput.util.FooterNavigationHelper;

public class ProductsActivity extends AppCompatActivity {

    private static final String TAG = "ProductsActivity";
    private ProductApiService productApiService;
    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private Long categoryId;
    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        FooterNavigationHelper.setupFooterNavigation(this);

        // Initialize API service and RecyclerView
        productApiService = ApiClient.getProductApiService(this);
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        productsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        //  Initialize adapter properly (fixes constructor issue)
        productAdapter = new ProductAdapter(this, new ArrayList<>());
        productsRecyclerView.setAdapter(productAdapter);

        // Get category and search query from Intent
        categoryId = getIntent().getLongExtra("CATEGORY_ID", -1);
        searchQuery = getIntent().getStringExtra("SEARCH_QUERY");
        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(categoryName != null ? categoryName : "Products");
        }

        loadProducts();
    }

    private void loadProducts() {
        if (categoryId != -1) {
            // Load products by category
            Call<List<Product>> call = productApiService.getProductsByCategory(categoryId);
            fetchProducts(call, true);
        } else if (searchQuery != null && !searchQuery.isEmpty()) {
            // Load all products and filter by search query
            Call<List<Product>> call = productApiService.getAllProducts();
            fetchProducts(call, false);
        } else {
            Toast.makeText(this, "No products to display", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchProducts(Call<List<Product>> call, boolean isCategoryFilter) {
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();

                    // Filter by search query manually (no Streams)
                    if (!isCategoryFilter && searchQuery != null && !searchQuery.isEmpty()) {
                        List<Product> filtered = new ArrayList<>();
                        for (Product p : products) {
                            if (p.getProductName().toLowerCase().contains(searchQuery.toLowerCase())) {
                                filtered.add(p);
                            }
                        }
                        products = filtered;
                    }

                    //  Ensure stock info is not null
                    for (Product p : products) {
                        if (p.getStockAvailability() == null) {
                            p.setStockAvailability("UNKNOWN");
                        }
                    }

                    productAdapter.setProducts(products);

                    if (products.isEmpty()) {
                        Toast.makeText(ProductsActivity.this, "No products found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProductsActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage());
                Toast.makeText(ProductsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
