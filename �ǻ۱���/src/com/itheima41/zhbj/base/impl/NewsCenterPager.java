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
 * ��������ҳ��
 */
public class NewsCenterPager extends TabBasePager {
	
	private List<NewsCenterMenuBasePager> pagerList; // ���˵���Ӧ��ҳ��
	private List<NewsCenterMenu> mLeftMenuList; // ���˵�������

	public NewsCenterPager(Context context) {
		super(context);
	}

	@Override
	public void initData() {
		tvTitle.setText("����");
		ibMenu.setVisibility(View.VISIBLE);
		
		getDataFromNet();
	}

	/**
	 * ץȡ����
	 */
	private void getDataFromNet() {
		// �������Ƿ�������, ��������ݰ�����ȡ����, ����ʾ��������. Ȼ����ȥ��������.
		String json = CacheUtils.getString(mContext, Constants.NEWSCENTER_URL, null);
		if(!TextUtils.isEmpty(json)) {
			// ��ǰ���治Ϊnull, ����ʾ��������.
			processData(json);
		}
		
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, Constants.NEWSCENTER_URL, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				System.out.println("�����������ݷ��ʳɹ�: " + responseInfo.result);
				
				// �����ݻ�������.
				CacheUtils.putString(mContext, Constants.NEWSCENTER_URL, responseInfo.result);
				
				// ��������.
				processData(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				System.out.println("�����������ݷ���ʧ��: " + msg);
			}
		});
	}

	/**
	 * �����Ͱ�����
	 * @param result
	 */
	protected void processData(String result) {
		Gson gson = new Gson();
		NewsCenterMenuBean bean = gson.fromJson(result, NewsCenterMenuBean.class);
		
		// ��ʼ���˵���Ӧ��ҳ��
		pagerList = new ArrayList<NewsCenterMenuBasePager>();
		pagerList.add(new NewsMenuPager(mContext, bean.data.get(0)));
		pagerList.add(new TopicMenuPager(mContext));
		pagerList.add(new PhotosMenuPager(mContext));
		pagerList.add(new InteractMenuPager(mContext));
		
		// ��ʼ�����˵�������.
		mLeftMenuList = bean.data;
		LeftMenuFragment mLeftMenuFragment = ((MainUI) mContext).getLeftMenuFragment();
		mLeftMenuFragment.setMenuDataList(mLeftMenuList);
	}
	
	/**
	 * �л�ҳ��
	 * @param position
	 */
	public void switchPager(int position) {
		NewsCenterMenuBasePager pager = pagerList.get(position);
		View view = pager.getRootView();
		flContent.removeAllViews();
		flContent.addView(view);
		
		tvTitle.setText(mLeftMenuList.get(position).title);
		pager.initData(); // ��ǰ��Ҫ��ʾ����, ��Ҫ�������ȳ�ʼ����.
	}
}
