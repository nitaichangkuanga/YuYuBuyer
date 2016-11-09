package com.wushuikeji.www.yuyubuyer.utils;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.ShopDetailsViewPagerAdapter;
import com.wushuikeji.www.yuyubuyer.bean.ImageInfo;

import java.util.List;

/**
 * @author Jack_chentao
 * @time 2016/10/10 0010 下午 12:26.
 * @des ${TODO}
 */
public class OperateViewpagerUtils {

    public static void operateViewpager(Activity activity,ViewPager mViewPager, LinearLayout linearLayout, String[] imgUrlsArray, List<ImageInfo> viewPagerList, InfinitePlayUtils mInfinitePlay) {
        //加之前清空
        viewPagerList.clear();
        linearLayout.removeAllViews();
        // 图片的数据
        // 给界面添加图片
        for (int i = 0; i < imgUrlsArray.length; i++) {
            ImageInfo imageInfo = new ImageInfo(imgUrlsArray[i],200,200);
            // 添加界面的数据
            viewPagerList.add(imageInfo);
            // 给点的容器Linearlayout初始化添加灰色点
            ImageView mV_point = new ImageView(UIUtils.getContext());
            mV_point.setBackgroundResource(R.drawable.point_selector);
            int dp = 10;
            // 设置灰色点的大小
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);// 注意单位是px 不是dp

            // 设置点与点直接的空隙

            // 第一个点不需要指定
            params.leftMargin = DensityUtil.dip2px(UIUtils.getContext(),10);

            if(i != 0) {
                mV_point.setEnabled(false);
            }

            mV_point.setLayoutParams(params);

            // 添加灰色的点到线性布局中
            linearLayout.addView(mV_point);
        }
        // 创建ViewPager的适配器
        ShopDetailsViewPagerAdapter mGuideViewPagerAdapter = new ShopDetailsViewPagerAdapter(viewPagerList,mInfinitePlay,activity);

        // 设置适配器
        mViewPager.setAdapter(mGuideViewPagerAdapter);
        //默认从第n个position选中
        int middle = Integer.MAX_VALUE / 2;
        int extra = middle % viewPagerList.size();
        int startItem = middle - extra;
        mViewPager.setCurrentItem(startItem);

        //开启无限播放
        mInfinitePlay.startPlay();
    }
}
