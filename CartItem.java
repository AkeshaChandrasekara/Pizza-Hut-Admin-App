package com.myapp.pizzahut_admin;

public class CartItem {
    private String productName;
    private String productPrice;
    private int quantity;
    private String imagePath;

    public CartItem() {

    }

    public CartItem(String productName, String productPrice, int quantity, String imagePath) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.imagePath = imagePath;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImagePath() {
        return imagePath;
    }
}
