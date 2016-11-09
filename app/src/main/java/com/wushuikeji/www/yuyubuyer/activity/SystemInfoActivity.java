package com.wushuikeji.www.yuyubuyer.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.SystemInfoListViewAdapter;
import com.wushuikeji.www.yuyubuyer.bean.SystemInfoBean;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.ContentSystemMessParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.mylibrary.PullToRefreshSwipeMenuListView;
import com.wushuikeji.www.yuyubuyer.mylibrary.pulltorefresh.interfaces.IXListViewListener;
import com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.bean.SwipeMenu;
import com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.bean.SwipeMenuItem;
import com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.interfaces.OnMenuItemClickListener;
import com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.interfaces.SwipeMenuCreator;
import com.wushuikeji.www.yuyubuyer.mylibrary.util.RefreshTime;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.ConvertUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToNextActivityUtils;
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

public class SystemInfoActivity extends AppCompatActivity {

    @InjectView(R.id.rl_systemInfo_back)
    RelativeLayout rl_systemInfo_back;

    //空视图
    @InjectView(R.id.tv_systemInfo_empty)
    TextView tv_systemInfo_empty;

    //进度条
    @InjectView(R.id.pb_systemLoading_progressBar)
    ProgressBar pb_systemLoading_progressBar;

    @InjectView(R.id.systemInfo_listView)
    PullToRefreshSwipeMenuListView systemInfo_listView;

    private View mFooterView;
    private List<SystemInfoBean> systemInfoList = new ArrayList<SystemInfoBean>();

    private boolean isBottom;//判断listView是否底部

    //系统消息
    private String sysMessUrl = Constants.commontUrl + "message/sys_msg_list";

    //发送删除的请求
    private String deleteSysMessUrl = Constants.commontUrl + "message/delete_sys_msg";

    private int count = 10;

    private int offset;

    private SystemInfoListViewAdapter mSystemInfoListViewAdapter;
    private SwipeMenuCreator mCreator;

    private static final int LOADING = 0;
    private static final int SUCCESS = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_systemLoading_progressBar.setVisibility(View.VISIBLE);
                    tv_systemInfo_empty.setVisibility(View.GONE);
                    systemInfo_listView.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    //有数据
                    if (systemInfoList.size() != 0) {
                        pb_systemLoading_progressBar.setVisibility(View.GONE);
                        tv_systemInfo_empty.setVisibility(View.GONE);
                        systemInfo_listView.setVisibility(View.VISIBLE);
                        //更新数据
                        if (mSystemInfoListViewAdapter != null) {
                            mSystemInfoListViewAdapter.notifyDataSetChanged();
                        }
                    } else {
                        pb_systemLoading_progressBar.setVisibility(View.GONE);
                        tv_systemInfo_empty.setVisibility(View.VISIBLE);
                        systemInfo_listView.setVisibility(View.GONE);
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
        setContentView(R.layout.activity_system_info);
        initView();
        initData();
        initEvent();

    }

    private void initView() {
        ButterKnife.inject(this);

        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");

        //去除系统自带的颜色渐变
        systemInfo_listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mSystemInfoListViewAdapter = new SystemInfoListViewAdapter(systemInfoList);
        systemInfo_listView.setAdapter(mSystemInfoListViewAdapter);

        systemInfo_listView.setPullRefreshEnable(true);
        systemInfo_listView.setPullLoadEnable(true);

        //添加侧滑图片和文字的操作
        SwipeMenuCreator mCreator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(UIUtils.getContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                deleteItem.setBackground(R.color.colorMainTitle);
                // set item width
                deleteItem.setWidth(ConvertUtils.dp2px(60, UIUtils.getContext()));
                deleteItem.setTitle("删除");
                deleteItem.setTitleSize(14);
                deleteItem.setTitleColor(Color.WHITE);
                // set a icon
                deleteItem.setIcon(R.mipmap.delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        systemInfo_listView.setMenuCreator(mCreator);
    }

    private void initData() {
        //判断网络
        if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            //有网
            loadNetworkData(true, mUserId, "0", false, false);
        }
    }

    private void initEvent() {
        //返回键
        BackListenerUtils.backFinish(rl_systemInfo_back,this);

        //侧滑点击删除
        systemInfo_listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        //进度条
                        pb_systemLoading_progressBar.setVisibility(View.VISIBLE);
                        requestDeleteSystemMess(position);
                        break;
                }
            }
        });
        //刷新与加载更多的监听
        systemInfo_listView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                            return;
                        } else {
                            //有网
                            offset = 0;
                            loadNetworkData(false, mUserId, "0", true, false);
                        }
                    }
                }, 150);
            }

            @Override
            public void onLoadMore() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                            return;
                        } else {
                            //网络请求
                            offset = offset + 10;
                            loadNetworkData(false, mUserId, String.valueOf(offset), false, true);
                        }
                    }
                }, 150);
            }
        });

        //item
        systemInfo_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //position从1开始
                if (position != 0) {
                    ToNextActivityUtils.toNextActivityNotFinishAndParameters(SystemInfoActivity.this, SystemInfoDetailsActivity.class,"messageId",systemInfoList.get(position - 1).sysMessId);
                }
            }
        });
    }

    /**
     * 加载网络的数据
     */
    private void loadNetworkData(boolean isNeedLoading, String user_id, String mOffset, boolean isFromRefresh, boolean isFromLoadMore) {
        final boolean itemIsFromRefresh = isFromRefresh;
        final boolean itemIsFromLoadMore = isFromLoadMore;
        if (isNeedLoading) {
            //进度条展示
            mHandler.obtainMessage(LOADING).sendToTarget();
        }
        OkHttpUtils.post().url(sysMessUrl).addParams("user_id", user_id).addParams("offset", mOffset)
                .addParams("count", String.valueOf(count)).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                try {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    loadAndRefreshComple(systemInfo_listView);
                    //发送handler，隐藏进度条
                    mHandler.obtainMessage(SUCCESS).sendToTarget();
                } catch (Exception exception) {
                    loadAndRefreshComple(systemInfo_listView);
                }
            }

            @Override
            public void onResponse(String response, int id) {

                operationResult(response, itemIsFromRefresh, itemIsFromLoadMore);
            }
        });
    }

    /**
     * 处理网络返回的结果
     */
    private void operationResult(String response, boolean isFromRefresh, boolean isFromLoadMore) {
        try {
            // 成功了，只是返回来请求的json数据,还需要解析
            Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
            String response_code = map.get("response_code");
            //需要判断各种响应码,获取具体错误信息
            String responseContent = RequestResponseUtils.getResponseContent(response_code);

            if ("0".equals(response_code)) {

                List<SystemInfoBean> itemList = ContentSystemMessParse.systemMessParse(response);
                if (itemList != null && itemList.size() > 0) {
                    systemInfoList.clear();
                    systemInfoList.addAll(itemList);
                    if (isFromRefresh) {
                        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
                        RefreshTime.setRefreshTime(UIUtils.getContext(), df.format(new Date()));
                        loadAndRefreshComple(systemInfo_listView);
                    }
                    if (mSystemInfoListViewAdapter != null) {
                        mSystemInfoListViewAdapter.notifyDataSetChanged();
                    }
                }
            } else if (responseContent != null) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                loadAndRefreshComple(systemInfo_listView);
            }
            if (isFromLoadMore) {
                loadAndRefreshComple(systemInfo_listView);
            }
            //发送handler，隐藏进度条
            mHandler.obtainMessage(SUCCESS).sendToTarget();
        } catch (Exception e) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
            loadAndRefreshComple(systemInfo_listView);
            //发送handler，隐藏进度条
            mHandler.obtainMessage(SUCCESS).sendToTarget();
        }
    }

    /**
     * 停止刷新和加载
     */
    private void loadAndRefreshComple(PullToRefreshSwipeMenuListView mListView) {
        mListView.setRefreshTime(RefreshTime.getRefreshTime(UIUtils.getContext()));
        mListView.stopRefresh();
        mListView.stopLoadMore();
    }

    /**
     * 删除后，告诉服务器
     */
    private void requestDeleteSystemMess(int position) {
        final int itemPosition = position;
        //判断网络
        boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
        if (!iSNetworkConnect) {
            pb_systemLoading_progressBar.setVisibility(View.GONE);
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            OkHttpUtils.post().url(deleteSysMessUrl).addParams("message_id", systemInfoList.get(itemPosition).sysMessId)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    pb_systemLoading_progressBar.setVisibility(View.GONE);
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        // 成功了，只是返回来请求的json数据,还需要解析
                        Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                        String response_code = map.get("response_code");
                        if ("0".equals(response_code)) {
                            systemInfoList.remove(itemPosition);
                            mSystemInfoListViewAdapter.notifyDataSetChanged();
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "删除成功");
                        } else {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "删除失败");
                        }
                        pb_systemLoading_progressBar.setVisibility(View.GONE);
                    } catch (Exception e) {
                        pb_systemLoading_progressBar.setVisibility(View.GONE);
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    }
                }
            });
        }
    }
}
