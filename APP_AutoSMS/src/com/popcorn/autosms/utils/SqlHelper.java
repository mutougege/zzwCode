package com.popcorn.autosms.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.popcorn.autosms.reply.MessageRecord;



import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class SqlHelper {
	private static final String DB_PATH=Environment.getExternalStorageDirectory().getAbsolutePath()+
                                        "/dxd/reply/reply.db";
    private static final String DB_NAME="reply.db";
    
    private Context context=null;
    private SQLiteDatabase mDb=null;
    private String mUri="content://sms/sent/";
    
    public SqlHelper(Context context){
    	this.context=context;
    	
    	File fDb=new File(SqlHelper.DB_PATH);
    	File pDir=new File(fDb.getParent());
    	
    	if(!pDir.exists()) pDir.mkdirs();
    	if(!fDb.exists()){    //db������
    		this.mDb=SQLiteDatabase.openDatabase(
    			SqlHelper.DB_PATH, 
    			null, 
    			SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.OPEN_READWRITE);

    		this.mDb.execSQL(
        	    	"CREATE TABLE sms_detail(" +
        	    	"id INTEGER PRIMARY KEY AUTOINCREMENT," +
        	    	"person VARCHAR(40), "+
        	    	"address VARCHAR(30),"+
        	    	"time VARCHAR(30),"+
        	    	"body VARCHAR(200))");
    		
    	}
    	
    	this.InitDb();
    }//end constructor SqlHelper
    
  
    
    public void Destory(){
    	if(this.mDb==null) return;
    	this.mDb.close();
    	this.mDb=null;
    }//end function Destroy
    
    private void InitDb(){
    	if(!this.IsDbExists()) return;
    	if(this.mDb!=null) return;
    	this.mDb=SQLiteDatabase.openDatabase(SqlHelper.DB_PATH, 
    			                             null,
    			                             SQLiteDatabase.OPEN_READWRITE);
    }//end function InitDb
    
    private boolean IsDbExists(){
    	File fDb=new File(SqlHelper.DB_PATH);
    	boolean b=fDb.exists();
    	if(!b){
    		Toast.makeText(this.context, "��ݿⲻ����", Toast.LENGTH_SHORT).show();
    	}
    	return b;
    }//end function isDbExists
    
    
  //测试是否已发祝福短信
    public boolean isAddressExist(String address) {
    	
    	boolean isSend = false;
    	
    	String[] projection={
    	    "_id",
    	    "body",
    	    "date",
    	};

    	String where ="address = '"+address+"' and type = 2";
    	Cursor c=this.context.getContentResolver().query(
    			Uri.parse(this.mUri), 
    			projection, 
    			where, 
    			null, 
    			"date DESC"+" LIMIT "+1);
    	Log.i("test","死锁测试"+address);
    	if(c.moveToFirst()){
    		int idIdx=c.getColumnIndex("_id");
    		int dateIdx=c.getColumnIndex("date");
    		int bodyIdx=c.getColumnIndex("body");
    		int _id=c.getInt(idIdx);
    		
    		long date=c.getLong(dateIdx);
    		String body=c.getString(bodyIdx);
    		
    		
    		Date curDate=new Date(System.currentTimeMillis());
    		long diff = curDate.getTime()-date;
	
    		Date d1 = new Time(date);
    		Date d2 = new Time(System.currentTimeMillis());
    		
    		Log.i("test","_id "+_id);
    		Log.i("test","date "+date);
    		Log.i("test","body "+body);
    		Log.i("test","curDate "+curDate.getTime());
    		Log.i("test","Date1 "+d1);
    		Log.i("test","Date2 "+d2);
    		
    		
    		
    		
    		//判断日期是否在五分钟之内
        	if(Math.abs(diff)<120000){
        		//五分钟之内以发送过短信给address，不需要自动回复
        		//没有测试过自己发送出去的短信是否是祝福短信
        		isSend = true;
        		
        	}else{
        		//五分钟之内没发送过短信给address，可以自动回复
        		isSend = false;
        	}
        	
        	/*Calendar date1 = Calendar.getInstance();    
        	date1.set(2008,1,5);    
        	Calendar from = Calendar.getInstance();    
        	from.set(2008,1,1);    
        	Calendar to = Calendar.getInstance();    
        	to.set(2008,1,6);    
        	System.out.println(date1.after(from));    
        	System.out.println(date1.before(to));*/
    	}
    	
    	c.close();
		return isSend;
	}
    

	public void InsertDetail(String person, String address, String body,
			String date) {
		//Date date1 = new Date(date);
		if(address.length()>20) address=address.substring(0, 19);
    	
    	this.mDb.execSQL("REPLACE INTO sms_detail (address,person,body,time)" +
    					" VALUES ('"+address+"',"+"'"+person+"',"+"'"+body+"',"+"'"+date+"')");
		
	}
	

	public List<MessageRecord> QueryMessage(){
		if(!this.IsDbExists()) return null;
		String sql = "select * from sms_detail order by id desc";
		//this.mDb.execSQL(sql);
		Cursor cursor = this.mDb.rawQuery(sql, null);
		List relist = new ArrayList<MessageRecord>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(0);
			String person = cursor.getString(1); //��ȡ��2�е�ֵ,��2�е������0��ʼ
			String address = cursor.getString(2);//��ȡ��3�е�ֵ
			String time = cursor.getString(3);//��ȡ��4�е�ֵ
			String body = cursor.getString(4);
			
			//Date date = new Date(time);
			MessageRecord record = new MessageRecord();
			record.setId(id);
			record.setPerson(person);
			record.setTime(time);
			record.setAddress(address);
			record.setBody(body);
			relist.add(record);
			
		}
		cursor.close();
		this.mDb.close(); 
		return relist;
	}
	
	 public void ClearListRecord(){
	    	if(!this.IsDbExists()) return;
	    	if(this.mDb==null) return;
	    	
	    	this.mDb.execSQL("DELETE FROM sms_detail");
	    }
}
