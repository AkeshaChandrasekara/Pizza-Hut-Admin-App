package com.myapp.pizzahut_admin;

import android.graphics.Bitmap;

public class Product {

    private String productId;
    private String name;
    private String description;
    private String price;
    private String imagePath;
    private Bitmap imageBitmap;

    public Product() { }

    public Product(String productId, String name, String description, String price, String imagePath,Bitmap imageBitmap) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imagePath = imagePath;
        this.imageBitmap=imageBitmap;

    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }
}
