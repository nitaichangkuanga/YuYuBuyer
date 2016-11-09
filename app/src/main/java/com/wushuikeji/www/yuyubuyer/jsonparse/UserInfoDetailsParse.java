package com.wushuikeji.www.yuyubuyer.jsonparse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack_chentao
 */
public class UserInfoDetailsParse {

    public static Map<String, String> userInfoDetailsParse(String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONObject userInfo = obj.optJSONObject("userInfo");
            if (userInfo == null) {
                return null;
            } else {
                //buyerId(10040)
                String buyerId = userInfo.optString("id");
                if (buyerId == null || buyerId.equals("") || buyerId.equals("null")) {
                    map.put("buyerId", "");
                } else {
                    map.put("buyerId", buyerId);
                }

                //username(你太猖狂啊)
                String username = userInfo.optString("username");
                if (username == null || username.equals("") || username.equals("null")) {
                    map.put("username", "");
                } else {
                    map.put("username", username);
                }

                //mobile
                String mobile = userInfo.optString("mobile");
                if (mobile == null || mobile.equals("") || mobile.equals("null")) {
                    map.put("mobile", "");
                } else {
                    map.put("mobile", mobile);
                }

                //sex(1 男  2 女)
                String sex = userInfo.optString("sex");
                if (sex == null || sex.equals("") || sex.equals("null")) {
                    map.put("sex", "");
                } else {
                    map.put("sex", sex);
                }
                //age
                String age = userInfo.optString("age");
                if (age == null || age.equals("") || age.equals("null")) {
                    map.put("age", "");
                } else {
                    map.put("age", age);
                }

                //head_img
                String head_img = userInfo.optString("head_img");
                if (head_img == null || head_img.equals("") || head_img.equals("null")) {
                    map.put("head_img", "");
                } else {
                    map.put("head_img", head_img);
                }

                //status
                String status = userInfo.optString("status");
                if (status == null || status.equals("") || status.equals("null")) {
                    map.put("status", "");
                } else {
                    map.put("status", status);
                }
                //description
                String description = userInfo.optString("description");
                if (description == null || description.equals("") || description.equals("null")) {
                    map.put("description", "");
                } else {
                    map.put("description", description);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 得到所属的商圈城市(成都)
     * @param jsonString
     * @return
     */
    public static Map<String, String> userInfoCityDetailsParse(String jsonString) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONObject gradeInfo = obj.optJSONObject("cityInfo");
            if (gradeInfo == null) {
                return null;
            } else {
                //city
                String city = gradeInfo.optString("name");
                if (city == null || city.equals("") || city.equals("null")) {
                    map.put("city", "");
                } else {
                    map.put("city", city);
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 得到所属的商圈地区(盐市口商圈)
     * @param jsonString
     * @return
     */
    public static Map<String, String> userInfoBusinessDetailsParse(String jsonString) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONObject area_info = obj.optJSONObject("area_info");
            if (area_info == null) {
                return null;
            } else {
                //name
                String businessName = area_info.optString("name");
                if (businessName == null || businessName.equals("") || businessName.equals("null")) {
                    map.put("businessName", "");
                } else {
                    map.put("businessName", businessName);
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

}
