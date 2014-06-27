package com.zzw.day140613_loagingnetwork;

import java.util.Random;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FullScreenLoadingView extends LinearLayout{
	
	private static final int SHOW_MSG_ID = 1001;		
	private LayoutInflater layoutInflater;
	private LinearLayout rootLinearLayout;
	//private LinearLayout msgLinearLayout;
	private TextView loadingMsg;
	
	private boolean isRootLayoutVisibility = true;
	
	public FullScreenLoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		layoutInflater = LayoutInflater.from(context);
		rootLinearLayout = (LinearLayout)layoutInflater.inflate(R.layout.fullscreen_loading_indicator, this, true);
		//msgLinearLayout = (LinearLayout)rootLinearLayout.findViewById(R.id.fullscreen_loading_indicator_msg);
		loadingMsg = (TextView)rootLinearLayout.findViewById(R.id.fullscreen_loading_indicator_msg_textview);
		
		showMsgHandler.sendEmptyMessageDelayed(SHOW_MSG_ID, 100);
	}

	public FullScreenLoadingView(Context context) {
		this(context, null);
	}
	
	@Override
	public void setVisibility(int visibility) {
		if(visibility == View.GONE){
			isRootLayoutVisibility = false;
		}else if(visibility == View.VISIBLE){
			isRootLayoutVisibility = true;
		}
		super.setVisibility(visibility);
	}
	
	private Handler showMsgHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SHOW_MSG_ID:
				
				if(isRootLayoutVisibility && rootLinearLayout != null){
					//msgLinearLayout.setVisibility(View.VISIBLE);
					loadingMsg.setText(getLoadingMsgIdByRandom());
					//msgLinearLayout.setVisibility(View.VISIBLE);
				}
					
				break;
			default:
				break;
			}
		};
	};
	
	
	
	//随机提示信息的id
	private String getLoadingMsgIdByRandom(){
		String idstr;
		String [] idArry = {"LOADING" };
	
		Random random = new Random();
		int i = Math.abs(random.nextInt() % idArry.length);
		idstr = idArry[i];
		
		return idstr;
	}
}
