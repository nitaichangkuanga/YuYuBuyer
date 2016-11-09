package com.wushuikeji.www.yuyubuyer.jsonparse;

import com.wushuikeji.www.yuyubuyer.bean.SelectCityBean;

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
public class SelectCityParse {
    public static List<SelectCityBean> selectCityParse(String jsonString) {

        List<SelectCityBean> list = new ArrayList<SelectCityBean>();

        try {
            JSONObject obj = new JSONObject(jsonString);

            JSONArray jsonArray = obj.optJSONArray("content");
            if (jsonArray == null) {
                return null;
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {
                    SelectCityBean selectCityJsonBean = new SelectCityBean();
                    JSONObject item_obj = jsonArray.getJSONObject(i);
                    //城市的ID
                    String id = item_obj.optString("id");
                    if (id ==null || id.equals("")) {
                        selectCityJsonBean.id = "";
                    }else {
                        selectCityJsonBean.id = id;
                    }
                    //城市的名字
                    String name = item_obj.optString("name");
                    if (name ==null || name.equals("")) {
                        selectCityJsonBean.cityName = "";
                    }else {
                        selectCityJsonBean.cityName = name;
                    }
                    //城市所属于的商圈ID
                    String is_valid = item_obj.optString("is_valid");
                    if (is_valid ==null || is_valid.equals("")) {
                        selectCityJsonBean.is_valid = "";
                    }else {
                        selectCityJsonBean.is_valid = is_valid;
                    }

                    list.add(selectCityJsonBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
