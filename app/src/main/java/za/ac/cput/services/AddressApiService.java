package za.ac.cput.services;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import za.ac.cput.model.Address;
import za.ac.cput.model.AddressDTO;

public interface AddressApiService {

    @POST("Address/create")
    Call<Address> createAddress(@Body Address address);

    @PUT("Address/update")
    Call<Address> updateAddress(@Body AddressDTO addressDTO);

    @GET("Address/user/{userId}")
    Call<List<Address>> getUserAddresses(@Path("userId") Long userId);

    @GET("Address/read/{id}")
    Call<Address> getAddressById(@Path("id") Long id);

    @GET("Address/getAll")
    Call<List<Address>> getAllAddresses();

    @DELETE("Address/delete/{id}")
    Call<Void> deleteAddress(@Path("id") Long id);
}
