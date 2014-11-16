package com.itheima41.zhbj.domain;

import java.util.List;

/**
 * @author andong
 * 新闻中心页面的数据实体类
 */
public class NewsCenterMenuBean {

	public int retcode;
	public List<NewsCenterMenu> data;
	public List<String> extend;
	
	/**
	 * @author andong
	 * 新闻中心左侧菜单实体类
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
	 * 新闻中心页签实体类
	 */
	public class NewsCenterTabBean {
		
		public int id;
		public String title;
		public int type;
		public String url;
	}
}
