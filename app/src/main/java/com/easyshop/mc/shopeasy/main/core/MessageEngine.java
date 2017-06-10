package com.easyshop.mc.shopeasy.main.core;

import com.easyshop.mc.shopeasy.main.DataBase.ShopEasy;
import com.easyshop.mc.shopeasy.main.DataBase.ShopEasyDBHelper;
import com.easyshop.mc.shopeasy.main.Model.Beacon;
import com.easyshop.mc.shopeasy.main.Model.Product;
import com.easyshop.mc.shopeasy.main.Model.Store;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DurgaPrasad on 19-04-2017.
 */

public class MessageEngine {

    private static MessageEngine instance = null;
    public static String messageSource = "";

    private MessageEngine(){

    }

    public static synchronized MessageEngine getInstance(){
        if(instance == null){
            instance = new MessageEngine();
        }
        return instance;
    }

    public List<String> getMessages(long storeId, long beaconId){
        List<String> messages = new ArrayList<String>();
        ShopEasyDBHelper dbHelper = ShopEasyDBHelper.getInstance(null);

        Store store = dbHelper.getStoreById(storeId);
        String genericMessage = store.getRecoMessage();

        List<Product> wishlistItems = dbHelper.getWishlistProductsNearBeacon(beaconId);
        if(wishlistItems!= null && wishlistItems.size() > 0){
            messageSource = ShopEasy.WISHLIST_MSG;
            return getCustomMessages(wishlistItems, genericMessage);
        }

        List<Product> recoItems = dbHelper.getRecoProductsNearBeacon(beaconId);
        if(recoItems!= null && recoItems.size() > 0){
            messageSource = ShopEasy.RECO_MSG;
            return getCustomMessages(recoItems, genericMessage);
        }

        Beacon beacon = dbHelper.getBeacon(beaconId);
        Product defaultProduct = dbHelper.getProduct(storeId, beacon.getDefProductId());

        genericMessage = genericMessage.replace("{pName}", defaultProduct.getPname());
        genericMessage = genericMessage.replace("{discount}", String.valueOf(defaultProduct.getDiscount()));
        genericMessage = genericMessage.replace("{price}", String.valueOf(defaultProduct.getPrice()));

        messages.add(genericMessage);
        messageSource = ShopEasy.DEFAULT_PRODUCT_MSG;
        return messages;
    }

    private List<String> getCustomMessages(List<Product> products, String genericMessage){
        List<String> messages = new ArrayList<String>();

        String tempMsg = "";
        for (Product p : products){
            tempMsg = genericMessage;
            tempMsg = tempMsg.replace("{pName}", p.getPname());
            tempMsg = tempMsg.replace("{discount}", String.valueOf(p.getDiscount()));
            tempMsg = tempMsg.replace("{price}", String.valueOf(p.getPrice()));

            messages.add(tempMsg);
        }

        return messages;
    }
}