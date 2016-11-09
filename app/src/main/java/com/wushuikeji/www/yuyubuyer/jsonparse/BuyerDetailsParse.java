package com.wushuikeji.www.yuyubuyer.jsonparse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack_chentao
 * @time 2016/10/13 0013 下午 1:56.
 * @des ${TODO}
 */
public class BuyerDetailsParse {

    public static Map<String, String> buyerDetailsUserInfoParse(String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONObject userInfo = obj.optJSONObject("userInfo");
            if (userInfo == null) {
                return null;
            } else {

                //im_name
                String imName = userInfo.optString("im_name");
                if (imName == null || imName.equals("") || imName.equals("null")) {
                    map.put("imName", "");
                } else {
                    map.put("imName", imName);
                }

                //buyerId
                String buyerId = userInfo.optString("id");
                if (buyerId == null || buyerId.equals("") || buyerId.equals("null")) {
                    map.put("buyerId", "");
                } else {
                    map.put("buyerId", buyerId);
                }

                //username
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

                //sex
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

                //followInfo关注
                String followInfo = obj.optString("followInfo");
                if (followInfo == null || followInfo.equals("") || followInfo.equals("null")) {
                    map.put("followInfo", "");
                } else {
                    map.put("followInfo", followInfo);
                }
                //fanInfo粉丝
                String fanInfo = obj.optString("fanInfo");
                if (fanInfo == null || fanInfo.equals("") || fanInfo.equals("null")) {
                    map.put("fanInfo", "");
                } else {
                    map.put("fanInfo", fanInfo);
                }

                //friendInfo好友
                String friendInfo = obj.optString("friendInfo");
                if (friendInfo == null || friendInfo.equals("") || friendInfo.equals("null")) {
                    map.put("friendInfo", "");
                } else {
                    map.put("friendInfo", friendInfo);
                }

                //orderInfo订单量
                String orderInfo = obj.optString("orderInfo");
                if (orderInfo == null || orderInfo.equals("") || orderInfo.equals("null")) {
                    map.put("orderInfo", "");
                } else {
                    map.put("orderInfo", orderInfo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Map<String, String> buyerDetailsStatusInfoParse(String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONObject statusInfo = obj.optJSONObject("statusInfo");
            if (statusInfo == null) {
                return null;
            } else {
                //is_video
                String is_video = statusInfo.optString("is_video");
                if (is_video == null || is_video.equals("") || is_video.equals("null")) {
                    map.put("is_video", "");
                } else {
                    map.put("is_video", is_video);
                }
                //is_shopping
                String is_shopping = statusInfo.optString("is_shopping");
                if (is_shopping == null || is_shopping.equals("") || is_shopping.equals("null")) {
                    map.put("is_shopping", "");
                } else {
                    map.put("is_shopping", is_shopping);
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Map<String, String> buyerDetailsGradeInfoParse(String jsonString) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONObject gradeInfo = obj.optJSONObject("gradeInfo");
            if (gradeInfo == null) {
                return null;
            } else {
                //grades
                String grades = gradeInfo.optString("grades");
                if (grades == null || grades.equals("") || grades.equals("null")) {
                    map.put("grades", "");
                } else {
                    map.put("grades", grades);
                }

                //online_time（秒）
                String onlineTime = gradeInfo.optString("online_time");
                if (onlineTime == null || onlineTime.equals("") || "null".equals(onlineTime)) {
                    map.put("online_time", "");
                } else {
                    map.put("online_time", onlineTime);
                }


                //credits(信用度)
                String credits = gradeInfo.optString("credits");
                if (credits == null || credits.equals("") || credits.equals("null")) {
                    map.put("credits", "");
                } else {
                    map.put("credits", credits);
                }
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Map<String, String> buyerDetailsAreaInfoParse(String jsonString) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONObject area_info = obj.optJSONObject("area_info");
            if (area_info == null) {
                return null;
            } else {
                //area_name
                String area_name = area_info.optString("area_name");
                if (area_name == null || area_name.equals("") || area_name.equals("null")) {
                    map.put("area_name", "");
                } else {
                    map.put("area_name", area_name);
                }
                //city_name
                String city_name = area_info.optString("city_name");
                if (city_name == null || city_name.equals("") || city_name.equals("null")) {
                    map.put("city_name", "");
                } else {
                    map.put("city_name", city_name);
                }
                //area_id
                String area_id = area_info.optString("area_id");
                if (area_id == null || area_id.equals("") || area_id.equals("null")) {
                    map.put("area_id", "");
                } else {
                    map.put("area_id", area_id);
                }
                //city_id
                String city_id = area_info.optString("city_id");
                if (city_id == null || city_id.equals("") || city_id.equals("null")) {
                    map.put("city_id", "");
                } else {
                    map.put("city_id", city_id);
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

}
