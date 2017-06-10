package com.easyshop.mc.shopeasy.main.DataBase;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by Siddartha on 4/1/2017.
 */

public class ShopEasy {
    public static final String WISHLIST_MSG = "WISHLIST_MSG";
    public static final String RECO_MSG = "RECO_MSG";
    public static final String DEFAULT_PRODUCT_MSG = "DEFAULT_PRODUCT_MSG";

    public ShopEasy(){}

    public static class User implements BaseColumns{

        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL = "emailid";
        public static final String COLUMN_PASSWORD = "password";
        public static final String GENDER = "gender";

    }

    public static class Products implements BaseColumns{

        public static final String TABLE_NAME = "products";
        public static final String COLUMN_STOREID = "storeId";
        public static final String COLUMN_PRODUCTID = "productId";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CATEGEORY = "categeory";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_DISCOUNT = "discount";
        public static final String COLUMN_STOCK = "stock";
        public static final String COLUMN_BEACONID = "beaconId";
        public static final String COLUMN_RECOMMENDED_LIST = "recommendedproducts";

    }

    public static class Store implements BaseColumns{

        public static final String TABLE_NAME = "store";
        public static final String COLUMN_STOREID = "storeId";
        public static final String COLUMN_STORENAME = "storeName";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_PRIMARY_BEACON = "primaryBeacon";
        public static final String COLUMN_MAP_URL = "mapUrl";
        public static final String COLUMN_LASTSYNC = "price";
        public static final String COLUMN_GENERIC_MESSAGE = "genericMessage";
    }


    public static class Beacons implements BaseColumns{

        public static final String TABLE_NAME = "beacons";
        public static final String COLUMN_STOREID = "storeId";
        public static final String COLUMN_BEACONID = "beaconId";
        public static final String COLUMN_ISPRIMARY = "isPrimary";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_MAP_INDEX = "mapindex";
        public static final String COLUMN_DEFAULT_PRODUCT = "defaultproductId";
        public static final String COLUMN_X = "x";
        public static final String COLUMN_Y = "y";

    }

    public static class WishList implements BaseColumns{

        public static final String TABLE_NAME = "wishlist";
        public static final String COLUMN_PRODUCTID = "productId";
    }

    public static class Recommendation implements BaseColumns{

        public static final String TABLE_NAME = "recommendation";
        public static final String COLUMN_PRODUCTID = "productId";
    }

    public interface BaseDateColumns extends BaseColumns {
        public static final String DATE   = "date";
        public static final String YEAR   = "year";
        public static final String YEAR_MONTH   = "year_month";
        public static final String YEAR_WEEK   = "year_week";
    }

    public static class WishlistProduct implements BaseColumns{

        public static final String TABLE_NAME = "wishlistproduct";
        public static final String COLUMN_PRODUCTID = "productId";
        public static final String COLUMN_PRODUCT_TAKEN = "productTaken";
    }

    public static class RecoProduct implements BaseColumns{

        public static final String TABLE_NAME = "recoproduct";
        public static final String COLUMN_PRODUCTID = "productId";
    }
}
