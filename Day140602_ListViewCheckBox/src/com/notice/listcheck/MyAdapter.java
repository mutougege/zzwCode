package com.notice.listcheck;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	// ������ݵ�list
	private ArrayList<String> list;
	// ��������CheckBox��ѡ��״��
	private static HashMap<Integer, Boolean> isSelected;
	// ������
	private Context context;
	// �������벼��
	private LayoutInflater inflater = null;

	// ������
	public MyAdapter(ArrayList<String> list, Context context) {
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
		isSelected = new HashMap<Integer, Boolean>();
		// ��ʼ������
		initDate();
	}

	// ��ʼ��isSelected������
	private void initDate() {
		for (int i = 0; i < list.size(); i++) {
			getIsSelected().put(i, false);
		}
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

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		// convertViewΪnull��ʱ���ʼ��convertView��
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.listviewitem, null);
			holder.tv = (TextView) convertView.findViewById(R.id.item_tv);
			holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv.setText(list.get(position));
		holder.cb.setChecked(getIsSelected().get(position));

		final CheckBox checkBox = holder.cb;
		final int arg2 = position;
		checkBox.setChecked(isSelected.get(position));

		checkBox.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// ��¼CheckBox��״̬
				if (isSelected.get(arg2)) {
					isSelected.put(arg2, false);
				} else {
					isSelected.put(arg2, true);
				}
				notifyDataSetChanged();
			}
		});

		return convertView;
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		MyAdapter.isSelected = isSelected;
	}

	public static class ViewHolder {
		TextView tv;
		CheckBox cb;
	}
}