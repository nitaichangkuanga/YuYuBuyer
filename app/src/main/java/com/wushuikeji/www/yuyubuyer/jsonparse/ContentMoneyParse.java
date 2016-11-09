package com.wushuikeji.www.yuyubuyer.jsonparse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack_chentao
 */
public class ContentMoneyParse {

    public static Map<String, String> contentParse(String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONObject jsonObject = obj.optJSONObject("content");
            String count = jsonObject.optString("count");

            if (count == null || count.equals("") || count.equals("null")) {
                map.put("count", "");
            } else {
                map.put("count", count);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
