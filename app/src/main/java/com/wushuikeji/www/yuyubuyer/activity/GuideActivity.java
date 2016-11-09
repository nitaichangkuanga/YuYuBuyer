package com.wushuikeji.www.yuyubuyer.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.GuideViewPagerAdapter;
import com.wushuikeji.www.yuyubuyer.base.BaseApplication;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.service.LocationService;
import com.wushuikeji.www.yuyubuyer.utils.DensityUtil;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToNextActivityUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GuideActivity extends AppCompatActivity {

    @InjectView(R.id.vp_guide_pages)
    ViewPager vp_guide_pages;

    @InjectView(R.id.ll_guide_points)
    LinearLayout ll_guide_points;

    @InjectView(R.id.bt_guide_input)
    Button bt_guide_input;

    private GuideViewPagerAdapter mGuideViewPagerAdapter;
    private List<ImageView> countsList;
    private View mV_point;

    //百度定位
    private LocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
        initData();
        initEvent();// 初始化组件事件
    }

    private void initView() {
        //注解，解决了find
        ButterKnife.inject(this);

        //只会定位一次（百度定位）一开始进来就先定位一次,因为需要搜索上次展示的信息
        locationService = ((BaseApplication) UIUtils.getContext()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();
    }

    private void initData() {
        // 图片的数据
        int[] picsArray = new int[] { R.mipmap.guide_1, R.mipmap.guide_2, R.mipmap.guide_3, R.mipmap.guide_4 };
        // 定义Viewpager使用的容器
        countsList = new ArrayList<ImageView>();
        // 给界面添加图片
        for (int i = 0; i < picsArray.length; i++) {
            ImageView iv_temp = new ImageView(UIUtils.getContext());
            iv_temp.setBackgroundResource(picsArray[i]);

            // 添加界面的数据
            countsList.add(iv_temp);

            // 给点的容器Linearlayout初始化添加灰色点
            mV_point = new View(UIUtils.getContext());
            mV_point.setBackgroundResource(R.drawable.point_selector);
            int dp = 10;
            // 设置灰色点的大小
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(getApplicationContext(), dp), DensityUtil.dip2px(getApplicationContext(), dp));// 注意单位是px 不是dp

            // 设置点与点直接的空隙
            // 第一个点不需要指定
            params.leftMargin = DensityUtil.dip2px(getApplicationContext(),15);
            //第一个显示红点
            if(i != 0) {
                mV_point.setEnabled(false);
            }
            mV_point.setLayoutParams(params);

            // 添加灰色的点到线性布局中
            ll_guide_points.addView(mV_point);
        }
        // 创建ViewPager的适配器
        mGuideViewPagerAdapter = new GuideViewPagerAdapter(countsList);

        // 设置适配器
        vp_guide_pages.setAdapter(mGuideViewPagerAdapter);
    }

    private void initEvent() {
        //ViewPager
        vp_guide_pages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //当前ViewPager显示的页码
                for(int i=0;i<ll_guide_points.getChildCount();i++) {
                    ll_guide_points.getChildAt(i).setEnabled(i == position);
                }
                if(position != ll_guide_points.getChildCount() - 1) {
                    bt_guide_input.setClickable(false);
                }else {
                    bt_guide_input.setClickable(true);

                    UIUtils.getMainThreadHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //到达了最后一页,1秒后自动滑进去
                            //保存经过向导界面的记录
                            SpUtils.putSpBoolean(UIUtils.getContext(), MyConstants.ISGUIDE,true);

//                            Intent intent = new Intent(GuideActivity.this,MainActivity.class);
//                            intent.putExtra("fragmentIndex",0);
//                            intent.putExtra("isNeedRefreshShopFragment",true);
//                            startActivity(intent);
//                            finish();
                            ToNextActivityUtils.toNextActivityAndParameters(GuideActivity.this,MainActivity.class,0);
//                            if(!"0".equals(SpUtils.getSpInt(UIUtils.getContext(),MyConstants.FRAGMENTINDEX,-1))) {
//                                Intent intent = new Intent(GuideActivity.this,MainActivity.class);
//                                intent.putExtra("fragmentIndex",0);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent);
//                                finish();
//                            }
                        }
                    },800);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //模拟Button进入主界面
//        bt_guide_input.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //保存经过向导界面的记录
//                SpUtils.putSpBoolean(UIUtils.getContext(), MyConstants.ISGUIDE,true);
////              ToNextActivityUtils.toNextActivityAndParameters(GuideActivity.this,MainActivity.class,0);
//
//                Intent mIntent = new Intent(GuideActivity.this,MainActivity.class);
//                mIntent.putExtra("fragmentIndex",0);
//                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(mIntent);
//                finish();
//            }
//        });
    }

    /*
     * 定位结果回调，重写onReceiveLocation方法
     */
    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {

                // 经度
                double longitude = location.getLongitude();
                // 纬度
                double latitude = location.getLatitude();
                //sdk6.0以下才进行判断，6.0的代码已经处理了权限问题
                if(location.getLocType() == BDLocation.TypeNetWorkException) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(),"请检查网络是否通畅");
                    return;
                }

                //保存起来,用于碎片的读取位置使用
                SpUtils.putSpString(UIUtils.getContext(), MyConstants.LONGITUDE, String.valueOf(longitude));
                SpUtils.putSpString(UIUtils.getContext(), MyConstants.LATITUDE, String.valueOf(latitude));

            }
        }
    };

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }
}
