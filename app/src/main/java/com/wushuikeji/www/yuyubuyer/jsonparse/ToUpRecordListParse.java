package com.wushuikeji.www.yuyubuyer.jsonparse;

import android.text.TextUtils;

import com.wushuikeji.www.yuyubuyer.bean.ToUpRecordBean;
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
public class ToUpRecordListParse {
    public static List<ToUpRecordBean> toUpRecordListParse(String jsonString) {

        List<ToUpRecordBean> list = new ArrayList<ToUpRecordBean>();

        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONArray jsonArray = obj.optJSONArray("content");
            if (jsonArray == null) {
                return null;
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {

                    ToUpRecordBean mToUpRecordBean = new ToUpRecordBean();

                    JSONObject item_obj = jsonArray.getJSONObject(i);

                    //id
                    String id = item_obj.optString("id");
                    if (id ==null || id.equals("") || "null".equals(id)) {
                        mToUpRecordBean.toUpRecordId = "";
                    }else {
                        mToUpRecordBean.toUpRecordId = id;
                    }


                    //create_time
                    String createTime = item_obj.optString("create_time");
                    if(!TextUtils.isEmpty(createTime)) {
                        String allTime = DateUtils.getDateToString(Long.parseLong(createTime));
                        //切割 2016-10-19 18:00:00
                        if(!TextUtils.isEmpty(allTime)) {
                            String[] splitArray = allTime.split(" ");
                            mToUpRecordBean.toUpRecordDate = splitArray[0];
                            mToUpRecordBean.toUpRecordTime = splitArray[1];
                        }else {
                            mToUpRecordBean.toUpRecordDate = "";
                            mToUpRecordBean.toUpRecordTime = "";
                        }
                    }

                    //count
                    String count = item_obj.optString("count");
                    if (count ==null || count.equals("") || "null".equals(count)) {
                        mToUpRecordBean.toUpRecordmoney = "";
                    }else {
                        mToUpRecordBean.toUpRecordmoney = count;
                    }

                    //status  1进行中  2已支付  3完成
                    String status = item_obj.optString("status");
                    if (status ==null || status.equals("") || "null".equals(status)) {
                        mToUpRecordBean.toUpRecordStatus = "";
                    }else {
                        if("1".equals(status)) {
                            mToUpRecordBean.toUpRecordStatus = "进行中";
                        }else if("2".equals(status)) {
                            mToUpRecordBean.toUpRecordStatus = "已支付";
                        }else if("3".equals(status)) {
                            mToUpRecordBean.toUpRecordStatus = "完成";
                        }
                    }

                    list.add(mToUpRecordBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
