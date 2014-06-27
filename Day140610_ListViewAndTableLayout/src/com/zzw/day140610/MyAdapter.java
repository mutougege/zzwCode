package com.zzw.day140610;

import java.util.ArrayList;
import java.util.List;

import com.zzw.day140610.MainActivity.GiftBagBean;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MyAdapter extends BaseAdapter {

	// 填充数据的list
	private ArrayList<GiftBagBean> list;
	// 上下文
	private Context context;
	// 用来导入布局
	private LayoutInflater inflater = null;

	// 构造器
	public MyAdapter(List<GiftBagBean> list, Context context) {
		this.context = context;
		this.list = (ArrayList<GiftBagBean>) list;
		inflater = LayoutInflater.from(context);
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
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item, null);

			holder.layout_app = (RelativeLayout) convertView
					.findViewById(R.id.app_layout);
			holder.tv_appName = (TextView) convertView
					.findViewById(R.id.app_name_tv);

			holder.layout_giftbag = (TableLayout) convertView
					.findViewById(R.id.giftbag_layout);
			holder.tv_giftbagDescs = new ArrayList<TextView>();
			holder.btn_giftbagDraws = new ArrayList<Button>();
			holder.layout_drawGiftbags = new ArrayList<LinearLayout>();

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		
		
		
		
		holder.tv_appName.setText(list.get(position).name);
		
		int num = list.get(position).num;
		int nnn = holder.layout_giftbag.getChildCount();
		
		
		if(num > nnn){
			Log.i("zzw", "num > nnn "+num+" > "+nnn );
			addTableView(0, nnn, holder,position);
			
			for(int i =nnn;i<num;i++){
				TableRow tr = (TableRow) inflater.inflate(R.layout.asset_giftbag_info, null);
				
				holder.tv_giftbagDescs.add((TextView) tr.findViewById(R.id.giftbag_desc_tv));
				
				holder.btn_giftbagDraws.add((Button) tr.findViewById(R.id.giftbag_draw_btn));
				
				holder.layout_drawGiftbags.add((LinearLayout)tr.findViewById(R.id.draw_giftbag_layout));
				
				holder.layout_giftbag.addView(tr, new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
						
			}
			addTableView(nnn, num, holder,position);
			
			
			
		}else if(num == nnn){
			Log.i("zzw", "num = nnn "+num+" = "+nnn );
			addTableView(0, num, holder,position);
			
		}else if(num < nnn){
			Log.i("zzw", "num < nnn "+num+" < "+nnn );
			addTableView(0, num, holder,position);
			for(int i =num;i<nnn;i++){
				holder.layout_giftbag.getChildAt(i).setVisibility(View.GONE);
			}
			
		}

		return convertView;
	}
	
	public void addTableView(int s, int e, ViewHolder holder,final int position){
		for(int i =s;i<e;i++){
			
			holder.layout_giftbag.getChildAt(i).setVisibility(View.VISIBLE);
			final LinearLayout layout = holder.layout_drawGiftbags.get(i);
			final TextView tv_drawGiftbagCode = (TextView) layout.findViewById(R.id.draw_giftbag_code_tv);
			boolean hasdrwa = list.get(position).draw[i];
			final int k = i;
			
			//既要添加点击事件，还要处理不同界面
			if(hasdrwa){
				layout.setVisibility(View.VISIBLE);
				tv_drawGiftbagCode.setText(list.get(position).codes[i]);
				holder.btn_giftbagDraws.get(i).setClickable(false);
				
			}else{
				layout.setVisibility(View.GONE);
				
				holder.btn_giftbagDraws.get(i).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						layout.setVisibility(View.VISIBLE);
						v.setClickable(false);
						list.get(position).draw[k] = true;
						list.get(position).codes[k] = list.get(position).name+" "+k;
						tv_drawGiftbagCode.setText(list.get(position).codes[k]);
						
						notifyDataSetChanged();
						
						
						layout.findViewById(R.id.draw_giftbag_copy_btn).setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								Toast.makeText(context, tv_drawGiftbagCode.getText(), Toast.LENGTH_SHORT).show();
							}
						});
					}
				});
			}
			
		}
	}

	public static class ViewHolder {
		RelativeLayout layout_app;
		TextView tv_appName;

		TableLayout layout_giftbag;
		
		ArrayList<LinearLayout> layout_drawGiftbags;
		ArrayList<Button> btn_giftbagDraws;
		ArrayList<TextView> tv_giftbagDescs;
		
	}
}