package za.ac.cput.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import za.ac.cput.R;
import za.ac.cput.model.Product;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.AdminProductViewHolder> {

    private List<Product> products;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEdit(Product product);
        void onDelete(Product product);
    }

    public AdminProductAdapter(List<Product> products, OnItemClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_product, parent, false);
        return new AdminProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminProductViewHolder holder, int position) {
        Product product = products.get(position);

        // Debug: Log the product data
        Log.d("AdminProductAdapter", "Binding product at position " + position + ": " +
                "Name: " + product.getProductName() + ", " +
                "Price: " + product.getProductPrice() + ", " +
                "Stock: " + product.getStockAvailability() + ", " +
                "Desc: " + product.getProductDescription());

        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    static class AdminProductViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, priceText, stockText, descriptionText;
        Button editButton, deleteButton;

        public AdminProductViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.productNameText);
            priceText = itemView.findViewById(R.id.productPriceText);
            stockText = itemView.findViewById(R.id.productStockText);
            descriptionText = itemView.findViewById(R.id.productDescriptionText);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(Product product, OnItemClickListener listener) {
            nameText.setText(product.getProductName());
            priceText.setText("R " + product.getProductPrice());
            stockText.setText("Stock: " + product.getStockAvailability());
            descriptionText.setText(product.getProductDescription());

            editButton.setOnClickListener(v -> listener.onEdit(product));
            deleteButton.setOnClickListener(v -> listener.onDelete(product));
        }
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }


}