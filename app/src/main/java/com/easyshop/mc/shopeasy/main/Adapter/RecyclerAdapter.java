package com.easyshop.mc.shopeasy.main.Adapter;

/**
 * Created by Siddartha on 4/19/2017.
 */

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easyshop.mc.shopeasy.main.Activity.StoreActivity;
import com.easyshop.mc.shopeasy.main.DataBase.ShopEasyDBHelper;
import com.easyshop.mc.shopeasy.main.Model.AppDataModel;
import com.easyshop.mc.shopeasy.main.Model.Product;
import com.easyshop.mc.shopeasy.main.Model.WishlistProduct;
import com.easyshop.mc.shopeasy.main.Util.ProductsImages;
import com.squareup.picasso.Picasso;
import com.easyshop.mc.shopeasy.R;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Siddartha on 2/9/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ProductHolder> {

    private List<Product> products;
    private Context context;
    private boolean isWishList;

    public RecyclerAdapter(Context context, List<Product> products, boolean isWishList) {
        this.context = context;
        this.products = products;
        this.isWishList = isWishList;
    }

    public static class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private static final String PRODUCT_KEY = "PRODUCT";

        private ImageView product_thumb;
        private TextView product_name;
        private TextView product_desc;
        private TextView product_dis;
        private TextView product_price;
        private Button cartButton;
        private Product product;

        private boolean isClicked = false;
        private boolean isWishList;

        public ProductHolder(View view, boolean isWishList) {
            super(view);
            product_thumb = (ImageView) view.findViewById(R.id.product_thumb);
            product_name = (TextView) view.findViewById(R.id.product_name);
            product_desc = (TextView) view.findViewById(R.id.product_desc);
            product_dis = (TextView)view.findViewById(R.id.product_dis);
            product_price = (TextView)view.findViewById(R.id.product_price);
            this.isWishList = isWishList;
            if(!isWishList){
                cartButton = (Button)view.findViewById(R.id.cartButton);

                cartButton.setOnClickListener(this);
            }
            view.setOnClickListener(this);
            ProductsImages.populateImages();
        }

        @Override
        public void onClick(View v) {
            Context context = itemView.getContext();
            if(v.getId() == cartButton.getId()){
                if(!isWishList){
                    if(isClicked)
                    {
                        cartButton.setText("Add");
                        cartButton.setTextAppearance(context, R.style.cartButton);
                        isClicked = false;
                        WishlistProduct wishProduct = new WishlistProduct(product.getPid());
                        ShopEasyDBHelper.getInstance(context).removeWishlistProduct(wishProduct);
                        AppDataModel.getAppDataModel().removeOneCartItems();

                    }else{
                        cartButton.setText("Remove");
                        cartButton.setTextAppearance(context, R.style.cartRemoveButton);
                        isClicked =true;
                        WishlistProduct wishProduct = new WishlistProduct(product.getPid());
                        ShopEasyDBHelper.getInstance(context).addWishlistProduct(wishProduct);
                        AppDataModel.getAppDataModel().addOneToCart();
                    }
                }else{
                    Toast.makeText(v.getContext(), "Card Clicked", Toast.LENGTH_LONG).show();
                }
                }
        }


        public void bindProduct(Product product) {
            this.product = product;
            String url = ProductsImages.getUrl(product.getPid());
            Picasso.with(product_thumb.getContext()).load(url).into(product_thumb);
            product_name.setText(product.getPname());
            product_desc.setText(product.getCategory());
            product_dis.setText(String.valueOf(product.getDiscount()));
            product_price.setText("$"+String.valueOf(product.getPrice()));
        }
    }

    @Override
    public RecyclerAdapter.ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(isWishList){
            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_item_wish, parent, false);

            return new ProductHolder(inflatedView, true);
        }
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);

        return new ProductHolder(inflatedView, false);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ProductHolder holder, int position) {
        Product productItem = products.get(position);
        holder.bindProduct(productItem);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}

