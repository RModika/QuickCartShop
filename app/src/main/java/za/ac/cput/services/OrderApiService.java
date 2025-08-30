package za.ac.cput.services;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import za.ac.cput.model.CustomerOrder;

public interface OrderApiService {

    @GET("orders") // adjust the endpoint to your backend
    Call<List<CustomerOrder>> getOrders();
}
