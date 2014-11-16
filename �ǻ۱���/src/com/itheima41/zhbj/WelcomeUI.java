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
	
	// 是否是第一次打开程序的键
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
		
		// 旋转动画: 0~360, 中心点
		RotateAnimation rotateAnim = new RotateAnimation(
				0, 360, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnim.setDuration(1000);
		rotateAnim.setFillAfter(true); // 动画执行完毕后, 停留在动画结束的状态下.
		animationSet.addAnimation(rotateAnim);
		
		// 缩放: 0~1, 以中心点缩放
		ScaleAnimation scaleAnim = new ScaleAnimation(
				0, 1, 
				0, 1, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnim.setDuration(1000);
		scaleAnim.setFillAfter(true);
		animationSet.addAnimation(scaleAnim);
		
		// 渐变动画: 从没有到有 0~1
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
			// 动画执行完毕, 需要判断是否是第一次打开程序
			boolean isFirstOpen = CacheUtils.getBoolean(WelcomeUI.this, IS_FIRST_OPEN, true);
			if(isFirstOpen) {
				// 当前是第一次打开, 跳转到引导页面
				System.out.println("跳转到引导页面");
				startActivity(new Intent(WelcomeUI.this, GuideUI.class));
			} else {
				// 已经打开过, 跳转到主界面
				System.out.println("跳转到主界面");
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
