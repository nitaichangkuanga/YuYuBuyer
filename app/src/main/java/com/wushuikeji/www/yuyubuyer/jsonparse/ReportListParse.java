package com.wushuikeji.www.yuyubuyer.jsonparse;

import android.text.TextUtils;

import com.wushuikeji.www.yuyubuyer.bean.ReportBean;
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
public class ReportListParse {
    public static List<ReportBean> reportListParse(String jsonString) {

        List<ReportBean> list = new ArrayList<ReportBean>();

        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONArray jsonArray = obj.optJSONArray("content");
            if (jsonArray == null) {
                return null;
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {

                    ReportBean mReportBean = new ReportBean();

                    JSONObject item_obj = jsonArray.getJSONObject(i);

                    //id
                    String id = item_obj.optString("id");
                    if (id ==null || id.equals("") || "null".equals(id)) {
                        mReportBean.reportId = "";
                    }else {
                        mReportBean.reportId = id;
                    }


                    //create_time
                    String createTime = item_obj.optString("create_time");
                    if(!TextUtils.isEmpty(createTime)) {
                        String allTime = DateUtils.getDateToString(Long.parseLong(createTime));
                        //切割 2016-10-19 18:00:00
                        if(!TextUtils.isEmpty(allTime)) {
                            String[] splitArray = allTime.split(" ");
                            mReportBean.reportDate = splitArray[0];
                            mReportBean.reportTime = splitArray[1];
                        }else {
                            mReportBean.reportDate = "";
                            mReportBean.reportTime = "";
                        }
                    }

                    //content
                    String content = item_obj.optString("content");
                    if (content ==null || content.equals("") || "null".equals(content)) {
                        mReportBean.reportContent = "";
                    }else {
                        mReportBean.reportContent = content;
                    }

                    //status  1处理中  2已处理
                    String status = item_obj.optString("status");
                    if (status ==null || status.equals("") || "null".equals(status)) {
                        mReportBean.reportStatus = "";
                    }else {
                        if("1".equals(status)) {
                            mReportBean.reportStatus = "处理中";
                        }else if("2".equals(status)) {
                            mReportBean.reportStatus = "已处理";
                        }
                    }
                    //buyername
                    String buyername = item_obj.optString("buyername");
                    if (buyername ==null || buyername.equals("") || "null".equals(buyername)) {
                        mReportBean.userName = "";
                    }else {
                        mReportBean.userName = buyername;
                    }
                    list.add(mReportBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
