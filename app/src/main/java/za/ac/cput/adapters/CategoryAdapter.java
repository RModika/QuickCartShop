//package za.ac.cput.adapters;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//import za.ac.cput.R;
//import za.ac.cput.model.Category;
//import za.ac.cput.ui.product.ProductsActivity;
//
//public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
//
//    private final List<Category> categories;
//    private final Context context;
//
//    public CategoryAdapter(List<Category> categories, Context context) {
//        this.categories = categories;
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Category category = categories.get(position);
//        holder.categoryName.setText(category.getName());
//
//        // Set correct icon based on category name
//        switch (category.getName().toLowerCase()) {
//            case "beauty":
//                holder.categoryIcon.setImageResource(R.drawable.ic_beauty);
//                break;
//            case "groceries":
//                holder.categoryIcon.setImageResource(R.drawable.ic_groceries);
//                break;
//            case "household":
//                holder.categoryIcon.setImageResource(R.drawable.ic_household);
//                break;
//            case "snacks":
//                holder.categoryIcon.setImageResource(R.drawable.ic_snacks);
//                break;
//            case "bakery":
//                holder.categoryIcon.setImageResource(R.drawable.ic_bakery); // match your drawable name
//                break;
//            case "dairy":
//                holder.categoryIcon.setImageResource(R.drawable.ic_dairy);
//                break;
//            default:
//                // fallback icon in case a new category is added
//                holder.categoryIcon.setImageResource(R.drawable.ic_groceries);
//                break;
//        }
//
//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(context, ProductsActivity.class);
//            intent.putExtra("CATEGORY_ID", category.getCategoryId());
//            intent.putExtra("CATEGORY_NAME", category.getName());
//            context.startActivity(intent);
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return categories.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        final TextView categoryName;
//        final ImageView categoryIcon;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            categoryName = itemView.findViewById(R.id.categoryName);
//            categoryIcon = itemView.findViewById(R.id.categoryIcon);
//        }
//    }
//}


package za.ac.cput.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import za.ac.cput.R;
import za.ac.cput.model.Category;
import za.ac.cput.ui.product.ProductsActivity;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories;
    private Context context;

    public CategoryAdapter(List<Category> categories, Context context) {
        this.categories = categories;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.categoryName.setText(category.getName());

        // Assign correct icon based on category name
        int iconRes = R.drawable.ic_placeholder; // default
        switch (category.getName().toLowerCase()) {
            case "beauty": iconRes = R.drawable.ic_beauty; break;
            case "groceries": iconRes = R.drawable.ic_groceries; break;
            case "snacks": iconRes = R.drawable.ic_snacks; break;
            case "household": iconRes = R.drawable.ic_household; break;
            case "bakery": iconRes = R.drawable.ic_bakery; break;
            case "dairy": iconRes = R.drawable.ic_dairy; break;
        }
        holder.categoryIcon.setImageResource(iconRes);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductsActivity.class);
            intent.putExtra("CATEGORY_ID", category.getCategoryId());
            intent.putExtra("CATEGORY_NAME", category.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryIcon;
        TextView categoryName;

        public ViewHolder(View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.categoryIcon);
            categoryName = itemView.findViewById(R.id.categoryName);
        }
    }
}