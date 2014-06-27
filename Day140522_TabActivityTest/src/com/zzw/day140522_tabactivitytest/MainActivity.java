package com.zzw.day140522_tabactivitytest;

import android.app.TabActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends TabActivity implements
		TabHost.TabContentFactory {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final TabHost tabHost = getTabHost();
		tabHost.addTab(tabHost
				.newTabSpec("tab1")
				.setIndicator("主页",
						getResources().getDrawable(R.drawable.a))
				.setContent(this));
		tabHost.addTab(tabHost
				.newTabSpec("tab2")
				.setIndicator("标题",
						getResources().getDrawable(R.drawable.a))
				.setContent(this));
		tabHost.addTab(tabHost
				.newTabSpec("tab3")
				.setIndicator("关于",
						getResources().getDrawable(R.drawable.a))
				.setContent(this));
	}

	@Override
	public View createTabContent(String arg0) {
		final TextView tv = new TextView(this);
		tv.setText("Content for tab with tag " + arg0);

		return tv;
	}

}