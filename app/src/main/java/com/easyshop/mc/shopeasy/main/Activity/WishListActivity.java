package com.easyshop.mc.shopeasy.main.Activity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.easyshop.mc.shopeasy.R;
import com.easyshop.mc.shopeasy.main.Adapter.RecyclerAdapter;
import com.easyshop.mc.shopeasy.main.DataBase.ShopEasyDBHelper;
import com.easyshop.mc.shopeasy.main.Model.AppDataModel;
import com.easyshop.mc.shopeasy.main.Model.Product;
import com.easyshop.mc.shopeasy.main.Model.WishlistProduct;
import com.easyshop.mc.shopeasy.main.Util.SharedPrefsUtil;

import java.util.ArrayList;
import java.util.List;

public class WishListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerAdapter productsRecyclerAdapter;

    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;

    private List<WishlistProduct> wishListProducts;
    private List<Product> wishProducts = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(linearLayoutManager);

        overridePendingTransition(R.anim.right_in, R.anim.right_out);

        wishListProducts = ShopEasyDBHelper.getInstance(WishListActivity.this).getWishlistProducts();
        long storeId = SharedPrefsUtil.getSharedPrefsLong(WishListActivity.this,SharedPrefsUtil.STORE_IN_CONTEXT);
        for(int i=0; i<wishListProducts.size();i++){
            wishProducts.add(ShopEasyDBHelper.getInstance(WishListActivity.this).getProduct(storeId, wishListProducts.get(i).getProductId()));
        }

        productsRecyclerAdapter = new RecyclerAdapter(this, wishProducts, true);
        recyclerView.setAdapter(productsRecyclerAdapter);
        setRecyclerViewScrollListener();
        setRecyclerViewItemTouchListener();

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            final Drawable back = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            back.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(back);
        }
    }

    private void setRecyclerViewScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
            }
        });
    }


    private void setRecyclerViewItemTouchListener() {
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {

                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                int position = viewHolder.getAdapterPosition();
                Product removeProduct = wishProducts.get(position);
                ShopEasyDBHelper.getInstance(WishListActivity.this).removeWishlistProduct(new WishlistProduct(removeProduct.getPid()));
                Toast.makeText(WishListActivity.this, "Removed Item "+removeProduct.getPname(),Toast.LENGTH_SHORT).show();
                wishProducts.remove(position);
                recyclerView.getAdapter().notifyItemRemoved(position);
            }
        };


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
