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
 * ����Fragment
 */
public class ContentFragment extends BaseFragment implements OnCheckedChangeListener {
	
	@ViewInject(R.id.nsvp_content_fragment)
	private NoScrollViewPager mViewPager;
	
	@ViewInject(R.id.rg_content_fragment)
	private RadioGroup mRadioGroup;
	
	private List<TabBasePager> pagerList; //ViewPager������: ����ҳ��

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.content_fragment, null);
		ViewUtils.inject(this, view); // �ѵ�ǰView����ע�뵽XUtils�����.
		return view;
	}
	
	@Override
	public void initData() {
		// ��ʼ��ViewPager������.
		pagerList = new ArrayList<TabBasePager>();
		pagerList.add(new HomePager(mActivity));
		pagerList.add(new NewsCenterPager(mActivity));
		pagerList.add(new SmartServicePager(mActivity));
		pagerList.add(new GovaffairsPager(mActivity));
		pagerList.add(new SettingsPager(mActivity));
		
		// ����������
		ContentAdapter mAdapter = new ContentAdapter();
		mViewPager.setAdapter(mAdapter);
		
		mRadioGroup.setOnCheckedChangeListener(this);
		// ����ҳ��RadioButtonѡ��
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
			System.out.println("Ԥ����: " + position);
			TabBasePager tabBasePager = pagerList.get(position);
			View view = tabBasePager.getRootView();
			container.addView(view);
			tabBasePager.initData(); // ��ʼ��ҳ�������.
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

	/**
	 * ��RadioGroup�е�RadioButtonѡ��״̬�ı�ʱ�����˷���.
	 * @param group
	 * @param checkedId ��ѡ�е�RadioButton��id
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rb_content_fragment_home:
			// ��ǰѡ�е�����ҳ
			mViewPager.setCurrentItem(0);
			// ��SlidingMenu�໬�˵�������
			isEnableMenu(false);
			break;
		case R.id.rb_content_fragment_newscenter:
			// ��ǰѡ�е�����������
			mViewPager.setCurrentItem(1);
			isEnableMenu(true);
			break;
		case R.id.rb_content_fragment_smartservice:
			// ��ǰѡ�е����ǻ۷���
			mViewPager.setCurrentItem(2);
			isEnableMenu(true);
			break;
		case R.id.rb_content_fragment_govaffairs:
			// ��ǰѡ�е�������
			mViewPager.setCurrentItem(3);
			isEnableMenu(true);
			break;
		case R.id.rb_content_fragment_settings:
			// ��ǰѡ�е�������
			mViewPager.setCurrentItem(4);
			// ��SlidingMenu�໬�˵�������
			isEnableMenu(false);
			break;
		default:
			break;
		}
	}

	/**
	 * �Ƿ����ò˵�
	 * @param flag true ����, false ����
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
	 * ��ȡ��������ҳ��
	 * @return
	 */
	public NewsCenterPager getNewsCenterPager() {
		return (NewsCenterPager) pagerList.get(1);
	}
}
