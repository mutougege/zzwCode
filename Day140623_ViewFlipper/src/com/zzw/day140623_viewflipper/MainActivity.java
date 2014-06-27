package com.zzw.day140623_viewflipper;




import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class MainActivity extends Activity {

	private ViewFlipper flipper;
	private LinearLayout mIndicator;
	private ImageView[] indiViews;
	private static final int mImageCounts = 6;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		flipper = (ViewFlipper) findViewById(R.id.flipper);
		
		
		//����
		flipper.setInAnimation(AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.push_left_in));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.push_left_out));
		setIndex(flipper);
		
		// �����ͼ
		flipper.addView(addImageById(R.drawable.a1));
		flipper.addView(addImageById(R.drawable.a2));
		flipper.addView(addImageById(R.drawable.a3));
		flipper.addView(addImageById(R.drawable.a4));
		flipper.addView(addImageById(R.drawable.a5));
		flipper.addView(addImageById(R.drawable.a6));
		//ָʾ��־��ͼ
		mIndicator = (LinearLayout) findViewById(R.id.gallery_indicator);
		indiViews = new ImageView[mImageCounts];
		for (int i = 0; i < mImageCounts; i++) {
			indiViews[i] = new ImageView(getApplicationContext());
			indiViews[i].setBackgroundResource(R.drawable.ty_round);
			mIndicator.addView(indiViews[i]);
		}
		
		//�����¼�
		flipper.setOnTouchListener(new BannerOnTouchListener());
		
		//�Զ�����
		flipper.setAutoStart(true);
		flipper.startFlipping();
		updateIndiViewStatus(0);
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

	public View addImageById(int id) {
		ImageView iv = new ImageView(this);
		iv.setImageResource(id);

		return iv;
	}

	private class BannerOnTouchListener implements View.OnTouchListener {
		float e1X;
		float e1Y;
		float e2X;
		float e2Y;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				flipper.stopFlipping();
				e1X = event.getX();
				e1Y = event.getY();
				Log.i("zzw", "X1 = "+e1X+" Y1 = "+e1Y);
				return true;
			case MotionEvent.ACTION_UP:
				e2X = event.getX();
				if (e2X - e1X > 100) { // �������һ���������ҳ���
					Animation rInAnim = AnimationUtils.loadAnimation(getApplicationContext(),
							R.anim.push_right_in); // ���һ���������Ľ���Ч����alpha 0.1 ->
													// 1.0��
					Animation rOutAnim = AnimationUtils.loadAnimation(getApplicationContext(),
							R.anim.push_right_out); // ���һ����Ҳ໬���Ľ���Ч����alpha 1.0 ->
													// 0.1��

					flipper.setInAnimation(rInAnim);
					flipper.setOutAnimation(rOutAnim);
					setIndex(flipper);
					flipper.showPrevious();
					updateIndiViewStatus(flipper.getDisplayedChild());
					flipper.startFlipping();
					return true;
				} else if (e2X - e1X < -100) { // �������󻬶����ҽ������
					Animation lInAnim = AnimationUtils.loadAnimation(getApplicationContext(),
							R.anim.push_left_in); // ���󻬶�������Ľ���Ч����alpha 0.1 -> 1.0��
					Animation lOutAnim = AnimationUtils.loadAnimation(getApplicationContext(),
							R.anim.push_left_out); // ���󻬶��Ҳ໬���Ľ���Ч����alpha 1.0 ->
													// 0.1��

					flipper.setInAnimation(lInAnim);
					flipper.setOutAnimation(lOutAnim);
					setIndex(flipper);
					flipper.showNext();
					updateIndiViewStatus(flipper.getDisplayedChild());
					flipper.startFlipping();
					return true;
				}else{
					Toast.makeText(getApplicationContext(), "zzzz", Toast.LENGTH_SHORT).show();
				}
			}
			return true;
		}

	}
	
	
	private void setIndex(final ViewFlipper viewFlipper){
		Animation inAnim = viewFlipper.getInAnimation();
		// ����ͼƬָʾ
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
	
}