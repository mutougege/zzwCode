package com.zzw.day140616_mgnfonttest;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.Menu;
import android.widget.EditText;

public class MainActivity extends Activity {

	private EditText  editText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		editText = (EditText) findViewById(R.id.editText1);
		
		Typeface tf = Utils.getDefaultTypeface(getApplicationContext());
		editText.setTypeface(tf);
	}

}
