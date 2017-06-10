package com.easyshop.mc.shopeasy.main.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Siddartha on 4/20/2017.
 */

public class Beacon {
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("store_id")
    @Expose
    private long storeId;
    @SerializedName("map_index")
    @Expose
    private int mapIndex;
    @SerializedName("defProductId")
    @Expose
    private long defProductId;
    @SerializedName("x")
    @Expose
    private double x;
    @SerializedName("y")
    @Expose
    private double y;
    @SerializedName("primary")
    @Expose
    private boolean primary;

    public Beacon(long id, String name, long storeId, int mapIndex, long defProductId, double x, double y, boolean primary) {
        this.id = id;
        this.name = name;
        this.storeId = storeId;
        this.mapIndex = mapIndex;
        this.defProductId = defProductId;
        this.x = x;
        this.y = y;
        this.primary = primary;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStoreId() {
        return storeId;
    }

    public void setStoreId(long storeId) {
        this.storeId = storeId;
    }

    public int getMapIndex() {
        return mapIndex;
    }

    public void setMapIndex(int mapIndex) {
        this.mapIndex = mapIndex;
    }

    public long getDefProductId() {
        return defProductId;
    }

    public void setDefProductId(long defProductId) {
        this.defProductId = defProductId;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean getPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }
}
