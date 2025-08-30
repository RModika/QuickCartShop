package za.ac.cput.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.ac.cput.R;
import za.ac.cput.model.Category;
import za.ac.cput.services.CategoryApiService;
import za.ac.cput.services.RetrofitClient;
import za.ac.cput.ui.auth.CartActivity;
import za.ac.cput.ui.product.ProductsActivity; // Add this import

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private CategoryApiService categoryApiService;
    private List<Category> loadedCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_home);

        categoryApiService = RetrofitClient.getCategoryApiService();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up all click listeners
//        setupCategoryClicks();
//
//        // Load categories from API
        loadCategoriesFromApi();

        setCategoryClick(R.id.groceriesCategory, "Groceries");
        setCategoryClick(R.id.householdCategory, "Household");
        setCategoryClick(R.id.beautyCategory, "Beauty");
        setCategoryClick(R.id.snacksCategory, "Snacks");
        setCategoryClick(R.id.ordersCategory, "Orders");
        setCategoryClick(R.id.profileCategory, "Profile");
        setCategoryClick(R.id.cartCategory, "Cart");
        setCategoryClick(R.id.homeCategory, "Home");
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setCategoryClick(int viewId, String displayName) {
        ImageView categoryView = findViewById(viewId);
        if (categoryView != null) {
            categoryView.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Animation scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
                        v.startAnimation(scaleDown);
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
                        v.startAnimation(scaleUp);
                        v.performClick();
                        return true;
                }
                return false;
            });

            categoryView.setOnClickListener(v -> {
                switch (displayName) {
                    case "Orders":
                        startActivity(new Intent(HomeActivity.this, OrdersActivity.class));
                        break;
                    case "Cart":
                        startActivity(new Intent(HomeActivity.this, CartActivity.class));
                        break;
                    default:
                        Toast.makeText(this, displayName + " clicked", Toast.LENGTH_SHORT).show();
                        break;
                }
            });
        }
    }

//    private void setupCategoryClicks() {
//        // Map your UI elements to EXACT backend category names
//        setCategoryClick(R.id.groceriesCategory, "Groceries");
//        setCategoryClick(R.id.householdCategory, "Household");
//        setCategoryClick(R.id.beautyCategory, "Beauty");
//        setCategoryClick(R.id.snacksCategory, "Snacks");
//        setCategoryClick(R.id.ordersCategory, "Orders");
//        setCategoryClick(R.id.profileCategory, "Profile");
//        setCategoryClick(R.id.cartCategory, "Cart");
//        setCategoryClick(R.id.homeCategory, "Home");
//    }

    private void loadCategoriesFromApi() {
        Call<List<Category>> call = categoryApiService.getAllCategories();

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loadedCategories = response.body();
                    Log.d(TAG, "API Success! Loaded " + loadedCategories.size() + " categories");

                    // Debug: Print all categories from backend
                    for (Category category : loadedCategories) {
                        Log.d(TAG, "BACKEND CATEGORY: " + category.getName() +
                                " - " + category.getDescription());
                    }

                    Toast.makeText(HomeActivity.this,
                            "Connected to backend! " + loadedCategories.size() + " categories loaded",
                            Toast.LENGTH_SHORT).show();

                } else {
                    Log.e(TAG, "API Error: " + response.code() + " - " + response.message());
                    Toast.makeText(HomeActivity.this,
                            "Backend connection failed",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage());
                Toast.makeText(HomeActivity.this,
                        "Cannot connect to server: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleCategoryClick(String categoryName) {
        Log.d(TAG, "User clicked: " + categoryName);

        if (loadedCategories != null) {
            Category foundCategory = findCategoryByName(categoryName);

            if (foundCategory != null) {
                // Navigate to products activity
                showCategoryDetails(foundCategory);
            } else {
                // This category exists in UI but not in backend
                Toast.makeText(this,
                        categoryName + " - Category coming soon!",
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Category not in backend: " + categoryName);
            }
        } else {
            // API not loaded yet
            Toast.makeText(this,
                    categoryName + " - Loading...",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private Category findCategoryByName(String name) {
        if (loadedCategories == null) return null;

        for (Category category : loadedCategories) {
            if (category.getName().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return null;
    }

    private void showCategoryDetails(Category category) {
        String message = category.getName() + "\n" + category.getDescription();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        Log.d(TAG, "Showing category: " + category.getName() +
                " - " + category.getDescription());

        // Navigate to products activity for this category
        Intent intent = new Intent(this, ProductsActivity.class);
        intent.putExtra("CATEGORY_ID", category.getCategoryId());
        intent.putExtra("CATEGORY_NAME", category.getName());
        startActivity(intent);
    }

    // Add this to see what's happening in Logcat
    private void checkBackendCategories() {
        if (loadedCategories != null) {
            Log.d(TAG, "=== BACKEND CATEGORIES AVAILABLE ===");
            for (Category cat : loadedCategories) {
                Log.d(TAG, cat.getCategoryId() + ": " + cat.getName());
            }
            Log.d(TAG, "===================================");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBackendCategories();
    }
}