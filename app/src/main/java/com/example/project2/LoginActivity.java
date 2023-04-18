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
import com.example.project2.databinding.ActivityLoginBinding;
import com.example.project2.db.AppDatabase;
import com.example.project2.db.ShoppingMasterDAO;
/**
 * Author: Nima Mahanloo
 * Title: Shop Master: Login
 * Date: April 10, 2023
 * It is an activity of my CST 338 Project 2,
 * which is in use for account login page.
 */
public class LoginActivity extends AppCompatActivity {
    private EditText login_username_editText;
    private EditText login_password_editText;
    private Button login_button;
    private TextView login_signup_textView;
    private String login_username;
    private String login_password;
    private long buttonPressTime;
    private User user;
    private ShoppingMasterDAO shoppingMasterDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActivityLoginBinding loginActivityBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = loginActivityBinding.getRoot();
        setContentView(view);
        login_username_editText = loginActivityBinding.loginUsernameEdittext;
        login_password_editText = loginActivityBinding.loginPasswordEdittext;
        login_button = loginActivityBinding.loginButton;
        login_signup_textView = loginActivityBinding.signupTextView;
        userLogin();
        getDatabase();
        userSignUp();
    }
    private void getDatabase(){
        shoppingMasterDAO = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getShoppingMasterDAO();
    }
    private boolean getValuesFromDisplay(){
        login_username = login_username_editText.getText().toString();
        login_password = login_password_editText.getText().toString();
        if (login_username.length() > 0) {
            if (login_password.length() > 0) {
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
        user = shoppingMasterDAO.getUserByUsername(login_username);
        if(user == null){
            Toast.makeText(this, "Invalid username", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean validatePassword(){
        return user.getPassword().equals(login_password);
    }
    private void userLogin(){
        login_button.setOnClickListener(v -> {
            if (getValuesFromDisplay()) {
                if (checkForUserInDatabase()) {
                    if (!validatePassword()) {
                        Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = MainActivity.intentFactory(getApplicationContext(), user.getUserId());
                        startActivity(intent);
                    }
                }
            }
        });
    }
    private void userSignUp(){
        login_signup_textView.setOnClickListener(view -> {
            Intent intent = SignUpActivity.getIntent(getApplicationContext());
            startActivity(intent);
        });
    }
    // Exit app by pressing back button.
    @Override
    public void onBackPressed() {
        if (buttonPressTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
            System.exit(0);
        } else {
            Toast.makeText(getBaseContext(), "Press back button again to exit", Toast.LENGTH_SHORT).show();
        }
        buttonPressTime = System.currentTimeMillis();
    }
    public static Intent getIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }
}