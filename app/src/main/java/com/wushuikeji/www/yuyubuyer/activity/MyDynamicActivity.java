package com.wushuikeji.www.yuyubuyer.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.DynamicListViewAdapter;
import com.wushuikeji.www.yuyubuyer.bean.jsonbean.DynamicBean;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.DynamicParse;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

public class MyDynamicActivity extends AppCompatActivity {

    @InjectView(R.id.rl_md_back)
    RelativeLayout rl_md_back;

    @InjectView(R.id.ib_myDynamic_add)
    ImageView ib_myDynamic_add;

    @InjectView(R.id.pb_md_progressBar)
    ProgressBar pb_md_progressBar;

    @InjectView(R.id.md_listView)
    PullToRefreshSwipeMenuListView md_listView;

    @InjectView(R.id.rl_md_add)
    RelativeLayout rl_md_add;

    private List<DynamicBean> dynamicList = new ArrayList<DynamicBean>();

    private DynamicListViewAdapter mDynamicListViewAdapter;

    //检查发表动态的次数是否超过限制
    private String checkSendCountUrl = Constants.commontUrl + "trend/check_num";

    public static MyDynamicActivity mMyDynamicActivity;

    private String mUserId = new String();

    //动态列表的url
    private String dynamicUrl = Constants.commontUrl + "trend/list";

    //发送删除的请求
    private String deleteDynamicUrl = Constants.commontUrl + "trend/delete";

    private int count = 10;

    private int offset;

    private boolean isFromOther;
    private boolean mIsLoginBoolean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dynamic);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);

        mMyDynamicActivity = this;

        //查看用户是否登陆过
        mIsLoginBoolean = SpUtils.getCacheBoolean(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.ISLOGINSTATUS, false);

        //去除系统自带的颜色渐变
        md_listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mDynamicListViewAdapter = new DynamicListViewAdapter(dynamicList, UIUtils.getContext());
        md_listView.setAdapter(mDynamicListViewAdapter);

        md_listView.setPullRefreshEnable(true);
        md_listView.setPullLoadEnable(true);

        //得到fromWhere,判断是从我的动态 还是从buyer详情列表里进来的
        Intent intent = getIntent();
        String fromWhere = intent.getStringExtra("fromWhere");
        if (!TextUtils.isEmpty(fromWhere)) {
            if ("other".equals(fromWhere)) {
                //从buyer详情列表里进来的
                ib_myDynamic_add.setVisibility(View.GONE);
                isFromOther = true;
                //得到buyerId
                mUserId = intent.getStringExtra("buyerId");
            } else if ("me".equals(fromWhere)) {
                //从我的动态
                ib_myDynamic_add.setVisibility(View.VISIBLE);
                isFromOther = false;
                //得到用户ID
                mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");
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
                md_listView.setMenuCreator(mCreator);
            }
        }
    }

    private void initData() {
        //判断网络
        boolean mISNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
        if (!mISNetworkConnect) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            //有网
            loadNetworkData(true, mUserId, "0", false, false);
        }
    }

    private void initEvent() {
        //返回
        BackListenerUtils.backFinish(rl_md_back, this);

        if (!isFromOther) {
            //添加动态
            rl_md_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //需要检查发表动态的次数，每天只能发表一次
                    requestNetworkCheck();
                }
            });
        }

        if (!isFromOther) {
            //侧滑点击删除
            md_listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                    switch (index) {
                        case 0:
                            //弹出对话框
                            customIsDelete(position);
                            break;
                    }
                }
            });
        }

        //item
        md_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mIsLoginBoolean) {
                    //进入登录界面
                    ToNextActivityUtils.toNextAndNoFinishActivity(MyDynamicActivity.this, LoginActivity.class);
                    return;
                }
                //position从1开始
                if (position != 0) {
                    Intent intent = new Intent(MyDynamicActivity.this, MyDynamicDetailsActivity.class);
                    intent.putExtra("trendId", dynamicList.get(position - 1).trendId);
                    intent.putExtra("imgUrl", dynamicList.get(position - 1).imgUrl);
                    intent.putExtra("userName", dynamicList.get(position - 1).userName);
                    intent.putExtra("date", dynamicList.get(position - 1).date);
                    intent.putExtra("time", dynamicList.get(position - 1).time);
                    intent.putExtra("content", dynamicList.get(position - 1).content);
                    intent.putExtra("videoUrl", dynamicList.get(position - 1).videoUrl);
                    startActivity(intent);
                }
            }
        });

        //刷新与加载更多的监听
        md_listView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
                if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    return;
                } else {
                    //有网
                    offset = 0;
                    loadNetworkData(false, mUserId, "0", true, false);
                }
            }

            @Override
            public void onLoadMore() {
                if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    return;
                } else {
                    //网络请求
                    offset = offset + 10;
                    loadNetworkData(false, mUserId, String.valueOf(offset), false, true);
                }
            }
        });

    }

    /**
     * 请求网络检查视频动态是否可以再次发表
     */
    private void requestNetworkCheck() {
        //日期
        String currentData = getCurrentData();
        if (currentData != null) {
            OkHttpUtils.post().url(checkSendCountUrl).addParams("user_id", mUserId)
                    .addParams("trend_date", currentData).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        // 成功了，只是返回来请求的json数据,还需要解析
                        Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                        String response_code = map.get("response_code");
                        //需要判断各种响应码,获取具体错误信息
                        String responseContent = RequestResponseUtils.getResponseContent(response_code);
                        if ("0".equals(response_code)) {
                            //可以发表动态，进入发表的界面
                            ToNextActivityUtils.toNextAndNoFinishActivity(MyDynamicActivity.this, AddDynamicActivity.class);
                        } else if (!TextUtils.isEmpty(response_code)) {
                            customDialog(response_code);
                        }
                    } catch (Exception e) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    }
                }
            });
        }
    }

    /**
     * 得到当前的日期  20160717
     */
    public String getCurrentData() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);

        int month = calendar.get(Calendar.MONTH) + 1;

        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "" + month + dayOfMonth;
    }

    /**
     * 弹出每天超过限制
     */
    private void customDialog(String response_code) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MyDynamicActivity.this);

        View inflate = View.inflate(MyDynamicActivity.this, R.layout.custom_dialog_send, null);

        //title
        TextView tv_send_title = (TextView) inflate.findViewById(R.id.tv_send_title);

        if ("4300".equals(response_code)) {
            tv_send_title.setText("您今天已经发布过一条动态");
        } else if ("4301".equals(response_code)) {
            tv_send_title.setText("您已经达到最大发布动态的上限");
        }
        //OK
        TextView decidedButton = (TextView) inflate.findViewById(R.id.tv_send_ok);
        mBuilder.setView(inflate);
        final AlertDialog alertDialog = mBuilder.create();
        alertDialog.show();

        decidedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
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
            pb_md_progressBar.setVisibility(View.VISIBLE);
        }
        OkHttpUtils.get().url(dynamicUrl).addParams("user_id", user_id).addParams("offset", mOffset)
                .addParams("count", String.valueOf(count)).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                loadAndRefreshComple(md_listView);
                //隐藏进度条
                pb_md_progressBar.setVisibility(View.GONE);
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
            final String responseContent = RequestResponseUtils.getResponseContent(response_code);

            if ("0".equals(response_code)) {

                List<DynamicBean> itemList = DynamicParse.dynamicParse(response);

                if (itemList != null && itemList.size() > 0 && isFromLoadMore) {
                    dynamicList.addAll(itemList);
                    if (mDynamicListViewAdapter != null) {
                        mDynamicListViewAdapter.notifyDataSetChanged();
                    }
                } else {
                    dynamicList.clear();
                    dynamicList.addAll(itemList);
                    mDynamicListViewAdapter.notifyDataSetChanged();
                }
                if (isFromRefresh) {
                    SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
                    RefreshTime.setRefreshTime(UIUtils.getContext(), df.format(new Date()));
                }
                pb_md_progressBar.setVisibility(View.GONE);
                UIUtils.getMainThreadHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadAndRefreshComple(md_listView);
                    }
                }, 1000);
            } else if (responseContent != null) {
                pb_md_progressBar.setVisibility(View.GONE);
                UIUtils.getMainThreadHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadAndRefreshComple(md_listView);
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                    }
                }, 1000);
                //loadAndRefreshComple(md_listView);
                //如果最后的数据大于3，才显示尾巴
                //                if(dynamicList.size() >= 3) {
                //                    md_listView.getFootView().setVisibility(View.VISIBLE);
                //                }else {
                //                    md_listView.getFootView().setVisibility(View.GONE);
                //                }
            }
            //            if(isFromLoadMore) {
            //                loadAndRefreshComple(md_listView);
            //            }

        } catch (Exception e) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
            loadAndRefreshComple(md_listView);
            //隐藏进度条
            pb_md_progressBar.setVisibility(View.GONE);
        }
    }

    private void loadAndRefreshComple(PullToRefreshSwipeMenuListView mListView) {
        if (mListView != null) {
            mListView.setRefreshTime(RefreshTime.getRefreshTime(UIUtils.getContext()));
            mListView.stopRefresh();
            mListView.stopLoadMore();
        }
    }

    /**
     * 给外调用我自动刷新
     */
    public void refreshData(String userId) {
        loadNetworkData(true, userId, "0", false, false);
    }

    /**
     * 删除后，告诉服务器
     */
    private void requestDelete(final int position) {
        final int tempPosition = position;
        //判断网络
        boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
        if (!iSNetworkConnect) {
            pb_md_progressBar.setVisibility(View.GONE);
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            DynamicBean dynamicBean = dynamicList.get(tempPosition);
            if (dynamicBean != null) {

                OkHttpUtils.post().url(deleteDynamicUrl)
                        .addParams("trend_id", dynamicBean.trendId)
                        .build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        pb_md_progressBar.setVisibility(View.GONE);
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            // 成功了，只是返回来请求的json数据,还需要解析
                            Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                            String response_code = map.get("response_code");
                            if ("0".equals(response_code)) {
                                dynamicList.remove(tempPosition);
                                mDynamicListViewAdapter.notifyDataSetChanged();
                                ToastUtils.showToastInUIThread(UIUtils.getContext(), "删除成功");
                            } else {
                                ToastUtils.showToastInUIThread(UIUtils.getContext(), "删除失败");
                            }
                            pb_md_progressBar.setVisibility(View.GONE);
                        } catch (Exception e) {
                            pb_md_progressBar.setVisibility(View.GONE);
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                        }
                    }
                });
            }else {
                pb_md_progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void customIsDelete(int position) {

        final int itemDialogPosition = position;
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MyDynamicActivity.this);
        View inflate = View.inflate(MyDynamicActivity.this, R.layout.custom_dialog_delete, null);
        //取消
        Button canleButton = (Button) inflate.findViewById(R.id.b_delete_canle);
        //确定
        Button decidedButton = (Button) inflate.findViewById(R.id.b_delete_decided);
        mBuilder.setView(inflate);
        final AlertDialog alertDialog = mBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        canleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        decidedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                //进度条展示
                pb_md_progressBar.setVisibility(View.VISIBLE);
                //请求网络
                requestDelete(itemDialogPosition);
            }
        });
    }
}