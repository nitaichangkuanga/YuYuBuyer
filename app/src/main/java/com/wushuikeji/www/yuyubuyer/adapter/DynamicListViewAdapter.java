package com.wushuikeji.www.yuyubuyer.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.activity.PlayVideoActivity;
import com.wushuikeji.www.yuyubuyer.bean.jsonbean.DynamicBean;
import com.wushuikeji.www.yuyubuyer.utils.BitmapUtils;
import com.wushuikeji.www.yuyubuyer.utils.GetNormalOptionsUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Jack_chentao
 */
public class DynamicListViewAdapter extends BaseAdapter {

    private List<DynamicBean> dynamicBeanList;

    private Context mContext;

    public DynamicListViewAdapter(List<DynamicBean> dynamicBeanList, Context mContext) {
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
            convertView = View.inflate(UIUtils.getContext(), R.layout.item_listview_dynamic, null);
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


        String videoPath = dynamicBeanList.get(position).videoUrl;

        final String tempVideoPath = videoPath;

        final ViewHolder finalViewHold = viewHold;

        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取视频的缩略图（宽高设置没用，内部处理了）,耗时的方法
                final Bitmap bitmap = BitmapUtils.createVideoThumbnail(tempVideoPath, 200, 200);
                UIUtils.getMainThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (bitmap != null) {
                            finalViewHold.videoImg.setImageBitmap(bitmap);
                        } else {
                            finalViewHold.videoImg.setImageResource(R.mipmap.zanwutupian);
                        }
                    }
                });
            }
        }).start();

        //视频图片的点击事件
        viewHold.videoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到播放视频的界面
                Intent intent = new Intent(mContext, PlayVideoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("url", tempVideoPath);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    static class ViewHolder {

        @InjectView(R.id.iv_itemDy_img)
        ImageView dynamicImg;

        @InjectView(R.id.tv_itemDy_userName)
        TextView dynamicTitle;

        @InjectView(R.id.tv_itemDy_date)
        TextView data;

        @InjectView(R.id.tv_itemDy_time)
        TextView time;

        @InjectView(R.id.tv_itemDy_content)
        TextView dynamicContent;

        //视频的缩约图
        @InjectView(R.id.iv_itemDy_video)
        ImageView videoImg;

        @InjectView(R.id.iv_itemDy_reply)
        ImageView iv_itemDy_reply;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
