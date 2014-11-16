package com.itheima41.zhbj.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itheima41.zhbj.R;

/**
 * @author andong
 * 在原有ListView的基础上增加下拉刷新头, 和加载更多尾.
 */
public class RefreshListView extends ListView implements OnScrollListener {

	private LinearLayout mHeaderView; // ListView的头布局
	private View mCustomHeaderView; // 用户添加的自定义头: 轮播图ViewPager
	private int downY = -1; // 按下时y轴值
	private int mPullDownHeaderViewHeight; // 下拉头布局的高度
	private View mPullDownHeaderView; // 下拉头布局对象

	private final int PULL_DOWN_REFRESH = 0; // 下拉刷新状态
	private final int RELEASE_REFRESH = 1; // 松开刷新状态
	private final int REFRESHING = 2;  // 正在刷新中状态
	
	private int currentState = PULL_DOWN_REFRESH; // 当前下拉刷新头的状态, 默认是: 下拉刷新状态
	private RotateAnimation downAnimation; // 向下旋转
	private RotateAnimation upAnimation; // 向上旋转
	private ImageView ivArrow; // 头布局中的箭头
	private ProgressBar mProgressBar; // 头布局的进度圈
	private TextView tvState; // 头布局的状态
	private TextView tvLastUpdateTime; // 头布局的最后刷新时间.
	private int mListViewOnScreenY = -1; // 当前ListView的左上角在屏幕中y轴的坐标点, 默认为: -1
	private OnRefreshListener mOnRefreshListener; // 用户的回调事件
	private View mFooterView; // 加载更多脚布局
	private int mFooterViewHeight; // 脚布局的高度
	private boolean isLoadingMore = false; // 是否正在加载更多中, 默认为: false
	private boolean isEnabledPullDownRefresh = false; // 是否启用下拉刷新的功能
	private boolean isEnabledLoadMoreRefresh = false;  // 是否启用加载更多的功能

	public RefreshListView(Context context) {
		super(context);
		initPullDownHeaderView();
		initLoadMoreFooterView();
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPullDownHeaderView();
		initLoadMoreFooterView();
	}
	
	/**
	 * 初始化加载更多的脚布局
	 */
	private void initLoadMoreFooterView() {
		mFooterView = View.inflate(getContext(), R.layout.refreshlistview_footer, null);
		mFooterView.measure(0, 0);
		mFooterViewHeight = mFooterView.getMeasuredHeight();
		
		mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
		addFooterView(mFooterView);
		
		// 给当前ListView设置滚动的监听事件
		setOnScrollListener(this);
	}

	/**
	 * 初始化下拉头
	 */
	private void initPullDownHeaderView() {
		mHeaderView = (LinearLayout) View.inflate(getContext(), R.layout.refreshlistview_header, null);
		// 下拉刷新的头布局
		mPullDownHeaderView = mHeaderView.findViewById(R.id.ll_refreshlistview_pull_down_header);
		ivArrow = (ImageView) mHeaderView.findViewById(R.id.iv_refreshlistview_header_arrow);
		mProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.pb_refreshlistview_header);
		tvState = (TextView) mHeaderView.findViewById(R.id.tv_refreshlistview_header_state);
		tvLastUpdateTime = (TextView) mHeaderView.findViewById(R.id.tv_refreshlistview_header_last_update_time);
		
		mPullDownHeaderView.measure(0, 0); // 自己测量自己的高度
		mPullDownHeaderViewHeight = mPullDownHeaderView.getMeasuredHeight();
		System.out.println("下拉头布局的高度: " + mPullDownHeaderViewHeight);
		
		// 隐藏下拉头布局
		mPullDownHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);
		
		this.addHeaderView(mHeaderView);
		
		initAnimation();
	}
	
	private void initAnimation() {
		upAnimation = new RotateAnimation(
				0, -180, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		upAnimation.setDuration(500);
		upAnimation.setFillAfter(true); 
		
		downAnimation = new RotateAnimation(
				-180, -360, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		downAnimation.setDuration(500);
		downAnimation.setFillAfter(true); 
	}

	/**
	 * 添加一个自定义的头布局, 加载下拉刷新的下面.
	 * @param v
	 */
	public void addListViewCustomHeaderView(View v) {
		mCustomHeaderView = v;
		mHeaderView.addView(v);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downY = (int) ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if(downY == -1) {
				downY = (int) ev.getY();
			}
			
			if(!isEnabledPullDownRefresh) {
				break;
			}
			
			// 如果当前状态是正在刷新中, 直接跳出switch语句.
			if(currentState == REFRESHING) {
				break;
			}
			
			if(mCustomHeaderView != null) {
				// 如果轮播图的布局没有完全显示, 不应该进行下拉头的操作, 
				// 而是响应ListView的本身的touch事件, 直接跳出Swtich语句.
				
				int[] location = new int[2]; // 第0位是x轴的地址, 第1位是y轴的值
				if(mListViewOnScreenY == -1) {
					this.getLocationOnScreen(location); // 获取ListView在屏幕中的坐标
					mListViewOnScreenY = location[1];
				}
				// 取出轮播图在屏幕中y轴的值
				mCustomHeaderView.getLocationOnScreen(location);
				
				// 如果轮播图在屏幕中y轴的值, 小于 当前Listview在屏幕中y轴的值, 
				// 轮播图没有完全显示, 不执行下拉头的操作, 直接跳出.
				if(location[1] < mListViewOnScreenY) {
//					System.out.println("轮播图没有完全显示, 直接跳出.");
					break;
				}
			}

			int moveY = (int) ev.getY();
			int diffY = moveY - downY;
			
			if(diffY > 0 && getFirstVisiblePosition() == 0) { // 当前是向下滑动, 并且是在ListView的顶部.
				int paddingTop = -mPullDownHeaderViewHeight + diffY;
				
				if(paddingTop < 0 && currentState != PULL_DOWN_REFRESH) {  
					// 当前没有完全显示, 并且当前状态属于松开刷新, 进入下拉刷新
					System.out.println("下拉刷新");
					currentState = PULL_DOWN_REFRESH;
					refreshPullDownState();
				} else if(paddingTop > 0 && currentState != RELEASE_REFRESH) { 
					// 当前完全显示, 并且当前的状态是下拉刷新, 进入到松开刷新
					System.out.println("松开刷新");
					currentState = RELEASE_REFRESH;
					refreshPullDownState();
				}
				mPullDownHeaderView.setPadding(0, paddingTop, 0, 0);
				return true; // 自己来处理当前事件, 不响应父类的touch事件
			}
			break;
		case MotionEvent.ACTION_UP:
			downY = -1;
			
			if(currentState == PULL_DOWN_REFRESH) {
				// 当前是下拉刷新, 把头布局隐藏
				mPullDownHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);
			} else if(currentState == RELEASE_REFRESH) {
				// 当前是松开刷新, 进入正在刷新中状态
				currentState = REFRESHING;
				refreshPullDownState();
				mPullDownHeaderView.setPadding(0, 0, 0, 0);
				
				// 调用使用者的监听事件.
				if(mOnRefreshListener != null) {
					mOnRefreshListener.onPullDownRefresh();
				}
			}
			
			break;
		default:
			break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 根据当前的状态currentState来刷新头布局
	 */
	private void refreshPullDownState() {
		switch (currentState) {
		case PULL_DOWN_REFRESH: // 下拉刷新
			// 箭头执行向下旋转的动画
			ivArrow.startAnimation(downAnimation);
			// 把状态修改为: 下拉刷新
			tvState.setText("下拉刷新");
			break;
		case RELEASE_REFRESH: // 释放刷新
			// 箭头执行向下旋转的动画
			ivArrow.startAnimation(upAnimation);
			// 把状态修改为: 下拉刷新
			tvState.setText("松开刷新");
			break;
		case REFRESHING: // 正在刷新中
			ivArrow.clearAnimation(); // 把动画清除掉
			ivArrow.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
			
			tvState.setText("正在刷新中..");
			break;
		default:
			break;
		}
	}
	
	/**
	 * 当用户刷新数据完成时, 回调此方法, 把下拉刷新的头或者加载更多的脚给隐藏
	 */
	public void OnRefreshDataFinish() {
		if(isLoadingMore) {
			// 当前是加载更多, 隐藏脚布局
			isLoadingMore = false;
			mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
		} else {
			// 当前是下拉刷新, 隐藏头布局
			ivArrow.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.INVISIBLE);
			tvState.setText("下拉刷新");
			tvLastUpdateTime.setText("最后刷新时间: " + getCurrentTime());
			mPullDownHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);
			currentState = PULL_DOWN_REFRESH;
		}
	}
	
	/**
	 * 获取当前系统的时间, 格式为: 2014-11-16 16:07:12
	 * @return
	 */
	public String getCurrentTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}
	
	/**
	 * 设置ListView刷新事件的监听事件
	 * @param listener
	 */
	public void setOnRefreshListener(OnRefreshListener listener) {
		this.mOnRefreshListener = listener;
	}
	
	/**
	 * @author andong
	 * 自定义ListView刷新的监听事件
	 */
	public interface OnRefreshListener {
		
		/**
		 * 当下拉刷新时, 回调此方法.
		 */
		public void onPullDownRefresh();
		
		/**
		 * 当加载更多时触发此方法.
		 */
		public void onLoadingMore();
	}

	/**
	 * 当滚动的状态改变时触发此方法.
	 * scrollState 当前的滚动状态
	 * 
	 * SCROLL_STATE_IDLE 停止
	 * SCROLL_STATE_TOUCH_SCROLL 触摸滚动
	 * SCROLL_STATE_FLING 快速的一滑
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(!isEnabledLoadMoreRefresh) {
			return;
		}
		
		// 当滚动停止时, 当前ListView是在底部(屏幕上最后一个显示的条目的索引是总长度 -1)
		if(scrollState == SCROLL_STATE_IDLE
				|| scrollState == SCROLL_STATE_FLING) {  // 滚动停止
			if((getLastVisiblePosition() == getCount() -1)
					&& !isLoadingMore) {
				System.out.println("滑动到底部了");
				
				isLoadingMore = true;
				// 显示脚布局
				mFooterView.setPadding(0, 0, 0, 0);
				// 让ListView滑动到底部.
				setSelection(getCount());
				
				if(mOnRefreshListener != null) {
					mOnRefreshListener.onLoadingMore();
				}
			}
		}
	}

	/**
	 * 当滚动时触发此方法
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}
	
	/**
	 * 设置是否启用下拉刷新
	 * @param b
	 */
	public void setEnabledPullDownRefresh(boolean b) {
		isEnabledPullDownRefresh = b;
	}

	public void setEnabledLoadMoreRefresh(boolean b) {
		isEnabledLoadMoreRefresh = b;
	}
}
