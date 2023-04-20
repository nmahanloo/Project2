package com.example.project2;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.room.Room;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.project2.databinding.ActivityCartBinding;
import com.example.project2.db.AppDatabase;
import com.example.project2.db.ShoppingMasterDAO;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
/**
 * Author: Nima Mahanloo
 * Title: Shop Master: Shopping Cart
 * Date: April 10, 2023
 * It is an activity of my CST 338 Project 2,
 * which is in use for placing an order in a
 * user's shopping cart.
 */
public class CartActivity extends AppCompatActivity {
    private static final String USER_ID_KEY = "com.example.project2.userIdKey";
    private ShoppingMasterDAO shoppingMasterDAO;
    private ListView cart_listView;
    private TextView cart_totalPrice_textView;
    private EditText cart_quantity_editText;
    private ImageButton cart_updateQuantity_button;
    private Button cart_removeItem_button;
    private Button cart_removeAll_button;
    private Button cart_order_button;
    int userID;
    private List<Cart> cartList = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();
    private List<CartItem> itemList = new ArrayList<>();
    private ArrayAdapter adapter = null;
    private CartItem selectedItem = null;
    private double totalPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ActivityCartBinding cartActivityBinding = ActivityCartBinding.inflate(getLayoutInflater());
        View view = cartActivityBinding.getRoot();
        setContentView(view);
        cart_listView = cartActivityBinding.cartListview;
        cart_totalPrice_textView = cartActivityBinding.totalPriceTextview;
        cart_quantity_editText = cartActivityBinding.cartQuantityEdittext;
        cart_updateQuantity_button = cartActivityBinding.quantityUpdateButton;
        cart_removeItem_button = cartActivityBinding.removeFromCartButton;
        cart_removeAll_button = cartActivityBinding.removeAllButton;
        cart_order_button = cartActivityBinding.orderButton;
        userID = getIntent().getExtras().getInt(USER_ID_KEY, -1);
        getDatabase();
        displayCartItems();
        itemQuantityUpdate();
        removeItem();
        removeAllItems();
        completeOrder();
    }
    private void getDatabase() {
        shoppingMasterDAO = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getShoppingMasterDAO();
    }
    @SuppressLint("SetTextI18n")
    public void displayCartItems(){
        getItems();
        calculateTotalPrice();
        String displayTotalPrice = "";
        if (totalPrice > 0.00) {
            displayTotalPrice = "Total: $" + Double.toString(totalPrice);
        }
        cart_totalPrice_textView.setText(displayTotalPrice);
        if (itemList.size() > 0) {
            List<CartItem> reversedItemList = reverseItems();
            adapter = new ArrayAdapter<>(CartActivity.this, android.R.layout.simple_list_item_1, reversedItemList);
            adapter.notifyDataSetChanged();
            cart_listView.setAdapter(adapter);
            cart_listView.setOnItemClickListener((adapterView, view, i, l) -> {
                selectedItem = (CartItem) adapterView.getAdapter().getItem(i);
                Toast.makeText(getBaseContext(), selectedItem.getProductName() + " selected", Toast.LENGTH_SHORT).show();
                int orderQuantity = selectedItem.getOrderQuantity();
                cart_quantity_editText.setText(Integer.toString(orderQuantity));
            });
        }
        else {
            List<String> noItem = new ArrayList<>();
            noItem.add("No item added to cart yet");
            ArrayAdapter<String> noItemAdapter = new ArrayAdapter<>(CartActivity.this, android.R.layout.simple_list_item_1, noItem);
            noItemAdapter.notifyDataSetChanged();
            cart_listView.setAdapter(noItemAdapter);
            cart_listView.setOnItemClickListener((adapterView, view, i, l) -> {
            });
        }
    }
    private List<CartItem> reverseItems() {
        List<CartItem> bufferList = new ArrayList<>();
        for (int index = itemList.size()-1; index >= 0; index--) {
            bufferList.add(itemList.get(index));
        }
        return bufferList;
    }
    private void itemQuantityUpdate() {
        cart_updateQuantity_button.setOnClickListener(view -> {
            if (selectedItem != null) {
                Cart item = null;
                Product product = shoppingMasterDAO.getProductByProductId(selectedItem.getProductId());
                int newQuantity = Integer.parseInt(cart_quantity_editText.getText().toString());
                for (Cart cart : cartList) {
                    if (cart.getProductId() == selectedItem.getProductId()) {
                        item = cart;
                    }
                }
                if (newQuantity > 0) {
                    if (product.getProductQuantity() < newQuantity) {
                        Toast.makeText(getBaseContext(), "Quantity not available", Toast.LENGTH_SHORT).show();
                    } else {
                        assert item != null;
                        item.setItemQuantity(newQuantity);
                        shoppingMasterDAO.updateItem(item);
                        Toast.makeText(getBaseContext(), "Item quantity updated", Toast.LENGTH_SHORT).show();
                        rebootActivity();
                    }
                } else {
                    shoppingMasterDAO.deleteItem(item);
                    Toast.makeText(getBaseContext(), "Item removed successfully", Toast.LENGTH_SHORT).show();
                    rebootActivity();
                }
            }
            else {
                Toast.makeText(getBaseContext(), "No item selected yet", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void removeItem() {
        cart_removeItem_button.setOnClickListener(view -> {
            if (selectedItem != null) {
                for (Cart cart : cartList) {
                    if (cart.getProductId() == selectedItem.getProductId()) {
                        shoppingMasterDAO.deleteItem(cart);
                        Toast.makeText(getBaseContext(), "Item removed successfully", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                rebootActivity();
            }
            else {
                Toast.makeText(getBaseContext(), "No item selected yet", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void removeAllItems() {
        cart_removeAll_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cartList.size() > 0) {
                    selectedItem = null;
                    for (Cart cart : cartList) {
                        shoppingMasterDAO.deleteItem(cart);
                    }
                    Toast.makeText(getBaseContext(), "All items removed successfully", Toast.LENGTH_SHORT).show();
                    rebootActivity();
                }
                else {
                    Toast.makeText(getBaseContext(), "Shopping cart is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean itemsStillAvailable() {
        Product product;
        boolean readyToOrder = true;
        for (Cart cart : cartList) {
            product = shoppingMasterDAO.getProductByProductId(cart.getProductId());
            if (product.getProductQuantity() == 0) {
                Toast.makeText(getBaseContext(), "Sorry! " + product.getProductName() + " is out of stuck", Toast.LENGTH_SHORT).show();
                shoppingMasterDAO.deleteItem(cart);
                Toast.makeText(getBaseContext(), "Shopping cart updated", Toast.LENGTH_SHORT).show();
                readyToOrder = false;
            } else if (product.getProductQuantity() < cart.getItemQuantity()) {
                Toast.makeText(getBaseContext(), "Sorry! Quantity " + cart.getItemQuantity() + " is not available for " + product.getProductName(), Toast.LENGTH_SHORT).show();
                cart.setItemQuantity(product.getProductQuantity());
                shoppingMasterDAO.updateItem(cart);
                Toast.makeText(getBaseContext(), "Item quantity updated in the cart", Toast.LENGTH_SHORT).show();
                readyToOrder = false;
            }
        }
        return readyToOrder;
    }
    private void completeOrder() {
        cart_order_button.setOnClickListener(view -> {
            if (cartList.size() > 0) {
                if (itemsStillAvailable()) {
                    Order newOrder = new Order(userID, totalPrice);
                    shoppingMasterDAO.addOrder(newOrder);
                    List<Order> orders = shoppingMasterDAO.getOrdersByUserId(userID);
                    int orderID = orders.get((orders.size() - 1)).getOrderId();
                    for (Cart cart : cartList) {
                        cart.setOrderId(orderID);
                        shoppingMasterDAO.updateItem(cart);
                    }
                    adjustProductQuantity();
                    Toast.makeText(getBaseContext(), "Order placed successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = OrderHistoryActivity.intentFactory(getApplicationContext(), userID);
                    startActivity(intent);
                }
                else {
                    rebootActivity();
                }
            }
            else {
                Toast.makeText(getBaseContext(), "Shopping cart is empty", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getItems() {
        int productID;
        Product bufferProduct;
        CartItem bufferCartItem;
        cartList = shoppingMasterDAO.getCartItemsByUserId(userID);
        if (itemsStillAvailable()) {
            for (Cart cart : cartList) {
                productID = cart.getProductId();
                bufferProduct = shoppingMasterDAO.getProductByProductId(productID);
                productList.add(bufferProduct);
            }
            for (int index=0; index < productList.size(); index++) {
                bufferCartItem = new CartItem(productList.get(index).getProductId(),productList.get(index).getProductName(),productList.get(index).getProductPrice(),cartList.get(index).getItemQuantity());
                itemList.add(bufferCartItem);
            }
        }
        else {
            rebootActivity();
        }
    }
    private void adjustProductQuantity() {
        int productID;
        Product bufferProduct;
        int adjustedQuantity;
        for (Cart cart : cartList) {
            productID = cart.getProductId();
            bufferProduct = shoppingMasterDAO.getProductByProductId(productID);
            adjustedQuantity = bufferProduct.getProductQuantity()-cart.getItemQuantity();
            bufferProduct.setProductQuantity(adjustedQuantity);
            shoppingMasterDAO.updateProduct(bufferProduct);
        }
    }
    private void rebootActivity() {
        cart_quantity_editText.setText("");
        selectedItem = null;
        cartList.clear();
        productList.clear();
        itemList.clear();
        displayCartItems();
    }
    private void calculateTotalPrice() {
        totalPrice = 0.00;
        for (CartItem item : itemList) {
            totalPrice += (item.getOrderQuantity()*item.getProductPrice());
        }
        DecimalFormat centsStyle = new DecimalFormat("#.##");
        totalPrice = Double.parseDouble(centsStyle.format(totalPrice));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = MainActivity.intentFactory(getApplicationContext(), userID);
        startActivity(intent);
    }
    public static Intent intentFactory(Context context, int userId) {
        Intent intent = new Intent(context, CartActivity.class);
        intent.putExtra(USER_ID_KEY, userId);
        return intent;
    }
}