package com.myapp.pizzahut_admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminLoginActivity extends AppCompatActivity {

    private static final String admin_email = "akeshanawanjali23@gmail.com";
    private static final String admin_password = "Akeshan@12";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imageView = findViewById(R.id.imageView2);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        imageView.startAnimation(animation);

        EditText editTextEmail = findViewById(R.id.EditText1);
        EditText editTextPassword = findViewById(R.id.EditText2);
        Button signInButton = findViewById(R.id.button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    showAlert("Email is required.", () -> editTextEmail.requestFocus());
                    return;
                }
                if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    showAlert("Invalid email format.", () -> editTextEmail.requestFocus());
                    return;
                }
                if (password.isEmpty()) {
                    showAlert("Password is required.", () -> editTextPassword.requestFocus());
                    return;
                }
                if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
                    showAlert("Password must be at least 8 characters, contain letters, numbers, and a special character.", () -> editTextPassword.requestFocus());
                    return;
                }

                if (email.equals(admin_email) && password.equals(admin_password)) {
                   // showAlert("Login Successful!", () -> navigateToHome());
                    showToastAndNavigate("Sign in successful!");

                } else {
                    showAlert("Invalid email or password.", null);
                }
            }
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(AdminLoginActivity.this, ScreensActivity.class);
        startActivity(intent);
        finish();
    }

    private void showAlert(String message, Runnable onOkClick) {
        new AlertDialog.Builder(this)
                .setTitle("Message")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (onOkClick != null) {
                        onOkClick.run();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void showToastAndNavigate(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        new android.os.Handler().postDelayed(() -> {
            Intent intent = new Intent(AdminLoginActivity.this, ScreensActivity.class);
            startActivity(intent);
            finish();
        }, 1000);
    }

    private void showToastAndFocus(String message, EditText field) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        field.requestFocus();
    }
}
