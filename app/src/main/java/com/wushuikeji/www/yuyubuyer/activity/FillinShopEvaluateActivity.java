package com.wushuikeji.www.yuyubuyer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.CloseInputUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.LoadingRoateAnimationUtils;
import com.wushuikeji.www.yuyubuyer.utils.RecoverClickUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

public class FillinShopEvaluateActivity extends AppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.ll_fillin_heartOne)
    LinearLayout ll_fillin_heartOne;

    @InjectView(R.id.ll_fillin_heartTwo)
    LinearLayout ll_fillin_heartTwo;

    @InjectView(R.id.ll_fillin_heartThree)
    LinearLayout ll_fillin_heartThree;

    @InjectView(R.id.ll_fillin_heartfour)
    LinearLayout ll_fillin_heartfour;

    @InjectView(R.id.ll_fillin_heartFive)
    LinearLayout ll_fillin_heartFive;

    @InjectView(R.id.one)
    ImageView iv_one;

    @InjectView(R.id.two)
    ImageView iv_two;

    @InjectView(R.id.three)
    ImageView iv_three;

    @InjectView(R.id.four)
    ImageView iv_four;

    @InjectView(R.id.five)
    ImageView iv_five;

    @InjectView(R.id.rl_fillin_RelativeLayout)
    RelativeLayout rl_fillin_RelativeLayout;

    @InjectView(R.id.b_fillinShop_submit)
    Button b_fillinShop_submit;

    @InjectView(R.id.et_fillinShop_et)
    EditText et_fillinShop_et;

    @InjectView(R.id.iv_fillinShop_loading)
    ImageView iv_fillinShop_loading;



    private boolean imgOne,imgTwo,imgThree,imgFour,imgFive;
    private int imgOneCount,imgTwoCount,imgThreeCount,imgFourCount,imgFiveCount;

    //评价商铺
    private String inputEvaluateUrl = Constants.commontUrl + "appraisal/evaluation_shop";
    private String mShopId;
    private String mUserId;
    private String mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fillin_shop_evaluate);
        initView();
        initEvent();
    }

    private void initView() {

        ButterKnife.inject(this);

        //得到传过来的shopId
        mShopId = getIntent().getStringExtra("shopId");

        //用户id
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");

        iv_one.setEnabled(false);
        iv_two.setEnabled(false);
        iv_three.setEnabled(false);
        iv_four.setEnabled(false);
        iv_five.setEnabled(false);
    }

    private void initEvent() {
        //返回
        BackListenerUtils.backFinish(rl_fillin_RelativeLayout,this);

        ll_fillin_heartOne.setOnClickListener(this);
        ll_fillin_heartTwo.setOnClickListener(this);
        ll_fillin_heartThree.setOnClickListener(this);
        ll_fillin_heartfour.setOnClickListener(this);
        ll_fillin_heartFive.setOnClickListener(this);
        b_fillinShop_submit.setOnClickListener(this);
    }

        @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_fillin_heartOne:
                if(!imgOne) {
                    //红心
                    iv_one.setEnabled(true);
                    imgOne = true;
                    imgOneCount = 1;
                }else {
                    //空心
                    iv_one.setEnabled(false);
                    imgOne = false;
                    imgOneCount = 0;
                }
                break;
            case R.id.ll_fillin_heartTwo:
                if(!imgTwo) {
                    //红心
                    iv_two.setEnabled(true);
                    imgTwo = true;
                    imgTwoCount = 1;
                }else {
                    //空心
                    iv_two.setEnabled(false);
                    imgTwo = false;
                    imgTwoCount = 0;
                }
                break;
            case R.id.ll_fillin_heartThree:
                if(!imgThree) {
                    //红心
                    iv_three.setEnabled(true);
                    imgThree = true;
                    imgThreeCount = 1;
                }else {
                    //空心
                    iv_three.setEnabled(false);
                    imgThree = false;
                    imgThreeCount = 0;
                }
                break;
            case R.id.ll_fillin_heartfour:
                if(!imgFour) {
                    //红心
                    iv_four.setEnabled(true);
                    imgFour = true;
                    imgFourCount = 1;
                }else {
                    //空心
                    iv_four.setEnabled(false);
                    imgFour = false;
                    imgFourCount = 0;
                }
                break;
            case R.id.ll_fillin_heartFive:
                if(!imgFive) {
                    //红心
                    iv_five.setEnabled(true);
                    imgFive = true;
                    imgFiveCount = 1;
                }else {
                    //空心
                    iv_five.setEnabled(false);
                    imgFive = false;
                    imgFiveCount = 0;
                }
                break;
            case R.id.b_fillinShop_submit://提交评价
                int allCount = imgOneCount + imgTwoCount + imgThreeCount + imgFourCount + imgFiveCount;
                submitContent(allCount);
                break;
            default:
                break;
        }
    }

    /**
     * 提交评价
     */
    private void submitContent(int scoreCount) {
        //内容
        mContent = et_fillinShop_et.getText().toString();
        if (TextUtils.isEmpty(mContent)) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "评价内容不能空");
            return;
        }
        //判断网络
        boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
        if (!iSNetworkConnect) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
            return;
        } else {
            //关闭输入法
            CloseInputUtils.closeInput(UIUtils.getContext(), FillinShopEvaluateActivity.this);
            //加载动画
            iv_fillinShop_loading.setVisibility(View.VISIBLE);
            LoadingRoateAnimationUtils.loadingGifPicture(iv_fillinShop_loading,UIUtils.getContext());
            // 动画的时候，按钮不能点击了
            cancelClick();
            OkHttpUtils.post().url(inputEvaluateUrl).addParams("shop_id", mShopId)
                    .addParams("user_id", mUserId)
                    .addParams("content", mContent).addParams("score",String.valueOf(scoreCount))
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    try {
                        //动画关闭
                        iv_fillinShop_loading.setVisibility(View.GONE);
                        //恢复编辑状态
                        recoverClick();
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"提交失败");
                    } catch (Exception exception) {
                    }
                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        //动画关闭
                        iv_fillinShop_loading.setVisibility(View.GONE);
                        //恢复编辑状态
                        recoverClick();
                        // 成功了，只是返回来请求的json数据,还需要解析
                        Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                        String response_code = map.get("response_code");
                        //需要判断各种响应码,获取具体错误信息
                        String responseContent = RequestResponseUtils.getResponseContent(response_code);

                        if("0".equals(response_code)) {
                            //提交成功
                            et_fillinShop_et.setText("");
                            iv_one.setEnabled(false);
                            iv_two.setEnabled(false);
                            iv_three.setEnabled(false);
                            iv_four.setEnabled(false);
                            iv_five.setEnabled(false);
                            ToastUtils.showToastInUIThread(UIUtils.getContext(),"提交成功");
                            FillinShopEvaluateActivity.this.finish();
                            //刷新商铺评价的数据
                            ShopEvaluateActivity.mShopEvaluateActivity.refreshData();
                        }else if (responseContent != null) {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                        }else {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(),"提交失败");
                        }
                    } catch (Exception e) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"提交失败");
                        //动画关闭
                        iv_fillinShop_loading.setVisibility(View.GONE);
                        //恢复编辑状态
                        recoverClick();
                    }
                }
            });
        }
    }
    /**
     * 动画的时候不能点击
     */
    private void cancelClick() {
        b_fillinShop_submit.setClickable(false);
        iv_one.setClickable(false);
        iv_two.setClickable(false);
        iv_three.setClickable(false);
        iv_four.setClickable(false);
        iv_five.setClickable(false);
        // 动画的时候，让EditText失去焦点，不能输入
        et_fillinShop_et.setFocusable(false);
    }

    /**
     * 恢复默认可以点击
     */
    private void recoverClick() {
        //让EditText恢复编辑状态
        RecoverClickUtils.recoverClick(et_fillinShop_et);
        //其他控件可以点击
        b_fillinShop_submit.setClickable(true);
        iv_one.setClickable(true);
        iv_two.setClickable(true);
        iv_three.setClickable(true);
        iv_four.setClickable(true);
        iv_five.setClickable(true);
    }
}
