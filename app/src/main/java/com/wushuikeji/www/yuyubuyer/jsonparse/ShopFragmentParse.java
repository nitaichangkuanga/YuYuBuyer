package com.wushuikeji.www.yuyubuyer.jsonparse;

import com.wushuikeji.www.yuyubuyer.bean.ShopBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jack_chentao
 * @time 2016/10/13 0013 下午 1:56.
 * @des ${TODO}
 */
public class ShopFragmentParse {
    public static List<ShopBean> shopFragmentParse(String jsonString) {

        List<ShopBean> list = new ArrayList<ShopBean>();

        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONArray jsonArray = obj.optJSONArray("content");
            if (jsonArray == null) {
                return null;
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {
                    ShopBean mShopBean = new ShopBean();
                    JSONObject item_obj = jsonArray.getJSONObject(i);
                    //商铺的ID
                    String shopId = item_obj.optString("shopId");
                    if (shopId ==null || shopId.equals("")) {
                        mShopBean.shopId = "";
                    }else {
                        mShopBean.shopId = shopId;
                    }
                    //商铺的名字
                    String shop_name = item_obj.optString("shop_name");
                    if (shop_name ==null || shop_name.equals("")) {
                        mShopBean.shopName = "";
                    }else {
                        mShopBean.shopName = shop_name;
                    }
                    //商铺的图片地址
                    String image_url = item_obj.optString("image_url");
                    if (image_url ==null || image_url.equals("")) {
                        mShopBean.imgUrl = "";
                    }else {
                        mShopBean.imgUrl = image_url;
                    }

                    //商铺的地址
                    String address = item_obj.optString("address");
                    if (address ==null || address.equals("") || address.equals("null")) {
                        mShopBean.shopAddress = "";
                    }else {
                        mShopBean.shopAddress = address;
                    }

                    //商铺的折扣价
                    String product_discount = item_obj.optString("product_discount");
                    if (product_discount ==null || product_discount.equals("")) {
                        mShopBean.discount = "";
                    }else {
                        mShopBean.discount = product_discount;
                    }

                    //商铺的距离
                    String juli = item_obj.optString("juli");
                    if (juli ==null || juli.equals("")) {
                        mShopBean.distance = "";
                    }else {
                        mShopBean.distance = juli;
                    }

                    list.add(mShopBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
