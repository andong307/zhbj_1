package com.itheima41.zhbj.base.newscentermenu.impl;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.andong.widget.OnRefreshListener;
import com.andong.widget.RefreshListView;
import com.google.gson.Gson;
import com.itheima41.zhbj.R;
import com.itheima41.zhbj.base.NewsCenterMenuBasePager;
import com.itheima41.zhbj.domain.NewsCenterMenuBean.NewsCenterTabBean;
import com.itheima41.zhbj.domain.TabDetailBean;
import com.itheima41.zhbj.domain.TabDetailBean.NewsBean;
import com.itheima41.zhbj.domain.TabDetailBean.TopNew;
import com.itheima41.zhbj.utils.CacheUtils;
import com.itheima41.zhbj.utils.Constants;
import com.itheima41.zhbj.view.HorizontalScrollViewPager;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * @author andong
 * 新闻中心页签对应的详情页面
 */
public class NewsMenuTabDetailPager extends NewsCenterMenuBasePager implements OnPageChangeListener, OnRefreshListener {

	private NewsCenterTabBean mNewsCenterTabBean;
	
	@ViewInject(R.id.hsvp_tab_detail_topnews)
	private HorizontalScrollViewPager topNewViewPager; // 顶部新闻的viewpager对象

	@ViewInject(R.id.tv_tab_detail_description)
	private TextView tvDescription; // 图片描述

	@ViewInject(R.id.ll_tab_detail_point_group)
	private LinearLayout llPointGroup; // 点的组

	@ViewInject(R.id.rlv_tab_detail_news)
	private RefreshListView newsListView; // 新闻列表listview对象

	private List<TopNew> topNewsList;

	private BitmapUtils bitmapUtils;

	private int previousEnabledPosition; // 前一个被选中的点的索引
	
	private InternalHandler mHandler; // 轮播图播放的消息处理器

	private List<NewsBean> newsList; // 新闻列表的数据.

	private String url;

	private TopNewsAdapter topNewsAdapter;

	private NewsAdapter newsAdapter;

	private String moreUrl;
	
	public NewsMenuTabDetailPager(Context context) {
		super(context);
	}

	public NewsMenuTabDetailPager(Context context, NewsCenterTabBean newsCenterTabBean) {
		super(context);
		this.mNewsCenterTabBean = newsCenterTabBean;

		bitmapUtils = new BitmapUtils(mContext);
		bitmapUtils.configDefaultBitmapConfig(Config.ARGB_4444);
	}

	@Override
	public View initView() {
		View view = View.inflate(mContext, R.layout.tab_detail, null);
		ViewUtils.inject(this, view);
		
		View topNewsView = View.inflate(mContext, R.layout.tab_detail_topnews, null);
		ViewUtils.inject(this, topNewsView);
		
//		newsListView.addListViewCustomHeaderView(topNewsView); // 把轮播图添加到ListView的头布局来显示.
//		newsListView.setEnabledPullDownRefresh(true);
//		newsListView.setEnabledLoadMoreRefresh(true);
		
		newsListView.addCustomHeaderView(topNewsView);
		newsListView.setPullToRefreshEnable(true);
		newsListView.setLoadingMoreEnable(true);
		newsListView.setOnRefreshListener(this);
		return view;
	}

	@Override
	public void initData() {
		url = Constants.SERVICE_URL + mNewsCenterTabBean.url;
		
		getDataFromNet(url);
	}

	private void getDataFromNet(final String url) {
		String json = CacheUtils.getString(mContext, url, null);
		if(!TextUtils.isEmpty(json)) {
			processData(json);
		}
		
		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				System.out.println(mNewsCenterTabBean.title + "数据请求成功: " + responseInfo.result);
				
				CacheUtils.putString(mContext, url, responseInfo.result);
				
				// 缓存完数据, 开始处理数据.
				processData(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				System.out.println(mNewsCenterTabBean.title + "数据请求失败: " + msg);
			}
		});
	}

	/**
	 * 解析json数据
	 * @param json
	 * @return
	 */
	private TabDetailBean parserJson(String json) {
		Gson gson = new Gson();
		TabDetailBean bean = gson.fromJson(json, TabDetailBean.class);
		moreUrl = bean.data.more;
		if(!TextUtils.isEmpty(moreUrl)) {
			moreUrl = Constants.SERVICE_URL + moreUrl;
		}
		return bean;
	}
	
	/**
	 * 接收json数据, 进行解析, 并展示界面.
	 * @param result
	 */
	protected void processData(String result) {
		TabDetailBean bean = parserJson(result);
		
		// 初始化顶部轮播新闻的数据
		topNewsList = bean.data.topnews;
		if(topNewsAdapter == null) {
			topNewsAdapter = new TopNewsAdapter();
			topNewViewPager.setAdapter(topNewsAdapter);
			topNewViewPager.setOnPageChangeListener(this);
		} else {
			topNewsAdapter.notifyDataSetChanged();
		}
		
		// 初始化轮播图对应的点
		llPointGroup.removeAllViews(); // 把线性布局中所有的点清楚
		View view;
		LayoutParams params;
		for (int i = 0; i < topNewsList.size(); i++) {
			view = new View(mContext);
			view.setBackgroundResource(R.drawable.tab_detail_topnews_point_bg);
			params = new LayoutParams(5, 5);
			if(i != 0) {
				params.leftMargin = 10;
			}
			view.setLayoutParams(params);
			view.setEnabled(false);
			llPointGroup.addView(view);
		}
		
		// 设置默认图片的描述和选中的点
		previousEnabledPosition = 0;
		TopNew topNew = topNewsList.get(previousEnabledPosition);
		tvDescription.setText(topNew.title);
		llPointGroup.getChildAt(previousEnabledPosition).setEnabled(true);
		
		// 开始轮播图循环播放.
		if(mHandler == null) {
			mHandler = new InternalHandler();
		}
		
		// 移除Handler对应消息队列中的回调和消息
		mHandler.removeCallbacksAndMessages(null);
		mHandler.postDelayed(new AutoSwitchPagerRunnable(), 5000);
		
		// 新闻列表数据初始化
		newsList = bean.data.news;
		if(newsAdapter == null) {
			newsAdapter = new NewsAdapter();
			newsListView.setAdapter(newsAdapter);
		} else {
			newsAdapter.notifyDataSetChanged();
		}
	}
	
	class NewsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return newsList.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			NewsViewHolder mHolder = null;
			if(convertView == null) {
				convertView = View.inflate(mContext, R.layout.tab_detail_news_item, null);
				mHolder = new NewsViewHolder();
				mHolder.ivImage = (ImageView) convertView.findViewById(R.id.iv_tab_detail_news_item_image);
				mHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_tab_detail_news_item_title);
				mHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_tab_detail_news_item_time);
				
				convertView.setTag(mHolder); // 把mholder类设置convertView, 为了下一次缓存时使用.
			} else {
				mHolder = (NewsViewHolder) convertView.getTag();
			}
			
			// 把mHolder类中的对象赋值.
			NewsBean newsBean = newsList.get(position);
			bitmapUtils.display(mHolder.ivImage, newsBean.listimage);
			mHolder.tvTitle.setText(newsBean.title);
			mHolder.tvTime.setText(newsBean.pubdate);
			
			return convertView;
		}
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}
	
	class NewsViewHolder {
		
		public ImageView ivImage;
		public TextView tvTitle;
		public TextView tvTime;
	}
	
	class TopNewsAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return topNewsList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView iv = new ImageView(mContext);
			iv.setScaleType(ScaleType.FIT_XY);
			// 设置一张默认的图片
			iv.setImageResource(R.drawable.home_scroll_default);
			iv.setOnTouchListener(new TopNewsItemTouchListener());
			
			container.addView(iv); // 把ImageView添加到ViewPager中.
			
			// 请求网络图片, 把图片展示给ImageView
			TopNew topNew = topNewsList.get(position);
			bitmapUtils.display(iv, topNew.topimage);
			return iv;
		}
	}
	
	/**
	 * @author andong
	 * 顶部轮播新闻中的图片的触摸事件
	 */
	class TopNewsItemTouchListener implements OnTouchListener {

		private int downX;
		private int downY;
		private long downTime;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				System.out.println("停止播放");
				mHandler.removeCallbacksAndMessages(null);
				downX = (int) event.getX();
				downY = (int) event.getY();
				downTime = System.currentTimeMillis();
				break;
			case MotionEvent.ACTION_UP:
				System.out.println("开始播放");
				mHandler.postDelayed(new AutoSwitchPagerRunnable(), 3000);
				int upX = (int) event.getX();
				int upY = (int) event.getY();
				
				if(downX == upX && downY == upY) {
					// 判断按下和抬起的时间是否超过了500毫秒.
					long upTime = System.currentTimeMillis();
					
					long time = upTime - downTime;
					if(time < 500) {
						topNewItemClick(v);
					}
				}
				break;
			default:
				break;
			}
			return true;
		}
	}

	public void topNewItemClick(View v) {
		System.out.println("轮播图的图片被点击了.");
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
		// 把对应position的点给选中, 并且把前一个被选中的点取消
		llPointGroup.getChildAt(previousEnabledPosition).setEnabled(false);
		llPointGroup.getChildAt(position).setEnabled(true);
		previousEnabledPosition = position;
		
		tvDescription.setText(topNewsList.get(position).title);
	}
	
	/**
	 * @author andong
	 * 内部消息处理器
	 */
	class InternalHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// 切换图片.
			int currentItem = (topNewViewPager.getCurrentItem() + 1) % topNewsList.size();
			topNewViewPager.setCurrentItem(currentItem);
			
			postDelayed(new AutoSwitchPagerRunnable(), 3000);
		}
	}
	
	class AutoSwitchPagerRunnable implements Runnable {

		@Override
		public void run() {
			// 得到一个消息发送给handler中的handleMessage方法.
			mHandler.obtainMessage().sendToTarget();
		}
	}

//	@Override
//	public void onPullDownRefresh() {
//		HttpUtils utils = new HttpUtils();
//		utils.send(HttpMethod.GET, url, new RequestCallBack<String>() {
//
//			@Override
//			public void onSuccess(ResponseInfo<String> responseInfo) {
//				newsListView.OnRefreshDataFinish();
//				Toast.makeText(mContext, "刷新数据成功", 0).show();
//				
//				CacheUtils.putString(mContext, url, responseInfo.result);
//				
//				processData(responseInfo.result);
//			}
//
//			@Override
//			public void onFailure(HttpException error, String msg) {
//				newsListView.OnRefreshDataFinish();
//				Toast.makeText(mContext, "刷新数据失败", 0).show();
//			}
//		});
//	}
//
//	@Override
//	public void onLoadingMore() {
//		// 加载更多数据, 去刷新更多的数据, 并且把脚布局隐藏
//		if(TextUtils.isEmpty(moreUrl)) {
//			// 没有更多数据了.
//			Toast.makeText(mContext, "没有更多数据", 0).show();
//			newsListView.OnRefreshDataFinish();
//		} else {
//			// 有更多的数据, 去请求.
//			HttpUtils utils = new HttpUtils();
//			utils.send(HttpMethod.GET, moreUrl, new RequestCallBack<String>() {
//
//				@Override
//				public void onSuccess(ResponseInfo<String> responseInfo) {
//					newsListView.OnRefreshDataFinish();
//					Toast.makeText(mContext, "加载更多数据成功", 0).show();
//					
//					TabDetailBean bean = parserJson(responseInfo.result);
//					// 把新闻列表数据取出来, 并且在原有集合基础上加上.
//					newsList.addAll(bean.data.news);
//					newsAdapter.notifyDataSetChanged();
//				}
//
//				@Override
//				public void onFailure(HttpException error, String msg) {
//					newsListView.OnRefreshDataFinish();
//					Toast.makeText(mContext, "加载更多数据失败", 0).show();
//				}
//			});
//		}
//	}

	@Override
	public void onRefresh() {
		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				newsListView.onRefreshFinish();
				Toast.makeText(mContext, "刷新数据成功", 0).show();
				
				CacheUtils.putString(mContext, url, responseInfo.result);
				
				processData(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				newsListView.onRefreshFinish();
				Toast.makeText(mContext, "刷新数据失败", 0).show();
			}
		});
	}

	@Override
	public void onLoadMoreData() {
		// 加载更多数据, 去刷新更多的数据, 并且把脚布局隐藏
		if(TextUtils.isEmpty(moreUrl)) {
			// 没有更多数据了.
			Toast.makeText(mContext, "没有更多数据", 0).show();
			newsListView.onRefreshFinish();
		} else {
			// 有更多的数据, 去请求.
			HttpUtils utils = new HttpUtils();
			utils.send(HttpMethod.GET, moreUrl, new RequestCallBack<String>() {

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					newsListView.onRefreshFinish();
					Toast.makeText(mContext, "加载更多数据成功", 0).show();
					
					TabDetailBean bean = parserJson(responseInfo.result);
					// 把新闻列表数据取出来, 并且在原有集合基础上加上.
					newsList.addAll(bean.data.news);
					newsAdapter.notifyDataSetChanged();
				}

				@Override
				public void onFailure(HttpException error, String msg) {
					newsListView.onRefreshFinish();
					Toast.makeText(mContext, "加载更多数据失败", 0).show();
				}
			});
		}
	}
}
