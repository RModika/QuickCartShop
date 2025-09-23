package za.ac.cput.services;

import android.content.Context;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.ac.cput.model.Address;
import za.ac.cput.services.AddressApiService;
import za.ac.cput.services.ApiClient;

public class AddressService {

    public interface AddressCallback {
        void onSuccess();
        void onError(String error);
    }

    public static void saveAddress(Context context, Address address, AddressCallback callback) {
        AddressApiService api = ApiClient.getAddressApiService(context);
        Call<Address> call = api.createAddress(address);

        call.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Server error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                callback.onError("Request failed: " + t.getMessage());
            }
        });
    }
}
