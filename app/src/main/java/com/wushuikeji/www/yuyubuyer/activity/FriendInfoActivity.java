package com.wushuikeji.www.yuyubuyer.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.FriendInfoListViewAdapter;
import com.wushuikeji.www.yuyubuyer.bean.SystemInfoBean;
import com.wushuikeji.www.yuyubuyer.mylibrary.PullToRefreshSwipeMenuListView;
import com.wushuikeji.www.yuyubuyer.mylibrary.pulltorefresh.interfaces.IXListViewListener;
import com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.bean.SwipeMenu;
import com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.bean.SwipeMenuItem;
import com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.interfaces.OnMenuItemClickListener;
import com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.interfaces.SwipeMenuCreator;
import com.wushuikeji.www.yuyubuyer.mylibrary.util.RefreshTime;
import com.wushuikeji.www.yuyubuyer.utils.ConvertUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToNextActivityUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.wushuikeji.www.yuyubuyer.view.CircularProgress;
import com.wushuikeji.www.yuyubuyer.view.ClearEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FriendInfoActivity extends AppCompatActivity {

    @InjectView(R.id.rl_friendinfo_back)
    RelativeLayout rl_friendinfo_back;

    //空视图
    @InjectView(R.id.tv_friendinfo_empty)
    TextView tv_friendinfo_empty;

    //搜索框
    @InjectView(R.id.et_friendInfo_head)
    ClearEditText et_friendInfo_head;

    @InjectView(R.id.friendinfo_listView)
    PullToRefreshSwipeMenuListView friendInfo_listView;

    //进度条
    @InjectView(R.id.pb_friendLoading_progressBar)
    CircularProgress pb_friendLoading_progressBar;

    private View mFooterView;
    private List<SystemInfoBean> friendInfoList = new ArrayList<SystemInfoBean>();

    private boolean isBottom;//判断listView是否底部

    private FriendInfoListViewAdapter mFriendInfoListViewAdapter;

    private static final int LOADING = 0;
    private static final int SUCCESS = 1;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_friendLoading_progressBar.setVisibility(View.VISIBLE);
                    tv_friendinfo_empty.setVisibility(View.GONE);
                    friendInfo_listView.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    //有数据
                    if(friendInfoList.size() != 0) {
                        pb_friendLoading_progressBar.setVisibility(View.GONE);
                        tv_friendinfo_empty.setVisibility(View.GONE);
                        friendInfo_listView.setVisibility(View.VISIBLE);
                        //更新数据
                        if(mFriendInfoListViewAdapter != null) {
                            mFriendInfoListViewAdapter.notifyDataSetChanged();
                        }
                    }else {
                        pb_friendLoading_progressBar.setVisibility(View.GONE);
                        tv_friendinfo_empty.setVisibility(View.VISIBLE);
                        friendInfo_listView.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private List<SystemInfoBean> mFilterDateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        initView();
        initData();
        initEvent();
    }
    private void initView() {
        ButterKnife.inject(this);

        //去除系统自带的颜色渐变
        friendInfo_listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        //将尾部布局蹦出来
//        if(mFooterView == null) {
//            mFooterView = View.inflate(UIUtils.getContext(), R.layout.item_listview_mainshop_loadmore, null);
//        }
//        //必须在setAdapter之前
//        //加之前，先判断数据源有几个，不够一屏幕显示，不加入
//        ListViewUtils.isAddListViewSystemInfoFooterView(friendInfoList,friendInfo_listView,mFooterView);
        mFriendInfoListViewAdapter = new FriendInfoListViewAdapter(friendInfoList);
        friendInfo_listView.setAdapter(mFriendInfoListViewAdapter);

        friendInfo_listView.setPullRefreshEnable(true);
        friendInfo_listView.setPullLoadEnable(true);

        //添加侧滑图片和文字的操作
        SwipeMenuCreator mCreator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(UIUtils.getContext());
                // set item background
                deleteItem.setBackground(R.color.colorMainTitle);
                // set item width
                deleteItem.setWidth(ConvertUtils.dp2px(60,UIUtils.getContext()));
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
        friendInfo_listView.setMenuCreator(mCreator);
    }

    private void initData() {
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

    private void initEvent() {
        //返回键
        rl_friendinfo_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //空视图的点击事件
        tv_friendinfo_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //listView的点击事件
        friendInfo_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //先让EditText光标消失
                et_friendInfo_head.setCursorVisible(false);
                //进入聊天信息详情
                ToNextActivityUtils.toNextAndNoFinishActivity(FriendInfoActivity.this,FriendInfoChatDetails.class);
            }
        });

        //让EditText光标显示
        et_friendInfo_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_friendInfo_head.setCursorVisible(true);
            }
        });

        //EditText的文字处理事件
        if(et_friendInfo_head != null) {
            et_friendInfo_head.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                    filterData(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        //侧滑点击删除
        friendInfo_listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        //先要知道数据源变成谁了
                        if(TextUtils.isEmpty(et_friendInfo_head.getText().toString())) {
                            friendInfoList.remove(position);
                            mFriendInfoListViewAdapter.updateListView(friendInfoList);
                        }else if(mFilterDateList != null && mFilterDateList.size() > 0){
                            mFilterDateList.remove(position);
                            mFriendInfoListViewAdapter.updateListView(mFilterDateList);
                        }
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"删除了" +position);
                        break;
                }
            }
        });
        //刷新与加载更多的监听
        friendInfo_listView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
                        RefreshTime.setRefreshTime(getApplicationContext(), df.format(new Date()));
                        updateData();
                    }
                }, 2000);
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

    private void loadAndRefreshComple(PullToRefreshSwipeMenuListView mListView) {
        //先让EditText光标消失
        et_friendInfo_head.setCursorVisible(false);
        mListView.setRefreshTime(RefreshTime.getRefreshTime(getApplicationContext()));
        mListView.stopRefresh();
        mListView.stopLoadMore();
    }
    /**
     * 下拉加载更多的数据显示
     */
    private void loadNewData() {
        moniData();
        loadAndRefreshComple(friendInfo_listView);
        mFriendInfoListViewAdapter.notifyDataSetChanged();
    }

    /**
     * 模拟数据
     */
    private void moniData() {
        Random random = null;
        for (int i = 0; i < 10; i++) {
            SystemInfoBean mFriendInfoBean = new SystemInfoBean();
            random = new Random();
            mFriendInfoBean.imgUrl = String.valueOf(R.mipmap.logo);
            mFriendInfoBean.friendName = "半夏微凉";
            mFriendInfoBean.content = "你们已经成为好友了，可以开始对话了";
            //mFriendInfoBean.date = "2016-06-"+random.nextInt(20);
            //mFriendInfoBean.time = random.nextInt(20)+":00:00";
            friendInfoList.add(mFriendInfoBean);
        }
    }

    /**
     * 真正刷新数据的方法
     */
    private void updateData() {
        //tv_systeminfo_empty.setVisibility(View.GONE);
        //清空数据源
        friendInfoList.clear();
        //添加数据到数据源
        moniData();
        loadAndRefreshComple(friendInfo_listView);
        //ListViewUtils.isAddListViewSystemInfoFooterView(friendInfoList,friendInfo_listView,mFooterView);
        mFriendInfoListViewAdapter.notifyDataSetChanged();
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private void filterData(String filterStr){
        mFilterDateList = new ArrayList<SystemInfoBean>();
        View footView = friendInfo_listView.getFootView();
        if(TextUtils.isEmpty(filterStr)){
            //没有过滤条件，数据源不变
            mFilterDateList = friendInfoList;
            try {
                //可以刷新
                friendInfo_listView.setPullRefreshEnable(true);
                friendInfo_listView.setPullLoadEnable(true);
                if(friendInfo_listView.getFooterViewsCount() == 0) {
                    friendInfo_listView.addFooterView(footView);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            mFilterDateList.clear();
            for(SystemInfoBean mSystemInfoBean : friendInfoList){
                String name = mSystemInfoBean.friendName;
                if(name.indexOf(filterStr.toString()) != -1 || name.startsWith(filterStr.toString())){
                    mFilterDateList.add(mSystemInfoBean);
                }
            }
            try {
                //不能向下划出刷新
                friendInfo_listView.setPullRefreshEnable(false);
                friendInfo_listView.setPullLoadEnable(false);
                friendInfo_listView.stopRefresh();
                friendInfo_listView.stopLoadMore();
                //起作用的代码
                if(friendInfo_listView.getFooterViewsCount() >= 1) {
                    friendInfo_listView.removeFooterView(footView);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        mFriendInfoListViewAdapter.updateListView(mFilterDateList);
    }
}