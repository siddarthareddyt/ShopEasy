package com.easyshop.mc.shopeasy.main.Model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.easyshop.mc.shopeasy.BR;

/**
 * Created by Siddartha on 3/31/2017.
 */

public class User extends BaseObservable {

    private String name;
    private String emailId;
    private String password;
    private boolean male;
    private boolean female;

    public static final String MALE = "male";
    public static final String FEMALE = "female";

    public User(){}

    public User(String name, String emailId, String password, boolean male, boolean female){
        this.name = name;
        this.emailId = emailId;
        this.password = password;
        this.male = male;
        this.female = female;
    }

    @Bindable
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
        notifyPropertyChanged(BR.emailId);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    @Bindable
    public boolean getMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
        notifyPropertyChanged(BR.male);
    }

    @Bindable
    public boolean getFemale() {
        return female;
    }

    public void setFemale(boolean isFemale) {
        this.female = isFemale;
        notifyPropertyChanged(BR.female);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    //public boolean

}
