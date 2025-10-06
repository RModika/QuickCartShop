package za.ac.cput.ui.auth;

import za.ac.cput.model.ResetPasswordRequest;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.ac.cput.R;
import za.ac.cput.services.ApiClient;
import za.ac.cput.services.UsersApi;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText editNewPassword;
    private Button btnSubmit;
    private String token;
    private UsersApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        editNewPassword = findViewById(R.id.editNewPassword);
        btnSubmit = findViewById(R.id.btnSubmit);

        api = ApiClient.getClient().create(UsersApi.class);  // <-- initialize here properly

        // Extract token from the deep link
        if (getIntent() != null && getIntent().getData() != null) {
            token = getIntent().getData().getQueryParameter("token");
        }

        if (token == null) {
            Toast.makeText(this, "Invalid reset link", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnSubmit.setOnClickListener(v -> {
            String newPassword = editNewPassword.getText().toString().trim();

            if (newPassword.isEmpty()) {
                Toast.makeText(this, "Enter new password", Toast.LENGTH_SHORT).show();
                return;
            }

            ResetPasswordRequest request = new ResetPasswordRequest(token, newPassword);  // <-- model class

            api.resetPassword(request).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ResetPasswordActivity.this, "Password reset successful", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, "Failed to reset password", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(ResetPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
