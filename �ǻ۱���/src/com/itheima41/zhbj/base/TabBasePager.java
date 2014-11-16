package com.itheima41.zhbj.base;

import com.itheima41.zhbj.MainUI;
import com.itheima41.zhbj.R;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * @author andong
 * ��ҳҳǩ��Ӧ���ݵĻ���
 */
public class TabBasePager implements OnClickListener {
	
	public Context mContext;
	private View rootView; // ��ǰView����
	public TextView tvTitle; // ����
	public ImageButton ibMenu; // �˵�
	public FrameLayout flContent; // ��������

	public TabBasePager(Context context) {
		this.mContext = context;
		
		rootView = initView();
	}

	/**
	 * ��ʼ������
	 * @return
	 */
	private View initView() {
		View view = View.inflate(mContext, R.layout.tab_base_pager, null);
		flContent = (FrameLayout) view.findViewById(R.id.fl_tab_base_pager_content);
		tvTitle = (TextView) view.findViewById(R.id.tv_title_bar_title);
		ibMenu = (ImageButton) view.findViewById(R.id.ib_title_bar_menu);
		ibMenu.setOnClickListener(this);
		return view;
	}
	
	/**
	 * ��õ�ǰҳ��Ĳ��ֶ���
	 * @return
	 */
	public View getRootView() {
		return rootView;
	}
	
	/**
	 * ������Ҫ���Ǵ˷���, ʵ���Լ��ĳ�ʼ�������߼�
	 */
	public void initData() {
		
	}

	@Override
	public void onClick(View v) {
		MainUI mainUI = ((MainUI) mContext);
		mainUI.getSlidingMenu().toggle();// ����SlidingMenu�˵��Ǵ򿪻��ǹر�
	}
}
