package com.wushuikeji.www.yuyubuyer.activity;

import android.content.Context;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMCallManager;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.media.EMLocalSurfaceView;
import com.hyphenate.media.EMOppositeSurfaceView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.adapter.ChatListViewAdapter;
import com.wushuikeji.www.yuyubuyer.bean.ChatBean;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.jsonparse.BusinessJsonParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.VideoJsonParse;
import com.wushuikeji.www.yuyubuyer.utils.GetNormalOptionsUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

public class MyVideoCallActivity extends CallActivity implements View.OnClickListener, EMMessageListener {

    @InjectView(R.id.iv_myVideo_img)
    ImageView iv_myVideo_img;

    @InjectView(R.id.ll_coming_call)
    LinearLayout comingBtnContainer;

    @InjectView(R.id.tv_nick)
    TextView nickTextView;

    @InjectView(R.id.tv_call_state)
    TextView callStateTextView;

    @InjectView(R.id.btn_refuse_call)
    ImageButton refuseBtn;

    @InjectView(R.id.btn_answer_call)
    ImageButton answerBtn;

    @InjectView(R.id.btn_hangup_call)
    ImageButton hangupBtn;

    @InjectView(R.id.root_layout)
    RelativeLayout rootContainer;

    @InjectView(R.id.opposite_surface)
    EMOppositeSurfaceView oppositeSurface;

    @InjectView(R.id.local_surface)
    EMLocalSurfaceView localSurface;

    @InjectView(R.id.ib_myVideo_close)
    ImageButton ib_myVideo_close;

    @InjectView(R.id.myVideo_ProgressBar)
    ProgressBar myVideo_ProgressBar;

    @InjectView(R.id.rl_myVideo_chatLinearLayout)
    RelativeLayout rl_myVideo_chatLinearLayout;

    @InjectView(R.id.rl_myVideo_inputRelativeLayout)
    RelativeLayout rl_myVideo_inputRelativeLayout;

    @InjectView(R.id.iv_myVideo_input)
    ImageView iv_myVideo_input;

    @InjectView(R.id.ib_myVideo_requestImg)
    ImageView ib_myVideo_requestImg;

    @InjectView(R.id.rl_myVideo_afterInput)
    RelativeLayout rl_myVideo_afterInput;

    @InjectView(R.id.ct_myVideo_chronometer)
    Chronometer mChronometer;

    @InjectView(R.id.ll_myVideo_input)
    LinearLayout ll_myVideo_input;

    @InjectView(R.id.ll_myVideo_ll)
    LinearLayout ll_myVideo_normal;

    @InjectView(R.id.et_myVideo_editText)
    EditText et_myVideo_editText;

    @InjectView(R.id.b_myVideo_send)
    Button b_myVideo_send;

    @InjectView(R.id.lv_myVideo_chatListView)
    ListView lv_myVideo_chatListView;

    //视频续时
    @InjectView(R.id.tv_myVideo_t)
    TextView tv_myVideo_t;

    //预约陪逛
    @InjectView(R.id.tv_myVideo_bootMattention)
    TextView tv_myVideo_bootMattention;

    //举报
    @InjectView(R.id.tv_myVideo_report)
    TextView tv_myVideo_report;

    @InjectView(R.id.rl_myVideo_buyerMoneyInfo)
    RelativeLayout rl_myVideo_buyerMoneyInfo;

    private Handler uiHandler;

    protected String msgid;

    private boolean isInCalling;
    private boolean isHandsfreeState;
    private boolean endCallTriggerByMe = false;
    boolean isRecording = false;
    private boolean monitor = true;
    private EMCallManager.EMVideoCallHelper callHelper;

    private String getBuyerUrl = Constants.commontUrl + "im/name";

    private List<ChatBean> messagesList = new ArrayList<ChatBean>();//装载双方的数据
    private ChatListViewAdapter mChatListViewAdapter;//自定义适配器

    private String getBuyerBusinessUrl = Constants.commontUrl + "area/areainfo";
    private Map<String, String> mCityNameMap;
    private Map<String, String> mBusinessNameMap;
    private Map<String, String> mBuyerInfoMap;

    private InputMethodManager mInputMethodManager;

    private SoundPool receiveSoundPool;
    //记录是否第一次发送
    private boolean isFirstSendMessage = true;

    //进度条的行走
    private TimerTask task;
    private Timer timer;
    //记录进度时间
    private long progressTime;

    // 消息监听器
    private EMMessageListener mMessageListener;

    // 当前会话对象
    private EMConversation mConversation;
    private String mHeadImgUrl;
    private String oneselfHeadUrl;
    private InputMethodManager mImm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_my_video_call);
        initView();
        initData();
        initEvent();
    }

    private void initView() {

        ButterKnife.inject(this);

        mMessageListener = this;
        //输入法
        mImm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        //1：video call
        callType = 1;
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        uiHandler = new Handler();
        receiveSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
    }

    private void initData() {
        //拒绝
        refuseBtn.setOnClickListener(this);
        //接听
        answerBtn.setOnClickListener(this);
        //挂断
        hangupBtn.setOnClickListener(this);
        //根布局
        rootContainer.setOnClickListener(this);
        //关闭视频
        ib_myVideo_close.setOnClickListener(this);
        //点击头像
        iv_myVideo_img.setOnClickListener(this);
        //点击输入法的图片，底部换成聊天的样式
        rl_myVideo_inputRelativeLayout.setOnClickListener(this);
        //再次点击恢复默认
        rl_myVideo_afterInput.setOnClickListener(this);

        msgid = UUID.randomUUID().toString();
        // 获取通话是否为接收方向的(请求方传false   接收方传true)
        isInComingCall = getIntent().getBooleanExtra("isComingCall", false);

        username = getIntent().getStringExtra("username");

        //得到自己的头像
        oneselfHeadUrl = getIntent().getStringExtra("oneselfHeadUrl");

        // 设置通话人（请求网络）
        requestNetworkSetNick(username);

        // 显示本地图像的surfaceview(项目中暂时不需要展示)
        localSurface.setZOrderMediaOverlay(true);
        localSurface.setZOrderOnTop(true);
        // 设置通话监听
        addCallStateListener();

        if (!isInComingCall) {// 拨打电话(请求方 false)
            soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
            outgoing = soundPool.load(this, R.raw.em_outgoing, 1);

            comingBtnContainer.setVisibility(View.INVISIBLE);
            hangupBtn.setVisibility(View.VISIBLE);
            //设置视频预约和预约陪逛颜色,还有文字的不同
            tv_myVideo_t.setTextColor(UIUtils.getResources().getColor(R.color.text_redcolor_selector));
            tv_myVideo_bootMattention.setTextColor(UIUtils.getResources().getColor(R.color.text_redcolor_selector));
            tv_myVideo_report.setText("更多");
            tv_myVideo_report.setTextColor(UIUtils.getResources().getColor(R.color.text_redcolor_selector));

            String st = getResources().getString(R.string.Are_connected_to_each_other);
            callStateTextView.setText(st);
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
            handler.sendEmptyMessage(MSG_CALL_MAKE_VIDEO);
        } else { //有电话进来(接收方)

            if (EMClient.getInstance().callManager().getCallState() == EMCallStateChangeListener.CallState.IDLE
                    || EMClient.getInstance().callManager().getCallState() == EMCallStateChangeListener.CallState.DISCONNECTED) {
                // the call has ended
                finish();
                return;
            }
            //localSurface.setVisibility(View.INVISIBLE);
            //设置视频预约和预约陪逛颜色,还有文字的不同
            tv_myVideo_t.setTextColor(UIUtils.getResources().getColor(R.color.colorrVideo));
            tv_myVideo_bootMattention.setTextColor(UIUtils.getResources().getColor(R.color.colorrVideo));
            tv_myVideo_report.setText("举报");
            tv_myVideo_report.setTextColor(UIUtils.getResources().getColor(R.color.text_redcolor_selector));

            Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(true);
            ringtone = RingtoneManager.getRingtone(this, ringUri);
            ringtone.play();
            //为了不能让自己看到请求方的视频
            //显示一张请求者的图片，对方图片像素太差，还是不展示好，最后接通的时候进行展示
            //            ib_myVideo_requestImg.setVisibility(View.VISIBLE);
            //            oppositeSurface.setVisibility(View.GONE);
            //EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
        }
        //响铃5秒
        final int MAKE_CALL_TIMEOUT = 50 * 1000;
        handler.removeCallbacks(timeoutHangup);
        handler.postDelayed(timeoutHangup, MAKE_CALL_TIMEOUT);

        // get instance of call helper, should be called after setSurfaceView was called
        callHelper = EMClient.getInstance().callManager().getVideoCallHelper();

        /**
         * This function is only meaningful when your app need recording
         * If not, remove it.
         * This function need be called before the video stream started, so we set it in onCreate function.
         * This method will set the preferred video record encoding codec.
         * Using default encoding format, recorded file may not be played by mobile player.
         */
        //        callHelper.setPreferMovFormatEnable(true);

        //        EMClient.getInstance().callManager().setCameraDataProcessor(dataProcessor);

        //ListView的初始化
        mChatListViewAdapter = new ChatListViewAdapter(messagesList);
        lv_myVideo_chatListView.setAdapter(mChatListViewAdapter);
    }

    private void initEvent() {
        //对输入框的监听
        et_myVideo_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(et_myVideo_editText.getText().toString().trim())) {
                    //恢复默认
                    b_myVideo_send.setClickable(false);
                    try {
                        b_myVideo_send.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.input_button_shap));
                        b_myVideo_send.setTextColor(UIUtils.getResources().getColor(R.color.colorregister));
                    } catch (Exception exception) {
                    }
                } else {
                    b_myVideo_send.setClickable(true);
                    try {
                        b_myVideo_send.setBackgroundDrawable(UIUtils.getResources().getDrawable(R.drawable.inputpre_button_shap));
                        b_myVideo_send.setTextColor(UIUtils.getResources().getColor(R.color.colorCustomEditText));
                    } catch (Exception exception) {
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //发送聊天信息
        b_myVideo_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    return;
                } else {
                    b_myVideo_send.requestFocus();
                    //发送聊天信息
                    sendChatMessage(username);
                }
            }
        });

        //一进来发送就不可以点击(必须在设置监听之后，不然不起作用)
        b_myVideo_send.setClickable(false);


    }

    /**
     * 发送聊天信息
     */
    private void sendChatMessage(String withChatName) {

        final String chatContent = et_myVideo_editText.getText().toString().trim();
        if (TextUtils.isEmpty(chatContent)) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "发送信息不能为空");
            return;
        } else if (TextUtils.isEmpty(withChatName)) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "无法明确和谁进行聊天");
            return;
        } else {
            //显示listView的界面
            rl_myVideo_chatLinearLayout.setVisibility(View.VISIBLE);
            //真正的发送聊天
            et_myVideo_editText.setText("");
            // 创建一条新消息，第一个参数为消息内容，第二个为接受者username
            EMMessage message = EMMessage.createTxtSendMessage(chatContent, withChatName);

            // 调用发送消息的方法
            EMClient.getInstance().chatManager().sendMessage(message);
            //由于第一次发送，可能不会回调onSuccess()所以在这里调用一次发送
            if(isFirstSendMessage) {
                //发送消息给对方，展示自己的头像
                sendAndReceiveMessage("wushui", chatContent,oneselfHeadUrl);
            }

            final String tempChatContent = chatContent;

            // 为消息设置回调
            message.setMessageStatusCallback(new EMCallBack() {
                @Override
                public void onSuccess() {
                    //子线程
                    // 消息发送成功，正常操作应该去刷新ui
                    //自己发送这里需要也保留发送消息
                    UIUtils.getMainThreadHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            isFirstSendMessage = false;
                            if(!isFirstSendMessage) {
                                //发送消息给对方，展示自己的头像
                                sendAndReceiveMessage("wushui", tempChatContent,oneselfHeadUrl);
                            }
                        }
                    });
                }

                @Override
                public void onError(int i, String s) {
                    // 消息发送失败，打印下失败的信息，正常操作应该去刷新ui
                }

                @Override
                public void onProgress(int i, String s) {
                    // 消息发送进度，一般只有在发送图片和文件等消息才会有回调，txt不回调
                }
            });
        }
    }

    /**
     * 初始化会话对象，并且根据需要加载更多消息(从保存的消息记录中取消息)
     */
    private void initConversation() {
        /**
         * 初始化会话对象，这里有三个参数么，
         * 第一个表示会话的当前聊天的 useranme 或者 groupid
         * 第二个是绘画类型可以为空
         * 第三个表示如果会话不存在是否创建
         */
        mConversation = EMClient.getInstance().chatManager().getConversation(username, null, true);

        //        int count = mConversation.getAllMessages().size();
        //        if (count < mConversation.getAllMsgCount() && count < 20) {
        //            // 获取已经在列表中的最上边的一条消息id
        //            String msgId = mConversation.getAllMessages().get(0).getMsgId();
        //            // 分页加载更多消息，需要传递已经加载的消息的最上边一条消息的id，以及需要加载的消息的条数
        //            mConversation.loadMoreMsgFromDB(msgId, 20 - count);
        //        }
        // 打开聊天界面获取最后一条消息内容并显示
        //        if (mConversation.getAllMessages().size() > 0) {
        //            EMMessage messge = mConversation.getLastMessage();
        //            EMTextMessageBody body = (EMTextMessageBody) messge.getBody();
        //            // 将消息内容和时间显示出来
        //            mContentText.setText("聊天记录：" + body.getMessage() + " - time: " + mConversation.getLastMessage().getMsgTime());
        //        }

        // 设置当前会话未读数为 0
        mConversation.markAllMessagesAsRead();

        int count = mConversation.getAllMessages().size();

        if (count > 0) {
            //打开聊天界面获取所有的消息内容并显示
            ChatBean mChatBean = null;
            //emMessage.getBody().toString()  txt:"hh"
            for(int i=0;i<count;i++) {
                EMTextMessageBody body = (EMTextMessageBody) mConversation.getAllMessages().get(i).getBody();
                mChatBean = new ChatBean();
                mChatBean.setTo("jack");
                mChatBean.setContent(body.getMessage());
                mChatBean.setIconUrl(mHeadImgUrl);
                messagesList.add(mChatBean);
            }
            mChatListViewAdapter.notifyDataSetChanged();
            //回到最下面
            lv_myVideo_chatListView.setSelection(messagesList.size());
        }
    }

    /**
     * 将加密之后的环信账户名改成鱼鱼账户名
     */
    private void requestNetworkSetNick(String imName) {

        OkHttpUtils.post().url(getBuyerUrl).addParams("im_name", imName).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    // 成功了，只是返回来请求的json数据,还需要解析
                    Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                    String response_code = map.get("response_code");
                    if ("0".equals(response_code)) {
                        mBuyerInfoMap = VideoJsonParse.videoJsonParse(response);
                        if (mBuyerInfoMap != null) {
                            //设置谁邀请我视频
                            nickTextView.setText(mBuyerInfoMap.get("username"));
                            //设置头像
                            mHeadImgUrl = mBuyerInfoMap.get("head_img");
                            if (!TextUtils.isEmpty(mHeadImgUrl)) {
                                ImageLoader.getInstance().displayImage(mHeadImgUrl, iv_myVideo_img, GetNormalOptionsUtils.getNormalOptionsUtils());

                                //显示让接受方看到对方的头像
                                ImageLoader.getInstance().displayImage(mHeadImgUrl, ib_myVideo_requestImg, GetNormalOptionsUtils.getNormalOptionsUtils());
                            }
                            //获取buyer的商圈信息
                            requestBusinessData(mBuyerInfoMap.get("area_id"));
                        }
                    } else {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "获取Buyer信息失败");
                    }
                    //只有等头像弄好了，才能展示以前回话未收到的消息，因为需要展示头像
                    initConversation();
                } catch (Exception e) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                }
            }
        });
    }

    /**
     * 获取buyer的商圈信息
     */
    private void requestBusinessData(String areaId) {
        OkHttpUtils.get().url(getBuyerBusinessUrl).addParams("area_id", areaId).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    // 成功了，只是返回来请求的json数据,还需要解析
                    Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                    String response_code = map.get("response_code");
                    if ("0".equals(response_code)) {
                        mCityNameMap = BusinessJsonParse.getCityNameParse(response);
                        mBusinessNameMap = BusinessJsonParse.getBusinessNameParse(response);
                    }
                } catch (Exception e) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_refuse_call: // 拒绝接听
                isRefused = true;
                refuseBtn.setEnabled(false);
                handler.sendEmptyMessage(MSG_CALL_REJECT);
                break;

            case R.id.btn_answer_call: // 接听电话
                answerBtn.setEnabled(false);
                openSpeakerOn();
                if (ringtone != null)
                    ringtone.stop();

                callStateTextView.setText("正在接听...");
                handler.sendEmptyMessage(MSG_CALL_ANSWER);
                isAnswered = true;
                isHandsfreeState = true;
                comingBtnContainer.setVisibility(View.INVISIBLE);
                //挂断
                hangupBtn.setVisibility(View.VISIBLE);
                //                localSurface.setVisibility(View.VISIBLE);
                break;

            case R.id.btn_hangup_call: // 挂断电话
                hangupBtn.setEnabled(false);
                //停止记时
                //                chronometer.stop();
                endCallTriggerByMe = true;
                callStateTextView.setText(getResources().getString(R.string.hanging_up));
                if (isRecording) {
                    callHelper.stopVideoRecord();
                }
                handler.sendEmptyMessage(MSG_CALL_END);
                break;

            case R.id.ib_myVideo_close: // 视频成功后，关闭视频
                ib_myVideo_close.setEnabled(false);
                //停止记时
                mChronometer.stop();
                endCallTriggerByMe = true;
                //callStateTextView.setText(getResources().getString(R.string.hanging_up));
                if (isRecording) {
                    callHelper.stopVideoRecord();
                }
                handler.sendEmptyMessage(MSG_CALL_END);
                break;
            case R.id.iv_myVideo_img://点击头像
                //弹出自定义对话框
                setBuyerInfoDialog();
                break;
            case R.id.rl_myVideo_inputRelativeLayout://点击左边聊天的输入法，打开聊天的界面
                ll_myVideo_normal.setVisibility(View.GONE);
                ll_myVideo_input.setVisibility(View.VISIBLE);
                //弹出输入法
                et_myVideo_editText.setFocusable(true);
                et_myVideo_editText.setFocusableInTouchMode(true);
                et_myVideo_editText.requestFocus();
                et_myVideo_editText.requestFocusFromTouch();
                displayInput(et_myVideo_editText);
                break;
            case R.id.rl_myVideo_afterInput://点击打开后的聊天输入法，恢复原状
                ll_myVideo_normal.setVisibility(View.VISIBLE);
                ll_myVideo_input.setVisibility(View.GONE);

                //listView聊天界面关闭
                rl_myVideo_chatLinearLayout.setVisibility(View.GONE);
                //不显示输入法
                View mView = getWindow().peekDecorView();
                if (mView != null && mView.getWindowToken() != null) {
                    mImm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                break;
            //            case R.id.root_layout:
            //                if (callingState == CallingState.NORMAL) {
            //                    comingBtnContainer.setVisibility(View.GONE);
            //                    //和谁通话
            //                    nickTextView.setVisibility(View.GONE);
            //                    //连接状态
            //                    callStateTextView.setVisibility(View.GONE);
            //                    //头像
            //                    iv_myVideo_img.setVisibility(View.GONE);
            //                    //挂断电话隐藏
            //
            //                    if (comingBtnContainer.getVisibility() == View.VISIBLE) {
            //                        oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
            //                    } else {
            //                        oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFit);
            //                    }
            //                }
            //                break;
            //            case R.id.btn_switch_camera: //切换照相机
            //                handler.sendEmptyMessage(MSG_CALL_SWITCH_CAMERA);
            //                break;
            default:
                break;
        }
    }

    /**
     * 设置自定义的对话框显示buyer的信息
     */
    private void setBuyerInfoDialog() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MyVideoCallActivity.this);
        View inflate = View.inflate(MyVideoCallActivity.this, R.layout.custom_buyerinfo_dialog, null);
        //头像
        ImageView headImg = (ImageView) inflate.findViewById(R.id.iv_customDialog_img);
        //buyerName
        TextView buyerName = (TextView) inflate.findViewById(R.id.tv_customDialog_buyerName);
        //buyer等级图片
        ImageView gradeImg = (ImageView) inflate.findViewById(R.id.iv_customDialog_grade);
        //buyerId
        TextView buyerId = (TextView) inflate.findViewById(R.id.tv_customDialog_buyerId);
        //buyerSexImg
        ImageView buyerSexImg = (ImageView) inflate.findViewById(R.id.iv_customDialog_sex);
        //buyerAge
        TextView buyerAge = (TextView) inflate.findViewById(R.id.tv_customDialog_age);
        //buyerCity
        TextView buyerCity = (TextView) inflate.findViewById(R.id.tv_customDialog_city);
        //buyerBusiness
        TextView buyerBusiness = (TextView) inflate.findViewById(R.id.tv_customDialog_business);
        //设置数据
        if (mBuyerInfoMap != null) {
            //设置头像
            ImageLoader.getInstance().displayImage(mBuyerInfoMap.get("head_img"), headImg, GetNormalOptionsUtils.getNormalOptionsUtils());

            buyerName.setText(mBuyerInfoMap.get("username"));

            //设置等级图片
            String grades = mBuyerInfoMap.get("user_level");
            if ("5".equals(grades)) {
                gradeImg.setBackgroundResource(R.mipmap.buyerdetails_v5);
            } else if ("4".equals(grades)) {
                gradeImg.setBackgroundResource(R.mipmap.buyerdetails_v4);
            } else if ("3".equals(grades)) {
                gradeImg.setBackgroundResource(R.mipmap.buyerdetails_v3);
            } else if ("2".equals(grades)) {
                gradeImg.setBackgroundResource(R.mipmap.buyerdetails_v2);
            } else {
                gradeImg.setBackgroundResource(R.mipmap.buyerdetails_v1);
            }

            buyerId.setText(mBuyerInfoMap.get("id"));
            //设置男还是女的图片 1:男
            String sex = mBuyerInfoMap.get("sex");
            if ("1".equals(sex)) {
                buyerSexImg.setImageResource(R.mipmap.boy);
            } else if ("2".equals(sex)) {
                buyerSexImg.setImageResource(R.mipmap.girl);
            }
            buyerAge.setText(mBuyerInfoMap.get("age"));
        }

        //设置城市
        if (mCityNameMap != null) {
            buyerCity.setText(mCityNameMap.get("city"));
        }
        //设置商圈
        if (mBusinessNameMap != null) {
            buyerBusiness.setText(mBusinessNameMap.get("name"));
        }

        mBuilder.setView(inflate);
        mBuilder.show();
        //alertDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 设置通话状态监听
     */
    void addCallStateListener() {
        callStateListener = new EMCallStateChangeListener() {

            @Override
            public void onCallStateChanged(CallState callState, final CallError error) {
                switch (callState) {

                    case CONNECTING: // 正在连接对方
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                //displayKongJian();
                                callStateTextView.setText(R.string.Are_connected_to_each_other);
                            }

                        });
                        break;
                    case CONNECTED: // 双方已经建立连接
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                //displayKongJian();
                                callStateTextView.setText(R.string.have_connected_with);
                            }

                        });
                        break;

                    case ACCEPTED: // 电话接通成功
                        handler.removeCallbacks(timeoutHangup);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    if (soundPool != null)
                                        soundPool.stop(streamID);
                                } catch (Exception e) {
                                }
                                openSpeakerOn();
                                isHandsfreeState = true;
                                isInCalling = true;
                                // 开始记时
                                //计时器显示
                                mChronometer.setVisibility(View.VISIBLE);
                                mChronometer.setBase(SystemClock.elapsedRealtime());
                                mChronometer.start();
                                progressBarDisplay();
                                //进度条走(暂时设置10分钟)
                                myVideo_ProgressBar.setMax(600);

                                //和谁通话
                                nickTextView.setVisibility(View.INVISIBLE);
                                //连接状态
                                callStateTextView.setVisibility(View.INVISIBLE);
                                //头像
                                iv_myVideo_img.setVisibility(View.GONE);
                                //拒绝和接听
                                comingBtnContainer.setVisibility(View.GONE);
                                //挂断电话隐藏，右上角关闭电话出现
                                hangupBtn.setVisibility(View.GONE);
                                ib_myVideo_close.setVisibility(View.VISIBLE);
                                if (!isInComingCall) {
                                    //请求方
                                    ib_myVideo_requestImg.setVisibility(View.GONE);
                                    oppositeSurface.setVisibility(View.VISIBLE);
                                    //buyer的价格摆出来
                                    rl_myVideo_buyerMoneyInfo.setVisibility(View.VISIBLE);
                                } else {
                                    //接收方
                                    ib_myVideo_requestImg.setVisibility(View.VISIBLE);
                                    oppositeSurface.setVisibility(View.GONE);
                                    //隐藏buyer的价格
                                    rl_myVideo_buyerMoneyInfo.setVisibility(View.GONE);
                                }
                                callingState = CallingState.NORMAL;
                            }

                        });
                        break;
                    case NETWORK_DISCONNECTED://网络不可用
                        runOnUiThread(new Runnable() {
                            public void run() {
                                ToastUtils.showToastInUIThread(UIUtils.getContext(), "网络连接不可用，请检查网络");
                            }
                        });
                        break;
                    case NETWORK_UNSTABLE:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (error == CallError.ERROR_NO_DATA) {
                                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "没有通话数据");
                                } else {
                                    ToastUtils.showToastInUIThread(UIUtils.getContext(), String.valueOf(R.string.network_unstable));
                                }
                            }
                        });
                        break;
                    case NETWORK_NORMAL:
                        break;
                    case VIDEO_PAUSE:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "VIDEO_PAUSE", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case VIDEO_RESUME:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "VIDEO_RESUME", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case VOICE_PAUSE:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "VOICE_PAUSE", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case VOICE_RESUME:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "VOICE_RESUME", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case DISCONNECTED: // 电话断了
                        handler.removeCallbacks(timeoutHangup);
                        @SuppressWarnings("UnnecessaryLocalVariable") final CallError fError = error;
                        runOnUiThread(new Runnable() {
                            private void postDelayedCloseMsg() {
                                uiHandler.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        removeCallStateListener();
                                        saveCallRecord();
                                        Animation animation = new AlphaAnimation(1.0f, 0.0f);
                                        animation.setDuration(1200);
                                        rootContainer.startAnimation(animation);
                                        finish();
                                    }

                                }, 200);
                            }

                            @Override
                            public void run() {
                                //关闭定时器
                                if (timer != null) {
                                    timer.cancel();
                                    timer = null;
                                }
                                if (task != null) {
                                    task.cancel();
                                    task = null;
                                }
                                mChronometer.stop();
                                String s1 = getResources().getString(R.string.The_other_party_refused_to_accept);
                                String s2 = getResources().getString(R.string.Connection_failure);
                                String s3 = getResources().getString(R.string.The_other_party_is_not_online);
                                String s4 = getResources().getString(R.string.The_other_is_on_the_phone_please);
                                String s5 = getResources().getString(R.string.The_other_party_did_not_answer);

                                String s6 = getResources().getString(R.string.hang_up);
                                String s7 = getResources().getString(R.string.The_other_is_hang_up);
                                String s8 = getResources().getString(R.string.did_not_answer);
                                String s9 = getResources().getString(R.string.Has_been_cancelled);
                                String s10 = getResources().getString(R.string.Refused);

                                if (fError == CallError.REJECTED) {
                                    callingState = CallingState.BEREFUSED;
                                    callStateTextView.setText(s1);
                                    ToastUtils.showToastInUIThread(UIUtils.getContext(), s1);
                                } else if (fError == CallError.ERROR_TRANSPORT) {
                                    callStateTextView.setText(s2);
                                    ToastUtils.showToastInUIThread(UIUtils.getContext(), s2+"请退出重新登录");
                                } else if (fError == CallError.ERROR_UNAVAILABLE) {
                                    callingState = CallingState.OFFLINE;
                                    callStateTextView.setText(s3);
                                    ToastUtils.showToastInUIThread(UIUtils.getContext(), s3);
                                } else if (fError == CallError.ERROR_BUSY) {
                                    callingState = CallingState.BUSY;
                                    callStateTextView.setText(s4);
                                    ToastUtils.showToastInUIThread(UIUtils.getContext(), s4);
                                } else if (fError == CallError.ERROR_NORESPONSE) {
                                    callingState = CallingState.NO_RESPONSE;
                                    callStateTextView.setText(s5);
                                    ToastUtils.showToastInUIThread(UIUtils.getContext(), s5);
                                } else if (fError == CallError.ERROR_LOCAL_SDK_VERSION_OUTDATED || fError == CallError.ERROR_REMOTE_SDK_VERSION_OUTDATED) {
                                    callingState = CallingState.VERSION_NOT_SAME;
                                    callStateTextView.setText(R.string.call_version_inconsistent);
                                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "通话协议版本不一致");
                                } else {
                                    if (isRefused) {
                                        callingState = CallingState.REFUSED;
                                        callStateTextView.setText(s10);
                                        ToastUtils.showToastInUIThread(UIUtils.getContext(), s10);
                                    } else if (isAnswered) {
                                        callingState = CallingState.NORMAL;
                                        if (endCallTriggerByMe) {
                                            //  callStateTextView.setText(s6);
                                        } else {
                                            callStateTextView.setText(s7);
                                            ToastUtils.showToastInUIThread(UIUtils.getContext(), s7);
                                        }
                                    } else {
                                        if (isInComingCall) {
                                            callingState = CallingState.UNANSWERED;
                                            callStateTextView.setText(s8);
                                            ToastUtils.showToastInUIThread(UIUtils.getContext(), s8);
                                        } else {
                                            if (callingState != CallingState.NORMAL) {
                                                callingState = CallingState.CANCELLED;
                                                callStateTextView.setText(s9);
                                                ToastUtils.showToastInUIThread(UIUtils.getContext(), s9);
                                            } else {
                                                callStateTextView.setText(s6);
                                                ToastUtils.showToastInUIThread(UIUtils.getContext(), s6);
                                            }
                                        }
                                    }
                                }
                                postDelayedCloseMsg();
                            }

                        });
                        break;
                    default:
                        break;
                }

            }
        };
        EMClient.getInstance().callManager().addCallStateChangeListener(callStateListener);
    }

    void removeCallStateListener() {
        EMClient.getInstance().callManager().removeCallStateChangeListener(callStateListener);
    }

    /**
     * 设置控件显示
     */
    private void displayKongJian() {
        //和谁通话
        nickTextView.setVisibility(View.VISIBLE);
        //连接状态
        callStateTextView.setVisibility(View.VISIBLE);
        //头像
        iv_myVideo_img.setVisibility(View.VISIBLE);
        //拒绝和接听
        comingBtnContainer.setVisibility(View.VISIBLE);
    }

    void stopMonitor() {
        monitor = false;
    }

    @Override
    protected void onDestroy() {
        // DemoHelper.getInstance().isVideoCalling = false;
        stopMonitor();
        if (isRecording) {
            callHelper.stopVideoRecord();
            isRecording = false;
        }
        localSurface.getRenderer().dispose();
        localSurface = null;
        oppositeSurface.getRenderer().dispose();
        oppositeSurface = null;
        super.onDestroy();

        //关闭定时器
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public void onBackPressed() {
        //callDruationText = chronometer.getText().toString();
        //关闭定时器
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        super.onBackPressed();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (isInCalling) {
            try {
                EMClient.getInstance().callManager().pauseVideoTransfer();
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isInCalling) {
            try {
                EMClient.getInstance().callManager().resumeVideoTransfer();
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
        // 添加消息监听
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
    }

    /**
     * 计时器，用于给进度条
     */
    public void progressBarDisplay() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                // 子线程,进度条可以更新UI
                progressTime++;
                //设置进度条再走
                myVideo_ProgressBar.setProgress((int) progressTime);
            }
        };
        timer.schedule(task, 0, 1000);
    }

    /**
     * 格式化时间  保留2位
     */
    private String timeStrFormat(String timeStr) {
        switch (timeStr.length()) {
            case 1:
                timeStr = "0" + timeStr;
                break;
        }
        return timeStr;
    }

    /**
     * time 单位为妙
     * 处理长时间,转换时 分 秒
     */
    private String dealTime(long time) {

        StringBuffer sb = new StringBuffer();

        long day = time / (24 * 60 * 60);

        long hours = (time % (24 * 60 * 60)) / (60 * 60);

        long minutes = ((time % (24 * 60 * 60)) % (60 * 60)) / 60;

        long second = ((time % (24 * 60 * 60)) % (60 * 60)) % 60;

        String dayStr = String.valueOf(day);
        String hoursStr = timeStrFormat(String.valueOf(hours));
        String minutesStr = timeStrFormat(String.valueOf(minutes));
        String secondStr = timeStrFormat(String.valueOf(second));

        sb.append(dayStr).append("天").append(hoursStr).append("小时").append(minutesStr).append("分").append(secondStr).append("秒");

        return sb.toString();
    }

    /**
     * 下面的5个方法全是发送和接受消息的回调
     */
    @Override
    public void onMessageReceived(List<EMMessage> list) {
        // 循环遍历当前收到的消息
        for (final EMMessage message : list) {
            if (message.getFrom().equals(username)) {
                // 设置消息为已读
                mConversation.markMessageAsRead(message.getMsgId());

                // 因为消息监听回调这里是非ui线程，所以要用handler去更新ui
                UIUtils.getMainThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if(rl_myVideo_chatLinearLayout.getVisibility() == View.GONE) {
                            rl_myVideo_chatLinearLayout.setVisibility(View.VISIBLE);
                            ll_myVideo_normal.setVisibility(View.GONE);
                            ll_myVideo_input.setVisibility(View.VISIBLE);
                        }
                        //受到消息发出声音
                        receiveSoundPool.load(MyVideoCallActivity.this,R.raw.message,1);
                        receiveSoundPool.play(1,1, 1, 0, 0, 1);//倒数第两个参数-1表示循环，其他非负数表示循环次数，也就是总共播放n+1次
                        //展示发消息给我的那个人的头像 useName刚才就是那个人的环信账号,已经获取了鱼鱼的headImg
                        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                        sendAndReceiveMessage("jack",body.getMessage(),mHeadImgUrl);
                    }
                });
            }else {
                // 如果消息不是当前会话的消息发送通知栏通知
            }
        }
    }
    /**
     * 收到新的 CMD 消息
     */
    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {
        for (int i = 0; i < list.size(); i++) {
            // 透传消息
            EMMessage cmdMessage = list.get(i);
            EMCmdMessageBody body = (EMCmdMessageBody) cmdMessage.getBody();
        }
    }
    /**
     * 收到新的已读回执
     */
    @Override
    public void onMessageReadAckReceived(List<EMMessage> list) {

    }
    /**
     * 收到新的发送回执
     * TODO 无效 暂时有bug
     */
    @Override
    public void onMessageDeliveryAckReceived(List<EMMessage> list) {

    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {

    }

    /**
     * 自己封装发送的消息
     */
    private void sendAndReceiveMessage(String to, String content,String imgUrl) {
        ChatBean mChatBean = new ChatBean();
        mChatBean.setTo(to);
        mChatBean.setContent(content);
        mChatBean.setIconUrl(imgUrl);
        messagesList.add(mChatBean);
        mChatListViewAdapter.notifyDataSetChanged();
        //回到最下面
        lv_myVideo_chatListView.setSelection(messagesList.size());
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 移除消息监听
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
    }

    //自动弹出输入法
    private void displayInput(final EditText et) {
        UIUtils.getMainThreadHandler().postDelayed(new Runnable() {
            public void run() {
                InputMethodManager inManager = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 200);
    }
}
