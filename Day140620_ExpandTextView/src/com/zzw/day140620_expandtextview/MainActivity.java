package com.zzw.day140620_expandtextview;


import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private LinearLayout layout_permission_Container;
	private ImageView iv_permission;
	private TextView tv_desc;
	private View view_permission;
	private View layout_permissions_head;
	
	private ScrollView scrollView;
	
	private static final String TAG_EXPANDABLE = "expand";
	private static final String TAG_COLLAPSE = "collapse";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.expand_textview);
		
		scrollView = (ScrollView) findViewById(R.id.asset_info_gallery);
		layout_permission_Container = (LinearLayout)findViewById(R.id.permissions_container);
		iv_permission = (ImageView)findViewById(R.id.expand_permissions);
		layout_permissions_head = findViewById(R.id.permissions_lay);
		tv_desc = (TextView) findViewById(R.id.desc_tv);
		
		
		iv_permission.setTag(TAG_EXPANDABLE);
		layout_permissions_head.setOnClickListener(permisstionViewOnClickListener);
		layout_permission_Container.setOnClickListener(permisstionViewOnClickListener);
		tv_desc.setOnClickListener(permisstionViewOnClickListener);
		
		setupPermissionView();
	}
	
	private OnClickListener permisstionViewOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String tag = (String) iv_permission.getTag();
			if (tag.equals(TAG_EXPANDABLE)) {
				iv_permission.setTag(TAG_COLLAPSE);
				iv_permission.setImageResource(R.drawable.navigation_collapse);
				
				tv_desc.setVisibility(View.GONE);
				
				if (view_permission != null)
					view_permission.setVisibility(View.VISIBLE);
				
				/*scrollView.post(new Runnable() {
					@Override
					public void run() {
						scrollView.scrollTo(0, layout_permissions_head.getTop());
					}
				});*/
			} else if (tag.equals(TAG_COLLAPSE)) {
				iv_permission.setTag(TAG_EXPANDABLE);
				iv_permission.setImageResource(R.drawable.navigation_expandable);
				
				tv_desc.setVisibility(View.VISIBLE);
				
				if (view_permission != null)
					view_permission.setVisibility(View.GONE);
			}
		}
	};

	private void setupPermissionView() {
		TextView tv = new TextView(this);
		tv.setTextSize(14);
		tv.setText("aaaaaaaaaaaaa\nbbbbbbbbbbb\ndaafasdfasdfsadfsafsa\nasdagaasdfasfsadf\nafadfasd\n" +
				"asdfsdfasfdsfsd\nasdfasdfasfsadf\ndaafasdfasdfsadfsafsa\nasdagaasdfasfsadf\nafadfasd\n" +
				"asdfsdfasfdsfsd\nasdfasdfasfsadf\ndaafasdfasdfsadfsafsa\nasdagaasdfasfsadf\nafadfasd\n" +
				"asdfsdfasfdsfsd\nasdfasdfasfsadf\ndaafasdfasdfsadfsafsa\nasdagaasdfasfsadf\nafadfasd\n" +
				"asdfsdfasfdsfsd\nasdfasdfasfsadf\ndaafasdfasdfsadfsafsa\nasdagaasdfasfsadf\nafadfasd\n" +
				"asdfsdfasfdsfsd\nasdfasdfasfsadf\ndaafasdfasdfsadfsafsa\nasdagaasdfasfsadf\nafadfasd\n" +
				"asdfsdfasfdsfsd\nasdfasdfasfsadf\ndaafasdfasdfsadfsafsa\nasdagaasdfasfsadf\nafadfasd\n" +
				"asdfsdfasfdsfsd\nasdfasdfasfsadf\ndaafasdfasdfsadfsafsa\nasdagaasdfasfsadf\nafadfasd\n");
		view_permission = tv;
		layout_permission_Container.addView(view_permission);
		view_permission.setVisibility(View.GONE);
		
		tv_desc.setText("aaaaaaaaaaaaa\nbbbbbbbbbbb\ndaafasdfasdfsadfsafsa\nasdagaasdfasfsadf\nafadfasd\n" +
				"asdfsdfasfdsfsd\nasdfasdfasfsadf\ndaafasdfasdfsadfsafsa\nasdagaasdfasfsadf\nafadfasd\n" +
				"asdfsdfasfdsfsd\nasdfasdfasfsadf\ndaafasdfasdfsadfsafsa\nasdagaasdfasfsadf\nafadfasd\n" +
				"asdfsdfasfdsfsd\nasdfasdfasfsadf\ndaafasdfasdfsadfsafsa\nasdagaasdfasfsadf");
	}
}
