package com.easyshop.mc.shopeasy.main.Service;

import android.util.Log;

import com.easyshop.mc.shopeasy.main.Model.Beacon;
import com.easyshop.mc.shopeasy.main.Model.Product;
import com.easyshop.mc.shopeasy.main.Model.QueryModels.LatLonQuery;
import com.easyshop.mc.shopeasy.main.Model.Store;

import java.util.List;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Siddartha on 4/19/2017.
 */

public class ShopEasySeriveProvider {
    public static final String TAG = "ShopEasyServiceProvider";
    public static final String API_URL = "http://34.209.40.164:8080/ShoppingAssistant/";

    private ServiceListener productServiceListener;
    private ServiceListener beaconServiceListener;
    private ServiceListener storeServiceListener;

    private Retrofit retrofit;

    private static volatile ShopEasySeriveProvider instance = null;


    private ShopEasySeriveProvider() {
        retrofit  = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ShopEasySeriveProvider getInstance(){
        if(instance == null) {
            synchronized (ShopEasySeriveProvider.class) {
                if(instance == null)
                    instance = new ShopEasySeriveProvider();
            }
        }
        return instance;
    }

    public void setProductServiceListener(ServiceListener serviceListener) {
        this.productServiceListener = serviceListener;
    }

    public void setBeaconServiceListener(ServiceListener serviceListener) {
        this.beaconServiceListener = serviceListener;
    }

    public void setStoreServiceListener(ServiceListener serviceListener) {
        this.storeServiceListener = serviceListener;
    }

    //provider for store on lat, lon
    public void getStoreForLatLon(LatLonQuery latLonQuery){

        ShopEasyService shopEasyService = retrofit.create(ShopEasyService.class);
        Call<Store> storeServiceCall = shopEasyService.getStoreForLatLon(latLonQuery);

        Request req =  storeServiceCall.request();
        storeServiceCall.enqueue(new Callback<Store>() {
            @Override
            public void onResponse(Call<Store> call, Response<Store> response) {
                if(response.isSuccessful()){
                    storeServiceListener.onStoreSuccess(response);
                }
            }

            @Override
            public void onFailure(Call<Store> call, Throwable t) {
                Log.e(TAG, "Failed Fetching store");
            }
        });
    }

    //provider for store on lat, lon
    public void getProductsById(long storeId){

        ShopEasyService shopEasyService = retrofit.create(ShopEasyService.class);
        Call<List<Product>> productsServiceCall = shopEasyService.getProductsById(storeId);

        Request req =  productsServiceCall.request();
        productsServiceCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()){
                    productServiceListener.onProductsSuccess(response);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "Failed to fetch products");
            }
        });
    }

    //provider for beacon on storeId
    public void getBeaconsById(long storeId){
        ShopEasyService shopEasyService = retrofit.create(ShopEasyService.class);
        Call<List<Beacon>> beaconServiceCall = shopEasyService.getBeaconsById(storeId);

        Request req =  beaconServiceCall.request();
        beaconServiceCall.enqueue(new Callback<List<Beacon>>() {
            @Override
            public void onResponse(Call<List<Beacon>> call, Response<List<Beacon>> response) {
                if(response.isSuccessful()){
                    beaconServiceListener.onBeaconsSuccess(response);
                }
            }

            @Override
            public void onFailure(Call<List<Beacon>> call, Throwable t) {
            }
        });
    }

    public interface ServiceListener {
        public void onStoreSuccess(Response<Store> response);
        public void onProductsSuccess(Response<List<Product>> response);
        public void onBeaconsSuccess(Response<List<Beacon>> response);
        public void onFailure(String message);
    }
}
