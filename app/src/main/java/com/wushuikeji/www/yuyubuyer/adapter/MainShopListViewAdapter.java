package com.wushuikeji.www.yuyubuyer.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.bean.ShopBean;
import com.wushuikeji.www.yuyubuyer.utils.GetNormalOptionsUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Jack_chentao
 * @time 2016/9/27 0027 上午 10:31.
 * @des ${TODO}
 */
public class MainShopListViewAdapter extends BaseAdapter {

    private List<ShopBean> shopBeanList;

    public MainShopListViewAdapter(List<ShopBean> shopBeanList) {
        this.shopBeanList = shopBeanList;
    }

    @Override
    public int getCount() {
        if(shopBeanList != null && shopBeanList.size() > 0) {
            return shopBeanList.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if(shopBeanList != null && shopBeanList.size() > 0) {
            return shopBeanList.get(position);
        }else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if(shopBeanList != null && shopBeanList.size() > 0) {
            return position;
        }else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHold = null;
        if(convertView == null) {
            convertView = View.inflate(UIUtils.getContext(), R.layout.item_listview_mainshop, null);
            viewHold = new ViewHolder(convertView);
            convertView.setTag(viewHold);
        }else {
            viewHold = (ViewHolder) convertView.getTag();
        }
        //设置数据
        viewHold.shopName.setText(shopBeanList.get(position).shopName);
        viewHold.shopAddress.setText(shopBeanList.get(position).shopAddress);

        String discount = shopBeanList.get(position).discount;
        if(!TextUtils.isEmpty(discount)) {
            viewHold.zheTextView.setVisibility(View.VISIBLE);
            viewHold.shopDiscount.setText(discount);
        }else {
            viewHold.zheTextView.setVisibility(View.GONE);
            //怕后台没有这个字段
            viewHold.shopDiscount.setText("暂无折扣");
        }
        //原来单位:米
        //后来Km
        if(!TextUtils.isEmpty(shopBeanList.get(position).distance)) {
            if(Double.parseDouble(shopBeanList.get(position).distance) > 1000) {
                double disance = Math.round(Double.parseDouble(shopBeanList.get(position).distance) / 100d) / 10d;
                viewHold.shopDistance.setText(disance + "km");
            }else {
                viewHold.shopDistance.setText(shopBeanList.get(position).distance + "m");
            }
        }else {
            //默认1km
            viewHold.shopDistance.setText("1000m");
        }

        //设置图片
        ImageLoader.getInstance().displayImage(shopBeanList.get(position).imgUrl,viewHold.img, GetNormalOptionsUtils.getNormalOptionsUtils());

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.iv_itemListViewShop_img)
        ImageView img;

        @InjectView(R.id.tv_itemListViewShop_name)
        TextView shopName;

        @InjectView(R.id.tv_iv_itemListViewShop_address)
        TextView shopAddress;

        @InjectView(R.id.tv_iv_itemListViewShop_num)
        TextView shopDiscount;

        @InjectView(R.id.tv_iv_itemListViewShop_zhe)
        TextView zheTextView;

        @InjectView(R.id.tv_itemListViewShop_distanceNum)
        TextView shopDistance;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
