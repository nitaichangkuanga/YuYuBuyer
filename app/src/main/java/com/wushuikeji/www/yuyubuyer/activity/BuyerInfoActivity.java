package com.wushuikeji.www.yuyubuyer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.BuyerDetailsParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.GetNormalOptionsUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
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
import okhttp3.Call;

public class BuyerInfoActivity extends AppCompatActivity {

    @InjectView(R.id.rl_userInfo_back)
    RelativeLayout rl_userInfo_back;

    @InjectView(R.id.tv_userinfo_dynamic)
    TextView tv_userinfo_dynamic;

    @InjectView(R.id.tv_userInfo_report)
    TextView tv_userInfo_report;

    @InjectView(R.id.tv_userInfo_bootMattention)
    TextView tv_userInfo_bootMattention;

    @InjectView(R.id.rl_userInfo_video)
    RelativeLayout rl_userInfo_video;

    @InjectView(R.id.pb_userInfo_progressBar)
    ProgressBar pb_userInfo_progressBar;

    @InjectView(R.id.tv_userInfo_userName)
    TextView tv_userInfo_userName;

    @InjectView(R.id.iv_userInfo_shopImg)
    ImageView iv_userInfo_shopImg;

    @InjectView(R.id.iv_userInfo_grade)
    ImageView iv_userInfo_grade;

    @InjectView(R.id.iv_userInfo_sex)
    ImageView iv_userInfo_sex;

    @InjectView(R.id.tv_userInfo_age)
    TextView tv_userInfo_age;

    @InjectView(R.id.tv_userinfo_buyerId)
    TextView tv_userinfo_buyerId;

    @InjectView(R.id.tv_city)
    TextView tv_city;

    @InjectView(R.id.tv_business)
    TextView tv_business;

    @InjectView(R.id.iv_userInfo_orderImg)
    ImageView iv_userInfo_orderImg;

    @InjectView(R.id.iv_userInfo_shopping)
    ImageView iv_userInfo_shopping;

    @InjectView(R.id.tv_userInfo_moko)
    TextView tv_userInfo_moko;

    @InjectView(R.id.commonInfo_attention)
    TextView commonInfo_attention;

    @InjectView(R.id.commonInfo_fanInfo)
    TextView commonInfo_fanInfo;

    @InjectView(R.id.commonInfo_friendInfo)
    TextView commonInfo_friendInfo;

    @InjectView(R.id.commonInfo_onlineTime)
    TextView commonInfo_onlineTime;

    @InjectView(R.id.commonInfo_order)
    TextView commonInfo_order;

    @InjectView(R.id.commonInfo_xin)
    TextView commonInfo_xin;

    @InjectView(R.id.tv_userInfo_title)
    TextView tv_userInfo_title;

    @InjectView(R.id.ll_commomInfo_friendLinearLayout)
    LinearLayout ll_commomInfo_friendLinearLayout;

    @InjectView(R.id.ll_commomInfo_fansLinearLayout)
    LinearLayout ll_commomInfo_fansLinearLayout;

    @InjectView(R.id.ll_commomInfo_attentionLinearLayout)
    LinearLayout ll_commomInfo_attentionLinearLayout;

    private boolean mIsLoginBoolean;
    private String mUserId;
    private String mBuyerId;
    //buyerInfo的地址
    private String buyerInfoUrl = Constants.commontUrl + "buyer/details";
    //请求关注的url
    private String attentionUrl = Constants.commontUrl + "buyer/relation";
    //用来记录用户是否点了关注
    private boolean isAttention;
    private boolean mIsCacheAttention;
    private String buyerHuanXinName = new String();
    private String mOneselfHeadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_info);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initData();

        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);
        //查看用户是否登陆过
        mIsLoginBoolean = SpUtils.getCacheBoolean(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.ISLOGINSTATUS, false);
        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");
        //得到buyerId
        mBuyerId = getIntent().getStringExtra("BuyerId");
        mIsCacheAttention = SpUtils.getCacheBoolean(MyConstants.BUYERDETAILSATTENTIONSPNAME, UIUtils.getContext(), MyConstants.ISATTENTION, false);
    }

    private void initData() {
        //得到自己的头像
        mOneselfHeadUrl = SpUtils.getSpString(UIUtils.getContext(), MyConstants.HEADIMGURL, "");

        //显示用户是否关注过
        if(mIsCacheAttention) {//说明关注过
            tv_userInfo_bootMattention.setText("取消关注");
        }else {
            tv_userInfo_bootMattention.setText("关注");
        }

        if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else if (mIsLoginBoolean) {//登录过
            loadNetworkData(true);
        } else {//没登录
            loadNetworkData(false);
        }
    }

    private void initEvent() {
        //返回
        BackListenerUtils.backFinish(rl_userInfo_back, this);
        //点击ta动态(和我的动态共用一个Activity)
        tv_userinfo_dynamic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToNextActivityUtils.toNextActivityNotFinishAndTwoParameters(BuyerInfoActivity.this, MyDynamicActivity.class,"fromWhere","other","buyerId",mBuyerId);
            }
        });
        //点击举报
        tv_userInfo_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsLoginBoolean) {
                    //登录过
                    ToNextActivityUtils.toNextActivityNotFinishAndParameters(BuyerInfoActivity.this, ReportInputTextActivity.class, "BuyerId", mBuyerId);
                } else {
                    //提示
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), MyConstants.LOGIN);
                    ToNextActivityUtils.toNextAndNoFinishActivity(BuyerInfoActivity.this, LoginActivity.class);
                }
            }
        });
        //点击关注
        tv_userInfo_bootMattention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsLoginBoolean) {
                    //登录过
                    //判断用户是否关注过
                    if(!mIsCacheAttention) {
                        //需要去关注
                        //发送网络请求关注
                        sendAttentionNetwork("2");
                    }else {
                        //取消关注
                        //发送网络请求关注
                        sendAttentionNetwork("3");
                    }
                } else {
                    //提示
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), MyConstants.LOGIN);
                    ToNextActivityUtils.toNextAndNoFinishActivity(BuyerInfoActivity.this, LoginActivity.class);
                }
            }
        });
        //点击视频指导
        rl_userInfo_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsLoginBoolean) {
                    //登录过鱼鱼app
                    //还需要进行判断是否登录上环信，才能(进行视频通话)
                    if (EMClient.getInstance().isLoggedInBefore()) {
                        //登录了环信
                        if (!EMClient.getInstance().isConnected()) {
                            Toast.makeText(BuyerInfoActivity.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (TextUtils.isEmpty(buyerHuanXinName)){
                                Toast.makeText(BuyerInfoActivity.this, "没找到需要连接的用户", Toast.LENGTH_SHORT).show();
                                return ;
                            }
                            Intent intent = new Intent(BuyerInfoActivity.this, MyVideoCallActivity.class);
                            intent.putExtra("username",buyerHuanXinName);
                            intent.putExtra("isComingCall", false);
                            //传自己的头像url进去，好实现聊天的时候展示
                            intent.putExtra("oneselfHeadUrl", mOneselfHeadUrl);
                            startActivity(intent);
                        }
                    }else {
                        //没登录环信
                        loginHuanxinAndVideo();
                    }
                }else {
                    //提示
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), MyConstants.LOGIN);
                    ToNextActivityUtils.toNextAndNoFinishActivity(BuyerInfoActivity.this, LoginActivity.class);
                }
            }
        });

        //点击列表中的关注
        ll_commomInfo_attentionLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToNextActivityUtils.toNextActivityNotFinishAndTwoParameters(BuyerInfoActivity.this,CommonAttentionFansFriendActivity.class,"buyerId",mBuyerId,"index","2");
            }
        });

        //点击粉丝
        ll_commomInfo_fansLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToNextActivityUtils.toNextActivityNotFinishAndTwoParameters(BuyerInfoActivity.this,CommonAttentionFansFriendActivity.class,"buyerId",mBuyerId,"index","3");
            }
        });

        //点击好友
        ll_commomInfo_friendLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToNextActivityUtils.toNextActivityNotFinishAndTwoParameters(BuyerInfoActivity.this,CommonAttentionFansFriendActivity.class,"buyerId",mBuyerId,"index","1");
            }
        });
    }


    /**
     * 登录环信并且请求视频
     */
    private void loginHuanxinAndVideo() {
        String imUserName = SpUtils.getSpString(UIUtils.getContext(), MyConstants.IMUSERPHONE, "");
        String imUserPassword = SpUtils.getSpString(UIUtils.getContext(), MyConstants.IMUSERPASSWORD, "");
        EMClient.getInstance().login(imUserName, imUserPassword, new EMCallBack() {
            /**
             * 登陆成功的回调
             */
            @Override
            public void onSuccess() {
                //子线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //本项目成功了不做什么处理
                        // 加载所有会话到内存
                        EMClient.getInstance().chatManager().loadAllConversations();
                        // 加载所有群组到内存，如果使用了群组的话
                        // EMClient.getInstance().groupManager().loadAllGroups();
                        //登录了环信
                        if (!EMClient.getInstance().isConnected()) {
                            Toast.makeText(BuyerInfoActivity.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
                        }else {
                            if (TextUtils.isEmpty(buyerHuanXinName)){
                                Toast.makeText(BuyerInfoActivity.this, "没找到需要连接的用户", Toast.LENGTH_SHORT).show();
                                return ;
                            }
                            Intent intent = new Intent(BuyerInfoActivity.this, MyVideoCallActivity.class);
                            intent.putExtra("username",buyerHuanXinName);
                            intent.putExtra("isComingCall", false);
                            //传自己的头像url进去，好实现聊天的时候展示
                            intent.putExtra("oneselfHeadUrl", mOneselfHeadUrl);
                            startActivity(intent);
                        }
                    }
                });
            }
            /**
             * 登陆错误的回调
             */
            @Override
            public void onError(final int i, final String s) {
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }

    /**
     * 发送关注的请求
     */
    private void sendAttentionNetwork(String relationValue) {

        final String tempRelationValue = relationValue;

        //进度条展示
        pb_userInfo_progressBar.setVisibility(View.VISIBLE);

        OkHttpUtils.post().url(attentionUrl).addParams("buyer_id", mBuyerId)
                .addParams("user_id", mUserId)
                .addParams("relation", relationValue)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pb_userInfo_progressBar.setVisibility(View.GONE);
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    //先解析reponse_code,给客户提醒
                    Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                    String response_code = map.get("response_code");
                    if ("0".equals(response_code)) {
                        //成功
                        if("2".equals(tempRelationValue)) {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "关注成功");
                            //字体样式改变
                            tv_userInfo_bootMattention.setText("取消关注");
                            mIsCacheAttention = true;
                            //保存是否关注(不清除)
                            SpUtils.putCacheBoolean(MyConstants.BUYERDETAILSATTENTIONSPNAME,UIUtils.getContext(),MyConstants.ISATTENTION,true);
                        }else if("3".equals(tempRelationValue)) {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "取消关注成功");
                            //字体样式改变
                            tv_userInfo_bootMattention.setText("关注");
                            mIsCacheAttention = false;
                            SpUtils.putCacheBoolean(MyConstants.BUYERDETAILSATTENTIONSPNAME,UIUtils.getContext(),MyConstants.ISATTENTION,false);
                        }
                    }else {
                        if("2".equals(tempRelationValue)) {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "关注失败");
                            tv_userInfo_bootMattention.setText("关注");
                            mIsCacheAttention = false;
                        }else if("3".equals(tempRelationValue)) {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "取消关注失败");
                            tv_userInfo_bootMattention.setText("取消关注");
                            mIsCacheAttention = true;
                        }
                    }
                    pb_userInfo_progressBar.setVisibility(View.GONE);
                } catch (Exception e) {
                    pb_userInfo_progressBar.setVisibility(View.GONE);
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                }
            }
        });
    }

    /**
     * 加载网络的数据
     */
    private void loadNetworkData(boolean isLogin) {

        //进度条展示
        pb_userInfo_progressBar.setVisibility(View.VISIBLE);

        if (isLogin) {//登录过
            OkHttpUtils.post().url(buyerInfoUrl).addParams("buyer_id", mBuyerId)
                    .addParams("user_id", mUserId)
                    .build().execute(new UserinfoCommonRequestCall());
        } else {//no
            OkHttpUtils.post().url(buyerInfoUrl).addParams("buyer_id", mBuyerId)
                    .build().execute(new UserinfoCommonRequestCall());
        }
    }

    /**
     * 处理网络返回的结果
     */
    private void operationResult(String response) {
        try {
            //先解析reponse_code,给客户提醒
            Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
            String response_code = map.get("response_code");
            //需要判断各种响应码,获取具体错误信息
            String responseContent = RequestResponseUtils.getResponseContent(response_code);
            if ("0".equals(response_code)) {
                //可以下一步解析buyerInfo
                Map<String, String> userInfoParseMap = BuyerDetailsParse.buyerDetailsUserInfoParse(response);

                Map<String, String> statusInfoParseMap = BuyerDetailsParse.buyerDetailsStatusInfoParse(response);

                Map<String, String> gradeInfoParseMap = BuyerDetailsParse.buyerDetailsGradeInfoParse(response);

                Map<String, String> areaInfoParseMap = BuyerDetailsParse.buyerDetailsAreaInfoParse(response);
                if (userInfoParseMap != null) {
                    buyerHuanXinName = userInfoParseMap.get("imName");
                    setUserInfoParseData(userInfoParseMap);
                }
                if (statusInfoParseMap != null) {
                    setStatusInfoParseData(statusInfoParseMap);
                }
                if (gradeInfoParseMap != null) {
                    setGradeInfoParseData(gradeInfoParseMap);
                }
                if (areaInfoParseMap != null) {
                    setAreaInfoParseData(areaInfoParseMap);
                }
                pb_userInfo_progressBar.setVisibility(View.GONE);
            } else if (responseContent != null) {
                //给出错误提示
                ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                pb_userInfo_progressBar.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
            pb_userInfo_progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * 设置解析得到buyer信息的数据
     */
    private void setUserInfoParseData(Map<String, String> buyerInfoMap) {
        //设置数据(username sex age head_img status description followInfo fanInfo friendInfo orderInfo)
        //buyer的名字
        tv_userInfo_userName.setText(buyerInfoMap.get("username"));
        //标题
        tv_userInfo_title.setText(buyerInfoMap.get("username"));
        //buyer的性别
        String sex = buyerInfoMap.get("sex");
        if (!TextUtils.isEmpty(sex)) {
            choiceBuyerSex(Integer.parseInt(sex));
        }
        //age
        tv_userInfo_age.setText(buyerInfoMap.get("age"));

        //buyer的头像
        ImageLoader.getInstance().displayImage(buyerInfoMap.get("head_img"), iv_userInfo_shopImg, GetNormalOptionsUtils.getNormalOptionsUtils());

        //个性签名
        String description = buyerInfoMap.get("description");
        if (!TextUtils.isEmpty(description)) {
            tv_userInfo_moko.setText(description);
        }
        //关注followInfo
        commonInfo_attention.setText(buyerInfoMap.get("followInfo"));
        //粉丝fanInfo
        commonInfo_fanInfo.setText(buyerInfoMap.get("fanInfo"));
        //好友friendInfo
        commonInfo_friendInfo.setText(buyerInfoMap.get("friendInfo"));
        //订单量orderInfo
        commonInfo_order.setText(buyerInfoMap.get("orderInfo"));
        //mBuyerId
        tv_userinfo_buyerId.setText(mBuyerId);
    }

    /**
     * 设置接单服务数据
     */
    private void setStatusInfoParseData(Map<String, String> buyerInfoMap) {
        //is_shopping is_video
        //判断视频指导和线下指导是否点亮
        String is_video = buyerInfoMap.get("is_video");
        String is_shopping = buyerInfoMap.get("is_shopping");
        if (!TextUtils.isEmpty(is_video) && !TextUtils.isEmpty(is_shopping)) {
            choiceWhichBuyerServer(Integer.parseInt(is_video), Integer.parseInt(is_shopping));
        }
    }

    /**
     * 设置等级信息
     */
    private void setGradeInfoParseData(Map<String, String> buyerInfoMap) {
        //buyer的等级
        String grades = buyerInfoMap.get("grades");
        if (!TextUtils.isEmpty(grades)) {
            choiceBuyerGrade(Integer.parseInt(grades));
        }
        //在线时长
        String time = buyerInfoMap.get("online_time");
        if (!TextUtils.isEmpty(time)) {
            DecimalFormat df = null;
            int online_time = Integer.parseInt(time);
            if (online_time >= 3600) {//小时
                df = new DecimalFormat("#.00");
                String format = df.format(online_time / 3600);
                commonInfo_onlineTime.setText(format + "小时");
            } else if (online_time >= 60) {//分
                int finalTime = (int) (online_time / 60 + 0.5f);
                commonInfo_onlineTime.setText(finalTime + "分钟");
            } else {
                commonInfo_onlineTime.setText(online_time + "秒");
            }
        }

        //信用度
        commonInfo_xin.setText(buyerInfoMap.get("credits"));
    }

    /**
     * 设置buyer所在的城市和商圈
     */
    private void setAreaInfoParseData(Map<String, String> buyerInfoMap) {
        //tv_city
        tv_city.setText(buyerInfoMap.get("city_name"));
        //tv_business
        tv_business.setText(buyerInfoMap.get("area_name"));
    }


    /**
     * 是否点亮图片
     */
    private void choiceWhichBuyerServer(int videoIndex, int shoppingIndex) {
        if (videoIndex == 1) {
            //点亮视频
            iv_userInfo_orderImg.setBackgroundResource(R.mipmap.camera);
        } else if (videoIndex == 2) {
            iv_userInfo_orderImg.setBackgroundResource(R.mipmap.camera_default);
        }

        if (shoppingIndex == 1) {
            //点亮线下（红色图片暂时没有）
            iv_userInfo_shopping.setBackgroundResource(R.mipmap.red_floor);
        } else if (shoppingIndex == 2) {
            iv_userInfo_shopping.setBackgroundResource(R.mipmap.shopping);
        }

    }

    /**
     * 选择性别 1:男 2:女
     */
    private void choiceBuyerSex(int index) {
        switch (index) {
            case 1:
                iv_userInfo_sex.setBackgroundResource(R.mipmap.boy);
                break;
            case 2:
                iv_userInfo_sex.setBackgroundResource(R.mipmap.girl);
                break;
            default:
                break;
        }
    }

    /**
     * 判断buyer的等级和显示图片
     */
    private void choiceBuyerGrade(int grades) {
        switch (grades) {
            case 1:
                iv_userInfo_grade.setBackgroundResource(R.mipmap.buyerdetails_v1);
                break;
            case 2:
                iv_userInfo_grade.setBackgroundResource(R.mipmap.buyerdetails_v2);
                break;
            case 3:
                iv_userInfo_grade.setBackgroundResource(R.mipmap.buyerdetails_v3);
                break;
            case 4:
                iv_userInfo_grade.setBackgroundResource(R.mipmap.buyerdetails_v4);
                break;
            case 5:
                iv_userInfo_grade.setBackgroundResource(R.mipmap.buyerdetails_v5);
                break;
            default:
                break;
        }
    }

    class UserinfoCommonRequestCall extends StringCallback {

        @Override
        public void onError(Call call, Exception e, int id) {
            try {
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                pb_userInfo_progressBar.setVisibility(View.GONE);
            } catch (Exception exception) {
            }
        }

        @Override
        public void onResponse(String response, int id) {
            operationResult(response);
        }
    }
}
