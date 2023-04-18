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
import com.example.project2.databinding.ActivitySignUpBinding;
import com.example.project2.db.ShoppingMasterDAO;
import com.example.project2.db.AppDatabase;
/**
 * Author: Nima Mahanloo
 * Title: Shop Master: Sign Up
 * Date: April 10, 2023
 * It is an activity of my CST 338 Project 2,
 * which is in use to register a new user account
 * and insert a new record into the USER_TABLE.
 */
public class SignUpActivity extends AppCompatActivity {
    EditText signUp_username_editText;
    EditText signUp_password_editText;
    Button signUp;
    String signUp_username;
    String signUp_password;
    ShoppingMasterDAO shoppingMasterDAO;
    ActivitySignUpBinding signupActivityBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signupActivityBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = signupActivityBinding.getRoot();
        setContentView(view);
        signUp_username_editText= signupActivityBinding.signupUsernameEdittext;
        signUp_password_editText = signupActivityBinding.signupPasswordEdittext;
        signUp = signupActivityBinding.signupButton;
        newSignUp();
        getDatabase();
    }
    private boolean getValuesFromDisplay(){
        signUp_username = signUp_username_editText.getText().toString();
        signUp_password = signUp_password_editText.getText().toString();
        if (signUp_username.length() > 0) {
            if (signUp_password.length() > 0) {
                return true;
            }
            else {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            }
        }
        Toast.makeText(this, "Enter username", Toast.LENGTH_SHORT).show();
        return false;
    }
    private boolean checkForUserInDatabase(){
        User user = shoppingMasterDAO.getUserByUsername(signUp_username);
        if(!(user == null)){
            Toast.makeText(SignUpActivity.this, "Username " + signUp_username + " has already taken", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void getDatabase(){
        shoppingMasterDAO = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getShoppingMasterDAO();
    }
    private void newSignUp() {
        signUp.setOnClickListener(view -> {
            if (getValuesFromDisplay()) {
                if (checkForUserInDatabase()) {
                    User newUser = new User(signUp_username, signUp_password);
                    shoppingMasterDAO.insertUser(newUser);
                    Toast.makeText(SignUpActivity.this, "Account " + signUp_username + " registered successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = LoginActivity.getIntent(getApplicationContext());
                    startActivity(intent);
                } else {
                    Toast.makeText(SignUpActivity.this, "Choose another username", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public static Intent getIntent(Context context) {
        return new Intent(context, SignUpActivity.class);
    }
}