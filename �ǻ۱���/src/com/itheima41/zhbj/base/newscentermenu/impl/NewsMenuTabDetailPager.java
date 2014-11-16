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
 * ��������ҳǩ��Ӧ������ҳ��
 */
public class NewsMenuTabDetailPager extends NewsCenterMenuBasePager implements OnPageChangeListener, OnRefreshListener {

	private NewsCenterTabBean mNewsCenterTabBean;
	
	@ViewInject(R.id.hsvp_tab_detail_topnews)
	private HorizontalScrollViewPager topNewViewPager; // �������ŵ�viewpager����

	@ViewInject(R.id.tv_tab_detail_description)
	private TextView tvDescription; // ͼƬ����

	@ViewInject(R.id.ll_tab_detail_point_group)
	private LinearLayout llPointGroup; // �����

	@ViewInject(R.id.rlv_tab_detail_news)
	private RefreshListView newsListView; // �����б�listview����

	private List<TopNew> topNewsList;

	private BitmapUtils bitmapUtils;

	private int previousEnabledPosition; // ǰһ����ѡ�еĵ������
	
	private InternalHandler mHandler; // �ֲ�ͼ���ŵ���Ϣ������

	private List<NewsBean> newsList; // �����б������.

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
		
//		newsListView.addListViewCustomHeaderView(topNewsView); // ���ֲ�ͼ��ӵ�ListView��ͷ��������ʾ.
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
				System.out.println(mNewsCenterTabBean.title + "��������ɹ�: " + responseInfo.result);
				
				CacheUtils.putString(mContext, url, responseInfo.result);
				
				// ����������, ��ʼ��������.
				processData(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				System.out.println(mNewsCenterTabBean.title + "��������ʧ��: " + msg);
			}
		});
	}

	/**
	 * ����json����
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
	 * ����json����, ���н���, ��չʾ����.
	 * @param result
	 */
	protected void processData(String result) {
		TabDetailBean bean = parserJson(result);
		
		// ��ʼ�������ֲ����ŵ�����
		topNewsList = bean.data.topnews;
		if(topNewsAdapter == null) {
			topNewsAdapter = new TopNewsAdapter();
			topNewViewPager.setAdapter(topNewsAdapter);
			topNewViewPager.setOnPageChangeListener(this);
		} else {
			topNewsAdapter.notifyDataSetChanged();
		}
		
		// ��ʼ���ֲ�ͼ��Ӧ�ĵ�
		llPointGroup.removeAllViews(); // �����Բ��������еĵ����
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
		
		// ����Ĭ��ͼƬ��������ѡ�еĵ�
		previousEnabledPosition = 0;
		TopNew topNew = topNewsList.get(previousEnabledPosition);
		tvDescription.setText(topNew.title);
		llPointGroup.getChildAt(previousEnabledPosition).setEnabled(true);
		
		// ��ʼ�ֲ�ͼѭ������.
		if(mHandler == null) {
			mHandler = new InternalHandler();
		}
		
		// �Ƴ�Handler��Ӧ��Ϣ�����еĻص�����Ϣ
		mHandler.removeCallbacksAndMessages(null);
		mHandler.postDelayed(new AutoSwitchPagerRunnable(), 5000);
		
		// �����б����ݳ�ʼ��
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
				
				convertView.setTag(mHolder); // ��mholder������convertView, Ϊ����һ�λ���ʱʹ��.
			} else {
				mHolder = (NewsViewHolder) convertView.getTag();
			}
			
			// ��mHolder���еĶ���ֵ.
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
			// ����һ��Ĭ�ϵ�ͼƬ
			iv.setImageResource(R.drawable.home_scroll_default);
			iv.setOnTouchListener(new TopNewsItemTouchListener());
			
			container.addView(iv); // ��ImageView��ӵ�ViewPager��.
			
			// ��������ͼƬ, ��ͼƬչʾ��ImageView
			TopNew topNew = topNewsList.get(position);
			bitmapUtils.display(iv, topNew.topimage);
			return iv;
		}
	}
	
	/**
	 * @author andong
	 * �����ֲ������е�ͼƬ�Ĵ����¼�
	 */
	class TopNewsItemTouchListener implements OnTouchListener {

		private int downX;
		private int downY;
		private long downTime;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				System.out.println("ֹͣ����");
				mHandler.removeCallbacksAndMessages(null);
				downX = (int) event.getX();
				downY = (int) event.getY();
				downTime = System.currentTimeMillis();
				break;
			case MotionEvent.ACTION_UP:
				System.out.println("��ʼ����");
				mHandler.postDelayed(new AutoSwitchPagerRunnable(), 3000);
				int upX = (int) event.getX();
				int upY = (int) event.getY();
				
				if(downX == upX && downY == upY) {
					// �жϰ��º�̧���ʱ���Ƿ񳬹���500����.
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
		System.out.println("�ֲ�ͼ��ͼƬ�������.");
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
		// �Ѷ�Ӧposition�ĵ��ѡ��, ���Ұ�ǰһ����ѡ�еĵ�ȡ��
		llPointGroup.getChildAt(previousEnabledPosition).setEnabled(false);
		llPointGroup.getChildAt(position).setEnabled(true);
		previousEnabledPosition = position;
		
		tvDescription.setText(topNewsList.get(position).title);
	}
	
	/**
	 * @author andong
	 * �ڲ���Ϣ������
	 */
	class InternalHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// �л�ͼƬ.
			int currentItem = (topNewViewPager.getCurrentItem() + 1) % topNewsList.size();
			topNewViewPager.setCurrentItem(currentItem);
			
			postDelayed(new AutoSwitchPagerRunnable(), 3000);
		}
	}
	
	class AutoSwitchPagerRunnable implements Runnable {

		@Override
		public void run() {
			// �õ�һ����Ϣ���͸�handler�е�handleMessage����.
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
//				Toast.makeText(mContext, "ˢ�����ݳɹ�", 0).show();
//				
//				CacheUtils.putString(mContext, url, responseInfo.result);
//				
//				processData(responseInfo.result);
//			}
//
//			@Override
//			public void onFailure(HttpException error, String msg) {
//				newsListView.OnRefreshDataFinish();
//				Toast.makeText(mContext, "ˢ������ʧ��", 0).show();
//			}
//		});
//	}
//
//	@Override
//	public void onLoadingMore() {
//		// ���ظ�������, ȥˢ�¸��������, ���ҰѽŲ�������
//		if(TextUtils.isEmpty(moreUrl)) {
//			// û�и���������.
//			Toast.makeText(mContext, "û�и�������", 0).show();
//			newsListView.OnRefreshDataFinish();
//		} else {
//			// �и��������, ȥ����.
//			HttpUtils utils = new HttpUtils();
//			utils.send(HttpMethod.GET, moreUrl, new RequestCallBack<String>() {
//
//				@Override
//				public void onSuccess(ResponseInfo<String> responseInfo) {
//					newsListView.OnRefreshDataFinish();
//					Toast.makeText(mContext, "���ظ������ݳɹ�", 0).show();
//					
//					TabDetailBean bean = parserJson(responseInfo.result);
//					// �������б�����ȡ����, ������ԭ�м��ϻ����ϼ���.
//					newsList.addAll(bean.data.news);
//					newsAdapter.notifyDataSetChanged();
//				}
//
//				@Override
//				public void onFailure(HttpException error, String msg) {
//					newsListView.OnRefreshDataFinish();
//					Toast.makeText(mContext, "���ظ�������ʧ��", 0).show();
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
				Toast.makeText(mContext, "ˢ�����ݳɹ�", 0).show();
				
				CacheUtils.putString(mContext, url, responseInfo.result);
				
				processData(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				newsListView.onRefreshFinish();
				Toast.makeText(mContext, "ˢ������ʧ��", 0).show();
			}
		});
	}

	@Override
	public void onLoadMoreData() {
		// ���ظ�������, ȥˢ�¸��������, ���ҰѽŲ�������
		if(TextUtils.isEmpty(moreUrl)) {
			// û�и���������.
			Toast.makeText(mContext, "û�и�������", 0).show();
			newsListView.onRefreshFinish();
		} else {
			// �и��������, ȥ����.
			HttpUtils utils = new HttpUtils();
			utils.send(HttpMethod.GET, moreUrl, new RequestCallBack<String>() {

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					newsListView.onRefreshFinish();
					Toast.makeText(mContext, "���ظ������ݳɹ�", 0).show();
					
					TabDetailBean bean = parserJson(responseInfo.result);
					// �������б�����ȡ����, ������ԭ�м��ϻ����ϼ���.
					newsList.addAll(bean.data.news);
					newsAdapter.notifyDataSetChanged();
				}

				@Override
				public void onFailure(HttpException error, String msg) {
					newsListView.onRefreshFinish();
					Toast.makeText(mContext, "���ظ�������ʧ��", 0).show();
				}
			});
		}
	}
}
