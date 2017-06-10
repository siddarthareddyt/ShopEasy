package com.easyshop.mc.shopeasy.main.Model;

/**
 * Created by Siddartha on 4/19/2017.
 */

public class WishlistProduct {
    private long productId;
    private int productTaken = 0;

    public WishlistProduct(){

    }

    public WishlistProduct(long productId) {
        this.productId = productId;
    }

    public int getProductTaken(){
        return productTaken;
    }

    public void setProductTaken(int productTaken){
        productTaken = productTaken;
    }
    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }
}
