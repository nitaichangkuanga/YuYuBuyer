package com.wushuikeji.www.yuyubuyer.fragment;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.AllEvaluateListViewAdapter;
import com.wushuikeji.www.yuyubuyer.base.BaseFragment;
import com.wushuikeji.www.yuyubuyer.bean.AllEvaluateBean;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.AllEvaluateParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.ListViewUtils;
import com.wushuikeji.www.yuyubuyer.utils.PtrFrameRefreshUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.wushuikeji.www.yuyubuyer.view.CircularProgress;
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

public class ShopEvaluateFragment extends BaseFragment {

    //空视图
    @InjectView(R.id.tv_shopEvaluateFragment_emptyView)
    TextView tv_shopEvaluateFragment_emptyView;

    //刷新的类
    @InjectView(R.id.in_shopEvaluateFragment_pf)
    PtrClassicFrameLayout mShopEvaluateFragmentPtrFrame;

    @InjectView(R.id.shopEvaluateFragment_listView)
    ListView shopEvaluateFragment_listView;

    //进度条
    @InjectView(R.id.pb_shopEvaluateFragment_progressBar)
    CircularProgress pb_shopEvaluateFragment_progressBar;

    private static final int LOADING = 0;
    private static final int SUCCESS = 1;

    private AllEvaluateListViewAdapter mAllEvaluatewListViewAdapter;

    private List<AllEvaluateBean> allEvaluateBeanList = new ArrayList<AllEvaluateBean>();

    private boolean isBottom;//判断listView是否底部

    private View mShopEvaluateFragmentView;
    private View mFooterView;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_shopEvaluateFragment_progressBar.setVisibility(View.VISIBLE);
                    tv_shopEvaluateFragment_emptyView.setVisibility(View.GONE);
                    mShopEvaluateFragmentPtrFrame.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    if(allEvaluateBeanList.size() != 0) {
                        pb_shopEvaluateFragment_progressBar.setVisibility(View.GONE);
                        tv_shopEvaluateFragment_emptyView.setVisibility(View.GONE);
                        mShopEvaluateFragmentPtrFrame.setVisibility(View.VISIBLE);

                        if(mAllEvaluatewListViewAdapter != null) {
                            mAllEvaluatewListViewAdapter.notifyDataSetChanged();
                        }
                    }else {
                        pb_shopEvaluateFragment_progressBar.setVisibility(View.GONE);
                        tv_shopEvaluateFragment_emptyView.setVisibility(View.VISIBLE);
                        mShopEvaluateFragmentPtrFrame.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //获取评价
    private String EvaluateUrl = Constants.commontUrl + "appraisal/appraisals";

    private int count = 10;
    private String mUserId;
    private int offset;


    @Override
    public View initView() {
        //需要展示的View
        if(mShopEvaluateFragmentView == null) {
            mShopEvaluateFragmentView = View.inflate(getActivity(), R.layout.shop_evaluate_fragment, null);
        }
        //注解
        ButterKnife.inject(this, mShopEvaluateFragmentView);

        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");

        //将尾部布局蹦出来
        if(mFooterView == null) {
            mFooterView = View.inflate(UIUtils.getContext(), R.layout.item_listview_mainshop_loadmore, null);
        }

        //ListView的初始化相关操作
        operateListView();

        //刷新的操作
        if(mShopEvaluateFragmentPtrFrame != null) {
            mShopEvaluateFragmentPtrFrame.setLastUpdateTimeRelateObject(this);
            PtrFrameRefreshUtils.setRefreshParams(mShopEvaluateFragmentPtrFrame);
        }

        return mShopEvaluateFragmentView;
    }

    @Override
    public void initData() {
        super.initData();
        boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
        if (!iSNetworkConnect) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            //post请求网络，获取评价的内容
            loadNetworkData("0", true,"1",false,true);
        }
    }

    /**
     * 加载网络的数据
     */
    private void loadNetworkData(String offset, boolean isNeedLoading, String type, final boolean isFromRefresh,boolean isNeedClearData) {

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
                .addParams("user_id", mUserId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                if(mShopEvaluateFragmentPtrFrame != null && itemIsFromRefresh) {
                    mShopEvaluateFragmentPtrFrame.refreshComplete();
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
                        List<AllEvaluateBean> evaluateBeenList = AllEvaluateParse.evaluateParse(response,true);

                        if(evaluateBeenList != null) {
                            if(itemIsNeedClearData) {
                                ListViewUtils.isAddListViewAllEvaluateFooterView(allEvaluateBeanList,shopEvaluateFragment_listView,mFooterView);
                                allEvaluateBeanList.clear();
                                allEvaluateBeanList.addAll(evaluateBeenList);
                            }else {
                                //加载更多
                                if (allEvaluateBeanList.size() == 0) {
                                    if (shopEvaluateFragment_listView.getFooterViewsCount() == 1) {
                                        try {
                                            shopEvaluateFragment_listView.removeFooterView(mFooterView);
                                            mAllEvaluatewListViewAdapter.notifyDataSetChanged();
                                        } catch (Exception e) {
                                        }
                                    }
                                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "没有数据了");
                                }else {
                                    allEvaluateBeanList.addAll(evaluateBeenList);
                                    ListViewUtils.isAddListViewAllEvaluateFooterView(allEvaluateBeanList,shopEvaluateFragment_listView,mFooterView);
                                    if(mAllEvaluatewListViewAdapter != null) {
                                        mAllEvaluatewListViewAdapter.notifyDataSetChanged();
                                    }
                                }
                            }

                            //刷新完成
                            if(mShopEvaluateFragmentPtrFrame != null && itemIsFromRefresh) {
                                mShopEvaluateFragmentPtrFrame.refreshComplete();
                            }
                        }
                    } else if (responseContent != null) {
                        //给出错误提示
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                        if(mShopEvaluateFragmentPtrFrame != null && itemIsFromRefresh) {
                            mShopEvaluateFragmentPtrFrame.refreshComplete();
                        }
                        if (MyConstants.FOOTVIEWNAME.equals(responseContent)) {
                            if (shopEvaluateFragment_listView.getFooterViewsCount() == 1) {
                                try {
                                    shopEvaluateFragment_listView.removeFooterView(mFooterView);
                                    mAllEvaluatewListViewAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                    //发送handler，隐藏进度条
                    mHandler.obtainMessage(SUCCESS).sendToTarget();
                }catch (Exception e) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    if(mShopEvaluateFragmentPtrFrame != null && itemIsFromRefresh) {
                        mShopEvaluateFragmentPtrFrame.refreshComplete();
                    }
                    //发送handler，隐藏进度条
                    mHandler.obtainMessage(SUCCESS).sendToTarget();
                }
            }
        });
    }

    @Override
    public void initEvent() {
        super.initEvent();
        //listView的滑动事件
        shopEvaluateFragment_listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 数据已经到底部，而且用户是松手状态，这个时候需要加载新的数据
                if (isBottom && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                        return;
                    }else {
                        offset = offset + 10;
                        loadNetworkData(String.valueOf(offset),false,"1",false,false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 判读是否需要加载新的数据
                isBottom = (firstVisibleItem + visibleItemCount) == totalItemCount;
            }
        });
        //实现下拉刷新
        mShopEvaluateFragmentPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                // here check $mListView instead of $content
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, shopEvaluateFragment_listView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    return;
                }else {
                    offset = 0;
                    loadNetworkData("0",false,"1",true,true);
                    //防止加载更多的时候，已经加了尾部布局，所以，先判断如果数据源小于8个,并且有尾部就删除
                    if (allEvaluateBeanList.size() <= 8 && shopEvaluateFragment_listView.getFooterViewsCount() == 1) {
                        try {
                            shopEvaluateFragment_listView.removeFooterView(mFooterView);
                            mAllEvaluatewListViewAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        });
    }

    private void operateListView() {
        //必须在setAdapter之前
        //加之前，先判断数据源有几个，不够一屏幕显示，不加入
        ListViewUtils.isAddListViewAllEvaluateFooterView(allEvaluateBeanList,shopEvaluateFragment_listView,mFooterView);
        //mTailView.setVisibility(View.GONE);//设置刷新视图默认情况下是不可见的
        mAllEvaluatewListViewAdapter = new AllEvaluateListViewAdapter(allEvaluateBeanList);
        shopEvaluateFragment_listView.setAdapter(mAllEvaluatewListViewAdapter);
    }
}
