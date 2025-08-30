package za.ac.cput.services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import za.ac.cput.model.Product;

import java.util.List;

public interface ProductApiService {
    @GET("Product/by-category/{categoryId}")
    Call<List<Product>> getProductsByCategory(@Path("categoryId") Long categoryId);

    @GET("Product/getAll")
    Call<List<Product>> getAllProducts();

    @GET("Product/image/{productId}")
    @Streaming
    Call<ResponseBody> getProductImage(@Path("productId") Long productId);
}