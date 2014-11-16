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
 * ��ԭ��ListView�Ļ�������������ˢ��ͷ, �ͼ��ظ���β.
 */
public class RefreshListView extends ListView implements OnScrollListener {

	private LinearLayout mHeaderView; // ListView��ͷ����
	private View mCustomHeaderView; // �û���ӵ��Զ���ͷ: �ֲ�ͼViewPager
	private int downY = -1; // ����ʱy��ֵ
	private int mPullDownHeaderViewHeight; // ����ͷ���ֵĸ߶�
	private View mPullDownHeaderView; // ����ͷ���ֶ���

	private final int PULL_DOWN_REFRESH = 0; // ����ˢ��״̬
	private final int RELEASE_REFRESH = 1; // �ɿ�ˢ��״̬
	private final int REFRESHING = 2;  // ����ˢ����״̬
	
	private int currentState = PULL_DOWN_REFRESH; // ��ǰ����ˢ��ͷ��״̬, Ĭ����: ����ˢ��״̬
	private RotateAnimation downAnimation; // ������ת
	private RotateAnimation upAnimation; // ������ת
	private ImageView ivArrow; // ͷ�����еļ�ͷ
	private ProgressBar mProgressBar; // ͷ���ֵĽ���Ȧ
	private TextView tvState; // ͷ���ֵ�״̬
	private TextView tvLastUpdateTime; // ͷ���ֵ����ˢ��ʱ��.
	private int mListViewOnScreenY = -1; // ��ǰListView�����Ͻ�����Ļ��y��������, Ĭ��Ϊ: -1
	private OnRefreshListener mOnRefreshListener; // �û��Ļص��¼�
	private View mFooterView; // ���ظ���Ų���
	private int mFooterViewHeight; // �Ų��ֵĸ߶�
	private boolean isLoadingMore = false; // �Ƿ����ڼ��ظ�����, Ĭ��Ϊ: false
	private boolean isEnabledPullDownRefresh = false; // �Ƿ���������ˢ�µĹ���
	private boolean isEnabledLoadMoreRefresh = false;  // �Ƿ����ü��ظ���Ĺ���

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
	 * ��ʼ�����ظ���ĽŲ���
	 */
	private void initLoadMoreFooterView() {
		mFooterView = View.inflate(getContext(), R.layout.refreshlistview_footer, null);
		mFooterView.measure(0, 0);
		mFooterViewHeight = mFooterView.getMeasuredHeight();
		
		mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
		addFooterView(mFooterView);
		
		// ����ǰListView���ù����ļ����¼�
		setOnScrollListener(this);
	}

	/**
	 * ��ʼ������ͷ
	 */
	private void initPullDownHeaderView() {
		mHeaderView = (LinearLayout) View.inflate(getContext(), R.layout.refreshlistview_header, null);
		// ����ˢ�µ�ͷ����
		mPullDownHeaderView = mHeaderView.findViewById(R.id.ll_refreshlistview_pull_down_header);
		ivArrow = (ImageView) mHeaderView.findViewById(R.id.iv_refreshlistview_header_arrow);
		mProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.pb_refreshlistview_header);
		tvState = (TextView) mHeaderView.findViewById(R.id.tv_refreshlistview_header_state);
		tvLastUpdateTime = (TextView) mHeaderView.findViewById(R.id.tv_refreshlistview_header_last_update_time);
		
		mPullDownHeaderView.measure(0, 0); // �Լ������Լ��ĸ߶�
		mPullDownHeaderViewHeight = mPullDownHeaderView.getMeasuredHeight();
		System.out.println("����ͷ���ֵĸ߶�: " + mPullDownHeaderViewHeight);
		
		// ��������ͷ����
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
	 * ���һ���Զ����ͷ����, ��������ˢ�µ�����.
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
			
			// �����ǰ״̬������ˢ����, ֱ������switch���.
			if(currentState == REFRESHING) {
				break;
			}
			
			if(mCustomHeaderView != null) {
				// ����ֲ�ͼ�Ĳ���û����ȫ��ʾ, ��Ӧ�ý�������ͷ�Ĳ���, 
				// ������ӦListView�ı����touch�¼�, ֱ������Swtich���.
				
				int[] location = new int[2]; // ��0λ��x��ĵ�ַ, ��1λ��y���ֵ
				if(mListViewOnScreenY == -1) {
					this.getLocationOnScreen(location); // ��ȡListView����Ļ�е�����
					mListViewOnScreenY = location[1];
				}
				// ȡ���ֲ�ͼ����Ļ��y���ֵ
				mCustomHeaderView.getLocationOnScreen(location);
				
				// ����ֲ�ͼ����Ļ��y���ֵ, С�� ��ǰListview����Ļ��y���ֵ, 
				// �ֲ�ͼû����ȫ��ʾ, ��ִ������ͷ�Ĳ���, ֱ������.
				if(location[1] < mListViewOnScreenY) {
//					System.out.println("�ֲ�ͼû����ȫ��ʾ, ֱ������.");
					break;
				}
			}

			int moveY = (int) ev.getY();
			int diffY = moveY - downY;
			
			if(diffY > 0 && getFirstVisiblePosition() == 0) { // ��ǰ�����»���, ��������ListView�Ķ���.
				int paddingTop = -mPullDownHeaderViewHeight + diffY;
				
				if(paddingTop < 0 && currentState != PULL_DOWN_REFRESH) {  
					// ��ǰû����ȫ��ʾ, ���ҵ�ǰ״̬�����ɿ�ˢ��, ��������ˢ��
					System.out.println("����ˢ��");
					currentState = PULL_DOWN_REFRESH;
					refreshPullDownState();
				} else if(paddingTop > 0 && currentState != RELEASE_REFRESH) { 
					// ��ǰ��ȫ��ʾ, ���ҵ�ǰ��״̬������ˢ��, ���뵽�ɿ�ˢ��
					System.out.println("�ɿ�ˢ��");
					currentState = RELEASE_REFRESH;
					refreshPullDownState();
				}
				mPullDownHeaderView.setPadding(0, paddingTop, 0, 0);
				return true; // �Լ�������ǰ�¼�, ����Ӧ�����touch�¼�
			}
			break;
		case MotionEvent.ACTION_UP:
			downY = -1;
			
			if(currentState == PULL_DOWN_REFRESH) {
				// ��ǰ������ˢ��, ��ͷ��������
				mPullDownHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);
			} else if(currentState == RELEASE_REFRESH) {
				// ��ǰ���ɿ�ˢ��, ��������ˢ����״̬
				currentState = REFRESHING;
				refreshPullDownState();
				mPullDownHeaderView.setPadding(0, 0, 0, 0);
				
				// ����ʹ���ߵļ����¼�.
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
	 * ���ݵ�ǰ��״̬currentState��ˢ��ͷ����
	 */
	private void refreshPullDownState() {
		switch (currentState) {
		case PULL_DOWN_REFRESH: // ����ˢ��
			// ��ͷִ��������ת�Ķ���
			ivArrow.startAnimation(downAnimation);
			// ��״̬�޸�Ϊ: ����ˢ��
			tvState.setText("����ˢ��");
			break;
		case RELEASE_REFRESH: // �ͷ�ˢ��
			// ��ͷִ��������ת�Ķ���
			ivArrow.startAnimation(upAnimation);
			// ��״̬�޸�Ϊ: ����ˢ��
			tvState.setText("�ɿ�ˢ��");
			break;
		case REFRESHING: // ����ˢ����
			ivArrow.clearAnimation(); // �Ѷ��������
			ivArrow.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
			
			tvState.setText("����ˢ����..");
			break;
		default:
			break;
		}
	}
	
	/**
	 * ���û�ˢ���������ʱ, �ص��˷���, ������ˢ�µ�ͷ���߼��ظ���ĽŸ�����
	 */
	public void OnRefreshDataFinish() {
		if(isLoadingMore) {
			// ��ǰ�Ǽ��ظ���, ���ؽŲ���
			isLoadingMore = false;
			mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
		} else {
			// ��ǰ������ˢ��, ����ͷ����
			ivArrow.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.INVISIBLE);
			tvState.setText("����ˢ��");
			tvLastUpdateTime.setText("���ˢ��ʱ��: " + getCurrentTime());
			mPullDownHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);
			currentState = PULL_DOWN_REFRESH;
		}
	}
	
	/**
	 * ��ȡ��ǰϵͳ��ʱ��, ��ʽΪ: 2014-11-16 16:07:12
	 * @return
	 */
	public String getCurrentTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}
	
	/**
	 * ����ListViewˢ���¼��ļ����¼�
	 * @param listener
	 */
	public void setOnRefreshListener(OnRefreshListener listener) {
		this.mOnRefreshListener = listener;
	}
	
	/**
	 * @author andong
	 * �Զ���ListViewˢ�µļ����¼�
	 */
	public interface OnRefreshListener {
		
		/**
		 * ������ˢ��ʱ, �ص��˷���.
		 */
		public void onPullDownRefresh();
		
		/**
		 * �����ظ���ʱ�����˷���.
		 */
		public void onLoadingMore();
	}

	/**
	 * ��������״̬�ı�ʱ�����˷���.
	 * scrollState ��ǰ�Ĺ���״̬
	 * 
	 * SCROLL_STATE_IDLE ֹͣ
	 * SCROLL_STATE_TOUCH_SCROLL ��������
	 * SCROLL_STATE_FLING ���ٵ�һ��
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(!isEnabledLoadMoreRefresh) {
			return;
		}
		
		// ������ֹͣʱ, ��ǰListView���ڵײ�(��Ļ�����һ����ʾ����Ŀ���������ܳ��� -1)
		if(scrollState == SCROLL_STATE_IDLE
				|| scrollState == SCROLL_STATE_FLING) {  // ����ֹͣ
			if((getLastVisiblePosition() == getCount() -1)
					&& !isLoadingMore) {
				System.out.println("�������ײ���");
				
				isLoadingMore = true;
				// ��ʾ�Ų���
				mFooterView.setPadding(0, 0, 0, 0);
				// ��ListView�������ײ�.
				setSelection(getCount());
				
				if(mOnRefreshListener != null) {
					mOnRefreshListener.onLoadingMore();
				}
			}
		}
	}

	/**
	 * ������ʱ�����˷���
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}
	
	/**
	 * �����Ƿ���������ˢ��
	 * @param b
	 */
	public void setEnabledPullDownRefresh(boolean b) {
		isEnabledPullDownRefresh = b;
	}

	public void setEnabledLoadMoreRefresh(boolean b) {
		isEnabledLoadMoreRefresh = b;
	}
}
