package com.wushuikeji.www.yuyubuyer.jsonparse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack_chentao
 */
public class BankInfoDetailsParse {

    public static Map<String, String> bankInfoDetailsParse(String jsonString) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONObject bankDetailsInfo = obj.optJSONObject("content");
            if (bankDetailsInfo == null) {
                return null;
            } else {
                //ID_number
                String ID_number = bankDetailsInfo.optString("ID_number");
                if (ID_number == null || ID_number.equals("") || ID_number.equals("null")) {
                    map.put("ID_number", "");
                } else {
                    map.put("ID_number", ID_number);
                }

                //card_number
                String card_number = bankDetailsInfo.optString("card_number");
                if (card_number ==null || card_number.equals("") || "null".equals(card_number)) {
                    map.put("footerNum", "");
                }else if(card_number.length() > 4){
                    //截取后四位
                    String footerNum = card_number.substring(card_number.length() - 4, card_number.length());
                    map.put("footerNum", footerNum);
                }

                //bank_id
                String bank_id = bankDetailsInfo.optString("bank_id");
                if (bank_id == null || bank_id.equals("") || bank_id.equals("null")) {
                    map.put("bank_id", "");
                } else {
                    map.put("bank_id", bank_id);
                }

                //bank_name
                String bank_name = bankDetailsInfo.optString("bank_name");
                if (bank_name == null || bank_name.equals("") || bank_name.equals("null")) {
                    map.put("bank_name", "");
                } else {
                    map.put("bank_name", bank_name);
                }
                //real_name
                String real_name = bankDetailsInfo.optString("real_name");
                if (real_name == null || real_name.equals("") || real_name.equals("null")) {
                    map.put("real_name", "");
                } else {
                    map.put("real_name", real_name);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
