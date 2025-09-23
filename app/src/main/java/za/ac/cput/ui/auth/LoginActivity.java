package za.ac.cput.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.ac.cput.R;
import za.ac.cput.model.User;
import za.ac.cput.model.UserAuth;
import za.ac.cput.services.ApiClient;
import za.ac.cput.services.UsersApi;
import za.ac.cput.ui.home.HomeActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private Button btnLogin;
    private UsersApi usersApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView registerText = findViewById(R.id.textRegister);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);

        usersApi = ApiClient.getClientWithBasicAuth(this).create(UsersApi.class);


        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(view -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            } else {
                performLogin(email, password);
            }
        });
    }

private void performLogin(String email, String password) {
    UserAuth loginRequest = new UserAuth(email, password);

    Call<User> call = usersApi.loginUser(loginRequest);
    call.enqueue(new Callback<User>() {
        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if (response.isSuccessful() && response.body() != null) {
                User user = response.body();

                // Save user details to SharedPreferences (without cookie)
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("userId", user.getUserId());
                editor.putString("firstName", user.getFirstName());
                editor.putString("surname", user.getSurname());
                editor.putString("email", user.getEmail());
                editor.putString("password", password);  // Save password here for Basic Auth
                editor.putString("phoneNumber", user.getPhoneNumber());
                editor.apply();

                Toast.makeText(LoginActivity.this, "Welcome " + user.getFirstName(), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("firstName", user.getFirstName());
                intent.putExtra("surname", user.getSurname());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Login failed. Check credentials.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<User> call, Throwable t) {
            Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
        }
    });
}

}


