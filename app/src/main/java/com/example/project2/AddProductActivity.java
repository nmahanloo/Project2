package com.example.project2;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.project2.databinding.ActivityAddProductBinding;
import com.example.project2.db.AppDatabase;
import com.example.project2.db.ShoppingMasterDAO;

import java.text.DecimalFormat;

/**
 * Author: Nima Mahanloo
 * Title: Shop Master: Add Product
 * Date: April 10, 2023
 * It is an activity of my CST 338 Project 2,
 * which is in use to add a new product into
 * the database.
 */
public class AddProductActivity extends AppCompatActivity {
    private static final String USER_ID_KEY = "com.example.project2.userIdKey";
    private EditText add_productName;
    private EditText add_productQuantity;
    private EditText add_productPrice;
    private EditText add_productDescription;
    private Button addAProduct;
    private ShoppingMasterDAO shoppingMasterDAO;
    private String newProductName = "";
    private double newProductPrice = 0.00;
    private int newProductQuantity = 0;
    private String newProductDescription = "No description";
    private int userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ActivityAddProductBinding addProductActivityBinding = ActivityAddProductBinding.inflate(getLayoutInflater());
        View view = addProductActivityBinding.getRoot();
        setContentView(view);
        add_productName = addProductActivityBinding.addProductNameEdittext;
        add_productQuantity = addProductActivityBinding.addProductQuantityEdittext;
        add_productPrice = addProductActivityBinding.addProductPriceEdittext;
        add_productDescription = addProductActivityBinding.addProductDescriptionEdittext;
        addAProduct = addProductActivityBinding.addAProductButton;
        userID = getIntent().getExtras().getInt(USER_ID_KEY, -1);
        getDatabase();
        addProduct();
    }
    private void getDatabase(){
        shoppingMasterDAO = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getShoppingMasterDAO();
    }
    private boolean getValuesFromDisplay(){
        newProductName = add_productName.getText().toString();
        if (add_productQuantity.getText().toString().length() > 0) {
            newProductQuantity = Integer.parseInt(add_productQuantity.getText().toString());
        }
        if (add_productPrice.getText().toString().length() > 0) {
            newProductPrice = Double.parseDouble(add_productPrice.getText().toString());
            DecimalFormat centsStyle = new DecimalFormat("#.##");
            newProductPrice = Double.parseDouble(centsStyle.format(newProductPrice));
        }
        if (add_productDescription.getText().toString().length() > 0) {
            newProductDescription = add_productDescription.getText().toString();
        }
        if (newProductName.length() > 0) {
            return true;
        }
        Toast.makeText(AddProductActivity.this, "Enter product name", Toast.LENGTH_SHORT).show();
        return false;
    }
    private void addProduct() {
        addAProduct.setOnClickListener(view -> {
            if (getValuesFromDisplay()) {
                Product newProduct = new Product(newProductName, newProductQuantity, newProductPrice, newProductDescription);
                shoppingMasterDAO.insertProduct(newProduct);
                Toast.makeText(AddProductActivity.this, newProductName + " added successfully", Toast.LENGTH_SHORT).show();
                Intent intent = ManageProductsActivity.intentFactory(getApplicationContext(), userID);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAndRemoveTask();
    }
    public static Intent intentFactory(Context context, int userId) {
        Intent intent = new Intent(context, AddProductActivity.class);
        intent.putExtra(USER_ID_KEY, userId);
        return intent;
    }
}