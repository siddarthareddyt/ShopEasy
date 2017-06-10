package com.easyshop.mc.shopeasy.main.Service.Sync;

import android.content.Context;

import com.easyshop.mc.shopeasy.main.Activity.SplashActivity;
import com.easyshop.mc.shopeasy.main.DataBase.ShopEasyDBHelper;
import com.easyshop.mc.shopeasy.main.Model.AppDataModel;
import com.easyshop.mc.shopeasy.main.Model.Beacon;
import com.easyshop.mc.shopeasy.main.Model.Product;
import com.easyshop.mc.shopeasy.main.Model.Store;
import com.easyshop.mc.shopeasy.main.Service.ShopEasySeriveProvider;

import java.util.List;

import retrofit2.Response;

/**
 * Created by Siddartha on 4/20/2017.
 */

public class SyncService {
    public static void syncProductsForStore(long storeId, final Context context){
        ShopEasySeriveProvider.getInstance().setStoreServiceListener(new ShopEasySeriveProvider.ServiceListener() {
            @Override
            public void onStoreSuccess(Response<Store> response) {

            }

            @Override
            public void onProductsSuccess(Response<List<Product>> response) {
                if(response.body()!= null){
                   List<Product> products = response.body();
                    ShopEasyDBHelper.getInstance(context).addProducts(products);
                    AppDataModel.getAppDataModel().setProductsData(products);
                }
            }

            @Override
            public void onBeaconsSuccess(Response<List<Beacon>> response) {

            }
            @Override
            public void onFailure(String message) {

            }
        });

        ShopEasySeriveProvider.getInstance().getProductsById(storeId);
    }
    public static void syncBeaconsForStore(long storeId, final Context context){

    }
}
