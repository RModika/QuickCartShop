package za.ac.cput.services;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import za.ac.cput.model.Address;
import za.ac.cput.model.LoginResponse;
import za.ac.cput.model.ResetPasswordRequest;
import za.ac.cput.model.User;
import za.ac.cput.model.UserAuth;

public interface UsersApi {
    @POST("users/login")
    Call<LoginResponse> loginUser(@Body UserAuth userAuth);


//    @POST("users/login")
//    Call<User> loginUser(@Body UserAuth userAuth);

    @POST("users/create")
    Call<User> registerUser(@Body User user);

    @GET("users/read/{userId}")
    Call<User> getUserById(@Path("userId") Long userId);

    @PUT("users/update")    Call<User> updateUser(@Body User user);
    @GET("users/{userId}/addresses")
    Call<List<Address>> getAddressesByUserId(@Path("userId") Long userId);

    @PUT("addresses/{id}")
    Call<Address> updateAddress(@Path("id") Long addressId, @Body Address address);
    @GET("users/me")
    Call<User> getCurrentUser();
    @POST("users/request-password-reset")
    Call<ResponseBody> requestPasswordReset(@Body Map<String, String> body);
    @POST("users/reset-password")
    Call<ResponseBody> resetPassword(@Body ResetPasswordRequest body);
    @DELETE("users/{id}")
    Call<Boolean> deleteUser(@Path("id") Long userId);
}
