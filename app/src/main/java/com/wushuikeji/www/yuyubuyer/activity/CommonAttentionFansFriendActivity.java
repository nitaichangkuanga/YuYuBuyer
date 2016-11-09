package com.wushuikeji.www.yuyubuyer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.MainBuyerListViewAdapter;
import com.wushuikeji.www.yuyubuyer.bean.BuyerBean;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.BuyerFragmentParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.ListViewUtils;
import com.wushuikeji.www.yuyubuyer.utils.PtrFrameRefreshUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
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
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import okhttp3.Call;

public class CommonAttentionFansFriendActivity extends AppCompatActivity {

    @InjectView(R.id.rl_commonFriend_back)
    RelativeLayout rl_commonFriend_back;

    //空视图
    @InjectView(R.id.tv_commonFriend_emptyView)
    TextView tv_commonFriend_emptyView;

    @InjectView(R.id.pb_commonFriendloading_ProgressBar)
    ProgressBar pb_commonFriendloading_ProgressBar;

    @InjectView(R.id.tv_commonFriend_title)
    TextView tv_commonFriend_title;

    //刷新的类
    @InjectView(R.id.in_commonFriend_pf)
    PtrClassicFrameLayout in_commonFriend_pf;

    @InjectView(R.id.commonFriend_listView)
    ListView commonFriend_listView;

    private List<BuyerBean> buyerBeanList = new ArrayList<BuyerBean>();

    private MainBuyerListViewAdapter mainBuyerListViewAdapter;

    private int count = 10;
    private boolean isBottom;//判断listView是否底部

    private View mFooterView;

    private static final int LOADING = 0;
    private static final int SUCCESS = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_commonFriendloading_ProgressBar.setVisibility(View.VISIBLE);
                    tv_commonFriend_emptyView.setVisibility(View.GONE);
                    in_commonFriend_pf.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    if (buyerBeanList.size() != 0) {
                        pb_commonFriendloading_ProgressBar.setVisibility(View.GONE);
                        tv_commonFriend_emptyView.setVisibility(View.GONE);
                        in_commonFriend_pf.setVisibility(View.VISIBLE);
                        //更新数据
                        if (mainBuyerListViewAdapter != null) {
                            mainBuyerListViewAdapter.notifyDataSetChanged();
                        }
                    } else {
                        pb_commonFriendloading_ProgressBar.setVisibility(View.GONE);
                        tv_commonFriend_emptyView.setVisibility(View.VISIBLE);
                        in_commonFriend_pf.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    //获取好友的地址
    private String commonFriendUrl = Constants.commontUrl + "buyer/relation_list";
    private String mBuyerId;
    private int offset;
    private String mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_friend);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);
        //得到buyerId
        mBuyerId = getIntent().getStringExtra("buyerId");
        //得到从哪个地方过来的  关注2  粉丝3  好友1
        mIndex = getIntent().getStringExtra("index");

        if("2".equals(mIndex)) {
            tv_commonFriend_title.setText("关注");
        }else if("3".equals(mIndex)) {
            tv_commonFriend_title.setText("粉丝");
        }else if("1".equals(mIndex)) {
            tv_commonFriend_title.setText("好友");
        }

        //将尾部布局蹦出来
        mFooterView = View.inflate(UIUtils.getContext(), R.layout.item_listview_mainshop_loadmore, null);
        //去除系统自带的颜色渐变
        commonFriend_listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        //ListView的相关操作
        operateBuyerListView();

        //刷新的操作
        in_commonFriend_pf.setLastUpdateTimeRelateObject(this);
        PtrFrameRefreshUtils.setRefreshParams(in_commonFriend_pf);
    }

    private void initData() {
        if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            //判断是需要展示关注2  粉丝3  好友1
            if("1".equals(mIndex) || "2".equals(mIndex) || "3".equals(mIndex)) {
                loadNetworkData("0", mIndex, true, false, true);
            }
        }
    }

    private void initEvent() {
        //返回
        BackListenerUtils.backFinish(rl_commonFriend_back, this);

        //listView的滑动事件
        commonFriend_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 数据已经到底部，而且用户是松手状态，这个时候需要加载新的数据
                if (isBottom && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                    if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                        return;
                    } else {
                        if("1".equals(mIndex) || "2".equals(mIndex) || "3".equals(mIndex)) {
                            offset = offset + 10;
                            loadNetworkData(String.valueOf(offset), mIndex, false, false, false);
                        }
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
        commonFriend_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToNextActivityUtils.toNextActivityNotFinishAndParameters(CommonAttentionFansFriendActivity.this, BuyerInfoActivity.class, "BuyerId", buyerBeanList.get(position).userId);
            }
        });

        //刷新
        in_commonFriend_pf.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                // here check $mListView instead of $content
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, commonFriend_listView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    in_commonFriend_pf.refreshComplete();
                    return;
                } else {
                    if("1".equals(mIndex) || "2".equals(mIndex) || "3".equals(mIndex)) {
                        //需要清0
                        offset = 0;
                        loadNetworkData(String.valueOf(offset), mIndex, false, true, true);
                    }
                    //防止加载更多的时候，已经加了尾部布局，所以，先判断如果数据源小于8个,并且有尾部就删除
                    if (buyerBeanList.size() <= 8 && commonFriend_listView.getFooterViewsCount() == 1) {
                        try {
                            commonFriend_listView.removeFooterView(mFooterView);
                            mainBuyerListViewAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        });
    }

    /**
     * 操作ListView
     */
    private void operateBuyerListView() {

        //必须在setAdapter之前
        //加之前，先判断数据源有几个，不够一屏幕显示，不加入
        ListViewUtils.isAddListViewBuyerFooterView(buyerBeanList, commonFriend_listView, mFooterView);

        mainBuyerListViewAdapter = new MainBuyerListViewAdapter(buyerBeanList);

        commonFriend_listView.setAdapter(mainBuyerListViewAdapter);
    }

    /**
     * 请求网络
     *
     * @param offset
     * @param isNeedLoading
     * @param isFromRefresh
     * @param isNeedClearData
     */
    private void loadNetworkData(String offset, String type, boolean isNeedLoading, boolean isFromRefresh, boolean isNeedClearData) {

        final boolean commonItemIsFromRefresh = isFromRefresh;
        final boolean commonItemIsNeedClearData = isNeedClearData;

        if (isNeedLoading) {
            //进度条展示
            mHandler.obtainMessage(LOADING).sendToTarget();
        }

        OkHttpUtils.post().url(commonFriendUrl)
                .addParams("user_id", mBuyerId)
                .addParams("type", type)
                .addParams("offset", offset)
                .addParams("count", String.valueOf(count))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                if (in_commonFriend_pf != null && commonItemIsFromRefresh) {
                    in_commonFriend_pf.refreshComplete();
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
                        //可以下一步解析
                        List<BuyerBean> tempList = BuyerFragmentParse.buyerFragmentParse(response);

                        if (tempList != null) {
                            //可能之前已经有尾巴，再次进来只加载了小于6条数据就可能看到尾巴
                            if (tempList.size() <= 6 && commonFriend_listView.getFooterViewsCount() == 1) {
                                try {
                                    commonFriend_listView.removeFooterView(mFooterView);
                                    mainBuyerListViewAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    //发送handler，隐藏进度条
                                    mHandler.obtainMessage(SUCCESS).sendToTarget();
                                }
                            }
                            if (commonItemIsNeedClearData) {
                                //刷新
                                buyerBeanList.clear();
                                buyerBeanList.addAll(tempList);
                                ListViewUtils.isAddListViewBuyerFooterView(buyerBeanList, commonFriend_listView, mFooterView);
                            } else {
                                //加载更多
                                if (tempList.size() == 0) {
                                    if (commonFriend_listView.getFooterViewsCount() == 1) {
                                        try {
                                            commonFriend_listView.removeFooterView(mFooterView);
                                            mainBuyerListViewAdapter.notifyDataSetChanged();
                                        } catch (Exception e) {
                                            //发送handler，隐藏进度条
                                            mHandler.obtainMessage(SUCCESS).sendToTarget();
                                        }
                                    }
                                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "没有数据了");
                                } else {
                                    buyerBeanList.addAll(tempList);
                                    ListViewUtils.isAddListViewBuyerFooterView(buyerBeanList, commonFriend_listView, mFooterView);
                                }
                            }
                            //刷新完成
                            if (in_commonFriend_pf != null && commonItemIsFromRefresh) {
                                in_commonFriend_pf.refreshComplete();
                            }
                        }
                        //发送handler，隐藏进度条
                        mHandler.obtainMessage(SUCCESS).sendToTarget();
                    } else if (responseContent != null) {
                        //给出错误提示
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);

                        if (in_commonFriend_pf != null && commonItemIsFromRefresh) {
                            in_commonFriend_pf.refreshComplete();
                        }
                        if (MyConstants.FOOTVIEWNAME.equals(responseContent)) {
                            if (commonFriend_listView.getFooterViewsCount() == 1) {
                                try {
                                    commonFriend_listView.removeFooterView(mFooterView);
                                    mainBuyerListViewAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    //发送handler，隐藏进度条
                                    mHandler.obtainMessage(SUCCESS).sendToTarget();
                                }
                            }
                        }
                        //发送handler，隐藏进度条
                        mHandler.obtainMessage(SUCCESS).sendToTarget();
                    }
                } catch (Exception e) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    if (in_commonFriend_pf != null && commonItemIsFromRefresh) {
                        in_commonFriend_pf.refreshComplete();
                    }
                    if (commonFriend_listView.getFooterViewsCount() == 1) {
                        try {
                            commonFriend_listView.removeFooterView(mFooterView);
                            mainBuyerListViewAdapter.notifyDataSetChanged();
                        } catch (Exception ex) {
                            //发送handler，隐藏进度条
                            mHandler.obtainMessage(SUCCESS).sendToTarget();
                        }
                    }
                    //发送handler，隐藏进度条
                    mHandler.obtainMessage(SUCCESS).sendToTarget();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        offset = 0;
    }
}
