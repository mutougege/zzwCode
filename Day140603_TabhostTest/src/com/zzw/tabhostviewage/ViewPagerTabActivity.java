/**
 * Program  : ViewPagerActivity.java
 * Author   : qianj
 * Create   : 2012-5-31 下午2:02:15
 */

package com.zzw.tabhostviewage;

import java.util.ArrayList;
import java.util.List;

import com.zzw.rank.RankActivity;

import cn.learn.tabhosttest.R;

import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class ViewPagerTabActivity extends TabActivity {

	Context context = null;

	Intent i1;
	Intent i2;
	Intent i3;

	List<View> listViews;
	private ViewPager pager = null;

	LocalActivityManager manager = null;

	TabHost tabHost = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = ViewPagerTabActivity.this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		tabHost = getTabHost();
		LayoutInflater.from(this).inflate(R.layout.layout_avtivity_main, tabHost.getTabContentView());
		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);

		initIntent();
		initTabHost();
		initViewpage();

	}

	private void initIntent() {

		i1 = new Intent(context, T1Activity.class);
		i2 = new Intent(context, RankActivity.class);
		i3 = new Intent(context, T3Activity.class);
	}

	private void initViewpage() {

		pager = (ViewPager) findViewById(R.id.viewpager);
		// 定放一个放view的list，用于存放viewPager用到的view
		listViews = new ArrayList<View>();

		listViews.add(getView("A", i1));
		listViews.add(getView("B", i2));
		listViews.add(getView("C", i3));

		pager.setAdapter(new MyPageAdapter(listViews));
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// 当viewPager发生改变时，同时改变tabhost上面的currentTab
				tabHost.setCurrentTab(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}

	private void initTabHost() {
		/*
		 * // 这儿主要是自定义一下tabhost中的tab的样式 RelativeLayout tabIndicator1 =
		 * (RelativeLayout)
		 * LayoutInflater.from(this).inflate(R.layout.tabwidget, null);
		 * 
		 * TextView tvTab1 = (TextView)
		 * tabIndicator1.findViewById(R.id.tv_title); tvTab1.setText("第一页");
		 * 
		 * RelativeLayout tabIndicator2 = (RelativeLayout)
		 * LayoutInflater.from(this).inflate(R.layout.tabwidget, null); TextView
		 * tvTab2 = (TextView) tabIndicator2.findViewById(R.id.tv_title);
		 * tvTab2.setText("第二页");
		 * 
		 * RelativeLayout tabIndicator3 = (RelativeLayout)
		 * LayoutInflater.from(this).inflate(R.layout.tabwidget, null); TextView
		 * tvTab3 = (TextView) tabIndicator3.findViewById(R.id.tv_title);
		 * tvTab3.setText("第三页");
		 */

		Intent intent = new Intent(context, EmptyActivity.class);
		tabHost.addTab(tabHost.newTabSpec("A").setIndicator("推荐")
				.setContent(intent));
		tabHost.addTab(tabHost.newTabSpec("B").setIndicator("排行")
				.setContent(intent));
		tabHost.addTab(tabHost.newTabSpec("C").setIndicator("分类")
				.setContent(intent));

		// 点击tabhost中的tab时，要切换下面的viewPager
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {

				if ("A".equals(tabId)) {
					pager.setCurrentItem(0);
				}
				if ("B".equals(tabId)) {

					pager.setCurrentItem(1);
				}
				if ("C".equals(tabId)) {
					pager.setCurrentItem(2);
				}
			}
		});
	}

	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}

}
