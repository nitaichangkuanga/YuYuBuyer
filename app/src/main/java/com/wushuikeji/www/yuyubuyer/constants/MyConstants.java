package com.wushuikeji.www.yuyubuyer.constants;

public interface MyConstants
{
	String SPNAME = "cachevalue";//sp的文件名
	String ISGUIDE = "isguide";//是否进入过向导界面
	String FRAGMENTINDEX = "fragmentIndex";//保存fragmentIndex
	int REQUESTCODE = 0;//请求码
	String USERNAME = "userName";//个人里的用户昵称
	String BUYERID = "BUYERID";//个人里的用户YUYUID
	String ISLOGINSTATUS = "isLoginStatus";//登录的状态
	String LOGINSPNAME = "loginSpName";//单独保存登录的sp名字，好用户退出登录删除
	String LATITUDE = "latitude";//纬度
	String LONGITUDE = "longitude";//经度
	String SHOPSPNAME = "shopSpName";//保存商铺的sp名字，好用户重新选择商圈时删除
	String SHOPFRAGMENTJSON = "shopFragmentJSON";//shopFragment的数据缓存
	String BUYERSPNAME = "buyerSpName";//保存商铺的sp名字，好用户重新选择商圈时删除
	String BUYERFRAGMENTJSON = "buyerFragmentJSON";//buyerFragment的数据缓存
	String FOOTVIEWNAME = "暂无数据";//用于删除footView
	String LOGIN = "请先登录";//提示登录
	String BUYERDETAILSATTENTIONSPNAME = "buyerDetailsAttentionSpName";//保存buyer详情里的是否关注的sp名字(清理缓存时不删除)
	String ISATTENTION = "isAttention";//是否关注过
	String SETTINGSPNAME = "settingSpName";//单独保存是否设置用户信息的sp名字,因为清除缓存也不清除
	String ISSETTINGUSERINFO = "isSettingUserInfo";
	String USERPHONESPNAME = "userPhoneSpName";//单独保存用户的手机号和密码个数，用于账户安全显示,因为清除缓存也不清除
	String USERPHONE = "userPhone";
	String USERPASSWORDCOUNT = "userPasswordCount";
	String REALUSERSPNAME = "realUserSpName";//在用户实名认证的时候，单独保存用户的姓名，用于查看用户的信息时展示，因为后台没提供，清除缓存也不清除
	String REALUSER = "realUser";
	String PERSONALINFO = "PersonalInfo";//个人碎片中的等级和好友等信息的缓存
	String IMUSERPHONE = "imUserPhone";
	String IMUSERPASSWORD = "imUserPassword";
	String HEADIMGURL = "headImgUrl";//自己的头像地址
}
