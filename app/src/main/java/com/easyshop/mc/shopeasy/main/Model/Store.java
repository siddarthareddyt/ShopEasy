package com.easyshop.mc.shopeasy.main.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Siddartha on 4/18/2017.
 */

public class Store {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("storeName")
    @Expose
    private String storeName;
    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("primaryBeaconId")
    @Expose
    private int primaryBeaconId;
    @SerializedName("map")
    @Expose
    private String map;
    @SerializedName("recoMessage")
    @Expose
    private String recoMessage;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("lastRecoEngineSyncTime")
    @Expose
    private long lastRecoEngineSyncTime;

    public Store(long storeId,
                 String storeName,
                 String status,
                 int primaryBeaconId,
                 String map,
                 long lastRecoEngineSyncTime,
                 String recoMessage){
        this.id = storeId;
        this.storeName = storeName;
        this.status = status;
        this.primaryBeaconId = primaryBeaconId;
        this.map = map;
        this.lastRecoEngineSyncTime = lastRecoEngineSyncTime;
        this.recoMessage = recoMessage;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPrimaryBeaconId() {
        return primaryBeaconId;
    }

    public void setPrimaryBeaconId(int primaryBeaconId) {
        this.primaryBeaconId = primaryBeaconId;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getRecoMessage() {
        return recoMessage;
    }

    public void setRecoMessage(String recoMessage) {
        this.recoMessage = recoMessage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getLastRecoEngineSyncTime() {
        return lastRecoEngineSyncTime;
    }

    public void setLastRecoEngineSyncTime(long lastRecoEngineSyncTime) {
        this.lastRecoEngineSyncTime = lastRecoEngineSyncTime;
    }

}
