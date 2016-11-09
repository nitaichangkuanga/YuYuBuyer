package com.wushuikeji.www.yuyubuyer.utils;

/**
 * @author Jack_chentao
 * @time 2016/10/12 0012 下午 5:05.
 * @des ${TODO}
 */
public class RequestResponseUtils {

    public static String getResponseContent(String response_code) {
        if ("0".equals(response_code)) {
            return "成功";
        } else if ("400".equals(response_code)) {
            return "暂无数据";
        } else if ("800".equals(response_code)) {
            return "账户错误";
        } else if ("1000".equals(response_code)) {
            return "用户名或密码错误";
        } else if ("1001".equals(response_code)) {
            return "手机号码已经被注册";
        } else if ("1002".equals(response_code)) {
            return "验证码与手机不匹配";
        } else if ("1003".equals(response_code)) {
            return "注册失败";
        } else if ("1004".equals(response_code)) {
            return "系统中没有该手机号码";
        } else if ("1100".equals(response_code)) {
            return "账户余额不足";
        } else if ("1200".equals(response_code)) {
            return "身份认证失败";
        } else if ("1201".equals(response_code)) {
            return "该账户已经被认证";
        } else if ("1301".equals(response_code)) {
            return "该订单已经申请了退款";
        } else if ("4000".equals(response_code)) {
            return "手机绑定失败";
        } else if ("4001".equals(response_code)) {
            return "修改密码失败";
        } else if ("4003".equals(response_code)) {
            return "商品添加失败";
        } else if ("4004".equals(response_code)) {
            return "商品更新失败";
        } else if ("4005".equals(response_code)) {
            return "验证码发送失败";
        } else if ("4006".equals(response_code)) {
            return "验证码错误";
        } else if ("4007".equals(response_code)) {
            return "验证码过期";
        } else if ("4008".equals(response_code)) {
            return "手机号码已经注册过";
        } else if ("4009".equals(response_code)) {
            return "手机绑定失败";
        } else if ("4010".equals(response_code)) {
            return "修改密码—旧密码输入错误";
        } else if ("4100".equals(response_code)) {
            return "收藏失败";
        } else if ("4101".equals(response_code)) {
            return "取消收藏成功";
        } else if ("4102".equals(response_code)) {
            return "取消收藏失败";
        } else if ("4103".equals(response_code)) {
            return "商铺评价失败";
        } else if ("4200".equals(response_code)) {
            return "银行卡添加失败";
        } else if ("4201".equals(response_code)) {
            return "银行卡已经存在";
        } else if ("4300".equals(response_code)) {
            return "已经达到每天发布动态的上限";
        } else if ("4301".equals(response_code)) {
            return "已经达到最大发布动态的上限";
        }
        return null;
    }
}
