package com.wushuikeji.www.yuyubuyer.fragment;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.AllEvaluateListViewAdapter;
import com.wushuikeji.www.yuyubuyer.base.BaseFragment;
import com.wushuikeji.www.yuyubuyer.bean.AllEvaluateBean;
import com.wushuikeji.www.yuyubuyer.utils.ListViewUtils;
import com.wushuikeji.www.yuyubuyer.utils.PtrFrameRefreshUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.wushuikeji.www.yuyubuyer.view.CircularProgress;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class BuyerEvaluateFragment extends BaseFragment {

    //空视图
    @InjectView(R.id.tv_shopEvaluateFragment_emptyView)
    TextView tv_buyerEvaluateFragment_emptyView;

    //刷新的类
    @InjectView(R.id.in_shopEvaluateFragment_pf)
    PtrClassicFrameLayout mBuyerEvaluateFragmentPtrFrame;

    @InjectView(R.id.shopEvaluateFragment_listView)
    ListView buyerEvaluateFragment_listView;

    //进度条
    @InjectView(R.id.pb_shopEvaluateFragment_progressBar)
    CircularProgress pb_buyerEvaluateFragment_progressBar;

    private static final int LOADING = 0;
    private static final int SUCCESS = 1;

    private AllEvaluateListViewAdapter mAllEvaluatewListViewAdapter;

    private List<AllEvaluateBean> allEvaluateBeanList = new ArrayList<AllEvaluateBean>();

    private boolean isBottom;//判断listView是否底部

    private View mBuyerEvaluateFragmentView;
    private View mFooterView;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_buyerEvaluateFragment_progressBar.setVisibility(View.VISIBLE);
                    tv_buyerEvaluateFragment_emptyView.setVisibility(View.GONE);
                    mBuyerEvaluateFragmentPtrFrame.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    //ListView的初始化相关操作
                    operateListView();

                    if(allEvaluateBeanList.size() != 0) {
                        pb_buyerEvaluateFragment_progressBar.setVisibility(View.GONE);
                        tv_buyerEvaluateFragment_emptyView.setVisibility(View.GONE);
                        mBuyerEvaluateFragmentPtrFrame.setVisibility(View.VISIBLE);
                        if(mAllEvaluatewListViewAdapter != null) {
                            mAllEvaluatewListViewAdapter.notifyDataSetChanged();
                        }
                    }else {
                        pb_buyerEvaluateFragment_progressBar.setVisibility(View.GONE);
                        tv_buyerEvaluateFragment_emptyView.setVisibility(View.VISIBLE);
                        mBuyerEvaluateFragmentPtrFrame.setVisibility(View.GONE);
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
        mBuyerEvaluateFragmentView = View.inflate(getActivity(), R.layout.shop_evaluate_fragment, null);
        //注解
        ButterKnife.inject(this, mBuyerEvaluateFragmentView);
        //将尾部布局蹦出来
        if(mFooterView == null) {
            mFooterView = View.inflate(UIUtils.getContext(), R.layout.item_listview_mainshop_loadmore, null);
        }
        //刷新的操作
        if(mBuyerEvaluateFragmentPtrFrame != null) {
            mBuyerEvaluateFragmentPtrFrame.setLastUpdateTimeRelateObject(this);
            PtrFrameRefreshUtils.setRefreshParams(mBuyerEvaluateFragmentPtrFrame);
        }

        return mBuyerEvaluateFragmentView;
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
                SystemClock.sleep(1000);

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
        tv_buyerEvaluateFragment_emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb_buyerEvaluateFragment_progressBar.setVisibility(View.GONE);
                tv_buyerEvaluateFragment_emptyView.setVisibility(View.GONE);
                mBuyerEvaluateFragmentPtrFrame.setVisibility(View.VISIBLE);
                mBuyerEvaluateFragmentPtrFrame.autoRefresh();
            }
        });

        //listView的滑动事件
        buyerEvaluateFragment_listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 数据已经到底部，而且用户是松手状态，这个时候需要加载新的数据
                if (isBottom && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    //设置刷新界面可见
                    //mTailView.setVisibility(View.VISIBLE);
                    //以后必须判断加载是否还有数据，没有数据将移除mTailView
                    //有时候在移除时回报空指针，但listview不为null ，mLoadingLayout也不为null，但还是报空指针，原因是因为listview要分为三部分。
                    //一是头部，二是中间部，三是尾部。在设置了头部或尾部时，必须要有中间部才能真正意义上的生效。没生效就去移除就会报空指针错误。
                    //所以在listView.removeFooterView(mLoadingLayout);时必须先调用 listView.setAdapter(adapter);（设置中间部）
                    // listView.removeFooterView(loadmoreView);//如果是最后一页的话，则将其从ListView中移出
                    loadNewData();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 判读是否需要加载新的数据
                isBottom = (firstVisibleItem + visibleItemCount) == totalItemCount;
            }
        });
        //实现下拉刷新
        mBuyerEvaluateFragmentPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                // here check $mListView instead of $content
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, buyerEvaluateFragment_listView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //真正实现下拉刷新,这是在主线程里
                updateData();
            }
        });
    }

    /**
     * 下拉加载更多的数据显示
     */
    private void loadNewData() {
        try {
            //模拟耗时操作
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        moniData();
        if(mAllEvaluatewListViewAdapter != null) {
            mAllEvaluatewListViewAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 模拟数据
     */
    private void moniData() {
        Random random = null;
        for (int i = 0; i < 10; i++) {
            AllEvaluateBean mAllEvaluateBean = new AllEvaluateBean();
            random = new Random();
            mAllEvaluateBean.userName = "蓦然回首的样";
            mAllEvaluateBean.count = random.nextInt(5) + 1;
            mAllEvaluateBean.evaluateContent = "宝贝音质很好，下次还来"+i;
            mAllEvaluateBean.date = "2016-10-"+random.nextInt(30);
            mAllEvaluateBean.time = "20:"+random.nextInt(60)+":"+random.nextInt(60);
            mAllEvaluateBean.type = String.valueOf(R.mipmap.shop_shop);
            mAllEvaluateBean.shopName = "谜底专卖店"+random.nextInt(10);
            allEvaluateBeanList.add(mAllEvaluateBean);
        }
    }

    /**
     * 真正刷新数据的方法
     */
    private void updateData() {
        //清空数据源
        allEvaluateBeanList.clear();
        //添加数据到数据源
        moniData();
        ListViewUtils.isAddListViewAllEvaluateFooterView(allEvaluateBeanList,buyerEvaluateFragment_listView,mFooterView);
        //刷新完成
        mBuyerEvaluateFragmentPtrFrame.refreshComplete();
        if(mAllEvaluatewListViewAdapter != null) {
            mAllEvaluatewListViewAdapter.notifyDataSetChanged();
        }
    }

    private void operateListView() {
        //必须在setAdapter之前
        //加之前，先判断数据源有几个，不够一屏幕显示，不加入
        ListViewUtils.isAddListViewAllEvaluateFooterView(allEvaluateBeanList,buyerEvaluateFragment_listView,mFooterView);
        //mTailView.setVisibility(View.GONE);//设置刷新视图默认情况下是不可见的
        if(mAllEvaluatewListViewAdapter == null) {
            mAllEvaluatewListViewAdapter = new AllEvaluateListViewAdapter(allEvaluateBeanList);
        }
        buyerEvaluateFragment_listView.setAdapter(mAllEvaluatewListViewAdapter);
    }
}
