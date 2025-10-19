package za.ac.cput.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.ac.cput.R;
import za.ac.cput.model.User;
import za.ac.cput.services.ApiClient;
import za.ac.cput.services.UsersApi;
import za.ac.cput.ui.product.AdminProductManagementActivity;

public class ViewUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);

        // Set up toolbar header
        Toolbar toolbar = findViewById(R.id.toolbarUsers);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Users");
        }
        // Back button listener
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(ViewUsersActivity.this, AdminProductManagementActivity.class);
            startActivity(intent);
            finish();
        });

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserAdapter(userList, this);
        recyclerView.setAdapter(adapter);

        loadUsers();
    }

    private void loadUsers() {
        UsersApi api = ApiClient.getUsersApi(this);
        api.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ViewUsersActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
                    Log.e("LOAD_USERS", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(ViewUsersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LOAD_USERS", "Failure: ", t);
            }
        });
    }

    // Adapter inner class
    public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

        private final List<User> users;
        private final ViewUsersActivity context;

        public UserAdapter(List<User> users, ViewUsersActivity context) {
            this.users = users;
            this.context = context;
        }

        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(UserViewHolder holder, int position) {
            User user = users.get(position);
            holder.bind(user, position);
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        public class UserViewHolder extends RecyclerView.ViewHolder {
            android.widget.TextView tvName, tvEmail;
            android.widget.Button btnDelete;

            public UserViewHolder(android.view.View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvUserName);
                tvEmail = itemView.findViewById(R.id.tvUserEmail);
                btnDelete = itemView.findViewById(R.id.btnDeleteUser);
            }

            public void bind(User user, int position) {
                tvName.setText(user.getFirstName() + " " + user.getSurname());
                tvEmail.setText(user.getEmail());

                btnDelete.setOnClickListener(v -> {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete User")
                            .setMessage("Are you sure you want to delete " + user.getFirstName() + "?")
                            .setPositiveButton("Yes", (dialog, which) -> deleteUser(user, position))
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .show();
                });
            }
        }
    }

    private void deleteUser(User user, int position) {
        UsersApi api = ApiClient.getUsersApi(this);

        Log.d("DELETE_USER", "Attempting to delete user with ID: " + user.getUserId());

        api.deleteUser(user.getUserId()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                    Toast.makeText(ViewUsersActivity.this, "User deleted", Toast.LENGTH_SHORT).show();
                    userList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, userList.size());
                } else if (response.code() == 401) {
                    Toast.makeText(ViewUsersActivity.this, "Unauthorized: Login required", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 404) {
                    Toast.makeText(ViewUsersActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ViewUsersActivity.this, "Delete failed. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("DELETE_USER", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(ViewUsersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("DELETE_USER", "Failure: ", t);
            }
        });
    }
}
