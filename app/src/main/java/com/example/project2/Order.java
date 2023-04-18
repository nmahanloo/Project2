package com.example.project2;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.example.project2.db.AppDatabase;
import java.util.Date;
/**
 * Author: Nima Mahanloo
 * Title: Shop Master: Order Class
 * Date: April 10, 2023
 * It is one of the main classes of my CST 338 Project 2,
 * which is in use for the ORDER_TABLE in the database.
 */
@Entity(tableName = AppDatabase.ORDER_TABLE)
public class Order {
    @PrimaryKey(autoGenerate = true)
    private int orderId;
    private int userId;
    private Date date;
    private double totalPrice;
    public Order(int userId, double totalPrice) {
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.date = new Date();
    }
    public int getOrderId() {
        return orderId;
    }
    public int getUserId() {
        return userId;
    }
    public double getTotalPrice() {
        return totalPrice;
    }
    public Date getDate() {
        return date;
    }
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    @NonNull
    @Override
    public String toString() {
        return ("Order ID: " + orderId + "\n" +
                "Placed at " + getDate() + "\n" +
                "Total Price: $" + getTotalPrice());
    }
}
