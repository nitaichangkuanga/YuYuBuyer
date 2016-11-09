package com.wushuikeji.www.yuyubuyer.adapter;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.bean.ToUpRecordBean;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Jack_chentao
 */
public class ToUpRecordListViewAdapter extends BaseAdapter {

    private List<ToUpRecordBean> toUpRecordBeanList;

    public ToUpRecordListViewAdapter(List<ToUpRecordBean> toUpRecordBeanList) {
        this.toUpRecordBeanList = toUpRecordBeanList;
    }

    @Override
    public int getCount() {
        if (toUpRecordBeanList != null && toUpRecordBeanList.size() > 0) {
            return toUpRecordBeanList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (toUpRecordBeanList != null && toUpRecordBeanList.size() > 0) {
            return toUpRecordBeanList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (toUpRecordBeanList != null && toUpRecordBeanList.size() > 0) {
            return position;
        } else {
            return 0;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHold = null;
        if (convertView == null) {
            convertView = View.inflate(UIUtils.getContext(), R.layout.item_listview_touprecord, null);
            viewHold = new ViewHolder(convertView);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHolder) convertView.getTag();
        }
        //设置数据
        viewHold.toupRecordMoney.setText("￥"+toUpRecordBeanList.get(position).toUpRecordmoney);
        viewHold.toupRecordDate.setText(toUpRecordBeanList.get(position).toUpRecordDate);
        viewHold.toupRecordTime.setText(toUpRecordBeanList.get(position).toUpRecordTime);

        String reportStatus = toUpRecordBeanList.get(position).toUpRecordStatus;
        if("进行中".equals(reportStatus)) {
            viewHold.toupRecordStatus.setTextColor(Color.parseColor("#F3Ab9E"));
        }else {
            viewHold.toupRecordStatus.setTextColor(Color.parseColor("#B40808"));
        }
        viewHold.toupRecordStatus.setText(reportStatus);

        return convertView;
    }

    static class ViewHolder {

        @InjectView(R.id.tv_itemToupRecord_content)
        TextView toupRecordMoney;

        @InjectView(R.id.tv_itemToupRecord_status)
        TextView toupRecordStatus;

        @InjectView(R.id.tv_itemToupRecord_date)
        TextView toupRecordDate;

        @InjectView(R.id.tv_itemToupRecord_time)
        TextView toupRecordTime;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
