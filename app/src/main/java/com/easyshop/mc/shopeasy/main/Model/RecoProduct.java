package com.easyshop.mc.shopeasy.main.Model;

/**
 * Created by Siddartha on 4/19/2017.
 */

public class RecoProduct {

    private long productId;

    public RecoProduct(){

    }

    public RecoProduct(long productId) {
        this.productId = productId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }
}
