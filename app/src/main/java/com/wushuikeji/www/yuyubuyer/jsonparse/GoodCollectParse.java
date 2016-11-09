package com.wushuikeji.www.yuyubuyer.jsonparse;

import com.wushuikeji.www.yuyubuyer.bean.ShopDetailsBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jack_chentao
 * @time 2016/10/18 0018 下午 7:52.
 * @des ${TODO}
 */
public class GoodCollectParse {
    public static List<ShopDetailsBean> goodCollectParse(String jsonString) {

        List<ShopDetailsBean> list = new ArrayList<ShopDetailsBean>();

        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONArray jsonArray = obj.optJSONArray("content");
            if (jsonArray == null) {
                return null;
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {

                    ShopDetailsBean mShopDetailsBean = new ShopDetailsBean();

                    JSONObject item_obj = jsonArray.getJSONObject(i);
                    //商品的ID
                    String productId = item_obj.optString("productId");
                    if (productId ==null || productId.equals("")) {
                        mShopDetailsBean.productId = "";
                    }else {
                        mShopDetailsBean.productId = productId;
                    }
                    //商品的名字
                    String product_name = item_obj.optString("product_name");
                    if (product_name ==null || product_name.equals("")) {
                        mShopDetailsBean.shopDetailsTitle = "";
                    }else {
                        mShopDetailsBean.shopDetailsTitle = product_name;
                    }

                    //商品的图片地址
                    String product_imageUrl = item_obj.optString("product_imageUrl");
                    if (product_imageUrl ==null || product_imageUrl.equals("")) {
                        mShopDetailsBean.imgUrl = "";
                    }else {
                        mShopDetailsBean.imgUrl = product_imageUrl;
                    }

                    //商品的折扣价
                    String discount_price = item_obj.optString("discount_price");
                    if (discount_price ==null || discount_price.equals("")) {
                        mShopDetailsBean.money = "";
                    }else {
                        mShopDetailsBean.money = discount_price;
                    }

                    //商品的打折
                    String discount = item_obj.optString("discount");
                    if (discount ==null || discount.equals("")) {
                        mShopDetailsBean.discount = "";
                    }else {
                        mShopDetailsBean.discount = discount;
                    }

                    //默认添加的   正常出售
                    mShopDetailsBean.goodType = "正常出售";

                    list.add(mShopDetailsBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
