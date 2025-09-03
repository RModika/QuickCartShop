package za.ac.cput.services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import za.ac.cput.model.User;
import za.ac.cput.model.UserAuth;

public interface UsersApi {

    @POST("users/login")
    Call<User> loginUser(@Body UserAuth userAuth);

    @POST("users/create")
    Call<User> registerUser(@Body User user);

    @GET("users/read/{userId}")
    Call<User> getUserById(@Path("userId") Long userId);

    @PUT("users/update")
    Call<User> updateUser(@Body User user);


}