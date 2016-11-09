package com.wushuikeji.www.yuyubuyer.bean;

/**
 * @author Jack_chentao
 * @time 2016/9/28 0028 下午 8:59.
 * @des ${TODO}
 */
public class OrderBean {
    //图片的url
    public String imgUrl;
    //用户的名字
    public String userName;
    //交易的时间
    public String time;
    //交易的类型
    public String type;
    //交易额具体时间
    public String tradeTime;
    //价格
    public String tradeMoney;

    @Override
    public String toString() {
        return "OrderBean{" +
                "imgUrl='" + imgUrl + '\'' +
                ", userName='" + userName + '\'' +
                ", time='" + time + '\'' +
                ", type='" + type + '\'' +
                ", tradeTime='" + tradeTime + '\'' +
                ", tradeMoney='" + tradeMoney + '\'' +
                '}';
    }
}
