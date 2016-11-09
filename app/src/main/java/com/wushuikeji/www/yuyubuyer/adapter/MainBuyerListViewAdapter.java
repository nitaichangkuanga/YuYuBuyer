package com.wushuikeji.www.yuyubuyer.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.bean.BuyerBean;
import com.wushuikeji.www.yuyubuyer.utils.GetNormalOptionsUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Jack_chentao
 * @time 2016/9/28 0028 下午 9:05.
 * @des ${TODO}
 */
public class MainBuyerListViewAdapter extends BaseAdapter {

    private List<BuyerBean> buyerBeanList;

    public MainBuyerListViewAdapter(List<BuyerBean> buyerBeanList) {
        this.buyerBeanList = buyerBeanList;
    }

    @Override
    public int getCount() {
        if(buyerBeanList != null && buyerBeanList.size() > 0) {
            return buyerBeanList.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if(buyerBeanList != null && buyerBeanList.size() > 0) {
            return buyerBeanList.get(position);
        }else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if(buyerBeanList != null && buyerBeanList.size() > 0) {
            return position;
        }else {
            return 0;
        }
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     */
    public void updateListView(List<BuyerBean> buyerBeanList){
        this.buyerBeanList = buyerBeanList;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHold = null;
        if(convertView == null) {
            convertView =  LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_listview_mainbuyer,null);
            viewHold = new ViewHolder(convertView);
            convertView.setTag(viewHold);
        }else {
            viewHold = (ViewHolder) convertView.getTag();
        }
        //设置数据
        viewHold.buyerName.setText(buyerBeanList.get(position).buyerName);
        viewHold.buyerAge.setText(buyerBeanList.get(position).age);
        viewHold.buyerMotto.setText(buyerBeanList.get(position).description);

//        double disance = Math.round(Double.parseDouble(buyerBeanList.get(position).distance) / 100d) / 10d;
//        viewHold.shopDistance.setText(String.valueOf(disance));

        if(!TextUtils.isEmpty(buyerBeanList.get(position).distance)) {
            if(Double.parseDouble(buyerBeanList.get(position).distance) > 1000) {
                double disance = Math.round(Double.parseDouble(buyerBeanList.get(position).distance) / 100d) / 10d;
                viewHold.shopDistance.setText(disance + "km");
            }else {
                viewHold.shopDistance.setText(buyerBeanList.get(position).distance + "m");
            }
        }else {
            //默认1km
            viewHold.shopDistance.setText("1000m");
        }


        //设置是否具有视频指导的功能is_video 1具有
        if("1".equals(buyerBeanList.get(position).is_video)) {
            viewHold.buyerIsVideo.setBackgroundResource(R.mipmap.camera);
        }else {
            viewHold.buyerIsVideo.setBackgroundResource(R.mipmap.camera_default);
        }

        //设置性别(1代表男)
        if("1".equals(buyerBeanList.get(position).sex)) {
            viewHold.buyerSex.setBackgroundResource(R.mipmap.boy);
        }else {
            viewHold.buyerSex.setBackgroundResource(R.mipmap.girl);
        }
        //设置等级图片
        String grades = buyerBeanList.get(position).grades;
        if("5".equals(grades)) {
            viewHold.buyerGrade.setBackgroundResource(R.mipmap.buyerdetails_v5);
        }else if("4".equals(grades)) {
            viewHold.buyerGrade.setBackgroundResource(R.mipmap.buyerdetails_v4);
        }else if("3".equals(grades)) {
            viewHold.buyerGrade.setBackgroundResource(R.mipmap.buyerdetails_v3);
        }else if("2".equals(grades)) {
            viewHold.buyerGrade.setBackgroundResource(R.mipmap.buyerdetails_v2);
        }else {
            viewHold.buyerGrade.setBackgroundResource(R.mipmap.buyerdetails_v1);
        }
        //设置图片
        ImageLoader.getInstance().displayImage(buyerBeanList.get(position).imgUrl,viewHold.buyerImg, GetNormalOptionsUtils.getNormalOptionsUtils());

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.iv_itemListViewBuyer_img)
        ImageView buyerImg;

        @InjectView(R.id.tv_itemListViewBuyer_buyerName)
        TextView buyerName;

        @InjectView(R.id.tv_itemListViewBuyer_age)
        TextView buyerAge;

        @InjectView(R.id.tv_iv_itemListViewBuyer_motto)
        TextView buyerMotto;

        @InjectView(R.id.tv_itemListViewBuyer_distanceNum)
        TextView shopDistance;

        //显示性别的图标
        @InjectView(R.id.iv_buyer_sex)
        ImageView buyerSex;

        //显示buyer的等级
        @InjectView(R.id.iv_mainBuyer_grade)
        ImageView buyerGrade;

        //显示视频的图片
        @InjectView(R.id.iv_buyer_xingxing)
        ImageView buyerIsVideo;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
