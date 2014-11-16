package com.itheima41.zhbj.domain;

import java.util.List;

/**
 * @author andong
 * ��������ҳ�������ʵ����
 */
public class NewsCenterMenuBean {

	public int retcode;
	public List<NewsCenterMenu> data;
	public List<String> extend;
	
	/**
	 * @author andong
	 * �����������˵�ʵ����
	 */
	public class NewsCenterMenu {
		
		public List<NewsCenterTabBean> children;
		public int id;
		public String title;
		public int type;
		public String url;
		public String url1;
		public String dayurl;
		public String excurl;
		public String weekurl;
	}
	
	/**
	 * @author andong
	 * ��������ҳǩʵ����
	 */
	public class NewsCenterTabBean {
		
		public int id;
		public String title;
		public int type;
		public String url;
	}
}
