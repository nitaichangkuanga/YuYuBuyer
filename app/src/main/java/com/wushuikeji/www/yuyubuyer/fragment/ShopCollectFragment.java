package com.wushuikeji.www.yuyubuyer.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.activity.ShopDetailsActivity;
import com.wushuikeji.www.yuyubuyer.adapter.ShopCollectListViewAdapter;
import com.wushuikeji.www.yuyubuyer.base.BaseFragment;
import com.wushuikeji.www.yuyubuyer.bean.ShopCollectBean;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.ShopCollectParse;
import com.wushuikeji.www.yuyubuyer.mylibrary.PullToRefreshSwipeMenuListView;
import com.wushuikeji.www.yuyubuyer.mylibrary.pulltorefresh.interfaces.IXListViewListener;
import com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.bean.SwipeMenu;
import com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.bean.SwipeMenuItem;
import com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.interfaces.OnMenuItemClickListener;
import com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.interfaces.SwipeMenuCreator;
import com.wushuikeji.www.yuyubuyer.mylibrary.util.RefreshTime;
import com.wushuikeji.www.yuyubuyer.utils.ConvertUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;


public class ShopCollectFragment extends BaseFragment {
    //空视图
    @InjectView(R.id.tv_shopCollect_empty)
    TextView tv_shopCollect_empty;

    @InjectView(R.id.shopCollect_listView)
    PullToRefreshSwipeMenuListView shopCollect_listView;

    //进度条
    @InjectView(R.id.pb_shopCollectLoading_progressBar)
    ProgressBar pb_shopCollectLoading_progressBar;

    private List<ShopCollectBean> shopCollectList = new ArrayList<ShopCollectBean>();

    private View mShopCollectView;

    private boolean isBottom;//判断listView是否底部

    private ShopCollectListViewAdapter mShopCollectListViewAdapter;

    private static final int LOADING = 0;
    private static final int SUCCESS = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_shopCollectLoading_progressBar.setVisibility(View.VISIBLE);
                    tv_shopCollect_empty.setVisibility(View.GONE);
                    shopCollect_listView.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    //有数据
                    if (shopCollectList.size() != 0) {
                        pb_shopCollectLoading_progressBar.setVisibility(View.GONE);
                        tv_shopCollect_empty.setVisibility(View.GONE);
                        shopCollect_listView.setVisibility(View.VISIBLE);
                        //更新数据
                        if (mShopCollectListViewAdapter != null) {
                            mShopCollectListViewAdapter.notifyDataSetChanged();
                        }
                    } else {
                        pb_shopCollectLoading_progressBar.setVisibility(View.GONE);
                        tv_shopCollect_empty.setVisibility(View.VISIBLE);
                        shopCollect_listView.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private String deleteUrl = Constants.commontUrl + "favorite/shop";

    private String mUserId;
    //商铺收藏
    private String shopCollectUrl = Constants.commontUrl + "favorite/shop_list";

    private int count = 10;

    private int offset;
    private boolean mISNetworkConnect;

    public static ShopCollectFragment mShopCollectFragment;

    @Override
    public View initView() {
        //需要展示的View
        if (mShopCollectView == null) {
            mShopCollectView = View.inflate(UIUtils.getContext(), R.layout.shop_collect_fragment, null);
        }
        //注解
        ButterKnife.inject(this, mShopCollectView);

        mShopCollectFragment = this;
        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");

        //去除系统自带的颜色渐变
        shopCollect_listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mShopCollectListViewAdapter = new ShopCollectListViewAdapter(shopCollectList);
        shopCollect_listView.setAdapter(mShopCollectListViewAdapter);

        shopCollect_listView.setPullRefreshEnable(true);
        shopCollect_listView.setPullLoadEnable(true);
        //添加侧滑图片和文字的操作
        SwipeMenuCreator mCreator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(UIUtils.getContext());
                // set item background
                deleteItem.setBackground(R.color.colorMainTitle);
                // set item width
                deleteItem.setWidth(ConvertUtils.dp2px(60, UIUtils.getContext()));
                deleteItem.setTitle("删除");
                deleteItem.setTitleSize(14);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                // set a icon
                deleteItem.setIcon(R.mipmap.delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        shopCollect_listView.setMenuCreator(mCreator);

        return mShopCollectView;
    }

    @Override
    public void initData() {
        super.initData();
        //判断网络
        mISNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
        if (!mISNetworkConnect) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            //有网
            loadNetworkData(true, mUserId, "0", false, false, false);
        }
    }

    @Override
    public void initEvent() {
        super.initEvent();
        //侧滑点击删除
        shopCollect_listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        shopCollect_listView.setVisibility(View.VISIBLE);
                        //position从0开始
                        requestDeleteCollect(position);
                        break;
                }
            }
        });
        //刷新与加载更多的监听
        shopCollect_listView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!mISNetworkConnect) {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                            return;
                        } else {
                            //有网
                            //清0
                            offset = 0;
                            loadNetworkData(true, mUserId, "0", true, false, false);
                        }
                    }
                });
            }

            @Override
            public void onLoadMore() {
                offset = offset + 10;
                loadMoreNetworkData(String.valueOf(offset));
            }
        });
        //item点击
        shopCollect_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //position从1开始  和删除的position开始位置不一样
                //得到距离
                if (position != 0 && !TextUtils.isEmpty(shopCollectList.get(position - 1).shopId)) {
                    String distance = SpUtils.getSpString(UIUtils.getContext(), shopCollectList.get(position - 1).shopId, "");
                    Intent intent = new Intent(getActivity(), ShopDetailsActivity.class);
                    intent.putExtra("shopId", shopCollectList.get(position - 1).shopId);
                    intent.putExtra("shopName", shopCollectList.get(position - 1).shopName);
                    intent.putExtra("distance", distance);
                    intent.putExtra("shopAddress", shopCollectList.get(position - 1).shopAddress);
                    intent.putExtra("isFromShopCollect", true);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 加载更多的数据
     */
    private void loadMoreNetworkData(String offset) {
        if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            //网络请求
            loadNetworkData(false, mUserId, offset, false, true, false);
        }
    }

    /**
     * 加载网络的数据
     */
    private void loadNetworkData(boolean isNeedLoading, String user_id, String mOffset, boolean isFromRefresh, boolean isFromLoadMore, boolean isFromShopCollect) {
        final boolean itemIsFromRefresh = isFromRefresh;
        final boolean itemIsFromLoadMore = isFromLoadMore;
        final boolean itemIsFromShopCollect = isFromShopCollect;
        if (isNeedLoading) {
            //进度条展示
            mHandler.obtainMessage(LOADING).sendToTarget();
        }
        OkHttpUtils.get().url(shopCollectUrl).addParams("user_id", user_id).addParams("offset", mOffset)
                .addParams("count", String.valueOf(count)).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                try {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    loadAndRefreshComple(shopCollect_listView);
                    //发送handler，隐藏进度条
                    mHandler.obtainMessage(SUCCESS).sendToTarget();
                } catch (Exception exception) {
                    loadAndRefreshComple(shopCollect_listView);
                }
            }

            @Override
            public void onResponse(String response, int id) {

                operationResult(response, itemIsFromRefresh, itemIsFromLoadMore, itemIsFromShopCollect);
            }
        });
    }

    /**
     * 处理网络返回的结果
     */
    private void operationResult(String response, boolean isFromRefresh, boolean isFromLoadMore, boolean isFromShopCollect) {
        try {
            // 成功了，只是返回来请求的json数据,还需要解析
            Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
            String response_code = map.get("response_code");
            //需要判断各种响应码,获取具体错误信息
            String responseContent = RequestResponseUtils.getResponseContent(response_code);

            if ("0".equals(response_code)) {
                List<ShopCollectBean> itemList = ShopCollectParse.shopCollectParse(response);
                if (itemList != null && itemList.size() > 0) {
                    shopCollectList.clear();
                    shopCollectList.addAll(itemList);
                    if (isFromRefresh) {
                        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
                        RefreshTime.setRefreshTime(UIUtils.getContext(), df.format(new Date()));
                        loadAndRefreshComple(shopCollect_listView);
                    }
                    if (mShopCollectListViewAdapter != null) {
                        mShopCollectListViewAdapter.notifyDataSetChanged();
                    }
                }
            } else if (responseContent != null) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                loadAndRefreshComple(shopCollect_listView);

                if (MyConstants.FOOTVIEWNAME.equals(responseContent) && isFromShopCollect) {
                    if (mShopCollectListViewAdapter != null) {
                        shopCollectList.clear();
                        mShopCollectListViewAdapter.notifyDataSetChanged();
                    }
                }

            }
            if (isFromLoadMore) {
                loadAndRefreshComple(shopCollect_listView);
            }
            //发送handler，隐藏进度条
            mHandler.obtainMessage(SUCCESS).sendToTarget();
        } catch (Exception e) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
            loadAndRefreshComple(shopCollect_listView);
            //发送handler，隐藏进度条
            mHandler.obtainMessage(SUCCESS).sendToTarget();
        }
    }

    /**
     * 删除后，告诉服务器
     */
    private void requestDeleteCollect(final int position) {
        final int itemPosition = position;
        //判断网络
        boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
        if (!iSNetworkConnect) {
            shopCollect_listView.setVisibility(View.GONE);
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            OkHttpUtils.post().url(deleteUrl).addParams("user_id", mUserId)
                    .addParams("shop_id", shopCollectList.get(itemPosition).shopId)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    shopCollect_listView.setVisibility(View.GONE);
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");

                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        // 成功了，只是返回来请求的json数据,还需要解析
                        Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                        String response_code = map.get("response_code");
                        if ("4101".equals(response_code)) {
                            shopCollectList.remove(itemPosition);
                            mShopCollectListViewAdapter.notifyDataSetChanged();
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "删除成功");
                        } else {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "删除失败");
                        }
                        shopCollect_listView.setVisibility(View.GONE);
                    } catch (Exception e) {
                        shopCollect_listView.setVisibility(View.GONE);
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    }
                }
            });
        }
    }

    /**
     * 停止刷新和加载
     *
     * @param mListView
     */
    private void loadAndRefreshComple(PullToRefreshSwipeMenuListView mListView) {
        mListView.setRefreshTime(RefreshTime.getRefreshTime(UIUtils.getContext()));
        mListView.stopRefresh();
        mListView.stopLoadMore();
    }

    /**
     * 给外部刷新数据
     */
    public void refreshData() {
        loadNetworkData(true, mUserId, "0", false, false, true);
    }
}
