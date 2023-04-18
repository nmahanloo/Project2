package com.example.project2;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.example.project2.db.AppDatabase;
/**
 * Author: Nima Mahanloo
 * Title: Shop Master: Product Class
 * Date: April 10, 2023
 * It is one of the main classes of my CST 338 Project 2,
 * which is in use for the PRODUCT_TABLE in the database.
 */
@Entity(tableName = AppDatabase.PRODUCT_TABLE)
public class Product {
    @PrimaryKey(autoGenerate = true)
    private int productId;
    private String productName;
    private int productQuantity;
    double productPrice;
    private String productDescription;
    public Product(String productName, int productQuantity, double productPrice, String productDescription) {
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
        this.productDescription = productDescription;
    }
    public int getProductId() {
        return productId;
    }
    public void setProductId(int productId) {
        this.productId = productId;
    }
    public int getProductQuantity() {
        return productQuantity;
    }
    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }
    public double getProductPrice() {
        return productPrice;
    }
    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getProductDescription() {
        return productDescription;
    }
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
    @NonNull
    @Override
    public String toString() {
        return ("Product ID: " + productId + "\n" +
                productName + "\n" +
                "$" + productPrice + "\n" +
                productQuantity + " available on stock" + "\n" +
                "Description: " + productDescription);
    }
}
