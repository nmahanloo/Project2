package com.example.project2;
import androidx.annotation.NonNull;
/**
 * Author: Nima Mahanloo
 * Title: Shop Master: CartItem Class
 * Date: April 10, 2023
 * It is a class of my CST 338 Project 2,
 * which is in use to display items in
 * a user's shopping cart with necessary
 * information through an ArrayAdapter.
 */
public class CartItem {
    private int productId;
    private String productName;
    private double productPrice;
    private int orderQuantity;
    public CartItem(int productId, String productName, double productPrice, int orderQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.orderQuantity = orderQuantity;
    }
    public int getProductId() {
        return productId;
    }
    public void setProductId(int productId) {
        this.productId = productId;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public double getProductPrice() {
        return productPrice;
    }
    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }
    public int getOrderQuantity() {
        return orderQuantity;
    }
    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }
    @NonNull
    @Override
    public String toString() {
        return ("Product ID: " + productId + "\n" +
                productName + "\n" +
                orderQuantity + " x $" + productPrice);
    }
}
