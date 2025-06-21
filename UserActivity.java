package com.myapp.pizzahut_admin;

import android.os.Bundle;
import android.util.Log;

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

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private List<User> userList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        recyclerViewUsers.setAdapter(userAdapter);

        db = FirebaseFirestore.getInstance();
        fetchUsersFromFirestore();
    }

    private void fetchUsersFromFirestore() {
        CollectionReference usersRef = db.collection("users");

        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                if (snapshot != null) {
                    userList.clear();
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        String firstName = document.getString("first_name");
                        String lastName = document.getString("last_name");
                        String email = document.getString("email");
                        String mobile = document.getString("mobile");

                        if (firstName != null && lastName != null && email != null && mobile != null) {
                            User user = new User(firstName, lastName, email, mobile);
                            userList.add(user);
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                }
            } else {
                Log.e("Firestore", "Error getting users", task.getException());
            }
        });
    }
}
