package com.wushuikeji.www.yuyubuyer.jsonparse;

import com.wushuikeji.www.yuyubuyer.bean.ShopCollectBean;

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
public class ShopCollectParse {
    public static List<ShopCollectBean> shopCollectParse(String jsonString) {

        List<ShopCollectBean> list = new ArrayList<ShopCollectBean>();

        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONArray jsonArray = obj.optJSONArray("content");
            if (jsonArray == null) {
                return null;
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {
                    ShopCollectBean mShopCollectBean = new ShopCollectBean();
                    JSONObject item_obj = jsonArray.getJSONObject(i);
                    //商铺的ID
                    String shopId = item_obj.optString("shop_id");
                    if (shopId ==null || shopId.equals("")) {
                        mShopCollectBean.shopId = "";
                    }else {
                        mShopCollectBean.shopId = shopId;
                    }
                    //商铺的名字
                    String shop_name = item_obj.optString("shop_name");
                    if (shop_name ==null || shop_name.equals("")) {
                        mShopCollectBean.shopName = "";
                    }else {
                        mShopCollectBean.shopName = shop_name;
                    }
                    //商铺的图片地址
                    String image_url = item_obj.optString("image_url");
                    if (image_url ==null || image_url.equals("")) {
                        mShopCollectBean.imgUrl = "";
                    }else {
                        mShopCollectBean.imgUrl = image_url;
                    }

                    //商铺的地址
                    String address = item_obj.optString("address");
                    if (address ==null || address.equals("")) {
                        mShopCollectBean.shopAddress = "";
                    }else {
                        mShopCollectBean.shopAddress = address;
                    }

                    //商铺的折扣价
                    String trade_name = item_obj.optString("trade_name");
                    if (trade_name ==null || trade_name.equals("")) {
                        mShopCollectBean.trade_name = "";
                    }else {
                        mShopCollectBean.trade_name = trade_name;
                    }
                    list.add(mShopCollectBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
