package com.wushuikeji.www.yuyubuyer.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.bean.ShopDetailsBean;
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
public class ShopDetailsListViewAdapter extends BaseAdapter {

    private List<ShopDetailsBean> shopDetailsList;

    public ShopDetailsListViewAdapter(List<ShopDetailsBean> shopDetailsList) {
        this.shopDetailsList = shopDetailsList;
    }

    @Override
    public int getCount() {
        if(shopDetailsList != null && shopDetailsList.size() > 0) {
            return shopDetailsList.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if(shopDetailsList != null && shopDetailsList.size() > 0) {
            return shopDetailsList.get(position);
        }else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if(shopDetailsList != null && shopDetailsList.size() > 0) {
            return position;
        }else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHold = null;
        if(convertView == null) {
            convertView = View.inflate(UIUtils.getContext(), R.layout.item_listview_shopdetails, null);
            viewHold = new ViewHolder(convertView);
            convertView.setTag(viewHold);
        }else {
            viewHold = (ViewHolder) convertView.getTag();
        }
        //设置数据
        viewHold.shopDetailsTitle.setText(shopDetailsList.get(position).shopDetailsTitle);
        viewHold.shopDetailsMoney.setText(shopDetailsList.get(position).money);
        viewHold.shopDetailsDiscount.setText(shopDetailsList.get(position).discount);

        String goodType = shopDetailsList.get(position).goodType;
        if(TextUtils.isEmpty(goodType)) {
            viewHold.shopDetailsType.setVisibility(View.GONE);
        }else {
            viewHold.shopDetailsType.setVisibility(View.VISIBLE);
            viewHold.shopDetailsType.setText(goodType);
        }
        //设置图片
        ImageLoader.getInstance().displayImage(shopDetailsList.get(position).imgUrl,viewHold.shopDetailsImg, GetNormalOptionsUtils.getNormalOptionsUtils());
        return convertView;
    }

    static class ViewHolder {

        @InjectView(R.id.iv_shopDetails_shopImg)
        ImageView shopDetailsImg;

        @InjectView(R.id.tv_shopDetails_title)
        TextView shopDetailsTitle;

        @InjectView(R.id.tv_shopDetails_money)
        TextView shopDetailsMoney;

        @InjectView(R.id.tv_shopDetails_count)
        TextView shopDetailsDiscount;

        @InjectView(R.id.tv_shopDetails_type)
        TextView shopDetailsType;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
