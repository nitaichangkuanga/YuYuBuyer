package com.wushuikeji.www.yuyubuyer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.bean.ChatBean;
import com.wushuikeji.www.yuyubuyer.utils.GetNormalOptionsUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;

import java.util.List;

/**
 * @author Jack_chentao
 */
public class ChatListViewAdapter extends BaseAdapter {

    private List<ChatBean> chatBeanList;

    public ChatListViewAdapter(List<ChatBean> chatBeanList) {
        this.chatBeanList = chatBeanList;
    }

    @Override
    public int getCount() {
        if(chatBeanList != null && chatBeanList.size() > 0) {
            return chatBeanList.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if(chatBeanList != null && chatBeanList.size() > 0) {
            return chatBeanList.get(position);
        }else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if(chatBeanList != null && chatBeanList.size() > 0) {
            return position;
        }else {
            return 0;
        }
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount() + 1;// 2
    }

    @Override
    public int getItemViewType(int position) {
        //TODO 得到message的类型  收到的还是发送的
        int ret = 0;
        ChatBean ChatMessage = chatBeanList.get(position);
        String to = ChatMessage.getTo();
        if (to.equals("jack")) {
            //别人发送给我的消息(消息实体 to 是jack)
            ret = 0;
        } else {
            ret = 1;
        }

        return ret;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ret = null;
        ChatBean message = chatBeanList.get(position);
        String to = message.getTo();
        if (to.equals("jack")) {
            //发向自己的信息(接受消息)
            if (convertView != null) {
                ret = convertView;
            } else {
                ret =  LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.layout_left,null);
            }

            LeftHolder holder = (LeftHolder) ret.getTag();
            if (holder == null) {
                holder = new LeftHolder();
                holder.left_headImg = (ImageView) ret.findViewById(R.id.iv_chat_leftHeadImg);
                holder.left_content = (TextView) ret.findViewById(R.id.tv_chat_leftContent);
                ret.setTag(holder);
            }
            //设置left数据
            //设置头像
            ImageLoader.getInstance().displayImage(message.getIconUrl(),holder.left_headImg, GetNormalOptionsUtils.getNormalOptionsUtils());
            holder.left_content.setText(message.getContent());
        } else {
            //发向对方的信息
            if (convertView != null) {
                ret = convertView;
            } else {
                ret =  LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.layout_right,null);
            }

            RightHolder holder = (RightHolder) ret.getTag();
            if (holder == null) {
                holder = new RightHolder();
                holder.right_headImg = (ImageView) ret.findViewById(R.id.iv_chat_rightHeadImg);
                holder.right_content = (TextView) ret.findViewById(R.id.iv_chat_rightContent);
                ret.setTag(holder);
            }
            //设置right数据
            ImageLoader.getInstance().displayImage(message.getIconUrl(),holder.right_headImg, GetNormalOptionsUtils.getNormalOptionsUtils());
            holder.right_content.setText(message.getContent());
        }
        return ret;
    }

    private class LeftHolder {
        ImageView left_headImg;
        TextView left_content;
    }

    private class RightHolder {
        ImageView right_headImg;
        TextView right_content;
    }

}
