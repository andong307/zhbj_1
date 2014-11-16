package com.itheima41.zhbj.base.impl;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.itheima41.zhbj.base.TabBasePager;

/**
 * @author andong
 * ��ҳҳ��
 */
public class HomePager extends TabBasePager {

	public HomePager(Context context) {
		super(context);
	}

	@Override
	public void initData() {
		tvTitle.setText("�ǻ۱���");
		ibMenu.setVisibility(View.GONE);
		
		TextView tv = new TextView(mContext);
		tv.setText("��ҳ������");
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(Color.RED);
		tv.setTextSize(25);
		flContent.addView(tv);
	}
}
