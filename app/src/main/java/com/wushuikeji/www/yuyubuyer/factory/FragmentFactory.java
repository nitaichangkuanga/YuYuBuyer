package com.wushuikeji.www.yuyubuyer.factory;

import android.os.Bundle;

import com.wushuikeji.www.yuyubuyer.base.BaseFragment;
import com.wushuikeji.www.yuyubuyer.fragment.BuyerFragment;
import com.wushuikeji.www.yuyubuyer.fragment.NewsFragment;
import com.wushuikeji.www.yuyubuyer.fragment.OrdersFragment;
import com.wushuikeji.www.yuyubuyer.fragment.PersonalFragment;
import com.wushuikeji.www.yuyubuyer.fragment.ShopFragment;

/**
 * @des	Fragment工厂类
 */
public class FragmentFactory {

	public static final int	FRAGMENT_SHOP = 0;
	public static final int	FRAGMENT_BUYER = 1;
	public static final int	FRAGMENT_NEWS = 2;
	public static final int	FRAGMENT_ORDERS = 3;
	public static final int	FRAGMENT_PERSONAL = 4;

//	public static SparseArrayCompat<BaseFragment> cachesFragment = new SparseArrayCompat<BaseFragment>();

	public static BaseFragment getFragment(int position,String cityName,String businessName,String area_id) {

		BaseFragment fragment = null;
		Bundle bundle = null;

		// 如果缓存里面有对应的fragment,就直接取出返回
//		BaseFragment tmpFragment = cachesFragment.get(position);
//		if (tmpFragment != null) {
//			fragment = tmpFragment;
//			return fragment;
//		}
		switch (position) {
		case FRAGMENT_SHOP:// 商铺
			fragment = new ShopFragment();
			bundle = new Bundle();
			bundle.putString("cityName",cityName);
			bundle.putString("businessName",businessName);
			bundle.putString("area_id",area_id);
			fragment.setArguments(bundle);
			break;
		case FRAGMENT_BUYER:// BUYER
			fragment = new BuyerFragment();
			bundle = new Bundle();
			bundle.putString("cityName",cityName);
			bundle.putString("businessName",businessName);
			bundle.putString("area_id",area_id);
			fragment.setArguments(bundle);
			break;
		case FRAGMENT_NEWS:// 消息
			fragment = new NewsFragment();
			break;
		case FRAGMENT_ORDERS:// 订单
			fragment = new OrdersFragment();
			break;
		case FRAGMENT_PERSONAL:// 个人
			fragment = new PersonalFragment();
			break;
		default:
			break;
		}
		 //保存对应的fragment
//		cachesFragment.put(position, fragment);
		return fragment;
	}
}
