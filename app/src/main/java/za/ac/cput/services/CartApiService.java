package za.ac.cput.services;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import za.ac.cput.model.CartItem;

public interface CartApiService {

    @GET("cart")
    Call<List<CartItem>> getCartItems();

    @POST("cart")
    Call<CartItem> addCartItem(@Body CartItem item);

    @PUT("cart/{id}")
    Call<CartItem> updateCartItem(@Path("id") int id, @Body CartItem item);

    @DELETE("cart/{id}")
    Call<Void> deleteCartItem(@Path("id") int id);
}
