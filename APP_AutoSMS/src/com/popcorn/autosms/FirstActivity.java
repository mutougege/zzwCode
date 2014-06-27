package com.popcorn.autosms;





import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class FirstActivity extends Activity {

	Button messageButton;
	Button preferenceButton;
	Button historyButton;
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		messageButton = (Button) findViewById(R.id.message_button);
		preferenceButton = (Button) findViewById(R.id.preference_button);
		historyButton = (Button) findViewById(R.id.history_button);

		messageButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						ReplyTxtWin.class);
				FirstActivity.this.startActivity(intent);
			}
		});
		Button btnTest=(Button)this.findViewById(R.id.history_button);
		btnTest.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent=new Intent(FirstActivity.this,ListRecordActivity.class);
				FirstActivity.this.startActivity(intent);
			}
		});//end btnTxtSettings click
		setListener();
		
	}
	
	//处理跳转到主Activity
    private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	Intent intttt = new Intent(getApplicationContext(),
						PreferencesActivity.class);
                    startActivity(intttt);
                    if (msg.what == 0) {
                        dialog.dismiss();
                }
            }
    };

	/**
	 * 点击按钮事件listener
	 */
	private void setListener() {
		
		preferenceButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
	            dialog = ProgressDialog.show(FirstActivity.this, "加载中...", "请稍后...");
	            Thread thread = new Thread(new Runnable() {
	                    public void run() {
	                            Message message = new Message();
	                            message.what = 0;
	                            mHandler.sendMessage(message);
	                    }
	            });
	            thread.start();
			}
	    });
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		/*
		 * * * add()方法的四个参数，依次是： * * 1、组别，如果不分组的话就写Menu.NONE, * *
		 * 2、Id，这个很重要，Android根据这个Id来确定不同的菜单 * * 3、顺序，那个菜单现在在前面由这个参数的大小决定 * *
		 * 4、文本，菜单的显示文本
		 */
		// 图标文件实现android系统自带的文件
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "取消");
		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "退出");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case Menu.FIRST + 2:
			finish();
			break;
		case Menu.FIRST + 1:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 创建退出对话框
			AlertDialog isExit = new AlertDialog.Builder(this).create();
			// 设置对话框标题
			isExit.setTitle("退出提示");
			// 设置对话框消息
			isExit.setMessage("确定要退出吗？");
			// 添加选择按钮并注册监听
			isExit.setButton("确定", listener);
			isExit.setButton2("取消", listener);
			// 显示对话框
			isExit.show();

		}

		return false;

	}

	/** 监听对话框里面的button点击事件 */
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
				finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				break;
			default:
				break;
			}
		}
	};

}
