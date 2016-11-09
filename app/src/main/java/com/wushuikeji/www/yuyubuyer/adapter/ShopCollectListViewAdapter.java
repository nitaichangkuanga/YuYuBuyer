package com.wushuikeji.www.yuyubuyer.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.bean.ShopCollectBean;
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
public class ShopCollectListViewAdapter extends BaseAdapter {

    private List<ShopCollectBean> shopCollectBeanList;

    public ShopCollectListViewAdapter(List<ShopCollectBean> shopCollectBeanList) {
        this.shopCollectBeanList = shopCollectBeanList;
    }

    @Override
    public int getCount() {
        if(shopCollectBeanList != null && shopCollectBeanList.size() > 0) {
            return shopCollectBeanList.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if(shopCollectBeanList != null && shopCollectBeanList.size() > 0) {
            return shopCollectBeanList.get(position);
        }else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if(shopCollectBeanList != null && shopCollectBeanList.size() > 0) {
            return position;
        }else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHold = null;
        if(convertView == null) {
            convertView = View.inflate(UIUtils.getContext(), R.layout.item_listview_shopcollect, null);
            viewHold = new ViewHolder(convertView);
            convertView.setTag(viewHold);
        }else {
            viewHold = (ViewHolder) convertView.getTag();
        }
        //设置数据
        viewHold.shopName.setText(shopCollectBeanList.get(position).shopName);
        viewHold.shopAddress.setText(shopCollectBeanList.get(position).shopAddress);
        viewHold.shopAddress.setText(shopCollectBeanList.get(position).shopAddress);
        viewHold.shopBusiness.setText(shopCollectBeanList.get(position).trade_name);
        //加载图片
        ImageLoader.getInstance().displayImage(shopCollectBeanList.get(position).imgUrl,viewHold.img, GetNormalOptionsUtils.getNormalOptionsUtils());
        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.iv_shopCollectF_img)
        ImageView img;

        @InjectView(R.id.tv_shopCollectF_name)
        TextView shopName;

        @InjectView(R.id.tv_shopCollectF_address)
        TextView shopAddress;

        @InjectView(R.id.tv_shopCollect_business)
        TextView shopBusiness;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
