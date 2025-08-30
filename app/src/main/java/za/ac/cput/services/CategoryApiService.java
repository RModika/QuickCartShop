package za.ac.cput.services;
import retrofit2.Call;
import retrofit2.http.*;
import za.ac.cput.model.Category;
import java.util.List;

public interface CategoryApiService {
    @GET("Category/getAll")
    Call<List<Category>> getAllCategories();

    @GET("Category/read/{categoryId}")
    Call<Category> getCategory(@Path("categoryId") Long categoryId);

    @POST("Category/create")
    Call<Category> createCategory(@Body Category category);

    @PUT("Category/update")
    Call<Category> updateCategory(@Body Category category);

    @DELETE("Category/delete/{categoryId}")
    Call<Void> deleteCategory(@Path("categoryId") Long categoryId);
}
