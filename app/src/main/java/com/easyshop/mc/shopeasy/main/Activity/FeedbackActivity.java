package com.easyshop.mc.shopeasy.main.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.easyshop.mc.shopeasy.R;
import com.easyshop.mc.shopeasy.main.DataBase.ShopEasyDBHelper;
import com.easyshop.mc.shopeasy.main.Model.WishlistProduct;
import com.easyshop.mc.shopeasy.main.Util.SharedPrefsUtil;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity {
    ListView listView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ShopEasyDBHelper dbHelper = ShopEasyDBHelper.getInstance(FeedbackActivity.this);
        long storeId = SharedPrefsUtil.getSharedPrefsLong(this, SharedPrefsUtil.STORE_IN_CONTEXT);

        listView = (ListView) findViewById(R.id.list);
        List<WishlistProduct> notTakenProducts = dbHelper.getNotTakenProducts();

        List<String> notTakenNames = new ArrayList<>();
        for(WishlistProduct notTaken:notTakenProducts){
            notTakenNames.add(dbHelper.getProduct(storeId, notTaken.getProductId()).getPname());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, notTakenNames);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) listView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                        .show();

            }

        });
    }

    public void sendFeedback(View view){
        Toast.makeText(getApplicationContext(), "Thank you for Feedback", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
