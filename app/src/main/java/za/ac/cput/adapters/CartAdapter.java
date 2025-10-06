//package za.ac.cput.adapters;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//
//import java.util.List;
//
//import za.ac.cput.R;
//import za.ac.cput.model.CartItem;
//
//public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
//
//    private List<CartItem> cartItems;
//    private OnCartChangeListener listener;
//
//    public interface OnCartChangeListener {
//        void onCartUpdated();
//    }
//
//    public CartAdapter(List<CartItem> cartItems, OnCartChangeListener listener) {
//        this.cartItems = cartItems;
//        this.listener = listener;
//    }
//
//    @NonNull
//    @Override
//    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.cart_item, parent, false);
//        return new CartViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
//        CartItem item = cartItems.get(position);
//
//        // Load product image
//        String imageUrl = item.getProductImageUrl();
//        Glide.with(holder.itemView.getContext())
//                .load(imageUrl)
//                .placeholder(R.drawable.placeholder_image)
//                .error(R.drawable.placeholder_image)
//                .into(holder.cartItemImage);
//
//        // Set product info
//        holder.tvProductName.setText(item.getProductName());
//        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
//        holder.tvUnitPrice.setText("R" + String.format("%.2f", item.getPrice()));
//        holder.tvTotalPrice.setText("R" + String.format("%.2f", item.getTotalPrice()));
//
//        // Increase quantity
//        holder.btnPlus.setOnClickListener(v -> {
//            item.setQuantity(item.getQuantity() + 1);
//            item.setTotalPrice(item.getQuantity() * item.getPrice());
//            notifyItemChanged(position);
//            if (listener != null) listener.onCartUpdated();
//        });
//
//        // Decrease quantity
//        holder.btnMinus.setOnClickListener(v -> {
//            if (item.getQuantity() > 1) {
//                item.setQuantity(item.getQuantity() - 1);
//                item.setTotalPrice(item.getQuantity() * item.getPrice());
//                notifyItemChanged(position);
//                if (listener != null) listener.onCartUpdated();
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return cartItems != null ? cartItems.size() : 0;
//    }
//
//    static class CartViewHolder extends RecyclerView.ViewHolder {
//        ImageView cartItemImage;
//        TextView tvProductName, tvQuantity, tvUnitPrice, tvTotalPrice;
//        Button btnPlus, btnMinus;
//
//        public CartViewHolder(@NonNull View itemView) {
//            super(itemView);
//            cartItemImage = itemView.findViewById(R.id.cartItemImage);
//            tvProductName = itemView.findViewById(R.id.tvProductName);
//            tvQuantity = itemView.findViewById(R.id.tvQuantity);
//            tvUnitPrice = itemView.findViewById(R.id.tvUnitPrice);
//            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
//            btnPlus = itemView.findViewById(R.id.btnPlus);
//            btnMinus = itemView.findViewById(R.id.btnMinus);
//        }
//    }
//}


package za.ac.cput.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import za.ac.cput.R;
import za.ac.cput.model.CartItem;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onCartUpdated();
    }

    public CartAdapter(List<CartItem> cartItems, OnCartChangeListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    public void setCartItems(List<CartItem> items) {
        this.cartItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        // Load product image
        Glide.with(holder.itemView.getContext())
                .load(item.getProductImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(holder.cartItemImage);

        // Set product info
        holder.tvProductName.setText(item.getProductName());
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvUnitPrice.setText("R" + String.format("%.2f", item.getPrice()));
        holder.tvTotalPrice.setText("R" + String.format("%.2f", item.getTotalPrice()));

        // 🔄 Set icon for minus/delete button depending on quantity
        if (item.getQuantity() > 1) {
            holder.btnMinus.setBackgroundResource(R.drawable.ic_minus); // your minus icon
        } else {
            holder.btnMinus.setBackgroundResource(R.drawable.ic_delete); // your delete icon
        }

        // ➕ Increase quantity
        holder.btnPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            item.setTotalPrice(item.getQuantity() * item.getPrice());
            notifyItemChanged(position);
            if (listener != null) listener.onCartUpdated();
        });

        // ➖ or 🗑 Decrease / Delete
        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                // decrease
                item.setQuantity(item.getQuantity() - 1);
                item.setTotalPrice(item.getQuantity() * item.getPrice());
                notifyItemChanged(position);
            } else {
                // delete
                cartItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItems.size());
            }
            if (listener != null) listener.onCartUpdated();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView cartItemImage;
        TextView tvProductName, tvQuantity, tvUnitPrice, tvTotalPrice;
        ImageButton btnPlus, btnMinus, btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cartItemImage = itemView.findViewById(R.id.cartItemImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvUnitPrice = itemView.findViewById(R.id.tvUnitPrice);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}