package za.ac.cput.services;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;

public class AuthInterceptor implements Interceptor {
    private final String token;

    public AuthInterceptor(String token) {
        this.token = token;
    }

    @Override
    public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder();
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        Request request = builder.build();

        // Log request details
        Log.d("AuthInterceptor", "Request URL: " + request.url());
        Log.d("AuthInterceptor", "Request headers: " + request.headers().toString());

        okhttp3.Response response = chain.proceed(request);

        // Log response status
        Log.d("AuthInterceptor", "Response code: " + response.code());
        Log.d("AuthInterceptor", "Response headers: " + response.headers().toString());
        return response;
    }
}

