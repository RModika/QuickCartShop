//package za.ac.cput.ui.home;
////
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.view.MotionEvent;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import za.ac.cput.R;
//
//public class HomeActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
//        setContentView(R.layout.activity_home);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        // Set click listeners for each category
//        setCategoryClick(R.id.beautyCategory, "Beauty");
//        setCategoryClick(R.id.cartCategory, "Cart");
//        setCategoryClick(R.id.groceriesCategory, "Groceries");
//        setCategoryClick(R.id.homeCategory, "Home");
//        setCategoryClick(R.id.householdCategory, "Household");
//        setCategoryClick(R.id.ordersCategory, "Orders");
//        setCategoryClick(R.id.profileCategory, "Profile");
//        setCategoryClick(R.id.snacksCategory, "Snacks");
//    }
//    @SuppressLint("ClickableViewAccessibility")
//    private void setCategoryClick(int viewId, String categoryName) {
//        ImageView category = findViewById(viewId);
//        if (category != null) {
//            category.setOnTouchListener((v, event) -> {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        Animation scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
//                        v.startAnimation(scaleDown);
//                        return true;
//
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//                        Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
//                        v.startAnimation(scaleUp);
//
//                        v.performClick(); // ✅ Ensures accessibility compliance
//                        return true;
//                }
//                return false;
//            });
//
//            category.setOnClickListener(v ->
//                    Toast.makeText(this, categoryName + " clicked", Toast.LENGTH_SHORT).show()
//            );
//        }
//    }
//
//
//}


package za.ac.cput.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import za.ac.cput.R;
import za.ac.cput.ui.home.OrdersActivity; // Make sure this import exists

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set click listeners for each category
        setCategoryClick(R.id.beautyCategory, "Beauty");
        setCategoryClick(R.id.cartCategory, "Cart");
        setCategoryClick(R.id.groceriesCategory, "Groceries");
        setCategoryClick(R.id.homeCategory, "Home");
        setCategoryClick(R.id.householdCategory, "Household");
        setCategoryClick(R.id.ordersCategory, "Orders");
        setCategoryClick(R.id.profileCategory, "Profile");
        setCategoryClick(R.id.snacksCategory, "Snacks");
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setCategoryClick(int viewId, String categoryName) {
        ImageView category = findViewById(viewId);
        if (category != null) {
            category.setOnTouchListener((v, event) -> {
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

            category.setOnClickListener(v -> {
                if (categoryName.equals("Orders")) {
                    // Navigate to OrdersActivity when Orders icon is clicked
                    Intent intent = new Intent(HomeActivity.this, OrdersActivity.class);
                    startActivity(intent);
                } else {
                    // Show a Toast for other categories
                    Toast.makeText(this, categoryName + " clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

