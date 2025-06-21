package com.myapp.pizzahut_admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    private RecyclerView recyclerViewOrders;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_order);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        recyclerViewOrders = findViewById(R.id.recyclerView);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList);
        recyclerViewOrders.setAdapter(orderAdapter);

        db = FirebaseFirestore.getInstance();
        fetchOrdersFromFirestore();
    }

    private void fetchOrdersFromFirestore() {
        CollectionReference ordersRef = db.collection("orders");

        ordersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                if (snapshot != null) {
                    orderList.clear();
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        try {
                            String orderId = document.getString("orderId");
                            String email = document.getString("userEmail");
                            String address = document.getString("address");
                            double total = document.getDouble("totalPrice");
                            double finalAmount = document.getDouble("finalAmount");
                            String status = document.getString("status");

                            List<CartItem> items = new ArrayList<>();
                            List<Object> itemObjects = (List<Object>) document.get("items");

                            if (itemObjects != null) {
                                for (Object obj : itemObjects) {
                                    String json = new Gson().toJson(obj);
                                    CartItem cartItem = new Gson().fromJson(json, CartItem.class);
                                    items.add(cartItem);
                                }
                            }

                            if (orderId != null && email != null && address != null && status != null) {
                                Order order = new Order(orderId, email, address, total, finalAmount, status, items);
                                orderList.add(order);
                            }
                        } catch (Exception e) {
                            Log.e("Firestore", "Error parsing order: " + e.getMessage());
                        }
                    }
                    orderAdapter.notifyDataSetChanged();
                }
            } else {
                Log.e("Firestore", "Error fetching orders", task.getException());
            }
        });
    }
}
