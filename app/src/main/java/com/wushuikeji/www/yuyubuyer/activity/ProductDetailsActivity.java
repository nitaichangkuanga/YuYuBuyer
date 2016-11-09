package com.wushuikeji.www.yuyubuyer.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.ShopDetailsViewPagerAdapter;
import com.wushuikeji.www.yuyubuyer.bean.ImageInfo;
import com.wushuikeji.www.yuyubuyer.bean.jsonbean.ImageViewBean;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.constants.ScreenSize;
import com.wushuikeji.www.yuyubuyer.fragment.GoodCollectFragment;
import com.wushuikeji.www.yuyubuyer.jsonparse.ProductDetailsImageViewParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.ProductDetailsParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.ShopDetailsIsFavoriteInfoParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.remarkParse;
import com.wushuikeji.www.yuyubuyer.utils.DensityUtil;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.InfinitePlayUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToNextActivityUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

public class ProductDetailsActivity extends AppCompatActivity {

    @InjectView(R.id.sv_productDetails_ScrollView)
    ScrollView sv_productDetails_ScrollView;

    @InjectView(R.id.pb_productDetailsLoading_progressBar)
    ProgressBar pb_productDetailsLoading_progressBar;

    //Viewpager
    @InjectView(R.id.vp_productDetails_ViewPager)
    ViewPager vp_productDetails_ViewPager;

    //装point的容器
    @InjectView(R.id.ll_productDetails_pointLinearLayout)
    LinearLayout ll_productDetails_pointLinearLayout;
    //返回
    @InjectView(R.id.rl_productDetails_back)
    RelativeLayout rl_productDetails_back;

    @InjectView(R.id.tv_productDetails_shopName)
    TextView tv_productDetails_shopName;

    @InjectView(R.id.tv_productDetails_money)
    TextView tv_productDetails_money;

    @InjectView(R.id.tv_productDetails_jia)
    TextView tv_productDetails_jia;

    @InjectView(R.id.tv_product_details_discount)
    TextView tv_product_details_discount;

    @InjectView(R.id.tv_productDetails_description)
    TextView tv_productDetails_description;

    @InjectView(R.id.tv_product_grade)
    TextView tv_product_grade;

    @InjectView(R.id.tv_product_gradeTitle)
    TextView tv_product_gradeTitle;

    @InjectView(R.id.b_productDetails_shop)
    Button b_productDetails_shop;

    @InjectView(R.id.b_productDetails_guide)
    Button b_productDetails_guide;

    @InjectView(R.id.rl_productDetails_collect)
    RelativeLayout rl_productDetails_collect;

    @InjectView(R.id.iv_productDetails_collect)
    ImageView iv_productDetails_collect;

    @InjectView(R.id.iv_productDetails_pre)
    ImageView iv_productDetails_pre;

    //传给Viewpager的
    private List<ImageInfo> viewPagerList = new ArrayList<>();

    private static final int LOADING = 0;
    private static final int SUCCESS = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_productDetailsLoading_progressBar.setVisibility(View.VISIBLE);
                    sv_productDetails_ScrollView.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    //不判断shopDetailsBeanList是否有数据，是因为没有数据时headView也必须显示
                    pb_productDetailsLoading_progressBar.setVisibility(View.GONE);
                    sv_productDetails_ScrollView.setVisibility(View.VISIBLE);
                    // 创建ViewPager的适配器
                    if (mGuideViewPagerAdapter == null) {
                        mGuideViewPagerAdapter = new ShopDetailsViewPagerAdapter(viewPagerList, mInfinitePlay, ProductDetailsActivity.this);
                        // 设置适配器
                        vp_productDetails_ViewPager.setAdapter(mGuideViewPagerAdapter);
                        //默认从第10个position选中（不放这的话，因为之前没有数据，所以不起作用）
                        vp_productDetails_ViewPager.setCurrentItem(10);
                        //开启无限播放
                        mInfinitePlay.startPlay();
                    }

                    if (mGuideViewPagerAdapter != null) {
                        mGuideViewPagerAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    //发送收藏的请求
    private String collectProductUrl = Constants.commontUrl + "favorite/product";

    private InfinitePlayUtils mInfinitePlay;
    //记录商品详情是否收藏
    private boolean isCollect;
    //商品详情
    private String productDetailstUrl = Constants.commontUrl + "product/details";
    private String mProduct_id;
    private ShopDetailsViewPagerAdapter mGuideViewPagerAdapter;
    private String mPhone_number;
    private String mBeforeArea;
    private String mAfterPhone;
    private String mUserId;
    private boolean mIsLoginBoolean;
    private boolean mIsFromGoodCollect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);

        //得到product_id
        mProduct_id = getIntent().getStringExtra("product_id");
        //判断是否从收藏过来的
        mIsFromGoodCollect = getIntent().getBooleanExtra("isFromGoodCollect",false);
        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");
        //判断用户是否登录
        mIsLoginBoolean = SpUtils.getCacheBoolean(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.ISLOGINSTATUS, false);

        //适配Viewpager的高度
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (ScreenSize.displayHeight * 0.35f + 0.5f));
        vp_productDetails_ViewPager.setLayoutParams(layoutParams);
        //去除系统自带的颜色渐变
        vp_productDetails_ViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        //无限播放
        mInfinitePlay = new InfinitePlayUtils(vp_productDetails_ViewPager);
    }

    private void initData() {
        //判断网络
        boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
        if (!iSNetworkConnect) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            ProductDetailsActivity.this.finish();
        } else {
            if (!mIsLoginBoolean) {
                //没登陆过
                loadNetworkData(false);
            } else {
                //登录过
                loadNetworkData(true);
            }
        }
    }

    /**
     * 添加点
     */
    private void opearView(List<ImageViewBean> imageViewList) {
        //加之前清空
        viewPagerList.clear();
        ll_productDetails_pointLinearLayout.removeAllViews();
        // 图片的数据
        // 给界面添加图片
        for (int i = 0; i < imageViewList.size(); i++) {
            ImageInfo imageInfo = new ImageInfo(imageViewList.get(i).imageViewUrl, 200, 200);
            // 添加界面的数据
            viewPagerList.add(imageInfo);
            // 给点的容器Linearlayout初始化添加灰色点
            ImageView mV_point = new ImageView(UIUtils.getContext());
            mV_point.setBackgroundResource(R.drawable.point_selector);
            int dp = 10;
            // 设置灰色点的大小
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);// 注意单位是px 不是dp

            // 设置点与点直接的空隙

            // 第一个点不需要指定
            params.leftMargin = DensityUtil.dip2px(UIUtils.getContext(), 10);

            if (i != 0) {
                mV_point.setEnabled(false);
            }

            mV_point.setLayoutParams(params);

            // 添加灰色的点到线性布局中
            ll_productDetails_pointLinearLayout.addView(mV_point);
        }
    }

    /**
     * 加载网络
     */
    private void loadNetworkData(boolean isLogin) {
        //进度条展示
        mHandler.obtainMessage(LOADING).sendToTarget();

        if (isLogin) {
            OkHttpUtils.get().url(productDetailstUrl).addParams("product_id", mProduct_id)
                    .addParams("user_id",mUserId).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    try {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                        //发送handler，隐藏进度条
                        mHandler.obtainMessage(SUCCESS).sendToTarget();
                    } catch (Exception exception) {
                    }
                }

                @Override
                public void onResponse(String response, int id) {

                    final String itemResponse = response;

                    operationResult(itemResponse);
                }
            });
        }else {
            OkHttpUtils.get().url(productDetailstUrl).addParams("product_id", mProduct_id).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    try {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                        //发送handler，隐藏进度条
                        mHandler.obtainMessage(SUCCESS).sendToTarget();
                    } catch (Exception exception) {
                    }
                }

                @Override
                public void onResponse(String response, int id) {

                    final String itemResponse = response;

                    operationResult(itemResponse);
                }
            });
        }
    }

    private void operationResult(final String itemResponse) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 成功了，只是返回来请求的json数据,还需要解析
                    Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(itemResponse);
                    String response_code = map.get("response_code");
                    //需要判断各种响应码,获取具体错误信息
                    String responseContent = RequestResponseUtils.getResponseContent(response_code);

                    if ("0".equals(response_code)) {
                        //解析图片
                        final List<ImageViewBean> imageViewList = ProductDetailsImageViewParse.imageViewParse(itemResponse);
                        if (imageViewList != null && imageViewList.size() > 0) {
                            UIUtils.getMainThreadHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    opearView(imageViewList);
                                }
                            });
                        }

                        //解析是否收藏
                        Map<String, String> favoriteInfoMap = ShopDetailsIsFavoriteInfoParse.favoriteInfoParse(itemResponse);
                        if (!TextUtils.isEmpty(favoriteInfoMap.get("favoriteInfo")) && "0".equals(favoriteInfoMap.get("favoriteInfo"))) {
                            //未收藏
                            isCollect = false;
                        } else if (!TextUtils.isEmpty(favoriteInfoMap.get("favoriteInfo")) && "1".equals(favoriteInfoMap.get("favoriteInfo"))) {
                            //收藏了
                            isCollect = true;
                        }

                        //TODO 解析商品介绍
                        Map<String, String> productMap = ProductDetailsParse.productDetailsParse(itemResponse);
                        final Map<String, String> itemProductMap = productMap;

                        //单独解析remark
                        final Map<String, String> remarkParseMap = remarkParse.remarkParse(itemResponse);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (itemProductMap != null) {
                                    //设置数据
                                    tv_productDetails_shopName.setText(itemProductMap.get("name"));
                                    tv_productDetails_money.setText(itemProductMap.get("discount_price"));
                                    tv_productDetails_jia.setText(itemProductMap.get("price"));
                                    tv_product_details_discount.setText(itemProductMap.get("discount"));
                                    tv_productDetails_description.setText(itemProductMap.get("description"));
                                    //得到电话号码
                                    mPhone_number = itemProductMap.get("phone_number");
                                    try {
                                        if (!TextUtils.isEmpty(remarkParseMap.get("grade").toString())) {
                                            tv_product_gradeTitle.setVisibility(View.VISIBLE);
                                            tv_product_grade.setVisibility(View.VISIBLE);
                                            tv_product_grade.setText(remarkParseMap.get("grade").toString());
                                        } else {
                                            tv_product_gradeTitle.setVisibility(View.INVISIBLE);
                                            tv_product_grade.setVisibility(View.GONE);
                                        }
                                    } catch (Exception c) {
                                    }
                                    //依据是否收藏来显示不同的图标
                                    if(isCollect) {
                                        iv_productDetails_pre.setVisibility(View.VISIBLE);
                                        iv_productDetails_collect.setVisibility(View.GONE);
                                    }else {
                                        iv_productDetails_pre.setVisibility(View.GONE);
                                        iv_productDetails_collect.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        });
                        //发送handler，隐藏进度条
                        mHandler.obtainMessage(SUCCESS).sendToTarget();
                    } else if (responseContent != null) {
                        ToastUtils.showToastInChildThread(UIUtils.getContext(), responseContent);
                    }
                } catch (Exception e) {
                    ToastUtils.showToastInChildThread(UIUtils.getContext(), "请求超时");
                    //发送handler，隐藏进度条
                    mHandler.obtainMessage(SUCCESS).sendToTarget();
                }
            }
        }).start();
    }

    private void initEvent() {
        //返回
        rl_productDetails_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsFromGoodCollect) {
                    ProductDetailsActivity.this.finish();
                    GoodCollectFragment.mGoodCollectFragment.refreshGoodData();
                }else {
                    ProductDetailsActivity.this.finish();
                }
            }
        });

        //ViewPager
        vp_productDetails_ViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                position = position % viewPagerList.size();
                //当前ViewPager显示的页码
                for (int i = 0; i < ll_productDetails_pointLinearLayout.getChildCount(); i++) {
                    ll_productDetails_pointLinearLayout.getChildAt(i).setEnabled(i == position);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //店铺咨询
        b_productDetails_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出自定义对话框
                customCallDialog();
            }
        });

        //在线指导
        b_productDetails_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String finalCityName = SpUtils.getSpString(UIUtils.getContext(), "finalCityName", "成都");
                String finalBusinessName = SpUtils.getSpString(UIUtils.getContext(), "finalBusinessName", "全城");
                //跳转到mainActivity
                Intent intent = new Intent();
                intent.setClass(ProductDetailsActivity.this,MainActivity.class);
                intent.putExtra("fragmentIndex",1);
                //设置目的，防止按返回键再次进入MainActivity
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                intent.putExtra("cityName",finalCityName);
                intent.putExtra("businessName",finalBusinessName);
                startActivity(intent);
            }
        });
        //商品收藏
        rl_productDetails_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mIsLoginBoolean) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请先登录");
                    ToNextActivityUtils.toNextAndNoFinishActivity(ProductDetailsActivity.this,LoginActivity.class);
                }else {
                    requestCollect();
                }
            }
        });
    }

    /**
     * 自定义对话框
     */
    private void customCallDialog() {
        try {
            //截取字符串，前面的区号
            mBeforeArea = mPhone_number.substring(0, 3);
            //后面的号码
            mAfterPhone = mPhone_number.substring(3, mPhone_number.length());
        }catch (Exception e) {

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailsActivity.this);
        View inflate = View.inflate(ProductDetailsActivity.this, R.layout.custom_call_dialog, null);

        TextView tv_customDialog_phone = (TextView) inflate.findViewById(R.id.tv_customDialog_phone);
        //设置电话号码
        if(TextUtils.isEmpty(mBeforeArea) || TextUtils.isEmpty(mAfterPhone)) {
            tv_customDialog_phone.setText(mPhone_number);
        }else {
            tv_customDialog_phone.setText(mBeforeArea + "-" + mAfterPhone);
        }

        //取消
        Button canleButton = (Button) inflate.findViewById(R.id.b_call_canle);
        //呼叫
        Button submitButton = (Button) inflate.findViewById(R.id.b_call_call);

        builder.setView(inflate);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();


        canleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接跳到拨通电话
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + mPhone_number));
                startActivity(intent);
                alertDialog.dismiss();
            }
        });

    }

    /**
     * 请求服务器，收藏
     */
    private void requestCollect() {
        //判断网络
        boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
        if (!iSNetworkConnect) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            OkHttpUtils.post().url(collectProductUrl).addParams("user_id", mUserId)
                    .addParams("product_id", mProduct_id)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    try {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                    } catch (Exception exception) {
                    }
                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        // 成功了，只是返回来请求的json数据,还需要解析
                        Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                        String response_code = map.get("response_code");
                        if("0".equals(response_code)) {
                            //收藏成功
                            ToastUtils.showToastInUIThread(UIUtils.getContext(),"收藏成功");
                            //换图片
                            iv_productDetails_pre.setVisibility(View.VISIBLE);
                            iv_productDetails_collect.setVisibility(View.GONE);
                        }else if ("400".equals(response_code)) {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "请先登录");
                            ToNextActivityUtils.toNextAndNoFinishActivity(ProductDetailsActivity.this,LoginActivity.class);
                        }else if ("4100".equals(response_code)){
                            ToastUtils.showToastInUIThread(UIUtils.getContext(),"收藏失败");
                            //换图片
                            iv_productDetails_pre.setVisibility(View.GONE);
                            iv_productDetails_collect.setVisibility(View.VISIBLE);
                        }else if ("4101".equals(response_code)){
                            ToastUtils.showToastInUIThread(UIUtils.getContext(),"取消收藏");
                            //换图片
                            iv_productDetails_collect.setBackgroundResource(R.mipmap.normal_collect);
                            iv_productDetails_pre.setVisibility(View.GONE);
                            iv_productDetails_collect.setVisibility(View.VISIBLE);
                        }else if ("4102".equals(response_code)){
                            ToastUtils.showToastInUIThread(UIUtils.getContext(),"取消收藏失败");
                            //换图片
                            iv_productDetails_pre.setVisibility(View.VISIBLE);
                            iv_productDetails_collect.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                    }
                }
            });
        }
    }

    //重写返回键，为了使从商铺收藏进来的刷新
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mIsFromGoodCollect) {
                ProductDetailsActivity.this.finish();
                GoodCollectFragment.mGoodCollectFragment.refreshGoodData();
            }else {
                ProductDetailsActivity.this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //    class ShopDetailsViewPagerAdapter extends PagerAdapter {
    //
    //        // 图片缓存 默认 等
    //        //        private DisplayImageOptions optionsImag = new DisplayImageOptions.Builder()
    //        //                .showImageForEmptyUri(R.mipmap.zanwutupian)
    //        //                .showImageOnFail(R.mipmap.zanwutupian).cacheInMemory(true).cacheOnDisk(true)
    //        //                .considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY)
    //        //                .bitmapConfig(Bitmap.Config.RGB_565).build();
    //
    //
    //        @Override
    //        public int getCount() {
    //            if (viewPagerList != null && viewPagerList.size() > 0) {
    //                //为了让Viepager滑到最左和最右还可以滑动
    //                return Integer.MAX_VALUE;
    //                //                return imageInfoList.size();
    //            } else {
    //                return 0;
    //            }
    //        }
    //
    //        @Override
    //        public boolean isViewFromObject(View view, Object object) {
    //            return view == object;
    //        }
    //
    //        @Override
    //        public Object instantiateItem(ViewGroup container, int position) {
    //
    //            position = position % viewPagerList.size();
    //
    //
    //            ImageView iv_image = new ImageView(UIUtils.getContext());
    //
    //            Glide.with(UIUtils.getContext()).load(viewPagerList.get(position).getUrl()).error(R.mipmap.zanwutupian).into(iv_image);
    //
    //            //ImageLoader.getInstance().displayImage(viewPagerList.get(position).getUrl(), iv_image, optionsImag);
    //            // 添加View
    //            container.addView(iv_image);
    //            final int tempPosition = position;
    //            //每个图片的点击事件(最好通过触摸添加防止滑动viewpager触发点击事件)
    //            //            iv_image.setOnClickListener(new View.OnClickListener() {
    //            //                @Override
    //            //                public void onClick(View v) {
    //            //                    PicShowDialog dialog = new PicShowDialog(ShopDetailsActivity.this,imageInfoList,tempPosition);
    //            //                    dialog.show();
    //            //                }
    //            //            });
    //            iv_image.setOnTouchListener(new View.OnTouchListener() {
    //                private float downX;
    //                private float downY;
    //                private long downTime;
    //
    //                @Override
    //                public boolean onTouch(View v, MotionEvent event) {
    //                    switch (event.getAction()) {
    //                        case MotionEvent.ACTION_DOWN://按下停止无限播放
    //                            downX = event.getX();
    //                            downY = event.getY();
    //                            downTime = System.currentTimeMillis();
    //                            mInfinitePlay.stopPlay();
    //                            break;
    //                        case MotionEvent.ACTION_CANCEL://事件取消也无限播放
    //                            mInfinitePlay.startPlay();
    //                            break;
    //                        case MotionEvent.ACTION_UP://松开继续无限播放
    //                            float upX = event.getX();
    //                            float upY = event.getY();
    //                            if (upX == downX && upY == downY) {
    //                                long upTime = System.currentTimeMillis();
    //                                if (upTime - downTime < 500) {
    //                                    //点击
    //                                    PicShowDialog dialog = new PicShowDialog(ProductDetailsActivity.this, viewPagerList, tempPosition);
    //                                    dialog.show();
    //                                }
    //                            }
    //                            mInfinitePlay.startPlay();
    //                            break;
    //                        case MotionEvent.ACTION_MOVE:
    //                            break;
    //                        default:
    //                            break;
    //                    }
    //                    return true;
    //                }
    //            });
    //
    //            return iv_image;
    //        }
    //
    //        @Override
    //        public void destroyItem(ViewGroup container, int position, Object object) {
    //            //position = position % imageInfoList.size();
    //            container.removeView((View) object);// 从Viewpager中移除
    //        }
    //    }
}
