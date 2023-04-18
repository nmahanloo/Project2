package com.example.project2;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.project2.databinding.ActivityAdminBinding;
/**
 * Author: Nima Mahanloo
 * Title: Shop Master: Admin
 * Date: April 10, 2023
 * It is an activity of my CST 338 Project 2,
 * which is in use to handle admin page.
 */
public class AdminActivity extends AppCompatActivity {
    private Button manage_products_button;
    private Button manage_users_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ActivityAdminBinding adminActivityBinding = ActivityAdminBinding.inflate(getLayoutInflater());
        View view = adminActivityBinding.getRoot();
        setContentView(view);
        manage_products_button = adminActivityBinding.manageProductsButton;
        manage_users_button = adminActivityBinding.manageUsersButton;
        manageProducts();
        manageUsers();
    }
    private void manageProducts() {
        manage_products_button.setOnClickListener(view -> {
            Intent intent = ManageProductsActivity.getIntent(getApplicationContext());
            startActivity(intent);
        });
    }
    private void manageUsers() {
        manage_users_button.setOnClickListener(view -> {
            Intent intent = ManageUsersActivity.getIntent(getApplicationContext());
            startActivity(intent);
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = MainActivity.getIntent(getApplicationContext());
        startActivity(intent);
    }
    public static Intent getIntent(Context context) {
        return new Intent(context, AdminActivity.class);
    }
}