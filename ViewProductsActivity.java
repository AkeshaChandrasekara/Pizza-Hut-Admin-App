package com.myapp.pizzahut_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewProductsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerViewPizzas, recyclerViewDesserts, recyclerViewDrinks;
    private PizzaAdapter pizzaAdapter;
    private DessertsAdapter dessertsAdapter;
    private DrinksAdapter drinksAdapter;

    private List<Product> pizzaList, dessertList, drinkList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_products);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        recyclerViewPizzas = findViewById(R.id.pizzaRecyclerView);
        recyclerViewDesserts = findViewById(R.id.dessertRecyclerView);
        recyclerViewDrinks = findViewById(R.id.drinksRecyclerView);


        pizzaList = new ArrayList<>();
        dessertList = new ArrayList<>();
        drinkList = new ArrayList<>();

        pizzaAdapter = new PizzaAdapter(this, pizzaList);
        dessertsAdapter = new DessertsAdapter(this, dessertList);
       drinksAdapter = new DrinksAdapter(this, drinkList);

        recyclerViewPizzas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPizzas.setAdapter(pizzaAdapter);

       recyclerViewDesserts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDesserts.setAdapter(dessertsAdapter);

        recyclerViewDrinks.setLayoutManager(new LinearLayoutManager(this));
       recyclerViewDrinks.setAdapter(drinksAdapter);

        Button addPizzaButton=findViewById(R.id.addPizzaButton);
        addPizzaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProductsActivity.this, AddPizzaActivity.class);
                startActivity(intent);
            }
        });

        Button addDessertButton=findViewById(R.id.addDessertButton);
        addDessertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProductsActivity.this, AddDessertActivity.class);
                startActivity(intent);
            }
        });

        Button addDrinksButton=findViewById(R.id.addDrinksButton);
        addDrinksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProductsActivity.this, AddDrinksActivity.class);
                startActivity(intent);
            }
        });

        loadPizzas();
        loadDesserts();
       loadDrinks();
    }

    private void loadPizzas() {
        db.collection("pizzas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pizzaList.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Product product = document.toObject(Product.class);
                                product.setImagePath(document.getString("imagePath"));
                                pizzaList.add(product);
                            }
                        }
                        pizzaAdapter.notifyDataSetChanged();
                    } else {
                       // Toast.makeText(HomeActivity.this, "Failed to load pizzas.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadDesserts() {
        db.collection("desserts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dessertList.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Product product = document.toObject(Product.class);
                                product.setImagePath(document.getString("imagePath"));
                                dessertList.add(product);
                            }
                        }
                        dessertsAdapter.notifyDataSetChanged();
                    } else {
                        //Toast.makeText(HomeActivity.this, "Failed to load desserts.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadDrinks() {
        db.collection("drinks")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        drinkList.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Product product = document.toObject(Product.class);
                                product.setImagePath(document.getString("imagePath"));
                                drinkList.add(product);
                            }
                        }
                        drinksAdapter.notifyDataSetChanged();
                    } else {
                       // Toast.makeText(HomeActivity.this, "Failed to load drinks.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}