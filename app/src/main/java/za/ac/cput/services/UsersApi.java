package za.ac.cput.services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import za.ac.cput.model.User;
import za.ac.cput.model.UserAuth;
import retrofit2.http.PUT;

public interface UsersApi {

    @POST("/users/login")
    Call<User> loginUser(@Body UserAuth userAuth);

    @POST("/users/create")
    Call<User> registerUser(@Body User user);

    @PUT("/users/update")
    Call<User> updateUser(@Body User user); // <-- Add this line
}

