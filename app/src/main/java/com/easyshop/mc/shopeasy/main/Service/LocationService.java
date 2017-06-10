package com.easyshop.mc.shopeasy.main.Service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.easyshop.mc.shopeasy.R;
import com.easyshop.mc.shopeasy.main.Activity.MainActivity;
import com.easyshop.mc.shopeasy.main.Activity.MapActivity;
import com.easyshop.mc.shopeasy.main.DataBase.ShopEasyDBHelper;
import com.easyshop.mc.shopeasy.main.Model.Beacon;
import com.easyshop.mc.shopeasy.main.Model.Product;
import com.easyshop.mc.shopeasy.main.Model.QueryModels.LatLonQuery;
import com.easyshop.mc.shopeasy.main.Model.Store;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Response;

/**
 * Created by Siddartha on 4/1/2017.
 */
//Reference : Used https://developer.android.com/training/location/index.html as a guide to implement this
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    protected static final String TAG = "LocationService";
    public static final long UPDATE_INTERVAL = 60000;
    public static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2;
    public static boolean isServiceStarted = false;

    protected GoogleApiClient googleApiClient;
    protected LocationRequest locationRequest;
    protected Location currentLocation;

    protected Boolean requestInProgress;
    protected String lastUpdateTime;

    @Override
    public void onCreate() {
        super.onCreate();
        requestInProgress = false;
        lastUpdateTime = "";
        Log.e(TAG, "service started: " );
        buildGoogleApiClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(googleApiClient != null){
            isServiceStarted = true;
            googleApiClient.connect();
            if (googleApiClient.isConnected()) {
                startLocationUpdates();
            }
        }
        Log.e(TAG, "onstartcommand: " );
        return Service.START_STICKY;

    }

    @Override
    public void onDestroy() {
        if(googleApiClient != null){
            googleApiClient.disconnect();
            super.onDestroy();
            isServiceStarted = false;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (currentLocation == null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                lastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                Log.e(TAG, "connected: " );

            }else{
                Toast.makeText(getApplicationContext(),"Permissions Denied",Toast.LENGTH_SHORT).show();
            }
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

        if(googleApiClient != null)
            googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        lastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        checkForStoreInLocation(location);
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();

    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        }else{
            Log.e(TAG,"Permissions no");
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        PendingIntent service = PendingIntent.getService(
                getApplicationContext(),
                1001,
                new Intent(getApplicationContext(), LocationService.class),
                PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
        Log.e(TAG, "onTaskRemoved: " );
    }

    private Thread.UncaughtExceptionHandler defaultUEH;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            Log.d(TAG, "Uncaught exception start!");
            ex.printStackTrace();
            PendingIntent service = PendingIntent.getService(
                    getApplicationContext(),
                    1001,
                    new Intent(getApplicationContext(), LocationService.class),
                    PendingIntent.FLAG_ONE_SHOT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
            System.exit(2);
        }
    };

    private void checkForStoreInLocation(Location location){
        ShopEasySeriveProvider.getInstance().setStoreServiceListener(new ShopEasySeriveProvider.ServiceListener() {

            @Override
            public void onStoreSuccess(Response<Store> response) {
                Log.e("adfa", "filae");
                if(response.body() != null){
                    Store store = response.body();
                    ShopEasyDBHelper.getInstance(getApplicationContext()).addStore(store);
                    showNotification(store);
                }
            }

            @Override
            public void onBeaconsSuccess(Response<List<Beacon>> response) {

            }
            @Override
            public void onProductsSuccess(Response<List<Product>> response) {

            }

            @Override
            public void onFailure(String message) {
                Log.e("adfa", "filae");
            }
        });
        LatLonQuery latLonQuery = new LatLonQuery(location.getLatitude(), location.getLongitude());

        ShopEasySeriveProvider.getInstance().getStoreForLatLon(latLonQuery);

    }

    private void showNotification(Store store){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.app_logo)
                        .setContentTitle("Shop Easy found a Store Near you!")
                        .setContentText(store.getStoreName() +" is nearby you. Click to start easy shopping");

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("StoreId", store.getId());
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify((int)store.getId(), mBuilder.build());

    }
}
