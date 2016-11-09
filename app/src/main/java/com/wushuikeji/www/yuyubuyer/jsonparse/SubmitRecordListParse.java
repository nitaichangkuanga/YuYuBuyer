package com.wushuikeji.www.yuyubuyer.jsonparse;

import android.text.TextUtils;

import com.wushuikeji.www.yuyubuyer.bean.SubmitBean;
import com.wushuikeji.www.yuyubuyer.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jack_chentao
 * @time 2016/10/13 0013 下午 1:56.
 * @des ${TODO}
 */
public class SubmitRecordListParse {
    public static List<SubmitBean> submitRecordListParse(String jsonString) {

        List<SubmitBean> list = new ArrayList<SubmitBean>();

        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONArray jsonArray = obj.optJSONArray("content");
            if (jsonArray == null) {
                return null;
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {

                    SubmitBean mSubmitBean = new SubmitBean();

                    JSONObject item_obj = jsonArray.getJSONObject(i);

                    //id
                    String id = item_obj.optString("id");
                    if (id ==null || id.equals("") || "null".equals(id)) {
                        mSubmitBean.submitRecordId = "";
                    }else {
                        mSubmitBean.submitRecordId = id;
                    }

                    //银行卡bank_id
                    String bank_id = item_obj.optString("bank_id");
                    if (bank_id ==null || bank_id.equals("") || "null".equals(bank_id)) {
                        mSubmitBean.submitRecordBankId = "";
                    }else {
                        mSubmitBean.submitRecordBankId = bank_id;
                    }


                    //create_time
                    String createTime = item_obj.optString("create_time");
                    if(!TextUtils.isEmpty(createTime)) {
                        String allTime = DateUtils.getDateToString(Long.parseLong(createTime));
                        //切割 2016-10-19 18:00:00
                        if(!TextUtils.isEmpty(allTime)) {
                            String[] splitArray = allTime.split(" ");
                            mSubmitBean.submitRecordDate = splitArray[0];
                            mSubmitBean.submitRecordTime = splitArray[1];
                        }else {
                            mSubmitBean.submitRecordDate = "";
                            mSubmitBean.submitRecordTime = "";
                        }
                    }

                    //count
                    String count = item_obj.optString("count");
                    if (count ==null || count.equals("") || "null".equals(count)) {
                        mSubmitBean.submitRecordmoney = "";
                    }else {
                        mSubmitBean.submitRecordmoney = count;
                    }

                    //status  1审核中  2已审核待提现  3已完成
                    String status = item_obj.optString("status");
                    if (status ==null || status.equals("") || "null".equals(status)) {
                        mSubmitBean.submitRecordStatus = "";
                    }else {
                        if("1".equals(status)) {
                            mSubmitBean.submitRecordStatus = "审核中";
                        }else if("2".equals(status)) {
                            mSubmitBean.submitRecordStatus = "已审核待提现";
                        }else if("3".equals(status)) {
                            mSubmitBean.submitRecordStatus = "已完成";
                        }
                    }

                    list.add(mSubmitBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
