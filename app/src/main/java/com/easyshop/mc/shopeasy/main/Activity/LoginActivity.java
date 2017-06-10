package com.easyshop.mc.shopeasy.main.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easyshop.mc.shopeasy.R;
import com.easyshop.mc.shopeasy.main.DataBase.ShopEasyDBHelper;
import com.easyshop.mc.shopeasy.main.Model.User;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private Button registerButton;
    private CheckBox rememberMe;
    private EditText email;
    private EditText password;
    private TextView noUserError;
    private Typeface typeFace;

    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
    }

    public void initViews(){
        typeFace = Typeface.createFromAsset(getAssets(), "fonts/CaviarDreams.ttf");
        //productName.setTypeface(typeface);4

        loginButton = (Button)findViewById(R.id.loginButton);
        registerButton = (Button)findViewById(R.id.registerButton);
        rememberMe = (CheckBox)findViewById(R.id.rememerMeId);
        email = (EditText)findViewById(R.id.userNameTextID);
        password = (EditText)findViewById(R.id.passwordTextID);
        noUserError = (TextView)findViewById(R.id.noUserError);
    }

    public void onLogin(View view){
        String emailIdValue = email.getText().toString();
        String passwordValue = password.getText().toString();

        if(hasLoginInfo()){
            User user = ShopEasyDBHelper.getInstance(this).getUserIfExist(emailIdValue);
            if(user != null){
                if(user.getPassword().equals(passwordValue)){
                    preferences = getSharedPreferences(SplashActivity.LOGIN_PREF, MODE_PRIVATE);
                    preferencesEditor = preferences.edit();

                    if(rememberMe.isChecked()){
                        preferencesEditor.putBoolean(SplashActivity.REMEMBER_ME_PREF, true);
                        preferencesEditor.putString(SplashActivity.EMAIL_PREF, emailIdValue);
                        preferencesEditor.putString(SplashActivity.PASSWORD_PREF, passwordValue);

                        preferencesEditor.commit();
                    }else{
                        preferencesEditor.clear();
                        preferencesEditor.commit();
                    }
                    Toast.makeText(this, "Logging in as "+ emailIdValue, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(this, "Invalid Password! Try again!", Toast.LENGTH_LONG).show();
                }
            }else{
                noUserError.setVisibility(View.VISIBLE);
            }
        }
    }
    private boolean hasLoginInfo(){
        boolean isValid = true;
        if(email.getText().toString()==null || email.getText().toString().equals("")){
            email.setError("Enter Email Id to proceed");
            isValid = false;
        }
        if(password.getText().toString()==null || password.getText().toString().equals("")){
            password.setError("Enter Password to proceed");
            isValid = false;
        }
        return isValid;
    }
    public void onRegister(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        this.finish();
    }
}
