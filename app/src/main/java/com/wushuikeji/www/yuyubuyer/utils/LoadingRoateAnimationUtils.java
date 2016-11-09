package com.wushuikeji.www.yuyubuyer.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wushuikeji.www.yuyubuyer.R;

/**
 * @author Jack_chentao
 * @time 2016/10/12 0012 下午 3:54.
 * @des ${TODO}
 */
public class LoadingRoateAnimationUtils {
    //    public static void loadingRoateAnimation(ImageView imageView, Context context) {
    //        try {
    //            // 16 = android.os.Build.VERSION_CODES.JELLY_BEAN
    //            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
    //                imageView.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.mipmap.loading_pic));
    //            } else {
    //                imageView.setBackground(UIUtils.getResources().getDrawable(R.mipmap.loading_pic));
    //            }
    //            // 加载旋转动画
    //           Animation mRoateAnimation = AnimationUtils.loadAnimation(context, R.anim.animation);
    //            // 使用ImageView显示动画
    //            imageView.startAnimation(mRoateAnimation);
    //        }catch(Exception e) {
    //        }
    //    }
    public static void loadingGifPicture(ImageView imageView, Context context) {
        Glide.with(context).load(R.drawable.loading).into(imageView);
    }
}
