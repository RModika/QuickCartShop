package za.ac.cput;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import za.ac.cput.ui.auth.SignUpActivity;

public class MainActivity extends AppCompatActivity {

    EditText editEmail, editPassword;
    Button btnLogin;

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

        // ✅ Make sure textRegister exists in your activity_main.xml
        TextView registerText = findViewById(R.id.textRegister);
        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(view -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Logging in as " + email, Toast.LENGTH_SHORT).show();
                // 🚀 Later: Navigate to home screen or verify login
            }
        });
    }



    private void setCategoryClick(int viewId, String categoryName) {
        ImageView category = findViewById(viewId);
        if (category != null) {
            category.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Animation scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
                        v.startAnimation(scaleDown);
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
                        v.startAnimation(scaleUp);

                        v.performClick(); // ✅ Ensures accessibility compliance
                        return true;
                }
                return false;
            });

            category.setOnClickListener(v ->
                    Toast.makeText(this, categoryName + " clicked", Toast.LENGTH_SHORT).show()
            );
        }
    }

}