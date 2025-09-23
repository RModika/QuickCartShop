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

    // Add item to cart
    @POST("cart-items/create")
    Call<CartItem> addToCart(@Body CartItem cartItem);

    // Get all cart items
    @GET("cart-items/getAll")
    Call<List<CartItem>> getAllCartItems();

    // Read a single cart item by ID
    @GET("cart-items/read/{id}")
    Call<CartItem> getCartItem(@Path("id") Long id);

    // Update cart item
    @PUT("cart-items/update")
    Call<CartItem> updateCartItem(@Body CartItem cartItem);

    // Delete cart item by ID
    @DELETE("cart-items/delete/{id}")
    Call<Void> deleteCartItem(@Path("id") Long id);
}