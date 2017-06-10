package com.easyshop.mc.shopeasy.main.Util;

import java.util.HashMap;

/**
 * Created by Siddartha on 4/21/2017.
 */

public class ProductsImages {

    static HashMap<Long, String> images = new HashMap<Long, String>();

    public static void populateImages(){
        images.put(Long.valueOf(10),"https://usercontent1.hubstatic.com/4757890_f248.jpg");
        images.put(Long.valueOf(20), "http://pngimg.com/uploads/onion/onion_PNG3831.png");
        images.put(Long.valueOf(30), "http://pngimg.com/uploads/egg/egg_PNG25.png");
        images.put(Long.valueOf(40), "http://rk.wsimgs.com/wsimgs/rk/images/dp/wcm/201711/0021/chefn-veggichop-vegetable-chopper-c.jpg");
        images.put(Long.valueOf(50), "http://www.homefoodservices.com/Products/115510%20Boneless%20Chicken%20Breast.jpg");
        images.put(Long.valueOf(60), "http://www.wessonoil.com/sites/g/files/qyyrlu401/files/images/products/canola-oil-38888.png");
        images.put(Long.valueOf(70), "http://images.wisegeek.com/potatoes-against-white-background.jpg");
        images.put(Long.valueOf(80), "http://antarcticajournal.com/wp-content/uploads/2016/01/red-tomato-meteorite.jpg");
        images.put(Long.valueOf(90), "http://www.mango.org/Mangos/media/Media/Images/Header%20Images/facts.jpg");
        images.put(Long.valueOf(100), "http://images.nike.com/is/image/DotCom/PDP_HERO_ZOOM/848187_600_A_PREM/air-presto-essential-mens-shoe.jpg");

    }

    public static String getUrl(long productId){
        return images.get(productId);
    }
}
