package com.easyshop.mc.shopeasy.main.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Siddartha on 4/19/2017.
 */

public class AppDataModel {

    private static AppDataModel appDataModel = null;

    public static AppDataModel getAppDataModel(){
        if(appDataModel == null) {
            synchronized (AppDataModel.class) {
                if(appDataModel == null)
                    appDataModel = new AppDataModel();
            }
        }
        return appDataModel;
    }

    //initial data
    private List<Product> productsData = Collections.synchronizedList( new ArrayList<Product>());

    //current displayin product
    private Product currentProduct;

    public List<Product> getProductsData() {
        return productsData;
    }



    public void setProductsData(List<Product> productsData) {
        this.productsData = productsData;
    }

    public static void setAppDataModel(AppDataModel appDataModel) {
        AppDataModel.appDataModel = appDataModel;
    }

    private int cartItems;

    public int getCartItems() {
        return cartItems;
    }

    public void setCartItems(int cartItems) {
        this.cartItems = cartItems;
    }
    public boolean productsListExists(){
        return !productsData.isEmpty();
    }

    public Product getCurrentProduct() {
        return currentProduct;
    }

    public void setCurrentProduct(Product currentProduct) {
        this.currentProduct = currentProduct;
    }

    public void addOneToCart(){
        this.cartItems++;
    }

    public void removeOneCartItems(){
        this.cartItems--;
    }

}
