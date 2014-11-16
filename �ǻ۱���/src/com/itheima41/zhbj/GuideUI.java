package com.itheima41.zhbj;

import java.util.ArrayList;
import java.util.List;

import com.itheima41.zhbj.utils.CacheUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

public class GuideUI extends Activity implements OnPageChangeListener, OnClickListener {

	private List<ImageView> imageViewList;
	private ViewPager mViewPager;
	private LinearLayout llPointGroup; // 点的组
	private View selectPoint; // 选中的点
	private int basicWidth; // 点与点之间的间距
	private Button btnStartExperience; // 开始体验

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 出去标题, 一定要在setContentView之前调用
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.guide);
		
		init();
	}

	private void init() {
		mViewPager = (ViewPager) findViewById(R.id.vp_welcome);
		btnStartExperience = (Button) findViewById(R.id.btn_welcome_start_experience);
		llPointGroup = (LinearLayout) findViewById(R.id.ll_welcome_point_group);
		selectPoint = findViewById(R.id.select_point);
		
		initData();
		
		// 设置适配器给ViewPager
		MyAdapter mAdapter = new MyAdapter();
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(this);
		btnStartExperience.setOnClickListener(this);
		
		// 获取间距
		selectPoint.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			/**
			 * 只要布局的参数一改变就会触发此方法.
			 */
			@Override
			public void onGlobalLayout() {
				// 把当前的事件给移除监听.
				selectPoint.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				
				// 获取间距
				basicWidth = llPointGroup.getChildAt(1).getLeft() - llPointGroup.getChildAt(0).getLeft();
				System.out.println("basicWidth: " + basicWidth);
			}
		});
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		int[] imageResIDs = {
				R.drawable.guide_1,
				R.drawable.guide_2,
				R.drawable.guide_3
		};
		
		imageViewList = new ArrayList<ImageView>();
		
		ImageView iv;
		View v;
		for (int i = 0; i < imageResIDs.length; i++) {
			iv = new ImageView(this);
			iv.setBackgroundResource(imageResIDs[i]);
			imageViewList.add(iv);
			
			// 每循环一次, 向LinearLayout布局中添加一个View.	
			v = new View(this);
			v.setBackgroundResource(R.drawable.point_normal);
			LayoutParams params = new LayoutParams(10, 10);
			if(i != 0) {
				params.leftMargin = 10;
			}
			v.setLayoutParams(params);
			llPointGroup.addView(v);
		}
	}
	
	class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageViewList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		/**
		 * container 就是ViewPager
		 * object 就是当前需要被移除的View
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
//			mViewPager.removeView(imageViewList.get(position));
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// 把ImageView添加到ViewPager中, 并且把ImageView返回回去.
			ImageView imageView = imageViewList.get(position);
			container.addView(imageView);
			return imageView;
		}
	}

	/**
	 * 当页面正在滑动中时触发此方法.
	 * @param position 当前滑动的是第几页
	 * @param positionOffset 页面滑动的百分比
	 * @param positionOffsetPixels 页面移动的像素
	 */
	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
//		System.out.println("onPageScrolled: position=" + position + ", positionOffset=" + positionOffset);
		
		float leftMargin = basicWidth * (position + positionOffset);
//		System.out.println("leftMargin: " + leftMargin);
		
		RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) selectPoint.getLayoutParams();
		lp.leftMargin = (int) leftMargin;
		selectPoint.setLayoutParams(lp);
	}

	/**
	 * 当新的页面被选中时触发.
	 */
	@Override
	public void onPageSelected(int position) {
		if(position == imageViewList.size() -1) {
			btnStartExperience.setVisibility(View.VISIBLE);
		} else {
			btnStartExperience.setVisibility(View.GONE);
		}
	}

	/**
	 * 当页面滚动状态改变时.
	 */
	@Override
	public void onPageScrollStateChanged(int state) {
		
	}

	@Override
	public void onClick(View v) {
		// 存储一个数据到本地, 标识已经打开过程序
		CacheUtils.putBoolean(this, WelcomeUI.IS_FIRST_OPEN, false);
		
		// 打开主页面
		startActivity(new Intent(this, MainUI.class));
		
		finish();
	}
}
