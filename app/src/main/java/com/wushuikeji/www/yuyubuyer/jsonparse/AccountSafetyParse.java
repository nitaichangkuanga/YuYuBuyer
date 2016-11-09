package com.wushuikeji.www.yuyubuyer.jsonparse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack_chentao
 */
public class AccountSafetyParse {
    public static Map<String, String> accountSafetyParse(String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONObject userObj = obj.getJSONObject("content");
            //用户真实的姓名
            String real_name = userObj.optString("real_name");
            if (real_name == null || real_name.equals("") || "null".equals(real_name)) {
                map.put("real_name", "");
            } else {
                map.put("real_name", real_name);
            }
            //用户的身份证号
            String id_number = userObj.optString("id_number");
            if (id_number == null || id_number.equals("") || "null".equals(id_number)) {
                map.put("id_number", "");
            } else {
                map.put("id_number", id_number);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
