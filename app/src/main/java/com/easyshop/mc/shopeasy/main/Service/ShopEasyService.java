package com.easyshop.mc.shopeasy.main.Service;

import com.easyshop.mc.shopeasy.main.Model.Beacon;
import com.easyshop.mc.shopeasy.main.Model.Product;
import com.easyshop.mc.shopeasy.main.Model.QueryModels.LatLonQuery;
import com.easyshop.mc.shopeasy.main.Model.Store;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Siddartha on 4/19/2017.
 */

public interface ShopEasyService {

    //to get single store based on lat, lon
    @POST("store/locate/")
    Call<Store> getStoreForLatLon(@Body LatLonQuery latLonQuery);

    @GET("product/list/{storeId}")
    Call<List<Product>> getProductsById(@Path("storeId") long storeId);

    @GET("beacon/list/{storeId}")
    Call<List<Beacon>> getBeaconsById(@Path("storeId") long storeId);
}
