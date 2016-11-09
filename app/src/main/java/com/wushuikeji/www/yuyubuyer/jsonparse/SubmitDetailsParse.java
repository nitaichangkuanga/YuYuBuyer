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
public class SubmitDetailsParse {

    public static Map<String, String> submitDetailsParse(String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONObject info = obj.optJSONObject("content");
            if (info == null) {
                return null;
            } else {
                //create_time
                String createTime = info.optString("create_time");
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

                //card_number
                String card_number = info.optString("card_number");
                if (card_number == null || card_number.equals("") || card_number.equals("null")) {
                    map.put("card_number", "");
                } else {
                    map.put("card_number", card_number);
                }

                //count
                String count = info.optString("count");
                if (count == null || count.equals("") || count.equals("null")) {
                    map.put("count", "");
                } else {
                    map.put("count","￥" + count);
                }

                //status
                String status = info.optString("status");
                if (status == null || status.equals("") || status.equals("null")) {
                    map.put("status", "");
                } else {
                    if("1".equals(status)) {
                        map.put("status", "审核中");
                    }else if("2".equals(status)) {
                        map.put("status", "已审核待提现");
                    }else if("3".equals(status)) {
                        map.put("status", "已完成");
                    }
                }

                //bank_name
                String bank_name = info.optString("bank_name");
                if (bank_name == null || bank_name.equals("") || bank_name.equals("null")) {
                    map.put("bank_name", "");
                } else {
                    map.put("bank_name", bank_name);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
