package com.wushuikeji.www.yuyubuyer.jsonparse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack_chentao
 */
public class ContentJsonParse {

    public static Map<String, String> contentJsonParse(String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONObject jsonObject = obj.optJSONObject("content");
            String order_no = jsonObject.optString("order_no");

            if (order_no == null || order_no.equals("") || order_no.equals("null")) {
                map.put("order_no", "");
            } else {
                map.put("order_no", order_no);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
