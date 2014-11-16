package com.itheima41.zhbj.base;

import com.itheima41.zhbj.MainUI;
import com.itheima41.zhbj.R;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * @author andong
 * 首页页签对应内容的基类
 */
public class TabBasePager implements OnClickListener {
	
	public Context mContext;
	private View rootView; // 当前View对象
	public TextView tvTitle; // 标题
	public ImageButton ibMenu; // 菜单
	public FrameLayout flContent; // 内容区域

	public TabBasePager(Context context) {
		this.mContext = context;
		
		rootView = initView();
	}

	/**
	 * 初始化布局
	 * @return
	 */
	private View initView() {
		View view = View.inflate(mContext, R.layout.tab_base_pager, null);
		flContent = (FrameLayout) view.findViewById(R.id.fl_tab_base_pager_content);
		tvTitle = (TextView) view.findViewById(R.id.tv_title_bar_title);
		ibMenu = (ImageButton) view.findViewById(R.id.ib_title_bar_menu);
		ibMenu.setOnClickListener(this);
		return view;
	}
	
	/**
	 * 获得当前页面的布局对象
	 * @return
	 */
	public View getRootView() {
		return rootView;
	}
	
	/**
	 * 子类需要覆盖此方法, 实现自己的初始化数据逻辑
	 */
	public void initData() {
		
	}

	@Override
	public void onClick(View v) {
		MainUI mainUI = ((MainUI) mContext);
		mainUI.getSlidingMenu().toggle();// 控制SlidingMenu菜单是打开还是关闭
	}
}
