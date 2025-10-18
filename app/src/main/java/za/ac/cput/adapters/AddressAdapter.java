package za.ac.cput.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import za.ac.cput.R;
import za.ac.cput.model.Address;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    public interface OnAddressClickListener {
        void onAddressClick(Address address);
    }

    private List<Address> addressList;
    private OnAddressClickListener listener;

    public AddressAdapter(List<Address> addressList, OnAddressClickListener listener) {
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.bind(address);
    }

    @Override
    public int getItemCount() {
        return addressList != null ? addressList.size() : 0;
    }

    class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullAddress;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullAddress = itemView.findViewById(R.id.tvFullAddress);
        }

        public void bind(final Address address) {
            String fullAddress = address.getStreetNumber() + " " + address.getStreetName() + ", "
                    + address.getSuburb() + ", " + address.getCity() + ", " + address.getPostalCode();
            tvFullAddress.setText(fullAddress);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddressClick(address);
                }
            });
        }
    }
}
