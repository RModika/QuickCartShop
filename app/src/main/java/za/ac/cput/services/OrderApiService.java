package za.ac.cput.services;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import za.ac.cput.model.CustomerOrder;

public interface OrderApiService {

    @GET("CustomerOrder/getAll")
    Call<List<CustomerOrder>> getOrders();

//    @POST("CustomerOrder/create")
//    Call<CustomerOrder> createOrder(@Body CustomerOrder order);
}
