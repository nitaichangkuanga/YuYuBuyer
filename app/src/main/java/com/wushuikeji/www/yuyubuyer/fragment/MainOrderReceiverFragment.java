package com.wushuikeji.www.yuyubuyer.fragment;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.MainOrderListViewAdapter;
import com.wushuikeji.www.yuyubuyer.base.BaseFragment;
import com.wushuikeji.www.yuyubuyer.bean.OrderBean;
import com.wushuikeji.www.yuyubuyer.mylibrary.PullToRefreshSwipeMenuListView;
import com.wushuikeji.www.yuyubuyer.mylibrary.pulltorefresh.interfaces.IXListViewListener;
import com.wushuikeji.www.yuyubuyer.mylibrary.util.RefreshTime;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainOrderReceiverFragment extends BaseFragment {

    //空视图
    @InjectView(R.id.tv_shopCollect_empty)
    TextView tv_mainOrder_empty;

    @InjectView(R.id.shopCollect_listView)
    PullToRefreshSwipeMenuListView mainOrder_listView;

    //进度条
    @InjectView(R.id.pb_shopCollectLoading_progressBar)
    ProgressBar pb_mainOrderLoading_progressBar;

    private View mMainOrderctView;

    private boolean isBottom;//判断listView是否底部

    private MainOrderListViewAdapter mainOrderListViewAdapter;

    private List<OrderBean> orderBeanList = new ArrayList<OrderBean>();

    private static final int LOADING = 0;
    private static final int SUCCESS = 1;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_mainOrderLoading_progressBar.setVisibility(View.VISIBLE);
                    tv_mainOrder_empty.setVisibility(View.GONE);
                    mainOrder_listView.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    //有数据
                    if(orderBeanList.size() != 0) {
                        pb_mainOrderLoading_progressBar.setVisibility(View.GONE);
                        tv_mainOrder_empty.setVisibility(View.GONE);
                        mainOrder_listView.setVisibility(View.VISIBLE);
                        //更新数据
                        if(mainOrderListViewAdapter != null) {
                            mainOrderListViewAdapter.notifyDataSetChanged();
                        }
                    }else {
                        pb_mainOrderLoading_progressBar.setVisibility(View.GONE);
                        tv_mainOrder_empty.setVisibility(View.VISIBLE);
                        mainOrder_listView.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View initView() {

        //需要展示的View
        if(mMainOrderctView == null) {
            mMainOrderctView = View.inflate(UIUtils.getContext(), R.layout.shop_collect_fragment, null);
        }
        //注解
        ButterKnife.inject(this, mMainOrderctView);
        //去除系统自带的颜色渐变
        mainOrder_listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mainOrderListViewAdapter = new MainOrderListViewAdapter(orderBeanList);
        mainOrder_listView.setAdapter(mainOrderListViewAdapter);

        mainOrder_listView.setPullRefreshEnable(true);
        mainOrder_listView.setPullLoadEnable(true);
        //添加侧滑图片和文字的操作
        //        SwipeMenuCreator mCreator = new SwipeMenuCreator() {
        //            @Override
        //            public void create(SwipeMenu menu) {
        //                // create "delete" item
        //                SwipeMenuItem deleteItem = new SwipeMenuItem(UIUtils.getContext());
        //                // set item background
        //                deleteItem.setBackground(R.color.colorMainTitle);
        //                // set item width
        //                deleteItem.setWidth(ConvertUtils.dp2px(60,UIUtils.getContext()));
        //                deleteItem.setTitle("删除");
        //                deleteItem.setTitleSize(14);
        //                // set item title font color
        //                deleteItem.setTitleColor(Color.WHITE);
        //                // set a icon
        //                deleteItem.setIcon(R.mipmap.delete);
        //                // add to menu
        //                menu.addMenuItem(deleteItem);
        //            }
        //        };
        //        // set creator
        //        mainOrder_listView.setMenuCreator(mCreator);

        return mMainOrderctView;
    }

    @Override
    public void initData() {
        super.initData();
        //开启线程加载数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                //休眠一秒
                mHandler.obtainMessage(LOADING).sendToTarget();
                SystemClock.sleep(2000);
                //加载数据
                //模拟初始化数据
                moniData();
                //放送handler
                mHandler.obtainMessage(SUCCESS).sendToTarget();
            }
        }).start();
    }

    @Override
    public void initEvent() {
        super.initEvent();
        //空视图的点击事件
        tv_mainOrder_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //刷新与加载更多的监听
        mainOrder_listView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
                        RefreshTime.setRefreshTime(UIUtils.getContext(), df.format(new Date()));
                        updateData();
                    }
                }, 1500);
            }

            @Override
            public void onLoadMore() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNewData();
                    }
                }, 1000);
            }
        });
    }

    /**
     * 下拉加载更多的数据显示
     */
    private void loadNewData() {
        moniData();
        loadAndRefreshComple(mainOrder_listView);
        mainOrderListViewAdapter.notifyDataSetChanged();
    }

    private void moniData() {
        Random random = null;
        for (int i = 0; i < 10; i++) {
            OrderBean mOrderBean = new OrderBean();
            random = new Random();
            mOrderBean.type = "视频" + i;
            mOrderBean.tradeTime = random.nextInt(3) + "小时" + random.nextInt(60) + "分钟";
            mOrderBean.tradeMoney = "￥" + random.nextInt(100);
            orderBeanList.add(mOrderBean);
        }
    }

    /**
     * 真正刷新数据的方法
     */
    private void updateData() {
        //清空数据源
        orderBeanList.clear();
        //添加数据到数据源
        moniData();
        //刷新完成
        loadAndRefreshComple(mainOrder_listView);

        mainOrderListViewAdapter.notifyDataSetChanged();
    }

    private void loadAndRefreshComple(PullToRefreshSwipeMenuListView mListView) {
        mListView.setRefreshTime(RefreshTime.getRefreshTime(UIUtils.getContext()));
        mListView.stopRefresh();
        mListView.stopLoadMore();
    }
}
