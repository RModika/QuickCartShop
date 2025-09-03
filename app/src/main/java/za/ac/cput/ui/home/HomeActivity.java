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
import za.ac.cput.services.ApiClient;
import za.ac.cput.ui.auth.CartActivity;
import za.ac.cput.ui.product.ProductsActivity;
import za.ac.cput.ui.profile.ProfileActivity;

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

        categoryApiService = ApiClient.getCategoryApiService();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        loadCategoriesFromApi();


        setBottomNavigationClicks();
    }

    private void setBottomNavigationClicks() {
        ImageView homeIcon = findViewById(R.id.homeIcon);
        ImageView profileIcon = findViewById(R.id.profileIcon);
        ImageView ordersIcon = findViewById(R.id.ordersIcon);
        ImageView cartIcon = findViewById(R.id.cartIcon);

        homeIcon.setOnClickListener(v ->
                Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show());

        profileIcon.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

        ordersIcon.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, OrdersActivity.class)));

        cartIcon.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, CartActivity.class)));
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

            categoryView.setOnClickListener(v -> handleCategoryClick(displayName));
        }
    }

    private void loadCategoriesFromApi() {
        Call<List<Category>> call = categoryApiService.getAllCategories();

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loadedCategories = response.body();
                    Log.d(TAG, "API Success! Loaded " + loadedCategories.size() + " categories");

                    for (Category category : loadedCategories) {
                        Log.d(TAG, "BACKEND CATEGORY: " + category.getName() +
                                " - " + category.getDescription());
                    }


                    updateCategoryUI();


                    enableCategoryClicks();

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

    private void updateCategoryUI() {
        toggleCategoryVisibility(R.id.groceriesCategory, "Groceries");
        toggleCategoryVisibility(R.id.householdCategory, "Household");
        toggleCategoryVisibility(R.id.beautyCategory, "Beauty");
        toggleCategoryVisibility(R.id.snacksCategory, "Snacks");
        toggleCategoryVisibility(R.id.dairyCategory, "Dairy");
        toggleCategoryVisibility(R.id.bakeryCategory, "Bakery");
    }

    private void toggleCategoryVisibility(int viewId, String categoryName) {
        ImageView categoryView = findViewById(viewId);
        if (categoryView != null) {
            Category found = findCategoryByName(categoryName);
            if (found == null) {
                categoryView.setVisibility(ImageView.GONE);
            } else {
                categoryView.setVisibility(ImageView.VISIBLE);
            }
        }
    }

    private void enableCategoryClicks() {
        setCategoryClick(R.id.groceriesCategory, "Groceries");
        setCategoryClick(R.id.householdCategory, "Household");
        setCategoryClick(R.id.beautyCategory, "Beauty");
        setCategoryClick(R.id.snacksCategory, "Snacks");
        setCategoryClick(R.id.bakeryCategory, "Bakery");
        setCategoryClick(R.id.dairyCategory, "Dairy");
    }

    private void handleCategoryClick(String categoryName) {
        Log.d(TAG, "User clicked: " + categoryName);

        Category foundCategory = findCategoryByName(categoryName);

        if (foundCategory != null) {
            showCategoryDetails(foundCategory);
        } else {
            Toast.makeText(this,
                    categoryName + " - Category coming soon!",
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Category not in backend: " + categoryName);
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
        Log.d(TAG, "Showing category: " + category.getName() + " - " + category.getDescription());

        Intent intent = new Intent(HomeActivity.this, ProductsActivity.class);
        intent.putExtra("CATEGORY_ID", category.getCategoryId()); // Matches backend
        intent.putExtra("CATEGORY_NAME", category.getName());
        startActivity(intent);
    }

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