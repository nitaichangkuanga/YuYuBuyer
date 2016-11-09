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
public class FragmentUserInfoParse {


    public static Map<String, String> UserGradeInfoParse(String jsonString) {
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

                //integral(积分)
                String integral = gradeInfo.optString("integral");
                if (integral == null || integral.equals("") || integral.equals("null")) {
                    map.put("integral", "");
                } else {
                    map.put("integral", integral);
                }

                //credits(信用度)
                String credits = gradeInfo.optString("credits");
                if (credits == null || credits.equals("") || credits.equals("null")) {
                    map.put("credits", "");
                } else {
                    map.put("credits", credits);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Map<String, String> userAttentionFriendAndFanParse(String jsonString) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);

            //关注followInfo
            String followInfo = obj.optString("followInfo");
            if (followInfo == null || followInfo.equals("") || followInfo.equals("null")) {
                map.put("followInfo", "");
            } else {
                map.put("followInfo", followInfo);
            }

            //粉丝fanInfo
            String fanInfo = obj.optString("fanInfo");
            if (fanInfo == null || fanInfo.equals("") || fanInfo.equals("null")) {
                map.put("fanInfo", "");
            } else {
                map.put("fanInfo", fanInfo);
            }

            //好友friendInfo
            String friendInfo = obj.optString("friendInfo");
            if (friendInfo == null || friendInfo.equals("") || friendInfo.equals("null")) {
                map.put("friendInfo", "");
            } else {
                map.put("friendInfo", friendInfo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 得到用户的头像
     */
    public static Map<String, String> userHeadImgParse(String jsonString) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONObject jsonObject = obj.optJSONObject("userInfo");

            String head_img = jsonObject.optString("head_img");
            if (head_img == null || head_img.equals("") || head_img.equals("null")) {
                map.put("head_img", "");
            } else {
                map.put("head_img", head_img);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

}
