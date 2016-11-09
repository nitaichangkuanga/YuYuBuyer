package com.wushuikeji.www.yuyubuyer.jsonparse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack_chentao
 */
public class PersonalOrderParse {
    public static Map<String, String> personalOrderParse(String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONObject userObj = obj.getJSONObject("content");
            //用户的状态
            String status = userObj.optString("status");
            if (status == null || status.equals("") || "null".equals(status)) {
                map.put("status", "");
            } else {
                map.put("status", status);
            }
            //用户的视频接单
            String is_video = userObj.optString("is_video");
            if (is_video == null || is_video.equals("") || "null".equals(is_video)) {
                map.put("is_video", "");
            } else {
                map.put("is_video", is_video);
            }
            //用户的线下接单
            String is_shopping = userObj.optString("is_shopping");
            if (is_shopping == null || is_shopping.equals("") || "null".equals(is_shopping)) {
                map.put("is_shopping", "");
            } else {
                map.put("is_shopping", is_shopping);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
