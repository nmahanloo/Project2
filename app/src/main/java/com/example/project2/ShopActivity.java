package com.example.project2;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import com.example.project2.databinding.ActivityShopBinding;
import com.example.project2.db.AppDatabase;
import com.example.project2.db.ShoppingMasterDAO;
import java.util.ArrayList;
import java.util.List;
/**
 * Author: Nima Mahanloo
 * Title: Shop Master: Shop
 * Date: April 10, 2023
 * It is an activity of my CST 338 Project 2,
 * which is in use to add a product into the
 * user's shopping cart, and insert a new record
 * into the CART_TABLE.
 */
public class ShopActivity extends AppCompatActivity {
    private static final String USER_ID_KEY = "com.example.project2.userIdKey";
    private ShoppingMasterDAO shoppingMasterDAO;
    private User user;
    private int userID;
    private EditText itemSearch;
    private ListView shopItems_listView;
    private EditText shopQuantity_editText ;
    private ImageButton searchButton;
    private ImageButton clearButton;
    private Button addToCart_Button;
    private Button goToCart_Button;
    private String searchText;
    private ArrayAdapter<Product> adapter;
    private Product selectedItem;
    private List<Product> matchItems = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        ActivityShopBinding shopActivityBinding = ActivityShopBinding.inflate(getLayoutInflater());
        View view = shopActivityBinding.getRoot();
        setContentView(view);
        itemSearch = shopActivityBinding.shopSearchEdittext;
        searchButton = shopActivityBinding.shopSearchButton;
        clearButton = shopActivityBinding.shopClearButton;
        shopItems_listView = shopActivityBinding.shopListview;
        shopQuantity_editText = shopActivityBinding.shopQuantityEdittext;
        addToCart_Button = shopActivityBinding.cartButton;
        goToCart_Button = shopActivityBinding.goToCartButton;
        userID = getIntent().getExtras().getInt("userIDKey", -1);
        getDatabase();
        setUser();
        displayAllItems();
        searchForItems();
        clearSearch();
        addToCart();
        goToCart();
    }
    private void getDatabase() {
        shoppingMasterDAO = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getShoppingMasterDAO();
    }
    private void setUser(){
        user = shoppingMasterDAO.getUserByUserId(userID);
    }
    public void displayAllItems(){
        List<Product> productList = shoppingMasterDAO.getAllProducts();
        if (productList.size() > 0) {
            adapter = new ArrayAdapter<>(ShopActivity.this, android.R.layout.simple_list_item_1, productList);
            adapter.notifyDataSetChanged();
            shopItems_listView.setAdapter(adapter);
            shopItems_listView.setOnItemClickListener((adapterView, view, i, l) -> {
                selectedItem = (Product) adapterView.getAdapter().getItem(i);
                Toast.makeText(getBaseContext(), selectedItem.getProductName() + " selected", Toast.LENGTH_SHORT).show();
                shopQuantity_editText.setText("1");
            });
        }
        else {
            List<String> noProduct = new ArrayList<>();
            noProduct.add("No product added yet");
            ArrayAdapter<String> noProductAdapter = new ArrayAdapter<>(ShopActivity.this, android.R.layout.simple_list_item_1, noProduct);
            noProductAdapter.notifyDataSetChanged();
            shopItems_listView.setAdapter(noProductAdapter);
            shopItems_listView.setOnItemClickListener((adapterView, view, i, l) -> {
            });
        }
    }
    private void getValuesFromDisplay(){
        searchText = itemSearch.getText().toString();
    }
    private boolean findItems() {
        getValuesFromDisplay();
        matchItems = shoppingMasterDAO.getProductBySearchText(searchText);
        return (matchItems.size() > 0);
    }
    private void displayFoundItems() {
        if (matchItems.size() > 0) {
            adapter = new ArrayAdapter<>(ShopActivity.this, android.R.layout.simple_list_item_1, matchItems);
            adapter.notifyDataSetChanged();
            shopItems_listView.setAdapter(adapter);
            shopItems_listView.setOnItemClickListener((adapterView, view, i, l) -> {
                selectedItem = (Product) adapterView.getAdapter().getItem(i);
                Toast.makeText(getBaseContext(), selectedItem.getProductName() + " selected", Toast.LENGTH_SHORT).show();
                shopQuantity_editText.setText("1");
            });
        }
    }
    private void searchForItems() {
        searchButton.setOnClickListener(view -> {
            boolean itemFound = findItems();
            if (itemFound) {
                displayFoundItems();
            }
            else {
                Toast.makeText(getBaseContext(), "No item found", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clearSearch() {
        clearButton.setOnClickListener(view -> {
            itemSearch.setText("");
            shopQuantity_editText.setText("");
            selectedItem = null;
            displayAllItems();
        });
    }
    private void addToCart() {
        addToCart_Button.setOnClickListener(view -> {
            if (selectedItem != null) {
                int itemQuantity = Integer.parseInt(shopQuantity_editText.getText().toString());
                if (selectedItem.getProductQuantity() == 0) {
                    Toast.makeText(getBaseContext(), "Item is out of stock", Toast.LENGTH_SHORT).show();
                } else if (itemQuantity > selectedItem.getProductQuantity()) {
                    Toast.makeText(getBaseContext(), "Quantity not available", Toast.LENGTH_SHORT).show();
                } else if (itemQuantity == 0) {
                    Toast.makeText(getBaseContext(), "Enter a valid quantity number", Toast.LENGTH_SHORT).show();
                } else {
                    Cart prevItem = shoppingMasterDAO.getCartItemByProductId(user.getUserId(), selectedItem.getProductId());
                    if (prevItem != null) {
                        int addedQuantity = prevItem.getItemQuantity();
                        if (addedQuantity+itemQuantity <= selectedItem.getProductQuantity()) {
                            addedQuantity += itemQuantity;
                            prevItem.setItemQuantity(addedQuantity);
                            shoppingMasterDAO.updateItem(prevItem);
                            Toast.makeText(getBaseContext(),  "Shopping cart updated", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getBaseContext(), "Quantity not available", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Cart currItem = new Cart(userID, selectedItem.getProductId(), itemQuantity);
                        shoppingMasterDAO.addItem(currItem);
                        Toast.makeText(getBaseContext(), selectedItem.getProductName() + " added to shopping cart", Toast.LENGTH_SHORT).show();
                    }
                    itemSearch.setText("");
                    shopQuantity_editText.setText("");
                    selectedItem = null;
                    displayAllItems();
                }
            }
            else {
                Toast.makeText(getBaseContext(), "No product selected yet", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void goToCart() {
        goToCart_Button.setOnClickListener(view -> {
            Intent intent = CartActivity.intentFactory(getApplicationContext(), user.getUserId());
            startActivity(intent);
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAndRemoveTask();
    }
    public static Intent getIntent(Context context) {
        return new Intent(context, ShopActivity.class);
    }
}