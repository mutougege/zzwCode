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

		// ΪAdapter׼������
		for (int i = 0; i < 15; i++) {
			list.add("data" + " " + i);
		}
		// ʵ�����Զ����MyAdapter
		mAdapter = new MyAdapter(list, this);
		// ��Adapter
		lv.setAdapter(mAdapter);

		// ��listView�ļ�����
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
                    long arg3) {  
                // ������һҳ
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