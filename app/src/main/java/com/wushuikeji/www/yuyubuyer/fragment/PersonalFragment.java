package com.wushuikeji.www.yuyubuyer.fragment;


import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.activity.AccountSafetyActivity;
import com.wushuikeji.www.yuyubuyer.activity.BindBankCardActivity;
import com.wushuikeji.www.yuyubuyer.activity.CommonAttentionFansFriendActivity;
import com.wushuikeji.www.yuyubuyer.activity.EditPersonActivity;
import com.wushuikeji.www.yuyubuyer.activity.LoginActivity;
import com.wushuikeji.www.yuyubuyer.activity.MoneyRecordActivity;
import com.wushuikeji.www.yuyubuyer.activity.MyAccountActivity;
import com.wushuikeji.www.yuyubuyer.activity.MyCollectActivity;
import com.wushuikeji.www.yuyubuyer.activity.MyDynamicActivity;
import com.wushuikeji.www.yuyubuyer.activity.MyEvaluateActivity;
import com.wushuikeji.www.yuyubuyer.activity.PersonOrderActivity;
import com.wushuikeji.www.yuyubuyer.activity.RealEditActivity;
import com.wushuikeji.www.yuyubuyer.activity.ReportActivity;
import com.wushuikeji.www.yuyubuyer.activity.ReturnApplyActivity;
import com.wushuikeji.www.yuyubuyer.activity.SettingActivity;
import com.wushuikeji.www.yuyubuyer.activity.SubmitRecordActivity;
import com.wushuikeji.www.yuyubuyer.base.BaseApplication;
import com.wushuikeji.www.yuyubuyer.base.BaseFragment;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.FragmentUserInfoParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.utils.GetNormalOptionsUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.PtrFrameRefreshUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToNextActivityUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.DecimalFormat;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import okhttp3.Call;

public class PersonalFragment extends BaseFragment implements View.OnClickListener {

    @InjectView(R.id.tv_personal_login)
    TextView tv_personal_login;

    @InjectView(R.id.tv_personal_id)
    TextView tv_personal_id;

    //刷新
    @InjectView(R.id.in_mainpersonal_pf)
    PtrClassicFrameLayout mPersonalPtrFrame;

    //ScrollView
    @InjectView(R.id.personal_scrollView)
    ScrollView personal_scrollView;

    //编辑
    @InjectView(R.id.iv_mainPersonal_edit)
    ImageView iv_mainPersonal_edit;

    //个人接单
    @InjectView(R.id.rl_mainPersonal_personalOrder)
    RelativeLayout rl_mainPersonal_personalOrder;
    //我的动态
    @InjectView(R.id.rl_mainPersonal_my)
    RelativeLayout rl_mainPersonal_my;

    @InjectView(R.id.rl_personal_bom)
    RelativeLayout rl_personal_bom;

    @InjectView(R.id.rl_personal_a)
    RelativeLayout rl_personal_a;
    //我的账户
    @InjectView(R.id.rl_personal_rl)
    RelativeLayout rl_personal_rl;

    @InjectView(R.id.rl_personal_b)
    RelativeLayout rl_personal_b;

    @InjectView(R.id.rl_personal_rltwo)
    RelativeLayout rl_personal_rltwo;

    @InjectView(R.id.rl_personal_c)
    RelativeLayout rl_personal_c;

    @InjectView(R.id.rl_personal_rlthree)
    RelativeLayout rl_personal_rlthree;

    @InjectView(R.id.rl_personal_d)
    RelativeLayout rl_personal_d;

    @InjectView(R.id.rl_personal_jubao)
    RelativeLayout rl_personal_jubao;

    @InjectView(R.id.rl_personal_setting)
    RelativeLayout rl_personal_setting;

    //点击最上面的图片
    @InjectView(R.id.iv_main_personal_bg)
    ImageView iv_main_personal_bg;

    @InjectView(R.id.tv_personalCommon_attention)
    TextView tv_personalCommon_attention;

    @InjectView(R.id.tv_personalCommon_fans)
    TextView tv_personalCommon_fans;

    @InjectView(R.id.tv_personalCommon_friends)
    TextView tv_personalCommon_friends;

    @InjectView(R.id.tv_personalCommon_integral)
    TextView tv_personalCommon_integral;

    @InjectView(R.id.tv_personalCommon_credits)
    TextView tv_personalCommon_credits;

    @InjectView(R.id.tv_personalCommon_onlineTime)
    TextView tv_personalCommon_onlineTime;

    @InjectView(R.id.iv_personal_one)
    ImageView iv_personal_one;

    @InjectView(R.id.iv_personal_imageview)
    ImageView iv_personal_imageview;

    @InjectView(R.id.iv_personal_ivtwo)
    ImageView iv_personal_ivtwo;

    @InjectView(R.id.iv_personal_ivthree)
    ImageView iv_personal_ivthree;

    @InjectView(R.id.iv_personal_ivfour)
    ImageView iv_personal_ivfour;

    @InjectView(R.id.iv_personal_two)
    ImageView iv_personal_two;

    @InjectView(R.id.iv_personal_three)
    ImageView iv_personal_three;

    @InjectView(R.id.iv_personal_four)
    ImageView iv_personal_four;

    @InjectView(R.id.iv_personal_five)
    ImageView iv_personal_five;

    @InjectView(R.id.iv_main_personal_img)
    ImageView iv_main_personal_img;

    @InjectView(R.id.ll_common_attentionLinearLayout)
    LinearLayout ll_common_attentionLinearLayout;

    @InjectView(R.id.ll_common_fansLinearLayoutLinearLayout)
    LinearLayout ll_common_fansLinearLayoutLinearLayout;

    @InjectView(R.id.ll_common_friendLinearLayoutLinearLayout)
    LinearLayout ll_common_friendLinearLayoutLinearLayout;



    //请求用户信息的网址
    private String userAllUrl = Constants.commontUrl + "personal/info";

    private View mPersonalFragmentView;
    private static final int SUCCESS = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    //对数据的刷新,设置数据
                    mPersonalPtrFrame.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };
    private boolean mIsLoginBoolean;
    private String mUserId;

    @Override
    public View initView() {
        //需要展示的PersonalView
        mPersonalFragmentView = View.inflate(UIUtils.getContext(), R.layout.main_personal, null);

        //注解
        ButterKnife.inject(this, mPersonalFragmentView);

        //刷新的操作
        mPersonalPtrFrame.setLastUpdateTimeRelateObject(this);
        PtrFrameRefreshUtils.setRefreshParams(mPersonalPtrFrame);
        personal_scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        //查看用户是否登陆过
        mIsLoginBoolean = SpUtils.getCacheBoolean(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.ISLOGINSTATUS, false);
        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");

        return mPersonalFragmentView;
    }

    @Override
    public void initData() {
        if (!mIsLoginBoolean) {
            return;
        } else {
            //先判断缓存中是否有数据
            String jsonString = SpUtils.getSpString(UIUtils.getContext(), MyConstants.PERSONALINFO, "");
            if (TextUtils.isEmpty(jsonString)) {
                if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    return;
                } else {
                    requestLoadNetworkData(false);
                }
            } else {//有缓存
                //可以下一步解析buyerInfo
                Map<String, String> userGradeCacheMap = FragmentUserInfoParse.UserGradeInfoParse(jsonString);
                Map<String, String> attentionCacheMap = FragmentUserInfoParse.userAttentionFriendAndFanParse(jsonString);
                //得到用户的头像
//                Map<String, String> UserHeadImgMap = FragmentUserInfoParse.userHeadImgParse(jsonString);

                if (userGradeCacheMap != null) {
                    setUserGradeParseData(userGradeCacheMap);
                }
                if (attentionCacheMap != null) {
                    setFriendInfoParseData(attentionCacheMap);
                }
//                if (UserHeadImgMap != null) {
//                    setUserHeadImgParseData(UserHeadImgMap);
//                }
                //从保留中获取头像
                String headImgUrl = SpUtils.getSpString(UIUtils.getContext(), MyConstants.HEADIMGURL, "");
                ImageLoader.getInstance().displayImage(headImgUrl, iv_main_personal_img, GetNormalOptionsUtils.getNormalOptionsUtils());
            }
        }
    }

    @Override
    public void initEvent() {
        super.initEvent();
        //点击登录
        tv_personal_login.setOnClickListener(this);
        //编辑
        iv_mainPersonal_edit.setOnClickListener(this);
        //点击最上面的图片
        iv_main_personal_bg.setOnClickListener(this);
        //个人接单
        rl_mainPersonal_personalOrder.setOnClickListener(this);
        rl_mainPersonal_my.setOnClickListener(this);
        rl_personal_bom.setOnClickListener(this);
        rl_personal_a.setOnClickListener(this);
        rl_personal_rl.setOnClickListener(this);
        rl_personal_b.setOnClickListener(this);
        rl_personal_rltwo.setOnClickListener(this);
        rl_personal_c.setOnClickListener(this);
        rl_personal_rlthree.setOnClickListener(this);
        rl_personal_d.setOnClickListener(this);
        rl_personal_jubao.setOnClickListener(this);
        rl_personal_setting.setOnClickListener(this);

        //点击关注
        ll_common_attentionLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsLoginBoolean) {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), LoginActivity.class);
                } else {
                    ToNextActivityUtils.toNextActivityNotFinishAndTwoParameters(getActivity(),CommonAttentionFansFriendActivity.class,"buyerId",mUserId,"index","2");
                }
            }
        });

        //点击粉丝
        ll_common_fansLinearLayoutLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsLoginBoolean) {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), LoginActivity.class);
                } else {
                    ToNextActivityUtils.toNextActivityNotFinishAndTwoParameters(getActivity(),CommonAttentionFansFriendActivity.class,"buyerId",mUserId,"index","3");
                }
            }
        });

        //点击好友
        ll_common_friendLinearLayoutLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsLoginBoolean) {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), LoginActivity.class);
                } else {
                    ToNextActivityUtils.toNextActivityNotFinishAndTwoParameters(getActivity(),CommonAttentionFansFriendActivity.class,"buyerId",mUserId,"index","1");
                }
            }
        });



        //实现下拉刷新
        mPersonalPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                // here check $mListView instead of $content
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, personal_scrollView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (!mIsLoginBoolean) {
                    mPersonalPtrFrame.refreshComplete();
                    return;
                } else {
                    if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                        mPersonalPtrFrame.refreshComplete();//目的让刷新隐藏掉
                        return;
                    } else {
                        requestLoadNetworkData(true);
                    }
                }
            }
        });
        //一进来从缓存中读取用户的用户名,必须放在设置监听事件之后才起作用
        String userName = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.USERNAME, null);
        String buyerId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");
        if (userName != null) {
            //登录了，显示用户名  可能没有昵称
            if ("".equals(userName)) {
                tv_personal_login.setText(userName);
                tv_personal_login.setClickable(false);
            } else {
                tv_personal_login.setText(userName);
                tv_personal_login.setClickable(false);
            }
        } else {
            tv_personal_login.setText("点击登录");
            tv_personal_login.setClickable(true);
        }
        //buyerId
        if (!TextUtils.isEmpty(buyerId)) {
            //登录了，显示id
            tv_personal_id.setText(buyerId);
        } else {
            tv_personal_id.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_personal_login://登录
                ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), LoginActivity.class);
                break;
            case R.id.iv_mainPersonal_edit://编辑
                if (!mIsLoginBoolean) {
                    //进入登录界面
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), LoginActivity.class);
                } else {
                    //进入到真正确定的界面
                    ToNextActivityUtils.toNextActivityNotFinishAndParameters(getActivity(), RealEditActivity.class, "fromIndex", "0");
                }
                break;
            case R.id.iv_main_personal_bg://图片
                //需要先判断是否登录过,登录了就跳转用户编辑信息界面,否则就进入登录界面
                if (!mIsLoginBoolean) {
                    //进入登录界面
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), LoginActivity.class);
                } else {
                    //进入到预编辑界面
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), EditPersonActivity.class);
                }
                break;
            case R.id.rl_mainPersonal_personalOrder://个人接单
                if (mIsLoginBoolean) {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), PersonOrderActivity.class);
                } else {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), LoginActivity.class);
                }
                break;
            case R.id.rl_mainPersonal_my://我的动态
                if (mIsLoginBoolean) {
                    ToNextActivityUtils.toNextActivityNotFinishAndParameters(getActivity(), MyDynamicActivity.class,"fromWhere","me");
                } else {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), LoginActivity.class);
                }
                break;
            case R.id.rl_personal_bom://我的评价
                if (mIsLoginBoolean) {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), MyEvaluateActivity.class);
                } else {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), LoginActivity.class);
                }
                break;
            case R.id.rl_personal_a://我的收藏
                if (mIsLoginBoolean) {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), MyCollectActivity.class);
                } else {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), LoginActivity.class);
                }
                break;
            case R.id.rl_personal_rl://我的账户
                if (mIsLoginBoolean) {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), MyAccountActivity.class);
                } else {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), LoginActivity.class);
                }
                break;
            case R.id.rl_personal_b://账户安全
                if (mIsLoginBoolean) {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), AccountSafetyActivity.class);
                } else {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), LoginActivity.class);
                }
                break;
            case R.id.rl_personal_rltwo://银行卡绑定
                if (mIsLoginBoolean) {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), BindBankCardActivity.class);
                } else {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), LoginActivity.class);
                }
                break;
            case R.id.rl_personal_c://退款申请
                ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), ReturnApplyActivity.class);
                break;
            case R.id.rl_personal_rlthree://充值记录
                if (mIsLoginBoolean) {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), MoneyRecordActivity.class);
                }else {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), LoginActivity.class);
                }
                break;
            case R.id.rl_personal_d://提现记录
                if (mIsLoginBoolean) {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), SubmitRecordActivity.class);
                }else {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), LoginActivity.class);
                }
                break;
            case R.id.rl_personal_jubao://举报
                if (mIsLoginBoolean) {
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), ReportActivity.class);
                } else {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), MyConstants.LOGIN);
                    ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), LoginActivity.class);
                }
                break;
            case R.id.rl_personal_setting://设置
                //点了设置就添加统一管理
                BaseApplication.getInstance().addActivity(getActivity());
                ToNextActivityUtils.toNextAndNoFinishActivity(getActivity(), SettingActivity.class);
                break;
            default:
                break;
        }
    }

    /**
     * 加载网络得到关注  粉丝   好友的个数
     */
    private void requestLoadNetworkData(boolean isFromRefresh) {

        final boolean itemIsFromRefresh = isFromRefresh;

        OkHttpUtils.get().url(userAllUrl).addParams("user_id", mUserId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                if (mPersonalPtrFrame != null && itemIsFromRefresh) {
                    mPersonalPtrFrame.refreshComplete();
                }
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    //先解析reponse_code
                    Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                    String response_code = map.get("response_code");
                    if ("0".equals(response_code)) {
                        //可以下一步解析buyerInfo
                        Map<String, String> userGradeInfoParseMap = FragmentUserInfoParse.UserGradeInfoParse(response);
                        Map<String, String> attentionFriendAndFanMap = FragmentUserInfoParse.userAttentionFriendAndFanParse(response);
                        //得到用户的头像
                        Map<String, String> UserHeadImgMap = FragmentUserInfoParse.userHeadImgParse(response);

                        //保存用户等级信息和好友粉丝关注jsonString
                        SpUtils.putSpString(UIUtils.getContext(), MyConstants.PERSONALINFO, response);

                        if (userGradeInfoParseMap != null) {
                            setUserGradeParseData(userGradeInfoParseMap);
                        }
                        if (attentionFriendAndFanMap != null) {
                            setFriendInfoParseData(attentionFriendAndFanMap);
                        }

                        if (UserHeadImgMap != null) {
                            setUserHeadImgParseData(UserHeadImgMap);
                        }
                    }
                    if (mPersonalPtrFrame != null && itemIsFromRefresh) {
                        mPersonalPtrFrame.refreshComplete();
                    }
                } catch (Exception e) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                    if (mPersonalPtrFrame != null && itemIsFromRefresh) {
                        mPersonalPtrFrame.refreshComplete();
                    }
                }
            }
        });
    }

    /**
     * 设置解析得到用户等级等信息的数据
     */
    private void setUserGradeParseData(Map<String, String> buyerInfoMap) {
        //buyer的等级
        String grades = buyerInfoMap.get("grades");
        if (!TextUtils.isEmpty(grades)) {
            choiceBuyerGrade(Integer.parseInt(grades));
        }
        //积分
        String integral = buyerInfoMap.get("integral");
        if ("".equals(integral)) {
            tv_personalCommon_integral.setText("0");
        } else {
            tv_personalCommon_integral.setText(integral);
        }

        //信用度
        String credits = buyerInfoMap.get("credits");
        if ("".equals(credits)) {
            tv_personalCommon_credits.setText("0");
        } else {
            tv_personalCommon_credits.setText(credits);
        }
        //在线时长
        String time = buyerInfoMap.get("online_time");
        if (!TextUtils.isEmpty(time)) {
            DecimalFormat df = null;
            int online_time = Integer.parseInt(time);
            if (online_time >= 3600) {//小时
                df = new DecimalFormat("#.00");
                String format = df.format(online_time / 3600);
                tv_personalCommon_onlineTime.setText(format + "小时");
            } else if (online_time >= 60) {//分
                int finalTime = (int) (online_time / 60 + 0.5f);
                tv_personalCommon_onlineTime.setText(finalTime + "分钟");
            } else {
                tv_personalCommon_onlineTime.setText(online_time + "秒");
            }
        }
    }

    /**
     * 设置解析得到用户朋友等信息的数据
     */
    private void setFriendInfoParseData(Map<String, String> buyerInfoMap) {

        //关注followInfo
        String followInfo = buyerInfoMap.get("followInfo");
        if ("".equals(followInfo)) {
            tv_personalCommon_attention.setText("0");
        } else {
            tv_personalCommon_attention.setText(followInfo);
        }

        //粉丝fanInfo
        String fanInfo = buyerInfoMap.get("fanInfo");
        if ("".equals(fanInfo)) {
            tv_personalCommon_fans.setText("0");
        } else {
            tv_personalCommon_fans.setText(fanInfo);
        }

        //好友friendInfo
        String friendInfo = buyerInfoMap.get("friendInfo");
        if ("".equals(friendInfo)) {
            tv_personalCommon_friends.setText("0");
        } else {
            tv_personalCommon_friends.setText(friendInfo);
        }
    }

    /**
     * 设置用户的头像
     */

    private void setUserHeadImgParseData(Map<String, String> headImgInfoMap) {
        String headImgString = headImgInfoMap.get("head_img");
        if ("".equals(headImgString)) {
            iv_main_personal_img.setImageResource(R.mipmap.login_avatar);
        } else {
            //设置图片
            ImageLoader.getInstance().displayImage(headImgString, iv_main_personal_img, GetNormalOptionsUtils.getNormalOptionsUtils());
        }
    }

    /**
     * 判断buyer的等级和显示图片
     */
    private void choiceBuyerGrade(int grades) {
        switch (grades) {
            case 1:
                iv_personal_imageview.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.personal_line_preshap));
                iv_personal_ivtwo.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.personal_line_shap));
                iv_personal_ivthree.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.personal_line_shap));
                iv_personal_ivfour.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.personal_line_shap));
                iv_personal_one.setBackgroundResource(R.mipmap.buyerdetails_v1);
                iv_personal_two.setBackgroundResource(R.mipmap.grade2);
                iv_personal_three.setBackgroundResource(R.mipmap.grade3);
                iv_personal_four.setBackgroundResource(R.mipmap.grade4);
                iv_personal_five.setBackgroundResource(R.mipmap.grade5);
                break;
            case 2:
                iv_personal_imageview.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.personal_line_shap));
                iv_personal_ivtwo.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.personal_line_preshap));
                iv_personal_ivthree.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.personal_line_shap));
                iv_personal_ivfour.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.personal_line_shap));
                iv_personal_one.setBackgroundResource(R.mipmap.grade1);
                iv_personal_two.setBackgroundResource(R.mipmap.buyerdetails_v2);
                iv_personal_three.setBackgroundResource(R.mipmap.grade3);
                iv_personal_four.setBackgroundResource(R.mipmap.grade4);
                iv_personal_five.setBackgroundResource(R.mipmap.grade5);
                break;
            case 3:
                iv_personal_imageview.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.personal_line_shap));
                iv_personal_ivtwo.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.personal_line_shap));
                iv_personal_ivthree.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.personal_line_preshap));
                iv_personal_ivfour.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.personal_line_shap));
                iv_personal_one.setBackgroundResource(R.mipmap.grade1);
                iv_personal_two.setBackgroundResource(R.mipmap.grade2);
                iv_personal_three.setBackgroundResource(R.mipmap.buyerdetails_v3);
                iv_personal_four.setBackgroundResource(R.mipmap.grade4);
                iv_personal_five.setBackgroundResource(R.mipmap.grade5);
                break;
            case 4:
                iv_personal_imageview.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.personal_line_shap));
                iv_personal_ivtwo.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.personal_line_shap));
                iv_personal_ivthree.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.personal_line_shap));
                iv_personal_ivfour.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.personal_line_preshap));
                iv_personal_one.setBackgroundResource(R.mipmap.grade1);
                iv_personal_two.setBackgroundResource(R.mipmap.grade2);
                iv_personal_three.setBackgroundResource(R.mipmap.grade3);
                iv_personal_four.setBackgroundResource(R.mipmap.buyerdetails_v4);
                iv_personal_five.setBackgroundResource(R.mipmap.grade5);
                break;
            case 5:
                iv_personal_imageview.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.personal_line_shap));
                iv_personal_ivtwo.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.personal_line_shap));
                iv_personal_ivthree.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.personal_line_shap));
                iv_personal_ivfour.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.personal_line_preshap));
                iv_personal_one.setBackgroundResource(R.mipmap.grade1);
                iv_personal_two.setBackgroundResource(R.mipmap.grade2);
                iv_personal_three.setBackgroundResource(R.mipmap.grade3);
                iv_personal_four.setBackgroundResource(R.mipmap.grade4);
                iv_personal_five.setBackgroundResource(R.mipmap.buyerdetails_v5);
                break;
            default:
                break;
        }
    }
}
