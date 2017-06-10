package com.easyshop.mc.shopeasy.main.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.easyshop.mc.shopeasy.main.Model.Beacon;
import com.easyshop.mc.shopeasy.main.Model.Product;
import com.easyshop.mc.shopeasy.main.Model.RecoProduct;
import com.easyshop.mc.shopeasy.main.Model.Store;
import com.easyshop.mc.shopeasy.main.Model.User;
import com.easyshop.mc.shopeasy.main.Model.WishlistProduct;
import com.easyshop.mc.shopeasy.main.Util.SharedPrefsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Siddartha on 4/1/2017.
 */

public class ShopEasyDBHelper extends SQLiteOpenHelper{

    private final static String TAG = "ShopEasyDBHelper";
    private final Context context;
    private static ShopEasyDBHelper instance = null;

    public static final String DATABASE_NAME = "shopeasy.db";
    public static final int DATABASE_VERSION = 1;

    private ShopEasyDBHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
    }

    public static synchronized ShopEasyDBHelper getInstance(Context context){
            if(instance == null){
                instance = new ShopEasyDBHelper(context);
            }
        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createUserTable(db);
        //cloud
        createStoreTable(db);
        createBeaconsTable(db);
        createProductsTable(db);

        //local
        createWishlistProductTable(db);
        createRecoProductTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion == 1){
            //upgrade queries here, mean, update tables here.
        }
    }

    private void createUserTable(SQLiteDatabase database){

        final String USER_CREATE = "create table "
                + ShopEasy.User.TABLE_NAME
                + "("
                + ShopEasy.User.COLUMN_NAME + " text not null, "
                + ShopEasy.User.COLUMN_EMAIL + " text primary key, "
                + ShopEasy.User.COLUMN_PASSWORD + " text not null, "
                + ShopEasy.User.GENDER + " text not null"
                + ");";
        database.execSQL(USER_CREATE);

    }

    private void createProductsTable(SQLiteDatabase database){
        final String PRODUCTS_CREATE = "create table "
                + ShopEasy.Products.TABLE_NAME
                + "("
                + ShopEasy.Products.COLUMN_STOREID + " UNSIGNED BIG INT not null, "
                + ShopEasy.Products.COLUMN_PRODUCTID + " UNSIGNED BIG INT PRIMARY KEY, "
                + ShopEasy.Products.COLUMN_NAME + " text not null, "
                + ShopEasy.Products.COLUMN_CATEGEORY + " text not null, "
                + ShopEasy.Products.COLUMN_PRICE + " REAL not null, "
                + ShopEasy.Products.COLUMN_DISCOUNT + " REAL not null default 0.0, "
                + ShopEasy.Products.COLUMN_BEACONID + " UNSIGNED BIG INT not null default 0, "
                + ShopEasy.Products.COLUMN_RECOMMENDED_LIST + " text not null, "
                + "FOREIGN KEY("+ ShopEasy.Products.COLUMN_BEACONID +") REFERENCES "+ShopEasy.Beacons.TABLE_NAME +" ("+ ShopEasy.Beacons.COLUMN_BEACONID+") ON UPDATE CASCADE ON DELETE CASCADE"
                + ");";
        database.execSQL(PRODUCTS_CREATE);
    }

    private void createBeaconsTable(SQLiteDatabase database){
        final String BEACONS_CREATE = "create table "
                + ShopEasy.Beacons.TABLE_NAME
                + "("
                + ShopEasy.Beacons.COLUMN_STOREID + " UNSIGNED BIG INT not null, "
                + ShopEasy.Beacons.COLUMN_BEACONID + " UNSIGNED BIG INT AUTO_INCREMENT PRIMARY KEY, "
                + ShopEasy.Beacons.COLUMN_ISPRIMARY + " INTEGER not null, "
                + ShopEasy.Beacons.COLUMN_NAME + " text not null, "
                + ShopEasy.Beacons.COLUMN_MAP_INDEX + " INTEGER not null, "
                + ShopEasy.Beacons.COLUMN_DEFAULT_PRODUCT + " UNSIGNED BIG INT, "
                + ShopEasy.Beacons.COLUMN_X + " REAL not null, "
                + ShopEasy.Beacons.COLUMN_Y + " REAL not null"
                + ");";
        database.execSQL(BEACONS_CREATE);
    }

    private void createWishlistProductTable(SQLiteDatabase database){
        final String WISHLISTPRODUCT_CREATE = "create table "
                + ShopEasy.WishlistProduct.TABLE_NAME
                + "("
                + ShopEasy.WishlistProduct.COLUMN_PRODUCTID + " UNSIGNED BIG INT not null,"
                + ShopEasy.WishlistProduct.COLUMN_PRODUCT_TAKEN + " INT not null default 0"
                + ");";
        database.execSQL(WISHLISTPRODUCT_CREATE);
    }


    private void createRecoProductTable(SQLiteDatabase database){
        final String RECOPRODUCT_CREATE = "create table "
                + ShopEasy.RecoProduct.TABLE_NAME
                + "("
                + ShopEasy.RecoProduct.COLUMN_PRODUCTID + " UNSIGNED BIG INT not null"
                + ");";
        database.execSQL(RECOPRODUCT_CREATE);
    }

    private void createStoreTable(SQLiteDatabase database){
        final String STORE_CREATE = "create table "
                + ShopEasy.Store.TABLE_NAME
                + "("
                + ShopEasy.Store.COLUMN_STOREID + " UNSIGNED BIG INT PRIMARY KEY, "
                + ShopEasy.Store.COLUMN_STORENAME + " text not null, "
                + ShopEasy.Store.COLUMN_STATUS + " text not null, "
                + ShopEasy.Store.COLUMN_PRIMARY_BEACON + " INT not null default 0, "
                + ShopEasy.Store.COLUMN_MAP_URL + " text not null, "
                + ShopEasy.Store.COLUMN_LASTSYNC + " UNSIGNED BIG INT not null default 0, "
                + ShopEasy.Store.COLUMN_GENERIC_MESSAGE + " text not null"
                + ");";
        database.execSQL(STORE_CREATE);
    }

    String[] userTableProjection = {
            ShopEasy.User.COLUMN_NAME,
            ShopEasy.User.COLUMN_EMAIL,
            ShopEasy.User.COLUMN_PASSWORD,
            ShopEasy.User.GENDER
    };

    public static final int USER_NAME_INDEX = 0;
    public static final int USER_EMAIL_INDEX = 1;
    public static final int USER_PASSWORD_INDEX = 2;
    public static final int USER_GENDER_INDEX = 3;


    //user account related
    public User getUserIfExist(String emailId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try{
            cursor = db.query(
                    ShopEasy.User.TABLE_NAME,
                    userTableProjection,
                    ShopEasy.User.COLUMN_EMAIL + "=?",
                    new String[]{emailId},
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                if ( cursor.getCount() > 0 ) {
                    cursor.moveToFirst();
                    if(cursor.getString(USER_GENDER_INDEX).equals(com.easyshop.mc.shopeasy.main.Model.User.MALE))
                        return new User(cursor.getString(USER_NAME_INDEX),cursor.getString(USER_EMAIL_INDEX),cursor.getString(USER_PASSWORD_INDEX), true, false);
                    else
                        return new User(cursor.getString(USER_NAME_INDEX),cursor.getString(USER_NAME_INDEX),cursor.getString(USER_NAME_INDEX), false, true);
                }
                else
                    return null;

            }

        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public boolean addUserAccount(User user){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ShopEasy.User.COLUMN_NAME, user.getName());
        values.put(ShopEasy.User.COLUMN_EMAIL, user.getEmailId());
        values.put(ShopEasy.User.COLUMN_PASSWORD, user.getPassword());
        if(user.getMale())
            values.put(ShopEasy.User.GENDER, user.MALE);
        else
            values.put(ShopEasy.User.GENDER, user.FEMALE);
        try{
            if(database.insert(ShopEasy.User.TABLE_NAME, null, values)>0)
                return true;
        }
        finally {
            if (database != null) {
                database.close();
            }
        }

        return false;
    }

    public boolean updateUser(User user){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ShopEasy.User.COLUMN_NAME, user.getName());
        values.put(ShopEasy.User.COLUMN_EMAIL, user.getEmailId());
        values.put(ShopEasy.User.COLUMN_PASSWORD, user.getPassword());

        if(user.getMale())
            values.put(ShopEasy.User.GENDER, user.MALE);
        else
            values.put(ShopEasy.User.GENDER, user.FEMALE);
        try{
            if(database.update(ShopEasy.User.TABLE_NAME, values, ShopEasy.User.COLUMN_EMAIL + "=?", new String[]{user.getEmailId()})>0)
                return true;
        }
        finally {
            if (database != null) {
                database.close();
            }
        }

        return false;
    }


    //products related
    String[] productsTableProjection = {
            ShopEasy.Products.COLUMN_STOREID,
            ShopEasy.Products.COLUMN_PRODUCTID,
            ShopEasy.Products.COLUMN_NAME,
            ShopEasy.Products.COLUMN_CATEGEORY,
            ShopEasy.Products.COLUMN_PRICE,
            ShopEasy.Products.COLUMN_DISCOUNT,
            ShopEasy.Products.COLUMN_BEACONID,
            ShopEasy.Products.COLUMN_RECOMMENDED_LIST
    };

    public static final int PRODUCTS_STOREID_INDEX = 0;
    public static final int PRODUCTS_PRODUCTID_INDEX = 1;
    public static final int PRODUCTS_NAME_INDEX = 2;
    public static final int PRODUCTS_CATEGEORY_INDEX = 3;
    public static final int PRODUCTS_PRICE_INDEX = 4;
    public static final int PRODUCTS_DISCOUNT_INDEX = 5;
    public static final int PRODUCTS_BEACON_INDEX = 6;
    public static final int PRODUCTS_RECOMMENDED_INDEX = 7;

    public boolean addProducts(List<Product> productList){
        SQLiteDatabase database = this.getWritableDatabase();

        try{
            for(Product product: productList){
                ContentValues values = new ContentValues();
                values.put(ShopEasy.Products.COLUMN_STOREID, product.getStoreId());
                values.put(ShopEasy.Products.COLUMN_PRODUCTID, product.getPid());
                values.put(ShopEasy.Products.COLUMN_NAME, product.getPname());
                values.put(ShopEasy.Products.COLUMN_CATEGEORY, product.getCategory());
                values.put(ShopEasy.Products.COLUMN_PRICE, product.getPrice());
                values.put(ShopEasy.Products.COLUMN_DISCOUNT, product.getDiscount());
                values.put(ShopEasy.Products.COLUMN_BEACONID, product.getBeaconId());
                values.put(ShopEasy.Products.COLUMN_RECOMMENDED_LIST, product.getRecoProducts());

                database.insert(ShopEasy.Products.TABLE_NAME, null, values);
            }
        }
        catch (Exception ex){
            return false;
        }
        finally {
            if (database != null) {
                database.close();
            }
        }

        return true;
    }

    public List<Product> getProductsForStore(long storeId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        ArrayList<Product> productList = new ArrayList<>();
        try{
            cursor = db.query(
                    ShopEasy.Products.TABLE_NAME,
                    productsTableProjection,
                    ShopEasy.Products.COLUMN_STOREID + "=?",
                    new String[]{String.valueOf(storeId)},
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                if ( cursor.getCount() > 0 ) {
                    cursor.moveToFirst();
                    do{
                        productList.add(new Product(cursor.getLong(PRODUCTS_PRODUCTID_INDEX),
                                                    cursor.getLong(PRODUCTS_STOREID_INDEX),
                                                    cursor.getString(PRODUCTS_NAME_INDEX),
                                                    cursor.getString(PRODUCTS_CATEGEORY_INDEX),
                                                    cursor.getDouble(PRODUCTS_PRICE_INDEX),
                                                    cursor.getDouble(PRODUCTS_DISCOUNT_INDEX),
                                                    cursor.getInt(PRODUCTS_BEACON_INDEX),
                                                    cursor.getString(PRODUCTS_RECOMMENDED_INDEX)));
                    }while(cursor.moveToNext());
                }
                else
                    return null;

            }
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return productList;
    }

    public Product getProduct(long storeId, long productId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try{
            cursor = db.query(
                    ShopEasy.Products.TABLE_NAME,
                    productsTableProjection,
                    ShopEasy.Products.COLUMN_PRODUCTID + "=?",
                    new String[]{String.valueOf(productId)},
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                if ( cursor.getCount() > 0 ) {
                    cursor.moveToFirst();
                    return new Product(cursor.getLong(PRODUCTS_PRODUCTID_INDEX),
                                        cursor.getLong(PRODUCTS_STOREID_INDEX),
                                        cursor.getString(PRODUCTS_NAME_INDEX),
                                        cursor.getString(PRODUCTS_CATEGEORY_INDEX),
                                        cursor.getDouble(PRODUCTS_PRICE_INDEX),
                                        cursor.getDouble(PRODUCTS_DISCOUNT_INDEX),
                                        cursor.getInt(PRODUCTS_BEACON_INDEX),
                                        cursor.getString(PRODUCTS_RECOMMENDED_INDEX));
                }
                else
                    return null;

            }

        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public Product getProductsForBeacon(long storeId, long beaconId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        ArrayList<Product> productList = new ArrayList<>();
        try{
            cursor = db.query(
                    ShopEasy.Products.TABLE_NAME,
                    productsTableProjection,
                    ShopEasy.Products.COLUMN_BEACONID + "=?",
                    new String[]{String.valueOf(beaconId)},
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                if ( cursor.getCount() > 0 ) {
                    cursor.moveToFirst();
                    do{
                        productList.add(new Product(cursor.getLong(PRODUCTS_PRODUCTID_INDEX),
                                                    cursor.getLong(PRODUCTS_STOREID_INDEX),
                                                    cursor.getString(PRODUCTS_NAME_INDEX),
                                                    cursor.getString(PRODUCTS_CATEGEORY_INDEX),
                                                    cursor.getDouble(PRODUCTS_PRICE_INDEX),
                                                    cursor.getDouble(PRODUCTS_DISCOUNT_INDEX),
                                                    cursor.getInt(PRODUCTS_BEACON_INDEX),
                                                    cursor.getString(PRODUCTS_RECOMMENDED_INDEX)));
                    }while(cursor.moveToNext());
                }
                else
                    return null;

            }

        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    //store

    String[] storeTableProjection = {
            ShopEasy.Store.COLUMN_STOREID,
            ShopEasy.Store.COLUMN_STORENAME,
            ShopEasy.Store.COLUMN_STATUS,
            ShopEasy.Store.COLUMN_PRIMARY_BEACON,
            ShopEasy.Store.COLUMN_MAP_URL,
            ShopEasy.Store.COLUMN_LASTSYNC,
            ShopEasy.Store.COLUMN_GENERIC_MESSAGE
    };

    public static final int STORE_STOREID_INDEX = 0;
    public static final int STORE_STORENAME_INDEX = 1;
    public static final int STORE_STATUS_INDEX = 2;
    public static final int STORE_ISPRIMARY_INDEX = 3;
    public static final int STORE_MAP_URL_INDEX = 4;
    public static final int STORE_LASTSYNC_INDEX = 5;
    public static final int STORE_GENERIC_MESSAGE_INDEX = 6;


    public boolean addStore(Store store){
        SQLiteDatabase database = this.getWritableDatabase();

        try{
            ContentValues values = new ContentValues();
            values.put(ShopEasy.Store.COLUMN_STOREID, store.getId());
            values.put(ShopEasy.Store.COLUMN_STORENAME, store.getStoreName());
            values.put(ShopEasy.Store.COLUMN_STATUS, store.getStatus());
            values.put(ShopEasy.Store.COLUMN_PRIMARY_BEACON, store.getPrimaryBeaconId());
            values.put(ShopEasy.Store.COLUMN_MAP_URL, store.getMap());
            values.put(ShopEasy.Store.COLUMN_LASTSYNC, store.getLastRecoEngineSyncTime());
            values.put(ShopEasy.Store.COLUMN_GENERIC_MESSAGE, store.getRecoMessage());


            database.insert(ShopEasy.Store.TABLE_NAME, null, values);
        }
        catch (Exception ex){
            return false;
        }
        finally {
            if (database != null) {
                database.close();
            }
        }

        return true;
    }

    public Store getStoreById(long storeId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try{
            cursor = db.query(
                    ShopEasy.Store.TABLE_NAME,
                    storeTableProjection,
                    ShopEasy.Store.COLUMN_STOREID + "=?",
                    new String[]{String.valueOf(storeId)},
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                if ( cursor.getCount() > 0 ) {
                    cursor.moveToFirst();
                    return new Store(cursor.getLong(STORE_STOREID_INDEX),
                            cursor.getString(STORE_STORENAME_INDEX),
                            cursor.getString(STORE_STATUS_INDEX),
                            cursor.getInt(STORE_ISPRIMARY_INDEX),
                            cursor.getString(STORE_MAP_URL_INDEX),
                            cursor.getLong(STORE_LASTSYNC_INDEX),
                            cursor.getString(STORE_GENERIC_MESSAGE_INDEX));
                }
                else
                    return null;

            }

        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public List<Product> getWishlistProductsNearBeacon(long beaconId){
        ArrayList<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        List<WishlistProduct> wishlistProducts = this.getWishlistProducts();
        String wishlistProductIds = "";

        if(wishlistProducts == null || wishlistProducts.size() == 0){
            return productList;
        }
        int i = 0;
        for(; i < wishlistProducts.size() - 1; i++){
            wishlistProductIds += wishlistProducts.get(i).getProductId() + ",";
        }
        wishlistProductIds += wishlistProducts.get(i).getProductId();

        final String allProducts = "select * from "+ ShopEasy.Products.TABLE_NAME + " where "
                + ShopEasy.Products.COLUMN_BEACONID + "=" + beaconId
                + " and "+ ShopEasy.Products.COLUMN_PRODUCTID + " IN (" + wishlistProductIds + ")";

        try{
            cursor = db.rawQuery(allProducts, null);

            if (cursor != null) {
                if ( cursor.getCount() > 0 ) {
                    cursor.moveToFirst();
                    do{
                        productList.add(new Product(cursor.getLong(PRODUCTS_PRODUCTID_INDEX),
                                cursor.getLong(PRODUCTS_STOREID_INDEX),
                                cursor.getString(PRODUCTS_NAME_INDEX),
                                cursor.getString(PRODUCTS_CATEGEORY_INDEX),
                                cursor.getDouble(PRODUCTS_PRICE_INDEX),
                                cursor.getDouble(PRODUCTS_DISCOUNT_INDEX),
                                cursor.getInt(PRODUCTS_BEACON_INDEX),
                                cursor.getString(PRODUCTS_RECOMMENDED_INDEX)));
                    }while(cursor.moveToNext());
                }
                else
                    return productList;

            }
        }catch(SQLException se){
            Log.e("DBHelper", "Error in fetching WishList at Beacon");
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return productList;
    }

    public List<Product> getRecoProductsNearBeacon(long beaconId){
        ArrayList<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        List<RecoProduct> recoProducts = this.getRecoProducts();
        String recoProductIds = "";

        if(recoProducts == null || recoProducts.size() == 0){
            return productList;
        }
        int i = 0;
        for(; i < recoProducts.size() - 1; i++){
            recoProductIds += recoProducts.get(i).getProductId() + ",";
        }
        recoProductIds += recoProducts.get(i).getProductId();

        final String allProducts = "select * from "+ ShopEasy.Products.TABLE_NAME + " where "
                + ShopEasy.Products.COLUMN_BEACONID + "=" + beaconId
                + " and "+ ShopEasy.Products.COLUMN_PRODUCTID + " IN (" + recoProductIds + ")";

        try{
            cursor = db.rawQuery(allProducts, null);

            if (cursor != null) {
                if ( cursor.getCount() > 0 ) {
                    cursor.moveToFirst();
                    do{
                        productList.add(new Product(cursor.getLong(PRODUCTS_PRODUCTID_INDEX),
                                cursor.getLong(PRODUCTS_STOREID_INDEX),
                                cursor.getString(PRODUCTS_NAME_INDEX),
                                cursor.getString(PRODUCTS_CATEGEORY_INDEX),
                                cursor.getDouble(PRODUCTS_PRICE_INDEX),
                                cursor.getDouble(PRODUCTS_DISCOUNT_INDEX),
                                cursor.getInt(PRODUCTS_BEACON_INDEX),
                                cursor.getString(PRODUCTS_RECOMMENDED_INDEX)));
                    }while(cursor.moveToNext());
                }
                else
                    return null;

            }
        }catch(SQLException se){
            Log.e("DBHelper", "Error in fetching Reco Products at Beacon");
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return productList;
    }

    //WishList

    public static final int WISHLISTPRODUCT_INDEX = 0;
    public static final int WISHLISTPRODUCT_TAKEN = 1;
    public static final int RECOPRODUCT_INDEX = 0;

    public List<WishlistProduct> getWishlistProducts(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        final String wishlistProducts = "select * from "+ ShopEasy.WishlistProduct.TABLE_NAME;
        ArrayList<WishlistProduct> productList = new ArrayList<>();
        try{
            cursor = db.rawQuery(wishlistProducts, null);

            if (cursor != null) {
                if ( cursor.getCount() > 0 ) {
                    cursor.moveToFirst();
                    do{
                        productList.add(new WishlistProduct(cursor.getLong(WISHLISTPRODUCT_INDEX)));
                    }while(cursor.moveToNext());
                }
               }
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return productList;
    }

    public List<RecoProduct> getRecoProducts(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        final String recoProducts = "select * from "+ ShopEasy.RecoProduct.TABLE_NAME;
        ArrayList<RecoProduct> productList = new ArrayList<>();
        try{
            cursor = db.rawQuery(recoProducts, null);

            if (cursor != null) {
                if ( cursor.getCount() > 0 ) {
                    cursor.moveToFirst();
                    do{
                        productList.add(new RecoProduct(cursor.getLong(RECOPRODUCT_INDEX)));
                    }while(cursor.moveToNext());
                }
                else
                    return null;

            }
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return productList;
    }

    public boolean addWishlistProduct(WishlistProduct wishlistProduct){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ShopEasy.WishlistProduct.COLUMN_PRODUCTID, wishlistProduct.getProductId());
        values.put(ShopEasy.WishlistProduct.COLUMN_PRODUCT_TAKEN, wishlistProduct.getProductTaken());
        try{
            if(database.insert(ShopEasy.WishlistProduct.TABLE_NAME, null, values)>0)
                return true;
        }
        finally {
            if (database != null) {
                database.close();
            }
        }
        return false;
    }

    public boolean removeWishlistProduct(WishlistProduct wishlistProduct){
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "delete from "+ShopEasy.WishlistProduct.TABLE_NAME+" where "+ShopEasy.WishlistProduct.COLUMN_PRODUCTID+" = "+ wishlistProduct.getProductId();
        try{
            int count = database.delete(ShopEasy.WishlistProduct.TABLE_NAME,
                                ShopEasy.WishlistProduct.COLUMN_PRODUCTID+"=?", new String[]{String.valueOf(wishlistProduct.getProductId())});
            if(count>-1){

                return true;
            }
        }catch(SQLiteException se){
            return false;
        }
        finally {
            if (database != null) {
                database.close();
            }
        }
        return false;
    }

    public boolean addRecoProduct(RecoProduct recoProduct){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ShopEasy.RecoProduct.COLUMN_PRODUCTID, recoProduct.getProductId());
        try{
            if(database.insert(ShopEasy.RecoProduct.TABLE_NAME, null, values)>0)
                return true;
        }
        finally {
            if (database != null) {
                database.close();
            }
        }
        return false;
    }

    public void setProductTaken(long productId){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ShopEasy.WishlistProduct.COLUMN_PRODUCT_TAKEN, 1);
        try {
            database.update(ShopEasy.WishlistProduct.TABLE_NAME, cv, ShopEasy.WishlistProduct.COLUMN_PRODUCTID + "= ?", new String[]{String.valueOf(productId)});
        }finally {
            if (database != null) {
                database.close();
            }
        }
    }

    public List<WishlistProduct> getNotTakenProducts(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        final String wishlistProducts = "select * from "+ ShopEasy.WishlistProduct.TABLE_NAME;
        ArrayList<WishlistProduct> productList = new ArrayList<>();
        try{
            cursor = db.rawQuery(wishlistProducts, null);

            if (cursor != null) {
                if ( cursor.getCount() > 0 ) {
                    cursor.moveToFirst();
                    do{
                        productList.add(new WishlistProduct(cursor.getLong(WISHLISTPRODUCT_INDEX)));
                    }while(cursor.moveToNext());
                }
            }
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return productList;

    }


    //Beacon
    String[] beaconsTableProjection = {
            ShopEasy.Beacons.COLUMN_STOREID,
            ShopEasy.Beacons.COLUMN_BEACONID,
            ShopEasy.Beacons.COLUMN_ISPRIMARY,
            ShopEasy.Beacons.COLUMN_NAME,
            ShopEasy.Beacons.COLUMN_MAP_INDEX,
            ShopEasy.Beacons.COLUMN_DEFAULT_PRODUCT,
            ShopEasy.Beacons.COLUMN_X,
            ShopEasy.Beacons.COLUMN_Y,
    };

    public static final int BEACONS_STOREID_INDEX = 0;
    public static final int BEACONS_BEACONID_INDEX = 1;
    public static final int BEACONS_ISPRIMARY_INDEX = 2;
    public static final int BEACONS_NAME_INDEX = 3;
    public static final int BEACONS_MAP_INDEX_INDEX = 4;
    public static final int BEACONS_DEFAULT_PRODUCT_INDEX = 5;
    public static final int BEACONS_X_INDEX = 6;
    public static final int BEACONS_Y_INDEX = 7;

    public Beacon getBeacon(long beaconId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try{
            cursor = db.query(
                    ShopEasy.Beacons.TABLE_NAME,
                    beaconsTableProjection,
                    ShopEasy.Beacons.COLUMN_BEACONID + "=?",
                    new String[]{String.valueOf(beaconId)},
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                if ( cursor.getCount() > 0 ) {
                    cursor.moveToFirst();
                    return new Beacon(cursor.getLong(BEACONS_BEACONID_INDEX),
                            cursor.getString(BEACONS_NAME_INDEX),
                            cursor.getLong(BEACONS_STOREID_INDEX),
                            cursor.getInt(BEACONS_MAP_INDEX_INDEX),
                            cursor.getLong(BEACONS_DEFAULT_PRODUCT_INDEX),
                            cursor.getDouble(BEACONS_X_INDEX),
                            cursor.getDouble(BEACONS_Y_INDEX),
                            (cursor.getInt(BEACONS_ISPRIMARY_INDEX)==1?true:false));
                }
                else
                    return null;

            }

        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public List<Beacon> getBeaconsForStore(long storeId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        ArrayList<Beacon> beaconsList = new ArrayList<>();
        try{
            cursor = db.query(
                    ShopEasy.Beacons.TABLE_NAME,
                    beaconsTableProjection,
                    ShopEasy.Beacons.COLUMN_STOREID + "=?",
                    new String[]{String.valueOf(storeId)},
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                if ( cursor.getCount() > 0 ) {
                    cursor.moveToFirst();
                    do{
                        beaconsList.add(new Beacon(cursor.getLong(BEACONS_BEACONID_INDEX),
                                cursor.getString(BEACONS_NAME_INDEX),
                                cursor.getLong(BEACONS_STOREID_INDEX),
                                cursor.getInt(BEACONS_MAP_INDEX_INDEX),
                                cursor.getLong(BEACONS_DEFAULT_PRODUCT_INDEX),
                                cursor.getDouble(BEACONS_X_INDEX),
                                cursor.getDouble(BEACONS_Y_INDEX),
                                (cursor.getInt(BEACONS_ISPRIMARY_INDEX)==1?true:false)));
                    }while(cursor.moveToNext());
                }
                else
                    return null;

            }
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return beaconsList;
    }

    public Beacon getBeaconIdByName(String beaconName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try{
            cursor = db.query(
                    ShopEasy.Beacons.TABLE_NAME,
                    beaconsTableProjection,
                    ShopEasy.Beacons.COLUMN_NAME + "=?",
                    new String[]{String.valueOf(beaconName)},
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                if ( cursor.getCount() > 0 ) {
                    cursor.moveToFirst();
                    return new Beacon(cursor.getLong(BEACONS_BEACONID_INDEX),
                            cursor.getString(BEACONS_NAME_INDEX),
                            cursor.getLong(BEACONS_STOREID_INDEX),
                            cursor.getInt(BEACONS_MAP_INDEX_INDEX),
                            cursor.getLong(BEACONS_DEFAULT_PRODUCT_INDEX),
                            cursor.getDouble(BEACONS_X_INDEX),
                            cursor.getDouble(BEACONS_Y_INDEX),
                            (cursor.getInt(BEACONS_ISPRIMARY_INDEX)==1?true:false));
                }
                else
                    return null;

            }

        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    public boolean addBeacons(List<Beacon> beaconList){
        SQLiteDatabase database = this.getWritableDatabase();

        try{
            for(Beacon beacon: beaconList){
                ContentValues values = new ContentValues();
                values.put(ShopEasy.Beacons.COLUMN_STOREID, beacon.getStoreId());
                values.put(ShopEasy.Beacons.COLUMN_BEACONID, beacon.getId());
                values.put(ShopEasy.Beacons.COLUMN_DEFAULT_PRODUCT, beacon.getDefProductId());
                values.put(ShopEasy.Beacons.COLUMN_MAP_INDEX, beacon.getMapIndex());
                values.put(ShopEasy.Beacons.COLUMN_NAME, beacon.getName());
                values.put(ShopEasy.Beacons.COLUMN_X, beacon.getX());
                values.put(ShopEasy.Beacons.COLUMN_Y, beacon.getY());

                if(beacon.getPrimary())
                    values.put(ShopEasy.Beacons.COLUMN_ISPRIMARY, 1);
                else
                    values.put(ShopEasy.Beacons.COLUMN_ISPRIMARY, 0);
                database.insert(ShopEasy.Beacons.TABLE_NAME, null, values);
            }
        }
        catch (Exception ex){
            return false;
        }
        finally {
            if (database != null) {
                database.close();
            }
        }

        return true;
    }

    public void cleanTables(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(ShopEasy.Store.TABLE_NAME,null, null);
        database.delete(ShopEasy.Products.TABLE_NAME, null, null);
        database.delete(ShopEasy.Beacons.TABLE_NAME, null, null);
        database.delete(ShopEasy.WishlistProduct.TABLE_NAME, null, null);
        database.delete(ShopEasy.RecoProduct.TABLE_NAME, null, null);
    }
}


