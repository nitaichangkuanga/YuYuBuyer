package com.wushuikeji.www.yuyubuyer.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.bean.ImageInfo;
import com.wushuikeji.www.yuyubuyer.utils.InfinitePlayUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.wushuikeji.www.yuyubuyer.view.PicShowDialog;

import java.util.List;

/**
 * @author Jack_chentao
 * @time 2016/10/10 0010 上午 11:59.
 * @des ${TODO}
 */
public class ShopDetailsViewPagerAdapter extends PagerAdapter {

    private List<ImageInfo> imageInfoList;

    private InfinitePlayUtils infinitePlay;

    private Activity mActivity;

    // 图片缓存 默认 等
    private DisplayImageOptions optionsImag = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.mipmap.zanwutupian)
            .showImageOnFail(R.mipmap.zanwutupian).cacheInMemory(true).cacheOnDisk(true)
            .considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565).build();

    public ShopDetailsViewPagerAdapter(List<ImageInfo> imageInfoList, InfinitePlayUtils infinitePlay,Activity mActivity) {
        this.imageInfoList = imageInfoList;
        this.infinitePlay = infinitePlay;
        this.mActivity = mActivity;
    }

    @Override
    public int getCount() {
        if(imageInfoList != null && imageInfoList.size()>0) {
            //为了让Viepager滑到最左和最右还可以滑动
            return Integer.MAX_VALUE;
            //                return imageInfoList.size();
        }else {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        position = position % imageInfoList.size();

        View view =View.inflate(UIUtils.getContext(), R.layout.item_image_view, null);
        ImageView iv_image= (ImageView) view.findViewById(R.id.iv_image);

        ImageLoader.getInstance().displayImage(imageInfoList.get(position).getUrl(),iv_image,optionsImag);
        // 添加View
        container.addView(view);
        final int tempPosition = position;
        //每个图片的点击事件(最好通过触摸添加防止滑动viewpager触发点击事件)
        //            iv_image.setOnClickListener(new View.OnClickListener() {
        //                @Override
        //                public void onClick(View v) {
        //                    PicShowDialog dialog = new PicShowDialog(ShopDetailsActivity.this,imageInfoList,tempPosition);
        //                    dialog.show();
        //                }
        //            });
        iv_image.setOnTouchListener(new View.OnTouchListener() {
            private float downX;
            private float downY;
            private long downTime;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN://按下停止无限播放
                        downX = event.getX();
                        downY = event.getY();
                        downTime = System.currentTimeMillis();
                        infinitePlay.stopPlay();
                        break;
                    case MotionEvent.ACTION_CANCEL://事件取消也无限播放
                        infinitePlay.startPlay();
                        break;
                    case MotionEvent.ACTION_UP://松开继续无限播放
                        float upX = event.getX();
                        float upY = event.getY();
                        if (upX == downX && upY == downY) {
                            long upTime = System.currentTimeMillis();
                            if (upTime - downTime < 600) {
                                //点击
                                try {
                                    PicShowDialog dialog = new PicShowDialog(mActivity,imageInfoList,tempPosition);
                                    dialog.show();
                                }catch (Exception e) {
                                }
                            }
                        }
                        infinitePlay.startPlay();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //position = position % imageInfoList.size();
        container.removeView((View) object);// 从Viewpager中移除
    }
}
