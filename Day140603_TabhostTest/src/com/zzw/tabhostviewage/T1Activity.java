/**
 * Program  : T1Activity.java
 * Author   : qianj
 * Create   : 2012-5-31 下午4:24:32
 *
 * Copyright 2012 by newyulong Technologies Ltd.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of newyulong Technologies Ltd.("Confidential Information").  
 * You shall not disclose such Confidential Information and shall 
 * use it only in accordance with the terms of the license agreement 
 * you entered into with newyulong Technologies Ltd.
 *
 */

package com.zzw.tabhostviewage;

import cn.learn.tabhosttest.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


public class T1Activity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.sub);
		((TextView) findViewById(R.id.tv_show)).setText("11111111111");
	}
}

