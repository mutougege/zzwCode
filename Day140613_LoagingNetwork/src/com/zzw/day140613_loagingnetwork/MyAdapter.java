package com.zzw.day140613_loagingnetwork;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

class MyAdapter extends BaseAdapter {

	
	LayoutInflater mInflater;
	Context context;
	ArrayList<String> list;

	public MyAdapter(Context context, ArrayList<String> list) {

		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {

		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void setItems(ArrayList<String> list){
		list.addAll(list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = mInflater.inflate(R.layout.item_list, null);
		TextView tv_item = (TextView) convertView
				.findViewById(R.id.item_tv);
		tv_item.setText(list.get(position));
		return convertView;
	}

}