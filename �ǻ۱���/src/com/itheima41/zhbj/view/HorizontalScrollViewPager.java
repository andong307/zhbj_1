package com.itheima41.zhbj.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class HorizontalScrollViewPager extends ViewPager {

	private int downX;
	private int downY;

	public HorizontalScrollViewPager(Context context) {
		super(context);
	}

	public HorizontalScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			getParent().requestDisallowInterceptTouchEvent(true);
			downX = (int) ev.getX();
			downY = (int) ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) ev.getX();
			int moveY = (int) ev.getY();
			
			int diffX = moveX - downX;
			int diffY = moveY - downY;
			if(Math.abs(diffX) > Math.abs(diffY)) { // 当前是横向滑动, 不让父元素拦截
				if(getCurrentItem() == 0 && diffX > 0) {
					// 显示的是第0张图, 并且是从左向右滑动.
					getParent().requestDisallowInterceptTouchEvent(false);
				} else if(getCurrentItem() == (getAdapter().getCount() -1)
						&& diffX < 0) {
					// 显示的是最后一张图, 并且是从右向左滑动.
					getParent().requestDisallowInterceptTouchEvent(false);
				} else {
					getParent().requestDisallowInterceptTouchEvent(true);
				}
			} else {
				getParent().requestDisallowInterceptTouchEvent(false);
			}
			break;

		default:
			break;
		}
		return super.dispatchTouchEvent(ev);
	}
}
