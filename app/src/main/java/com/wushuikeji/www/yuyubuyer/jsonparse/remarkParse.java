package com.wushuikeji.www.yuyubuyer.jsonparse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack_chentao
 * @time 2016/10/18 0018 上午 11:04.
 * @des ${TODO}
 */
public class remarkParse {

    public static Map<String, String> remarkParse(String jsonString) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONObject userObj = obj.getJSONObject("proInfo");

            String remark = userObj.optString("remark");

            JSONArray jsonArray = new JSONArray(remark);
            if(jsonArray != null && jsonArray.length() > 0) {
                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String grade = jsonObject.optString("一级");
                    if (grade == null || grade.equals("")) {
                        map.put("grade", "");
                    } else {
                        map.put("grade", grade);
                    }
                }
            }
            //            if (id == null || id.equals("")) {
//                map.put("id", "");
//            } else {
//                map.put("id", id);
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
