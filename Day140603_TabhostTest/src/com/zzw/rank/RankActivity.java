package com.zzw.rank;

import cn.learn.tabhosttest.R;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class RankActivity extends TabActivity implements OnTabChangeListener {

	private TabSpec ts1 ,ts2, ts3 ;
	private TabHost tableHost;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        tableHost = this.getTabHost();
        
        Intent it1 = new Intent();
        it1.setClass(getApplicationContext(), GameListViewActivity.class);
        Intent it2= new Intent();
        it2.setClass(getApplicationContext(), AppsListViewActivity.class);
        Intent it3 = new Intent();
        it3.setClass(getApplicationContext(), LeaderListViewActivity.class);
        
        
        tableHost.addTab(tableHost.newTabSpec("tabOne").setIndicator("游戏").setContent(it1));
        tableHost.addTab(tableHost.newTabSpec("tabTwo").setIndicator("应用").setContent(it2));
        tableHost.addTab(tableHost.newTabSpec("tabThree").setIndicator("风向").setContent(it3));
        
        tableHost.setOnTabChangedListener(this);       
    }
	public void onTabChanged(String tabId){
    	if(tabId.equals("tabOne")){
    		Toast.makeText(this, "游戏", Toast.LENGTH_LONG).show();
    	}
    	if(tabId.equals("tabTwo")){
    		Toast.makeText(this, "应用", Toast.LENGTH_LONG).show();
    	}
    	if(tabId.equals("tabThree")){
    		Toast.makeText(this, "风向", Toast.LENGTH_LONG).show();
    	}
    }
}