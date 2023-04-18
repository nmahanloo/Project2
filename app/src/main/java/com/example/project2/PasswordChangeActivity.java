package com.example.project2;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.project2.databinding.ActivityPasswordChangeBinding;
import com.example.project2.db.AppDatabase;
import com.example.project2.db.ShoppingMasterDAO;
/**
 * Author: Nima Mahanloo
 * Title: Shop Master: Password Change
 * Date: April 10, 2023
 * It is an activity of my CST 338 Project 2,
 * which is in use to change a login password of
 * a user account in the database.
 */
public class PasswordChangeActivity extends AppCompatActivity {
    private TextView username_textView;
    private EditText current_password_editText;
    private EditText new_password_editText;
    private EditText confirmNew_password_editText;
    private Button passwordChange_button;
    private String username;
    private int userID;
    private User user;
    private String currentPassword = "";
    private String oldPassword;
    private String newPassword = "";
    private ShoppingMasterDAO shoppingMasterDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        ActivityPasswordChangeBinding passwordChangeActivityBinding = ActivityPasswordChangeBinding.inflate(getLayoutInflater());
        View view = passwordChangeActivityBinding.getRoot();
        setContentView(view);
        username_textView = passwordChangeActivityBinding.passwordChangeUsernameTextview;
        current_password_editText = passwordChangeActivityBinding.currentPasswordEdittext;
        new_password_editText = passwordChangeActivityBinding.newPasswordEdittext;
        confirmNew_password_editText = passwordChangeActivityBinding.confirmNewPasswordEdittext;
        passwordChange_button = passwordChangeActivityBinding.passChangeButton;
        userID = getIntent().getExtras().getInt("userIDKey", -1);
        getDatabase();
        setUser();
        passwordChange();
    }
    private void getDatabase() {
        shoppingMasterDAO = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getShoppingMasterDAO();
    }
    private void setUser(){
        user = shoppingMasterDAO.getUserByUserId(userID);
        username = user.getUserName();
        oldPassword = user.getPassword();
        username_textView.setText(username);
    }
    private boolean checkCurrentPassword(){
        currentPassword = current_password_editText.getText().toString();
        if (currentPassword.length() > 0) {
            return oldPassword.equals(currentPassword);
        }
        Toast.makeText(PasswordChangeActivity.this, "Enter current password", Toast.LENGTH_SHORT).show();
        return false;
    }
    private boolean newPasswordMatch() {
        newPassword = new_password_editText.getText().toString();
        String confirmedNewPassword = confirmNew_password_editText.getText().toString();
        if (newPassword.length() > 0) {
            if (confirmedNewPassword.length() > 0) {
                return newPassword.equals(confirmedNewPassword);
            }
            else {
                Toast.makeText(PasswordChangeActivity.this, "Confirm new password", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(PasswordChangeActivity.this, "Enter new password", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    private boolean newPasswordDiffer() {
        return newPassword.equals(currentPassword);
    }
    private void passwordChange() {
        passwordChange_button.setOnClickListener(view -> {
            if (checkCurrentPassword()) {
                if (newPasswordMatch()) {
                    if (!(newPasswordDiffer())) {
                        user.setPassword(newPassword);
                        shoppingMasterDAO.updateUser(user);
                        Toast.makeText(PasswordChangeActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = MainActivity.getIntent(getApplicationContext());
                        startActivity(intent);
                    } else {
                        Toast.makeText(PasswordChangeActivity.this, "Entered password is same as the current password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PasswordChangeActivity.this, "New passwords are not match", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(PasswordChangeActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static Intent getIntent(Context context) {
        return new Intent(context, PasswordChangeActivity.class);
    }
}