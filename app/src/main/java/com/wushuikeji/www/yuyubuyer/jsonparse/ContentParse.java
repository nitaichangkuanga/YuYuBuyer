package com.wushuikeji.www.yuyubuyer.jsonparse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack_chentao
 */
public class ContentParse {

    public static Map<String, String> contentParse(String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);

            //content
            String content = obj.optString("content");
            if (content == null || content.equals("") || content.equals("null")) {
                map.put("content", "");
            } else {
                map.put("content", content);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
