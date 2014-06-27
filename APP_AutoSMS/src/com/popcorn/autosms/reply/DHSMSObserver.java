package com.popcorn.autosms.reply;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.popcorn.autosms.constant.IntentKey;
import com.popcorn.autosms.service.SMSSendService;
import com.popcorn.autosms.utils.NumberConvert;
import com.popcorn.autosms.utils.SqlHelper;
import com.popcorn.wordfilter.WordFilter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;


public class DHSMSObserver extends ContentObserver {
    private Context context=null;
    public static boolean isObserverReg=false;
    private long lastSmsDate=0;
    SharedPreferences prefs;
    
	public DHSMSObserver(Handler handler) {
		super(handler);
		
		this.lastSmsDate=System.currentTimeMillis();
	}//end constructor DHSMSObserver

	public DHSMSObserver(Handler handler,Context c){
		super(handler);
		
		this.lastSmsDate=System.currentTimeMillis();
		this.context=c;
	}//end function
	
	@Override
	public void onChange(boolean selfChange) {
		
		prefs=this.context.getSharedPreferences("com.popcorn.autosms_preferences", 0);
		
		if(!prefs.getBoolean("auto_reply", false))return;
		if(this.context==null) return;
		
		DHSMS oSms=new DHSMS(DHSMS.TYPE_RECEIVE,this.context);
		
		oSms.FetchSMSFromDb(1);
		//得到收到短信的id和内容
		
		if(oSms.GetSMSList().size() ==0)return;
		
		int id = oSms.GetSMSList().get(0)._id;
		String body = oSms.GetSMSList().get(0).body;
		String address = oSms.GetSMSList().get(0).address;
		
		if(body.indexOf("This Message has been replied.")!= -1)return;
		if(oSms.GetSMSList().isEmpty()) return;
		if(oSms.GetSMSList().get(0).address==null) return;		
		
		//判断号码是否在选择回复的列表
		String nameList = prefs.getString("select_contacts_list_preference", "");
		if(nameList=="" || nameList==null){
			return;
		}else if( nameList.indexOf(address) == -1){
			return;
		}
				
		if(this.JudgeBody(body)){
			
			//测试是否已发祝福短信
			SqlHelper helper=new SqlHelper(this.context);
			boolean isSent = helper.isAddressExist(address);
			helper.Destory();
			if(isSent) return;  
			
			
			
			
			//将接收短信存到本地数据库中
			
			String body1 = oSms.GetSMSList().get(0).body;
			String person = oSms.GetSMSList().get(0).person;
			SimpleDateFormat  formatter  = new  SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");       
			Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
			String date = formatter.format(curDate);  
			SqlHelper helper1=new SqlHelper(this.context);
			helper1.InsertDetail(person,address,body1,date);
			helper1.Destory();
			
			
			//发送程序进行中，修改短信数据库
		    oSms.updateMessageById(id,body);
			
			
			Intent intent=new Intent(this.context,SMSSendService.class);
		    intent.putExtra(IntentKey.SMS_ADDRESS, oSms.GetSMSList().get(0).address);
		    this.context.startService(intent);
		}
		
		super.onChange(selfChange);
	}//end event onChange

	public void Register(){
		if(this.context==null) return;
		if(DHSMSObserver.isObserverReg) return;
		
		this.context.getContentResolver().registerContentObserver(
			Uri.parse("content://sms/"), 
			true, 
			this
		);
		DHSMSObserver.isObserverReg=true;
	}//end function Register
	
	public void UnRegister(){
		if(this.context==null) return;
		if(!DHSMSObserver.isObserverReg) return;
		
		this.context.getContentResolver().unregisterContentObserver(this);
		DHSMSObserver.isObserverReg=false;
	}//end function UnRegister
	
	public boolean JudgeBody(String body){
		if(this.context==null) return false;
		
		
		
		String festival=prefs.getString("festival_Preference", "");
		String strThrshold=prefs.getString("check_threshold", "0");
		int thrshold=NumberConvert.ConvertToInt(strThrshold);
		
		if(thrshold<=0) thrshold=3;
		if(thrshold>10) thrshold=10;
		
		WordFilter wf = new WordFilter();
		int num = wf.filterWord(body);
		if(num>=thrshold){
			return true;
		}else{
			return false;
		}
	}//end function JudgeBody
}
