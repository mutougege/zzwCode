package com.popcorn.autosms.utils;



import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ReplyMessageUtil {
	private static Context context;
	public ReplyMessageUtil(Context applicationContext) {
		// TODO Auto-generated constructor stub
		this.context = applicationContext;
	}
	public Map<String, String> getReplyMessage(String filename) {
		// TODO Auto-generated method stub
		String message = null;
		try{
			SharedPreferences preference  = context.getSharedPreferences(filename, context.MODE_PRIVATE);
			 message = preference.getString("replyMessage", "");
		}catch(Exception e){
			message = "";
		}
		Map<String ,String > map = new HashMap<String, String>();
		map.put("replyMessage", message);
		return map;
		
	}
	public boolean saveContent(String filename, String content) {
		// TODO Auto-generated method stub
		SharedPreferences preference  = context.getSharedPreferences(filename, context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putString("replyMessage", content);
		editor.commit();
		return true;
	}
	
}
