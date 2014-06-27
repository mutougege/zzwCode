package com.notice.listcheck;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.notice.listcheck.MyAdapter.ViewHolder;

public class Ex_checkboxActivity extends Activity {

	private ListView lv;
	private MyAdapter mAdapter;
	private ArrayList<String> list;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		lv = (ListView) findViewById(R.id.lv);
		list = new ArrayList<String>();

		// 为Adapter准备数据
		for (int i = 0; i < 15; i++) {
			list.add("data" + " " + i);
		}
		// 实例化自定义的MyAdapter
		mAdapter = new MyAdapter(list, this);
		// 绑定Adapter
		lv.setAdapter(mAdapter);

		// 绑定listView的监听器
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
                    long arg3) {  
                // 跳入下一页
				ViewHolder holder = (ViewHolder) arg1.getTag();
				String uid = (String) holder.tv.getText();
				
				Intent intent = new Intent();
				intent.putExtra("uid", uid);
				intent.setClass(getApplicationContext(), DetailActivity.class);
				startActivity(intent);
  
            } 
		});
	}

}