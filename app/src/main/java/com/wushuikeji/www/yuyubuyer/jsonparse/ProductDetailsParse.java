package com.wushuikeji.www.yuyubuyer.jsonparse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack_chentao
 * @time 2016/10/13 0013 下午 1:56.
 * @des ${TODO}
 */
public class ProductDetailsParse {
    public static Map<String, String> productDetailsParse(String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);
            //解析电话

            String phone_number = obj.optString("phone_number");
            if (phone_number == null || phone_number.equals("")) {
                map.put("phone_number", "");
            } else {
                map.put("phone_number", phone_number);
            }

            JSONObject userObj = obj.getJSONObject("proInfo");

            String id = userObj.optString("id");
            if (id == null || id.equals("")) {
                map.put("id", "");
            } else {
                map.put("id", id);
            }

            String name = userObj.optString("name");
            if (name == null || name.equals("")) {
                map.put("name", "");
            } else {
                map.put("name", name);
            }

            String description = userObj.optString("description");
            if (description == null || description.equals("")) {
                map.put("description", "");
            } else {
                map.put("description", description);
            }

            String price = userObj.optString("price");
            if (price == null || price.equals("")) {
                map.put("price", "");
            } else {
                map.put("price", price);
            }

            String discount_price = userObj.optString("discount_price");
            if (discount_price == null || discount_price.equals("")) {
                map.put("discount_price", "");
            } else {
                map.put("discount_price", discount_price);
            }

            String discount = userObj.optString("discount");
            if (discount == null || discount.equals("")) {
                map.put("discount", "");
            } else {
                map.put("discount", discount);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
