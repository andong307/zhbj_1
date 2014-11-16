package com.itheima41.zhbj.fragment;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.itheima41.zhbj.R;
import com.itheima41.zhbj.base.BaseFragment;
import com.itheima41.zhbj.base.TabBasePager;
import com.itheima41.zhbj.base.impl.GovaffairsPager;
import com.itheima41.zhbj.base.impl.HomePager;
import com.itheima41.zhbj.base.impl.NewsCenterPager;
import com.itheima41.zhbj.base.impl.SettingsPager;
import com.itheima41.zhbj.base.impl.SmartServicePager;
import com.itheima41.zhbj.view.NoScrollViewPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * @author andong
 * 正文Fragment
 */
public class ContentFragment extends BaseFragment implements OnCheckedChangeListener {
	
	@ViewInject(R.id.nsvp_content_fragment)
	private NoScrollViewPager mViewPager;
	
	@ViewInject(R.id.rg_content_fragment)
	private RadioGroup mRadioGroup;
	
	private List<TabBasePager> pagerList; //ViewPager的数据: 各个页面

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.content_fragment, null);
		ViewUtils.inject(this, view); // 把当前View对象注入到XUtils框架中.
		return view;
	}
	
	@Override
	public void initData() {
		// 初始化ViewPager的数据.
		pagerList = new ArrayList<TabBasePager>();
		pagerList.add(new HomePager(mActivity));
		pagerList.add(new NewsCenterPager(mActivity));
		pagerList.add(new SmartServicePager(mActivity));
		pagerList.add(new GovaffairsPager(mActivity));
		pagerList.add(new SettingsPager(mActivity));
		
		// 设置适配器
		ContentAdapter mAdapter = new ContentAdapter();
		mViewPager.setAdapter(mAdapter);
		
		mRadioGroup.setOnCheckedChangeListener(this);
		// 把首页的RadioButton选中
		mRadioGroup.check(R.id.rb_content_fragment_home);
	}
	
	class ContentAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return pagerList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			System.out.println("预加载: " + position);
			TabBasePager tabBasePager = pagerList.get(position);
			View view = tabBasePager.getRootView();
			container.addView(view);
			tabBasePager.initData(); // 初始化页面的数据.
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

	/**
	 * 当RadioGroup中的RadioButton选中状态改变时触发此方法.
	 * @param group
	 * @param checkedId 被选中的RadioButton的id
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rb_content_fragment_home:
			// 当前选中的是首页
			mViewPager.setCurrentItem(0);
			// 把SlidingMenu侧滑菜单给屏蔽
			isEnableMenu(false);
			break;
		case R.id.rb_content_fragment_newscenter:
			// 当前选中的是新闻中心
			mViewPager.setCurrentItem(1);
			isEnableMenu(true);
			break;
		case R.id.rb_content_fragment_smartservice:
			// 当前选中的是智慧服务
			mViewPager.setCurrentItem(2);
			isEnableMenu(true);
			break;
		case R.id.rb_content_fragment_govaffairs:
			// 当前选中的是政务
			mViewPager.setCurrentItem(3);
			isEnableMenu(true);
			break;
		case R.id.rb_content_fragment_settings:
			// 当前选中的是设置
			mViewPager.setCurrentItem(4);
			// 把SlidingMenu侧滑菜单给屏蔽
			isEnableMenu(false);
			break;
		default:
			break;
		}
	}

	/**
	 * 是否启用菜单
	 * @param flag true 启用, false 屏蔽
	 */
	private void isEnableMenu(boolean flag) {
		SlidingFragmentActivity context = (SlidingFragmentActivity) mActivity;
		SlidingMenu slidingMenu = context.getSlidingMenu();
		if(flag) {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
	}
	
	/**
	 * 获取新闻中心页面
	 * @return
	 */
	public NewsCenterPager getNewsCenterPager() {
		return (NewsCenterPager) pagerList.get(1);
	}
}
