package com.myapp.pizzahut_admin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    private EditText editTextMessage;
    private Button buttonSend;
    private FirebaseFirestore db;
    private DatabaseHelper databaseHelper;
    private static final String PHONE_NUMBER = "0754027915";
    private static final int SMS_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextMessage = findViewById(R.id.editTextTextMultiLine);
        buttonSend = findViewById(R.id.button11);
        db = FirebaseFirestore.getInstance();
        databaseHelper = new DatabaseHelper(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        }

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNotificationAndSendSMS();
            }
        });
    }

    private void saveNotificationAndSendSMS() {
        String message = editTextMessage.getText().toString().trim();

        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message!", Toast.LENGTH_SHORT).show();
            return;
        }

        Timestamp timestamp = Timestamp.now();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(timestamp.toDate());


        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "Pizza Hut");
        notification.put("message", message);
        notification.put("date", formattedDate);
        notification.put("timestamp", timestamp);

        db.collection("notifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    sendSMS(PHONE_NUMBER, message, formattedDate);
                    editTextMessage.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(NotificationActivity.this, "Failed to save notification", Toast.LENGTH_SHORT).show());

        boolean isInserted = databaseHelper.insertNotification("Pizza Hut", message, formattedDate);
        if (isInserted) {
           // Toast.makeText(this, "Notification saved to SQLite", Toast.LENGTH_SHORT).show();
        } else {
           // Toast.makeText(this, "Failed to save to SQLite", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSMS(String phoneNumber, String message, String date) {
        String smsBody = "📢 Pizza Hut\n" + date + "\n" + message;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(smsBody);
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
            Toast.makeText(this, "Notification Sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "SMS failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            openMessagingApp(phoneNumber, smsBody);
        }
    }

    private void openMessagingApp(String phoneNumber, String message) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setData(Uri.parse("smsto:" + phoneNumber));
        smsIntent.putExtra("sms_body", message);
        startActivity(smsIntent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
