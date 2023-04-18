package com.example.project2.db;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.project2.User;
import com.example.project2.Product;
import com.example.project2.Cart;
import com.example.project2.Order;
import com.example.project2.db.typeConverters.DateTypeConverter;
/**
 * Author: Nima Mahanloo
 * Title: Shop Master: App Database
 * Date: April 10, 2023
 * It is an abstract class of my CST 338 Project 2,
 * which is in use to initial data tables of the database.
 */
@Database(entities = {Product.class, User.class, Cart.class, Order.class}, version = 1)
@TypeConverters(DateTypeConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DB_NAME = "SHOPPING_MASTER_DATABASE";
    public static final String USER_TABLE = "USER_TABLE";
    public static final String PRODUCT_TABLE = "PRODUCT_TABLE";
    public static final String CART_TABLE = "CART_TABLE";
    public static final String ORDER_TABLE = "ORDER_TABLE";
    public abstract ShoppingMasterDAO getShoppingMasterDAO();
}
