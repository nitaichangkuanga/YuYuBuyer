package com.wushuikeji.www.yuyubuyer.jsonparse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack_chentao
 * @time 2016/10/12 0012 下午 4:58.
 * @des ${TODO}
 */
public class SendAuthoCodeParse {
    public static Map<String,String> sendAuthoCodeJson(String jsonString) {

        Map<String,String> map = new HashMap<String,String>();

        try {
            JSONObject obj = new JSONObject(jsonString);
            String response_code = obj.optString("response_code");
            if(response_code==null || response_code.equals("")) {
                map.put("response_code","");
            }else {
                map.put("response_code",response_code);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
