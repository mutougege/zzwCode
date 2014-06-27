package com.zzw.day140618_notification;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	TextView tv_downloadURL;
	Button btn_download;
	Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mHandler = new Handler();
		tv_downloadURL = (TextView) findViewById(R.id.download_url_tv);
		btn_download = (Button) findViewById(R.id.download_btn);
		btn_download.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppFileDownUtils appFileDownUtils = new AppFileDownUtils(
						getApplicationContext(), mHandler, tv_downloadURL
								.getText().toString(), "kuwo.apk");
				
				appFileDownUtils.start();
			}
		});
	}

}
