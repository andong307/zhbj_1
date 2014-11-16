package com.itheima41.zhbj.base.newscentermenu.impl;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.itheima41.zhbj.base.NewsCenterMenuBasePager;

/**
 * @author andong
 * 专题菜单页面
 */
public class TopicMenuPager extends NewsCenterMenuBasePager {

	public TopicMenuPager(Context context) {
		super(context);
	}

	@Override
	public View initView() {
		TextView tv = new TextView(mContext);
		tv.setText("专题菜单");
		tv.setTextSize(23);
		tv.setTextColor(Color.RED);
		tv.setGravity(Gravity.CENTER); // 指定自己的内容是居中显示
		return tv;
	}

}
