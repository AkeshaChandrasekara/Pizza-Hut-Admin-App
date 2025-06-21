package com.myapp.pizzahut_admin;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ScreensActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screens);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        CardView menuManagementCard = findViewById(R.id.menuManagementCard);
        CardView userManagementCard = findViewById(R.id.userManagementCard);
        CardView orderManagementCard = findViewById(R.id.orderManagementCard);
        CardView sendNotificationsCard = findViewById(R.id.sendNotificationsCard);
        CardView salesManagementCard = findViewById(R.id.salesManagementCard);


        menuManagementCard.setOnClickListener(v -> {
            Intent intent = new Intent(ScreensActivity.this, ViewProductsActivity.class);
            startActivity(intent);
        });

        userManagementCard.setOnClickListener(v -> {
            Intent intent = new Intent(ScreensActivity.this, UserActivity.class);
            startActivity(intent);
        });

        orderManagementCard.setOnClickListener(v -> {
            Intent intent = new Intent(ScreensActivity.this, OrderActivity.class);
            startActivity(intent);
        });

        sendNotificationsCard.setOnClickListener(v -> {
            Intent intent = new Intent(ScreensActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        salesManagementCard.setOnClickListener(v -> {
            Intent intent = new Intent(ScreensActivity.this, SalesActivity.class);
            startActivity(intent);
        });
    }
}