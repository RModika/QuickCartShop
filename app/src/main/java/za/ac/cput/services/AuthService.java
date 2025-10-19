package za.ac.cput.services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import za.ac.cput.model.User;
import za.ac.cput.model.UserAuth;

public interface AuthService {

    // Regular user login
    @POST("/mobileApp/users/login")
    Call<User> login(@Body UserAuth loginRequest);

    // Admin login - only works for ADMIN role users
    @POST("/mobileApp/admin/login")
    Call<User> adminLogin(@Body UserAuth loginRequest);

    // Check if user is admin
    @GET("/mobileApp/admin/check")
    Call<Boolean> checkAdmin(@Header("User-Id") Long userId);
}