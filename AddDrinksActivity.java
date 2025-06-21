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

public class AddDrinksActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageButton addDrinksImageButton;
    private String currentProductId = "";
    private FirebaseFirestore firebaseFirestore;
    private EditText productNameText, productDescriptionText, productPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_drinks);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseFirestore = FirebaseFirestore.getInstance();


        // Optionally animate an ImageView
        ImageView animatedImage = findViewById(R.id.imageView10);
        // Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim);
        // animatedImage.startAnimation(animation);

        addDrinksImageButton = findViewById(R.id.imageButton7);
        productNameText = findViewById(R.id.text11);
        productDescriptionText = findViewById(R.id.text9);
        productPrice = findViewById(R.id.text10);
        Button addDrinksButton = findViewById(R.id.button5);
        Button updateDrinksButton = findViewById(R.id.button10);

        addDrinksImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        addDrinksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndAddDrinks();
            }
        });

        updateDrinksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentProductId.isEmpty()) {
                    showUpdateDialog();
                } else {
                    updateDrinks();
                }
            }
        });

    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); // Only images
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            addDrinksImageButton.setImageURI(imageUri);
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

            File file = new File(directory, "Drinks_" + productId + ".jpg");
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

    private String generateProductId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder productId = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            productId.append(chars.charAt(random.nextInt(chars.length())));
        }
        return productId.toString();
    }

    private void validateAndAddDrinks() {
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
            showAlert("Please select an image for the drink.");
            return;
        }

        String productId = generateProductId();
        String imagePath =  saveImageToSharedStorage(imageUri, productId);
        if (imagePath.isEmpty()) {
            showAlert("Failed to save image.");
            return;
        }

        Map<String, Object> drink = new HashMap<>();
        drink.put("productId", productId);
        drink.put("name", productName);
        drink.put("description", productDescription);
        drink.put("price", priceText);
        drink.put("imagePath", imagePath);

        firebaseFirestore.collection("drinks").document(productId)
                .set(drink)
                .addOnSuccessListener(aVoid -> {
                    showAlert("Drink added successfully!");
                    // Clear input fields and reset image button
                    productNameText.setText("");
                    productDescriptionText.setText("");
                    productPrice.setText("");
                    addDrinksImageButton.setImageResource(R.drawable.baseline_add_photo_alternate_24);
                    imageUri = null;
                })
                .addOnFailureListener(e -> showAlert("Error adding drink: " + e.getMessage()));
    }


    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Product ID to Update");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Search", (dialog, which) -> {
            String productId = input.getText().toString().trim();
            if (!productId.isEmpty()) {
                fetchDrinksDetails(productId);
            } else {
                showAlert("Please enter a valid product ID.");
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void fetchDrinksDetails(String productId) {
        firebaseFirestore.collection("drinks").document(productId).get()
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
                                addDrinksImageButton.setImageBitmap(myBitmap);
                            }
                        }
                    } else {
                        showAlert("No product found with this ID.");
                    }
                })
                .addOnFailureListener(e -> showAlert("Error fetching details: " + e.getMessage()));
    }

    private void updateDrinks() {
        if (currentProductId.isEmpty()) {
            showAlert("No product selected for update.");
            return;
        }

        String productName = productNameText.getText().toString().trim();
        String productDescription = productDescriptionText.getText().toString().trim();
        String priceText = productPrice.getText().toString().trim();

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

        firebaseFirestore.collection("drinks").document(currentProductId)
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    showAlert("Drink updated successfully!");
                    productNameText.setText("");
                    productDescriptionText.setText("");
                    productPrice.setText("");
                    addDrinksImageButton.setImageResource(R.drawable.baseline_add_photo_alternate_24);
                    imageUri = null;
                })
                .addOnFailureListener(e -> showAlert("Error updating drink: " + e.getMessage()));
    }

    private void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
