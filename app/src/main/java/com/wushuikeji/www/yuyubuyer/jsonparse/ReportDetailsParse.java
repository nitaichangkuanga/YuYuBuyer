package com.wushuikeji.www.yuyubuyer.jsonparse;

import android.text.TextUtils;

import com.wushuikeji.www.yuyubuyer.utils.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack_chentao
 */
public class ReportDetailsParse {

    public static Map<String, String> reportDetailsParse(String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONObject bankDetailsInfo = obj.optJSONObject("content");
            if (bankDetailsInfo == null) {
                return null;
            } else {
                //update_time
                String update_time = bankDetailsInfo.optString("update_time");
                if(!TextUtils.isEmpty(update_time)) {
                    String allupdate_time = DateUtils.getDateToString(Long.parseLong(update_time));
                    if(!TextUtils.isEmpty(allupdate_time)) {
                        map.put("update_time", update_time);
                    }else {
                        map.put("update_time", "");
                    }
                }else {
                    map.put("update_time", "");
                }

                //create_time
                String createTime = bankDetailsInfo.optString("create_time");
                if(!TextUtils.isEmpty(createTime)) {
                    String allTime = DateUtils.getDateToString(Long.parseLong(createTime));
                    if(!TextUtils.isEmpty(createTime)) {
                        map.put("create_time", allTime);
                    }else {
                        map.put("create_time", "");
                    }
                }else {
                    map.put("create_time", "");
                }

                //user_id
                String user_id = bankDetailsInfo.optString("user_id");
                if (user_id == null || user_id.equals("") || user_id.equals("null")) {
                    map.put("user_id", "");
                } else {
                    map.put("user_id", user_id);
                }

                //content
                String content = bankDetailsInfo.optString("content");
                if (content == null || content.equals("") || content.equals("null")) {
                    map.put("content", "");
                } else {
                    map.put("content", content);
                }
                //status
                String status = bankDetailsInfo.optString("status");
                if (status == null || status.equals("") || status.equals("null")) {
                    map.put("status", "");
                } else {
                    map.put("status", status);
                }
                //buyername
                String buyername = bankDetailsInfo.optString("buyername");
                if (buyername == null || buyername.equals("") || buyername.equals("null")) {
                    map.put("buyername", "");
                } else {
                    map.put("buyername", buyername);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
