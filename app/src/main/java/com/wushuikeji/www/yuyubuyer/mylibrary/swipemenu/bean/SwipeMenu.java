package com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.bean;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class SwipeMenu {

	private Context mContext;
	private List<com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.bean.SwipeMenuItem> mItems;
	private int mViewType;

	public SwipeMenu(Context context) {
		mContext = context;
		mItems = new ArrayList<com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.bean.SwipeMenuItem>();
	}

	public Context getContext() {
		return mContext;
	}

	public void addMenuItem(com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.bean.SwipeMenuItem item) {
		mItems.add(item);
	}

	public void removeMenuItem(com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.bean.SwipeMenuItem item) {
		mItems.remove(item);
	}

	public List<com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.bean.SwipeMenuItem> getMenuItems() {
		return mItems;
	}

	public com.wushuikeji.www.yuyubuyer.mylibrary.swipemenu.bean.SwipeMenuItem getMenuItem(int index) {
		return mItems.get(index);
	}

	public int getViewType() {
		return mViewType;
	}

	public void setViewType(int viewType) {
		this.mViewType = viewType;
	}

}
