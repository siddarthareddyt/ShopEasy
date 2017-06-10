package com.easyshop.mc.shopeasy.main.core;

/**
 * Created by DurgaPrasad on 20-04-2017.
 */

import com.easyshop.mc.shopeasy.main.DataBase.ShopEasyDBHelper;
import com.easyshop.mc.shopeasy.main.Model.Product;
import com.easyshop.mc.shopeasy.main.Model.RecoProduct;
import com.easyshop.mc.shopeasy.main.Model.WishlistProduct;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple class for fetching recoProducts and populating them into RecoProduct table
 */
public class RecoEngine {

    public static void populateRecoProducts(long storeId){
        ShopEasyDBHelper dbHelper = ShopEasyDBHelper.getInstance(null);
        List<WishlistProduct> wishlistProducts = dbHelper.getWishlistProducts();
        Product p = null;
        long recoProductId;
        for ( WishlistProduct wp : wishlistProducts ) {
            p = dbHelper.getProduct(storeId, wp.getProductId());
            recoProductId = ( p.getRecoProducts() == null || p.getRecoProducts() == "" ) ? -1 : Long.parseLong(p.getRecoProducts());
           if(recoProductId!=-1)
                dbHelper.addRecoProduct(new RecoProduct(recoProductId));
        }

    }
}