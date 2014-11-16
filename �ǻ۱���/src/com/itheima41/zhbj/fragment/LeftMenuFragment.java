package com.itheima41.zhbj.fragment;

import java.util.List;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima41.zhbj.MainUI;
import com.itheima41.zhbj.R;
import com.itheima41.zhbj.base.BaseFragment;
import com.itheima41.zhbj.base.impl.NewsCenterPager;
import com.itheima41.zhbj.domain.NewsCenterMenuBean.NewsCenterMenu;

/**
 * @author andong
 * 左侧菜单Fragment
 */
public class LeftMenuFragment extends BaseFragment implements OnItemClickListener {

	private ListView mListView;
	private List<NewsCenterMenu> mLeftMenuList; // 当前页面的数据
	private int currentEnalbledPosition; // 当前可用的选项的索引
	private MenuAdapter mAdapter;
	
	@Override
	public View initView() {
		mListView = new ListView(mActivity);
		mListView.setBackgroundColor(Color.BLACK);
		mListView.setCacheColorHint(Color.TRANSPARENT);
		mListView.setDividerHeight(0);
		mListView.setPadding(0, 40, 0, 0);
		mListView.setSelector(android.R.color.transparent);
		mListView.setOnItemClickListener(this);
		return mListView;
	}

	/**
	 * 设置菜单的数据
	 * @param mLeftMenuList
	 */
	public void setMenuDataList(List<NewsCenterMenu> mLeftMenuList) {
		this.mLeftMenuList = mLeftMenuList;
		
		currentEnalbledPosition = 0;
		mAdapter = new MenuAdapter();
		mListView.setAdapter(mAdapter);
		
		// 设置主界面默认显示的布局
		switchNewsCenterPager();
	}

	/**
	 * 根据currentEnalbledPosition的索引, 来切换新闻中心对应的菜单页面
	 */
	private void switchNewsCenterPager() {
		MainUI mainUI = ((MainUI) mActivity);
		ContentFragment contentFragment = mainUI.getContentFragment();
		NewsCenterPager newsCenterPager = contentFragment.getNewsCenterPager();
		newsCenterPager.switchPager(currentEnalbledPosition);
	}
	
	class MenuAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mLeftMenuList.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv = null;
			if(convertView == null) {
				tv = (TextView) View.inflate(mActivity, R.layout.left_menu_item, null);
			} else {
				tv = (TextView) convertView;
			}
			tv.setText(mLeftMenuList.get(position).title);
			tv.setEnabled(currentEnalbledPosition == position);
			return tv;
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		currentEnalbledPosition = position;
		// 刷新一下ListView
		mAdapter.notifyDataSetChanged();
		
		// 把菜单关闭, 显示主界面
		((MainUI) mActivity).getSlidingMenu().toggle();
		
		// 把主界面切换成对应菜单的页面
		switchNewsCenterPager();
	}
}
