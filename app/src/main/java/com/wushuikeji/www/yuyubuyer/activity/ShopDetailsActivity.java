package com.wushuikeji.www.yuyubuyer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.ShopDetailsListViewAdapter;
import com.wushuikeji.www.yuyubuyer.bean.ImageInfo;
import com.wushuikeji.www.yuyubuyer.bean.ShopDetailsBean;
import com.wushuikeji.www.yuyubuyer.bean.jsonbean.ImageViewBean;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.constants.ScreenSize;
import com.wushuikeji.www.yuyubuyer.fragment.ShopCollectFragment;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.ShopDetailsImageViewParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.ShopDetailsIsFavoriteInfoParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.ShopDetailsLoadingProInfoParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.ShopDetailsProInfoParse;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.InfinitePlayUtils;
import com.wushuikeji.www.yuyubuyer.utils.ListViewUtils;
import com.wushuikeji.www.yuyubuyer.utils.OperateViewpagerUtils;
import com.wushuikeji.www.yuyubuyer.utils.PtrFrameRefreshUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToNextActivityUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.wushuikeji.www.yuyubuyer.view.CircularProgress;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import okhttp3.Call;

public class ShopDetailsActivity extends AppCompatActivity {

    @InjectView(R.id.lv_shopDetails_listView)
    ListView lv_shopDetails_listView;

    //刷新
    @InjectView(R.id.in_shopDetails_pf)
    PtrClassicFrameLayout in_shopDetails_pf;

    @InjectView(R.id.pb_shopDetailsLoading_progressBar)
    CircularProgress pb_shopDetailsLoading_progressBar;

    @InjectView(R.id.rl_shopDetails_RelativeLayout)
    RelativeLayout rl_shopDetails_RelativeLayout;

    @InjectView(R.id.rl_shopDetails_collect)
    RelativeLayout rl_shopDetails_collect;

    @InjectView(R.id.iv_shopDetails_normal)
    ImageView iv_shopDetails_normal;

    @InjectView(R.id.iv_shopDetails_pre)
    ImageView iv_shopDetails_pre;

    private ShopDetailsListViewAdapter mShopDetailsListViewAdapter;
    private List<ShopDetailsBean> shopDetailsBeanList = new ArrayList<ShopDetailsBean>();
    private LinearLayout mLl_point;
    private ViewPager mViewPager;
    private View mHeadView;
    private boolean isBottom;//判断listView是否底部
    private View mFooterView;
    private Button mButton;

    private static final int LOADING = 0;
    private static final int SUCCESS = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_shopDetailsLoading_progressBar.setVisibility(View.VISIBLE);
                    in_shopDetails_pf.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    //不判断shopDetailsBeanList是否有数据，是因为没有数据时headView也必须显示
                    pb_shopDetailsLoading_progressBar.setVisibility(View.GONE);
                    in_shopDetails_pf.setVisibility(View.VISIBLE);
                    if (mShopDetailsListViewAdapter != null) {
                        mShopDetailsListViewAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private String[] imgUrlsArray;
    //传给Viewpager的
    private List<ImageInfo> viewPagerList = new ArrayList<>();
    //商铺详情
    private String shopFragmenDetailstUrl = Constants.commontUrl + "shop/details";
    //商品列表
    private String productUrl = Constants.commontUrl + "product/lists";

    private String mShopId;
    private boolean mIsLoginBoolean;
    private String mUserId;
    //记录商铺详情是否收藏
    private boolean isCollect;
    private InfinitePlayUtils mInfinitePlayUtils;
    //发送收藏的请求
    private String collectUrl = Constants.commontUrl + "favorite/shop";
    private String mDistance;
    private boolean mIsFromShopCollect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);

        //得到shopId
        mShopId = getIntent().getStringExtra("shopId");
        String shopName = getIntent().getStringExtra("shopName");
        mDistance = getIntent().getStringExtra("distance");
        String shopAddress = getIntent().getStringExtra("shopAddress");
        //判断是否从收藏过来的
        mIsFromShopCollect = getIntent().getBooleanExtra("isFromShopCollect",false);
        //查看用户是否登陆过
        mIsLoginBoolean = SpUtils.getCacheBoolean(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.ISLOGINSTATUS, false);
        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");

        //头布局Viewpager+TextView
        mHeadView = LayoutInflater.from(this).inflate(R.layout.item_listview_headview, lv_shopDetails_listView, false);

        //评价Button
        mButton = (Button) mHeadView.findViewById(R.id.b_shopDetails_button);
        //商铺名字
        TextView shopNameTv = (TextView) mHeadView.findViewById(R.id.tv_shopDetails_shopName);
        TextView shopDistanceTv = (TextView) mHeadView.findViewById(R.id.tv_shopDetails_distanceNum);
        TextView shopAddressTv = (TextView) mHeadView.findViewById(R.id.tv_shopDetails_address);
        //设置值
        shopNameTv.setText(shopName);
        //处理距离的单位
        double afterDisance = Math.round(Double.parseDouble(mDistance) / 100d) / 10d;
        shopDistanceTv.setText(String.valueOf(afterDisance));

        shopAddressTv.setText(shopAddress);

        //viewPager
        mViewPager = (ViewPager) mHeadView.findViewById(R.id.vp_shopDetails_ViewPager);
        //适配Viewpager的高度
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (ScreenSize.displayHeight * 0.35f + 0.5f));
        mViewPager.setLayoutParams(layoutParams);

        //去除系统自带的颜色渐变
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        //添加点的LinearLayout
        mLl_point = (LinearLayout) mHeadView.findViewById(R.id.ll_shopDetails_pointLinearLayout);

        //添加headView到listView中
        lv_shopDetails_listView.addHeaderView(mHeadView);
        //lv_shopDetails_listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        //将尾部布局蹦出来
        if (mFooterView == null) {
            mFooterView = View.inflate(UIUtils.getContext(), R.layout.item_listview_mainshop_loadmore, null);
        }

        //加之前，先判断数据源有几个，不够一屏幕显示，不加入
        ListViewUtils.isAddListViewShopDetailsFooterView(shopDetailsBeanList, lv_shopDetails_listView, mFooterView);
        //ListView的adapter
        if (mShopDetailsListViewAdapter == null) {
            mShopDetailsListViewAdapter = new ShopDetailsListViewAdapter(shopDetailsBeanList);
        }
        lv_shopDetails_listView.setAdapter(mShopDetailsListViewAdapter);
        //刷新的操作
        in_shopDetails_pf.setLastUpdateTimeRelateObject(this);
        in_shopDetails_pf.disableWhenHorizontalMove(true);
        PtrFrameRefreshUtils.setRefreshParams(in_shopDetails_pf);
        mInfinitePlayUtils = new InfinitePlayUtils(mViewPager);
    }

    private void initData() {
        //判断网络
        boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
        if (!iSNetworkConnect) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            ShopDetailsActivity.this.finish();
        } else {
            //有网
            //判断用户是否登陆过
            if (!mIsLoginBoolean) {
                //没登陆过
                loadNetworkData(false, true, "");
            } else {
                //登录过
                loadNetworkData(true, true, mUserId);
            }
        }
    }
    private void initEvent() {
        //返回
        //BackListenerUtils.backFinish(rl_shopDetails_RelativeLayout, this);
        rl_shopDetails_RelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsFromShopCollect) {
                    ShopDetailsActivity.this.finish();
                    ShopCollectFragment.mShopCollectFragment.refreshData();
                }else {
                    ShopDetailsActivity.this.finish();
                }
            }
        });


        //ViewPager
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //当前ViewPager显示的页码
                position = position % viewPagerList.size();
                for (int i = 0; i < mLl_point.getChildCount(); i++) {
                    mLl_point.getChildAt(i).setEnabled(i == position);
                }

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                in_shopDetails_pf.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });
        //listView的滑动事件
        //        lv_shopDetails_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
        //            @Override
        //            public void onScrollStateChanged(AbsListView view, int scrollState) {
        //                // 数据已经到底部，而且用户是松手状态，这个时候需要加载新的数据
        //                if (isBottom && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
        //                    loadMoreNetworkData();
        //                }
        //            }
        //
        //            @Override
        //            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //                // 判读是否需要加载新的数据
        //                isBottom = (firstVisibleItem + visibleItemCount) == totalItemCount;
        //            }
        //        });
        //listView的点击事件
        lv_shopDetails_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0) {
                    ToNextActivityUtils.toNextActivityNotFinishAndParameters(ShopDetailsActivity.this, ProductDetailsActivity.class,"product_id",shopDetailsBeanList.get(position - 1).productId);
                }
            }
        });
        //实现下拉刷新
        in_shopDetails_pf.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                // here check $mListView instead of $content
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, lv_shopDetails_listView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //真正实现下拉刷新,这是在主线程里
                //内部已经添加之前清空了数据源
                //判断网络
                boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
                if (!iSNetworkConnect) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    in_shopDetails_pf.refreshComplete();//目的让刷新隐藏掉
                    return;
                } else {
                    //有网
                    //判断用户是否登陆过
                    if (!mIsLoginBoolean) {
                        //没登陆过
                        refreshData();
                    } else {
                        //登录过
                        refreshLoginData();
                    }
                    //防止加载更多的时候，已经加了尾部布局，所以，先判断如果数据源小于8个,并且有尾部就删除
                    if (shopDetailsBeanList.size() <= 8 && lv_shopDetails_listView.getFooterViewsCount() == 1) {
                        try {
                            lv_shopDetails_listView.removeFooterView(mFooterView);
                            mShopDetailsListViewAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        });
        //评价的按钮
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToNextActivityUtils.toNextActivityNotFinishAndParameters(ShopDetailsActivity.this, ShopEvaluateActivity.class,"shopId",mShopId);

            }
        });
        //收藏的点击
        rl_shopDetails_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //依据后台判断了是否登录（最好自己判断）
                if(!mIsLoginBoolean) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请先登录");
                    ToNextActivityUtils.toNextAndNoFinishActivity(ShopDetailsActivity.this,LoginActivity.class);
                }else {
                    requestCollect();
                }
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
            OkHttpUtils.post().url(collectUrl).addParams("user_id", mUserId)
                    .addParams("shop_id", mShopId)
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
                            iv_shopDetails_pre.setVisibility(View.VISIBLE);
                            iv_shopDetails_normal.setVisibility(View.GONE);
                            //收藏成功就保存距离,因为用户点击查看我的收藏时，需要传到这里来，json上没有距离字段,key唯一，用商铺的id
                            SpUtils.putSpString(UIUtils.getContext(),mShopId,mDistance);
                        }else if ("400".equals(response_code)) {
                            //虽然判断了是否登录，判断也没事
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "请先登录");
                            ToNextActivityUtils.toNextAndNoFinishActivity(ShopDetailsActivity.this,LoginActivity.class);
                        }else if ("4100".equals(response_code)){
                            ToastUtils.showToastInUIThread(UIUtils.getContext(),"收藏失败");
                            //换图片
                            iv_shopDetails_pre.setVisibility(View.GONE);
                            iv_shopDetails_normal.setVisibility(View.VISIBLE);
                        }else if ("4101".equals(response_code)){
                            ToastUtils.showToastInUIThread(UIUtils.getContext(),"取消收藏");
                            //换图片
                            iv_shopDetails_pre.setVisibility(View.GONE);
                            iv_shopDetails_normal.setVisibility(View.VISIBLE);
                        }else if ("4102".equals(response_code)){
                            ToastUtils.showToastInUIThread(UIUtils.getContext(),"取消收藏失败");
                            //换图片
                            iv_shopDetails_pre.setVisibility(View.VISIBLE);
                            iv_shopDetails_normal.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"请求超时");
                    }
                }
            });
        }
    }
    /**
     * 加载网络的数据
     */
    private void loadNetworkData(boolean isLogin, boolean isNeedLoading, String user_id) {

        if (isNeedLoading) {
            //进度条展示
            mHandler.obtainMessage(LOADING).sendToTarget();
        }
        if (isLogin) {
            //登录
            OkHttpUtils.get().url(shopFragmenDetailstUrl).addParams("shop_id", mShopId).addParams("user_id", user_id).build().execute(new StringCallback() {
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
                    operationResult(response);
                }
            });
        } else {
            OkHttpUtils.get().url(shopFragmenDetailstUrl).addParams("shop_id", mShopId).build().execute(new StringCallback() {
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
                    operationResult(response);
                }
            });
        }
    }

    /**
     * 处理网络返回的结果
     */
    private void operationResult(String response) {
        try {
            // 成功了，只是返回来请求的json数据,还需要解析
            Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
            String response_code = map.get("response_code");
            //需要判断各种响应码,获取具体错误信息
            String responseContent = RequestResponseUtils.getResponseContent(response_code);

            if ("0".equals(response_code)) {
                //解析图片
                List<ImageViewBean> imageViewList = ShopDetailsImageViewParse.imageViewParse(response);
                imgUrlsArray = new String[imageViewList.size()];
                for (int i = 0; i < imageViewList.size(); i++) {
                    imgUrlsArray[i] = imageViewList.get(i).imageViewUrl;
                }

                //解析是否收藏
                Map<String, String> favoriteInfoMap = ShopDetailsIsFavoriteInfoParse.favoriteInfoParse(response);
                if (!TextUtils.isEmpty(favoriteInfoMap.get("favoriteInfo")) && "0".equals(favoriteInfoMap.get("favoriteInfo"))) {
                    //未收藏
                    isCollect = false;
                } else if (!TextUtils.isEmpty(favoriteInfoMap.get("favoriteInfo")) && "1".equals(favoriteInfoMap.get("favoriteInfo"))) {
                    //收藏了
                    isCollect = true;
                }
                //依据是否收藏来显示不同的图标
                if(isCollect) {
                    iv_shopDetails_pre.setVisibility(View.VISIBLE);
                    iv_shopDetails_normal.setVisibility(View.GONE);
                }else {
                    iv_shopDetails_pre.setVisibility(View.GONE);
                    iv_shopDetails_normal.setVisibility(View.VISIBLE);
                }

                OperateViewpagerUtils.operateViewpager(ShopDetailsActivity.this, mViewPager, mLl_point, imgUrlsArray, viewPagerList, mInfinitePlayUtils);
                //TODO 解析商铺推荐
                List<ShopDetailsBean> productList = ShopDetailsProInfoParse.proInfoParse(response);
                if (productList != null) {
                    shopDetailsBeanList.clear();
                    shopDetailsBeanList.addAll(productList);
                    //ListViewUtils.isAddListViewShopDetailsFooterView(shopDetailsBeanList, lv_shopDetails_listView, mFooterView);
                }
                //发送handler，隐藏进度条
                mHandler.obtainMessage(SUCCESS).sendToTarget();
            } else if (responseContent != null) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                //发送handler，隐藏进度条
                mHandler.obtainMessage(SUCCESS).sendToTarget();
            }
        } catch (Exception e) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
            //发送handler，隐藏进度条
            mHandler.obtainMessage(SUCCESS).sendToTarget();
        }
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        OkHttpUtils.get().url(shopFragmenDetailstUrl).addParams("shop_id", mShopId).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                try {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                } catch (Exception exception) {
                }
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    // 成功了，只是返回来请求的json数据,还需要解析
                    Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                    String response_code = map.get("response_code");

                    if ("0".equals(response_code)) {
                        //TODO 解析商铺推荐
                        List<ShopDetailsBean> productList = ShopDetailsProInfoParse.proInfoParse(response);
                        if (productList != null) {
                            shopDetailsBeanList.clear();
                            shopDetailsBeanList.addAll(productList);
                            //ListViewUtils.isAddListViewShopDetailsFooterView(shopDetailsBeanList, lv_shopDetails_listView, mFooterView);
                            //刷新完成
                            in_shopDetails_pf.refreshComplete();
                            mShopDetailsListViewAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                }
            }
        });
    }

    private void refreshLoginData() {
        if(!TextUtils.isEmpty(mUserId)) {
            OkHttpUtils.get().url(shopFragmenDetailstUrl).addParams("shop_id", mShopId).addParams("user_id", mUserId).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    try {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    } catch (Exception exception) {
                    }
                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        // 成功了，只是返回来请求的json数据,还需要解析
                        Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                        String response_code = map.get("response_code");

                        if ("0".equals(response_code)) {
                            //TODO 解析商铺推荐
                            List<ShopDetailsBean> productList = ShopDetailsProInfoParse.proInfoParse(response);
                            if (productList != null) {
                                //ListViewUtils.isAddListViewShopDetailsFooterView(shopDetailsBeanList, lv_shopDetails_listView, mFooterView);
                                shopDetailsBeanList.clear();
                                shopDetailsBeanList.addAll(productList);
                                //刷新完成
                                in_shopDetails_pf.refreshComplete();
                                mShopDetailsListViewAdapter.notifyDataSetChanged();
                            }
                        }
                    } catch (Exception e) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                        //刷新完成
                        in_shopDetails_pf.refreshComplete();
                    }
                }
            });
        }
    }
    /**
     * 加载更多的数据
     */
    private void loadMoreNetworkData() {
        if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            if (lv_shopDetails_listView.getFooterViewsCount() == 1) {
                try {
                    lv_shopDetails_listView.removeFooterView(mFooterView);
                    mShopDetailsListViewAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                }
            }
            return;
        } else {
            //网络请求
            OkHttpUtils.get().url(productUrl).addParams("shop_id", mShopId)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    if (lv_shopDetails_listView.getFooterViewsCount() == 1) {
                        try {
                            lv_shopDetails_listView.removeFooterView(mFooterView);
                            mShopDetailsListViewAdapter.notifyDataSetChanged();
                        } catch (Exception ex) {
                        }
                    }
                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        //ListViewUtils.isAddListViewShopDetailsFooterView(shopDetailsBeanList, lv_shopDetails_listView, mFooterView);
                        //先解析reponse_code,给客户提醒
                        Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                        String response_code = map.get("response_code");
                        //需要判断各种响应码,获取具体错误信息
                        String responseContent = RequestResponseUtils.getResponseContent(response_code);
                        if ("0".equals(response_code)) {
                            //可以下一步解析
                            List<ShopDetailsBean> itemList = ShopDetailsLoadingProInfoParse.loadingMoreProInfoParse(response);
                            if (itemList.size() == shopDetailsBeanList.size() || itemList == null || itemList.size() == 0) {
                                if (lv_shopDetails_listView.getFooterViewsCount() == 1) {
                                    try {
                                        lv_shopDetails_listView.removeFooterView(mFooterView);
                                        mShopDetailsListViewAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                    }
                                }
                                ToastUtils.showToastInUIThread(UIUtils.getContext(), "没有数据了");
                            }
                        } else if (responseContent != null) {
                            //给出错误提示
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                        }
                    }catch (Exception r) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "没有数据了");
                        if (lv_shopDetails_listView.getFooterViewsCount() == 1) {
                            try {
                                lv_shopDetails_listView.removeFooterView(mFooterView);
                                mShopDetailsListViewAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            });
        }
    }
    //重写返回键，为了使从商铺收藏进来的刷新
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mIsFromShopCollect) {
                ShopDetailsActivity.this.finish();
                ShopCollectFragment.mShopCollectFragment.refreshData();
            }else {
                ShopDetailsActivity.this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
