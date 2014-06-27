package com.popcorn.autosms.service;

import java.util.LinkedList;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.SmsManager;

import com.popcorn.autosms.constant.IntentKey;
import com.popcorn.autosms.reply.DHSMSSentReceiver;
import com.popcorn.autosms.utils.SqlHelper;

public class SMSSendService extends Service {
	public static LinkedList<String> lstAddress = new LinkedList<String>();
	public static boolean isThreadStart = false;
	private SmsManager mSms = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}// end event onBind

	@Override
	public void onCreate() {
		DHSMSSentReceiver.RegisterSmsSentReceiver(this);
		this.mSms = SmsManager.getDefault();
		super.onCreate();
	}// end event onCreate

	@Override
	public void onDestroy() {
		DHSMSSentReceiver.UnRegisterSmsSentReceiver(this);
		super.onDestroy();
	}// end event onDestroy

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String address = intent.getStringExtra(IntentKey.SMS_ADDRESS);

		do {
			if (address == null)
				break;

			SMSSendService.PutAddressToList(address);

			if (SMSSendService.isThreadStart)
				break;
			SMSSendService.isThreadStart = true;
			new Thread(new TaskSendSMS()).start();
		} while (false);

		return super.onStartCommand(intent, flags, startId);
	}// end event onStartCommand

	public synchronized static void PutAddressToList(String address) {
		SMSSendService.lstAddress.addLast(address);
	}// end static function PutAddressToList

	public synchronized static String GetAddressFromList() {
		if (SMSSendService.lstAddress.isEmpty())
			return null;
		return SMSSendService.lstAddress.removeFirst();
	}// end static function GetAddressFromList

	private class TaskSendSMS implements Runnable {
		public void run() {
			SqlHelper helper=new SqlHelper(SMSSendService.this);
			SMSSendService.isThreadStart=true;
			
			//回复短信内容
			SharedPreferences prefs = getApplicationContext().getSharedPreferences("reply", 0);
			String smsCnt = prefs.getString("replyMessage", "");
			if(smsCnt == null || smsCnt == ""){
				smsCnt = "谢谢！";
			}
			while(!SMSSendService.lstAddress.isEmpty()){
				String address=SMSSendService.GetAddressFromList();
				if(address==null) continue;
				
				PendingIntent mPi=PendingIntent.getBroadcast(SMSSendService.this,
						                                     0,
						                                     new Intent(DHSMSSentReceiver.ACTION_SMS_SENT),
						                                     0);
				SMSSendService.this.mSms.sendTextMessage(address, 
						                                 null, 
						                                 smsCnt, 
						                                 mPi, 
						                                 null);
				//helper.InsertAddress(address);
				
				ContentValues values = new ContentValues();  
				              //发送时间  
				values.put("date", System.currentTimeMillis());   
				              //阅读状态              
				values.put("read", 0);             
				             //1为收 2为发             
			    values.put("type", 2);           
				              //送达号码              
				values.put("address",address);             
				              //送达内容            
				values.put("body", smsCnt);
				getContentResolver().insert(Uri.parse("content://sms/sent"), values);  
				
				if(SMSSendService.lstAddress.isEmpty()) break;
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			
			
			helper.Destory();
			SMSSendService.isThreadStart=false;
		}
	}// end inner class TaskSendSMS
}
