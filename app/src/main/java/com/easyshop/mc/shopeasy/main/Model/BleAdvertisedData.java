package com.easyshop.mc.shopeasy.main.Model;

import java.util.List;
import java.util.UUID;

/**
 * Created by Siddartha on 4/24/2017.
 */

public class BleAdvertisedData {
    private List<UUID> mUuids;
    private String mName;
    public BleAdvertisedData(List<UUID> uuids, String name){
        mUuids = uuids;
        mName = name;
    }

    public List<UUID> getUuids(){
        return mUuids;
    }

    public String getName(){
        return mName;
    }
}
