package com.wushuikeji.www.yuyubuyer.jsonparse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack_chentao
 */
public class BusinessJsonParse {

    public static Map<String, String> getCityNameParse(String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONObject cityInfoObject = obj.optJSONObject("cityInfo");

            //城市名name
            String city = cityInfoObject.optString("name");
            if (city == null || city.equals("") || city.equals("null")) {
                map.put("city", "");
            } else {
                map.put("city", city);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Map<String, String> getBusinessNameParse(String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONObject areaInfoObject = obj.optJSONObject("area_info");

            //商圈名name
            String name = areaInfoObject.optString("name");
            if (name == null || name.equals("") || name.equals("null")) {
                map.put("name", "");
            } else {
                map.put("name", name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
