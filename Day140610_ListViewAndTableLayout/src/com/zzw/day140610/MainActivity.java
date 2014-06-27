package com.zzw.day140610;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	List<GiftBagBean> listData;
	ListView lv;
	MyAdapter myAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		listData = new ArrayList<GiftBagBean>();
		for(int i=1;i<8;i++){
			
			GiftBagBean tmp = new GiftBagBean();
			tmp.num = i;
			tmp.name = "haha"+i;
			tmp.draw = new boolean[i];
			for(int j=0;j<i;j++){
				tmp.draw[j]=false;
			}
			
			tmp.codes = new String[i];
			for(int j=0;j<i;j++){
				tmp.codes[j] = "";
			}
			
			listData.add(tmp);
		}
		
		lv = (ListView) findViewById(R.id.lv);
		
		myAdapter = new MyAdapter(listData, getApplicationContext());
		lv.setAdapter(myAdapter);
	}

	
	class GiftBagBean {
		int num;
		String name;
		boolean []draw;
		String []codes;
	}
	
}
