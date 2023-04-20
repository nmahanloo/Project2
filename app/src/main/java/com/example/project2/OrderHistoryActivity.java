package com.example.project2;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.room.Room;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.project2.databinding.ActivityOrderHistoryBinding;
import com.example.project2.db.AppDatabase;
import com.example.project2.db.ShoppingMasterDAO;
import java.util.ArrayList;
import java.util.List;
/**
 * Author: Nima Mahanloo
 * Title: Shop Master: Order History
 * Date: April 10, 2023
 * It is an activity of my CST 338 Project 2,
 * which is in use to display order history for
 * current user account.
 */
public class OrderHistoryActivity extends AppCompatActivity {
    private static final String USER_ID_KEY = "com.example.project2.userIdKey";
    private ShoppingMasterDAO shoppingMasterDAO;
    private User user;
    private int userID;
    private List<Order> orderList = new ArrayList<>();
    private List<Cart> cartList = new ArrayList<>();
    private List<CartItem> itemList = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();
    private Order selectedOrder;
    private TextView orderList_textView;
    private TextView orderDetail_textView;
    private TextView productDetail_textView;
    private ListView orderHistory_listView;
    private Button orderDetail_button;
    private Button cancelOrder_button;
    private Button orderHistory_back_button;
    ActivityOrderHistoryBinding orderHistoryActivityBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        orderHistoryActivityBinding = ActivityOrderHistoryBinding.inflate(getLayoutInflater());
        View view = orderHistoryActivityBinding.getRoot();
        setContentView(view);
        orderList_textView = orderHistoryActivityBinding.allOrdersTextview;
        orderDetail_textView = orderHistoryActivityBinding.orderItemsTextview;
        orderDetail_textView.setVisibility(View.INVISIBLE);
        productDetail_textView = orderHistoryActivityBinding.orderProductDetailTextview;
        productDetail_textView.setVisibility(View.INVISIBLE);
        orderHistory_listView = orderHistoryActivityBinding.orderHistoryListview;
        orderDetail_button = orderHistoryActivityBinding.orderDetailButton;
        cancelOrder_button = orderHistoryActivityBinding.cancelOrderButton;
        orderHistory_back_button = orderHistoryActivityBinding.orderHistoryBackButton;
        orderHistory_back_button.setVisibility(View.INVISIBLE);
        userID = getIntent().getExtras().getInt(USER_ID_KEY, -1);
        getDatabase();
        //setUser();
        displayOrders();
        orderDetail();
        cancelOrder();
    }
    private void getDatabase() {
        shoppingMasterDAO = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getShoppingMasterDAO();
    }
/*    private void setUser(){
        user = shoppingMasterDAO.getUserByUserId(userID);
    }*/
    public void displayOrders(){
        orderList = shoppingMasterDAO.getOrdersByUserId(userID);
        if (orderList.size() > 0) {
            List<Order> reversedOrderList = reverseOrders();
            ArrayAdapter<Order> adapter = new ArrayAdapter<>(OrderHistoryActivity.this, android.R.layout.simple_list_item_1, reversedOrderList);
            adapter.notifyDataSetChanged();
            orderHistory_listView.setAdapter(adapter);
            orderHistory_listView.setOnItemClickListener((adapterView, view, i, l) -> {
                selectedOrder = (Order) adapterView.getAdapter().getItem(i);
                Toast.makeText(getBaseContext(), "Order ID " + selectedOrder.getOrderId() + " selected", Toast.LENGTH_SHORT).show();
            });
        }
        else {
            List<String> noOrder = new ArrayList<>();
            noOrder.add("No order placed yet");
            ArrayAdapter<String> noOrderAdapter = new ArrayAdapter<>(OrderHistoryActivity.this, android.R.layout.simple_list_item_1, noOrder);
            noOrderAdapter.notifyDataSetChanged();
            orderHistory_listView.setAdapter(noOrderAdapter);
        }
    }
    private List<Order> reverseOrders() {
        List<Order> bufferList = new ArrayList<>();
        for (int index = orderList.size()-1; index >= 0; index--) {
            bufferList.add(orderList.get(index));
        }
        return bufferList;
    }
    public void orderDetail() {
        orderDetail_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedOrder != null) {
                    orderList_textView.setVisibility(View.INVISIBLE);
                    orderDetail_textView.setVisibility(View.VISIBLE);
                    cancelOrder_button.setVisibility(View.INVISIBLE);
                    orderDetail_button.setVisibility(View.INVISIBLE);
                    orderHistory_back_button.setVisibility(View.VISIBLE);
                    productList.clear();
                    itemList.clear();
                    int productID;
                    Product bufferProduct;
                    CartItem bufferCartItem;
                    cartList = shoppingMasterDAO.getOrderedCart(selectedOrder.getOrderId());
                    for (Cart cart : cartList) {
                        productID = cart.getProductId();
                        bufferProduct = shoppingMasterDAO.getProductByProductId(productID);
                        productList.add(bufferProduct);
                    }
                    for (int index = 0; index < productList.size(); index++) {
                        bufferCartItem = new CartItem(productList.get(index).getProductId(), productList.get(index).getProductName(), productList.get(index).getProductPrice(), cartList.get(index).getItemQuantity());
                        itemList.add(bufferCartItem);
                    }
                    displayDetail();
                } else {
                    Toast.makeText(getBaseContext(), "No order selected yet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void displayDetail() {
        if (itemList.size() > 0) {
            List<CartItem> reversedItemList = reverseItems();
            ArrayAdapter<CartItem> cartAdapter = new ArrayAdapter<>(OrderHistoryActivity.this, android.R.layout.simple_list_item_1, reversedItemList);
            cartAdapter.notifyDataSetChanged();
            orderHistory_listView.setAdapter(cartAdapter);
            orderHistory_listView.setOnItemClickListener((adapterView, view, i, l) -> {
                orderDetail_textView.setVisibility(View.INVISIBLE);
                productDetail_textView.setVisibility(View.VISIBLE);
                CartItem selectedItem = (CartItem) adapterView.getAdapter().getItem(i);
                Toast.makeText(getBaseContext(), selectedItem.getProductName() + " selected", Toast.LENGTH_SHORT).show();
                List<Product> selectedProduct = new ArrayList<>();
                selectedProduct.add(shoppingMasterDAO.getProductByProductId(selectedItem.getProductId()));
                ArrayAdapter<Product> selectedProductAdapter = new ArrayAdapter<>(OrderHistoryActivity.this, android.R.layout.simple_list_item_1, selectedProduct);
                selectedProductAdapter.notifyDataSetChanged();
                orderHistory_listView.setAdapter(selectedProductAdapter);
                orderHistory_listView.setOnItemClickListener((adapterView1, view1, i1, l1) -> {
                });
                orderHistory_back_button.setOnClickListener(view12 -> {
                    productDetail_textView.setVisibility(View.INVISIBLE);
                    orderDetail_textView.setVisibility(View.VISIBLE);
                    displayDetail();
                });
            });
        }
        else {
            List<String> noOrder = new ArrayList<>();
            noOrder.add("Error: No order detail");
            ArrayAdapter<String> noOrderAdapter = new ArrayAdapter<>(OrderHistoryActivity.this, android.R.layout.simple_list_item_1, noOrder);
            noOrderAdapter.notifyDataSetChanged();
            orderHistory_listView.setAdapter(noOrderAdapter);
            orderHistory_listView.setOnItemClickListener((adapterView, view, i, l) -> {
            });
        }
        orderHistory_back_button.setOnClickListener(view -> {
            selectedOrder = null;
            orderList_textView.setVisibility(View.VISIBLE);
            orderDetail_textView.setVisibility(View.INVISIBLE);
            orderHistory_back_button.setVisibility(View.INVISIBLE);
            orderDetail_button.setVisibility(View.VISIBLE);
            cancelOrder_button.setVisibility(View.VISIBLE);
            displayOrders();
        });
    }
    private List<CartItem> reverseItems() {
        List<CartItem> bufferList = new ArrayList<>();
        for (int index = itemList.size()-1; index >= 0; index--) {
            bufferList.add(itemList.get(index));
        }
        return bufferList;
    }
    private void adjustTables() {
        cartList = shoppingMasterDAO.getOrderedCart(selectedOrder.getOrderId());
        int productID;
        int productQuantity;
        Product bufferProduct;
        for (Cart cart : cartList) {
            productID = cart.getProductId();
            bufferProduct = shoppingMasterDAO.getProductByProductId(productID);
            productQuantity = bufferProduct.getProductQuantity() + cart.getItemQuantity();
            bufferProduct.setProductQuantity(productQuantity);
            shoppingMasterDAO.updateProduct(bufferProduct);
            shoppingMasterDAO.deleteItem(cart);
        }
    }
    private void cancelOrder() {
        cancelOrder_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedOrder != null) {
                    adjustTables();
                    shoppingMasterDAO.deleteOrder(selectedOrder);
                    Toast.makeText(getBaseContext(), "Order canceled successfully", Toast.LENGTH_SHORT).show();
                    selectedOrder = null;
                    displayOrders();
                }
                else {
                    Toast.makeText(getBaseContext(), "No order selected yet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = MainActivity.intentFactory(getApplicationContext(), userID);
        startActivity(intent);
    }
    public static Intent intentFactory(Context context, int userId) {
        Intent intent = new Intent(context, OrderHistoryActivity.class);
        intent.putExtra(USER_ID_KEY, userId);
        return intent;
    }
}