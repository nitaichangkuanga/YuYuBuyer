package com.wushuikeji.www.yuyubuyer.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.bean.OrderBean;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Jack_chentao
 * @time 2016/9/28 0028 下午 9:05.
 * @des ${TODO}
 */
public class MainOrderListViewAdapter extends BaseAdapter {

    private List<OrderBean> orderBeanList;

    public MainOrderListViewAdapter(List<OrderBean> orderBeanList) {
        this.orderBeanList = orderBeanList;
    }

    @Override
    public int getCount() {
        if(orderBeanList != null && orderBeanList.size() > 0) {
            return orderBeanList.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if(orderBeanList != null && orderBeanList.size() > 0) {
            return orderBeanList.get(position);
        }else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if(orderBeanList != null && orderBeanList.size() > 0) {
            return position;
        }else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null) {
            convertView = View.inflate(UIUtils.getContext(), R.layout.item_listview_mainorder, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //设置数据
        viewHolder.orderImg.setImageResource(R.mipmap.logo);
        viewHolder.userName.setText("往事随风"+position);
        viewHolder.orderDate.setText("2016-10-02");
        viewHolder.orderTime.setText("18:00:00");
        viewHolder.orderType.setText(orderBeanList.get(position).type);
        viewHolder.tradeTime.setText(orderBeanList.get(position).tradeTime);
        viewHolder.tradeMoney.setText(orderBeanList.get(position).tradeMoney);
        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.iv_itemListViewOrder_img)
        ImageView orderImg;

        @InjectView(R.id.tv_itemListViewOrder_userName)
        TextView userName;

        @InjectView(R.id.tv_itemListViewOrder_date)
        TextView orderDate;

        @InjectView(R.id.tv_itemListViewOrder_time)
        TextView orderTime;

        @InjectView(R.id.tv_itemListViewOrder_type)
        TextView orderType;

        @InjectView(R.id.tv_itemListViewOrder_tradeTime)
        TextView tradeTime;

        @InjectView(R.id.tv_itemListViewOrder_money)
        TextView tradeMoney;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
