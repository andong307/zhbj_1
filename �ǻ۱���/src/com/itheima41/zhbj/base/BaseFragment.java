package com.itheima41.zhbj.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author andong
 * 所有Fragment的基类.
 */
public abstract class BaseFragment extends Fragment {

	public Activity mActivity; // 上下文对象.

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
	}

	/**
	 * 返回的View会作为当前的Fragment来显示
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = initView();
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// 调用初始化数据的方法
		initData();
	}

	public abstract View initView();

	/**
	 * 子类去覆盖此方法, 实现自己的数据初始化.
	 */
	public void initData() {
		
	}
}
