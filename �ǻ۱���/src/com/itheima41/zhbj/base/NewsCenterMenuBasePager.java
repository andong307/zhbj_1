package com.itheima41.zhbj.base;

import android.content.Context;
import android.view.View;

public abstract class NewsCenterMenuBasePager {
	
	public Context mContext;
	private View rootView;

	public NewsCenterMenuBasePager(Context context) {
		this.mContext = context;
		
		rootView = initView();
	}

	public abstract View initView();
	
	public View getRootView() {
		return rootView;
	}
	
	/**
	 * ��ʼ������, ������Ҫ���Ǵ˷���, ȥʵ���Լ������ݳ�ʼ��
	 */
	public void initData() {
		
	}
}
