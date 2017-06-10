package com.easyshop.mc.shopeasy.main.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Siddartha on 4/1/2017.
 */

public class Product {

    @SerializedName("pid")
    @Expose
    private long pid;
    @SerializedName("store_id")
    @Expose
    private long storeId;
    @SerializedName("pname")
    @Expose
    private String pname;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("price")
    @Expose
    private double price;
    @SerializedName("discount")
    @Expose
    private double discount;
    @SerializedName("beacon_id")
    @Expose
    private int beaconId;
    @SerializedName("recoProducts")
    @Expose
    private String recoProducts;

    public Product(long pid,
                   long storeId,
                   String pname,
                   String category,
                   double price,
                   double discount,
                   int beaconId,
                   String recoProducts ){
        this.pid = pid;
        this.storeId = storeId;
        this.pname = pname;
        this.category = category;
        this.price = price;
        this.discount = discount;
        this.beaconId = beaconId;
        this.recoProducts = recoProducts;

    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public long getStoreId() {
        return storeId;
    }

    public void setStoreId(long storeId) {
        this.storeId = storeId;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(int beaconId) {
        this.beaconId = beaconId;
    }

    public String getRecoProducts() {
        return recoProducts;
    }

    public void setRecoProducts(String recoProducts) {
        this.recoProducts = recoProducts;
    }
}
