package com.wushuikeji.www.yuyubuyer.jsonparse;

import com.wushuikeji.www.yuyubuyer.bean.BankBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jack_chentao
 */
public class BankInfoParse {
    public static List<BankBean> bankInfoParse(String jsonString) {

        List<BankBean> list = new ArrayList<BankBean>();

        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONArray jsonArray = obj.optJSONArray("content");
            if (jsonArray == null) {
                return null;
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {

                    BankBean mBankBean = new BankBean();

                    JSONObject item_obj = jsonArray.getJSONObject(i);

                    //ID_number
                    String ID_number = item_obj.optString("ID_number");
                    if (ID_number ==null || ID_number.equals("") || "null".equals(ID_number)) {
                        mBankBean.ID_number = "";
                    }else {
                        mBankBean.ID_number = ID_number;
                    }

                    //card_number
                    String card_number = item_obj.optString("card_number");
                    if (card_number ==null || card_number.equals("") || "null".equals(card_number)) {
                        mBankBean.bankFooterNum = "";
                    }else if(card_number.length() > 4){
                        //截取后四位
                        String footerNum = card_number.substring(card_number.length() - 4, card_number.length());
                        mBankBean.bankFooterNum = footerNum;
                    }

                    //bank_id
                    String bank_id = item_obj.optString("bank_id");
                    if (bank_id ==null || bank_id.equals("") || "null".equals(bank_id)) {
                        mBankBean.bank_id = "";
                    }else {
                        mBankBean.bank_id = bank_id;
                    }

                    //bank_name
                    String bank_name = item_obj.optString("bank_name");
                    if (bank_name ==null || bank_name.equals("") || "null".equals(bank_name)) {
                        mBankBean.bankType = "";
                    }else {
                        mBankBean.bankType = bank_name;
                    }
                    //real_name
                    String real_name = item_obj.optString("real_name");
                    if (real_name ==null || real_name.equals("") || "null".equals(real_name)) {
                        mBankBean.real_name = "";
                    }else {
                        mBankBean.real_name = real_name;
                    }

                    list.add(mBankBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
