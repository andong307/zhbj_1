package com.itheima41.zhbj.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author andong
 * ����Fragment�Ļ���.
 */
public abstract class BaseFragment extends Fragment {

	public Activity mActivity; // �����Ķ���.

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
	}

	/**
	 * ���ص�View����Ϊ��ǰ��Fragment����ʾ
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
		
		// ���ó�ʼ�����ݵķ���
		initData();
	}

	public abstract View initView();

	/**
	 * ����ȥ���Ǵ˷���, ʵ���Լ������ݳ�ʼ��.
	 */
	public void initData() {
		
	}
}
