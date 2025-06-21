package com.myapp.pizzahut_admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {
    private static final int LOAD_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView logo = findViewById(R.id.imageView);
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0.9f, 1.0f,
                0.9f, 1.0f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.9f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.9f
        );
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);
        logo.startAnimation(scaleAnimation);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
            startActivity(intent);
            finish();
        }, LOAD_TIME);
    }
}