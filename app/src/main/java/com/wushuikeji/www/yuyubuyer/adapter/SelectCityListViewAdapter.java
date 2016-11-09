package com.wushuikeji.www.yuyubuyer.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.bean.SelectCityBean;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SelectCityListViewAdapter extends BaseAdapter {

    private List<SelectCityBean> selectCityBeanList;


    public SelectCityListViewAdapter(List<SelectCityBean> selectCityBeanList) {
        this.selectCityBeanList = selectCityBeanList;
    }

    @Override
    public int getCount() {
        if (selectCityBeanList != null && selectCityBeanList.size() > 0) {
            return selectCityBeanList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (selectCityBeanList != null && selectCityBeanList.size() > 0) {
            return selectCityBeanList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (selectCityBeanList != null && selectCityBeanList.size() > 0) {
            return position;
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHold = null;
        if (convertView == null) {
            convertView = View.inflate(UIUtils.getContext(), R.layout.item_listview_selectcity, null);
            viewHold = new ViewHolder(convertView);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHolder) convertView.getTag();
        }
        //设置数据
        viewHold.cityName.setText(selectCityBeanList.get(position).cityName);
        return convertView;
    }

    static class ViewHolder {

        //城市
        @InjectView(R.id.item_selectCity_cityName)
        TextView cityName;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
