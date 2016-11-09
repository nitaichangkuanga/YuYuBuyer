package com.wushuikeji.www.yuyubuyer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.DynamicListViewAdapter;
import com.wushuikeji.www.yuyubuyer.bean.jsonbean.DynamicBean;
import com.wushuikeji.www.yuyubuyer.mylibrary.PullToRefreshSwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public class DynamicListActivity extends AppCompatActivity {

    @InjectView(R.id.rl_dynamicEvaluate_back)
    RelativeLayout rl_dynamicEvaluate_back;

    //进度条
    @InjectView(R.id.pb_evaluateLoading_progressBar)
    ProgressBar pb_evaluateLoading_progressBar;

    @InjectView(R.id.dynamicEvaluate_listView)
    PullToRefreshSwipeMenuListView dynamicEvaluate_listView;

    private List<DynamicBean> dynamicEvaluateList = new ArrayList<DynamicBean>();

    private DynamicListViewAdapter mDynamicListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
//        setContentView(R.layout.activity_dynamic_evaluate);
//        initView();
//        initData();
//        initEvent();
//    }
//    private void initView() {
//        ButterKnife.inject(this);
//
//        //去除系统自带的颜色渐变
//        dynamicEvaluate_listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
//        //将尾部布局蹦出来
////        if(mFooterView == null) {
////            mFooterView = View.inflate(UIUtils.getContext(), R.layout.item_listview_mainshop_loadmore, null);
////        }
////
////        //必须在setAdapter之前
////        //加之前，先判断数据源有几个，不够一屏幕显示，不加入
////        ListViewUtils.isAddListViewSystemInfoFooterView(dynamicEvaluateList,dynamicEvaluate_listView,mFooterView);
//        mDynamicEvaluateListViewAdapter = new FriendInfoListViewAdapter(dynamicEvaluateList);
//        dynamicEvaluate_listView.setAdapter(mDynamicEvaluateListViewAdapter);
//
//        dynamicEvaluate_listView.setPullRefreshEnable(true);
//        dynamicEvaluate_listView.setPullLoadEnable(true);
//
//        //添加侧滑图片和文字的操作
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
//
//                // set a icon
//                deleteItem.setIcon(R.mipmap.delete);
//                // add to menu
//                menu.addMenuItem(deleteItem);
//            }
//        };
//        // set creator
//        dynamicEvaluate_listView.setMenuCreator(mCreator);
//    }
//
//    private void initData() {
//        //开启线程加载数据
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //休眠一秒
//                mHandler.obtainMessage(LOADING).sendToTarget();
//                SystemClock.sleep(2000);
//                //加载数据
//                //模拟初始化数据
//                moniData();
//                //放送handler
//                mHandler.obtainMessage(SUCCESS).sendToTarget();
//            }
//        }).start();
//
//
//    }
//
//    private void initEvent() {
//        //返回键
//        rl_dynamicEvaluate_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        //空视图的点击事件
//        tv_dynamicEvaluate_empty.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        //listView的滑动事件
////        dynamicEvaluate_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
////            @Override
////            public void onScrollStateChanged(AbsListView view, int scrollState) {
////                // 数据已经到底部，而且用户是松手状态，这个时候需要加载新的数据
////                if (isBottom && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
////                    loadNewData();
////                }
////            }
////
////            @Override
////            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
////                // 判读是否需要加载新的数据
////                isBottom = (firstVisibleItem + visibleItemCount) == totalItemCount;
////            }
////        });
//        //侧滑点击删除
//        dynamicEvaluate_listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//            @Override
//            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
//                switch (index) {
//                    case 0:
//                        dynamicEvaluateList.remove(position);
//                        mDynamicEvaluateListViewAdapter.notifyDataSetChanged();
//                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"删除了" +position);
//                        break;
//                }
//            }
//        });
//        //刷新与加载更多的监听
//        dynamicEvaluate_listView.setXListViewListener(new IXListViewListener() {
//            @Override
//            public void onRefresh() {
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
//                        RefreshTime.setRefreshTime(getApplicationContext(), df.format(new Date()));
//                        updateData();
//                    }
//                }, 2000);
//            }
//
//            @Override
//            public void onLoadMore() {
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        loadNewData();
//                    }
//                }, 2000);
//            }
//        });
//    }
//
//    private void loadAndRefreshComple(PullToRefreshSwipeMenuListView mListView) {
//        mListView.setRefreshTime(RefreshTime.getRefreshTime(getApplicationContext()));
//        mListView.stopRefresh();
//        mListView.stopLoadMore();
//    }
//
//    /**
//     * 下拉加载更多的数据显示
//     */
//    private void loadNewData() {
//        SystemClock.sleep(1000);
//        moniData();
//        loadAndRefreshComple(dynamicEvaluate_listView);
//        mDynamicEvaluateListViewAdapter.notifyDataSetChanged();
//    }
//
//    /**
//     * 模拟数据
//     */
//    private void moniData() {
//        Random random = null;
//        for (int i = 0; i < 10; i++) {
//            SystemInfoBean mFriendInfoBean = new SystemInfoBean();
//            random = new Random();
//            mFriendInfoBean.imgUrl = String.valueOf(R.mipmap.logo);
//            mFriendInfoBean.friendName = "半夏微凉";
//            mFriendInfoBean.content = "写的太棒了，正是我需要，太棒了!";
//            mFriendInfoBean.date = "2016-06-"+random.nextInt(20);
//            mFriendInfoBean.time = random.nextInt(20)+":00:00";
//            dynamicEvaluateList.add(mFriendInfoBean);
//        }
//    }
//
//    /**
//     * 真正刷新数据的方法
//     */
//    private void updateData() {
//        //tv_systeminfo_empty.setVisibility(View.GONE);
//        //清空数据源
//        dynamicEvaluateList.clear();
//        //添加数据到数据源
//        moniData();
//        loadAndRefreshComple(dynamicEvaluate_listView);
//        //ListViewUtils.isAddListViewSystemInfoFooterView(dynamicEvaluateList,dynamicEvaluate_listView,mFooterView);
//        //刷新完成
//        mDynamicEvaluateListViewAdapter.notifyDataSetChanged();
//    }
}
