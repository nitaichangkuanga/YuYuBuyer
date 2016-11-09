package com.wushuikeji.www.yuyubuyer.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.ShopEvaluateListViewAdapter;
import com.wushuikeji.www.yuyubuyer.bean.ShopEvaluateBean;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.ShopEvaluateParse;
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

public class ShopEvaluateActivity extends AppCompatActivity {

    //空视图
    @InjectView(R.id.tv_shopEvaluate_empty)
    TextView tv_shopEvaluate_empty;

    @InjectView(R.id.rl_shopEvaluate_RelativeLayout)
    RelativeLayout rl_shopEvaluate_RelativeLayout;

    //刷新的类
    @InjectView(R.id.in_shopEvaluate_pf)
    PtrClassicFrameLayout mShopEvaluatePtrFrame;

    @InjectView(R.id.shopEvaluate_listView)
    ListView shopEvaluate_listView;

    //进度条
    @InjectView(R.id.pb_shopEvaluateLoading_progressBar)
    ProgressBar pb_shopEvaluateLoading_progressBar;

    //评价按钮
    @InjectView(R.id.rl_shopEvaluate_all)
    RelativeLayout rl_shopEvaluate_all;

    @InjectView(R.id.tv_shopEvaluate_kua)
    TextView tv_shopEvaluate_kua;

    //滑动ListView的距离
    int touchSlop = 10;

    public static ShopEvaluateActivity mShopEvaluateActivity;

    private static final int LOADING = 0;
    private static final int SUCCESS = 1;

    private ShopEvaluateListViewAdapter mShopEvaluateListViewAdapter;

    private List<ShopEvaluateBean> shopEvaluateList = new ArrayList<ShopEvaluateBean>();

    private boolean isBottom;//判断listView是否底部

    private View mFooterView;
    //用来判断用户是否具有评价的权限
    private boolean isEvaluate;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_shopEvaluateLoading_progressBar.setVisibility(View.VISIBLE);
                    tv_shopEvaluate_empty.setVisibility(View.GONE);
                    mShopEvaluatePtrFrame.setVisibility(View.GONE);
                    break;
                case SUCCESS:

                    if(shopEvaluateList.size() != 0) {
                        pb_shopEvaluateLoading_progressBar.setVisibility(View.GONE);
                        tv_shopEvaluate_empty.setVisibility(View.GONE);
                        mShopEvaluatePtrFrame.setVisibility(View.VISIBLE);
                        if(mShopEvaluateListViewAdapter != null) {
                            mShopEvaluateListViewAdapter.notifyDataSetChanged();
                        }
                    }else {
                        pb_shopEvaluateLoading_progressBar.setVisibility(View.GONE);
                        tv_shopEvaluate_empty.setVisibility(View.VISIBLE);
                        mShopEvaluatePtrFrame.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    //是否具有评价的权限
    private String isEvaluateUrl = Constants.commontUrl + "appraisal/check_permissions";
    //获取评价
    private String EvaluateUrl = Constants.commontUrl + "appraisal/appraisals";

    private String mShopId;

    private int offset;
    //每次10条数据
    private int count = 10;
    private boolean mIsLoginBoolean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopevaluate);
        initView();
        checkIsEvaluate();
        initData();
        initEvent();
    }

    private void initView() {
        //注解
        ButterKnife.inject(this);
        //提供给外部刷新用
        mShopEvaluateActivity = this;

        //得到传过来的shopId
        mShopId = getIntent().getStringExtra("shopId");
        //将尾部布局蹦出来
        if(mFooterView == null) {
            mFooterView = View.inflate(UIUtils.getContext(), R.layout.item_listview_mainshop_loadmore, null);
        }
        //滚动过多少距离后才开始计算是否隐藏/显示头尾元素。这里用了默认touchslop的0.9倍。
        touchSlop = (int) (ViewConfiguration.get(ShopEvaluateActivity.this).getScaledTouchSlop() * 0.9);
        //ListView的初始化相关操作
        operateListView();
        //刷新的操作
        mShopEvaluatePtrFrame.setLastUpdateTimeRelateObject(this);
        PtrFrameRefreshUtils.setRefreshParams(mShopEvaluatePtrFrame);
        //记录是否登录
        mIsLoginBoolean = SpUtils.getCacheBoolean(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.ISLOGINSTATUS, false);

    }
    private void checkIsEvaluate() {
        //一进来就先判断用户是否具有评价的权限
        boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
        if (!iSNetworkConnect) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            ShopEvaluateActivity.this.finish();
        } else {
            //post请求网络，判断是否具有权限
            OkHttpUtils.post().url(isEvaluateUrl).addParams("shop_id", mShopId).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    isEvaluate = false;
                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        // 成功了，只是返回来请求的json数据,还需要解析
                        Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                        String response_code = map.get("response_code");

                        if("0".equals(response_code)) {
                            isEvaluate = true;
                        }else {
                            isEvaluate = false;
                        }
                    } catch (Exception e) {
                        isEvaluate = false;
                    }
                }
            });
        }
    }

    private void initData() {
        //显示评价的样式
        if(isEvaluate) {//有权限
            //隐藏提示
            tv_shopEvaluate_kua.setVisibility(View.GONE);
            rl_shopEvaluate_all.setClickable(true);
            rl_shopEvaluate_all.setBackgroundColor(UIUtils.getResources().getColor(R.color.colorMainTitle));
        }else {
            //显示提示
            tv_shopEvaluate_kua.setVisibility(View.VISIBLE);
            rl_shopEvaluate_all.setClickable(false);
            rl_shopEvaluate_all.setBackgroundColor(UIUtils.getResources().getColor(R.color.colorCustomProgressBar));
        }
        //post请求网络，获取评价的内容
        loadNetworkData("0", true,"4",false,true);
    }

    /**
     * 加载网络的数据
     */
    private void loadNetworkData(String offset, boolean isNeedLoading, String type, boolean isFromRefresh,boolean isNeedClearData) {

        final boolean itemIsFromRefresh = isFromRefresh;
        final boolean itemIsNeedClearData = isNeedClearData;

            if (isNeedLoading) {
                //进度条展示
                mHandler.obtainMessage(LOADING).sendToTarget();
            }

            //网络请求
            OkHttpUtils.post().url(EvaluateUrl).addParams("offset", offset)
                    .addParams("count", String.valueOf(count))
                    .addParams("type", type)
                    .addParams("shop_id", mShopId)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    if(mShopEvaluatePtrFrame != null && itemIsFromRefresh) {
                        mShopEvaluatePtrFrame.refreshComplete();
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
                            //可以下一步解析商铺评价
                            List<ShopEvaluateBean> tempList = ShopEvaluateParse.shopEvaluateParse(response);

                            if(tempList != null) {
                                if(itemIsNeedClearData) {
                                    ListViewUtils.isAddListViewShopEvaluateFooterView(shopEvaluateList,shopEvaluate_listView,mFooterView);
                                    shopEvaluateList.clear();
                                    shopEvaluateList.addAll(tempList);
                                }else {
                                    //加载更多
                                    if (tempList.size() == 0) {
                                        if (shopEvaluate_listView.getFooterViewsCount() == 1) {
                                            try {
                                                shopEvaluate_listView.removeFooterView(mFooterView);
                                                mShopEvaluateListViewAdapter.notifyDataSetChanged();
                                            } catch (Exception e) {
                                            }
                                        }
                                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "没有数据了");
                                    } else {
                                        shopEvaluateList.addAll(tempList);
                                        ListViewUtils.isAddListViewShopEvaluateFooterView(shopEvaluateList,shopEvaluate_listView,mFooterView);
                                        if(mShopEvaluateListViewAdapter != null) {
                                            mShopEvaluateListViewAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }

                                //刷新完成
                                if(mShopEvaluatePtrFrame != null && itemIsFromRefresh) {
                                    mShopEvaluatePtrFrame.refreshComplete();
                                }
                            }
                        } else if (responseContent != null) {
                            //给出错误提示
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                            if(mShopEvaluatePtrFrame != null && itemIsFromRefresh) {
                                mShopEvaluatePtrFrame.refreshComplete();
                            }
                            if (MyConstants.FOOTVIEWNAME.equals(responseContent)) {
                                if (shopEvaluate_listView.getFooterViewsCount() == 1) {
                                    try {
                                        shopEvaluate_listView.removeFooterView(mFooterView);
                                        mShopEvaluateListViewAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                        //发送handler，隐藏进度条
                        mHandler.obtainMessage(SUCCESS).sendToTarget();
                    }catch (Exception e) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                        if(mShopEvaluatePtrFrame != null && itemIsFromRefresh) {
                            mShopEvaluatePtrFrame.refreshComplete();
                        }
                        //发送handler，隐藏进度条
                        mHandler.obtainMessage(SUCCESS).sendToTarget();
                    }
                }
            });
    }

    /**
     * 显示的属性动画
     */
    AnimatorSet backAnimatorSet;

    private void animateBack() {
        //先清除其他动画
        if (hideAnimatorSet != null && hideAnimatorSet.isRunning()) {
            hideAnimatorSet.cancel();
        }
        if (backAnimatorSet != null && backAnimatorSet.isRunning()) {
            //如果这个动画已经在运行了，就不管它
        } else {
            backAnimatorSet = new AnimatorSet();
            //下面是将尾元素放回初始位置。
            ObjectAnimator footerAnimator = ObjectAnimator.ofFloat(rl_shopEvaluate_all, "translationY", rl_shopEvaluate_all.getTranslationY(), 0f);

            ArrayList<Animator> animators = new ArrayList<>();
            animators.add(footerAnimator);
            backAnimatorSet.setDuration(300);
            backAnimatorSet.playTogether(animators);
            backAnimatorSet.start();
        }
    }

    /**
     * 隐藏的属性动画
     */
    AnimatorSet hideAnimatorSet;

    private void animateHide() {
        if (backAnimatorSet != null && backAnimatorSet.isRunning()) {
            backAnimatorSet.cancel();
        }
        if (hideAnimatorSet != null && hideAnimatorSet.isRunning()) {
        } else {
            hideAnimatorSet = new AnimatorSet();
            //将Button隐藏到下面
            ObjectAnimator footerAnimator = ObjectAnimator.ofFloat(rl_shopEvaluate_all, "translationY", rl_shopEvaluate_all.getTranslationY(), rl_shopEvaluate_all.getHeight());

            ArrayList<Animator> animators = new ArrayList<>();
            animators.add(footerAnimator);
            hideAnimatorSet.setDuration(200);
            hideAnimatorSet.playTogether(animators);
            hideAnimatorSet.start();
        }
    }

    private void initEvent() {

        //返回
        BackListenerUtils.backFinish(rl_shopEvaluate_RelativeLayout,this);

        //ListView的触摸事件
        shopEvaluate_listView.setOnTouchListener(new View.OnTouchListener() {

            float lastY = 0f;
            float currentY = 0f;
            //下面两个表示滑动的方向，大于0表示向下滑动，小于0表示向上滑动，等于0表示未滑动
            int lastDirection = 0;
            int currentDirection = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastY = event.getY();
                        currentY = event.getY();
                        currentDirection = 0;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //只有在listView.getFirstVisiblePosition()>0的时候才判断是否进行显隐动画。因为listView.getFirstVisiblePosition()==0时,listview没动
                        if (shopEvaluate_listView.getFirstVisiblePosition() > 0) {
                            float tmpCurrentY = event.getY();
                            if (Math.abs(tmpCurrentY - lastY) > touchSlop) {
                                currentY = tmpCurrentY;
                                currentDirection = (int) (currentY - lastY);
                                if (lastDirection != currentDirection) {
                                    if (currentDirection < 0) {
                                        animateHide();
                                    } else {
                                        animateBack();
                                    }
                                }
                                lastY = currentY;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        //恢复默认没滑动
                        currentDirection = 0;
                        break;
                }
                return false;
            }
        });
        //listView的滑动事件
        shopEvaluate_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            //这个Listener其实是用来对付当用户的手离开列表后列表仍然在滑动的情况，也就是SCROLL_STATE_FLING
            int lastPosition = 0;//上次滚动到的第一个可见元素在listview里的位置——firstVisibleItem
            int state = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //记录当前列表状态
                state = scrollState;
                // 数据已经到底部，而且用户是松手状态，这个时候需要加载新的数据
                if (isBottom && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                        return;
                    }else {
                        offset = offset + 10;
                        loadNetworkData(String.valueOf(offset),false,"4",false,false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 判读是否需要加载新的数据
                isBottom = (firstVisibleItem + visibleItemCount) == totalItemCount;

                if (firstVisibleItem == 0) {

                    animateBack();
                }
                if (firstVisibleItem > 0) {
                    //如果上次的位置小于当前位置，那么隐藏头尾元素
                    if (firstVisibleItem > lastPosition && state == SCROLL_STATE_FLING) {
                        animateHide();
                    }
                }
                lastPosition = firstVisibleItem;
            }
        });
        //实现下拉刷新
        mShopEvaluatePtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                // here check $mListView instead of $content
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, shopEvaluate_listView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    mShopEvaluatePtrFrame.refreshComplete();
                    return;
                }else {
                    //qing 0
                    offset = 0;
                    loadNetworkData("0",false,"4",true,true);
                    //防止加载更多的时候，已经加了尾部布局，所以，先判断如果数据源小于8个,并且有尾部就删除
                    if (shopEvaluateList.size() <= 8 && shopEvaluate_listView.getFooterViewsCount() == 1) {
                        try {
                            shopEvaluate_listView.removeFooterView(mFooterView);
                            mShopEvaluateListViewAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        });
        //实现评价商铺点击
        rl_shopEvaluate_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEvaluate) {//有权限
                    //隐藏提示
                    tv_shopEvaluate_kua.setVisibility(View.GONE);
                    rl_shopEvaluate_all.setClickable(true);
                    rl_shopEvaluate_all.setBackgroundColor(UIUtils.getResources().getColor(R.color.colorMainTitle));
                    //还需要判断是否登录
                    if(mIsLoginBoolean) {
                        ToNextActivityUtils.toNextActivityNotFinishAndParameters(ShopEvaluateActivity.this, FillinShopEvaluateActivity.class,"shopId",mShopId);
                    }else {
                        //跳转到登录界面
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"请先登录");
                        ToNextActivityUtils.toNextAndNoFinishActivity(ShopEvaluateActivity.this,LoginActivity.class);
                    }
                }else {
                    //显示提示
                    tv_shopEvaluate_kua.setVisibility(View.VISIBLE);
                    rl_shopEvaluate_all.setClickable(false);
                    rl_shopEvaluate_all.setBackgroundColor(UIUtils.getResources().getColor(R.color.colorCustomProgressBar));
                    ToastUtils.showToastInUIThread(UIUtils.getContext(),"暂时还没有权限评价");
                }
            }
        });
    }

    private void operateListView() {
        //必须在setAdapter之前
        //加之前，先判断数据源有几个，不够一屏幕显示，不加入
        ListViewUtils.isAddListViewShopEvaluateFooterView(shopEvaluateList,shopEvaluate_listView,mFooterView);
        //mTailView.setVisibility(View.GONE);//设置刷新视图默认情况下是不可见的
        if(mShopEvaluateListViewAdapter == null) {
            mShopEvaluateListViewAdapter = new ShopEvaluateListViewAdapter(shopEvaluateList);
        }
        shopEvaluate_listView.setAdapter(mShopEvaluateListViewAdapter);
    }

    /**
     * 提供一个方法给外部刷新用
     */
    public void refreshData() {
        //post请求网络，获取评价的内容
        loadNetworkData("0", true,"4",false,true);
    }
}
