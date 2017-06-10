package com.easyshop.mc.shopeasy.main.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.easyshop.mc.shopeasy.R;
import com.easyshop.mc.shopeasy.databinding.ActivityRegisterBinding;
import com.easyshop.mc.shopeasy.main.DataBase.ShopEasyDBHelper;
import com.easyshop.mc.shopeasy.main.Model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private User user;
    private EditText confirmPassword;
    private CheckBox rememberMe;
    private boolean userExists;
    ActivityRegisterBinding registerBinding;

    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        confirmPassword = (EditText)findViewById(R.id.confirmpasswordId);
        rememberMe = (CheckBox)findViewById(R.id.rememerMeId);

        user = new User();
        user.setMale(true);

        registerBinding.setUser(user);
        registerBinding.setUserExists(userExists);
    }

    public void onRegisterAccount(View view){
        if(hasFillInInfo()){
            if(ShopEasyDBHelper.getInstance(this).getUserIfExist(user.getEmailId()) == null){
                if(isValidEmail(user.getEmailId()) && passwordsMatch()){
                    if(ShopEasyDBHelper.getInstance(this).addUserAccount(user))
                    {
                        String emailIdValue = user.getEmailId();
                        String passwordValue = user.getPassword();

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

                        Toast.makeText(this, "Logging in as "+ user.getEmailId(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(this, "Unable to Add User Account, Please try later!", Toast.LENGTH_LONG).show();
                }
                userExists = false;
            }else{
                userExists = true;
            }
        }
    }

    private boolean hasFillInInfo(){
        boolean isValid = true;
        if(user.getName()==null || user.getName().isEmpty()){
            registerBinding.userName.setError("Enter Name to proceed");
            isValid = false;
        }
        if(user.getEmailId() == null || user.getEmailId().isEmpty()){
            registerBinding.emailId.setError("Email ID is a mandatory field");
            isValid = false;
        }
        if(user.getPassword() == null || user.getPassword().isEmpty()){
            registerBinding.passwordId.setError("Password is a mandatory field");
            isValid = false;
        }
        if( confirmPassword.getText() == null || confirmPassword.getText().equals("")){
            registerBinding.confirmpasswordId.setError("Password is a mandatory field");
            isValid = false;
        }

        return isValid;
    }
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches()){
            return true;
        }
        registerBinding.emailId.setError("Invalid EmailID");
        return false;
    }

    private boolean passwordsMatch(){
        if(user.getPassword().equals(confirmPassword.getText().toString())){
            return true;
        }
        registerBinding.passwordId.setError("Passwords Mismatch");
        confirmPassword.setError("Passwords Mismatch");
        return false;
    }
}
