//package za.ac.cput.ui.home;
//
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import za.ac.cput.adapters.OrdersAdapter;
//
//public class OrdersActivity extends AppCompatActivity {
//
//    private RecyclerView recyclerOrders;
//    private OrdersAdapter ordersAdapter;
//    private ProgressBar progressBar;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_orders);
//
//        recyclerOrders = findViewById(R.id.recyclerOrders);
//        progressBar = findViewById(R.id.progressBar);
//
//        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
//        ordersAdapter = new OrdersAdapter();
//        recyclerOrders.setAdapter(ordersAdapter);
//
//        fetchOrders();
//    }
//
//    private void fetchOrders() {
//        progressBar.setVisibility(View.VISIBLE);
//
//        ApiService apiService = ApiClient.getClient().create(ApiService.class);
//        apiService.getAllOrders().enqueue(new Callback<List<CustomerOrder>>() {
//            @Override
//            public void onResponse(Call<List<CustomerOrder>> call, Response<List<CustomerOrder>> response) {
//                progressBar.setVisibility(View.GONE);
//                if (response.isSuccessful() && response.body() != null) {
//                    ordersAdapter.setData(response.body());
//                } else {
//                    Toast.makeText(OrdersActivity.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<CustomerOrder>> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
//                Toast.makeText(OrdersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//}


package za.ac.cput.ui.home;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.ac.cput.R;
import za.ac.cput.adapters.OrdersAdapter;
import za.ac.cput.model.CustomerOrder;
import za.ac.cput.services.ApiClient;
import za.ac.cput.services.ApiService;

public class OrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrdersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrdersAdapter();
        recyclerView.setAdapter(adapter);

        fetchOrders();
    }

    private void fetchOrders() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<CustomerOrder>> call = apiService.getOrders();
        call.enqueue(new Callback<List<CustomerOrder>>() {
            @Override
            public void onResponse(Call<List<CustomerOrder>> call, Response<List<CustomerOrder>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setData(response.body());
                } else {
                    Toast.makeText(OrdersActivity.this, "No orders found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CustomerOrder>> call, Throwable t) {
                Toast.makeText(OrdersActivity.this, "Failed to fetch orders", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
