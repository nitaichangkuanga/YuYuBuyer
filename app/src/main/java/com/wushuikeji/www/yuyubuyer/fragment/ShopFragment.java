package com.wushuikeji.www.yuyubuyer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.activity.SelectCityActivity;
import com.wushuikeji.www.yuyubuyer.activity.ShopDetailsActivity;
import com.wushuikeji.www.yuyubuyer.adapter.MainShopListViewAdapter;
import com.wushuikeji.www.yuyubuyer.base.BaseFragment;
import com.wushuikeji.www.yuyubuyer.bean.ShopBean;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.ShopFragmentParse;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.ListViewUtils;
import com.wushuikeji.www.yuyubuyer.utils.PtrFrameRefreshUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.wushuikeji.www.yuyubuyer.view.CircularProgress;
import com.wushuikeji.www.yuyubuyer.view.ClearEditText;
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


public class ShopFragment extends BaseFragment {
    //空视图
    @InjectView(R.id.tv_mainshop_emptyView)
    TextView tv_mainshop_emptyView;

    //刷新的类
    @InjectView(R.id.in_mainshop_pf)
    PtrClassicFrameLayout mPtrFrame;

    @InjectView(R.id.mainShopFragment_list)
    ListView mainShopFragment_list;

    //进度条
    @InjectView(R.id.pb_loading_progressBar)
    CircularProgress pb_loading_progressBar;

    //自定义的EditText
    @InjectView(R.id.custom_main_editText)
    ClearEditText custom_main_editText;

    //点击选择城市
    @InjectView(R.id.tv_shop_city)
    TextView tv_shop_city;

    //点击选择商圈
    @InjectView(R.id.tv_mainShop_location)
    TextView tv_mainShop_location;

    private static final int LOADING = 0;
    private static final int SUCCESS = 1;

    private MainShopListViewAdapter mainShopListViewAdapter;

    private List<ShopBean> shopBeanList = new ArrayList<ShopBean>();

    private boolean isBottom;//判断listView是否底部

    private int count = 10;//每页加载10个

    private int offset;//每次从哪个位置查询

    //商铺的地址
    private String shopFragmentUrl = Constants.commontUrl + "shop/nearshops";

    private View mTailView;
    private View mShopFragmentView;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_loading_progressBar.setVisibility(View.VISIBLE);
                    tv_mainshop_emptyView.setVisibility(View.GONE);
                    mPtrFrame.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    if (shopBeanList.size() != 0) {
                        pb_loading_progressBar.setVisibility(View.GONE);
                        tv_mainshop_emptyView.setVisibility(View.GONE);
                        mPtrFrame.setVisibility(View.VISIBLE);
                        if (mainShopListViewAdapter != null) {
                            mainShopListViewAdapter.notifyDataSetChanged();
                        }
                    } else {
                        pb_loading_progressBar.setVisibility(View.GONE);
                        tv_mainshop_emptyView.setVisibility(View.VISIBLE);
                        mPtrFrame.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private String mCityName;
    private String mBusinessName;
    private String mArea_id;
    private String mLongitude;
    private String mLatitude;
    //提供给外部刷新
    public static ShopFragment mShopFragment;

    @Override
    public void init() {
        Bundle bundle = getArguments();
        mCityName = bundle.getString("cityName");
        mBusinessName = bundle.getString("businessName");
        mArea_id = bundle.getString("area_id");
    }

    @Override
    public View initView() {
        //需要展示的View
        mShopFragmentView = View.inflate(UIUtils.getContext(), R.layout.main_shop, null);

        //注解
        ButterKnife.inject(this, mShopFragmentView);

        mShopFragment = this;

        //将尾部布局蹦出来
        if (mTailView == null) {
            mTailView = View.inflate(UIUtils.getContext(), R.layout.item_listview_mainshop_loadmore, null);
        }
        //去除系统自带的颜色渐变
        mainShopFragment_list.setOverScrollMode(View.OVER_SCROLL_NEVER);
        //ListView的初始化相关操作
        operateListView();
        //刷新的操作
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        PtrFrameRefreshUtils.setRefreshParams(mPtrFrame);
        return mShopFragmentView;
    }


    @Override
    public void initData() {
        super.initData();
        //一进来先从缓存中读取最后保存的城市名和商圈名
        String finalCityName = SpUtils.getSpString(UIUtils.getContext(), "finalCityName", "成都");
        String finalBusinessName = SpUtils.getSpString(UIUtils.getContext(), "finalBusinessName", "全城");
        String finalBusinessId = SpUtils.getSpString(UIUtils.getContext(), "finalBusinessId", "0");

        //一开始的城市名和商圈名
        if (!TextUtils.isEmpty(finalCityName)) {
            tv_shop_city.setText(finalCityName);
        }
        if (!TextUtils.isEmpty(finalBusinessName)) {
            tv_mainShop_location.setText(finalBusinessName);
        }
        if (!TextUtils.isEmpty(finalBusinessId)) {
            mArea_id = finalBusinessId;
        }

        //用户选择之后设置的城市名和商圈名
        if (!TextUtils.isEmpty(mCityName)) {
            tv_shop_city.setText(mCityName);
        }
        if (!TextUtils.isEmpty(mBusinessName)) {
            tv_mainShop_location.setText(mBusinessName);
        }
        //获取定位后的经纬度
        //经度
        mLongitude = SpUtils.getSpString(UIUtils.getContext(), MyConstants.LONGITUDE, "");
        //纬度
        mLatitude = SpUtils.getSpString(UIUtils.getContext(), MyConstants.LATITUDE, "");

        //判断网络
        boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
        //先判断缓存中是否有数据
        String jsonString = SpUtils.getCacheString(MyConstants.SHOPSPNAME, UIUtils.getContext(), MyConstants.SHOPFRAGMENTJSON, "");

        if (TextUtils.isEmpty(jsonString)) {
            //为空，在需要判断网络,再去解析
            if (!iSNetworkConnect) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                return;
            } else {
                loadNetworkData("0", true,false,true,true);
            }
        } else {
            //有缓存，进行解析显示
            List<ShopBean> cacheTempList = ShopFragmentParse.shopFragmentParse(jsonString);
            if(cacheTempList != null) {
                shopBeanList.clear();
                shopBeanList.addAll(cacheTempList);
                ListViewUtils.isAddListViewShopFooterView(shopBeanList, mainShopFragment_list, mTailView);
            }
            //发送handler，为了让listView显示出来并加载数据
            mHandler.obtainMessage(SUCCESS).sendToTarget();
        }
    }

    /**
     * 加载网络的数据
     */
    private void loadNetworkData(String offset, boolean isNeedLoading,boolean isFromRefresh,boolean isNeedClearData,boolean isFirstSave) {
        if (!TextUtils.isEmpty(mLatitude) && !TextUtils.isEmpty(mLongitude)) {

            final boolean itemIsFromRefresh = isFromRefresh;
            final boolean itemIsNeedClearData = isNeedClearData;
            final boolean itemIsFirstSave = isFirstSave;

            if (isNeedLoading) {
                //进度条展示
                mHandler.obtainMessage(LOADING).sendToTarget();
            }

            //网络请求
            OkHttpUtils.get().url(shopFragmentUrl).addParams("area_id", mArea_id)
                    .addParams("lat", mLatitude)
                    .addParams("lng", mLongitude)
                    .addParams("offset", offset)
                    .addParams("count", String.valueOf(count)).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    if(mPtrFrame != null && itemIsFromRefresh) {
                        mPtrFrame.refreshComplete();
                    }
                    //发送handler，隐藏进度条
                    mHandler.obtainMessage(SUCCESS).sendToTarget();
                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        //先解析reponse_code,给客户提醒
                        Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                        String response_code = map.get("response_code");
                        //需要判断各种响应码,获取具体错误信息
                        String responseContent = RequestResponseUtils.getResponseContent(response_code);
                        if ("0".equals(response_code)) {
                            //保存缓存（只保留第一次的，因为加载更多后后面的json数据会覆盖掉）
                            if(itemIsFirstSave) {
                                SpUtils.putCacheString(MyConstants.SHOPSPNAME, UIUtils.getContext(), MyConstants.SHOPFRAGMENTJSON, response);

                            }
                            //可以下一步解析商铺
                            List<ShopBean> tempList = ShopFragmentParse.shopFragmentParse(response);

                            if(tempList != null) {

                                if(itemIsNeedClearData) {
                                    shopBeanList.clear();
                                    shopBeanList.addAll(tempList);
                                    ListViewUtils.isAddListViewShopFooterView(shopBeanList, mainShopFragment_list, mTailView);

                                }else {
                                    //加载更多
                                    if (tempList.size() == 0) {
                                        if (mainShopFragment_list.getFooterViewsCount() == 1) {
                                            try {
                                                mainShopFragment_list.removeFooterView(mTailView);
                                                mainShopListViewAdapter.notifyDataSetChanged();
                                            } catch (Exception e) {
                                            }
                                        }
                                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "没有数据了");
                                    }else {
                                        shopBeanList.addAll(tempList);
                                        ListViewUtils.isAddListViewShopFooterView(shopBeanList, mainShopFragment_list, mTailView);

                                    }
                                }
                                //刷新完成
                                if(mPtrFrame != null && itemIsFromRefresh) {
                                    mPtrFrame.refreshComplete();
                                }
                            }
                        } else if (responseContent != null) {
                            //给出错误提示
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                            if(mPtrFrame != null && itemIsFromRefresh) {
                                mPtrFrame.refreshComplete();
                            }
                            if (MyConstants.FOOTVIEWNAME.equals(responseContent)) {
                                if (mainShopFragment_list.getFooterViewsCount() == 1) {
                                    try {
                                        mainShopFragment_list.removeFooterView(mTailView);
                                        mainShopListViewAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                        //发送handler，隐藏进度条
                        mHandler.obtainMessage(SUCCESS).sendToTarget();
                    }catch (Exception e) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                        if(mPtrFrame != null && itemIsFromRefresh) {
                            mPtrFrame.refreshComplete();
                        }
                        //发送handler，隐藏进度条
                        mHandler.obtainMessage(SUCCESS).sendToTarget();
                    }
                }
            });
        }else {
            ToastUtils.showToastInUIThread(UIUtils.getContext(),"网络不稳定或者请开启读取位置信息权限");
        }
    }


    @Override
    public void initEvent() {
        super.initEvent();

        //listView的滑动事件
        mainShopFragment_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 数据已经到底部，而且用户是松手状态，这个时候需要加载新的数据
                if (isBottom && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    custom_main_editText.setCursorVisible(false);
                    if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                        return;
                    }else {
                        offset = offset + 10;
                        loadNetworkData(String.valueOf(offset),false,false,false,false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 判读是否需要加载新的数据
                isBottom = (firstVisibleItem + visibleItemCount) == totalItemCount;
            }
        });
        //listView的点击事件
        mainShopFragment_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                custom_main_editText.setCursorVisible(false);
                Intent intent = new Intent(getActivity(), ShopDetailsActivity.class);
                intent.putExtra("shopId", shopBeanList.get(position).shopId);
                intent.putExtra("shopName", shopBeanList.get(position).shopName);
                intent.putExtra("distance", shopBeanList.get(position).distance);
                intent.putExtra("shopAddress", shopBeanList.get(position).shopAddress);
                startActivity(intent);
            }
        });
        //实现下拉刷新
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                // here check $mListView instead of $content
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mainShopFragment_list, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //先让EditText光标消失
                custom_main_editText.setCursorVisible(false);
                if(!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    mPtrFrame.refreshComplete();//目的让刷新隐藏掉
                    return;
                }else {
                    //需要清0
                    offset = 0;
                    loadNetworkData("0", false,true,true,true);
                    //防止加载更多的时候，已经加了尾部布局，所以，先判断如果数据源小于8个,并且有尾部就删除
                    if (shopBeanList.size() <= 8 && mainShopFragment_list.getFooterViewsCount() == 1) {
                        try {
                            mainShopFragment_list.removeFooterView(mTailView);
                            mainShopListViewAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        });
        //让EditText恢复光标
        custom_main_editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custom_main_editText.setCursorVisible(true);
            }
        });
        //点击选择城市
        tv_shop_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectCityActivity.class);
                intent.putExtra("fragmentIndex", 0);
                intent.putExtra("clickLocation", 0);
                startActivity(intent);
            }
        });
        //点击商圈
        tv_mainShop_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(tv_shop_city.getText().toString())) {
                    ToastUtils.showToastInUIThread(getActivity(), "请先选择城市");
                    return;
                } else {
                    //需要将城市传给SelectCityActivity，因为要自动跳转
                    Intent intent = new Intent(getActivity(), SelectCityActivity.class);
                    intent.putExtra("fragmentIndex", 0);
                    intent.putExtra("clickLocation", 1);
                    intent.putExtra("fragmentCityName", tv_shop_city.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }

    private void operateListView() {
        //必须在setAdapter之前
        //加之前，先判断数据源有几个，不够一屏幕显示，不加入
        ListViewUtils.isAddListViewShopFooterView(shopBeanList, mainShopFragment_list, mTailView);
        mainShopListViewAdapter = new MainShopListViewAdapter(shopBeanList);

        mainShopFragment_list.setAdapter(mainShopListViewAdapter);
    }

    /**
     * 给外部刷新数据
     */
    public void refreshShopFragmentData() {
        //重新获取经纬度
        //经度
        mLongitude = SpUtils.getSpString(UIUtils.getContext(), MyConstants.LONGITUDE, "");
        //纬度
        mLatitude = SpUtils.getSpString(UIUtils.getContext(), MyConstants.LATITUDE, "");
        loadNetworkData("0", true,false,true,true);
    }
}
