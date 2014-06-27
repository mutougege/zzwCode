package com.homer.viewflipper;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class ViewFlipperActivity extends Activity {

	private int[] imgs = { R.drawable.img1, R.drawable.img2, R.drawable.img3,
			R.drawable.img4, R.drawable.img5 };

	private GestureDetector gestureDetector = null;

	private ViewFlipper viewFlipper = null;

	private Activity mActivity = null;

	private LinearLayout mIndicator;
	private ImageView[] indiViews;
	private static final int mImageCounts = 5;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mActivity = this;

		viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
		for (int i = 0; i < imgs.length; i++) { // 添加图片源
			ImageView iv = new ImageView(this);
			iv.setImageResource(imgs[i]);
			iv.setScaleType(ImageView.ScaleType.FIT_XY);
			viewFlipper.addView(iv, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
		}
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.push_left_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.push_left_out));
		setIndex(viewFlipper);
		viewFlipper.setAutoStart(true); // 设置自动播放功能（点击事件，前自动播放）
		viewFlipper.setFlipInterval(3000);
		if (viewFlipper.isAutoStart() && !viewFlipper.isFlipping()) {
			viewFlipper.startFlipping();
		}

		gestureDetector = new GestureDetector(new MyGestureListener()); // 声明检测手势事件

		mIndicator = (LinearLayout) findViewById(R.id.gallery_indicator);
		indiViews = new ImageView[mImageCounts];
		for (int i = 0; i < mImageCounts; i++) {
			indiViews[i] = new ImageView(getApplicationContext());
			indiViews[i].setBackgroundResource(R.drawable.ty_round);
			mIndicator.addView(indiViews[i]);
		}
		updateIndiViewStatus(0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//viewFlipper.stopFlipping(); // 点击事件后，停止自动播放
		//viewFlipper.setAutoStart(false);
		return gestureDetector.onTouchEvent(event); // 注册手势事件
	}

	private class MyGestureListener implements OnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e2.getX() - e1.getX() > 120) { // 从左向右滑动（左进右出）
				Animation rInAnim = AnimationUtils.loadAnimation(mActivity,
						R.anim.push_right_in); // 向右滑动左侧进入的渐变效果（alpha 0.1 ->
												// 1.0）
				Animation rOutAnim = AnimationUtils.loadAnimation(mActivity,
						R.anim.push_right_out); // 向右滑动右侧滑出的渐变效果（alpha 1.0 ->
												// 0.1）

				viewFlipper.setInAnimation(rInAnim);
				viewFlipper.setOutAnimation(rOutAnim);
				setIndex(viewFlipper);
				viewFlipper.showPrevious();
				updateIndiViewStatus(viewFlipper.getDisplayedChild());
				return true;
			} else if (e2.getX() - e1.getX() < -120) { // 从右向左滑动（右进左出）
				Animation lInAnim = AnimationUtils.loadAnimation(mActivity,
						R.anim.push_left_in); // 向左滑动左侧进入的渐变效果（alpha 0.1 -> 1.0）
				Animation lOutAnim = AnimationUtils.loadAnimation(mActivity,
						R.anim.push_left_out); // 向左滑动右侧滑出的渐变效果（alpha 1.0 ->
												// 0.1）

				viewFlipper.setInAnimation(lInAnim);
				viewFlipper.setOutAnimation(lOutAnim);
				setIndex(viewFlipper);
				viewFlipper.showNext();
				updateIndiViewStatus(viewFlipper.getDisplayedChild());
				return true;
			}
			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
	}

	private void setIndex(final ViewFlipper viewFlipper){
		Animation inAnim = viewFlipper.getInAnimation();
		// 进行图片指示
		inAnim.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				updateIndiViewStatus(viewFlipper.getDisplayedChild());
			}
		});
	}
	
	private void updateIndiViewStatus(int position) {
		position = position % mImageCounts;
		if (indiViews == null || position < 0
				|| position + 1 > indiViews.length) {
			return;
		}
		for (int i = 0; i < indiViews.length; i++) {
			if (position == i) {
				indiViews[i].setBackgroundResource(R.drawable.ty_round1);
			} else {
				indiViews[i].setBackgroundResource(R.drawable.ty_round);
			}
		}
	}

}
