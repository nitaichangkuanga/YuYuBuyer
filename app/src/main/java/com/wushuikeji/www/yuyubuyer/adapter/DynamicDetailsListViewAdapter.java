package com.wushuikeji.www.yuyubuyer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.bean.jsonbean.DynamicBean;
import com.wushuikeji.www.yuyubuyer.utils.GetNormalOptionsUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Jack_chentao
 */
public class DynamicDetailsListViewAdapter extends BaseAdapter {

    private List<DynamicBean> dynamicBeanList;

    private Context mContext;

    public DynamicDetailsListViewAdapter(List<DynamicBean> dynamicBeanList, Context mContext) {
        this.dynamicBeanList = dynamicBeanList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        if (dynamicBeanList != null && dynamicBeanList.size() > 0) {
            return dynamicBeanList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (dynamicBeanList != null && dynamicBeanList.size() > 0) {
            return dynamicBeanList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (dynamicBeanList != null && dynamicBeanList.size() > 0) {
            return position;
        } else {
            return 0;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHold = null;
        if (convertView == null) {
            convertView = View.inflate(UIUtils.getContext(), R.layout.item_listview_dynamicdetails, null);
            viewHold = new ViewHolder(convertView);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHolder) convertView.getTag();
        }
        //设置数据
        viewHold.dynamicTitle.setText(dynamicBeanList.get(position).userName);
        viewHold.data.setText(dynamicBeanList.get(position).date);
        viewHold.time.setText(dynamicBeanList.get(position).time);
        viewHold.dynamicContent.setText(dynamicBeanList.get(position).content);

        //设置图片
        ImageLoader.getInstance().displayImage(dynamicBeanList.get(position).imgUrl, viewHold.dynamicImg, GetNormalOptionsUtils.getNormalOptionsUtils());

        return convertView;
    }

    static class ViewHolder {

        @InjectView(R.id.iv_itemDyDetails_img)
        ImageView dynamicImg;

        @InjectView(R.id.tv_itemDyDetails_userName)
        TextView dynamicTitle;

        @InjectView(R.id.tv_itemDyDetails_date)
        TextView data;

        @InjectView(R.id.tv_itemDyDetails_time)
        TextView time;

        @InjectView(R.id.tv_itemDyDetails_content)
        TextView dynamicContent;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
