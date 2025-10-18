package za.ac.cput.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import za.ac.cput.R;
import za.ac.cput.model.CartItem;
import za.ac.cput.model.Product;
import za.ac.cput.ui.product.ProductDetailsActivity;
import za.ac.cput.util.LocalCart;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context context;
    private List<Product> products;

    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product, context);
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productName, productPrice, productDescription, productStock;
        private Button btnAddToCart;
        private Button btnViewDetails;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
//            productDescription = itemView.findViewById(R.id.productDescription);
            productStock = itemView.findViewById(R.id.productStock);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }

        public void bind(Product product, Context context) {
            productName.setText(product.getProductName());
            productPrice.setText(String.format("R%.2f", product.getProductPrice()));

            // ✅ Show stock status visually
            String stock = product.getStockAvailability();
            if (stock != null) {
                if (product.isAvailable()) {
                    productStock.setText("Stock: Available");
                    productStock.setTextColor(ContextCompat.getColor(context, R.color.green));
                    btnAddToCart.setEnabled(true);
                    btnAddToCart.setBackgroundTintList(
                            ContextCompat.getColorStateList(context, R.color.accent_pink));
                } else if (product.isLowStock()) {
                    productStock.setText("Stock: Low");
                    productStock.setTextColor(ContextCompat.getColor(context, R.color.orange));
                    btnAddToCart.setEnabled(true);
                    btnAddToCart.setBackgroundTintList(
                            ContextCompat.getColorStateList(context, R.color.accent_pink));
                } else if (product.isOutOfStock()) {
                    productStock.setText("Stock: Out of Stock");
                    productStock.setTextColor(ContextCompat.getColor(context, R.color.red));
                    btnAddToCart.setEnabled(false);
                    btnAddToCart.setBackgroundTintList(
                            ContextCompat.getColorStateList(context, R.color.gray));
                } else {
                    productStock.setText("Stock: Unknown");
                    productStock.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
                }
            }

            loadProductImage(product.getProductId());

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("PRODUCT_ID", product.getProductId());
                intent.putExtra("PRODUCT_NAME", product.getProductName());
                intent.putExtra("PRODUCT_PRICE", product.getProductPrice());
                intent.putExtra("PRODUCT_DESCRIPTION", product.getProductDescription());
                intent.putExtra("PRODUCT_STOCK", product.getStockAvailability());
                context.startActivity(intent);
            });

            btnAddToCart.setOnClickListener(v -> {
                int quantity = 1;
                CartItem cartItem = new CartItem.Builder()
                        .setProductId(product.getProductId())
                        .setProductName(product.getProductName())
                        .setProductImageUrl("http://10.0.2.2:8080/mobileApp/Product/image/" + product.getProductId())
                        .setQuantity(quantity)
                        .setPrice(product.getProductPrice())
                        .setTotalPrice(product.getProductPrice() * quantity)
                        .build();

                LocalCart.addItem(cartItem);

                Toast.makeText(context,
                        product.getProductName() + " added to cart",
                        Toast.LENGTH_SHORT).show();
            });

            btnViewDetails.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("PRODUCT_ID", product.getProductId());
                intent.putExtra("PRODUCT_NAME", product.getProductName());
                intent.putExtra("PRODUCT_PRICE", product.getProductPrice());
                intent.putExtra("PRODUCT_DESCRIPTION", product.getProductDescription());
                intent.putExtra("PRODUCT_STOCK", product.getStockAvailability());
                context.startActivity(intent);
            });
        }

        private void loadProductImage(Long productId) {
            if (productId == null) {
                Glide.with(itemView.getContext())
                        .load(R.drawable.placeholder_image)
                        .into(productImage);
                return;
            }

            String imageUrl = "http://10.0.2.2:8080/mobileApp/Product/image/" + productId;

            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                    .into(productImage);
        }
    }
}
