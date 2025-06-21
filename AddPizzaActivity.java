package com.myapp.pizzahut_admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddPizzaActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageButton addPizzaImageButton;
    private String currentProductId = "";
    private FirebaseFirestore firebaseFirestore;
    private EditText productNameText, productDescriptionText, productPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_pizza);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        firebaseFirestore = FirebaseFirestore.getInstance();

        addPizzaImageButton = findViewById(R.id.imageButton);
        productNameText = findViewById(R.id.Text1);
        productDescriptionText = findViewById(R.id.Text2);
        productPrice = findViewById(R.id.Text3);
        Button addPizzaButton = findViewById(R.id.button6);
        Button updatePizzaButton = findViewById(R.id.button7);

        addPizzaImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        addPizzaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndAddPizza();
            }
        });

        updatePizzaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentProductId.isEmpty()) {
                    showUpdateDialog();
                } else {
                    updatePizza();
                }
            }
        });

    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            addPizzaImageButton.setImageURI(imageUri);
        }
    }

    private String saveImageToSharedStorage(Uri uri, String productId) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);


            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, "Pizza_" + productId + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            outputStream.flush();
            outputStream.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            showAlert("Error saving image: " + e.getMessage());
            return "";
        }
    }

    private void validateAndAddPizza() {
        String productName = productNameText.getText().toString().trim();
        String productDescription = productDescriptionText.getText().toString().trim();
        String priceText = productPrice.getText().toString().trim();

        if (productName.isEmpty()) {
            showAlert("Product name cannot be empty!");
            return;
        }

        if (productDescription.isEmpty()) {
            showAlert("Product description cannot be empty!");
            return;
        }

        if (priceText.isEmpty()) {
            showAlert("Product price cannot be empty!");
            return;
        }

        if (imageUri == null) {
            showAlert("Please select an image for the pizza.");
            return;
        }

        String productId = generateProductId();
        String imagePath = saveImageToSharedStorage(imageUri, productId);
        if (imagePath.isEmpty()) {
            showAlert("Failed to save image.");
            return;
        }

        Map<String, Object> pizza = new HashMap<>();
        pizza.put("productId", productId);
        pizza.put("name", productName);
        pizza.put("description", productDescription);
        pizza.put("price", priceText);
        pizza.put("imagePath", imagePath);

        firebaseFirestore.collection("pizzas").document(productId)
                .set(pizza)
                .addOnSuccessListener(documentReference -> {
                    showAlert("Pizza added successfully!");
                    resetForm();
                })
                .addOnFailureListener(e -> showAlert("Error adding pizza: " + e.getMessage()));
    }

    private void resetForm() {
        productNameText.setText("");
        productDescriptionText.setText("");
        productPrice.setText("");
        addPizzaImageButton.setImageResource(R.drawable.baseline_add_photo_alternate_24);
        imageUri = null;
        currentProductId = "";
    }

    private String generateProductId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder productId = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            productId.append(chars.charAt(random.nextInt(chars.length())));
        }
        return productId.toString();
    }

    private void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Pizza");
        builder.setMessage("Enter the Product ID of the pizza you want to update:");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String productId = input.getText().toString().trim();
            if (!productId.isEmpty()) {
                fetchPizzaDetails(productId);
            } else {
                showAlert("Please enter a valid Product ID.");
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void fetchPizzaDetails(String productId) {
        firebaseFirestore.collection("pizzas").document(productId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentProductId = productId;
                        productNameText.setText(documentSnapshot.getString("name"));
                        productDescriptionText.setText(documentSnapshot.getString("description"));
                        productPrice.setText(documentSnapshot.getString("price"));

                        String imagePath = documentSnapshot.getString("imagePath");
                        if (imagePath != null && !imagePath.isEmpty()) {
                            File imgFile = new File(imagePath);
                            if (imgFile.exists()) {
                                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                addPizzaImageButton.setImageBitmap(myBitmap);
                            }
                        }
                    } else {
                        showAlert("No pizza found with this ID.");
                    }
                })
                .addOnFailureListener(e -> showAlert("Error fetching pizza details: " + e.getMessage()));
    }

    private void updatePizza() {
        if (currentProductId.isEmpty()) {
            showAlert("No product selected for update.");
            return;
        }

        String productName = productNameText.getText().toString().trim();
        String productDescription = productDescriptionText.getText().toString().trim();
        String priceText = productPrice.getText().toString().trim();

        if (productName.isEmpty()) {
            showAlert("Product name cannot be empty!");
            return;
        }

        if (productDescription.isEmpty()) {
            showAlert("Product description cannot be empty!");
            return;
        }

        if (priceText.isEmpty()) {
            showAlert("Product price cannot be empty!");
            return;
        }

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", productName);
        updatedData.put("description", productDescription);
        updatedData.put("price", priceText);

        if (imageUri != null) {
            String imagePath = saveImageToSharedStorage(imageUri, currentProductId);
            if (!imagePath.isEmpty()) {
                updatedData.put("imagePath", imagePath);
            }
        }

        firebaseFirestore.collection("pizzas").document(currentProductId)
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    showAlert("Pizza updated successfully!");
                    resetForm();
                })
                .addOnFailureListener(e -> showAlert("Error updating pizza: " + e.getMessage()));
    }
}