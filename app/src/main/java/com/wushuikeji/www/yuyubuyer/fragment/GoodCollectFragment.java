package com.wushuikeji.www.yuyubuyer.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.activity.ProductDetailsActivity;
import com.wushuikeji.www.yuyubuyer.adapter.ShopDetailsListViewAdapter;
import com.wushuikeji.www.yuyubuyer.base.BaseFragment;
import com.wushuikeji.www.yuyubuyer.bean.ShopDetailsBean;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.GoodCollectParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
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

public class GoodCollectFragment extends BaseFragment {
    //空视图
    @InjectView(R.id.tv_goodCollect_empty)
    TextView tv_goodCollect_empty;

    @InjectView(R.id.goodCollect_listView)
    PullToRefreshSwipeMenuListView goodCollect_listView;

    //进度条
    @InjectView(R.id.pb_goodCollectLoading_progressBar)
    ProgressBar pb_goodCollectLoading_progressBar;

    private List<ShopDetailsBean> goodCollectList = new ArrayList<ShopDetailsBean>();

    private View mGoodCollectView;

    private boolean isBottom;//判断listView是否底部

    private ShopDetailsListViewAdapter mShopDetailsListViewAdapter;

    private boolean mISNetworkConnect;

    public static GoodCollectFragment mGoodCollectFragment;
    private static final int LOADING = 0;
    private static final int SUCCESS = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_goodCollectLoading_progressBar.setVisibility(View.VISIBLE);
                    tv_goodCollect_empty.setVisibility(View.GONE);
                    goodCollect_listView.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    //有数据
                    if (goodCollectList.size() != 0) {
                        pb_goodCollectLoading_progressBar.setVisibility(View.GONE);
                        tv_goodCollect_empty.setVisibility(View.GONE);
                        goodCollect_listView.setVisibility(View.VISIBLE);
                        //更新数据
                        if (mShopDetailsListViewAdapter != null) {
                            mShopDetailsListViewAdapter.notifyDataSetChanged();
                        }
                    } else {
                        pb_goodCollectLoading_progressBar.setVisibility(View.GONE);
                        tv_goodCollect_empty.setVisibility(View.VISIBLE);
                        goodCollect_listView.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private String mUserId;
    //商品收藏列表
    private String goodCollectUrl = Constants.commontUrl + "favorite/product_list";

    //发送删除的请求
    private String deleteProductUrl = Constants.commontUrl + "favorite/product";

    private int count = 10;

    private int offset;

    @Override
    public View initView() {
        //需要展示的View
        if (mGoodCollectView == null) {
            mGoodCollectView = View.inflate(UIUtils.getContext(), R.layout.good_collect_fragment, null);
        }
        //注解
        ButterKnife.inject(this, mGoodCollectView);

        mGoodCollectFragment = this;

        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");

        //去除系统自带的颜色渐变
        goodCollect_listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mShopDetailsListViewAdapter = new ShopDetailsListViewAdapter(goodCollectList);
        goodCollect_listView.setAdapter(mShopDetailsListViewAdapter);

        goodCollect_listView.setPullRefreshEnable(true);
        goodCollect_listView.setPullLoadEnable(true);

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
        goodCollect_listView.setMenuCreator(mCreator);

        return mGoodCollectView;
    }

    @Override
    public void initData() {
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

    /**
     * 加载网络的数据
     */
    private void loadNetworkData(boolean isNeedLoading, String user_id, String mOffset, boolean isFromRefresh, boolean isFromLoadMore, boolean isFromGoodCollect) {

        final boolean itemIsFromRefresh = isFromRefresh;
        final boolean itemIsFromLoadMore = isFromLoadMore;
        final boolean itemIsFromGoodCollect = isFromGoodCollect;
        if (isNeedLoading) {
            //进度条展示
            mHandler.obtainMessage(LOADING).sendToTarget();
        }
        OkHttpUtils.get().url(goodCollectUrl).addParams("user_id", user_id).addParams("offset", mOffset)
                .addParams("count", String.valueOf(count)).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                try {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    loadAndRefreshComple(goodCollect_listView);
                    //发送handler，隐藏进度条
                    mHandler.obtainMessage(SUCCESS).sendToTarget();
                } catch (Exception exception) {
                }
            }

            @Override
            public void onResponse(String response, int id) {

                operationResult(response, itemIsFromRefresh, itemIsFromLoadMore, itemIsFromGoodCollect);
            }
        });
    }

    /**
     * 处理网络返回的结果
     */
    private void operationResult(String response, boolean isFromRefresh, boolean isFromLoadMore, boolean isFromGoodCollect) {
        try {
            // 成功了，只是返回来请求的json数据,还需要解析
            Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
            String response_code = map.get("response_code");
            //需要判断各种响应码,获取具体错误信息
            String responseContent = RequestResponseUtils.getResponseContent(response_code);

            if ("0".equals(response_code)) {

                List<ShopDetailsBean> itemList = GoodCollectParse.goodCollectParse(response);
                if (itemList != null && itemList.size() > 0 && isFromLoadMore) {
                    goodCollectList.addAll(itemList);
                    mShopDetailsListViewAdapter.notifyDataSetChanged();
                } else {
                    goodCollectList.clear();
                    goodCollectList.addAll(itemList);
                    mShopDetailsListViewAdapter.notifyDataSetChanged();
                }

                if (isFromRefresh) {
                    SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
                    RefreshTime.setRefreshTime(UIUtils.getContext(), df.format(new Date()));
                    loadAndRefreshComple(goodCollect_listView);
                }


            } else if (responseContent != null) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                loadAndRefreshComple(goodCollect_listView);

                if (MyConstants.FOOTVIEWNAME.equals(responseContent) && isFromGoodCollect) {
                    if (mShopDetailsListViewAdapter != null) {
                        goodCollectList.clear();
                        mShopDetailsListViewAdapter.notifyDataSetChanged();
                    }
                }
            }
            if (isFromLoadMore) {
                loadAndRefreshComple(goodCollect_listView);
            }
            //发送handler，隐藏进度条
            mHandler.obtainMessage(SUCCESS).sendToTarget();
        } catch (Exception e) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
            loadAndRefreshComple(goodCollect_listView);
            //发送handler，隐藏进度条
            mHandler.obtainMessage(SUCCESS).sendToTarget();
        }
    }

    @Override
    public void initEvent() {
        //侧滑点击删除
        goodCollect_listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        //进度条
                        pb_goodCollectLoading_progressBar.setVisibility(View.VISIBLE);
                        requestDeleteCollect(position);
                        break;
                }
            }
        });
        //刷新与加载更多的监听
        goodCollect_listView.setXListViewListener(new IXListViewListener() {
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
                            offset = 0;
                            loadNetworkData(false, mUserId, "0", true, false, false);
                        }
                    }
                });
            }

            @Override
            public void onLoadMore() {
                if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    return;
                } else {
                    //网络请求
                    offset = offset + 10;
                    loadNetworkData(false, mUserId, String.valueOf(offset), false, true, false);
                }
            }
        });
        //item
        goodCollect_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //position从1开始
                if (position != 0) {
                    Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                    intent.putExtra("product_id", goodCollectList.get(position - 1).productId);
                    intent.putExtra("isFromGoodCollect", true);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 删除后，告诉服务器
     */
    private void requestDeleteCollect(int position) {
        final int itemPosition = position;
        //判断网络
        boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
        if (!iSNetworkConnect) {
            pb_goodCollectLoading_progressBar.setVisibility(View.GONE);
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            OkHttpUtils.post().url(deleteProductUrl).addParams("user_id", mUserId)
                    .addParams("product_id", goodCollectList.get(itemPosition).productId)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                        pb_goodCollectLoading_progressBar.setVisibility(View.GONE);
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        // 成功了，只是返回来请求的json数据,还需要解析
                        Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                        String response_code = map.get("response_code");
                        if ("4101".equals(response_code)) {
                            goodCollectList.remove(itemPosition);
                            mShopDetailsListViewAdapter.notifyDataSetChanged();
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "删除成功");
                        } else {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "删除失败");
                        }
                        pb_goodCollectLoading_progressBar.setVisibility(View.GONE);
                    } catch (Exception e) {
                        pb_goodCollectLoading_progressBar.setVisibility(View.GONE);
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    }
                }
            });
        }
    }

    private void loadAndRefreshComple(PullToRefreshSwipeMenuListView mListView) {
        mListView.setRefreshTime(RefreshTime.getRefreshTime(UIUtils.getContext()));
        mListView.stopRefresh();
        mListView.stopLoadMore();
    }

    /**
     * 提供给外部刷新
     */
    public void refreshGoodData() {
        loadNetworkData(true, mUserId, "0", false, false, true);
    }
}
