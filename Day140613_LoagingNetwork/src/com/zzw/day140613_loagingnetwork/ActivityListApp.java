package com.zzw.day140613_loagingnetwork;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public final class ActivityListApp extends Activity {

	private final String TAG = ActivityListApp.class.getSimpleName();

	//数据计数
	private int pageCount;
	private int startIndex;
	private int total;
	private boolean mReachEnd;
	private boolean inflatingAppList;
	private int mNextIndexStart = 0;
	private static final int COUNT_PER_TIME = 15;

	//加载数据时界面
	private View mLoagdingIndicator;
	
	//ListView末尾界面
	private View mFooterView;
	private View mFooterLoadingView;
	private View mFooterRetryView;
	
	//数据列表界面，适配器，数据
	private ListView mListView;
	private MyAdapter mAppListAdapter;
	private ArrayList<String> mAssetList;

	//网络错误时界面和标记数据
	private View mRetryView;
	private Button mRetryButton;
	private TextView mRetryText;
	private boolean networkFailedBefore = false;
	
	//处理向网络获取数据之后的结果
	private Handler mHttpHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mHttpHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					processHttpError(msg);
					break;
				case 0:
					processHttpResponse(msg);
					break;
				}
			}
		};
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_list);
		mAssetList = new ArrayList<String>();
		initView();
		initData();
	}

	private boolean isNetworkAvailable(ActivityListApp activityListApp) {
		
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isNetworkAvailable(this)) {
			if (networkFailedBefore && startIndex == 0) {
				mRetryText.setText(R.string.connecting);
				initData();
				networkFailedBefore = false;
			}else{
				mLoagdingIndicator.setVisibility(View.VISIBLE);
			}
		} else {
			if (mListView.getAdapter() != null
					&& !mListView.getAdapter().isEmpty()) {
				// ignore, just show the cache
				networkFailedBefore = true;
			} else {
				mLoagdingIndicator.setVisibility(View.GONE);
				mListView.setVisibility(View.GONE);
				mRetryText.setText(R.string.offline_hint);
				mRetryView.setVisibility(View.VISIBLE);
				networkFailedBefore = true;
			}
		}
		if (mAppListAdapter != null) {
			mAppListAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onSearchRequested() {
		return false;
	}

	protected void initView() {
		
		mLoagdingIndicator = findViewById(R.id.mygiftbag_fullscreen_loading_indicator);
		
		mFooterView = LayoutInflater.from(this).inflate(R.layout.asset_list_footer, null);
		mFooterLoadingView = mFooterView.findViewById(R.id.loading_layout);
		mFooterRetryView = mFooterView.findViewById(R.id.footer_retry_view);
		mFooterRetryView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFooterRetryView.setVisibility(View.GONE);
				mFooterLoadingView.setVisibility(View.VISIBLE);
				inflateAppList(0);
			}
		});
		
		mRetryView = findViewById(R.id.retry_layout);
		mRetryText = (TextView) findViewById(R.id.offline_hint);
		mRetryButton = (Button) findViewById(R.id.retry_button);
		mRetryButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isNetworkAvailable(ActivityListApp.this)) {
					mRetryText.setText(R.string.connecting);
					initData();
					networkFailedBefore = false;
				} else {
					Toast.makeText(getApplicationContext(),
							R.string.retry_failed, Toast.LENGTH_SHORT).show();
				}
			}
		});


		mListView = (ListView) findViewById(R.id.mygiftbag_lv);
		mListView.addFooterView(mFooterView);
		mListView.setOnScrollListener(scrollListener);
		if (mAppListAdapter == null) {
			mAppListAdapter = new MyAdapter(getApplicationContext(), mAssetList);
		}
		mListView.setAdapter(mAppListAdapter);
		mAppListAdapter = null;
	}

	protected void initData() {

		ArrayList<String> cacheList = null;
		//去缓存取数据

		if (cacheList != null && cacheList.size() > 0) {
			mNextIndexStart += COUNT_PER_TIME;

			if (mNextIndexStart + 1 >= total) {
				mReachEnd = true;
			}
			//拿到缓存数据，就将数据装入adapter
			processAppListHandler(cacheList);
			cacheList.clear();
			cacheList = null;
		} else {
			//缓存没有数据，去HTTP拿数据
			inflateAppList(0);
		}
	}

	private void inflateAppList(int k) {
		if (mReachEnd) {
			return;
		}
		Log.e(TAG, "inflateAppList = "+inflatingAppList);
		if (!inflatingAppList) {
			
			if (mFooterLoadingView.getVisibility() != View.VISIBLE) {
				mFooterLoadingView.setVisibility(View.VISIBLE);
			}
			inflatingAppList = true;
			//去HTTP拿数据，并将handler传给线程。
			Log.i(TAG, "mNextIndexStart = "+mNextIndexStart);
			ThreadLoadApps mThreadLoadApps = new ThreadLoadApps(mNextIndexStart, COUNT_PER_TIME, mHttpHandler , k);
			mThreadLoadApps.start();
		}
	}
	private class ThreadLoadApps extends Thread {

		Handler  mHandler;
		int index;
		int count;
		
		//测试用
		int k;
		ThreadLoadApps(int index, int count, Handler mHandler, int k){
			this.index = index;
			this.count = count;
			this.mHandler = mHandler;
			this.k = k;
		}
		@Override
		public void run() {
			try {
				// 耗时操作
				ArrayList<String> listData = new ArrayList<String>();
				
				for (int i = startIndex; i < startIndex+count & i<23; i++) {
					listData.add(i + "dfdfsfsd" + i);
				}
				sleep(1000);// 毫秒
				Message message = new Message();
				message.what = k;
				message.obj = listData;
				message.arg1 = 23;
				mHandler.sendMessage(message);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

	@SuppressWarnings("unchecked")
	protected void processHttpResponse(Message msg) {
		ArrayList<String> assets = null;
		assets = (ArrayList<String>) msg.obj;
		
		total = (Integer) msg.arg1;
		mNextIndexStart += COUNT_PER_TIME;
		if (assets != null && assets.size() >= total
				|| mNextIndexStart + 1 >= total ) {
			mReachEnd = true;
		}
		if (mFooterRetryView.getVisibility() == View.VISIBLE) {
			mFooterRetryView.setVisibility(View.GONE);
		}

		processAppListHandler(assets);

		inflatingAppList = false;


		if (assets != null) {
			assets.clear();
			assets = null;
		}
	}


	private void processAppListHandler(ArrayList<String> assets) {
		int assetsRealSize = (assets == null ? 0 : assets.size());

		if (mAssetList == null) {
			if (assets != null) {
				mAssetList = new ArrayList<String>(assets);
			} else {
				mAssetList = new ArrayList<String>();
			}
		} else {
			if (assets != null)
				mAssetList.addAll(assets);
		}
		mAssetList.trimToSize();
		if (mAppListAdapter == null) {
			mAppListAdapter = new MyAdapter(getApplicationContext(), mAssetList);
			mListView.setAdapter(mAppListAdapter);
		} else {
			mAppListAdapter.setItems(assets);
			mAppListAdapter.notifyDataSetChanged();
		}

		startIndex += assetsRealSize;

		if (mAssetList.isEmpty()) {
			mRetryView.setVisibility(View.GONE);
		}

		mLoagdingIndicator.setVisibility(View.GONE);
		mRetryView.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);
	}

	protected void processHttpError(Message msg) {
		inflatingAppList = false;
		if (mLoagdingIndicator.getVisibility() == View.VISIBLE) {

			mLoagdingIndicator.setVisibility(View.GONE);
			mRetryView.setVisibility(View.VISIBLE);
			networkFailedBefore = true;
		} else {
			// Just discard the error message
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
			if (mAssetList == null||mAssetList.size()==0)
				return;

			if (mReachEnd) {
				mFooterView.setVisibility(View.GONE);
				mListView.removeFooterView(mFooterView);
				return;
			}
			
			Log.d(TAG, "VisiblePosition = "+view.getLastVisiblePosition());

			boolean scrollEnd = false;

			int itemCount = 0;
			itemCount = mAssetList.size();

			if (itemCount - view.getLastVisiblePosition() <= 2) {
				
				scrollEnd = true;
			}
			if (scrollEnd
					&& !(mFooterRetryView.getVisibility() == View.VISIBLE)) {
				Log.d(TAG, "onScroll scrollEnd this");
				inflateAppList(1);
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

		}
	};

}
