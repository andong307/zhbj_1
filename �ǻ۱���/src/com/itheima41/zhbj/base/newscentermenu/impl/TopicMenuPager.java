package com.itheima41.zhbj.base.newscentermenu.impl;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.itheima41.zhbj.base.NewsCenterMenuBasePager;

/**
 * @author andong
 * ר��˵�ҳ��
 */
public class TopicMenuPager extends NewsCenterMenuBasePager {

	public TopicMenuPager(Context context) {
		super(context);
	}

	@Override
	public View initView() {
		TextView tv = new TextView(mContext);
		tv.setText("ר��˵�");
		tv.setTextSize(23);
		tv.setTextColor(Color.RED);
		tv.setGravity(Gravity.CENTER); // ָ���Լ��������Ǿ�����ʾ
		return tv;
	}

}
