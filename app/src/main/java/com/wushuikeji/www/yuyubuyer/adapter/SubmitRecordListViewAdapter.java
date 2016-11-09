package com.wushuikeji.www.yuyubuyer.adapter;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.bean.SubmitBean;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Jack_chentao
 */
public class SubmitRecordListViewAdapter extends BaseAdapter {

    private List<SubmitBean> submitRecordBeanList;

    public SubmitRecordListViewAdapter(List<SubmitBean> submitRecordBeanList) {
        this.submitRecordBeanList = submitRecordBeanList;
    }

    @Override
    public int getCount() {
        if (submitRecordBeanList != null && submitRecordBeanList.size() > 0) {
            return submitRecordBeanList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (submitRecordBeanList != null && submitRecordBeanList.size() > 0) {
            return submitRecordBeanList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (submitRecordBeanList != null && submitRecordBeanList.size() > 0) {
            return position;
        } else {
            return 0;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHold = null;
        if (convertView == null) {
            convertView = View.inflate(UIUtils.getContext(), R.layout.item_listview_submitrecord, null);
            viewHold = new ViewHolder(convertView);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHolder) convertView.getTag();
        }
        //设置数据
        viewHold.submitRecordMoney.setText("￥"+submitRecordBeanList.get(position).submitRecordmoney);
        viewHold.submitRecordDate.setText(submitRecordBeanList.get(position).submitRecordDate);
        viewHold.submitRecordTime.setText(submitRecordBeanList.get(position).submitRecordTime);

        String submitRecordStatus = submitRecordBeanList.get(position).submitRecordStatus;
        if("审核中".equals(submitRecordStatus)) {
            viewHold.submitRecordStatus.setTextColor(Color.parseColor("#F3Ab9E"));
        }else {
            viewHold.submitRecordStatus.setTextColor(Color.parseColor("#B40808"));
        }
        viewHold.submitRecordStatus.setText(submitRecordStatus);

        return convertView;
    }

    static class ViewHolder {

        @InjectView(R.id.tv_itemSubmitRecord_content)
        TextView submitRecordMoney;

        @InjectView(R.id.tv_itemSubmitRecord_status)
        TextView submitRecordStatus;

        @InjectView(R.id.tv_itemSubmitRecord_date)
        TextView submitRecordDate;

        @InjectView(R.id.tv_itemSubmitRecord_time)
        TextView submitRecordTime;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
