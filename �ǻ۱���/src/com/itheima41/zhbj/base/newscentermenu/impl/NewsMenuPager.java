package com.itheima41.zhbj.base.newscentermenu.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.itheima41.zhbj.MainUI;
import com.itheima41.zhbj.R;
import com.itheima41.zhbj.base.NewsCenterMenuBasePager;
import com.itheima41.zhbj.domain.NewsCenterMenuBean.NewsCenterMenu;
import com.itheima41.zhbj.domain.NewsCenterMenuBean.NewsCenterTabBean;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.viewpagerindicator.TabPageIndicator;

/**
 * @author andong
 * 新闻菜单页面
 */
public class NewsMenuPager extends NewsCenterMenuBasePager implements OnPageChangeListener {
	
	@ViewInject(R.id.tpi_news_menu)
	private TabPageIndicator mIndicator;

	@ViewInject(R.id.vp_news_menu)
	private ViewPager mViewPager;

	private List<NewsCenterTabBean> tabBeanList; // 页签的数据.
	private List<NewsMenuTabDetailPager> tabDetailPagerList; // 页签对应的页面
	
	public NewsMenuPager(Context context) {
		super(context);
	}

	public NewsMenuPager(Context context, NewsCenterMenu newsCenterMenu) {
		super(context);

		tabBeanList = newsCenterMenu.children;
	}

	@Override
	public View initView() {
		View view = View.inflate(mContext, R.layout.news_menu, null);
		ViewUtils.inject(this, view); // 把当前布局注入到xUtils框架中
		return view;
	}

	@Override
	public void initData() {
		// 初始化ViewPager数据.
		tabDetailPagerList = new ArrayList<NewsMenuTabDetailPager>();
		for (int i = 0; i < tabBeanList.size(); i++) {
			tabDetailPagerList.add(new NewsMenuTabDetailPager(mContext, tabBeanList.get(i)));
		}
		
		NewsMenuAdapter mAdapter = new NewsMenuAdapter();
		mViewPager.setAdapter(mAdapter);
		// 把ViewPager和页签关联.
		mIndicator.setViewPager(mViewPager);
		
		// 监听页面的改变
		mIndicator.setOnPageChangeListener(this);
	}
	
	class NewsMenuAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return tabDetailPagerList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		/**
		 * 返回的String会作为TabPageIndicator的页签数据来展示, 并且和对应position位置的页面想匹配
		 */
		@Override
		public CharSequence getPageTitle(int position) {
			return tabBeanList.get(position).title;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			NewsMenuTabDetailPager pager = tabDetailPagerList.get(position);
			View view = pager.getRootView();
			container.addView(view);
			pager.initData();
			return view;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int position) {
		// 如果position的位置是0, 菜单置为可用.
		if(position == 0) {
			((MainUI) mContext).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			((MainUI) mContext).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
	}
	
	@OnClick(R.id.ib_news_menu_next_tab)
	public void nextTab(View v) {
		mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
	}
}
