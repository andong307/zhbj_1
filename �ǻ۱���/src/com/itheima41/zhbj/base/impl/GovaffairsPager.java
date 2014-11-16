package com.itheima41.zhbj.base.impl;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.itheima41.zhbj.base.TabBasePager;

/**
 * @author andong
 * 政务页面
 */
public class GovaffairsPager extends TabBasePager {

	public GovaffairsPager(Context context) {
		super(context);
	}

	@Override
	public void initData() {
		tvTitle.setText("人口管理");
		ibMenu.setVisibility(View.VISIBLE);
		
		TextView tv = new TextView(mContext);
		tv.setText("政务的内容");
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(Color.RED);
		tv.setTextSize(25);
		flContent.addView(tv);
	}
}
