package com.wushuikeji.www.yuyubuyer.jsonparse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack_chentao
 * @time 2016/10/17 0017 上午 11:58.
 * @des ${TODO}
 */
public class ShopDetailsIsFavoriteInfoParse {
    public static Map<String, String> favoriteInfoParse (String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);

            //是否收藏
            String favoriteInfo = obj.optString("favoriteInfo");

            if (favoriteInfo == null || favoriteInfo.equals("")) {
                map.put("favoriteInfo", "");
            } else {
                map.put("favoriteInfo", favoriteInfo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
