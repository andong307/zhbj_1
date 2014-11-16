package com.itheima41.zhbj;

import com.itheima41.zhbj.fragment.ContentFragment;
import com.itheima41.zhbj.fragment.LeftMenuFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

public class MainUI extends SlidingFragmentActivity {

	// 主界面Fragment的标识
	private final String CONTENT_FRAGMENT_TAG = "content";
	// 左侧菜单Fragment的标识
	private final String LEFT_MENU_FRAGMENT_TAG = "left_menu";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_content); // 设置主界面布局
		setBehindContentView(R.layout.main_left_menu); // 设置左侧菜单布局.
		
		SlidingMenu mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setMode(SlidingMenu.LEFT); // 设置只有左侧菜单可以滑出来.
		// 设置整个屏幕都可以滑动出菜单
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		mSlidingMenu.setBehindOffset(200); // 在滑动时, 设置主界面可以留在屏幕上200个像素
		
		initFragment();
	}

	/**
	 * 初始化并替换菜单和主界面的Fragment
	 */
	private void initFragment() {
		// 获得一个Fragment管理器对象.
		FragmentManager fm = getSupportFragmentManager();
		
		// 开启事物
		FragmentTransaction ft = fm.beginTransaction();
		
		// 替换
		// 替换主界面(正文)的Fragment
		ft.replace(R.id.fl_main_content, new ContentFragment(), CONTENT_FRAGMENT_TAG);
		// 替换左侧菜单的Fragment
		ft.replace(R.id.fl_main_left_menu, new LeftMenuFragment(), LEFT_MENU_FRAGMENT_TAG);
		
		// 提交
		ft.commit();
	}
	
	/**
	 * 获取主页面的菜单Fragment
	 * @return
	 */
	public LeftMenuFragment getLeftMenuFragment() {
		FragmentManager fm = getSupportFragmentManager();
		LeftMenuFragment leftMenuFragment = (LeftMenuFragment) fm.findFragmentByTag(LEFT_MENU_FRAGMENT_TAG);
		return leftMenuFragment;
	}
	
	/**
	 * alt + shift + J 加注释
	 * 获取主界面的正文fragment
	 * @return
	 */
	public ContentFragment getContentFragment() {
		FragmentManager fm = getSupportFragmentManager();
		ContentFragment contentFragment = (ContentFragment) fm.findFragmentByTag(CONTENT_FRAGMENT_TAG);
		return contentFragment;
	}
}
