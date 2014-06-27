package com.popcorn.autosms.service;

import java.util.List;

import com.popcorn.autosms.reply.MessageRecord;
import com.popcorn.autosms.utils.SqlHelper;


import android.content.Context;


public class GetRecord {
	private Context context;
	
	public GetRecord(Context context) {
		super();
		this.context = context;
	}

	public List getRecord(){
		SqlHelper helper1=new SqlHelper(this.context);
		List<MessageRecord> record = helper1.QueryMessage();
		helper1.Destory();
		
		return record;
	}
}
