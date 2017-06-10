package com.easyshop.mc.shopeasy.main.Activity;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.easyshop.mc.shopeasy.R;
import com.easyshop.mc.shopeasy.main.DataBase.ShopEasyDBHelper;
import com.easyshop.mc.shopeasy.main.Model.Beacon;
import com.easyshop.mc.shopeasy.main.Model.Product;
import com.easyshop.mc.shopeasy.main.Model.WishlistProduct;
import com.easyshop.mc.shopeasy.main.Service.BeaconService;
import com.easyshop.mc.shopeasy.main.Service.LocationService;
import com.easyshop.mc.shopeasy.main.Util.SharedPrefsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MapActivity extends AppCompatActivity {

    private LinearLayout layout;
    private List<Button> beacons;
    private Button cart;
    private List<ObjectAnimator> animations;
    private int numberofBeacons;
    private int numberBeacons;
    private ObjectAnimator animation6;
    ImageView presentLocationImage;
    ShopEasyDBHelper dbHelper = ShopEasyDBHelper.getInstance(MapActivity.this);
    public static final String NOTIFICATION_EXTRA_BEACON = "beaconId";
    public static final String NOTIFICATION_EXTRA_PRODUCTS = "productIds";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        layout.setBackgroundResource(R.drawable.walmart_map);

        long storeID = SharedPrefsUtil.getSharedPrefsLong(MapActivity.this, SharedPrefsUtil.STORE_IN_CONTEXT);
        final List<Beacon> beaconID = dbHelper.getBeaconsForStore(storeID);
        beacons = new ArrayList<>();
        animations = new ArrayList<>();

        for (numberofBeacons = 0; numberofBeacons < beaconID.size(); numberofBeacons++) {
            this.beacons.add(new Button(this));
            this.beacons.get(numberofBeacons).setLayoutParams(new LinearLayout.LayoutParams(70, 90));
            this.beacons.get(numberofBeacons).setBackgroundResource(R.drawable.hotspot);
            beacons.get(numberofBeacons).setY((float) beaconID.get(numberofBeacons).getY());
            this.animations.add(ObjectAnimator.ofFloat(beacons.get(numberofBeacons), "translationX", (float) beaconID.get(numberofBeacons).getX()));
            animations.get(numberofBeacons).setDuration(3000);
            animations.get(numberofBeacons).start();
            layout.addView(beacons.get(numberofBeacons));
        }
        cart = new Button(this);
        cart.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
        cart.setBackgroundResource(R.drawable.cart);
        cart.offsetTopAndBottom(10);
        cart.setX(50);
        cart.setY(1800);
        cart.setZ(6);
        animation6 = ObjectAnimator.ofFloat(cart, "translationY", 1750);
        animation6.setDuration(1000);
        animation6.start();
        animation6.setRepeatCount(3);
        layout.addView(cart);

        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(NOTIFICATION_EXTRA_BEACON)) {
                long beaconId = extras.getLong(NOTIFICATION_EXTRA_BEACON);
                List<String> productsAtBeacon = extras.getStringArrayList(NOTIFICATION_EXTRA_PRODUCTS);
                if(productsAtBeacon!=null && productsAtBeacon.size()>0){
                    for(String product: productsAtBeacon){
                        ShopEasyDBHelper.getInstance(MapActivity.this).setProductTaken(Long.valueOf(product));
                    }
                }

                Beacon currentBeacon = ShopEasyDBHelper.getInstance(MapActivity.this).getBeacon(beaconId);
                if(currentBeacon.getPrimary()){
                    final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MapActivity.this, R.style.AlertDialog);
                    alertBuilder.setTitle("Are you finished with Shopping?");
                    alertBuilder.setMessage("Seems like you are done Shopping!");
                    alertBuilder.setPositiveButton("I'm Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishShopping();
                        }
                    });
                    alertBuilder.setNegativeButton("Let me shop!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                        }
                    });
                    alertBuilder.create().show();
                }
                presentLocationImage = new ImageView(this);
                presentLocationImage.setLayoutParams(new LinearLayout.LayoutParams(50,50));
                presentLocationImage.setImageResource(R.drawable.present_location);
                presentLocationImage.setX(350);
                presentLocationImage.setY(0); // replace with present beacon position values
                layout.addView(presentLocationImage);

            }
        }

        setContentView(layout);

        final List<Button> popupButtons = new ArrayList<>();
        for (numberBeacons = 0; numberBeacons < beaconID.size(); numberBeacons++) {
            this.beacons.get(numberBeacons).setClickable(true);
            popupButtons.add(this.beacons.get(numberBeacons));
            final int beaconNumber = numberBeacons;
            popupButtons.get(numberBeacons).setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View arg) {
                    LayoutInflater layoutInflater
                            = (LayoutInflater) getBaseContext()
                            .getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = layoutInflater.inflate(R.layout.popup_layout, null);

                    TextView text = (TextView) popupView.findViewById(R.id.textforPopUp1);
                    List<Product> productsAtBeacon = dbHelper.getWishlistProductsNearBeacon(beaconID.get(beaconNumber).getId());
                    String messageAtBeacon ="";
                    for(Product product: productsAtBeacon){
                            messageAtBeacon+=product.getPname()+System.lineSeparator();
                    }
                    if(messageAtBeacon.equals("") || messageAtBeacon == null){
                        messageAtBeacon = "No products here "+System.lineSeparator()+"from your wish list";
                    }
                    text.setText(messageAtBeacon);

                    final PopupWindow popupWindow = new PopupWindow(
                            popupView,
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT);
                    Button btnDismiss = (Button) popupView.findViewById(R.id.dismiss);
                    btnDismiss.setOnClickListener(new Button.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            popupWindow.dismiss();
                        }
                    });
                    popupWindow.showAsDropDown(popupButtons.get(beaconNumber), 50, -30);
                }
            });
        }
        cart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void finishShopping(){
        SharedPrefsUtil.saveSharedPrefsPrimaryBeacon(getApplicationContext(), false);
        SharedPrefsUtil.saveSharedPrefsStore(getApplicationContext(), 0);
        ShopEasyDBHelper.getInstance(MapActivity.this).cleanTables();

        stopService(new Intent(getApplicationContext(), BeaconService.class));
        List<WishlistProduct> notTakenProducts = dbHelper.getNotTakenProducts();
        if(notTakenProducts.size()>0){
            Intent intent = new Intent(MapActivity.this, FeedbackActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(MapActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }
}