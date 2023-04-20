package com.example.project2;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.example.project2.databinding.ActivityManageProductsBinding;
import com.example.project2.db.AppDatabase;
import com.example.project2.db.ShoppingMasterDAO;
import java.util.ArrayList;
import java.util.List;
/**
 * Author: Nima Mahanloo
 * Title: Shop Master: Manage Products
 * Date: April 10, 2023
 * It is an activity of my CST 338 Project 2,
 * which is in use to manage products in
 * the database by admin.
 */
public class ManageProductsActivity extends AppCompatActivity {
    private static final String USER_ID_KEY = "com.example.project2.userIdKey";
    private Button add_product_button;
    private Button update_product_button;
    private Button remove_product_button;
    private ListView products_listView;
    private ShoppingMasterDAO shoppingMasterDAO;
    private Product selectedProduct = null;
    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);
        ActivityManageProductsBinding activityManageProductsBinding = ActivityManageProductsBinding.inflate(getLayoutInflater());
        View view = activityManageProductsBinding.getRoot();
        setContentView(view);
        add_product_button = activityManageProductsBinding.addProductButton;
        update_product_button = activityManageProductsBinding.updateProductButton;
        remove_product_button = activityManageProductsBinding.removeProductButton;
        products_listView = activityManageProductsBinding.productsListview;
        userID = getIntent().getExtras().getInt(USER_ID_KEY, -1);
        getDatabase();
        displayProductList();
        addProduct();
        updateProduct();
        removeProduct();
    }
    private void getDatabase() {
        shoppingMasterDAO = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getShoppingMasterDAO();
    }
    public void displayProductList(){
        List<Product> productList = shoppingMasterDAO.getAllProducts();
        if (productList.size() > 0) {
            ArrayAdapter<Product> adapter = new ArrayAdapter<>(ManageProductsActivity.this, android.R.layout.simple_list_item_1, productList);
            adapter.notifyDataSetChanged();
            products_listView.setAdapter(adapter);
            products_listView.setOnItemClickListener((adapterView, view, i, l) -> {
                selectedProduct = (Product) adapterView.getAdapter().getItem(i);
                Toast.makeText(getBaseContext(), selectedProduct.getProductName() + " selected", Toast.LENGTH_SHORT).show();
            });
        }
        else {
            List<String> noProduct = new ArrayList<>();
            noProduct.add("No product added yet");
            ArrayAdapter<String> noProductAdapter = new ArrayAdapter<>(ManageProductsActivity.this, android.R.layout.simple_list_item_1, noProduct);
            noProductAdapter.notifyDataSetChanged();
            products_listView.setAdapter(noProductAdapter);
        }
    }
    private void addProduct()
    {
        add_product_button.setOnClickListener(view -> {
            Intent intent = AddProductActivity.intentFactory(getApplicationContext(), userID);
            startActivity(intent);
        });
    }
    private void updateProduct() {
        update_product_button.setOnClickListener(view -> {
            if (selectedProduct != null) {
                Intent intent = new Intent(ManageProductsActivity.this, UpdateProductActivity.class);
                intent.putExtra("productIDKey", selectedProduct.getProductId());
                intent.putExtra(USER_ID_KEY, userID);
                startActivity(intent);
            }
            else {
                Toast.makeText(getBaseContext(), "Select a product", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void removeProduct() {
        remove_product_button.setOnClickListener(view -> {
            if (selectedProduct != null) {
                shoppingMasterDAO.deleteProduct(selectedProduct);
                Toast.makeText(getBaseContext(), selectedProduct.getProductName() + " removed successfully", Toast.LENGTH_SHORT).show();
                Intent intent = ManageProductsActivity.intentFactory(getApplicationContext(), userID);
                startActivity(intent);
            }
            else {
                Toast.makeText(getBaseContext(), "Select a product", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = AdminActivity.intentFactory(getApplicationContext(), userID);
        startActivity(intent);
    }
    public static Intent intentFactory(Context context, int userId) {
        Intent intent = new Intent(context, ManageProductsActivity.class);
        intent.putExtra(USER_ID_KEY, userId);
        return intent;
    }
}