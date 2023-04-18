package com.example.project2;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.example.project2.db.AppDatabase;
/**
 * Author: Nima Mahanloo
 * Title: Shop Master: Cart Class
 * Date: April 10, 2023
 * It is one of the main classes of my CST 338 Project 2,
 * which is in use for the CART_TABLE in the database.
 */
@Entity(tableName = AppDatabase.CART_TABLE)
public class Cart {
    @PrimaryKey(autoGenerate = true)
    private int itemNum;
    private int orderId;
    private int userId;
    private int productId;
    private int itemQuantity;
    public Cart(int userId, int productId, int itemQuantity) {
        this.userId = userId;
        this.productId = productId;
        this.itemQuantity = itemQuantity;
        this.orderId = -1;
    }
    public int getItemNum() {
        return itemNum;
    }
    public void setItemNum(int itemNum) {
        this.itemNum = itemNum;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getProductId() {
        return productId;
    }
    public void setProductId(int productId) {
        this.productId = productId;
    }
    public int getOrderId() {
        return orderId;
    }
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    public int getItemQuantity() {
        return itemQuantity;
    }
    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }
    @NonNull
    @Override
    public String toString() {
        return ("Product ID: " + productId + ":\n" +
                "Order quantity: = " + itemQuantity);
    }
}
