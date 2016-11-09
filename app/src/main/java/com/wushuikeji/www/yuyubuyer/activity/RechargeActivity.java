package com.wushuikeji.www.yuyubuyer.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.base.BaseApplication;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.ContentJsonParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.MyObjeceParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.myalipay.PayResult;
import com.wushuikeji.www.yuyubuyer.myalipay.SignUtils;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.Base64Utils;
import com.wushuikeji.www.yuyubuyer.utils.CloseInputUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.MD5;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.wushuikeji.www.yuyubuyer.view.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

public class RechargeActivity extends AppCompatActivity {

    @InjectView(R.id.b_recharge_recharge)
    Button b_recharge_recharge;

    @InjectView(R.id.rl_recharge_back)
    RelativeLayout rl_recharge_back;

    @InjectView(R.id.ct_recharge_money)
    ClearEditText ct_recharge_money;

    private PopupWindow mPopupWindow;

    // 微信支付
    private PayReq req;
    private IWXAPI msgApi;
    private Map<String, Object> map;
    private ProgressDialog dialog;

    private static final int SDK_PAY_FLAG = 1;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(RechargeActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        RechargeActivity.this.finish();
                        //回到了我的账户界面，并且刷新
                        MyAccountActivity.mMyAccountActivity.reSetRefreshData();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(RechargeActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(RechargeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };
    //充值
    private String topUpUrl = "http://120.25.86.253/payserver/generate/order";
    //支付
    private String payUrl = "http://120.25.86.253/payserver/pay";

    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        initView();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);
        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");
        //初始化微信
        msgApi = WXAPIFactory.createWXAPI(this, null);
        req = new PayReq();
    }

    private void initEvent() {
        //返回
        BackListenerUtils.backFinish(rl_recharge_back, this);
        //确认充值
        b_recharge_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //金额必须大于0
                String money = ct_recharge_money.getText().toString().trim();
                if (TextUtils.isEmpty(money)) {
                    ct_recharge_money.setShakeAnimation();
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "金额不能为空");
                    return;
                }
                if ("0".equals(money)) {
                    ct_recharge_money.setShakeAnimation();
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "金额不能为0");
                    return;
                }
                //关闭输入法
                CloseInputUtils.closeInput(UIUtils.getContext(), RechargeActivity.this);
                setPayPop();
            }
        });
    }

    /**
     * 弹出支付的选择方式
     */
    private void setPayPop() {
        View popupwindowView = getLayoutInflater().inflate(R.layout.pay_select_pop, null);
        //银联
        //        TextView yinLianTV = (TextView) popupwindowView.findViewById(R.id.tv_popupwindow_yinLian);
        //支付宝
        TextView aliPayTV = (TextView) popupwindowView.findViewById(R.id.tv_popupwindow_aliPay);
        //微信
        TextView weixinTV = (TextView) popupwindowView.findViewById(R.id.tv_popupwindow_weixin);
        //取消
        TextView cancelTV = (TextView) popupwindowView.findViewById(R.id.tv_popupwindow_cancel);

        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(popupwindowView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            // 设置一个透明的背景，不然无法实现点击弹框外，弹框消失
            //popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popupwindow));
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            // 设置点击弹框外部，弹框消失
            mPopupWindow.setOutsideTouchable(true);
            // 设置焦点
            mPopupWindow.setFocusable(true);
            // 设置所在布局
            // x和y的单位是像素
            mPopupWindow.showAtLocation(popupwindowView, Gravity.BOTTOM, 0, 15);
        }
        //取消
        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
            }
        });
        //银联
        //        yinLianTV.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //
        //                if (mPopupWindow != null && mPopupWindow.isShowing()) {
        //                    mPopupWindow.dismiss();
        //                }
        //            }
        //        });
        //支付宝
        aliPayTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                if(!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(),"请开启网络");
                    return;
                }else {
                    requestGetOrderAndPay("2");
                }
            }
        });
        //微信
        weixinTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                if(!ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext())) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(),"请开启网络");
                    return;
                }else {
                    // 检查微信版本是否支持支付
                    boolean isPaySupported = msgApi.getWXAppSupportAPI() >= com.tencent.mm.sdk.constants.Build.PAY_SUPPORTED_SDK_INT;
                    if (!isPaySupported) {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(),"您的微信版本过低,不支持支付,请下载最新版本!");
                        return;
                    }
                    requestGetOrderAndPay("3");
                }
            }
        });
    }

    private void requestGetOrderAndPay(String payType) {
        //请求充值接口生成订单
        //得到money
        String upTomoney = ct_recharge_money.getText().toString().trim();
        //得到content
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", mUserId);
            json.put("count", upTomoney);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonString = json.toString();
        String content = Base64Utils.encode(jsonString.getBytes());
        //签名  count=1&user_id=1yuyubuyer
        String signaturestr = "yuyubuyer" + "count=" + upTomoney + "&user_id=" + mUserId;
        String signature = MD5.getMessageDigest(signaturestr.getBytes());
        if (!TextUtils.isEmpty(jsonString)) {
            //type:1:union 2:alipay 3:wechat
            requestTopUpInterface(signature, content, payType);
        }
    }

    /**
     * 请求充值接口生成订单
     * signature MD5签名
     * content  base64处理
     * 目的得到支付的订单id，支付时需要使用
     */
    private void requestTopUpInterface(String signature, String content, String type) {
        final String tempType = type;

        OkHttpUtils.post().url(topUpUrl).addParams("signature", signature)
                .addParams("content", content).build().execute(new StringCallback() {
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
                        //解析得到订单的id
                        Map<String, String> contentMap = ContentJsonParse.contentJsonParse(response);
                        if (contentMap != null) {
                            //每次请求充值的订单号
                            String order_no = contentMap.get("order_no");
                            if (!TextUtils.isEmpty(order_no)) {
                                if ("2".equals(tempType)) {
                                    //发送支付宝支付的请求
                                    requestPay(tempType, order_no);
                                } else if ("3".equals(tempType)) {
                                    //发送微信支付的请求
                                    requestPay(tempType, order_no);
                                }
                            }
                        } else {
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "充值失败");
                        }
                    } else {
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "充值失败");
                    }
                } catch (Exception e) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                }
            }
        });
    }

    /**
     * 请求支付
     */
    private void requestPay(String payType, String order_no) {
        if ("3".equals(payType)) {
            // 微信支付提示框
            dialog = ProgressDialog.show(RechargeActivity.this,
                    getString(R.string.app_tip),
                    getString(R.string.getting_prepayid));
            dialog.show();
        }

        final String tempPayType = payType;

        final String tempOrderNo = order_no;
        //得到content
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", mUserId);
            json.put("order_no", order_no);
            json.put("type", payType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonString = json.toString();
        String content = Base64Utils.encode(jsonString.getBytes());
        //签名  yuyubuyercount=1&user_id=1yuyubuyer
        String signaturestr = "yuyubuyer" + "order_no=" + order_no + "&type=" + payType + "&user_id=" + mUserId;
        String signature = MD5.getMessageDigest(signaturestr.getBytes());

        OkHttpUtils.post().url(payUrl).addParams("signature", signature)
                .addParams("content", content).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                //关闭提示框
                if (dialog != null) {
                    dialog.dismiss();
                }
                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    // 成功了，只是返回来请求的json数据,还需要解析
                    Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                    String response_code = map.get("response_code");
                    if ("0".equals(response_code)) {
                        if ("2".equals(tempPayType)) {
                            //支付宝
                            Map<String, String> aliPayInfoMap = MyObjeceParse.getAlipayParse(response);

                            if(aliPayInfoMap != null && aliPayInfoMap.size() > 0) {
                                //向支付宝请求
                                requestAliPay(aliPayInfoMap,tempOrderNo);
                            }
                        }else if("3".equals(tempPayType)) {
                            //微信
                            Map<String, String> weixinInfoMap = MyObjeceParse.getWeiXinParse(response);
                            if(weixinInfoMap != null && weixinInfoMap.size() > 0) {
                                //向微信请求
                                requestWeiXinPay(weixinInfoMap);
                            }
                        }
                    } else {
                        //关闭提示框
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        ToastUtils.showToastInUIThread(UIUtils.getContext(), "充值失败");
                    }
                } catch (Exception e) {
                    //关闭提示框
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                }
            }
        });
    }

    /**
     * 支付宝支付
     */
    private void requestAliPay(Map<String, String> infoMap,String tempOrderNo) {

        String finalMoney = ct_recharge_money.getText().toString().trim();

        //得到后台的签名
       String sign = infoMap.get("sign");

        //得到回调地址
        String url = infoMap.get("url");

        //得到订单参数
        String orderInfo = infoMap.get("param");

        String out_trade_no = infoMap.get("out_trade_no");

//        String orderInfo = getOrderInfo("鱼鱼充值","鱼鱼充值", finalMoney,out_trade_no,url);


        //partner="2088221980368618"&seller_id="yuyubuyer@qq.com"&out_trade_no="RC201611052247279387480"&subject="鱼鱼充值"&body="鱼鱼充值"&total_fee="0.01"&notify_url="http://120.25.86.253/payserver/alipay/callback"&service="mobile.securitypay.pay"&payment_type="1"&_input_charset="utf-8"&it_b_pay="30m"
        /**
         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
         */
//       String sign = sign(orderInfo);
       try {
           /**
            * 仅需对sign 做URL编码
            * &_input_charset="utf-8"&body="鱼鱼充值"&it_b_pay="30m"&notify_url="http://120.25.86.253/payserver/alipay/callback"&out_trade_no="RC201611052247279387480"&payment_type="1"&service="mobile.securitypay.pay"&subject="鱼鱼充值"&total_fee="0.01"&seller_id="yuyubuyer@qq.com"&out_trade_no="RC201611052247279387480"&subject="鱼鱼充值"&body="鱼鱼充值"&total_fee="0.01"&notify_url="http://120.25.86.253/payserver/alipay/callback"&service="mobile.securitypay.pay"&payment_type="1"&_input_charset="utf-8"&it_b_pay="30m"
            */
           sign = URLEncoder.encode(sign, "UTF-8");
       } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
       }

        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        final String payInfo =  orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(RechargeActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }
    /**
     * 向微信请求支付
     *  从后台得到的微信支付的参数weixinMap
     */
    private void requestWeiXinPay(Map<String, String> weixinMap) {
        try{
            // 得到appId
            req.appId = weixinMap.get("appid").toString();
            // 得到partnerid
            req.partnerId = weixinMap.get("partnerid").toString();
            // 得到prepayid
            req.prepayId = weixinMap.get("prepayid").toString();
            // 得到packageValue
            req.packageValue = "Sign=WXPay";
            // 得到nonceStr
            req.nonceStr = weixinMap.get("noncestr").toString();
            // 得到timeStamp
            req.timeStamp = weixinMap.get("timestamp").toString();
            // 得到sign
            req.sign = weixinMap.get("sign").toString();
            // 3.调用微信支付接口
            msgApi.registerApp(req.appId);
            msgApi.sendReq(req);
        }catch (Exception e) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(),"充值失败");
        }
        //关闭提示框
        if (dialog != null) {
            dialog.dismiss();
        }
        //清空金额
        //ct_recharge_money.setText("");
        //加入到管理模式
        BaseApplication.getInstance().addActivity(RechargeActivity.this);
    }


    /**
     * create the order info. 创建订单信息
     *
     */
    private String getOrderInfo(String subject, String body, String price,String out_trade_no,String url) {

//        String orderInfo = "_input_charset=\"utf-8\"";
//        orderInfo += "&body=" + "\"" + body + "\"";
//        orderInfo += "&it_b_pay=\"30m\"";
//        orderInfo += "&notify_url=" + "\"" + "http://120.25.86.253/payserver/alipay/callback" + "\"";
//        orderInfo += "&out_trade_no=" + "\"" + out_trade_no + "\"";
//        orderInfo += "&partner=" + "\"" + Constants.PARTNER + "\"";
//        orderInfo += "&payment_type=\"1\"";
//        orderInfo += "&seller_id=" + "\"" + Constants.SELLER + "\"";
//        orderInfo += "&service=\"mobile.securitypay.pay\"";
//        orderInfo += "&subject=" + "\"" + subject + "\"";
//        orderInfo += "&total_fee=" + "\"" + price + "\"";
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + Constants.PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + Constants.SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + out_trade_no + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + url + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
       // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     *
     */
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content
     *            待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, Constants.RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }
}
