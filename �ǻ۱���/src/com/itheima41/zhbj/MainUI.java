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

	// ������Fragment�ı�ʶ
	private final String CONTENT_FRAGMENT_TAG = "content";
	// ���˵�Fragment�ı�ʶ
	private final String LEFT_MENU_FRAGMENT_TAG = "left_menu";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_content); // ���������沼��
		setBehindContentView(R.layout.main_left_menu); // �������˵�����.
		
		SlidingMenu mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setMode(SlidingMenu.LEFT); // ����ֻ�����˵����Ի�����.
		// ����������Ļ�����Ի������˵�
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		mSlidingMenu.setBehindOffset(200); // �ڻ���ʱ, �������������������Ļ��200������
		
		initFragment();
	}

	/**
	 * ��ʼ�����滻�˵����������Fragment
	 */
	private void initFragment() {
		// ���һ��Fragment����������.
		FragmentManager fm = getSupportFragmentManager();
		
		// ��������
		FragmentTransaction ft = fm.beginTransaction();
		
		// �滻
		// �滻������(����)��Fragment
		ft.replace(R.id.fl_main_content, new ContentFragment(), CONTENT_FRAGMENT_TAG);
		// �滻���˵���Fragment
		ft.replace(R.id.fl_main_left_menu, new LeftMenuFragment(), LEFT_MENU_FRAGMENT_TAG);
		
		// �ύ
		ft.commit();
	}
	
	/**
	 * ��ȡ��ҳ��Ĳ˵�Fragment
	 * @return
	 */
	public LeftMenuFragment getLeftMenuFragment() {
		FragmentManager fm = getSupportFragmentManager();
		LeftMenuFragment leftMenuFragment = (LeftMenuFragment) fm.findFragmentByTag(LEFT_MENU_FRAGMENT_TAG);
		return leftMenuFragment;
	}
	
	/**
	 * alt + shift + J ��ע��
	 * ��ȡ�����������fragment
	 * @return
	 */
	public ContentFragment getContentFragment() {
		FragmentManager fm = getSupportFragmentManager();
		ContentFragment contentFragment = (ContentFragment) fm.findFragmentByTag(CONTENT_FRAGMENT_TAG);
		return contentFragment;
	}
}
