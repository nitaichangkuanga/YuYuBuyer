package com.wushuikeji.www.yuyubuyer.jsonparse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack_chentao
 * @time 2016/10/18 0018 上午 11:04.
 * @des ${TODO}
 */
public class RealNameParse {

    public static Map<String, String> realNameParse(String jsonString) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONObject content = obj.optJSONObject("content");
            if (content == null) {
                return null;
            } else {
                //real_name
                String real_name = content.optString("real_name");
                if (real_name == null || real_name.equals("") || real_name.equals("null")) {
                    map.put("real_name", "");
                } else {
                    map.put("real_name", real_name);
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
