package com.wushuikeji.www.yuyubuyer.adapter;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.bean.ReportBean;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Jack_chentao
 */
public class ReportListViewAdapter extends BaseAdapter {

    private List<ReportBean> reportBeanList;

    public ReportListViewAdapter(List<ReportBean> reportBeanList) {
        this.reportBeanList = reportBeanList;
    }

    @Override
    public int getCount() {
        if (reportBeanList != null && reportBeanList.size() > 0) {
            return reportBeanList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (reportBeanList != null && reportBeanList.size() > 0) {
            return reportBeanList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (reportBeanList != null && reportBeanList.size() > 0) {
            return position;
        } else {
            return 0;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHold = null;
        if (convertView == null) {
            convertView = View.inflate(UIUtils.getContext(), R.layout.item_listview_report, null);
            viewHold = new ViewHolder(convertView);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHolder) convertView.getTag();
        }
        //设置数据
        viewHold.userName.setText(reportBeanList.get(position).userName);
        viewHold.reportContent.setText(reportBeanList.get(position).reportContent);
        viewHold.reportDate.setText(reportBeanList.get(position).reportDate);
        viewHold.reportTime.setText(reportBeanList.get(position).reportTime);

        String reportStatus = reportBeanList.get(position).reportStatus;
        if("处理中".equals(reportStatus)) {
            viewHold.reportStatus.setTextColor(Color.parseColor("#F3Ab9E"));
        }else {
            viewHold.reportStatus.setTextColor(Color.parseColor("#B40808"));
        }
        viewHold.reportStatus.setText(reportStatus);


        return convertView;
    }

    static class ViewHolder {

        @InjectView(R.id.tv_itemReport_name)
        TextView userName;

        @InjectView(R.id.tv_itemReport_content)
        TextView reportContent;

        @InjectView(R.id.tv_itemReport_status)
        TextView reportStatus;

        @InjectView(R.id.tv_itemReport_date)
        TextView reportDate;

        @InjectView(R.id.tv_itemReport_time)
        TextView reportTime;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
