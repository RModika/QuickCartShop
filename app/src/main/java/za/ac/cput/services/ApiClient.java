package za.ac.cput.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/mobileApp/";
    private static Retrofit retrofit = null;

    // Default Retrofit client without any auth
    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Manual token injection
    public static Retrofit getClientWithToken(String token) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // Automatically fetch token from SharedPreferences
    public static Retrofit getClientWithTokenFromPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        if (token != null && !token.isEmpty()) {
            clientBuilder.addInterceptor(chain -> {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            });
        }

        OkHttpClient client = clientBuilder.build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // API service getters — now using token-based client
    public static CartApiService getCartApiService(Context context) {
        return getClientWithTokenFromPrefs(context).create(CartApiService.class);
    }

    public static CategoryApiService getCategoryApiService(Context context) {
        return getClientWithTokenFromPrefs(context).create(CategoryApiService.class);
    }

    public static ProductApiService getProductApiService(Context context) {
        return getClientWithTokenFromPrefs(context).create(ProductApiService.class);
    }

    public static OrderApiService getOrderApiService(Context context) {
        return getClientWithTokenFromPrefs(context).create(OrderApiService.class);
    }

    public static PaymentApiService getPaymentApiService(Context context) {
        return getClientWithTokenFromPrefs(context).create(PaymentApiService.class);
    }

    public static UsersApi getUsersApi(Context context) {
        return getClientWithTokenFromPrefs(context).create(UsersApi.class);
    }

    public static AdminProductApiService getAdminProductApiService(Context context) {
        return getClientWithTokenFromPrefs(context).create(AdminProductApiService.class);
    }

    public static AddressApiService getAddressApiService(Context context) {
        return getClientWithTokenFromPrefs(context).create(AddressApiService.class);
    }
}
////package za.ac.cput.services;
////
////import okhttp3.Credentials;
////import okhttp3.Interceptor;
////import okhttp3.OkHttpClient;
////import okhttp3.Request;
////import okhttp3.Response;
////import okhttp3.logging.HttpLoggingInterceptor;
////import retrofit2.Retrofit;
////import retrofit2.converter.gson.GsonConverterFactory;
////
////import java.io.IOException;
////import java.util.concurrent.TimeUnit;
////
////public class ApiClient {
////
////    private static final String BASE_URL = "http://10.0.2.2:8080/mobileApp/";
////    private static Retrofit retrofit = null;
////
////    public static Retrofit getClient() {
////        if (retrofit == null) {
////            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
////            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
////
////            OkHttpClient client = new OkHttpClient.Builder()
////                    .addInterceptor(logging)
////                    .connectTimeout(30, TimeUnit.SECONDS)
////                    .readTimeout(30, TimeUnit.SECONDS)
////                    .writeTimeout(30, TimeUnit.SECONDS)
////                    .build();
////
////            retrofit = new Retrofit.Builder()
////                    .baseUrl(BASE_URL)
////                    .client(client)
////                    .addConverterFactory(GsonConverterFactory.create())
////                    .build();
////        }
////        return retrofit;
////    }
////
////    public static CartApiService getCartApiService() {
////        return getClient().create(CartApiService.class);
////    }
////
////    public static CategoryApiService getCategoryApiService() {
////        return getClient().create(CategoryApiService.class);
////    }
////
////    public static ProductApiService getProductApiService() {
////        return getClient().create(ProductApiService.class);
////    }
////    public static Retrofit getClientWithBasicAuth(String email, String password) {
////        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
////        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
////
////        OkHttpClient client = new OkHttpClient.Builder()
////                .addInterceptor(new BasicAuthInterceptor(email, password))
////                .addInterceptor(logging)
////                .connectTimeout(30, TimeUnit.SECONDS)
////                .readTimeout(30, TimeUnit.SECONDS)
////                .writeTimeout(30, TimeUnit.SECONDS)
////                .build();
////
////        return new Retrofit.Builder()
////                .baseUrl(BASE_URL)
////                .client(client)
////                .addConverterFactory(GsonConverterFactory.create())
////                .build();
////    }
////
////    private static class BasicAuthInterceptor implements Interceptor {
////        private final String credentials;
////
////        public BasicAuthInterceptor(String user, String password) {
////            this.credentials = Credentials.basic(user, password);
////        }
////
////        @Override
////        public Response intercept(Chain chain) throws IOException {
////            Request request = chain.request();
////            Request authenticatedRequest = request.newBuilder()
////                    .header("Authorization", credentials)
////                    .build();
////            return chain.proceed(authenticatedRequest);
////        }
////        // Helper method for Payment API
////        public static PaymentApiService getPaymentApiService() {
////            return getClient().create(PaymentApiService.class);
////        }
////
////    }
////
////}
//package za.ac.cput.services;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
//import okhttp3.Credentials;
//import okhttp3.Interceptor;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import okhttp3.logging.HttpLoggingInterceptor;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//import java.io.IOException;
//import java.util.concurrent.TimeUnit;
//
//public class ApiClient {
//
//    private static final String BASE_URL = "http://10.0.2.2:8080/mobileApp/";
//    private static Retrofit retrofit = null;
//
//    public static Retrofit getClient() {
//        if (retrofit == null) {
//            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//            OkHttpClient client = new OkHttpClient.Builder()
//                    .addInterceptor(logging)
//                    .connectTimeout(30, TimeUnit.SECONDS)
//                    .readTimeout(30, TimeUnit.SECONDS)
//                    .writeTimeout(30, TimeUnit.SECONDS)
//                    .build();
//
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .client(client)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//        }
//        return retrofit;
//    }
//    public static Retrofit getClientWithToken(String token) {
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(logging)
//                .addInterceptor(chain -> {
//                    Request original = chain.request();
//                    Request request = original.newBuilder()
//                            .header("Authorization", "Bearer " + token)
//                            .method(original.method(), original.body())
//                            .build();
//                    return chain.proceed(request);
//                })
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .build();
//
//        return new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .client(client)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//    }
//
//    public static Retrofit getClientWithBasicAuth(Context context) {
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
//        String email = prefs.getString("email", null);
//        String password = prefs.getString("password", null);
//
//        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
//                .addInterceptor(logging)
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS);
//
//        if (email != null && password != null) {
//            clientBuilder.addInterceptor(new BasicAuthInterceptor(email, password));
//        }
//
//        OkHttpClient client = clientBuilder.build();
//
//
//        return new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .client(client)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//    }
//
//    private static class BasicAuthInterceptor implements Interceptor {
//        private final String credentials;
//
//        public BasicAuthInterceptor(String user, String password) {
//            this.credentials = Credentials.basic(user, password);
//        }
//
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            Request request = chain.request();
//            Request authenticatedRequest = request.newBuilder()
//                    .header("Authorization", credentials)
//                    .build();
//            return chain.proceed(authenticatedRequest);
//        }
//    }
//
//    // API service getters
//    public static CartApiService getCartApiService(Context context) {
//        return getClientWithBasicAuth(context).create(CartApiService.class);
//    }
//
//    public static CategoryApiService getCategoryApiService(Context context) {
//        return getClientWithBasicAuth(context).create(CategoryApiService.class);
//    }
//
//    public static ProductApiService getProductApiService(Context context) {
//        return getClientWithBasicAuth(context).create(ProductApiService.class);
//    }
//    public static OrderApiService getOrderApiService(Context context) {
//        return getClientWithBasicAuth(context).create(OrderApiService.class);
//    }
//
//    public static PaymentApiService getPaymentApiService(Context context) {
//        return getClientWithBasicAuth(context).create(PaymentApiService.class);
//    }
//
//    public static UsersApi getUsersApi(Context context) {
//        return getClientWithBasicAuth(context).create(UsersApi.class);
//    }
//
//    public static AdminProductApiService getAdminProductApiService(Context context) {
//        return getClientWithBasicAuth(context).create(AdminProductApiService.class);
//    }
//    public static AddressApiService getAddressApiService(Context context) {
//        return getClientWithBasicAuth(context).create(AddressApiService.class);
//    }
//
//
//}
//
//
//
//
