package com.wushuikeji.www.yuyubuyer.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.bean.AllEvaluateBean;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Jack_chentao
 * @time 2016/9/27 0027 上午 10:31.
 * @des ${TODO}
 */
public class AllEvaluateListViewAdapter extends BaseAdapter {

    private List<AllEvaluateBean> allEvaluateBeanList;

    public AllEvaluateListViewAdapter(List<AllEvaluateBean> allEvaluateBeanList) {
        this.allEvaluateBeanList = allEvaluateBeanList;
    }

    @Override
    public int getCount() {
        if(allEvaluateBeanList != null && allEvaluateBeanList.size() > 0) {
            return allEvaluateBeanList.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if(allEvaluateBeanList != null && allEvaluateBeanList.size() > 0) {
            return allEvaluateBeanList.get(position);
        }else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if(allEvaluateBeanList != null && allEvaluateBeanList.size() > 0) {
            return position;
        }else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHold = null;
        if(convertView == null) {
            convertView = View.inflate(UIUtils.getContext(), R.layout.item_listview_allevaluate, null);
            viewHold = new ViewHolder(convertView);
            convertView.setTag(viewHold);
        }else {
            viewHold = (ViewHolder) convertView.getTag();
        }
        //设置数据
        viewHold.userName.setText(allEvaluateBeanList.get(position).userName);
        int heartCount = allEvaluateBeanList.get(position).count;
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
        viewHold.evaluateContent.setText(allEvaluateBeanList.get(position).evaluateContent);
        viewHold.date.setText(allEvaluateBeanList.get(position).date);
        viewHold.time.setText(allEvaluateBeanList.get(position).time);

        if(TextUtils.isEmpty(allEvaluateBeanList.get(position).type)) {
            viewHold.shopImg.setVisibility(View.GONE);
            viewHold.shopName.setVisibility(View.GONE);
        }else {
            viewHold.shopImg.setVisibility(View.VISIBLE);
            viewHold.shopName.setVisibility(View.VISIBLE);
            viewHold.shopImg.setBackgroundResource(Integer.parseInt(allEvaluateBeanList.get(position).type));
            viewHold.shopName.setText(allEvaluateBeanList.get(position).shopName);
        }

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.tv_itemAllEvaluate_name)
        TextView userName;

        @InjectView(R.id.iv_itemAllEvaluate_heardOne)
        ImageView heartOne;

        @InjectView(R.id.iv_itemAllEvaluate_heardTwo)
        ImageView heartTwo;

        @InjectView(R.id.iv_itemAllEvaluate_heardThree)
        ImageView heartThree;

        @InjectView(R.id.iv_itemAllEvaluate_heardFour)
        ImageView heartFour;

        @InjectView(R.id.iv_itemAllEvaluate_heardFive)
        ImageView heartFive;

        @InjectView(R.id.tv_itemAllEvaluate_evaluate)
        TextView evaluateContent;

        @InjectView(R.id.tv_itemAllEvaluate_date)
        TextView date;

        @InjectView(R.id.tv_itemAllEvaluate_time)
        TextView time;

        @InjectView(R.id.tv_itemAllEvaluate_shopImg)
        ImageView shopImg;

        @InjectView(R.id.tv_itemAllEvaluate_sname)
        TextView shopName;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
