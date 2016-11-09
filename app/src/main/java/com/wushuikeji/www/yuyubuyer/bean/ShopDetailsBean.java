package com.wushuikeji.www.yuyubuyer.bean;

/**
 * @author Jack_chentao
 * @time 2016/9/27 0027 上午 10:34.
 * @des ${TODO}
 */
public class ShopDetailsBean {
    //商品的id
    public String productId;
    //图片的url
    public String imgUrl;
    //商品的标题
    public String shopDetailsTitle;
    //商品的价格
    public String money;
    //折扣
    public String discount;
    //商品的类型
    public String goodType;

    @Override
    public String toString() {
        return "ShopDetailsBean{" +
                "productId='" + productId + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", shopDetailsTitle='" + shopDetailsTitle + '\'' +
                ", money='" + money + '\'' +
                ", discount='" + discount + '\'' +
                ", goodType='" + goodType + '\'' +
                '}';
    }
}
