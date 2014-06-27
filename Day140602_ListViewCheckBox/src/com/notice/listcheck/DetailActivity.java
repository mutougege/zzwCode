package com.notice.listcheck;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		String uid = intent.getStringExtra("uid");
		
		setContentView(R.layout.detail);
		TextView tv_uid = (TextView) findViewById(R.id.tv_uid);
		tv_uid.setText("ÓÃ»§ID"+uid);
		
	}

}
