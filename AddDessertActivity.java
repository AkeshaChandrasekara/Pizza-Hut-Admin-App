package com.myapp.pizzahut_admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddDessertActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageButton addDessertImageButton;
    private String savedImagePath = "";
    private String currentProductId = "";
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private EditText productNameText, productDescriptionText, productPrice;
    private boolean isDialogShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_dessert);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        firebaseFirestore = FirebaseFirestore.getInstance();

        addDessertImageButton = findViewById(R.id.imageButton5);
        productNameText = findViewById(R.id.Text4);
        productDescriptionText = findViewById(R.id.Text5);
        productPrice = findViewById(R.id.Text6);
        Button addDessertButton = findViewById(R.id.button12);
        Button updateDessertButton = findViewById(R.id.button13);

        addDessertImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        addDessertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndAddDessert();
            }
        });

        updateDessertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentProductId.isEmpty()) {
                    showUpdateDialog();
                } else {
                    updateDessert();
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
            addDessertImageButton.setImageURI(imageUri);
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

            File file = new File(directory, "Desserts_" + productId + ".jpg");
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

    private void  validateAndAddDessert() {
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
            showAlert("Please select an image for the dessert.");
            return;
        }

        String productId = generateProductId();
        String imagePath = saveImageToSharedStorage(imageUri, productId);
        if (imagePath.isEmpty()) {
            showAlert("Failed to save image.");
            return;
        }

        Map<String, Object> desserts = new HashMap<>();
        desserts.put("productId", productId);
        desserts.put("name", productName);
        desserts.put("description", productDescription);
        desserts.put("price", priceText);
        desserts.put("imagePath", imagePath);

        firebaseFirestore.collection("desserts").document(productId)
                .set(desserts)
                .addOnSuccessListener(documentReference -> {
                    showAlert("Dessert added successfully!");
                    resetForm();
                })
                .addOnFailureListener(e -> showAlert("Error adding desserts: " + e.getMessage()));
    }

    private void resetForm() {
        productNameText.setText("");
        productDescriptionText.setText("");
        productPrice.setText("");
        addDessertImageButton.setImageResource(R.drawable.baseline_add_photo_alternate_24);
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
        builder.setTitle("Update Dessert");
        builder.setMessage("Enter the Product ID of the dessert you want to update:");

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
        firebaseFirestore.collection("desserts").document(productId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentProductId = productId; // Store the product ID for update
                        productNameText.setText(documentSnapshot.getString("name"));
                        productDescriptionText.setText(documentSnapshot.getString("description"));
                        productPrice.setText(documentSnapshot.getString("price"));

                        String imagePath = documentSnapshot.getString("imagePath");
                        if (imagePath != null && !imagePath.isEmpty()) {
                            File imgFile = new File(imagePath);
                            if (imgFile.exists()) {
                                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                addDessertImageButton.setImageBitmap(myBitmap);
                            }
                        }
                    } else {
                        showAlert("No pizza found with this ID.");
                    }
                })
                .addOnFailureListener(e -> showAlert("Error fetching pizza details: " + e.getMessage()));
    }

    private void updateDessert() {
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

        firebaseFirestore.collection("desserts").document(currentProductId)
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    showAlert("Dessert updated successfully!");
                    resetForm();
                })
                .addOnFailureListener(e -> showAlert("Error updating pizza: " + e.getMessage()));
    }

}