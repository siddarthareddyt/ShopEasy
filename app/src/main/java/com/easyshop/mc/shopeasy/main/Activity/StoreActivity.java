package com.easyshop.mc.shopeasy.main.Activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.easyshop.mc.shopeasy.R;
import com.easyshop.mc.shopeasy.main.Adapter.RecyclerAdapter;
import com.easyshop.mc.shopeasy.main.Model.AppDataModel;
import com.easyshop.mc.shopeasy.main.Model.Product;
import com.easyshop.mc.shopeasy.main.Model.WishlistProduct;
import com.easyshop.mc.shopeasy.main.Service.BeaconService;
import com.easyshop.mc.shopeasy.main.Util.SharedPrefsUtil;
import com.easyshop.mc.shopeasy.main.core.RecoEngine;

import java.util.List;

import retrofit2.Response;

public class StoreActivity extends AppCompatActivity {
    FloatingActionButton showMap;
    private EditText searchText;
    private String searchQuery = null;

    private RecyclerView recyclerView;
    private RecyclerAdapter productsRecyclerAdapter;

    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;

    private List<Product> productList;
    private Button checkOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        searchText = (EditText)findViewById(R.id.searchQuery);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        checkOut = (Button)findViewById(R.id.checkOut);

        hideSoftKeyboard();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);

        boolean status = getIntent().getBooleanExtra(SplashActivity.EXTRA_PRODUCTS, false);

        productList = AppDataModel.getAppDataModel().getProductsData();
        productsRecyclerAdapter = new RecyclerAdapter(this, productList, false);
        recyclerView.setAdapter(productsRecyclerAdapter);
        setRecyclerViewScrollListener();
        setRecyclerViewItemTouchListener();

        Intent intent = new Intent(StoreActivity.this, BeaconService.class);
        startService(intent);

        showMap = (FloatingActionButton) findViewById(R.id.map);
        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(StoreActivity.this, MapActivity.class);
                startActivity(myIntent);
            }
        });

        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long storeId = SharedPrefsUtil.getSharedPrefsLong(StoreActivity.this, SharedPrefsUtil.STORE_IN_CONTEXT);
                RecoEngine.populateRecoProducts(storeId);
                Toast.makeText(StoreActivity.this, "Generated WishList", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(StoreActivity.this, MapActivity.class);
                startActivity(intent);

            }
        });

    }

    //on search query fired
    public void onSearch(View view){
        searchQuery = searchText.getText().toString();
/*        final Intent intent = new Intent(this, ProductPageActivity.class);
        //get single instance of the service provider
        ProductServiceProvider.getInstance().setServiceListener(new ProductServiceProvider.ServiceListener() {
            @Override
            public void onResultsSuccess(Response<SearchResult> response) {
                TODO remove loading icon
                List<Result> results = response.body().getResults();
                if(results != null && results.size() > 0){
                    intent.putExtra(EXTRA_PRODUCT, response.body().getResults().get(0));
                    startActivity(intent);
                }
            }

            @Override
            public void onProductsSuccess(Response<Products> response) {

            }

            @Override
            public void onFailure(String message) {

            }
        });

        ProductServiceProvider.getInstance().searchProducts(searchQuery);*/
    }

    public void showWishList(View view){
            Intent intent = new Intent(StoreActivity.this, WishListActivity.class);
            startActivity(intent);
    }

    public void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
                productList.remove(position);
                recyclerView.getAdapter().notifyItemRemoved(position);
            }
        };


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}

