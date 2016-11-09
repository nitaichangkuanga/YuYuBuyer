package com.wushuikeji.www.yuyubuyer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.constants.ScreenSize;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToNextActivityUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SplashActivity extends AppCompatActivity {

    @InjectView(R.id.iv_splash_start)
    ImageView iv_splash_start;

    private Animation mScaleAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();

        initData();

        initEvent();

    }

    private void initView() {
        ButterKnife.inject(this);
    }

    /**
     * 动画的事件
     */
    private void initEvent() {
        mScaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //动画结束
                // 判断进入向导界面还是主界面
                if (SpUtils.getSpBoolean(UIUtils.getContext(), MyConstants.ISGUIDE, false)){
                    //true，设置过 ，直接进入主界面
                    //进入主界面之前，还需要判断用户是否已经设置过了自己的信息
                    boolean isSettingUserinfo = SpUtils.getCacheBoolean(MyConstants.SETTINGSPNAME, UIUtils.getContext(), MyConstants.ISSETTINGUSERINFO, false);
                    //查看用户是否登陆过
                    boolean isLoginBoolean = SpUtils.getCacheBoolean(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.ISLOGINSTATUS, false);

                    if(isLoginBoolean && !isSettingUserinfo){
                        //进入编辑信息界面
                        ToNextActivityUtils.toNextActivity(SplashActivity.this,RealEditActivity.class);
                    }else {
                        ToNextActivityUtils.toNextActivityAndParameters(SplashActivity.this,MainActivity.class,0);
                    }
                } else {
                    //false 没设置过，进入设置向导界面
                    ToNextActivityUtils.toNextActivity(SplashActivity.this,GuideActivity.class);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void initData() {
        mScaleAnimation = new ScaleAnimation(1.0f, 1.3f, 1.0f, 1.3f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        mScaleAnimation.setDuration(1500);
        mScaleAnimation.setRepeatCount(1);
        mScaleAnimation.setRepeatMode(Animation.REVERSE);
        //mScaleAnimation = AnimationUtils.loadAnimation(UIUtils.getContext(), R.anim.scale_anim);
        iv_splash_start.startAnimation(mScaleAnimation);

        // 获取手机的屏幕
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ScreenSize.displayWidth = displayMetrics.widthPixels;
        ScreenSize.displayHeight = displayMetrics.heightPixels;
    }
}
