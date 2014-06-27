package com.zzw.day140620_gallery;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class MainActivity extends Activity {

	SlowSpeedGallery mGallery;
	LinearLayout mIndicator;
	ImageView[] indiViews;
	private static final int mImageCounts = 6;
	private int[] myImageIds = { R.drawable.a1, R.drawable.a2,
			R.drawable.a3, R.drawable.a4, R.drawable.a5, R.drawable.a6, };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mGallery = (SlowSpeedGallery) findViewById(R.id.app_gallery);
		mGallery.setAdapter(new ImageAdapter(this,myImageIds));
		mGallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v,int position, long id) {
				if (mImageCounts != 0) {
					//imageViewInAniamtion(position);
					updateIndiViewStatus(position);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		mGallery.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView parent, View v, int position,
					long id) {
				Toast.makeText(MainActivity.this, "ͼƬ" + (position % mImageCounts),
						Toast.LENGTH_SHORT).show();
			}
		});

		mIndicator = (LinearLayout) findViewById(R.id.gallery_indicator);
		indiViews = new ImageView[mImageCounts];
		for (int i = 0; i < mImageCounts; i++) {
			indiViews[i] = new ImageView(getApplicationContext());
			indiViews[i].setBackgroundResource(R.drawable.ty_round);
			mIndicator.addView(indiViews[i]);
		}

		//init();
	}

	

	private void updateIndiViewStatus(int position) {
		position = position % mImageCounts;
		if (indiViews == null || position < 0 || position + 1 > indiViews.length) {
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

	public class ImageAdapter extends BaseAdapter {

		private Context mContext;
		int []myImageIds;
		public ImageAdapter(Context c,int []myImageIds) {
			mContext = c;
			this.myImageIds = myImageIds;
		}

		public int getCount() {
			return Integer.MAX_VALUE;
			// return myImageIds.length;
		}

		public Object getItem(int position) {
			return myImageIds[position];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			 ImageView imageView;
             if (convertView == null) {
                     imageView = new ImageView(mContext);
                     imageView.setScaleType(ImageView.ScaleType.FIT_XY);
             } else {
                     imageView = (ImageView) convertView;
             }

             imageView.setImageResource(myImageIds[position % myImageIds.length]);

             return imageView;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/*private static final int timerAnimation = 1;
	private final Handler mHandler = new Handler() {

		public void handleMessage(Message message) {
			super.handleMessage(message);
			switch (message.what) {
			case timerAnimation:
				int position = mGallery.getSelectedItemPosition();
				//imageViewOutAniamtion(position);
				mGallery.setSelection(position + 1);
				break;
			}
		}
	};

	public void imageViewOutAniamtion(int position) {
		position = position % mImageCounts;
		ImageView img = (ImageView) mGallery.getChildAt(position);
		if (img != null) {
			Log.i("zzw", "imageViewOutAniamtion"+img.getX()+" "+img.getY());
			try {
				img.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,
						R.anim.ty_banner_out_anim));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void imageViewInAniamtion(int position) {
		position = position % mImageCounts;
		ImageView img = (ImageView) mGallery.getChildAt(position);
		if (img != null) {
			Log.i("zzw", "imageViewInAniamtion"+img.getX()+" "+img.getY());
			try {
				img.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,
						R.anim.ty_banner_in_anim));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private void init() {

		final Timer timer = new Timer();

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				mHandler.sendEmptyMessage(timerAnimation);

			}
		}, 2000, 2000);

	}*/
	
	
	
	
	
	

}
