package com.popcorn.autosms.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.popcorn.autosms.constant.IntentKey;
import com.popcorn.autosms.constant.TaskCode;
import com.popcorn.autosms.reply.DHSMSObserver;

public class MainService extends Service {
    public static DHSMSObserver smsObserver=null;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
	}//end event onCreate

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		
		int taskId=intent.getIntExtra(IntentKey.TASK, 0);
		
		Log.i("test", "taskId "+taskId);
		
		switch (taskId) {
		    case TaskCode.TASK_START_OBSERVER:
			    this.ProcSmsObserver(true);
			    //Toast.makeText(this, "短信自动回复已经启动", Toast.LENGTH_SHORT).show();
			    break;
		    case TaskCode.TASK_STOP_OBSERVER:
			    this.ProcSmsObserver(false);
			    //Toast.makeText(this, "短信自动回复已经关闭", Toast.LENGTH_SHORT).show();
			    break;
		    default:
			    break;
		}
		return super.onStartCommand(intent, flags, startId);
	}//end event onStartCommand
	
	private void ProcSmsObserver(boolean isStart){
		if(isStart){
			if(MainService.smsObserver==null){
				MainService.smsObserver=new DHSMSObserver(new Handler(),this);
			}
			MainService.smsObserver.Register();
			Log.i("test","短信自动回复已经启动");
			Toast.makeText(this, "短信自动回复已经启动", Toast.LENGTH_SHORT).show();
		}else{
			if(MainService.smsObserver==null) return;
			MainService.smsObserver.UnRegister();
			MainService.smsObserver=null;
			Log.i("test","短信自动回复已经关闭");
			Toast.makeText(this, "短信自动回复已经关闭", Toast.LENGTH_SHORT).show();
		}
		
	}//end function ProcSmsObserver
	
}
