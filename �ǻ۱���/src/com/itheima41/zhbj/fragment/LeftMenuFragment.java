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
 * ���˵�Fragment
 */
public class LeftMenuFragment extends BaseFragment implements OnItemClickListener {

	private ListView mListView;
	private List<NewsCenterMenu> mLeftMenuList; // ��ǰҳ�������
	private int currentEnalbledPosition; // ��ǰ���õ�ѡ�������
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
	 * ���ò˵�������
	 * @param mLeftMenuList
	 */
	public void setMenuDataList(List<NewsCenterMenu> mLeftMenuList) {
		this.mLeftMenuList = mLeftMenuList;
		
		currentEnalbledPosition = 0;
		mAdapter = new MenuAdapter();
		mListView.setAdapter(mAdapter);
		
		// ����������Ĭ����ʾ�Ĳ���
		switchNewsCenterPager();
	}

	/**
	 * ����currentEnalbledPosition������, ���л��������Ķ�Ӧ�Ĳ˵�ҳ��
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
		// ˢ��һ��ListView
		mAdapter.notifyDataSetChanged();
		
		// �Ѳ˵��ر�, ��ʾ������
		((MainUI) mActivity).getSlidingMenu().toggle();
		
		// ���������л��ɶ�Ӧ�˵���ҳ��
		switchNewsCenterPager();
	}
}
