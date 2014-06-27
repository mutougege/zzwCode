package com.popcorn.autosms.reply;

import java.util.ArrayList;
import java.util.HashMap;

import com.popcorn.autosms.utils.SqlHelper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.StaticLayout;
import android.util.Log;

public class DHSMS {
	public static final int TYPE_ALL = 0;
	public static final int TYPE_SENT = 1;
	public static final int TYPE_RECEIVE = 2;

	public static final String URI_SMS_ALL = "content://sms/";
	public static final String URI_SMS_SENT = "content://sms/sent/";
	public static final String URI_SMS_RECEIVE = "content://sms/inbox/";

	private String mUri = DHSMS.URI_SMS_ALL;
	private Context mContext = null;
	private ArrayList<DHSMS.SMSContent> mContentList = new ArrayList<DHSMS.SMSContent>(
			30);
	private HashMap<String, String> mHashPhones = new HashMap<String, String>();

	public DHSMS(int t, Context win) {
		switch (t) {
		case DHSMS.TYPE_ALL:
			this.mUri = DHSMS.URI_SMS_ALL;
			break;
		case DHSMS.TYPE_SENT:
			this.mUri = DHSMS.URI_SMS_SENT;
			break;
		case DHSMS.TYPE_RECEIVE:
			this.mUri = DHSMS.URI_SMS_RECEIVE;
			break;
		default:
			this.mUri = DHSMS.URI_SMS_ALL;
			break;
		}
		this.mContext = win;
	}// end constructor DHSMS

	public ArrayList<DHSMS.SMSContent> GetSMSList() {
		return this.mContentList;
	}// end function GetSMSList

	public void FetchSMSFromDb(int limit) {
		this.mContentList.clear();
		SqlHelper helper = new SqlHelper(this.mContext);

		String[] projection = { "_id", "address", "body", "date", "type" };
		
		//得到一条没有未读的，来信。
		String where = "type = 1";
		
		Cursor c = this.mContext.getContentResolver().query(
				Uri.parse(this.mUri), projection, where, null,
				"_id DESC" + " LIMIT " + (limit > 0 ? limit : 20));

		if (c.moveToFirst()) {
			int idIdx = c.getColumnIndex("_id");
			int addressIdx = c.getColumnIndex("address");
			int bodyIdx = c.getColumnIndex("body");
			int dateIdx = c.getColumnIndex("date");
			int typeIdx = c.getColumnIndex("type");

			do {
				SMSContent oSmsCnt = new SMSContent();
				oSmsCnt._id = c.getInt(idIdx);
				oSmsCnt.address = c.getString(addressIdx).replace("+86", "");
				oSmsCnt.body = c.getString(bodyIdx);
				oSmsCnt.person = this.GetPersonByAddress(oSmsCnt.address);
				oSmsCnt.date = c.getLong(dateIdx);
				oSmsCnt.type = c.getInt(typeIdx);
				// oSmsCnt.isReply=helper.IsAddressExist(oSmsCnt.address);

				Log.i("test", "_id " + oSmsCnt._id);
				Log.i("test", "address " + oSmsCnt.address);
				Log.i("test", "body " + oSmsCnt.body);
				Log.i("test", "date " + oSmsCnt.date);
				Log.i("test", "type " + oSmsCnt.type);

				this.mContentList.add(oSmsCnt);
			} while (c.moveToNext());
		}
		c.close();
		helper.Destory();
		Log.i("test", "mContentList.size " + mContentList.size());
	}// end function FetchSMSFromDb

	public void FetchSMSFromDb() {
		this.FetchSMSFromDb(20);
	}// end function FetchSMSFraomDb

	private String GetPersonByAddress(String address) {
		/*
		 * if(!this.mHashPhones.containsKey(address)){
		 * this.mHashPhones.put(address, "陌生人");
		 * 
		 * String[] projection={ ContactsContract.PhoneLookup.DISPLAY_NAME,
		 * ContactsContract.CommonDataKinds.Phone.NUMBER };
		 * 
		 * Cursor c=this.mContext.getContentResolver().query(
		 * ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
		 * ContactsContract.CommonDataKinds.Phone.NUMBER+"='"+address+"'", null,
		 * "_id ASC LIMIT 1" );
		 * 
		 * if(c.moveToFirst()){ int
		 * nameIdx=c.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
		 * this.mHashPhones.put(address, c.getString(nameIdx)); } c.close(); }
		 * 
		 * return this.mHashPhones.get(address);
		 */

		String contactName = "陌生人";
		ContentResolver cr = mContext.getContentResolver();
		Cursor pCur = cr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
				new String[] { address }, null);
		if (pCur.moveToFirst()) {
			contactName = pCur
					.getString(pCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			pCur.close();
			return contactName;
		}
		return contactName;
	}// end function GetPersonByAddress

	public class SMSContent {
		public int _id;
		public String address;
		public String body;
		public String person;
		public long date;
		public boolean isReply;
		public int type;
	}// end inner classSmsContent

	public void updateMessageById(int id, String body) {

		Log.i("test", "修改短信中~~~~");
		ContentValues cv = new ContentValues();
		cv.put("body", body + "\n" + "This Message has been replied.");
		cv.put("read", 1);
		String where = "_id = " + id;
		this.mContext.getContentResolver().update(Uri.parse(this.mUri), cv,
				where, null);
		Log.i("test", "修改完短信");
	}
}
