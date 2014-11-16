package com.itheima41.zhbj.base.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.itheima41.zhbj.MainUI;
import com.itheima41.zhbj.base.NewsCenterMenuBasePager;
import com.itheima41.zhbj.base.TabBasePager;
import com.itheima41.zhbj.base.newscentermenu.impl.InteractMenuPager;
import com.itheima41.zhbj.base.newscentermenu.impl.NewsMenuPager;
import com.itheima41.zhbj.base.newscentermenu.impl.PhotosMenuPager;
import com.itheima41.zhbj.base.newscentermenu.impl.TopicMenuPager;
import com.itheima41.zhbj.domain.NewsCenterMenuBean;
import com.itheima41.zhbj.domain.NewsCenterMenuBean.NewsCenterMenu;
import com.itheima41.zhbj.fragment.LeftMenuFragment;
import com.itheima41.zhbj.utils.CacheUtils;
import com.itheima41.zhbj.utils.Constants;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * @author andong
 * 新闻中心页面
 */
public class NewsCenterPager extends TabBasePager {
	
	private List<NewsCenterMenuBasePager> pagerList; // 左侧菜单对应的页面
	private List<NewsCenterMenu> mLeftMenuList; // 左侧菜单的数据

	public NewsCenterPager(Context context) {
		super(context);
	}

	@Override
	public void initData() {
		tvTitle.setText("新闻");
		ibMenu.setVisibility(View.VISIBLE);
		
		getDataFromNet();
	}

	/**
	 * 抓取数据
	 */
	private void getDataFromNet() {
		// 看本地是否有数据, 如果有数据把数据取出来, 先显示到界面上. 然后再去请求网络.
		String json = CacheUtils.getString(mContext, Constants.NEWSCENTER_URL, null);
		if(!TextUtils.isEmpty(json)) {
			// 当前缓存不为null, 先显示到界面上.
			processData(json);
		}
		
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, Constants.NEWSCENTER_URL, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				System.out.println("新闻中心数据访问成功: " + responseInfo.result);
				
				// 把数据缓存起来.
				CacheUtils.putString(mContext, Constants.NEWSCENTER_URL, responseInfo.result);
				
				// 处理数据.
				processData(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				System.out.println("新闻中心数据访问失败: " + msg);
			}
		});
	}

	/**
	 * 解析和绑定数据
	 * @param result
	 */
	protected void processData(String result) {
		Gson gson = new Gson();
		NewsCenterMenuBean bean = gson.fromJson(result, NewsCenterMenuBean.class);
		
		// 初始化菜单对应的页面
		pagerList = new ArrayList<NewsCenterMenuBasePager>();
		pagerList.add(new NewsMenuPager(mContext, bean.data.get(0)));
		pagerList.add(new TopicMenuPager(mContext));
		pagerList.add(new PhotosMenuPager(mContext));
		pagerList.add(new InteractMenuPager(mContext));
		
		// 初始化左侧菜单的数据.
		mLeftMenuList = bean.data;
		LeftMenuFragment mLeftMenuFragment = ((MainUI) mContext).getLeftMenuFragment();
		mLeftMenuFragment.setMenuDataList(mLeftMenuList);
	}
	
	/**
	 * 切换页面
	 * @param position
	 */
	public void switchPager(int position) {
		NewsCenterMenuBasePager pager = pagerList.get(position);
		View view = pager.getRootView();
		flContent.removeAllViews();
		flContent.addView(view);
		
		tvTitle.setText(mLeftMenuList.get(position).title);
		pager.initData(); // 当前需要显示界面, 需要把数据先初始化了.
	}
}
