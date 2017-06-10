package com.easyshop.mc.shopeasy.main.Activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


import com.easyshop.mc.shopeasy.R;
import com.easyshop.mc.shopeasy.main.DataBase.ShopEasy;
import com.easyshop.mc.shopeasy.main.DataBase.ShopEasyDBHelper;
import com.easyshop.mc.shopeasy.main.Model.AppDataModel;
import com.easyshop.mc.shopeasy.main.Model.Beacon;
import com.easyshop.mc.shopeasy.main.Model.Product;
import com.easyshop.mc.shopeasy.main.Model.Store;
import com.easyshop.mc.shopeasy.main.Service.ShopEasySeriveProvider;
import com.easyshop.mc.shopeasy.main.Service.Sync.SyncService;
import com.easyshop.mc.shopeasy.main.Util.SharedPrefsUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    public final static String EXTRA_PRODUCTS = "com.easyshop.mc.shopeasy.ProductsData";

    private Animation splashAnimation;
    private ImageView splashImage;
    private ImageView loveIconImage;

    private SharedPreferences preferences;
    public static final String LOGIN_PREF = "LoginPreferences";
    public static final String REMEMBER_ME_PREF = "rememberMe";
    public static final String EMAIL_PREF = "emailId";
    public static final String PASSWORD_PREF = "password";

    private List<Product> products;
    private Class intentAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splashImage = (ImageView)findViewById(R.id.splashLogoImage);
        loveIconImage = (ImageView)findViewById(R.id.loveImageId);

        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        if (savedInstanceState == null) {
            splashAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_image_animation);
            splashImage.startAnimation(splashAnimation);

            splashAnimation = AnimationUtils.loadAnimation(this, R.anim.love_icon_animation);
            loveIconImage.startAnimation(splashAnimation);
        }

        loadInitialData();
    }


    private void loadInitialData(){
        preferences = getSharedPreferences(LOGIN_PREF, MODE_PRIVATE);
        intentAction = null;
        boolean isRemembered = preferences.getBoolean(REMEMBER_ME_PREF, false);

        if(isRemembered && preferences.getString(EMAIL_PREF, null) != null && preferences.getString(PASSWORD_PREF, null) != null){
            intentAction = MainActivity.class;
            long storeId = SharedPrefsUtil.getSharedPrefsLong(this, SharedPrefsUtil.STORE_IN_CONTEXT);
            if(storeId!= 0){
                intentAction = StoreActivity.class;

                long lastStore = SharedPrefsUtil.getSharedPrefsLong(this, SharedPrefsUtil.LAST_STORE_SYNC);
                String lastStoreSync = SharedPrefsUtil.getSharedPrefsString(this, SharedPrefsUtil.LAST_STORE_SYNC_DATE);

                DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                Date lastStoreSyncDate = new Date();
               try{
                   lastStoreSyncDate = (Date)formatter.parse(lastStoreSync);
               } catch(ParseException pe){
                    Log.e("Splash", pe.getMessage());
               }
                int hoursDifference = (int)(new Date().getTime() - lastStoreSyncDate.getTime())/(1000*60*60);
                if(hoursDifference>24){
                    syncProductsForStore(storeId, SplashActivity.this);
                    syncBeaconsForStore(storeId, SplashActivity.this);
                    SharedPrefsUtil.saveSharedPrefsLong(this, SharedPrefsUtil.LAST_STORE_SYNC, storeId);
                    SharedPrefsUtil.saveSharedPrefsString(this, SharedPrefsUtil.LAST_STORE_SYNC_DATE, formatter.format(new Date()));
                }else{
                    AppDataModel.getAppDataModel().setProductsData(ShopEasyDBHelper.getInstance(SplashActivity.this).getProductsForStore(storeId));
                    gotoActivity();
                }
            }
        }else{
            intentAction = LoginActivity.class;
        }
        gotoActivity();
    }

    public void syncProductsForStore(long storeId, final Context context){
        ShopEasySeriveProvider.getInstance().setProductServiceListener(new ShopEasySeriveProvider.ServiceListener() {
            @Override
            public void onStoreSuccess(Response<Store> response) {

            }

            @Override
            public void onProductsSuccess(Response<List<Product>> response) {
                if(response.body()!= null){
                    List<Product> products = response.body();
                    ShopEasyDBHelper.getInstance(context).addProducts(products);
                    AppDataModel.getAppDataModel().setProductsData(products);

                   gotoActivity();
                }
            }
            @Override
            public void onBeaconsSuccess(Response<List<Beacon>> response) {

            }

            @Override
            public void onFailure(String message) {
                Log.e("Splash", "Failed fetcheing Products");
            }
        });

        ShopEasySeriveProvider.getInstance().getProductsById(storeId);
    }

    public void syncBeaconsForStore(long storeId, final Context context){
        ShopEasySeriveProvider.getInstance().setBeaconServiceListener(new ShopEasySeriveProvider.ServiceListener() {
            @Override
            public void onStoreSuccess(Response<Store> response) {

            }

            @Override
            public void onProductsSuccess(Response<List<Product>> response) {
            }

            @Override
            public void onBeaconsSuccess(Response<List<Beacon>> response) {
                if(response.body()!= null){
                    List<Beacon> beacons = response.body();
                    ShopEasyDBHelper.getInstance(context).addBeacons(beacons);
                }
            }

            @Override
            public void onFailure(String message) {

            }
        });

        ShopEasySeriveProvider.getInstance().getBeaconsById(storeId);
    }

    private void gotoActivity(){
        final Intent intent = new Intent(SplashActivity.this, intentAction);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
