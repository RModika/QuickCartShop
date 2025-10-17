package za.ac.cput.services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import za.ac.cput.model.AdminLoginResponse;
import za.ac.cput.model.LoginResponse;
import za.ac.cput.model.UserAuth;

public interface AdminApiService {
    @POST("admin/login")
    Call<AdminLoginResponse> loginAdmin(@Body UserAuth auth);
}
