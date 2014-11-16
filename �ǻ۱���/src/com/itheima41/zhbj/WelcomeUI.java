package com.itheima41.zhbj;

import com.itheima41.zhbj.utils.CacheUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

public class WelcomeUI extends Activity {
	
	// �Ƿ��ǵ�һ�δ򿪳���ļ�
	public static final String IS_FIRST_OPEN = "is_first_open";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		
		init();
	}

	private void init() {
		View rootView = findViewById(R.id.rl_welcome_root);
		
		AnimationSet animationSet = new AnimationSet(false);
		
		// ��ת����: 0~360, ���ĵ�
		RotateAnimation rotateAnim = new RotateAnimation(
				0, 360, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnim.setDuration(1000);
		rotateAnim.setFillAfter(true); // ����ִ����Ϻ�, ͣ���ڶ���������״̬��.
		animationSet.addAnimation(rotateAnim);
		
		// ����: 0~1, �����ĵ�����
		ScaleAnimation scaleAnim = new ScaleAnimation(
				0, 1, 
				0, 1, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnim.setDuration(1000);
		scaleAnim.setFillAfter(true);
		animationSet.addAnimation(scaleAnim);
		
		// ���䶯��: ��û�е��� 0~1
		AlphaAnimation alphaAnima = new AlphaAnimation(0, 1);
		alphaAnima.setDuration(2000);
		alphaAnima.setFillAfter(true);
		animationSet.addAnimation(alphaAnima);
		animationSet.setAnimationListener(new MyAnimationListener());
		
		rootView.startAnimation(animationSet);
	}
	
	class MyAnimationListener implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {
			// ����ִ�����, ��Ҫ�ж��Ƿ��ǵ�һ�δ򿪳���
			boolean isFirstOpen = CacheUtils.getBoolean(WelcomeUI.this, IS_FIRST_OPEN, true);
			if(isFirstOpen) {
				// ��ǰ�ǵ�һ�δ�, ��ת������ҳ��
				System.out.println("��ת������ҳ��");
				startActivity(new Intent(WelcomeUI.this, GuideUI.class));
			} else {
				// �Ѿ��򿪹�, ��ת��������
				System.out.println("��ת��������");
				startActivity(new Intent(WelcomeUI.this, MainUI.class));
			}
			finish();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			
		}
		
	}
}
