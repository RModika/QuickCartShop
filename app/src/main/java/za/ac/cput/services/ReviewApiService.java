package za.ac.cput.services;

import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;
import za.ac.cput.model.Review;

public interface ReviewApiService {

    @POST("Review/create")
    Call<Review> createReview(@Body Review review);

    @GET("Review/read/{reviewId}")
    Call<Review> readReview(@Path("reviewId") Long reviewId);

    @POST("Review/update")
    Call<Review> updateReview(@Body Review review);

    @DELETE("Review/delete/{reviewId}")
    Call<Void> deleteReview(@Path("reviewId") Long reviewId);

    @GET("Review/getAll")
    Call<List<Review>> getAllReviews();

    @GET("Review/getByUserId/{userId}")
    Call<List<Review>> getReviewsByUserId(@Path("userId") Long userId);

    @GET("Review/getByCategoryId/{categoryId}")
    Call<List<Review>> getReviewsByCategoryId(@Path("categoryId") Long categoryId);
    @Multipart
    @POST("Review/create")
    Call<Review> createReviewWithImage(
            @Part("review") RequestBody reviewJson,                    // JSON string
            @Part MultipartBody.Part image                             // optional image
    );
}
