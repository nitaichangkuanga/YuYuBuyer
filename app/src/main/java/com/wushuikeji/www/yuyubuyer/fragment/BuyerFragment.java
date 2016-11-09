package com.wushuikeji.www.yuyubuyer.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.activity.SelectCityActivity;
import com.wushuikeji.www.yuyubuyer.activity.BuyerInfoActivity;
import com.wushuikeji.www.yuyubuyer.adapter.MainBuyerListViewAdapter;
import com.wushuikeji.www.yuyubuyer.base.BaseFragment;
import com.wushuikeji.www.yuyubuyer.bean.BuyerBean;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.BuyerFragmentParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.ListViewUtils;
import com.wushuikeji.www.yuyubuyer.utils.PtrFrameRefreshUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToNextActivityUtils;
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

public class BuyerFragment extends BaseFragment {

    @InjectView(R.id.mainBuyerFragment_list)
    ListView mainBuyerFragment_list;

    //刷新的类
    @InjectView(R.id.in_mainbuyer_pf)
    PtrClassicFrameLayout mBuyerPtrFrame;

    //进度条
    @InjectView(R.id.pb_loading_BuyerProgressBar)
    CircularProgress pb_loading_BuyerProgressBar;

    //刷选
    @InjectView(R.id.tv_buyer_choice)
    TextView tv_buyer_choice;

    //点击城市
    @InjectView(R.id.tv_buyer_city)
    TextView tv_buyer_city;

    //点击商圈
    @InjectView(R.id.tv_buyer_business)
    TextView tv_buyer_business;

    //排序
    @InjectView(R.id.tv_buyer_sort)
    TextView tv_buyer_sort;

    //空视图
    @InjectView(R.id.tv_mainbuyer_emptyView)
    TextView tv_mainbuyer_emptyView;


    private MainBuyerListViewAdapter mainBuyerListViewAdapter;

    private List<BuyerBean> buyerBeanList = new ArrayList<BuyerBean>();

    private View mFooterView;

    private boolean isBottom;//判断listView是否底部

    private PopupWindow mChoicePopupWindow;
    private PopupWindow mSortPopupWindow;

    private View mChoiceView;
    private View mSortView;
    private LinearLayout mLl_popSort_grade;
    private ImageView mIv_sort_gradeGou;
    private TextView mTv_sort_grade;
    private LinearLayout mLl_popSort_xin;
    private ImageView mIv_sort_xinGou;
    private TextView mTv_sort_xin;
    private LinearLayout mLl_popSort_distance;
    private ImageView mIv_sort_distanceGou;
    private TextView mTv_sort_distance;
    private View mBuyerFragmentView;
    private RelativeLayout ll_mainHead_llDeleteimg;

    private static final int LOADING = 0;
    private static final int SUCCESS = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_loading_BuyerProgressBar.setVisibility(View.VISIBLE);
                    tv_mainbuyer_emptyView.setVisibility(View.GONE);
                    mBuyerPtrFrame.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    if (buyerBeanList.size() != 0) {
                        pb_loading_BuyerProgressBar.setVisibility(View.GONE);
                        tv_mainbuyer_emptyView.setVisibility(View.GONE);
                        mBuyerPtrFrame.setVisibility(View.VISIBLE);
                        //更新数据
                        if (mainBuyerListViewAdapter != null) {
                            mainBuyerListViewAdapter.notifyDataSetChanged();
                        }
                    } else {
                        pb_loading_BuyerProgressBar.setVisibility(View.GONE);
                        tv_mainbuyer_emptyView.setVisibility(View.VISIBLE);
                        mBuyerPtrFrame.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private View mHeadView;
    private EditText mEditTextView;
    private boolean isFilterStatus = false;
    private ImageView ib_mainHead_deleteimg;
    private String mCityName;
    private String mBusinessName;
    private String mLongitude;
    private String mLatitude;
    private String mArea_id;
    private int count = 10;
    //buyer的地址
    private String buyerFragmentUrl = Constants.commontUrl + "buyer/list";
    private boolean mIsLoginBoolean;
    private String mUserId;
    private int offset;
    private int sortOffset;
    private RadioGroup mServerRadioGroup;
    private RadioGroup mSexRadioGroup;
    private RadioGroup mStatusRadioGroup;
    private RadioButton mRb_popFiltrate_serverOne;
    private RadioButton mRb_popFiltrate_serverTwo;
    private RadioButton mRb_popFiltrate_serverThree;
    private RadioButton mRb_popFiltrate_sexOne;
    private RadioButton mRb_popFiltrate_sexTwo;
    private RadioButton mRb_popFiltrate_sexThree;
    private RadioButton mRb_popFiltrate_StatusOne;
    private RadioButton mRb_popFiltrate_StatusTwo;
    private RadioButton mRb_popFiltrate_StatusThree;
    private TextView mTv_pop_reset;
    private TextView tv_pop_decided;
    private int radioButtonServerIndex;
    private int radioButtonSexIndex;
    private int radioButtonStatusIndex;

    private int radioButtonFinalServerIndex;
    private int radioButtonFinalSexIndex;
    private int radioButtonFinalStatusIndex;
    private int finalSortIndex = 2;
    //这个值用来判断用户点了刷选还是排序通过这个值来进行不同的刷新操作
    private int swichRefreshOrSort;

    private boolean isClearSortAfterData;
    private List<BuyerBean> mFilterDateList;
    //提供给外部刷新
    public static BuyerFragment mBuyerFragment;

    @Override
    public void init() {
        super.init();
        Bundle bundle = getArguments();
        mCityName = bundle.getString("cityName");
        mBusinessName = bundle.getString("businessName");
        mArea_id = bundle.getString("area_id");
    }

    @Override
    public View initView() {
        //需要展示的View
        mBuyerFragmentView = View.inflate(UIUtils.getContext(), R.layout.main_buyer, null);
        //注解
        ButterKnife.inject(this, mBuyerFragmentView);

        mBuyerFragment = this;
        //查看用户是否登陆过
        mIsLoginBoolean = SpUtils.getCacheBoolean(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.ISLOGINSTATUS, false);
        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");

        //将尾部布局蹦出来
        mFooterView = View.inflate(UIUtils.getContext(), R.layout.item_listview_mainshop_loadmore, null);

        //去除系统自带的颜色渐变
        mainBuyerFragment_list.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mHeadView = View.inflate(UIUtils.getContext(), R.layout.main_buyer_head, null);

        //ListView的相关操作
        operateBuyerListView();

        //得到EditText
        mEditTextView = (EditText) mHeadView.findViewById(R.id.head_editText);
        //删除图标
        ib_mainHead_deleteimg = (ImageView) mHeadView.findViewById(R.id.ib_mainHead_deleteimg);
        ll_mainHead_llDeleteimg = (RelativeLayout) mHeadView.findViewById(R.id.rl_mainHead_rlDeleteimg);
        //刷新的操作
        mBuyerPtrFrame.setLastUpdateTimeRelateObject(this);

        PtrFrameRefreshUtils.setRefreshParams(mBuyerPtrFrame);

        //popupwindow的操作
        opearPopupwindow();

        return mBuyerFragmentView;
    }

    @Override
    public void initData() {
        super.initData();
        //一进来先从缓存中读取最后保存的城市名和商圈名
        String finalCityName = SpUtils.getSpString(UIUtils.getContext(), "finalCityName", "成都");
        String finalBusinessName = SpUtils.getSpString(UIUtils.getContext(), "finalBusinessName", "全城");
        String finalBusinessId = SpUtils.getSpString(UIUtils.getContext(), "finalBusinessId", "0");
        //一开始的城市名和商圈名
        if (!TextUtils.isEmpty(finalCityName)) {
            tv_buyer_city.setText(finalCityName);
        }
        if (!TextUtils.isEmpty(finalBusinessName)) {
            tv_buyer_business.setText(finalBusinessName);
        }
        if (!TextUtils.isEmpty(finalBusinessId)) {
            mArea_id = finalBusinessId;
        }

        //用户选择之后的
        if (!TextUtils.isEmpty(mCityName)) {
            tv_buyer_city.setText(mCityName);
        }
        if (!TextUtils.isEmpty(mBusinessName)) {
            tv_buyer_business.setText(mBusinessName);
        }

        //获取定位后的经纬度
        //经度
        mLongitude = SpUtils.getSpString(UIUtils.getContext(), MyConstants.LONGITUDE, "");
        //纬度
        mLatitude = SpUtils.getSpString(UIUtils.getContext(), MyConstants.LATITUDE, "");
        //先判断缓存中是否有数据
        String jsonString = SpUtils.getCacheString(MyConstants.BUYERSPNAME, UIUtils.getContext(), MyConstants.BUYERFRAGMENTJSON, "");

        if (TextUtils.isEmpty(jsonString)) {
            //为空，在需要判断网络,再去解析
            if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                return;
            } else if (mIsLoginBoolean) {//登录过
                loadNetworkData(true, "0", true, false, true, true);
            } else {//没登录
                loadNetworkData(false, "0", true, false, true, true);
            }
        } else {
            //有缓存，进行解析显示
            List<BuyerBean> cacheList = BuyerFragmentParse.buyerFragmentParse(jsonString);
            if (cacheList != null) {
                buyerBeanList.clear();
                buyerBeanList.addAll(cacheList);
                ListViewUtils.isAddListViewBuyerFooterView(buyerBeanList, mainBuyerFragment_list, mFooterView);
            }
            //发送handler，为了让listView显示出来并加载数据
            mHandler.obtainMessage(SUCCESS).sendToTarget();
        }
    }

    @Override
    public void initEvent() {
        super.initEvent();
        //listView的滑动事件
        mainBuyerFragment_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 数据已经到底部，而且用户是松手状态，这个时候需要加载新的数据
                if (isBottom && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                    if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                        return;
                    } else if (TextUtils.isEmpty(mEditTextView.getText().toString())) {
                        if (mIsLoginBoolean) {
                            //登陆过
                            if (swichRefreshOrSort == 0) {
                                offset = offset + 10;
                                if (radioButtonFinalServerIndex == 0 && radioButtonFinalSexIndex == 0 && radioButtonFinalStatusIndex == 0) {
                                    loadNetworkData(true, String.valueOf(offset), false, false, false, false);
                                } else {
                                    loadNetworkDataFromChoice(true, String.valueOf(offset), String.valueOf(radioButtonFinalSexIndex), String.valueOf(radioButtonFinalStatusIndex), false, false, false, false);
                                }
                            } else if (swichRefreshOrSort == 1) {
                                //排序后的加载更多
                                sortOffset = sortOffset + 10;
                                if (finalSortIndex == 0) {
                                    sortRequestNetWork(0, true, String.valueOf(sortOffset), false, false, false, false);
                                } else if (finalSortIndex == 1) {
                                    sortRequestNetWork(1, true, String.valueOf(sortOffset), false, false, false, false);
                                } else if (finalSortIndex == 2) {
                                    sortRequestNetWork(2, true, String.valueOf(sortOffset), false, false, false, false);
                                }
                            }

                        } else {
                            if (swichRefreshOrSort == 0) {
                                offset = offset + 10;
                                if (radioButtonFinalServerIndex == 0 && radioButtonFinalSexIndex == 0 && radioButtonFinalStatusIndex == 0) {
                                    loadNetworkData(false, String.valueOf(offset), false, false, false, false);
                                } else {
                                    loadNetworkDataFromChoice(false, String.valueOf(offset), String.valueOf(radioButtonFinalSexIndex), String.valueOf(radioButtonFinalStatusIndex), false, false, false, false);
                                }
                            } else if (swichRefreshOrSort == 1) {
                                //排序后的加载更多
                                sortOffset = sortOffset + 10;
                                if (finalSortIndex == 0) {
                                    sortRequestNetWork(0, false, String.valueOf(sortOffset), false, false, false, false);
                                } else if (finalSortIndex == 1) {
                                    sortRequestNetWork(1, false, String.valueOf(sortOffset), false, false, false, false);
                                } else if (finalSortIndex == 2) {
                                    sortRequestNetWork(2, false, String.valueOf(sortOffset), false, false, false, false);
                                }
                            }
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
        mainBuyerFragment_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    //先让EditText光标消失
                    mEditTextView.setCursorVisible(false);
                    //因为数据源不一样，可能从过滤后的数据源中进行跳转
                    if(TextUtils.isEmpty(mEditTextView.getText().toString().trim())) {
                        ToNextActivityUtils.toNextActivityNotFinishAndParameters(getActivity(), BuyerInfoActivity.class, "BuyerId", buyerBeanList.get(position - 1).userId);
                    }else {
                        ToNextActivityUtils.toNextActivityNotFinishAndParameters(getActivity(), BuyerInfoActivity.class, "BuyerId", mFilterDateList.get(position - 1).userId);
                    }
                }
            }
        });

        //刷选的点击事件
        tv_buyer_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //一进来箭头就向上，因为我在mPopupWindow消失的监听中恢复默认了
                conversionImg(tv_buyer_choice, R.mipmap.return_normal);
                setPopupwindow(v, mChoicePopupWindow, mChoiceView);
                //选中用户之前选择的状态
                setChoiceStatus();
                mChoicePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //消失触发，监听消失，改变箭头图标,恢复正常
                        conversionImg(tv_buyer_choice, R.mipmap.arrow_right);
                    }
                });
            }
        });
        //点击服务选项
        if (mServerRadioGroup != null) {
            mServerRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.rb_popFiltrate_serverOne:
                            radioButtonServerIndex = 0;
                            break;
                        case R.id.rb_popFiltrate_serverTwo:
                            radioButtonServerIndex = 1;
                            break;
                        case R.id.rb_popFiltrate_serverThree:
                            radioButtonServerIndex = 2;
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        //点击性别
        if (mSexRadioGroup != null) {
            mSexRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.rb_popFiltrate_sexOne:
                            radioButtonSexIndex = 0;
                            break;
                        case R.id.rb_popFiltrate_sexTwo:
                            radioButtonSexIndex = 1;
                            break;
                        case R.id.rb_popFiltrate_sexThree:
                            radioButtonSexIndex = 2;
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        //点击状态
        if (mStatusRadioGroup != null) {
            mStatusRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.rb_popFiltrate_StatusOne:
                            radioButtonStatusIndex = 0;
                            break;
                        case R.id.rb_popFiltrate_statusTwo:
                            radioButtonStatusIndex = 1;
                            break;
                        case R.id.rb_popFiltrate_statusThree:
                            radioButtonStatusIndex = 2;
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        //pop中的重置
        if (mTv_pop_reset != null) {
            mTv_pop_reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRb_popFiltrate_serverOne.setChecked(true);
                    mRb_popFiltrate_sexOne.setChecked(true);
                    mRb_popFiltrate_StatusOne.setChecked(true);
                }
            });
        }
        //pop中的确定
        if (tv_pop_decided != null) {
            tv_pop_decided.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                        return;
                    } else {
                        //记录下最后点击的位置
                        radioButtonFinalServerIndex = radioButtonServerIndex;
                        radioButtonFinalSexIndex = radioButtonSexIndex;
                        radioButtonFinalStatusIndex = radioButtonStatusIndex;

                        swichRefreshOrSort = 0;
                        //清0
                        offset = 0;
                        //关闭pop
                        if (mChoicePopupWindow != null && mChoicePopupWindow.isShowing()) {
                            mChoicePopupWindow.dismiss();
                        }
                        //查询和刷新buyer
                        sendParamsRequestNetwork();
                    }
                }
            });
        }
        //排序的点击事件
        tv_buyer_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //一进来箭头就向上，因为我在mPopupWindow消失的监听中恢复默认了
                conversionImg(tv_buyer_sort, R.mipmap.return_normal);
                setPopupwindow(v, mSortPopupWindow, mSortView);
                //根据点击记录来显示
                displayWhichSortClick(finalSortIndex);
                mSortPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //消失触发，监听消失，改变箭头图标,恢复正常
                        conversionImg(tv_buyer_sort, R.mipmap.arrow_right);
                    }
                });
            }
        });
        //pop星级点击事件
        mLl_popSort_grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    if (mSortPopupWindow != null && mSortPopupWindow.isShowing()) {
                        mSortPopupWindow.dismiss();
                    }
                    return;
                } else {
                    changleSortPopText(0, mIv_sort_gradeGou, mIv_sort_xinGou, mIv_sort_distanceGou, mTv_sort_grade, mTv_sort_xin, mTv_sort_distance);
                }
            }
        });
        //pop信用度点击事件
        mLl_popSort_xin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    if (mSortPopupWindow != null && mSortPopupWindow.isShowing()) {
                        mSortPopupWindow.dismiss();
                    }
                    return;
                } else {
                    changleSortPopText(1, mIv_sort_xinGou, mIv_sort_gradeGou, mIv_sort_distanceGou, mTv_sort_xin, mTv_sort_grade, mTv_sort_distance);

                }
            }
        });
        //pop距离点击事件
        mLl_popSort_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    if (mSortPopupWindow != null && mSortPopupWindow.isShowing()) {
                        mSortPopupWindow.dismiss();
                    }
                    return;
                } else {
                    changleSortPopText(2, mIv_sort_distanceGou, mIv_sort_xinGou, mIv_sort_gradeGou, mTv_sort_distance, mTv_sort_xin, mTv_sort_grade);

                }
            }
        });

        //刷新
        mBuyerPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                // here check $mListView instead of $content
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mainBuyerFragment_list, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //先让EditText光标消失
                mEditTextView.setCursorVisible(false);
                if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    mBuyerPtrFrame.refreshComplete();
                    return;
                } else {
                    //需要清0
                    offset = 0;
                    sortOffset = 0;
                    if (mIsLoginBoolean) {
                        //登陆过
                        if (swichRefreshOrSort == 0) {
                            //选择之后的刷新
                            if (radioButtonFinalServerIndex == 0 && radioButtonFinalSexIndex == 0 && radioButtonFinalStatusIndex == 0) {
                                loadNetworkData(true, "0", false, true, true, true);
                            } else {
                                loadNetworkDataFromChoice(true, "0", String.valueOf(radioButtonFinalSexIndex), String.valueOf(radioButtonFinalStatusIndex), false, true, true, true);
                            }
                        } else if (swichRefreshOrSort == 1) {
                            //排序后的刷新
                            if (finalSortIndex == 0) {
                                sortRequestNetWork(0, true, "0", false, true, true, true);
                            } else if (finalSortIndex == 1) {
                                sortRequestNetWork(1, true, "0", false, true, true, true);
                            } else if (finalSortIndex == 2) {
                                sortRequestNetWork(2, true, "0", false, true, true, true);
                            }
                        }

                    } else {
                        if (swichRefreshOrSort == 0) {
                            if (radioButtonFinalServerIndex == 0 && radioButtonFinalSexIndex == 0 && radioButtonFinalStatusIndex == 0) {
                                loadNetworkData(false, "0", false, true, true, true);
                            } else {
                                loadNetworkDataFromChoice(false, "0", String.valueOf(radioButtonFinalSexIndex), String.valueOf(radioButtonFinalStatusIndex), false, true, true, true);
                            }
                        } else if (swichRefreshOrSort == 1) {
                            //排序后的刷新
                            if (finalSortIndex == 0) {
                                sortRequestNetWork(0, false, "0", false, true, true, true);
                            } else if (finalSortIndex == 1) {
                                sortRequestNetWork(1, false, "0", false, true, true, true);
                            } else if (finalSortIndex == 2) {
                                sortRequestNetWork(2, false, "0", false, true, true, true);
                            }
                        }
                    }
                    //防止加载更多的时候，已经加了尾部布局，所以，先判断如果数据源小于8个,并且有尾部就删除
                    if (buyerBeanList.size() <= 8 && mainBuyerFragment_list.getFooterViewsCount() == 1) {
                        try {
                            mainBuyerFragment_list.removeFooterView(mFooterView);
                            mainBuyerListViewAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        });
        //让EditText光标显示
        mEditTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditTextView.setCursorVisible(true);
            }
        });
        //删除图标的点击事件
        if (ll_mainHead_llDeleteimg != null) {
            ll_mainHead_llDeleteimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //清空EditText
                    mEditTextView.setText("");
                    //隐藏自己
                    ib_mainHead_deleteimg.setVisibility(View.GONE);
                }
            });
        }

        //EditText的文字处理事件(让删除图标显示)
        if (mEditTextView != null) {
            mEditTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (TextUtils.isEmpty(mEditTextView.getText().toString().trim())) {
                        //隐藏自己
                        ib_mainHead_deleteimg.setVisibility(View.GONE);
                        //恢复
                        tv_buyer_city.setClickable(true);
                        tv_buyer_business.setClickable(true);
                        tv_buyer_sort.setClickable(true);
                        tv_buyer_choice.setClickable(true);
                    } else {
                        //一输入文字就显示图标
                        ib_mainHead_deleteimg.setVisibility(View.VISIBLE);
                        //并且上面的4个控件不能点击
                        tv_buyer_city.setClickable(false);
                        tv_buyer_business.setClickable(false);
                        tv_buyer_sort.setClickable(false);
                        tv_buyer_choice.setClickable(false);
                    }
                    //挑选之后的显示数据
                    //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                    filterData(s.toString().trim());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        //点击城市
        tv_buyer_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectCityActivity.class);
                intent.putExtra("fragmentIndex", 1);
                intent.putExtra("clickLocation", 0);
                startActivity(intent);
            }
        });
        //点击商圈
        tv_buyer_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(tv_buyer_city.getText().toString())) {
                    ToastUtils.showToastInUIThread(getActivity(), "请先选择城市");
                    return;
                } else {
                    Intent intent = new Intent(getActivity(), SelectCityActivity.class);
                    intent.putExtra("fragmentIndex", 1);
                    intent.putExtra("clickLocation", 1);
                    intent.putExtra("fragmentCityName", tv_buyer_city.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 显示哪个排序点击了
     */
    private void displayWhichSortClick(int sortindex) {
        switch (sortindex) {
            case 0:
                changeAllTextColor(mIv_sort_gradeGou, mIv_sort_xinGou, mIv_sort_distanceGou, mTv_sort_grade, mTv_sort_xin, mTv_sort_distance);
                break;
            case 1:
                changeAllTextColor(mIv_sort_xinGou, mIv_sort_gradeGou, mIv_sort_distanceGou, mTv_sort_xin, mTv_sort_grade, mTv_sort_distance);
                break;
            case 2:
                changeAllTextColor(mIv_sort_distanceGou, mIv_sort_xinGou, mIv_sort_gradeGou, mTv_sort_distance, mTv_sort_xin, mTv_sort_grade);
                break;
            default:
                break;
        }
    }

    /**
     * 刷选后的请求
     */
    private void sendParamsRequestNetwork() {

        if (mIsLoginBoolean) {//登录过
            loadNetworkDataFromChoice(true, "0", String.valueOf(radioButtonFinalSexIndex), String.valueOf(radioButtonFinalStatusIndex), true, false, true, true);
        } else {//没登录
            loadNetworkDataFromChoice(false, "0", String.valueOf(radioButtonFinalSexIndex), String.valueOf(radioButtonFinalStatusIndex), true, false, true, true);
        }
    }

    /**
     * 请求网络
     */
    private void loadNetworkDataFromChoice(boolean isLogin, String offset, String sexIndex, String statusIndex, boolean isNeedLoading, boolean isFromRefresh, boolean isNeedClearData, boolean isNeedSaveCache) {
        if (!TextUtils.isEmpty(mLatitude) && !TextUtils.isEmpty(mLongitude)) {

//            final boolean choiceIsFromRefresh = isFromRefresh;
//            final boolean choiceIsNeedClearData = isNeedClearData;
//            final boolean choiceIsNeedSaveCache = isNeedSaveCache;

            if (isNeedLoading) {
                //进度条展示
                mHandler.obtainMessage(LOADING).sendToTarget();
            }
            if (isLogin) {//登录过(还要判断各种用户选择)
                if (radioButtonFinalServerIndex == 0) {
                    OkHttpUtils.post().url(buyerFragmentUrl)
                            .addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("type", "6")
                            .addParams("user_id", mUserId)
                            .addParams("search_sex", sexIndex)
                            .addParams("search_status", statusIndex)
                            .addParams("search_video", "0")
                            .addParams("search_shopping", "0")
                            .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                } else if (radioButtonFinalServerIndex == 1) {
                    OkHttpUtils.post().url(buyerFragmentUrl)
                            .addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("type", "6")
                            .addParams("user_id", mUserId)
                            .addParams("search_sex", sexIndex)
                            .addParams("search_status", statusIndex)
                            .addParams("search_video", "1").build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                } else if (radioButtonFinalServerIndex == 2) {
                    OkHttpUtils.post().url(buyerFragmentUrl)
                            .addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("type", "6")
                            .addParams("user_id", mUserId)
                            .addParams("search_sex", sexIndex)
                            .addParams("search_status", statusIndex)
                            .addParams("search_shopping", "1").build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                }
            } else {//no
                if (radioButtonFinalServerIndex == 0) {
                    OkHttpUtils.post().url(buyerFragmentUrl)
                            .addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("type", "6")
                            .addParams("search_sex", sexIndex)
                            .addParams("search_status", statusIndex)
                            .addParams("search_video", "0")
                            .addParams("search_shopping", "0")
                            .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                } else if (radioButtonFinalServerIndex == 1) {
                    OkHttpUtils.post().url(buyerFragmentUrl)
                            .addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("type", "6")
                            .addParams("search_sex", sexIndex)
                            .addParams("search_status", statusIndex)
                            .addParams("search_video", "1").build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                } else if (radioButtonFinalServerIndex == 2) {
                    OkHttpUtils.post().url(buyerFragmentUrl)
                            .addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("type", "6")
                            .addParams("search_sex", sexIndex)
                            .addParams("search_status", statusIndex)
                            .addParams("search_shopping", "1").build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                }
            }
        }
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        mFilterDateList = new ArrayList<BuyerBean>();
        if (TextUtils.isEmpty(filterStr)) {
            //没有过滤条件，数据源不变
            mFilterDateList = buyerBeanList;
            mBuyerPtrFrame.setEnabled(true);
            //添加尾部
            //加之前，先判断数据源有几个，不够一屏幕显示，不加入
            ListViewUtils.isAddListViewBuyerFooterView(buyerBeanList, mainBuyerFragment_list, mFooterView);
        } else {
            mFilterDateList.clear();
            for (BuyerBean buyerModel : buyerBeanList) {
                String name = buyerModel.buyerName;
                if (name.indexOf(filterStr.toString()) != -1 || name.startsWith(filterStr.toString())) {
                    mFilterDateList.add(buyerModel);
                }
            }
            //如果有rootView，需要移除掉，在更新数据
            if (mainBuyerFragment_list.getFooterViewsCount() >= 1) {
                try {
                    //移除
                    mainBuyerFragment_list.removeFooterView(mFooterView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mBuyerPtrFrame.setEnabled(false);
        }

        mainBuyerListViewAdapter.updateListView(mFilterDateList);
    }

    /**
     * 改变排序pop里字体颜色和显示的变化
     */
    private void changleSortPopText(int index, ImageView iv1, ImageView iv2, ImageView iv3, TextView tv1, TextView tv2, TextView tv3) {

        changeAllTextColor(iv1, iv2, iv3, tv1, tv2, tv3);

        finalSortIndex = index;
        swichRefreshOrSort = 1;
        //qing 0
        sortOffset = 0;

        if (mSortPopupWindow != null && mSortPopupWindow.isShowing()) {
            mSortPopupWindow.dismiss();
        }
        //请求网络，重新加载数据
        if (mIsLoginBoolean) {//登陆过
            sortRequestNetWork(index, true, "0", true, false, true, true);
        } else {
            sortRequestNetWork(index, false, "0", true, false, true, true);
        }
    }

    private void sortRequestNetWork(int index, boolean isLogin, String offset, boolean isNeedLoading, boolean isFromRefresh, boolean isNeedClearData, boolean isNeedSaveCache) {
        if (!TextUtils.isEmpty(mLatitude) && !TextUtils.isEmpty(mLongitude)) {

            if (isNeedLoading) {
                //进度条展示
                mHandler.obtainMessage(LOADING).sendToTarget();
            }

            if (isLogin) {//登录过
                commonRequest(index, isLogin, offset, isFromRefresh, isNeedClearData, isNeedSaveCache);
            } else {//no
                commonRequest(index, isLogin, offset, isFromRefresh, isNeedClearData, isNeedSaveCache);
            }
        }
    }

    private void commonRequest(int index, boolean isLogin, String offset, boolean isFromRefresh, boolean isNeedClearData, boolean isNeedSaveCache) {
//        final boolean commonItemIsFromRefresh = isFromRefresh;
//        final boolean commonItemIsNeedClearData = isNeedClearData;
//        final boolean commonIsNeedSaveCache = isNeedSaveCache;
        if (isLogin) {
            //还需要判断用户是否选了涮选
            if (radioButtonFinalServerIndex == 0) {
                if (index == 0) {
                    OkHttpUtils.post().url(buyerFragmentUrl).addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("user_id", mUserId)
                            .addParams("type", "6")//Buyer列表类型，目的是在BUYER列表里，去掉隐藏状态的项。如果是BUYER列表，就传任何数字。如果是好友/关注/粉丝列表，该参数就可以不填
                            .addParams("orderby_star", "1")
                            .addParams("search_video", "0")
                            .addParams("search_shopping", "0")
                            .addParams("search_sex", String.valueOf(radioButtonFinalSexIndex))
                            .addParams("search_status", String.valueOf(radioButtonFinalStatusIndex))
                            .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                } else if (index == 1) {
                    OkHttpUtils.post().url(buyerFragmentUrl).addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("user_id", mUserId)
                            .addParams("type", "6")//Buyer列表类型，目的是在BUYER列表里，去掉隐藏状态的项。如果是BUYER列表，就传任何数字。如果是好友/关注/粉丝列表，该参数就可以不填
                            .addParams("orderby_credits", "1")
                            .addParams("search_video", "0")
                            .addParams("search_shopping", "0")
                            .addParams("search_sex", String.valueOf(radioButtonFinalSexIndex))
                            .addParams("search_status", String.valueOf(radioButtonFinalStatusIndex))
                            .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                } else if (index == 2) {
                    OkHttpUtils.post().url(buyerFragmentUrl).addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("user_id", mUserId)
                            .addParams("type", "6")//Buyer列表类型，目的是在BUYER列表里，去掉隐藏状态的项。如果是BUYER列表，就传任何数字。如果是好友/关注/粉丝列表，该参数就可以不填
                            .addParams("orderby_juli", "1")
                            .addParams("search_video", "0")
                            .addParams("search_shopping", "0")
                            .addParams("search_sex", String.valueOf(radioButtonFinalSexIndex))
                            .addParams("search_status", String.valueOf(radioButtonFinalStatusIndex))
                            .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                }
            } else if (radioButtonFinalServerIndex == 1) {
                if (index == 0) {
                    OkHttpUtils.post().url(buyerFragmentUrl).addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("user_id", mUserId)
                            .addParams("type", "6")//Buyer列表类型，目的是在BUYER列表里，去掉隐藏状态的项。如果是BUYER列表，就传任何数字。如果是好友/关注/粉丝列表，该参数就可以不填
                            .addParams("orderby_star", "1")
                            .addParams("search_video", "1")
                            .addParams("search_sex", String.valueOf(radioButtonFinalSexIndex))
                            .addParams("search_status", String.valueOf(radioButtonFinalStatusIndex))
                            .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                } else if (index == 1) {
                    OkHttpUtils.post().url(buyerFragmentUrl).addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("user_id", mUserId)
                            .addParams("type", "6")//Buyer列表类型，目的是在BUYER列表里，去掉隐藏状态的项。如果是BUYER列表，就传任何数字。如果是好友/关注/粉丝列表，该参数就可以不填
                            .addParams("orderby_credits", "1")
                            .addParams("search_video", "1")
                            .addParams("search_sex", String.valueOf(radioButtonFinalSexIndex))
                            .addParams("search_status", String.valueOf(radioButtonFinalStatusIndex))
                            .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                } else if (index == 2) {
                    OkHttpUtils.post().url(buyerFragmentUrl).addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("user_id", mUserId)
                            .addParams("type", "6")//Buyer列表类型，目的是在BUYER列表里，去掉隐藏状态的项。如果是BUYER列表，就传任何数字。如果是好友/关注/粉丝列表，该参数就可以不填
                            .addParams("orderby_juli", "1")
                            .addParams("search_video", "1")
                            .addParams("search_sex", String.valueOf(radioButtonFinalSexIndex))
                            .addParams("search_status", String.valueOf(radioButtonFinalStatusIndex))
                            .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                }
            } else if (radioButtonFinalServerIndex == 2) {
                if (index == 0) {
                    OkHttpUtils.post().url(buyerFragmentUrl).addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("user_id", mUserId)
                            .addParams("type", "6")//Buyer列表类型，目的是在BUYER列表里，去掉隐藏状态的项。如果是BUYER列表，就传任何数字。如果是好友/关注/粉丝列表，该参数就可以不填
                            .addParams("orderby_star", "1")
                            .addParams("search_shopping", "1")
                            .addParams("search_sex", String.valueOf(radioButtonFinalSexIndex))
                            .addParams("search_status", String.valueOf(radioButtonFinalStatusIndex))
                            .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                } else if (index == 1) {
                    OkHttpUtils.post().url(buyerFragmentUrl).addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("user_id", mUserId)
                            .addParams("type", "6")//Buyer列表类型，目的是在BUYER列表里，去掉隐藏状态的项。如果是BUYER列表，就传任何数字。如果是好友/关注/粉丝列表，该参数就可以不填
                            .addParams("orderby_credits", "1")
                            .addParams("search_shopping", "1")
                            .addParams("search_sex", String.valueOf(radioButtonFinalSexIndex))
                            .addParams("search_status", String.valueOf(radioButtonFinalStatusIndex))
                            .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                } else if (index == 2) {
                    OkHttpUtils.post().url(buyerFragmentUrl).addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("user_id", mUserId)
                            .addParams("type", "6")//Buyer列表类型，目的是在BUYER列表里，去掉隐藏状态的项。如果是BUYER列表，就传任何数字。如果是好友/关注/粉丝列表，该参数就可以不填
                            .addParams("orderby_juli", "1")
                            .addParams("search_shopping", "1")
                            .addParams("search_sex", String.valueOf(radioButtonFinalSexIndex))
                            .addParams("search_status", String.valueOf(radioButtonFinalStatusIndex))
                            .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                }
            }

        } else {
            //没登录
            if (radioButtonFinalServerIndex == 0) {
                if (index == 0) {
                    OkHttpUtils.post().url(buyerFragmentUrl).addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("type", "6")//Buyer列表类型，目的是在BUYER列表里，去掉隐藏状态的项。如果是BUYER列表，就传任何数字。如果是好友/关注/粉丝列表，该参数就可以不填
                            .addParams("orderby_star", "1")
                            .addParams("search_video", "0")
                            .addParams("search_shopping", "0")
                            .addParams("search_sex", String.valueOf(radioButtonFinalSexIndex))
                            .addParams("search_status", String.valueOf(radioButtonFinalStatusIndex))
                            .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                } else if (index == 1) {
                    OkHttpUtils.post().url(buyerFragmentUrl).addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("type", "6")//Buyer列表类型，目的是在BUYER列表里，去掉隐藏状态的项。如果是BUYER列表，就传任何数字。如果是好友/关注/粉丝列表，该参数就可以不填
                            .addParams("orderby_credits", "1")
                            .addParams("search_video", "0")
                            .addParams("search_shopping", "0")
                            .addParams("search_sex", String.valueOf(radioButtonFinalSexIndex))
                            .addParams("search_status", String.valueOf(radioButtonFinalStatusIndex))
                            .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                } else if (index == 2) {
                    OkHttpUtils.post().url(buyerFragmentUrl).addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("type", "6")//Buyer列表类型，目的是在BUYER列表里，去掉隐藏状态的项。如果是BUYER列表，就传任何数字。如果是好友/关注/粉丝列表，该参数就可以不填
                            .addParams("orderby_juli", "1")
                            .addParams("search_video", "0")
                            .addParams("search_shopping", "0")
                            .addParams("search_sex", String.valueOf(radioButtonFinalSexIndex))
                            .addParams("search_status", String.valueOf(radioButtonFinalStatusIndex))
                            .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                }
            } else if (radioButtonFinalServerIndex == 1) {
                if (index == 0) {
                    OkHttpUtils.post().url(buyerFragmentUrl).addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("type", "6")//Buyer列表类型，目的是在BUYER列表里，去掉隐藏状态的项。如果是BUYER列表，就传任何数字。如果是好友/关注/粉丝列表，该参数就可以不填
                            .addParams("orderby_star", "1")
                            .addParams("search_video", "1")
                            .addParams("search_sex", String.valueOf(radioButtonFinalSexIndex))
                            .addParams("search_status", String.valueOf(radioButtonFinalStatusIndex))
                            .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                } else if (index == 1) {
                    OkHttpUtils.post().url(buyerFragmentUrl).addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("type", "6")//Buyer列表类型，目的是在BUYER列表里，去掉隐藏状态的项。如果是BUYER列表，就传任何数字。如果是好友/关注/粉丝列表，该参数就可以不填
                            .addParams("orderby_credits", "1")
                            .addParams("search_video", "1")
                            .addParams("search_sex", String.valueOf(radioButtonFinalSexIndex))
                            .addParams("search_status", String.valueOf(radioButtonFinalStatusIndex))
                            .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                } else if (index == 2) {
                    OkHttpUtils.post().url(buyerFragmentUrl).addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("type", "6")//Buyer列表类型，目的是在BUYER列表里，去掉隐藏状态的项。如果是BUYER列表，就传任何数字。如果是好友/关注/粉丝列表，该参数就可以不填
                            .addParams("orderby_juli", "1")
                            .addParams("search_video", "1")
                            .addParams("search_sex", String.valueOf(radioButtonFinalSexIndex))
                            .addParams("search_status", String.valueOf(radioButtonFinalStatusIndex))
                            .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                }
            } else if (radioButtonFinalServerIndex == 2) {
                if (index == 0) {
                    OkHttpUtils.post().url(buyerFragmentUrl).addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("type", "6")//Buyer列表类型，目的是在BUYER列表里，去掉隐藏状态的项。如果是BUYER列表，就传任何数字。如果是好友/关注/粉丝列表，该参数就可以不填
                            .addParams("orderby_star", "1")
                            .addParams("search_shopping", "1")
                            .addParams("search_sex", String.valueOf(radioButtonFinalSexIndex))
                            .addParams("search_status", String.valueOf(radioButtonFinalStatusIndex))
                            .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                } else if (index == 1) {
                    OkHttpUtils.post().url(buyerFragmentUrl).addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("type", "6")//Buyer列表类型，目的是在BUYER列表里，去掉隐藏状态的项。如果是BUYER列表，就传任何数字。如果是好友/关注/粉丝列表，该参数就可以不填
                            .addParams("orderby_credits", "1")
                            .addParams("search_shopping", "1")
                            .addParams("search_sex", String.valueOf(radioButtonFinalSexIndex))
                            .addParams("search_status", String.valueOf(radioButtonFinalStatusIndex))
                            .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                } else if (index == 2) {
                    OkHttpUtils.post().url(buyerFragmentUrl).addParams("area_id", mArea_id)
                            .addParams("lat", mLatitude)
                            .addParams("lng", mLongitude)
                            .addParams("offset", offset)
                            .addParams("count", String.valueOf(count))
                            .addParams("type", "6")//Buyer列表类型，目的是在BUYER列表里，去掉隐藏状态的项。如果是BUYER列表，就传任何数字。如果是好友/关注/粉丝列表，该参数就可以不填
                            .addParams("orderby_juli", "1")
                            .addParams("search_shopping", "1")
                            .addParams("search_sex", String.valueOf(radioButtonFinalSexIndex))
                            .addParams("search_status", String.valueOf(radioButtonFinalStatusIndex))
                            .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isNeedSaveCache));
                }
            }
        }
    }

    /**
     * 初始化popup
     */
    private void setPopupwindow(View parentView, PopupWindow mPopupWindow, View view) {
        //一进来箭头就向上，因为我在mPopupWindow消失的监听中恢复默认了
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            //设置透明背景，可以实现动画，可以点击空白处消失
            //getResources().getDrawable(R.drawable.popupwindow_bg)
            mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popupwindow_bg));
            //可以点击空白处消失,与设置背景一起使用
            mPopupWindow.setOutsideTouchable(true);
            //获取焦点,里面的事件可以获取焦点
            mPopupWindow.setFocusable(true);
            //开始动画(将要显示的view加动画)
            //            Animation animation = AnimationUtils.loadAnimation(UIUtils.getContext(),R.anim.alpha_animation_popupwindow);
            //            if (view != null) {
            //                view.startAnimation(animation);
            //            }
            //x,y相对整个屏幕来说的
            //得到按钮的想，x,y
            int[] location = new int[2];
            // 获得位置
            parentView.getLocationOnScreen(location);
            tv_buyer_choice.getLocationInWindow(location);
            mPopupWindow.showAsDropDown(parentView);
            //mPopupWindow.showAtLocation(tv_buyer_choice, Gravity.LEFT | Gravity.TOP, 0, location[1] + tv_buyer_choice.getHeight());
        }
    }

    /**
     * 刷选的变化图标
     */
    private void conversionImg(TextView textView, int id) {
        try {
            Drawable arrow_right = getResources().getDrawable(id);
            arrow_right.setBounds(0, 0, arrow_right.getMinimumWidth(), arrow_right.getMinimumHeight());
            textView.setCompoundDrawables(null, null, arrow_right, null);
        } catch (Exception e) {
        }
    }

    /**
     * 操作popupwindow
     */
    private void opearPopupwindow() {
        //刷选
        mChoiceView = View.inflate(UIUtils.getContext(), R.layout.popupwindow_filtrate, null);
        //找到radioGroup
        findServerRadioGroup(mChoiceView);
        mChoicePopupWindow = new PopupWindow(mChoiceView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //排序
        mSortView = View.inflate(UIUtils.getContext(), R.layout.popupwindow_sort, null);
        mSortPopupWindow = new PopupWindow(mSortView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        findPopSortId(mSortView);
    }

    /**
     * 找到服务、性别、状态的选项
     */
    private void findServerRadioGroup(View view) {
        mServerRadioGroup = (RadioGroup) view.findViewById(R.id.rg_popFiltrate_server);
        mRb_popFiltrate_serverOne = (RadioButton) view.findViewById(R.id.rb_popFiltrate_serverOne);
        mRb_popFiltrate_serverTwo = (RadioButton) view.findViewById(R.id.rb_popFiltrate_serverTwo);
        mRb_popFiltrate_serverThree = (RadioButton) view.findViewById(R.id.rb_popFiltrate_serverThree);

        mSexRadioGroup = (RadioGroup) view.findViewById(R.id.rg_popFiltrate_sex);
        mRb_popFiltrate_sexOne = (RadioButton) view.findViewById(R.id.rb_popFiltrate_sexOne);
        mRb_popFiltrate_sexTwo = (RadioButton) view.findViewById(R.id.rb_popFiltrate_sexTwo);
        mRb_popFiltrate_sexThree = (RadioButton) view.findViewById(R.id.rb_popFiltrate_sexThree);


        mStatusRadioGroup = (RadioGroup) view.findViewById(R.id.rg_popFiltrate_status);
        mRb_popFiltrate_StatusOne = (RadioButton) view.findViewById(R.id.rb_popFiltrate_StatusOne);
        mRb_popFiltrate_StatusTwo = (RadioButton) view.findViewById(R.id.rb_popFiltrate_statusTwo);
        mRb_popFiltrate_StatusThree = (RadioButton) view.findViewById(R.id.rb_popFiltrate_statusThree);

        mRb_popFiltrate_serverOne.setChecked(true);
        mRb_popFiltrate_sexOne.setChecked(true);
        mRb_popFiltrate_StatusOne.setChecked(true);
        //重置
        mTv_pop_reset = (TextView) view.findViewById(R.id.tv_pop_reset);
        //确定
        tv_pop_decided = (TextView) view.findViewById(R.id.tv_pop_decided);
    }

    /**
     * 找到排序的pop控件
     */
    private void findPopSortId(View mView) {
        //pop星级
        mLl_popSort_grade = (LinearLayout) mView.findViewById(R.id.ll_popSort_grade);
        mIv_sort_gradeGou = (ImageView) mView.findViewById(R.id.iv_sort_gradeGou);
        mTv_sort_grade = (TextView) mView.findViewById(R.id.tv_sort_grade);

        //pop信用度
        mLl_popSort_xin = (LinearLayout) mView.findViewById(R.id.ll_popSort_xin);
        mIv_sort_xinGou = (ImageView) mView.findViewById(R.id.iv_sort_xinGou);
        mTv_sort_xin = (TextView) mView.findViewById(R.id.tv_sort_xin);

        //pop距离
        mLl_popSort_distance = (LinearLayout) mView.findViewById(R.id.ll_popSort_distance);
        mIv_sort_distanceGou = (ImageView) mView.findViewById(R.id.iv_sort_distanceGou);
        mTv_sort_distance = (TextView) mView.findViewById(R.id.tv_sort_distance);
    }

    /**
     * 操作ListView
     */
    private void operateBuyerListView() {

        //必须在setAdapter之前
        //加之前，先判断数据源有几个，不够一屏幕显示，不加入
        ListViewUtils.isAddListViewBuyerFooterView(buyerBeanList, mainBuyerFragment_list, mFooterView);
        //添加head视图
        //只允许添加一次
        if (mainBuyerFragment_list.getHeaderViewsCount() == 0) {
            mainBuyerFragment_list.addHeaderView(mHeadView, null, true);
        }
        mainBuyerListViewAdapter = new MainBuyerListViewAdapter(buyerBeanList);

        mainBuyerFragment_list.setAdapter(mainBuyerListViewAdapter);
    }

    /**
     * 设置最后选中的状态
     */
    private void setChoiceStatus() {

        if (radioButtonFinalServerIndex == 0) {
            mRb_popFiltrate_serverOne.setChecked(true);
            mRb_popFiltrate_serverTwo.setChecked(false);
            mRb_popFiltrate_serverThree.setChecked(false);
        } else if (radioButtonFinalServerIndex == 1) {
            mRb_popFiltrate_serverOne.setChecked(false);
            mRb_popFiltrate_serverTwo.setChecked(true);
            mRb_popFiltrate_serverThree.setChecked(false);
        } else if (radioButtonFinalServerIndex == 2) {
            mRb_popFiltrate_serverOne.setChecked(false);
            mRb_popFiltrate_serverTwo.setChecked(false);
            mRb_popFiltrate_serverThree.setChecked(true);
        }

        if (radioButtonFinalSexIndex == 0) {
            mRb_popFiltrate_sexOne.setChecked(true);
            mRb_popFiltrate_sexTwo.setChecked(false);
            mRb_popFiltrate_sexThree.setChecked(false);
        } else if (radioButtonFinalSexIndex == 1) {
            mRb_popFiltrate_sexOne.setChecked(false);
            mRb_popFiltrate_sexTwo.setChecked(true);
            mRb_popFiltrate_sexThree.setChecked(false);
        } else if (radioButtonFinalSexIndex == 2) {
            mRb_popFiltrate_sexOne.setChecked(false);
            mRb_popFiltrate_sexTwo.setChecked(false);
            mRb_popFiltrate_sexThree.setChecked(true);
        }

        if (radioButtonFinalStatusIndex == 0) {
            mRb_popFiltrate_StatusOne.setChecked(true);
            mRb_popFiltrate_StatusTwo.setChecked(false);
            mRb_popFiltrate_StatusThree.setChecked(false);
        } else if (radioButtonFinalStatusIndex == 1) {
            mRb_popFiltrate_StatusOne.setChecked(false);
            mRb_popFiltrate_StatusTwo.setChecked(true);
            mRb_popFiltrate_StatusThree.setChecked(false);
        } else if (radioButtonFinalStatusIndex == 2) {
            mRb_popFiltrate_StatusOne.setChecked(false);
            mRb_popFiltrate_StatusTwo.setChecked(false);
            mRb_popFiltrate_StatusThree.setChecked(true);
        }
    }

    /**
     * 加载网络的数据
     */
    private void loadNetworkData(boolean isLogin, String offset, boolean isNeedLoading, boolean isFromRefresh, boolean isNeedClearData, boolean isFirstSave) {
        if (!TextUtils.isEmpty(mLatitude) && !TextUtils.isEmpty(mLongitude)) {

//            final boolean itemIsFromRefresh = isFromRefresh;
//            final boolean itemIsNeedClearData = isNeedClearData;
//            final boolean itemIsFirstSave = isFirstSave;

            if (isNeedLoading) {
                //进度条展示
                mHandler.obtainMessage(LOADING).sendToTarget();
            }

            if (isLogin) {//登录过
                OkHttpUtils.post().url(buyerFragmentUrl).addParams("area_id", mArea_id)
                        .addParams("lat", mLatitude)
                        .addParams("lng", mLongitude)
                        .addParams("offset", offset)
                        .addParams("count", String.valueOf(count))
                        .addParams("user_id", mUserId)
                        .addParams("type", "6")//Buyer列表类型，目的是在BUYER列表里，去掉隐藏状态的项。如果是BUYER列表，就传任何数字。如果是好友/关注/粉丝列表，该参数就可以不填
                        .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isFirstSave));
            } else {//no
                OkHttpUtils.post().url(buyerFragmentUrl).addParams("area_id", mArea_id)
                        .addParams("lat", mLatitude)
                        .addParams("lng", mLongitude)
                        .addParams("offset", offset)
                        .addParams("count", String.valueOf(count))
                        .addParams("type", "6")//Buyer列表类型，目的是在BUYER列表里，去掉隐藏状态的项。如果是BUYER列表，就传任何数字。如果是好友/关注/粉丝列表，该参数就可以不填
                        .build().execute(new CommonRequestCall(isFromRefresh,isNeedClearData,isFirstSave));
            }
        }
    }

    /**
     * 处理网络返回的结果
     */
    private void operationResult(String response, boolean itemIsFromRefresh, boolean itemIsNeedClearData, boolean isFirstSave) {
        try {
            //先解析reponse_code,给客户提醒
            Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
            String response_code = map.get("response_code");
            //需要判断各种响应码,获取具体错误信息
            String responseContent = RequestResponseUtils.getResponseContent(response_code);
            if ("0".equals(response_code)) {
                //保存缓存（只保留第一次的，因为加载更多后后面的json数据会覆盖掉）
                if (isFirstSave) {
                    SpUtils.putCacheString(MyConstants.BUYERSPNAME, UIUtils.getContext(), MyConstants.BUYERFRAGMENTJSON, response);
                }

                //可以下一步解析buyer
                List<BuyerBean> tempList = BuyerFragmentParse.buyerFragmentParse(response);

                if (tempList != null) {
                    //可能之前已经有尾巴，再次进来只加载了小于6条数据就可能看到尾巴
                    if(tempList.size() <= 6 && mainBuyerFragment_list.getFooterViewsCount() == 1) {
                        try {
                            mainBuyerFragment_list.removeFooterView(mFooterView);
                            mainBuyerListViewAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            //发送handler，隐藏进度条
                            mHandler.obtainMessage(SUCCESS).sendToTarget();
                        }
                    }
                    if (itemIsNeedClearData) {
                        buyerBeanList.clear();
                        buyerBeanList.addAll(tempList);
                        ListViewUtils.isAddListViewBuyerFooterView(buyerBeanList, mainBuyerFragment_list, mFooterView);
                    } else {
                        //加载更多
                        if (tempList.size() == 0) {
                            if (mainBuyerFragment_list.getFooterViewsCount() == 1) {
                                try {
                                    mainBuyerFragment_list.removeFooterView(mFooterView);
                                    mainBuyerListViewAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    //发送handler，隐藏进度条
                                    mHandler.obtainMessage(SUCCESS).sendToTarget();
                                }
                            }
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "没有数据了");
                        } else {
                            buyerBeanList.addAll(tempList);
                            ListViewUtils.isAddListViewBuyerFooterView(buyerBeanList, mainBuyerFragment_list, mFooterView);
                        }
                    }
                    //刷新完成
                    if (mBuyerPtrFrame != null && itemIsFromRefresh) {
                        mBuyerPtrFrame.refreshComplete();
                    }
                }
                //发送handler，隐藏进度条
                mHandler.obtainMessage(SUCCESS).sendToTarget();
            } else if (responseContent != null) {
                //给出错误提示
                ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                if (mBuyerPtrFrame != null && itemIsFromRefresh) {
                    mBuyerPtrFrame.refreshComplete();
                }
                if (MyConstants.FOOTVIEWNAME.equals(responseContent)) {
                    if (mainBuyerFragment_list.getFooterViewsCount() == 1) {
                        try {
                            mainBuyerFragment_list.removeFooterView(mFooterView);
                            mainBuyerListViewAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            //发送handler，隐藏进度条
                            mHandler.obtainMessage(SUCCESS).sendToTarget();
                        }
                    }
                }
                if (MyConstants.FOOTVIEWNAME.equals(responseContent) && itemIsNeedClearData && radioButtonFinalStatusIndex == 2) {
                    //没有数据,qingkong数据源
                    buyerBeanList.clear();
                    mainBuyerListViewAdapter.notifyDataSetChanged();
                }
                //数据源里全是女的或者全是男的
                if (radioButtonFinalSexIndex == 1 || radioButtonFinalSexIndex == 2) {
                    if (MyConstants.FOOTVIEWNAME.equals(responseContent)) {
                        if(buyerBeanList.size() > 0 && Integer.parseInt(buyerBeanList.get(0).sex) != radioButtonFinalSexIndex) {
                            //没有数据,qingkong数据源
                            buyerBeanList.clear();
                            mainBuyerListViewAdapter.notifyDataSetChanged();
                        }
                    }
                }
                //发送handler，隐藏进度条
                mHandler.obtainMessage(SUCCESS).sendToTarget();
            }
        } catch (Exception e) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
            if (mBuyerPtrFrame != null && itemIsFromRefresh) {
                mBuyerPtrFrame.refreshComplete();
            }
            if (mainBuyerFragment_list.getFooterViewsCount() == 1) {
                try {
                    mainBuyerFragment_list.removeFooterView(mFooterView);
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

    public void changeAllTextColor(ImageView iv1, ImageView iv2, ImageView iv3, TextView tv1, TextView tv2, TextView tv3) {
        iv1.setVisibility(View.VISIBLE);
        tv1.setTextColor(UIUtils.getResources().getColor(R.color.colorMainTitle));
        //其他恢复默认
        iv2.setVisibility(View.INVISIBLE);
        tv2.setTextColor(UIUtils.getResources().getColor(R.color.colorpoptitle));
        iv3.setVisibility(View.INVISIBLE);
        tv3.setTextColor(UIUtils.getResources().getColor(R.color.colorpoptitle));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //将offset默认值
        offset = 0;
        finalSortIndex = 2;
        swichRefreshOrSort = 0;
        //关闭pop
        if (mChoicePopupWindow != null && mChoicePopupWindow.isShowing()) {
            mChoicePopupWindow.dismiss();
            mChoicePopupWindow = null;
        }
        //关闭pop
        if (mSortPopupWindow != null && mSortPopupWindow.isShowing()) {
            mSortPopupWindow.dismiss();
            mSortPopupWindow = null;
        }
    }

    /**
     * 排序公共的请求回调
     */
    class CommonRequestCall extends StringCallback {

        private boolean commonItemIsFromRefresh;
        private boolean commonItemIsNeedClearData;
        private boolean commonIsNeedSaveCache;

        public CommonRequestCall(boolean commonItemIsFromRefresh, boolean commonItemIsNeedClearData, boolean commonIsNeedSaveCache) {
            this.commonItemIsFromRefresh = commonItemIsFromRefresh;
            this.commonItemIsNeedClearData = commonItemIsNeedClearData;
            this.commonIsNeedSaveCache = commonIsNeedSaveCache;
        }

        @Override
        public void onError(Call call, Exception e, int id) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                if (mBuyerPtrFrame != null && commonItemIsFromRefresh) {
                    mBuyerPtrFrame.refreshComplete();
                }
                //发送handler，隐藏进度条
                mHandler.obtainMessage(SUCCESS).sendToTarget();
        }

        @Override
        public void onResponse(String response, int id) {
            operationResult(response, commonItemIsFromRefresh, commonItemIsNeedClearData, commonIsNeedSaveCache);
        }
    }
    /**
     * 给外部刷新数据
     */
    public void refreshBuyerFragmentData() {
        //重新获取经纬度
        //经度
        mLongitude = SpUtils.getSpString(UIUtils.getContext(), MyConstants.LONGITUDE, "");
        //纬度
        mLatitude = SpUtils.getSpString(UIUtils.getContext(), MyConstants.LATITUDE, "");
        loadNetworkData(false, "0", true, false, true, true);
    }
}
