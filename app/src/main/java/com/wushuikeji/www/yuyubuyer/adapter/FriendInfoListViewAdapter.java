package com.wushuikeji.www.yuyubuyer.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.bean.SystemInfoBean;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Jack_chentao
 * @time 2016/10/2 0002 下午 11:25.
 * @des ${TODO}
 */
public class FriendInfoListViewAdapter extends BaseAdapter{

    private List<SystemInfoBean> systemInfoList;


    public FriendInfoListViewAdapter(List<SystemInfoBean> systemInfoList) {
        this.systemInfoList = systemInfoList;
    }

    @Override
    public int getCount() {
        if(systemInfoList != null && systemInfoList.size() > 0) {
            return systemInfoList.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if(systemInfoList != null && systemInfoList.size() > 0) {
            return systemInfoList.get(position);
        }else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if(systemInfoList != null && systemInfoList.size() > 0) {
            return position;
        }else {
            return 0;
        }
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     */
    public void updateListView(List<SystemInfoBean> systemInfoList){
        this.systemInfoList = systemInfoList;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHold = null;
        if(convertView == null) {
            convertView = View.inflate(UIUtils.getContext(), R.layout.item_listview_friendinfo, null);
            viewHold = new ViewHolder(convertView);
            convertView.setTag(viewHold);
        }else {
            viewHold = (ViewHolder) convertView.getTag();
        }
        //设置数据
        viewHold.sysDateAndTime.setText(systemInfoList.get(position).dateAndTime);
        viewHold.sysContent.setText(systemInfoList.get(position).content);

        return convertView;
    }

    static class ViewHolder {

        @InjectView(R.id.tv_sysytem_dateAndTime)
        TextView sysDateAndTime;

        @InjectView(R.id.tv_sysytem_content)
        TextView sysContent;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
