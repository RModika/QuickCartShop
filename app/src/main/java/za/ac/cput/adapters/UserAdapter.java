package za.ac.cput.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.ac.cput.R;
import za.ac.cput.model.User;
import za.ac.cput.services.ApiClient;
import za.ac.cput.services.UsersApi;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> users;
    private final Context context;

    public UserAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(users.get(position), position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail;
        Button btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvEmail = itemView.findViewById(R.id.tvUserEmail);
            btnDelete = itemView.findViewById(R.id.btnDeleteUser);
        }

        public void bind(User user, int position) {
            tvName.setText(user.getFirstName() + " " + user.getSurname());
            tvEmail.setText(user.getEmail());

            btnDelete.setOnClickListener(v -> new AlertDialog.Builder(context)
                    .setTitle("Delete User")
                    .setMessage("Are you sure you want to delete " + user.getFirstName() + "?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteUser(user, position))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show());
        }

        private void deleteUser(User user, int position) {
            UsersApi api = ApiClient.getUsersApi(context);

            Log.d("DELETE_USER", "Attempting to delete user with ID: " + user.getUserId());

            api.deleteUser(user.getUserId()).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                        Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show();
                        users.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, users.size());
                    } else if (response.code() == 401) {
                        Toast.makeText(context, "Unauthorized: Login required", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 404) {
                        Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Delete failed. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                        Log.e("DELETE_USER", "Response code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DELETE_USER", "Failure: ", t);
                }
            });
        }
    }
}
