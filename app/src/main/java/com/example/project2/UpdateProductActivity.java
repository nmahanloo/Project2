package com.example.project2;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingParent;
import androidx.core.widget.NestedScrollView;
import androidx.room.Room;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.project2.databinding.ActivityUpdateProductBinding;
import com.example.project2.db.AppDatabase;
import com.example.project2.db.ShoppingMasterDAO;
import java.security.PrivateKey;
import java.text.DecimalFormat;
import java.util.List;
/**
 * Author: Nima Mahanloo
 * Title: Shop Master: Update Product
 * Date: April 10, 2023
 * It is an activity of my CST 338 Project 2,
 * which is in use to update a data record of
 * a product data in the database.
 */
public class UpdateProductActivity extends AppCompatActivity {
    private static final String USER_ID_KEY = "com.example.project2.userIdKey";
    private ActivityUpdateProductBinding updateProductActivityBinding;
    private EditText update_productName;
    private EditText update_productQuantity;
    private EditText update_productPrice;
    private EditText update_productDescription;
    private Button updateAProduct;
    private ShoppingMasterDAO shoppingMasterDAO;
    private String updateProductName;
    private double updateProductPrice;
    private int updateProductQuantity;
    private String updateProductDescription;
    private int userID;
    private int productID;
    private Product product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);
        updateProductActivityBinding = ActivityUpdateProductBinding.inflate(getLayoutInflater());
        View view = updateProductActivityBinding.getRoot();
        setContentView(view);
        update_productName = updateProductActivityBinding.updateProductNameEdittext;
        update_productQuantity = updateProductActivityBinding.updateProductQuantityEdittext;
        update_productPrice = updateProductActivityBinding.updateProductPriceEdittext;
        update_productDescription = updateProductActivityBinding.updateProductDescriptionEdittext;
        updateAProduct = updateProductActivityBinding.updateAProductButton;
        productID = getIntent().getExtras().getInt("productIDKey", -1);
        userID = getIntent().getExtras().getInt(USER_ID_KEY, -1);
        getDatabase();
        setValuesOnDisplay();
        updateProduct();
    }
    private void getDatabase(){
        shoppingMasterDAO = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getShoppingMasterDAO();
    }
    private void setValuesOnDisplay() {
        product = shoppingMasterDAO.getProductByProductId(productID);
        update_productName.setText(product.getProductName());
        update_productQuantity.setText(Integer.toString(product.getProductQuantity()));
        update_productPrice.setText(Double.toString(product.getProductPrice()));
        update_productDescription.setText(product.getProductDescription());
    }
    private void getValuesFromDisplay(){
        updateProductName = update_productName.getText().toString();
        updateProductQuantity = Integer.parseInt(update_productQuantity.getText().toString());
        updateProductPrice =  Double.parseDouble(update_productPrice.getText().toString());
        DecimalFormat centsStyle = new DecimalFormat("#.##");
        updateProductPrice = Double.parseDouble(centsStyle.format(updateProductPrice));
        updateProductDescription = update_productDescription.getText().toString();
    }
    private void updateProduct() {
        updateAProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getValuesFromDisplay();
                product.setProductName(updateProductName);
                product.setProductQuantity(updateProductQuantity);
                product.setProductPrice(updateProductPrice);
                product.setProductDescription(updateProductDescription);
                shoppingMasterDAO.updateProduct(product);
                Toast.makeText(UpdateProductActivity.this, "Product data updated successfully", Toast.LENGTH_SHORT).show();
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
    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, UpdateProductActivity.class);
        return intent;
    }
}