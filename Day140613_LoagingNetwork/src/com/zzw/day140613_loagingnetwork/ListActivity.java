package com.zzw.day140613_loagingnetwork;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ListActivity extends Activity {

	private ArrayList<String> listData;
	private ListView lv_myGiftBag;
	private Handler mHandler;
	FullScreenLoadingView loadingView;
	LayoutInflater mInflater;
	private View mRetryView;
	private TextView mRetryText;
	private Button mRetryButton;
	private View mFooterView;
	private View mFooterLoadingView;
	private View mFooterRetryView;

	public int k;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listData = new ArrayList<String>();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					// 正常列表显示
					loadingView.setVisibility(View.GONE);
					lv_myGiftBag.setVisibility(View.VISIBLE);
					break;
				case 1:
					processHttpError(msg);
					break;
				}
			}
		};

		initView();

		initData(1);

	}

	private void initView() {

		loadingView = (FullScreenLoadingView) findViewById(R.id.mygiftbag_fullscreen_loading_indicator);
		loadingView.setVisibility(View.VISIBLE);
		lv_myGiftBag = (ListView) findViewById(R.id.mygiftbag_lv);
		lv_myGiftBag.setVisibility(View.GONE);

		mFooterView = LayoutInflater.from(this).inflate(
				R.layout.asset_list_footer, null);
		mFooterLoadingView = mFooterView.findViewById(R.id.loading_layout);
		mFooterRetryView = mFooterView.findViewById(R.id.footer_retry_view);

		mFooterRetryView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFooterRetryView.setVisibility(View.GONE);
				mFooterLoadingView.setVisibility(View.VISIBLE);
				initData(0);
			}
		});
		lv_myGiftBag.addFooterView(mFooterView);

		mRetryView = findViewById(R.id.retry_layout);
		mRetryText = (TextView) findViewById(R.id.offline_hint);
		mRetryButton = (Button) findViewById(R.id.retry_button);
		mRetryButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.retry_button:
					boolean network = true;
					if (network) {
						// 有网
						mRetryText.setText(R.string.connecting);
						initData(0);
					} else {
						Toast.makeText(getApplicationContext(),
								R.string.retry_failed, Toast.LENGTH_SHORT)
								.show();
					}
				}
			}
		});

		lv_myGiftBag.setOnScrollListener(scrollListener);
	}

	private void initData(int ds) {

		k = ds;
		ThreadLoadApps mThreadLoadApps = new ThreadLoadApps();
		mThreadLoadApps.start();
		lv_myGiftBag
				.setAdapter(new MyAdapter(getApplicationContext(), listData));
	}

	private class ThreadLoadApps extends Thread {

		@Override
		public void run() {
			try {
				// 耗时操作
				for (int i = 0; i < 15; i++) {
					listData.add(i + "dfdfsfsd" + i);
				}
				sleep(1000);// 毫秒
				mHandler.sendEmptyMessage(k);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

	protected void processHttpError(Message msg) {

		if (loadingView.getVisibility() == View.VISIBLE) {
			loadingView.setVisibility(View.GONE);
			mRetryView.setVisibility(View.VISIBLE);
		} else {
			mRetryText.setText(R.string.offline_hint);
			if (mFooterLoadingView.getVisibility() == View.VISIBLE) {
				mFooterLoadingView.setVisibility(View.GONE);
				mFooterRetryView.setVisibility(View.VISIBLE);
			}
		}
	}

	private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (listData == null)
				return;

			boolean scrollEnd = false;

			int itemCount = 0;
			itemCount = listData.size();

			if (itemCount - view.getLastVisiblePosition() <= 2) {
				scrollEnd = true;
			}
			if (scrollEnd
					&& !(mFooterRetryView.getVisibility() == View.VISIBLE)) {
				initData(1);
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

		}
	};

	
}