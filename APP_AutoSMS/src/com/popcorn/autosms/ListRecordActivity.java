package com.popcorn.autosms;

import java.util.ArrayList;
import java.util.List;

import com.popcorn.autosms.reply.MessageRecord;
import com.popcorn.autosms.service.GetRecord;
import com.popcorn.autosms.utils.SqlHelper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ListRecordActivity extends Activity {
	
	private void refresh() {  
         finish();  
        Intent intent = new Intent(ListRecordActivity.this,ListRecordActivity.class);  
         startActivity(intent);  
	   }  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_record);
		
		List<MessageRecord> list = new GetRecord(this.getApplicationContext()).getRecord();
		if(list != null && !list.isEmpty()){
			SmsAdapter adapter=new SmsAdapter((ArrayList<MessageRecord>) list);
			ListView lvSms=(ListView)this.findViewById(R.id.lst_sms);
			lvSms.setAdapter(adapter);
		}
    	
    	Button clear  = (Button) this.findViewById(R.id.btn_list_clear);
    	clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {	
				SqlHelper helper=new SqlHelper(v.getContext());
				helper.ClearListRecord();
				helper.Destory();
				refresh();
			}
		});
		
		
		
		/*
		EditText text = (EditText) this.findViewById(R.id.test);
		List<MessageRecord> list = new GetRecord(this.getApplicationContext()).getRecord();
		String record = "" ;
		for(MessageRecord m : list){
			record = "fgdfg"+m.getAddress()+m.getBody()+m.getPerson()+m.getTime();
			record  = record +"fgdfg"+m.getAddress()+m.getBody()+m.getPerson()+m.getTime();
		}
		System.out.println(record);
		text.setText(record);*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_record, menu);
		
		return true;
	}
	public class SmsAdapter extends BaseAdapter{
	    public ArrayList<MessageRecord> mList=null;
		public SmsAdapter(ArrayList<MessageRecord> l){
			this.mList=l;
		}
		
		public int getCount() {
			return this.mList.size();
		}

		public Object getItem(int position) {
			return this.mList.get(position);
		}

		public long getItemId(int position) {
			return this.mList.get(position).getId();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				convertView=View.inflate(ListRecordActivity.this, 
						                 R.layout.sms_row, 
						                 null);
			}
			View v=convertView;
			
			TextView tvAddress=(TextView)v.findViewById(R.id.tv_address);
			String txtTmp=this.mList.get(position).getAddress()+
			           " ("+this.mList.get(position).getPerson()+")";
			 txtTmp+="已回复";
			
			tvAddress.setText(txtTmp);
			
			TextView tvBody=(TextView)v.findViewById(R.id.tv_body);
			tvBody.setText(this.mList.get(position).getBody());
			TextView tvTime=(TextView)v.findViewById(R.id.tv_time);
			tvTime.setText(this.mList.get(position).getTime());
			return v;
		}
	}//end inner class SMSAdpater
}
