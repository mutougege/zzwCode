package com.zzw.day140607.contentobserver;

import com.zzw.day140607.contentprovider.StuInfos;

import android.content.Context;  
import android.database.ContentObserver;  
import android.database.Cursor;  
import android.net.Uri;  
import android.os.Handler;  
import android.util.Log;  
  

public class MyContentObserver extends ContentObserver {  
    
      
    private Context mContext;  
    public static boolean isObserverReg=false;
    
    private final int TABLEEMPTY = 0;
    private Handler mHandler;   //更新UI线程  
      
    public MyContentObserver(Context context,Handler handler) {  
        super(handler);  
        mContext = context ;  
        mHandler = handler ;  
    }  
    
    
    
    /** 
     * 当所监听的Uri发生改变时，就会回调此方法 
     * @param selfChange  此值意义不大 一般情况下该回调值false 
     */  
    @Override  
    public void onChange(boolean selfChange){  
          
    	Log.i("zzw", "onChange()");
    	
        //查询是否为空     
        Cursor cursor = mContext.getContentResolver().query(StuInfos.CONTENT_URI, null, null, null ,null);  
        if(cursor != null){  
        	if(!cursor.moveToNext()){
        		Log.i("zzw", "empty = true") ;  
            	mHandler.obtainMessage(TABLEEMPTY).sendToTarget(); 
                cursor.close();            
        	}
        }  
    }
    
    
    public void Register(){
		if(this.mContext==null) return;
		if(this.isObserverReg) return;
		this.mContext.getContentResolver().registerContentObserver(StuInfos.CONTENT_URI, true, this);
		this.isObserverReg=true;
	}//end function Register
	
	public void UnRegister(){
		if(this.mContext==null) return;
		if(!this.isObserverReg) return;
		this.mContext.getContentResolver().unregisterContentObserver(this);
		this.isObserverReg=false;
	}//end function UnRegister
      
} 