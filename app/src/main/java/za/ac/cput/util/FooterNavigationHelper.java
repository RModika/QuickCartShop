package za.ac.cput.util;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import za.ac.cput.R;
import za.ac.cput.ui.auth.CartActivity;
import za.ac.cput.ui.home.HomeActivity;
import za.ac.cput.ui.profile.ProfileActivity;
import za.ac.cput.ui.home.OrdersActivity;

public class FooterNavigationHelper {

    public static void setupFooterNavigation(Activity activity) {
        // Home
        View homeBtn = activity.findViewById(R.id.homeCategory);
        if (homeBtn != null) {
            homeBtn.setOnClickListener(v -> {
                Intent intent = new Intent(activity, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
            });
        }

        // Profile
        View profileBtn = activity.findViewById(R.id.profileCategory);
        if (profileBtn != null) {
            profileBtn.setOnClickListener(v ->
                    activity.startActivity(new Intent(activity, ProfileActivity.class)));
        }

        // Orders
        View ordersBtn = activity.findViewById(R.id.ordersCategory);
        if (ordersBtn != null) {
            ordersBtn.setOnClickListener(v ->
                    activity.startActivity(new Intent(activity, OrdersActivity.class)));
        }

        // Cart
        View cartBtn = activity.findViewById(R.id.cartCategory);
        if (cartBtn != null) {
            cartBtn.setOnClickListener(v ->
                    activity.startActivity(new Intent(activity, CartActivity.class)));
        }
    }
}
