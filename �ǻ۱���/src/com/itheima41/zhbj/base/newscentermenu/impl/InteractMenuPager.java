package com.itheima41.zhbj.base.newscentermenu.impl;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.itheima41.zhbj.base.NewsCenterMenuBasePager;

/**
 * @author andong
 * �����˵�ҳ��
 */
public class InteractMenuPager extends NewsCenterMenuBasePager {

	public InteractMenuPager(Context context) {
		super(context);
	}

	@Override
	public View initView() {
		TextView tv = new TextView(mContext);
		tv.setText("�����˵�");
		tv.setTextSize(23);
		tv.setTextColor(Color.RED);
		tv.setGravity(Gravity.CENTER); // ָ���Լ��������Ǿ�����ʾ
		return tv;
	}

}
