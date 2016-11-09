package com.wushuikeji.www.yuyubuyer.jsonparse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack_chentao
 */
public class LoginParse {
    public static Map<String, String> loginParse(String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONObject userObj = obj.getJSONObject("content");
            //用户的别名
            String username = userObj.optString("username");
            if (username == null || username.equals("") || "null".equals(username)) {
                map.put("username", "");
            } else {
                map.put("username", username);
            }
            //用户注册的手机号
            String mobile = userObj.optString("mobile");
            if (mobile == null || mobile.equals("") || "null".equals(mobile)) {
                map.put("mobile", "");
            } else {
                map.put("mobile", mobile);
            }
            //用户的别名
            String id = userObj.optString("id");
            if (id == null || id.equals("")) {
                map.put("id", "");
            } else {
                map.put("id", id);
            }

            //用户的环信账号
            String imName = userObj.optString("im_name");
            if (imName == null || imName.equals("") || "null".equals(imName)) {
                map.put("imName", "");
            } else {
                map.put("imName", imName);
            }

            //用户的环信密码
            String imPassword = userObj.optString("im_password");
            if (imPassword == null || imPassword.equals("") || "null".equals(imPassword)) {
                map.put("imPassword", "");
            } else {
                map.put("imPassword", imPassword);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
