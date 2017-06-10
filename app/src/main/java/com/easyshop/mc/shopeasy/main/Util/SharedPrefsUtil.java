package com.easyshop.mc.shopeasy.main.Util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * Created by Siddartha on 4/19/2017.
 */

public class SharedPrefsUtil {
    public final static String TAG = "SharedPrefsUtil";
    public static final String SHOPEASY_PREF = "ShopEasyPreferences";
    public static final String REMEMBER_ME_PREF = "rememberMe";
    public static final String EMAIL_PREF = "emailId";
    public static final String PASSWORD_PREF = "password";
    public static final String STORE_IN_CONTEXT = "storeInContext";
    public static final String PRIMARY_VISITED ="primaryVisited";
    public static final String LAST_STORE_SYNC ="lastStoreSync";
    public static final String LAST_STORE_SYNC_DATE ="lastStoreSyncDate";


    private SharedPrefsUtil(){}

    public static void saveSharedPrefsAccount(Context context, String emailId, String password, boolean rememberMe) {
        SharedPreferences  sharedPreferences = context.getSharedPreferences(SHOPEASY_PREF, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(EMAIL_PREF, emailId);
        editor.putString(emailId, password);
        editor.putBoolean(emailId, rememberMe);
        editor.commit();
    }

    public static void saveSharedPrefsStore(Context context, long storeId){
        SharedPreferences  sharedPreferences = context.getSharedPreferences(SHOPEASY_PREF, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(STORE_IN_CONTEXT, storeId);
        editor.commit();
    }

    public static void saveSharedPrefsPrimaryBeacon(Context context, boolean visited){
        SharedPreferences  sharedPreferences = context.getSharedPreferences(SHOPEASY_PREF, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PRIMARY_VISITED, visited);
        editor.commit();
    }

    public static void saveSharedPrefsString(Context context, String key, String value){
        SharedPreferences  sharedPreferences = context.getSharedPreferences(SHOPEASY_PREF, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void saveSharedPrefsLong(Context context, String key, long value){
        SharedPreferences  sharedPreferences = context.getSharedPreferences(SHOPEASY_PREF, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static String getSharedPrefsString(Context context, String key) {
        SharedPreferences  sharedPreferences = context.getSharedPreferences(SHOPEASY_PREF, context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public static boolean getSharedPrefsBoolean(Context context, String key) {
        SharedPreferences  sharedPreferences = context.getSharedPreferences(SHOPEASY_PREF, context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public static long getSharedPrefsLong(Context context, String key) {
        SharedPreferences  sharedPreferences = context.getSharedPreferences(SHOPEASY_PREF, context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, 0);
    }

}
