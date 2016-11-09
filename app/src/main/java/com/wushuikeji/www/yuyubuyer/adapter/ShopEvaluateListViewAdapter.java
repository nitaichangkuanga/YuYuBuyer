package com.wushuikeji.www.yuyubuyer.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.bean.ShopEvaluateBean;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Jack_chentao
 * @time 2016/9/27 0027 上午 10:31.
 * @des ${TODO}
 */
public class ShopEvaluateListViewAdapter extends BaseAdapter {

    private List<ShopEvaluateBean> shopEvaluateBeanList;

    public ShopEvaluateListViewAdapter(List<ShopEvaluateBean> shopEvaluateBeanList) {
        this.shopEvaluateBeanList = shopEvaluateBeanList;
    }

    @Override
    public int getCount() {
        if(shopEvaluateBeanList != null && shopEvaluateBeanList.size() > 0) {
            return shopEvaluateBeanList.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if(shopEvaluateBeanList != null && shopEvaluateBeanList.size() > 0) {
            return shopEvaluateBeanList.get(position);
        }else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if(shopEvaluateBeanList != null && shopEvaluateBeanList.size() > 0) {
            return position;
        }else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHold = null;
        if(convertView == null) {
            convertView = View.inflate(UIUtils.getContext(), R.layout.item_listview_shopevaluate, null);
            viewHold = new ViewHolder(convertView);
            convertView.setTag(viewHold);
        }else {
            viewHold = (ViewHolder) convertView.getTag();
        }
        //设置数据
        viewHold.userName.setText(shopEvaluateBeanList.get(position).userName);
        int heartCount = shopEvaluateBeanList.get(position).count;
        if(heartCount == 1) {
            viewHold.heartOne.setVisibility(View.VISIBLE);
            viewHold.heartTwo.setVisibility(View.INVISIBLE);
            viewHold.heartThree.setVisibility(View.INVISIBLE);
            viewHold.heartFour.setVisibility(View.INVISIBLE);
            viewHold.heartFive.setVisibility(View.INVISIBLE);
        }else if(heartCount == 2) {
            viewHold.heartOne.setVisibility(View.VISIBLE);
            viewHold.heartTwo.setVisibility(View.VISIBLE);
            viewHold.heartThree.setVisibility(View.INVISIBLE);
            viewHold.heartFour.setVisibility(View.INVISIBLE);
            viewHold.heartFive.setVisibility(View.INVISIBLE);
        }else if(heartCount == 3) {
            viewHold.heartOne.setVisibility(View.VISIBLE);
            viewHold.heartTwo.setVisibility(View.VISIBLE);
            viewHold.heartThree.setVisibility(View.VISIBLE);
            viewHold.heartFour.setVisibility(View.INVISIBLE);
            viewHold.heartFive.setVisibility(View.INVISIBLE);
        }else if(heartCount == 4) {
            viewHold.heartOne.setVisibility(View.VISIBLE);
            viewHold.heartTwo.setVisibility(View.VISIBLE);
            viewHold.heartThree.setVisibility(View.VISIBLE);
            viewHold.heartFour.setVisibility(View.VISIBLE);
            viewHold.heartFive.setVisibility(View.INVISIBLE);
        }else if(heartCount == 5) {
            viewHold.heartOne.setVisibility(View.VISIBLE);
            viewHold.heartTwo.setVisibility(View.VISIBLE);
            viewHold.heartThree.setVisibility(View.VISIBLE);
            viewHold.heartFour.setVisibility(View.VISIBLE);
            viewHold.heartFive.setVisibility(View.VISIBLE);
        }else {
            viewHold.heartOne.setVisibility(View.INVISIBLE);
            viewHold.heartTwo.setVisibility(View.INVISIBLE);
            viewHold.heartThree.setVisibility(View.INVISIBLE);
            viewHold.heartFour.setVisibility(View.INVISIBLE);
            viewHold.heartFive.setVisibility(View.INVISIBLE);
        }
        viewHold.evaluateContent.setText(shopEvaluateBeanList.get(position).evaluateContent);
        viewHold.date.setText(shopEvaluateBeanList.get(position).date);
        viewHold.time.setText(shopEvaluateBeanList.get(position).time);
        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.tv_itemEvaluate_name)
        TextView userName;

        @InjectView(R.id.iv_itemEvaluate_heardOne)
        ImageView heartOne;

        @InjectView(R.id.iv_itemEvaluate_heardTwo)
        ImageView heartTwo;

        @InjectView(R.id.iv_itemEvaluate_heardThree)
        ImageView heartThree;

        @InjectView(R.id.iv_itemEvaluate_heardFour)
        ImageView heartFour;

        @InjectView(R.id.iv_itemEvaluate_heardFive)
        ImageView heartFive;

        @InjectView(R.id.tv_itemEvaluate_evaluate)
        TextView evaluateContent;

        @InjectView(R.id.tv_itemEvaluate_date)
        TextView date;

        @InjectView(R.id.tv_itemEvaluate_time)
        TextView time;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
