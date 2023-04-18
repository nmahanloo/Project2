package com.example.project2;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import com.example.project2.databinding.ActivityManageUsersBinding;
import com.example.project2.db.AppDatabase;
import com.example.project2.db.ShoppingMasterDAO;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;
/**
 * Author: Nima Mahanloo
 * Title: Shop Master: Manage Users
 * Date: April 10, 2023
 * It is an activity of my CST 338 Project 2,
 * which is in use to manage user accounts by
 * admin.
 */
public class ManageUsersActivity extends AppCompatActivity {
    private ListView users_listView;
    private SwitchCompat adminPermit_switch;
    private Button deleteAccount_button;
    private ShoppingMasterDAO shoppingMasterDAO;
    private User selectedUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);
        ActivityManageUsersBinding manageUsersActivityBinding = ActivityManageUsersBinding.inflate(getLayoutInflater());
        View view = manageUsersActivityBinding.getRoot();
        setContentView(view);
        users_listView = manageUsersActivityBinding.usersListview;
        adminPermit_switch = manageUsersActivityBinding.adminSwitch;
        deleteAccount_button = manageUsersActivityBinding.deleteAccountButton;
        getDatabase();
        displayUserList();
        permitChange();
        deleteAccount();
    }
    private void getDatabase() {
        shoppingMasterDAO = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getShoppingMasterDAO();
    }
    @SuppressLint("SetTextI18n")
    public void displayUserList(){
        List<User> userList = shoppingMasterDAO.getAllUsers();
        if (userList.size() > 0) {
            ArrayAdapter<User> adapter = new ArrayAdapter<>(ManageUsersActivity.this, android.R.layout.simple_list_item_1, userList);
            adapter.notifyDataSetChanged();
            users_listView.setAdapter(adapter);
            users_listView.setOnItemClickListener((adapterView, view, i, l) -> {
                selectedUser = (User) adapterView.getAdapter().getItem(i);
                if (selectedUser.isAdmin()) {
                    adminPermit_switch.setChecked(true);
                    adminPermit_switch.setText("Admin is ON");
                }
                else {
                    adminPermit_switch.setChecked(false);
                    adminPermit_switch.setText("Admin is OFF");
                }
                Toast.makeText(getBaseContext(), "User " + selectedUser.getUserName() + " selected", Toast.LENGTH_SHORT).show();
            });
        }
        else {
            List<String> noUser = new ArrayList<>();
            noUser.add("No user account registered yet");
            ArrayAdapter<String> noUserAdapter = new ArrayAdapter<>(ManageUsersActivity.this, android.R.layout.simple_list_item_1, noUser);
            noUserAdapter.notifyDataSetChanged();
            users_listView.setAdapter(noUserAdapter);
        }
    }
    private void permitChange() {
            adminPermit_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean adminSwitch) {
                    if (selectedUser != null) {
                        if (adminSwitch) {
                            if (!(selectedUser.isAdmin())) {
                                selectedUser.setAdmin(true);
                                adminPermit_switch.setText("Admin is ON");
                                shoppingMasterDAO.updateUser(selectedUser);
                                Toast.makeText(ManageUsersActivity.this, "User " + selectedUser.getUserName() + " permission updated", Toast.LENGTH_SHORT).show();
                                displayUserList();
                            }
                        } else {
                            if (selectedUser.isAdmin()) {
                                selectedUser.setAdmin(false);
                                adminPermit_switch.setText("Admin is OFF");
                                shoppingMasterDAO.updateUser(selectedUser);
                                Toast.makeText(ManageUsersActivity.this, "User " + selectedUser.getUserName() + " permission updated", Toast.LENGTH_SHORT).show();
                                displayUserList();
                            }
                        }
                    }
                    else {
                        Toast.makeText(ManageUsersActivity.this, "Select an account", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    private void deleteAccount() {
        deleteAccount_button.setOnClickListener(view -> {
            if(selectedUser != null) {
                shoppingMasterDAO.deleteUser(selectedUser);
                Toast.makeText(ManageUsersActivity.this, "Account " + selectedUser.getUserName() + " deleted successfully", Toast.LENGTH_SHORT).show();
                checkUserAccounts();
                displayUserList();
            }
            else {
                Toast.makeText(ManageUsersActivity.this, "Select an account", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkUserAccounts() {
        List<User> allUsers = shoppingMasterDAO.getAllUsers();
        if (allUsers.size() < 1) {
            Toast.makeText(ManageUsersActivity.this, "No user found", Toast.LENGTH_SHORT).show();
            Intent intent = MainActivity.intentFactory(getApplicationContext(), -1);
            startActivity(intent);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAndRemoveTask();
    }
    public static Intent getIntent(Context context) {
        return new Intent(context, ManageUsersActivity.class);
    }
}