package com.zzw.day140607;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.zzw.day140607.MyAdapter.ViewHolder;
import com.zzw.day140607.contentobserver.MyContentObserver;
import com.zzw.day140607.contentprovider.StuInfos;

public class MainActivity extends Activity {

	private ListView lv;
	private ImageView iv;
	private MyAdapter mAdapter;
	private ArrayList<String> list;

	private final int TABLEEMPTY = 0;
	private ContentResolver contentResolver;
	private MyContentObserver myContentObserver;

	public boolean initData() {
		 

		// Uri uri = ContentUris.withAppendedId(StuInfos.CONTENT_URI, 1);
		Uri uri1 = StuInfos.CONTENT_URI;
		// Cursor cursor = contentResolver.query(uri1, null,
		// " "+StuInfos.USERNAME+" = ?", new String[]{"zzw"}, null);
		Cursor cursor = contentResolver.query(uri1, null, null, null, null);
		
		if(cursor != null){  
        	while (cursor.moveToNext()) {
    			list.add(cursor.getString(cursor.getColumnIndex(StuInfos.USERNAME)));
    		}
        }
		cursor.close(); // 查找后关闭游标
		return true;
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		lv = (ListView) findViewById(R.id.lv);
		iv =  (ImageView) findViewById(R.id.iv);
		list = new ArrayList<String>();

		contentResolver = getContentResolver();
		myContentObserver = new MyContentObserver(getApplicationContext(), mHandler);
		myContentObserver.Register();

		initData();
		if(list.size() == 0 ||list == null){
			Log.i("zzw", list.size()+" ");
			lv.setVisibility(View.GONE);
			iv.setVisibility(View.VISIBLE);
		}

		mAdapter = new MyAdapter(list, this);
		lv.setAdapter(mAdapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				/*
				 * ListView listView = (ListView)parent;
				 * listView.getItemAtPosition(position);
				 */

				ViewHolder holder = (ViewHolder) view.getTag();
				String name = (String) holder.tv.getText();
				Uri uri1 = StuInfos.CONTENT_URI;

				// 三步操作必须有，否则界面不刷新
				list.remove(position);
				contentResolver.delete(uri1, StuInfos.USERNAME + " = ?",
						new String[] { name });
				mAdapter.notifyDataSetChanged();

				Toast.makeText(getApplicationContext(), name,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {
			case TABLEEMPTY:
				Log.i("zzw", "handler1");
				recreate();
				Log.i("zzw", "handler2");
				break;
			default:
				break;
			}
		}
	};

}