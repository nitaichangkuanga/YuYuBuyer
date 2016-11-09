package com.wushuikeji.www.yuyubuyer.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.DynamicDetailsListViewAdapter;
import com.wushuikeji.www.yuyubuyer.bean.jsonbean.DynamicBean;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.DynamicParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.BitmapUtils;
import com.wushuikeji.www.yuyubuyer.utils.GetNormalOptionsUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.ListViewUtils;
import com.wushuikeji.www.yuyubuyer.utils.PtrFrameRefreshUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.wushuikeji.www.yuyubuyer.view.InputMethodLayout;
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

public class MyDynamicDetailsActivity extends AppCompatActivity {

    @InjectView(R.id.rl_mdDetails_back)
    RelativeLayout rl_mdDetails_back;

    @InjectView(R.id.pb_loading_dyDetailsProgressBar)
    ProgressBar pb_loading_dyDetailsProgressBar;

    @InjectView(R.id.in_dyDetails_pf)
    PtrClassicFrameLayout ptr;

    @InjectView(R.id.mdDetails_listView)
    ListView mdDetails_listView;

    @InjectView(R.id.custom_dyDetails_input)
    InputMethodLayout custom_dyDetails_input;

    @InjectView(R.id.rl_bottom)
    RelativeLayout rl_bottom;

    //输入框
    @InjectView(R.id.ce_dnDetails_input)
    EditText ce_dnDetails_input;

    //表情
    @InjectView(R.id.iv_dnDetails_emoIv)
    ImageView iv_dnDetails_emoIv;

    //发送按钮
    @InjectView(R.id.b_dnDetails_send)
    Button b_dnDetails_send;

    private List<DynamicBean> dynamicDetailsList = new ArrayList<DynamicBean>();

    private DynamicDetailsListViewAdapter mDynamicDetailsListViewAdapter;
    //获取动态评论列表
    private String trendUrl = Constants.commontUrl + "trend/comments";
    //发送动态评论
    private String sendEvaluateUrl = Constants.commontUrl + "trend/reply";

    private static final int LOADING = 0;
    private static final int SUCCESS = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_loading_dyDetailsProgressBar.setVisibility(View.VISIBLE);
                    ptr.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    pb_loading_dyDetailsProgressBar.setVisibility(View.GONE);
                    ptr.setVisibility(View.VISIBLE);
                    //更新数据
                    if (mDynamicDetailsListViewAdapter != null) {
                        mDynamicDetailsListViewAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private int count = 10;
    private int offset = 0;

    private boolean isBottom;
    private View mFooterView;
    private String mUserId;
    private String mTrendId;
    private String mVideoUrl;
    private ImageView mTouView;
    private TextView mUserNameView;
    private TextView mUserDateView;
    private TextView mUserTimeView;
    private TextView mUserContentView;
    private ImageView mUserVideoView;
    private ImageView mreplyView;
    private View mHeadView;
    private InputMethodManager mInputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dynamic_details);
        initView();
        initHead();
        initData();
        initEvent();
    }


    private void initView() {
        ButterKnife.inject(this);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");
        //将尾部布局蹦出来
        mFooterView = View.inflate(UIUtils.getContext(), R.layout.item_listview_mainshop_loadmore, null);

        //去除系统自带的颜色渐变
        mdDetails_listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mHeadView = View.inflate(UIUtils.getContext(), R.layout.item_listview_dynamic, null);

        //添加head视图
        //只允许添加一次
        if (mdDetails_listView.getHeaderViewsCount() == 0) {
            mdDetails_listView.addHeaderView(mHeadView, null, true);
        }
        mDynamicDetailsListViewAdapter = new DynamicDetailsListViewAdapter(dynamicDetailsList, UIUtils.getContext());

        mdDetails_listView.setAdapter(mDynamicDetailsListViewAdapter);

        //刷新的操作
        ptr.setLastUpdateTimeRelateObject(this);
        PtrFrameRefreshUtils.setRefreshParams(ptr);

        //找出mHeadView里的控件
        //头像
        mTouView = (ImageView) mHeadView.findViewById(R.id.iv_itemDy_img);
        mUserNameView = (TextView) mHeadView.findViewById(R.id.tv_itemDy_userName);
        mUserDateView = (TextView) mHeadView.findViewById(R.id.tv_itemDy_date);
        mUserTimeView = (TextView) mHeadView.findViewById(R.id.tv_itemDy_time);
        mUserContentView = (TextView) mHeadView.findViewById(R.id.tv_itemDy_content);
        mUserVideoView = (ImageView) mHeadView.findViewById(R.id.iv_itemDy_video);
        //小短信图片
        mreplyView = (ImageView) mHeadView.findViewById(R.id.iv_itemDy_reply);
    }

    private void initHead() {
        Intent intent = getIntent();
        mTrendId = intent.getStringExtra("trendId");
        String mImgUrlString = intent.getStringExtra("imgUrl");
        String mUserNameString = intent.getStringExtra("userName");
        String mDateString = intent.getStringExtra("date");
        String mTimeString = intent.getStringExtra("time");
        String mContentString = intent.getStringExtra("content");
        mVideoUrl = intent.getStringExtra("videoUrl");

        mUserNameView.setText(mUserNameString);
        mUserDateView.setText(mDateString);
        mUserTimeView.setText(mTimeString);
        mUserContentView.setText(mContentString);
        //设置头像
        if (mTouView != null) {
            ImageLoader.getInstance().displayImage(mImgUrlString, mTouView, GetNormalOptionsUtils.getNormalOptionsUtils());
        }
        //设置视频缩约图
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取视频的缩略图（宽高设置没用，内部处理了）,耗时的方法
                final Bitmap bitmap = BitmapUtils.createVideoThumbnail(mVideoUrl, 200, 200);
                UIUtils.getMainThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (bitmap != null) {
                            mUserVideoView.setImageBitmap(bitmap);
                        } else {
                            mUserVideoView.setImageResource(R.mipmap.zanwutupian);
                        }
                    }
                });
            }
        }).start();
    }

    private void initData() {
        if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            loadNetworkData("0", true, false, true);
        }
    }

    private void initEvent() {
        //返回
        BackListenerUtils.backFinish(rl_mdDetails_back, this);
        //点击视频播放
        if (mUserVideoView != null) {
            mUserVideoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转到播放视频的界面
                    Intent intent = new Intent(UIUtils.getContext(), PlayVideoActivity.class);
                    intent.putExtra("url", mVideoUrl);
                    startActivity(intent);
                }
            });
        }
        //点击整个mHeadView
        if (mHeadView != null) {
            mHeadView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rl_bottom.setVisibility(View.VISIBLE);
                    //输入法自动弹出，自动收缩
                    mInputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            });
        }
        //监听软键盘的打开和关闭
        if (custom_dyDetails_input != null) {
            custom_dyDetails_input.setOnkeyboarddStateListener(new InputMethodLayout.onKeyboardsChangeListener() {
                @Override
                public void onKeyBoardStateChange(int state) {
                    switch (state) {
                        case InputMethodLayout.KEYBOARD_STATE_SHOW:
                            //弹出最下面隐藏的评论输入框
                            rl_bottom.setVisibility(View.VISIBLE);
                            ce_dnDetails_input.requestFocus();
                            //mdDetails_listView.setEnabled(false);
                            break;
                        case InputMethodLayout.KEYBOARD_STATE_HIDE:
                            rl_bottom.setVisibility(View.GONE);
                            //mdDetails_listView.setEnabled(true);
                            break;
                    }
                }
            });
        }
        //对输入框的监听
        ce_dnDetails_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(ce_dnDetails_input.getText().toString().trim())) {
                    //恢复默认
                    b_dnDetails_send.setClickable(false);
                    try {
                        b_dnDetails_send.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.input_button_shap));
                        b_dnDetails_send.setTextColor(UIUtils.getResources().getColor(R.color.colorregister));
                    } catch (Exception exception) {
                    }
                } else {
                    b_dnDetails_send.setClickable(true);
                    try {
                        b_dnDetails_send.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.inputpre_button_shap));
                        b_dnDetails_send.setTextColor(UIUtils.getResources().getColor(R.color.colorCustomEditText));
                    } catch (Exception exception) {
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //发送评论
        b_dnDetails_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    return;
                }else {
                    b_dnDetails_send.requestFocus();
                    //请求网络,发送动态评论
                    requestEvaluate(ce_dnDetails_input.getText().toString().trim());
                }
            }
        });
        //一进来发送就不可以点击
        b_dnDetails_send.setClickable(false);

        //刷新
        ptr.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                // here check $mListView instead of $content
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mdDetails_listView, header);
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
                    if (dynamicDetailsList.size() <= 6 && mdDetails_listView.getFooterViewsCount() == 1) {
                        try {
                            mdDetails_listView.removeFooterView(mFooterView);
                            mDynamicDetailsListViewAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        });

        //listView的滑动事件
        mdDetails_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 数据已经到底部，而且用户是松手状态，这个时候需要加载新的数据
                if (isBottom && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                    if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                        return;
                    } else {
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

        //mdDetails_listView的item点击
        mdDetails_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0) {
                    rl_bottom.setVisibility(View.VISIBLE);
                    //输入法自动弹出，自动收缩
                    mInputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
    }


    /**
     * 点击了整个mHeadView，发送动态评论
     */
    private void requestEvaluate(String content) {

        OkHttpUtils.post().url(sendEvaluateUrl).addParams("trend_id", mTrendId)
                .addParams("content", content)
                .addParams("user_id", mUserId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    //先解析reponse_code,给客户提醒
                    Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                    String response_code = map.get("response_code");
                    //需要判断各种响应码,获取具体错误信息
                    String responseContent = RequestResponseUtils.getResponseContent(response_code);
                    if (!"0".equals(response_code)) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "评论失败");
                    } else {
                        //关闭输入法,会触发监听将bootm隐藏
                        ce_dnDetails_input.setText("");
                        mInputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        //成功评论后，自动刷新数据源，
                        ptr.autoRefresh();
                        //并显示第一条
                        mdDetails_listView.setSelection(0);
                    }
                } catch (Exception e) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                }
            }
        });
    }

    private void loadNetworkData(String offset, boolean isNeedLoading, boolean isFromRefresh, boolean isNeedClearData) {

        final boolean commonItemIsFromRefresh = isFromRefresh;

        final boolean itemIsNeedClearData = isNeedClearData;

        if (isNeedLoading) {
            //进度条展示
            mHandler.obtainMessage(LOADING).sendToTarget();
        }
        OkHttpUtils.post().url(trendUrl).addParams("trend_id", mTrendId)
                .addParams("offset", offset)
                .addParams("count", String.valueOf(count))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                if (ptr != null && commonItemIsFromRefresh) {
                    ptr.refreshComplete();
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
                        List<DynamicBean> itemList = DynamicParse.dynamicParse(response);

                        if (itemList != null) {
                            //可能之前已经有尾巴，再次进来只加载了小于6条数据就可能看到尾巴
                            if (itemList.size() <= 6 && mdDetails_listView.getFooterViewsCount() == 1) {
                                try {
                                    mdDetails_listView.removeFooterView(mFooterView);
                                    mDynamicDetailsListViewAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    //发送handler，隐藏进度条
                                    mHandler.obtainMessage(SUCCESS).sendToTarget();
                                }
                            }
                            if (itemIsNeedClearData) {
                                dynamicDetailsList.clear();
                                dynamicDetailsList.addAll(itemList);
                                ListViewUtils.isAddListViewDynamicFooterView(dynamicDetailsList, mdDetails_listView, mFooterView);
                            } else {
                                //加载更多
                                if (itemList.size() == 0) {
                                    if (mdDetails_listView.getFooterViewsCount() == 1) {
                                        try {
                                            mdDetails_listView.removeFooterView(mFooterView);
                                            mDynamicDetailsListViewAdapter.notifyDataSetChanged();
                                        } catch (Exception e) {
                                            //发送handler，隐藏进度条
                                            mHandler.obtainMessage(SUCCESS).sendToTarget();
                                        }
                                    }
                                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "没有数据了");
                                } else {
                                    dynamicDetailsList.addAll(itemList);
                                    ListViewUtils.isAddListViewDynamicFooterView(dynamicDetailsList, mdDetails_listView, mFooterView);
                                }
                            }
                        }
                        //刷新完成
                        if (ptr != null && commonItemIsFromRefresh) {
                            ptr.refreshComplete();
                        }
                        //发送handler，隐藏进度条
                        mHandler.obtainMessage(SUCCESS).sendToTarget();
                    } else if (responseContent != null) {

                        if (ptr != null && commonItemIsFromRefresh) {
                            ptr.refreshComplete();
                        }
                        if (MyConstants.FOOTVIEWNAME.equals(responseContent)) {
                            if (mdDetails_listView.getFooterViewsCount() == 1) {
                                try {
                                    mdDetails_listView.removeFooterView(mFooterView);
                                    mDynamicDetailsListViewAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    //发送handler，隐藏进度条
                                    mHandler.obtainMessage(SUCCESS).sendToTarget();
                                }
                            }
                            //给出错误提示
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "暂无评论数据");
                        } else {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                        }
                        //发送handler，隐藏进度条
                        mHandler.obtainMessage(SUCCESS).sendToTarget();
                    }
                } catch (Exception e) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    if (ptr != null && commonItemIsFromRefresh) {
                        ptr.refreshComplete();
                    }
                    if (mdDetails_listView.getFooterViewsCount() == 1) {
                        try {
                            mdDetails_listView.removeFooterView(mFooterView);
                            mDynamicDetailsListViewAdapter.notifyDataSetChanged();
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

    //Dialog中弹出输入法
    private void displayInput(final EditText et) {
        UIUtils.getMainThreadHandler().postDelayed(new Runnable() {
            public void run() {
                InputMethodManager inManager = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 200);
    }
}
