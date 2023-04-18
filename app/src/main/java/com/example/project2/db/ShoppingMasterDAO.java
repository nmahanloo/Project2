package com.example.project2.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.project2.User;
import com.example.project2.Product;
import com.example.project2.Cart;
import com.example.project2.Order;

import java.util.Date;
import java.util.List;
/**
 * Author: Nima Mahanloo
 * Title: Shop Master: Database
 * Date: April 10, 2023
 * It is the DAO file of my CST 338 Project 2,
 * which is responsible for database, data operation
 * and sql queries.
 */
@Dao
public interface ShoppingMasterDAO {
    @Insert
    void insertUser(User... users);
    @Update
    void updateUser(User... users);
    @Delete
    void deleteUser(User... users);
    @Query("DELETE FROM " + AppDatabase.USER_TABLE)
    void deleteAllUsers();
    @Query("SELECT * FROM " + AppDatabase.USER_TABLE)
    List<User> getAllUsers();
    @Query("SELECT * FROM " + AppDatabase.USER_TABLE + " WHERE userName = :username")
    User getUserByUsername(String username);
    @Query("SELECT * FROM " + AppDatabase.USER_TABLE + " WHERE userId = :userId")
    User getUserByUserId(int userId);
    @Insert
    void insertProduct(Product... products);
    @Update
    void updateProduct(Product... products);
    @Delete
    void deleteProduct(Product... products);
    @Query("DELETE FROM " + AppDatabase.PRODUCT_TABLE)
    void deleteAllProducts();
    @Query("SELECT * FROM " + AppDatabase.PRODUCT_TABLE)
    List<Product> getAllProducts();
    @Query("SELECT * FROM " + AppDatabase.PRODUCT_TABLE + " WHERE productName = :productName")
    Product getProductByProductName(String productName);
    @Query("SELECT * FROM " + AppDatabase.PRODUCT_TABLE + " WHERE productId = :productId")
    Product getProductByProductId(int productId);
    @Query("SELECT * FROM " + AppDatabase.PRODUCT_TABLE + " WHERE productName LIKE '%' || :searchText || '%' OR productDescription LIKE '%' || :searchText || '%'")
    List<Product> getProductBySearchText(String searchText);
    @Insert
    void addItem(Cart... carts);
    @Update
    void updateItem(Cart... carts);
    @Delete
    void deleteItem(Cart... carts);
    @Query("DELETE FROM " + AppDatabase.CART_TABLE)
    void deleteAllItems();
    @Query("SELECT * FROM " + AppDatabase.CART_TABLE)
    List<Cart> getAllItems();
    @Query("SELECT * FROM " + AppDatabase.CART_TABLE + " WHERE userId = :userId AND orderId = -1")
    List<Cart> getCartItemsByUserId(int userId);
    @Query("SELECT * FROM " + AppDatabase.CART_TABLE + " WHERE userId = :userId AND productId = :productId AND orderId = -1")
    Cart getCartItemByProductId(int userId, int productId);
    @Insert
    void addOrder(Order... orders);
    @Update
    void updateOrder(Order... orders);
    @Delete
    void deleteOrder(Order... orders);
    @Query("DELETE FROM " + AppDatabase.ORDER_TABLE)
    void deleteAllOrders();
    @Query("SELECT * FROM " + AppDatabase.ORDER_TABLE)
    List<Order> getAllOrders();
    @Query("SELECT * FROM " + AppDatabase.ORDER_TABLE + " WHERE userId = :userId")
    List<Order> getOrdersByUserId(int userId);
    @Query("SELECT * FROM " + AppDatabase.ORDER_TABLE + " WHERE orderId = :orderId")
    Order getOrderByOrderId(int orderId);
    @Query("SELECT * FROM " + AppDatabase.ORDER_TABLE + " WHERE date = :date")
    Order getOrderByDate(Date date);
    @Query("SELECT CART_TABLE.* FROM " + AppDatabase.CART_TABLE + ", " + AppDatabase.ORDER_TABLE + " WHERE ORDER_TABLE.orderId = :orderId" + " AND ORDER_TABLE.orderId = CART_TABLE.orderId")
    List<Cart> getOrderedCart(int orderId);
    @Query("SELECT PRODUCT_TABLE.* FROM " + AppDatabase.PRODUCT_TABLE + ", " + AppDatabase.CART_TABLE + " WHERE CART_TABLE.orderId = :orderId" + " AND CART_TABLE.productId = PRODUCT_TABLE.productId")
    List<Product> getOrderedProductsByOrderId(int orderId);
}
