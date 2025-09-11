package za.ac.cput.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import za.ac.cput.R;
import za.ac.cput.model.PaymentMethod;

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.PaymentMethodViewHolder> {

    private List<PaymentMethod> paymentMethods;
    private OnPaymentMethodClickListener listener;

    public interface OnPaymentMethodClickListener {
        void onPaymentMethodClick(PaymentMethod method);
    }

    public PaymentMethodAdapter(List<PaymentMethod> paymentMethods, OnPaymentMethodClickListener listener) {
        this.paymentMethods = paymentMethods;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PaymentMethodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment_method, parent, false);
        return new PaymentMethodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentMethodViewHolder holder, int position) {
        PaymentMethod method = paymentMethods.get(position);
        holder.tvMethodName.setText(method.getName());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPaymentMethodClick(method);
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentMethods.size();
    }

    static class PaymentMethodViewHolder extends RecyclerView.ViewHolder {
        TextView tvMethodName;

        public PaymentMethodViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMethodName = itemView.findViewById(R.id.tvMethodName);
        }
    }
}
