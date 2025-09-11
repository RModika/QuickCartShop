package za.ac.cput.services;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import za.ac.cput.model.Payment;
import za.ac.cput.model.PaymentDetails;

public interface PaymentApiService {

    // -------- Payment Endpoints --------
    @POST("/payment/create")
    Call<Payment> createPayment(@Body Payment payment);

    @GET("/payment/read/{id}")
    Call<Payment> getPayment(@Path("id") Long paymentId);

    @POST("/payment/update")
    Call<Payment> updatePayment(@Body Payment payment);

    @DELETE("/payment/delete/{id}")
    Call<Void> deletePayment(@Path("id") Long paymentId);

    @GET("/payment/getAll")
    Call<List<Payment>> getAllPayments();

    // -------- PaymentDetails Endpoints --------
    @POST("/paymentDetails/create")
    Call<PaymentDetails> createPaymentDetails(@Body PaymentDetails details);

    @GET("/paymentDetails/read/{id}")
    Call<PaymentDetails> getPaymentDetails(@Path("id") Long id);

    @POST("/paymentDetails/update")
    Call<PaymentDetails> updatePaymentDetails(@Body PaymentDetails details);

    @DELETE("/paymentDetails/delete/{id}")
    Call<Void> deletePaymentDetails(@Path("id") Long id);

    @GET("/paymentDetails/getAll")
    Call<List<PaymentDetails>> getAllPaymentDetails();
}
