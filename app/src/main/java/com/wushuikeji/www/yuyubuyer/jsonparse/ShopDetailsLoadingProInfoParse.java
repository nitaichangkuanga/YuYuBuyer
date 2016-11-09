package com.wushuikeji.www.yuyubuyer.jsonparse;

import com.wushuikeji.www.yuyubuyer.bean.ShopDetailsBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jack_chentao
 * @time 2016/10/13 0013 下午 1:56.
 * @des 解析商品
 */
public class ShopDetailsLoadingProInfoParse {

    public static List<ShopDetailsBean> loadingMoreProInfoParse(String jsonString) {

        List<ShopDetailsBean> list = new ArrayList<ShopDetailsBean>();

        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONArray shop_productArray = obj.getJSONArray("content");
            if (shop_productArray == null) {
                return null;
            } else {
                for (int i = 0; i < shop_productArray.length(); i++) {

                    ShopDetailsBean mShopDetailsBean = new ShopDetailsBean();

                    JSONObject item_obj = shop_productArray.getJSONObject(i);
                    //商品的ID
                    String proId = item_obj.optString("proId");
                    if (proId ==null || proId.equals("")) {
                        mShopDetailsBean.productId = "";
                    }else {
                        mShopDetailsBean.productId = proId;
                    }
                    //图片的url
                    String url = item_obj.optString("url");
                    if (url ==null || url.equals("")) {
                        mShopDetailsBean.imgUrl = "";
                    }else {
                        mShopDetailsBean.imgUrl = url;
                    }
                    //商品的名字
                    String name = item_obj.optString("name");
                    if (name ==null || name.equals("")) {
                        mShopDetailsBean.shopDetailsTitle = "";
                    }else {
                        mShopDetailsBean.shopDetailsTitle = name;
                    }
                    //商品的discount
                    String discount = item_obj.optString("discount");
                    if (discount ==null || discount.equals("")) {
                        mShopDetailsBean.discount = "";
                    }else {
                        mShopDetailsBean.discount = discount;
                    }
                    //商品的price
                    String price = item_obj.optString("price");
                    if (price ==null || price.equals("")) {
                        mShopDetailsBean.money = "";
                    }else {
                        mShopDetailsBean.money = price;
                    }
                    list.add(mShopDetailsBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
