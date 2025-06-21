package com.myapp.pizzahut_admin;

import java.util.List;

public class Order {
    private String orderId;
    private String email;
    private String address;
    private double total;
    private double finalAmount;
    private String status;
    private List<CartItem> items;
    private String phoneNumber;

    public Order() {

    }

    public Order(String orderId, String email, String address, double total, double finalAmount, String status, List<CartItem> items) {
        this.orderId = orderId;
        this.email = email;
        this.address = address;
        this.total = total;
        this.finalAmount = finalAmount;
        this.status = status;
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public double getTotal() {
        return total;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public String getStatus() {
        return status;
    }

    public List<CartItem> getItems() {
        return items;
    }
}
