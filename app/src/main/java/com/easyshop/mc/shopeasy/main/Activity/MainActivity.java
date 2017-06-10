package com.easyshop.mc.shopeasy.main.Activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easyshop.mc.shopeasy.R;
import com.easyshop.mc.shopeasy.main.DataBase.ShopEasyDBHelper;
import com.easyshop.mc.shopeasy.main.Model.AppDataModel;
import com.easyshop.mc.shopeasy.main.Model.Beacon;
import com.easyshop.mc.shopeasy.main.Model.Product;
import com.easyshop.mc.shopeasy.main.Model.Store;
import com.easyshop.mc.shopeasy.main.Service.BeaconService;
import com.easyshop.mc.shopeasy.main.Service.LocationService;
import com.easyshop.mc.shopeasy.main.Service.ShopEasySeriveProvider;
import com.easyshop.mc.shopeasy.main.Util.SharedPrefsUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String NOTIFICATION_EXTRA = "StoreId";
    private static final int MY_PERMISSIONS_REQUEST = 1;
    private ImageView storeImage;
    private TextView storeName;
    private TextView storeProxim;
    private Button startShopping;
    private Button ignore;
    RelativeLayout storeSelectionLayout;
    RelativeLayout noStoresLayout;

    Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initListeners();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                                Manifest.permission.BLUETOOTH,
                                Manifest.permission.BLUETOOTH_ADMIN,
                                Manifest.permission.BLUETOOTH_PRIVILEGED},MY_PERMISSIONS_REQUEST);

            }else{
                onNewIntent(getIntent());
            }
        }else{
            onNewIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(NOTIFICATION_EXTRA)) {
                long storeId = extras.getLong(NOTIFICATION_EXTRA);
                store = ShopEasyDBHelper.getInstance(MainActivity.this).getStoreById(storeId);

                if(store != null && store.getStoreName() != null){
                    storeSelectionLayout.setVisibility(View.VISIBLE);
                    noStoresLayout.setVisibility(View.GONE);
                    storeName.setText(store.getStoreName());
                    storeProxim.setText("Nearly 100m away");
                }

            }
        }else{
            if(!LocationService.isServiceStarted){
                Intent serviceIntent = new Intent(getApplicationContext(), LocationService.class);
                startService(serviceIntent);
            }

        }
    }

    protected void initViews(){
        storeImage = (ImageView) findViewById(R.id.storeImage);
        storeName = (TextView)findViewById(R.id.storeName);
        storeProxim = (TextView)findViewById(R.id.storeProxim);
        startShopping = (Button)findViewById(R.id.startShopping);
        ignore = (Button)findViewById(R.id.ignore);
        storeSelectionLayout = (RelativeLayout)findViewById(R.id.storeSelection);
        noStoresLayout = (RelativeLayout)findViewById(R.id.noStores);
    }

    protected void initListeners(){
        startShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(), LocationService.class));
                SharedPrefsUtil.saveSharedPrefsStore(MainActivity.this, store.getId());

                syncProductsForStore(store.getId(), MainActivity.this);
                syncBeaconsForStore(store.getId(), MainActivity.this);

                DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");

                SharedPrefsUtil.saveSharedPrefsLong(MainActivity.this, SharedPrefsUtil.LAST_STORE_SYNC, store.getId());
                SharedPrefsUtil.saveSharedPrefsString(MainActivity.this, SharedPrefsUtil.LAST_STORE_SYNC_DATE, formatter.format(new Date()));

            }
        });

        ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeSelectionLayout.setVisibility(View.GONE);
                noStoresLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults.length == 6) {
                    onNewIntent(getIntent());
                } else {
                    Toast.makeText(MainActivity.this, "App needs all the permissions!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
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

                    Intent intent = new Intent(MainActivity.this, StoreActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onBeaconsSuccess(Response<List<Beacon>> response) {

            }

            @Override
            public void onFailure(String message) {
                Log.e("Main","Failed to Fetch Products");
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

                    Intent intent = new Intent(MainActivity.this, BeaconService.class);
                    startService(intent);
                   // AppDataModel.getAppDataModel().setProductsData(products);
                }
            }

            @Override
            public void onFailure(String message) {

            }
        });

        ShopEasySeriveProvider.getInstance().getBeaconsById(storeId);
    }
}
