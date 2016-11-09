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
public class ContentSysMessDetailsParse {

    public static Map<String, String> contentSysMessDetailsParse(String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONObject jsonObject = obj.optJSONObject("content");

            String content = jsonObject.optString("content");
            if (content == null || content.equals("") || content.equals("null")) {
                map.put("content", "");
            } else {
                map.put("content", content);
            }

            //create_time
            String createTime = jsonObject.optString("create_time");
            if(!TextUtils.isEmpty(createTime)) {
                String allTime = DateUtils.getDateToString(Long.parseLong(createTime));
                if(!TextUtils.isEmpty(createTime)) {
                    map.put("createTime", allTime);
                }else {
                    map.put("createTime", "");
                }
            }else {
                map.put("createTime", "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
