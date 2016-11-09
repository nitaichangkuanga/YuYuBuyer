package com.wushuikeji.www.yuyubuyer.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.EdgeEffectCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.MainViewPagerFragmentAdapter;
import com.wushuikeji.www.yuyubuyer.base.BaseApplication;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.service.LocationService;
import com.wushuikeji.www.yuyubuyer.utils.CheckLocationIsSuccessUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.PermissionsCheckerUtils;
import com.wushuikeji.www.yuyubuyer.utils.ShowPermissionDialogUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToNextActivityUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.wushuikeji.www.yuyubuyer.view.ViewPagerWhichView;

import java.lang.reflect.Field;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends FragmentActivity {

    @InjectView(R.id.vp_main_pages)
    ViewPagerWhichView vp_main_pages;

    @InjectView(R.id.ll_main_shop)
    LinearLayout ll_main_shop;

    @InjectView(R.id.ll_main_buyer)
    LinearLayout ll_main_buyer;

    @InjectView(R.id.ll_main_news)
    LinearLayout ll_main_news;

    @InjectView(R.id.ll_main_orders)
    LinearLayout ll_main_orders;

    @InjectView(R.id.ll_main_personal)
    LinearLayout ll_main_personal;

    @InjectView(R.id.ll_main_all)
    LinearLayout ll_main_all;

    @InjectView(R.id.iv_main_shopImg)
    ImageView iv_main_shopImg;

    @InjectView(R.id.tv_main_shopName)
    TextView tv_main_shopName;

    @InjectView(R.id.iv_main_buyerImg)
    ImageView iv_main_buyerImg;

    @InjectView(R.id.tv_main_buyerImg)
    TextView tv_main_buyerImg;

    @InjectView(R.id.iv_main_newsImg)
    ImageView iv_main_newsImg;

    @InjectView(R.id.tv_main_newsImg)
    TextView tv_main_newsImg;

    @InjectView(R.id.iv_main_orderImg)
    ImageView iv_main_orderImg;

    @InjectView(R.id.tv_main_orderImg)
    TextView tv_main_orderImg;

    @InjectView(R.id.iv_main_personalImg)
    ImageView iv_main_personalImg;

    @InjectView(R.id.tv_main_personalImg)
    TextView tv_main_personalImg;

    public static MainActivity mMainActivity;

    private int selectIndex;
    private int mFragmentIndex;

    private MainViewPagerFragmentAdapter mMainViewPagerFragmentAdapter;

    private FragmentManager mFragmentManager;

    // 定义一个变量，来标识是否退出
    private boolean isExit = false;
    //百度定位
    private LocationService locationService;
    private PermissionsCheckerUtils mPermissionsCheckerUtils;
    //实现viewpager第一页和最后一页不会出现系统自带的滑动渐变颜色
    private EdgeEffectCompat leftEdge;
    private EdgeEffectCompat rightEdge;
    private String mCityName;
    private String mBusinessName;
    private SharedPreferences mSharedPreferences;
    private boolean isRequireCheck; // 是否需要请求系统权限检测
    private static final int PERMISSION_REQUEST_CODE = 0; //统权限返回码
    //危险权限（运行时权限）
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private String mArea_id;
    private boolean mIsLoginStatus;
    private boolean mIsNeedRefreshShopFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkIsHasSD();
        initView();
        initData();
        initEvent();

    }

    private void checkIsHasSD() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(),"请先安装SD卡");
            finish();
        }
    }

    private void initView() {
        ButterKnife.inject(this);
        //需要请求权限
        isRequireCheck = true;

        mMainActivity = this;

        //得到是从哪个fragment过来的
        Intent intent = getIntent();
        mFragmentIndex = intent.getIntExtra("fragmentIndex", -1);
        //保存一下fragmentIndex，为了让用户如果点击进来了，就不让它自己1秒后再次自动划进来
        //        SpUtils.putSpInt(UIUtils.getContext(),MyConstants.FRAGMENTINDEX,mFragmentIndex);
        //得到城市名和商圈名
        mCityName = intent.getStringExtra("cityName");
        mBusinessName = intent.getStringExtra("businessName");
        //得到商圈的id
        mArea_id = intent.getStringExtra("area_id");
        //从向导界面进来的
        //mIsNeedRefreshShopFragment = intent.getBooleanExtra("isNeedRefreshShopFragment",false);


        if (!TextUtils.isEmpty(mCityName)) {
            //保存城市名,因为再次进来需要显示
            SpUtils.putSpString(UIUtils.getContext(), "finalCityName", mCityName);
        }
        if (!TextUtils.isEmpty(mBusinessName)) {
            //保存城市名和商圈名,因为再次进来需要显示
            SpUtils.putSpString(UIUtils.getContext(), "finalBusinessName", mBusinessName);
        }
        if (!TextUtils.isEmpty(mArea_id)) {
            //保存id,因为再次进来需要显示
            SpUtils.putSpString(UIUtils.getContext(), "finalBusinessId", mArea_id);
        }
        //为了获取定位的权限
        mPermissionsCheckerUtils = ((BaseApplication) UIUtils.getContext()).mPermissionsCheckerUtils;
        //只会定位一次（百度定位）一开始进来就先定位一次,因为需要搜索上次展示的信息
        locationService = ((BaseApplication) UIUtils.getContext()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();

        //为了清除城市缓存
        mSharedPreferences = getSharedPreferences("cacheCityName", Context.MODE_PRIVATE);
        try {
            Field leftEdgeField = vp_main_pages.getClass().getDeclaredField("mLeftEdge");
            Field rightEdgeField = vp_main_pages.getClass().getDeclaredField("mRightEdge");
            if (leftEdgeField != null && rightEdgeField != null) {
                leftEdgeField.setAccessible(true);
                rightEdgeField.setAccessible(true);
                leftEdge = (EdgeEffectCompat) leftEdgeField.get(vp_main_pages);
                rightEdge = (EdgeEffectCompat) rightEdgeField.get(vp_main_pages);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mIsLoginStatus = SpUtils.getCacheBoolean(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.ISLOGINSTATUS, false);
    }


    /**
     * 定位结果回调，重写onReceiveLocation方法
     */
    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {

                // 经度
                double longitude = location.getLongitude();
                // 纬度
                double latitude = location.getLatitude();
                //sdk6.0以下才进行判断，6.0的代码已经处理了权限问题
                if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请检查网络是否通畅");
                    return;
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                    CheckLocationIsSuccessUtils.checkLocationIsSuccess(String.valueOf(longitude), String.valueOf(latitude), MainActivity.this, getPackageName());
                }

                //保存起来,用于碎片的读取位置使用
                SpUtils.putSpString(UIUtils.getContext(), MyConstants.LONGITUDE, String.valueOf(longitude));
                SpUtils.putSpString(UIUtils.getContext(), MyConstants.LATITUDE, String.valueOf(latitude));

                //刷新shopFragment
                //                if(mIsNeedRefreshShopFragment) {
                //                    //只会安装的时候执行一次
                //                    if(ShopFragment.mShopFragment != null) {
                //                        ShopFragment.mShopFragment.refreshShopFragmentData();
                //                    }
                //                    if(BuyerFragment.mBuyerFragment != null) {
                //                        BuyerFragment.mBuyerFragment.refreshBuyerFragmentData();
                //                    }
                //                }

                //ToastUtils.showToastInUIThread(UIUtils.getContext(), location.getLocType() + "纬度=" + latitude + "\n经度=" + longitude);
            }
        }
    };

    /**
     * Stop location service
     */
    @Override
    public void onStop() {
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    private void initData() {
        mFragmentManager = getSupportFragmentManager();
        //用来区分有多少个fragment和tab
        int ll_main_allCounts = ll_main_all.getChildCount();
        //装载数据
        mMainViewPagerFragmentAdapter = new MainViewPagerFragmentAdapter(mFragmentManager, ll_main_allCounts, mCityName, mBusinessName, mArea_id);
        vp_main_pages.setAdapter(mMainViewPagerFragmentAdapter);

        //设置从别的Activity过来之后显示哪个Fragment
        if (mFragmentIndex == 0) {
            switchPage(0);
            changeTabColor(0);
        } else if (mFragmentIndex == 1) {
            switchPage(1);
            changeTabColor(1);
        }

        //iv_main_newsImg.setNum(10);

        //vp_main_pages.setOffscreenPageLimit(2);
        //设置默认选择商铺
        //switchPage(0);
        //设置默认选择商铺，颜色变化
        //changeTabColor(0);
    }

    private void initEvent() {
        //ViewPager侧滑
        vp_main_pages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                //让tab按钮变色
                changeTabColor(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                try {
                    //让滑到第一和最后，屏蔽系统的渐变滑动颜色
                    if (leftEdge != null && rightEdge != null) {
                        leftEdge.finish();
                        rightEdge.finish();
                        leftEdge.setSize(0, 0);
                        rightEdge.setSize(0, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //tab点击
        ll_main_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变颜色
                changeTabColor(0);
                switchPage(0);
            }
        });

        ll_main_buyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变颜色
                changeTabColor(1);
                switchPage(1);
            }
        });

        ll_main_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看用户是否登陆过
                if (SpUtils.getCacheBoolean(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.ISLOGINSTATUS, false)) {
                    //改变颜色
                    changeTabColor(2);
                    switchPage(2);
                } else {
                    ToastUtils.showToastInUIThread(MainActivity.this, MyConstants.LOGIN);
                    ToNextActivityUtils.toNextAndNoFinishActivity(MainActivity.this, LoginActivity.class);
                }
            }
        });
        ll_main_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SpUtils.getCacheBoolean(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.ISLOGINSTATUS, false)) {
                    //改变颜色
                    changeTabColor(3);
                    switchPage(3);
                } else {
                    ToastUtils.showToastInUIThread(MainActivity.this, MyConstants.LOGIN);
                    ToNextActivityUtils.toNextAndNoFinishActivity(MainActivity.this, LoginActivity.class);
                }
            }
        });
        ll_main_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变颜色
                changeTabColor(4);
                switchPage(4);
            }
        });
    }

    /**
     * 统一改变颜色的方法
     */
    private void changeTabColor(int position) {
        switch (position) {
            case 0:
                //改变颜色
                changeShopColor();
                break;
            case 1:
                changeBuyerColor();
                break;
            case 2:
                if (SpUtils.getCacheBoolean(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.ISLOGINSTATUS, false)) {
                    changeNewsColor();
                } else {
                    switchPage(1);
                    changeBuyerColor();
                    ToastUtils.showToastInUIThread(MainActivity.this, MyConstants.LOGIN);
                    ToNextActivityUtils.toNextAndNoFinishActivity(MainActivity.this, LoginActivity.class);
                }
                break;
            case 3:
                if (SpUtils.getCacheBoolean(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.ISLOGINSTATUS, false)) {
                    changeOrderColor();
                } else {
                    switchPage(4);
                    changePersonalColor();
                    ToastUtils.showToastInUIThread(MainActivity.this, MyConstants.LOGIN);
                    ToNextActivityUtils.toNextAndNoFinishActivity(MainActivity.this, LoginActivity.class);
                }
                break;
            case 4:
                changePersonalColor();
                break;
            default:
                break;
        }
    }

    /**
     * 改变个人的颜色
     */
    private void changePersonalColor() {
        iv_main_shopImg.setImageResource(R.mipmap.shop);
        tv_main_shopName.setTextColor(Color.parseColor("#D3D3D3"));

        iv_main_buyerImg.setImageResource(R.mipmap.buyer);
        tv_main_buyerImg.setTextColor(Color.parseColor("#D3D3D3"));

        iv_main_newsImg.setImageResource(R.mipmap.news);
        tv_main_newsImg.setTextColor(Color.parseColor("#D3D3D3"));

        iv_main_orderImg.setImageResource(R.mipmap.orders);
        tv_main_orderImg.setTextColor(Color.parseColor("#D3D3D3"));

        iv_main_personalImg.setImageResource(R.mipmap.personal_pre);
        tv_main_personalImg.setTextColor(Color.parseColor("#B40808"));
    }

    /**
     * 改变Order的颜色
     */
    private void changeOrderColor() {
        iv_main_shopImg.setImageResource(R.mipmap.shop);
        tv_main_shopName.setTextColor(Color.parseColor("#D3D3D3"));

        iv_main_buyerImg.setImageResource(R.mipmap.buyer);
        tv_main_buyerImg.setTextColor(Color.parseColor("#D3D3D3"));

        iv_main_newsImg.setImageResource(R.mipmap.news);
        tv_main_newsImg.setTextColor(Color.parseColor("#D3D3D3"));

        iv_main_orderImg.setImageResource(R.mipmap.orders_pre);
        tv_main_orderImg.setTextColor(Color.parseColor("#B40808"));

        iv_main_personalImg.setImageResource(R.mipmap.personal);
        tv_main_personalImg.setTextColor(Color.parseColor("#D3D3D3"));
    }

    /**
     * 改变消息的颜色
     */
    private void changeNewsColor() {
        iv_main_shopImg.setImageResource(R.mipmap.shop);
        tv_main_shopName.setTextColor(Color.parseColor("#D3D3D3"));

        iv_main_buyerImg.setImageResource(R.mipmap.buyer);
        tv_main_buyerImg.setTextColor(Color.parseColor("#D3D3D3"));

        iv_main_newsImg.setImageResource(R.mipmap.news_pre);
        tv_main_newsImg.setTextColor(Color.parseColor("#B40808"));

        iv_main_orderImg.setImageResource(R.mipmap.orders);
        tv_main_orderImg.setTextColor(Color.parseColor("#D3D3D3"));

        iv_main_personalImg.setImageResource(R.mipmap.personal);
        tv_main_personalImg.setTextColor(Color.parseColor("#D3D3D3"));
    }

    /**
     * 改变BUYER的颜色
     */
    private void changeBuyerColor() {
        iv_main_shopImg.setImageResource(R.mipmap.shop);
        tv_main_shopName.setTextColor(Color.parseColor("#D3D3D3"));

        iv_main_buyerImg.setImageResource(R.mipmap.buyer_pre);
        tv_main_buyerImg.setTextColor(Color.parseColor("#B40808"));

        iv_main_newsImg.setImageResource(R.mipmap.news);
        tv_main_newsImg.setTextColor(Color.parseColor("#D3D3D3"));

        iv_main_orderImg.setImageResource(R.mipmap.orders);
        tv_main_orderImg.setTextColor(Color.parseColor("#D3D3D3"));

        iv_main_personalImg.setImageResource(R.mipmap.personal);
        tv_main_personalImg.setTextColor(Color.parseColor("#D3D3D3"));
    }

    /**
     * 改变shop颜色
     */
    private void changeShopColor() {
        iv_main_shopImg.setImageResource(R.mipmap.shop_pre);
        tv_main_shopName.setTextColor(Color.parseColor("#B40808"));

        iv_main_buyerImg.setImageResource(R.mipmap.buyer);
        tv_main_buyerImg.setTextColor(Color.parseColor("#D3D3D3"));

        iv_main_newsImg.setImageResource(R.mipmap.news);
        tv_main_newsImg.setTextColor(Color.parseColor("#D3D3D3"));

        iv_main_orderImg.setImageResource(R.mipmap.orders);
        tv_main_orderImg.setTextColor(Color.parseColor("#D3D3D3"));

        iv_main_personalImg.setImageResource(R.mipmap.personal);
        tv_main_personalImg.setTextColor(Color.parseColor("#D3D3D3"));
    }

    /**
     * 切换viewPager
     */
    private void switchPage(int index) {
        vp_main_pages.setCurrentItem(index);//设置viewpager显示页面
    }


    // 对键盘的监听事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 退出的方法
    private void exit() {
        if (!isExit) {
            isExit = true;
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "再按一次退出应用");
            // 利用handler延迟发送更改状态信息
            UIUtils.getMainThreadHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            //退出之前，清空保存城市的缓存，因为不清除，缓存都存在，那么城市信息永远得不到刷新
            mSharedPreferences.edit().clear().commit();
            this.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isRequireCheck) {
            //权限没有授权，进入授权界面.6.0的手机才会起作用
            if (mPermissionsCheckerUtils.judgePermissions(PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
            }
        } else {
            isRequireCheck = true;
        }
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            isRequireCheck = true;
        } else {
            isRequireCheck = false;
            ShowPermissionDialogUtils.showPermissionDialogUtils(this, "读取位置信息权限", getPackageName());
        }
    }

    // 含有全部的权限
    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
}
