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
import com.wushuikeji.www.yuyubuyer.adapter.ToUpRecordListViewAdapter;
import com.wushuikeji.www.yuyubuyer.bean.ToUpRecordBean;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.ToUpRecordListParse;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.ListViewUtils;
import com.wushuikeji.www.yuyubuyer.utils.PtrFrameRefreshUtils;
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
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import okhttp3.Call;

public class MoneyRecordActivity extends AppCompatActivity {

    @InjectView(R.id.rl_moneyRecord_back)
    RelativeLayout rl_moneyRecord_back;

    //空视图
    @InjectView(R.id.tv_moneyRecord_emptyView)
    TextView tv_moneyRecord_emptyView;

    @InjectView(R.id.returnApply_listView)
    ListView returnApply_listView;

    //刷新的类
    @InjectView(R.id.in_returnApply_pf)
    PtrClassicFrameLayout in_returnApply_pf;

    //进度条
    @InjectView(R.id.pb_moneyRecordLoading_progressBar)
    ProgressBar pb_moneyRecordLoading_progressBar;

    private ToUpRecordListViewAdapter mToUpRecordListViewAdapter;

    private List<ToUpRecordBean> toUpRecordBeanList = new ArrayList<ToUpRecordBean>();

    private View mFooterView;

    private boolean isBottom;//判断listView是否底部

    private int count = 10;
    private int offset;

    //充值记录列表的地址
    private String upToRecordListUrl = Constants.commontUrl + "recharge/list";

    private static final int LOADING = 0;
    private static final int SUCCESS = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_moneyRecordLoading_progressBar.setVisibility(View.VISIBLE);
                    tv_moneyRecord_emptyView.setVisibility(View.GONE);
                    in_returnApply_pf.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    if (toUpRecordBeanList.size() != 0) {
                        pb_moneyRecordLoading_progressBar.setVisibility(View.GONE);
                        tv_moneyRecord_emptyView.setVisibility(View.GONE);
                        in_returnApply_pf.setVisibility(View.VISIBLE);
                        //更新数据
                        if (mToUpRecordListViewAdapter != null) {
                            mToUpRecordListViewAdapter.notifyDataSetChanged();
                        }
                    } else {
                        pb_moneyRecordLoading_progressBar.setVisibility(View.GONE);
                        tv_moneyRecord_emptyView.setVisibility(View.VISIBLE);
                        in_returnApply_pf.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_record);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);
        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");
        //将尾部布局蹦出来
        mFooterView = View.inflate(UIUtils.getContext(), R.layout.item_listview_mainshop_loadmore, null);
        //去除系统自带的颜色渐变
        returnApply_listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mToUpRecordListViewAdapter = new ToUpRecordListViewAdapter(toUpRecordBeanList);

        returnApply_listView.setAdapter(mToUpRecordListViewAdapter);
        //刷新的操作
        in_returnApply_pf.setLastUpdateTimeRelateObject(this);
        PtrFrameRefreshUtils.setRefreshParams(in_returnApply_pf);
    }

    private void initData() {
        if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        }else {
            loadNetworkData("0", true, false, true);
        }
    }

    private void initEvent() {
        BackListenerUtils.backFinish(rl_moneyRecord_back,this);
        //刷新
        in_returnApply_pf.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                return PtrDefaultHandler.checkContentCanBePulledDown(frame, returnApply_listView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    return;
                } else {
                    //需要清0
                    offset = 0;
                    loadNetworkData("0", false, true, true);
                    //防止加载更多的时候，已经加了尾部布局，所以，先判断如果数据源小于8个,并且有尾部就删除
                    if (toUpRecordBeanList.size() <= 8 && returnApply_listView.getFooterViewsCount() == 1) {
                        try {
                            returnApply_listView.removeFooterView(mFooterView);
                            mToUpRecordListViewAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        });
        //listView的滑动事件
        returnApply_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 数据已经到底部，而且用户是松手状态，这个时候需要加载新的数据
                if (isBottom && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                    if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                        return;
                    }else {
                        offset = offset + 10;
                        loadNetworkData(String.valueOf(offset), false, false, false);
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
        returnApply_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToNextActivityUtils.toNextActivityNotFinishAndParameters(MoneyRecordActivity.this, MoneyRecordDetailsActivity.class, "toUpRecordId", toUpRecordBeanList.get(position).toUpRecordId);
            }
        });
    }

    private void loadNetworkData(String offset, boolean isNeedLoading, boolean isFromRefresh, boolean isNeedClearData) {

        final boolean commonItemIsFromRefresh = isFromRefresh;
        final boolean commonItemIsNeedClearData = isNeedClearData;

        if (isNeedLoading) {
            //进度条展示
            mHandler.obtainMessage(LOADING).sendToTarget();
        }

        OkHttpUtils.post().url(upToRecordListUrl).addParams("user_id", mUserId)
                .addParams("offset", offset)
                .addParams("count", String.valueOf(count))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                if (in_returnApply_pf != null && commonItemIsFromRefresh) {
                    in_returnApply_pf.refreshComplete();
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
                        List<ToUpRecordBean> tempList = ToUpRecordListParse.toUpRecordListParse(response);

                        if (tempList != null) {
                            if (commonItemIsNeedClearData) {
                                toUpRecordBeanList.clear();
                                toUpRecordBeanList.addAll(tempList);
                                ListViewUtils.isAddListViewToUpFooterView(toUpRecordBeanList, returnApply_listView, mFooterView);
                            } else {
                                //加载更多
                                if (tempList.size() == 0) {
                                    if (returnApply_listView.getFooterViewsCount() == 1) {
                                        try {
                                            returnApply_listView.removeFooterView(mFooterView);
                                            mToUpRecordListViewAdapter.notifyDataSetChanged();
                                        } catch (Exception e) {
                                            //发送handler，隐藏进度条
                                            mHandler.obtainMessage(SUCCESS).sendToTarget();
                                        }
                                    }
                                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "没有数据了");
                                } else {
                                    toUpRecordBeanList.addAll(tempList);
                                    ListViewUtils.isAddListViewToUpFooterView(toUpRecordBeanList, returnApply_listView, mFooterView);
                                }
                            }
                            //刷新完成
                            if (in_returnApply_pf != null && commonItemIsFromRefresh) {
                                in_returnApply_pf.refreshComplete();
                            }
                        }
                        //发送handler，隐藏进度条
                        mHandler.obtainMessage(SUCCESS).sendToTarget();
                    } else if (responseContent != null) {
                        //给出错误提示
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                        if (in_returnApply_pf != null && commonItemIsFromRefresh) {
                            in_returnApply_pf.refreshComplete();
                        }
                        if (MyConstants.FOOTVIEWNAME.equals(responseContent)) {
                            if (returnApply_listView.getFooterViewsCount() == 1) {
                                try {
                                    returnApply_listView.removeFooterView(mFooterView);
                                    mToUpRecordListViewAdapter.notifyDataSetChanged();
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
                    if (in_returnApply_pf != null && commonItemIsFromRefresh) {
                        in_returnApply_pf.refreshComplete();
                    }
                    if (returnApply_listView.getFooterViewsCount() == 1) {
                        try {
                            returnApply_listView.removeFooterView(mFooterView);
                            mToUpRecordListViewAdapter.notifyDataSetChanged();
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
}
