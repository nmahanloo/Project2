package com.example.project2;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.project2.databinding.ActivityMainBinding;
import com.example.project2.db.AppDatabase;
import com.example.project2.db.ShoppingMasterDAO;
import java.util.List;
/**
 * Author: Nima Mahanloo
 * Title: Shop Master: Main
 * Date: April 10, 2023
 * It is the main activity of my CST 338 Project 2,
 * which is in use to manage application main operations
 * and it's main menu.
 * I used some materials of "Dr. C. GymLogSP20 MainActivity" on this MainActivity
 */
public class MainActivity extends AppCompatActivity {
    private static final String USER_ID_KEY = "com.example.project2.userIdKey";
    private static final String PREFERENCES_KEY = "com.example.project2.PREFERENCES_KEY";
    Button shop_button;
    Button orderHistory_button;
    Button shoppingCard_button;
    Button changePassword_button;
    Button menu_admin_button;
    ActivityMainBinding MainActivityBinding;
    private ShoppingMasterDAO shoppingMasterDAO;
    private int userId = -1;
    private SharedPreferences preferences = null;
    private User user;
    private long buttonPressTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getDatabase();
        checkForProducts();
        checkForUser();
        loginUser(userId);
        MainActivityBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = MainActivityBinding.getRoot();
        setContentView(view);
        shop_button = MainActivityBinding.shopButton;
        shoppingCard_button = MainActivityBinding.cartButton;
        orderHistory_button = MainActivityBinding.orderHistoryButton;
        changePassword_button = MainActivityBinding.changePassButton;
        menu_admin_button = MainActivityBinding.adminButton;
        if (user != null) {
            if (user.isAdmin()) {
                menu_admin_button.setVisibility(View.VISIBLE);
            } else {
                menu_admin_button.setVisibility(View.INVISIBLE);
            }
        }
        else {
            clearUserFromIntent();
            clearUserFromPref();
            userId = -1;
            checkForUser();
        }
        goToShop();
        goToCart();
        goToOrderHistory();
        goToPasswordChange();
        goToAdmin();
    }
    private void getDatabase() {
        shoppingMasterDAO = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getShoppingMasterDAO();
    }
    private void checkForUser() {
        userId = getIntent().getIntExtra(USER_ID_KEY, -1);
        if (userId != -1) {
            return;
        }
        if (preferences == null) {
            getPrefs();
        }
        userId = preferences.getInt(USER_ID_KEY, -1);
        if (userId != -1) {
            return;
        }
        List<User> users = shoppingMasterDAO.getAllUsers();
        if (users.size() < 1) {
            User defaultAdmin = new User("admin2", "1234", true);
            User defaultUser = new User("testuser1", "1234");
            shoppingMasterDAO.insertUser(defaultAdmin, defaultUser);
        }
        Intent intent = LoginActivity.getIntent(this);
        startActivity(intent);
    }
    private void checkForProducts() {
        List<Product> productList = shoppingMasterDAO.getAllProducts();
        if (productList.size() <  1) {
            Product defaultProduct = new Product("Gibson Explorer", 3, 1199.99, "An iconic explorer style electric guitar.");
            Product secondProduct = new Product("Korg Kronos 73", 2, 3599.99, "One of the best workstation keyboard.");
            Product thirdProduct = new Product("Dell Alienware R15", 1, 4299.99, "A powerful desktop computer.");
            shoppingMasterDAO.insertProduct(defaultProduct, secondProduct, thirdProduct);
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (user != null) {
            MenuItem item = menu.findItem(R.id.userMenuLogout);
            item.setTitle(user.getUserName());
        }
        return super.onPrepareOptionsMenu(menu);
    }
    private void addUserToPreference(int userId) {
        if (preferences == null) {
            getPrefs();
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(USER_ID_KEY, userId);
        editor.apply();
    }
    private void loginUser(int userId) {
        user = shoppingMasterDAO.getUserByUserId(userId);
        addUserToPreference(userId);
        invalidateOptionsMenu();
    }
    private void getPrefs() {
        preferences = this.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
    }
    private void logoutUser() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.logout);
        alertBuilder.setPositiveButton(getString(R.string.yes),
                (dialog, which) -> {
                    clearUserFromIntent();
                    clearUserFromPref();
                    userId = -1;
                    checkForUser();
                });
        alertBuilder.setNegativeButton(getString(R.string.no),
                (dialog, which) -> {
                });
        alertBuilder.create().show();
    }
    private void clearUserFromIntent() {
        getIntent().putExtra(USER_ID_KEY, -1);
    }
    private void clearUserFromPref() {
        addUserToPreference(-1);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.userMenuLogout) {
            logoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void goToShop() {
        shop_button.setOnClickListener(view1 -> {
            Intent intent = new Intent(MainActivity.this, ShopActivity.class);
            intent.putExtra("userIDKey",  user.getUserId());
            startActivity(intent);
        });
    }
    private void goToCart() {
        shoppingCard_button.setOnClickListener(view12 -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            intent.putExtra("userIDKey",  user.getUserId());
            startActivity(intent);
        });
    }
    private void goToOrderHistory() {
        orderHistory_button.setOnClickListener(view13 -> {
            Intent intent = new Intent(MainActivity.this, OrderHistoryActivity.class);
            intent.putExtra("userIDKey",  user.getUserId());
            startActivity(intent);
        });
    }
    private void goToPasswordChange() {
        changePassword_button.setOnClickListener(view14 -> {
            Intent intent = PasswordChangeActivity.intentFactory(getApplicationContext(), user.getUserId());
            startActivity(intent);
        });
    }
    private void goToAdmin() {
        menu_admin_button.setOnClickListener(view15 -> {
            Intent intent = AdminActivity.getIntent(getApplicationContext());
            startActivity(intent);
        });
    }
    // Go to launcher's home page by pressing back button.
    @Override
    public void onBackPressed() {
        if (buttonPressTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            Intent androidHome = new Intent(Intent.ACTION_MAIN);
            androidHome.addCategory(Intent.CATEGORY_HOME);
            androidHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(androidHome);
        } else {
            Toast.makeText(getBaseContext(), "Press back button again to exit", Toast.LENGTH_SHORT).show();
        }
        buttonPressTime = System.currentTimeMillis();
    }
    public static Intent intentFactory(Context context, int userId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(USER_ID_KEY, userId);
        return intent;
    }
    public static Intent getIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }
}