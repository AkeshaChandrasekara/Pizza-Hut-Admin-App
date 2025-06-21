package com.myapp.pizzahut_admin;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class OrderUpdateActivity extends AppCompatActivity {

    private Spinner spinnerOrderStatus;
    private Button btnSendUpdate;
    private String phoneNumber;

    private static final int SMS_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_update);

        spinnerOrderStatus = findViewById(R.id.spinnerOrderStatus);
        btnSendUpdate = findViewById(R.id.btnSendUpdate);

        phoneNumber = getIntent().getStringExtra("phoneNumber");

        String[] orderUpdates = {"Select","Order is being prepared", "Order is baked", "Order is out for delivery", "Order is delivered"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, orderUpdates);
        spinnerOrderStatus.setAdapter(adapter);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        }

        btnSendUpdate.setOnClickListener(v -> sendOrderUpdate());
    }

    private void sendOrderUpdate() {
        String orderUpdate = spinnerOrderStatus.getSelectedItem().toString();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, "Pizza Hut Update: " + orderUpdate, null, null);
            Toast.makeText(this, "Order update sent!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}