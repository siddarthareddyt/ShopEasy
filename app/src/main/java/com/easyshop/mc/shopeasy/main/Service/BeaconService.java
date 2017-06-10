package com.easyshop.mc.shopeasy.main.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.easyshop.mc.shopeasy.R;
import com.easyshop.mc.shopeasy.main.Activity.MainActivity;
import com.easyshop.mc.shopeasy.main.Activity.MapActivity;
import com.easyshop.mc.shopeasy.main.Activity.SplashActivity;
import com.easyshop.mc.shopeasy.main.DataBase.ShopEasy;
import com.easyshop.mc.shopeasy.main.DataBase.ShopEasyDBHelper;
import com.easyshop.mc.shopeasy.main.Model.Beacon;
import com.easyshop.mc.shopeasy.main.Model.BleAdvertisedData;
import com.easyshop.mc.shopeasy.main.Model.Product;
import com.easyshop.mc.shopeasy.main.Util.BleUtil;
import com.easyshop.mc.shopeasy.main.Util.SharedPrefsUtil;
import com.easyshop.mc.shopeasy.main.core.MessageEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Siddartha on 4/20/2017.
 */

public class BeaconService extends Service implements BluetoothAdapter.LeScanCallback{

    private final static String TAG = BeaconService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;

    private final String BLE_DEVICE_NAME= "ShopEasy";
    private static final long SCAN_PERIOD = 10000;

    private HashMap<String, String> beacons = new HashMap<String, String>();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
       // populateBeacons();
        mBluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = (BluetoothAdapter) mBluetoothManager.getAdapter();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Bluetooth Not Supported on the phone!", Toast.LENGTH_SHORT).show();
            stopSelf();
        }else{
            Toast.makeText(this, mBluetoothAdapter.getName(), Toast.LENGTH_SHORT).show();

            if(!mBluetoothAdapter.isEnabled()){
                enableDisableBluetooth(true);
            }
            if(mBluetoothAdapter!=null){
                scanLeDevice(true);
            }else{
                stopSelf();
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopBLEscan();
        super.onDestroy();
    }

    @Override
    public boolean stopService(Intent name) {
        stopSelf();
        return super.stopService(name);
    }

    public void startBLEscan(){
        mBluetoothAdapter.startLeScan(this);
    }

    public void stopBLEscan(){
        mBluetoothAdapter.stopLeScan(this);
    }

    public void scanLeDevice(final boolean enable) {
        if (enable) {
            startBLEscan();
        } else {
            stopBLEscan();
        }
    }

    public static void enableDisableBluetooth(boolean enable){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if(enable) {
                bluetoothAdapter.enable();
            }else{
                bluetoothAdapter.disable();
            }
        }
    }

    @Override
    public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
        if(device!=null){
            String address = device.getAddress();
           if(device.getName() !=null && device.getName().contains(BLE_DEVICE_NAME)){
                   Beacon beacon = ShopEasyDBHelper.getInstance(getApplicationContext()).getBeaconIdByName(device.getName());
                   if(beacon != null){
                       checkAndNotify(beacon);
                   }
           }
        }
    }

    private void checkAndNotify(Beacon beacon){
        long storeId = SharedPrefsUtil.getSharedPrefsLong(getApplicationContext(), SharedPrefsUtil.STORE_IN_CONTEXT);
        if(beacon.getPrimary()){
            boolean isAlreadyVisited = SharedPrefsUtil.getSharedPrefsBoolean(getApplicationContext(), SharedPrefsUtil.PRIMARY_VISITED);
            if(isAlreadyVisited){
                showShoppingDoneNotification(beacon);
            }else{
                SharedPrefsUtil.saveSharedPrefsPrimaryBeacon(getApplicationContext(), !isAlreadyVisited);
            }
        }else{
            List<String> messages = MessageEngine.getInstance().getMessages(storeId, beacon.getId());
            if(!messages.isEmpty()){
                showNotification(messages, beacon);
            }
        }

    }

    private void showShoppingDoneNotification(Beacon primaryBeacon){

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.app_logo)
                        .setContentTitle("Are you finished Shopping?")
                        .setContentText("Seems like you reached the billing counter!");

        Intent resultIntent = new Intent(this, MapActivity.class);
        resultIntent.putExtra(MapActivity.NOTIFICATION_EXTRA_BEACON, primaryBeacon.getId());
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MapActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify((int)primaryBeacon.getId(), mBuilder.build());
    }

    private void showNotification(List<String> messages, Beacon beacon){


        String title = "";
        String subTitle = "";
        if(MessageEngine.messageSource.equals(ShopEasy.DEFAULT_PRODUCT_MSG)){
            subTitle="We have an offer";
        }else if(MessageEngine.messageSource.equals(ShopEasy.WISHLIST_MSG)){
            subTitle="Product you are looking for!";
        }else{
            subTitle="We have a Recommendation for you!";
        }

        for(String message: messages)
            title+=message+ System.lineSeparator();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.app_logo)
                        .setContentTitle(subTitle)
                        .setContentText(title);

        List<Product> wishlistItems = ShopEasyDBHelper.getInstance(this).getWishlistProductsNearBeacon(beacon.getId());
        ArrayList<String> productIds = new ArrayList<>();
        for(Product product: wishlistItems){
                productIds.add(String.valueOf(product.getPid()));
        }


        Intent resultIntent = new Intent(this, MapActivity.class);
        resultIntent.putExtra(MapActivity.NOTIFICATION_EXTRA_BEACON, beacon.getId());
        resultIntent.putStringArrayListExtra(MapActivity.NOTIFICATION_EXTRA_PRODUCTS, productIds);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MapActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify((int)beacon.getId(), mBuilder.build());
    }
}
