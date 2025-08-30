package za.ac.cput.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import za.ac.cput.R;
import za.ac.cput.model.CustomerOrder;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>{

    private final List<CustomerOrder> orders = new ArrayList<>();

    public void setData(List<CustomerOrder> newData) {
        orders.clear();
        if (newData != null) orders.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        CustomerOrder order = orders.get(position);
        holder.orderId.setText("Order ID: " + order.getOrderId());
        holder.orderStatus.setText("Status: " + (order.getStatus() == null ? "—" : order.getStatus()));

        // Format date safely
        String raw = order.getOrderDate();
        holder.orderDate.setText("Date: " + formatDateSafely(raw));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderStatus, orderDate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.textOrderId);
            orderStatus = itemView.findViewById(R.id.textOrderStatus);
            orderDate = itemView.findViewById(R.id.textOrderDate);
        }
    }

    private String formatDateSafely(String raw) {
        if (raw == null || raw.isEmpty()) return "—";
        try {
            Date d;
            if (raw.length() > 10) {
                SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                d = iso.parse(raw);
            } else {
                SimpleDateFormat shortIso = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                d = shortIso.parse(raw);
            }
            if (d != null) {
                return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(d);
            }
        } catch (ParseException ignored) {}
        return raw; // fallback
    }
}
