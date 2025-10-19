package za.ac.cput.services;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import za.ac.cput.model.Product;

public interface AdminProductApiService {

    // Get all products
    @GET("Product/getAll")
    Call<List<Product>> getAllProducts();

    // Delete product by ID
    @DELETE("Product/delete/{id}")
    Call<Void> deleteProduct(@Path("id") Long productId);

    // Create a product (JSON request, without image)
    @POST("Product/create")
    Call<Product> createProduct(@Body Product product);

    // Create a product with an image (Multipart)
    @Multipart
    @POST("Product/create-with-image")
    Call<Product> createProductWithImage(
            @Part("productName") RequestBody productName,
            @Part("productDescription") RequestBody productDescription,
            @Part("productPrice") RequestBody productPrice,
            @Part("stockAvailability") RequestBody stockAvailability,
            @Part("categoryId") RequestBody categoryId,
            @Part MultipartBody.Part image
    );

    // ✅ ADD THESE UPDATE METHODS:

    // Update a product (JSON request, without image)
    @POST("Product/update")
    Call<Product> updateProduct(@Body Product product);

    // Update a product with an image (Multipart)
    @Multipart
    @POST("Product/update-with-image/{productId}")
    Call<Product> updateProductWithImage(
            @Path("productId") Long productId,
            @Part("productName") RequestBody productName,
            @Part("productDescription") RequestBody productDescription,
            @Part("productPrice") RequestBody productPrice,
            @Part("stockAvailability") RequestBody stockAvailability,
            @Part("categoryId") RequestBody categoryId,
            @Part MultipartBody.Part image
    );
}