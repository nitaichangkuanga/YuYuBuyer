package com.wushuikeji.www.yuyubuyer.jsonparse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack_chentao
 */
public class VideoJsonParse {

    public static Map<String, String> videoJsonParse(String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONObject jsonObject = obj.optJSONObject("content");

            //鱼鱼的名字
            String username = jsonObject.optString("username");
            if (username == null || username.equals("") || username.equals("null")) {
                map.put("username", "");
            } else {
                map.put("username", username);
            }
            //sex
            String sex = jsonObject.optString("sex");
            if (sex == null || sex.equals("") || sex.equals("null")) {
                map.put("sex", "");
            } else {
                map.put("sex", sex);
            }

            //age
            String age = jsonObject.optString("age");
            if (age == null || age.equals("") || age.equals("null")) {
                map.put("age", "");
            } else {
                map.put("age", age);
            }

            //head_img
            String head_img = jsonObject.optString("head_img");
            if (head_img == null || head_img.equals("") || head_img.equals("null")) {
                map.put("head_img", "");
            } else {
                map.put("head_img", head_img);
            }

            //id
            String id = jsonObject.optString("id");
            if (id == null || id.equals("") || id.equals("null")) {
                map.put("id", "");
            } else {
                map.put("id", id);
            }

            //user_level buyer的等级
            String user_level = jsonObject.optString("user_level");
            if (user_level == null || user_level.equals("") || user_level.equals("null")) {
                map.put("user_level", "");
            } else {
                map.put("user_level", user_level);
            }

            //area_id 商圈的信息
            String area_id = jsonObject.optString("area_id");
            if (area_id == null || area_id.equals("") || area_id.equals("null")) {
                map.put("area_id", "");
            } else {
                map.put("area_id", area_id);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
