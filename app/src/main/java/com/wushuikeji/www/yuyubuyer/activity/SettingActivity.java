package com.wushuikeji.www.yuyubuyer.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.base.BaseApplication;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.global.SoundSetting;
import com.wushuikeji.www.yuyubuyer.manager.DataCleanManager;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToNextActivityUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.wushuikeji.www.yuyubuyer.view.EaseSwitchButton;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.rl_setting_back)
    RelativeLayout rl_setting_back;

    @InjectView(R.id.rl_setting_rl)
    RelativeLayout rl_setting_rl;

    @InjectView(R.id.tv_setting_loginStatus)
    TextView tv_setting_loginStatus;

    @InjectView(R.id.tv_setting_cacheSize)
    TextView tv_setting_cacheSize;

    @InjectView(R.id.tv_setting_versionName)
    TextView tv_setting_versionName;

    @InjectView(R.id.rl_setting_exit)
    RelativeLayout rl_setting_exit;

    @InjectView(R.id.rl_setting_c)
    RelativeLayout rl_setting_c;

    @InjectView(R.id.rl_setting_rlthree)
    RelativeLayout rl_setting_rlthree;

    @InjectView(R.id.rl_setting_sound)
    RelativeLayout rl_setting_sound;

    @InjectView(R.id.custom_setting_Soundsetting)
    EaseSwitchButton custom_setting_Soundsetting;

    private boolean mIsLoginStatus;

    private SharedPreferences mSharedPreferences;
    private String mTotalCacheSize;
    private PackageManager mPackageManager;

    private SoundPool soundPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initData();
        initEvent();
    }

    private void initView() {

        ButterKnife.inject(this);
        //得到包的管理者
        mPackageManager = getPackageManager();
            //播放声音
//        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
        //soundPool.load(this,R.raw.a,1);
        //soundPool.play(1,1, 1, 0, 0, 1);//倒数第两个参数-1表示循环，其他非负数表示循环次数，也就是总共播放n+1次
        //得到用户是否有登录的缓存
        mIsLoginStatus = SpUtils.getCacheBoolean(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.ISLOGINSTATUS, false);

        mSharedPreferences = getSharedPreferences(MyConstants.LOGINSPNAME, Context.MODE_PRIVATE);
        //一进来就判断是否登陆过
        if (!mIsLoginStatus) {
            //说明没登录过,那么点击就是登录,字改成登录
            tv_setting_loginStatus.setText("登录");
        }else {
            tv_setting_loginStatus.setText("退出登录");
        }
    }

    private void initData() {
        //得到app的缓存大小
        try {
            mTotalCacheSize = DataCleanManager.getTotalCacheSize(UIUtils.getContext());
            if(!TextUtils.isEmpty(mTotalCacheSize)) {
                tv_setting_cacheSize.setText(mTotalCacheSize);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //设置当前版本号
        try {
            PackageInfo packageInfo = mPackageManager.getPackageInfo(UIUtils.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            if(packageInfo != null) {
                tv_setting_versionName.setText(packageInfo.versionName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //一进来就判断是否选中声音
        if(SoundSetting.isNeedNoticationSound) {
            custom_setting_Soundsetting.openSwitch();
        }else {
            custom_setting_Soundsetting.closeSwitch();
        }
    }

    private void initEvent() {
        BackListenerUtils.backFinish(rl_setting_back, this);
        //登录或者退出
        rl_setting_exit.setOnClickListener(this);
        //清除缓存
        rl_setting_rl.setOnClickListener(this);
        //服务协议
        rl_setting_c.setOnClickListener(this);
        //成长指南
        rl_setting_rlthree.setOnClickListener(this);
        //接受新消息通知声音
        rl_setting_sound.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_setting_exit://登录或者退出登录
                if (mIsLoginStatus) {
                    //退出登录，清理登录的缓存
                    mSharedPreferences.edit().clear().commit();
                    //关闭mainactivity和自己
                    BaseApplication.getInstance().closeActivity();
                    ToNextActivityUtils.toNextActivity(SettingActivity.this,LoginActivity.class);
                    //退出环信的服务器
                     exitHuanXinServer();
                }else {
                    //跳转到登录，进行登录
                    ToNextActivityUtils.toNextAndNoFinishActivity(SettingActivity.this,LoginActivity.class);
                }
                break;
            case R.id.rl_setting_rl://清理缓存
                DataCleanManager.clearAllCache(UIUtils.getContext());
                try {
                    String clearAfterCacheSize = DataCleanManager.getTotalCacheSize(UIUtils.getContext());
                    if(!TextUtils.isEmpty(clearAfterCacheSize)) {
                        tv_setting_cacheSize.setText(clearAfterCacheSize);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.rl_setting_c: //服务协议
                ToNextActivityUtils.toNextAndNoFinishActivity(SettingActivity.this,AgreementActivity.class);
                break;
            case R.id.rl_setting_rlthree: //成长指南
                ToNextActivityUtils.toNextAndNoFinishActivity(SettingActivity.this,GrowUpActivity.class);
                break;
            case R.id.rl_setting_sound: //接受新消息通知声音
                if(SpUtils.getCacheBoolean(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.ISLOGINSTATUS, false)) {
                    if(SoundSetting.isNeedNoticationSound) {
                        custom_setting_Soundsetting.closeSwitch();
                        SoundSetting.isNeedNoticationSound = false;
                    }else {
                        custom_setting_Soundsetting.openSwitch();
                        SoundSetting.isNeedNoticationSound = true;
                    }
                }else {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(),MyConstants.LOGIN);
                    ToNextActivityUtils.toNextAndNoFinishActivity(SettingActivity.this, LoginActivity.class);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 退出环信
     */
    private void exitHuanXinServer() {
        // 调用sdk的退出登录方法，第一个参数表示是否解绑推送的token，没有使用推送或者被踢都要传false
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                //子线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        ToastUtils.showToastInChildThread(UIUtils.getContext(),"退出环信服务器成功");
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }
}
