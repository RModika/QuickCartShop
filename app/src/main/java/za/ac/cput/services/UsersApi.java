package za.ac.cput.services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import za.ac.cput.model.User;
import za.ac.cput.model.UserAuth;

public interface UsersApi {

    @POST("/users/login")
    Call<User> loginUser(@Body UserAuth userAuth);

    @POST("/users/create")
    Call<User> registerUser(@Body User user);
}
