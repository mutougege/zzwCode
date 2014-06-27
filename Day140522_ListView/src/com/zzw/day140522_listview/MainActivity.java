package com.zzw.day140522_listview;


import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定Layout里面的ListView
        ListView list = (ListView) findViewById(R.id.ListView01);
        
        //生成动态数组，加入数据
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<10;i++)
        {
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	map.put("ItemImage", R.drawable.ic_launcher);//图像资源的ID
        	map.put("ItemTitle", "Level "+i);
        	map.put("ItemText", "Finished in 1 Min 54 Secs, 70 Moves! ");
        	listItem.add(map);
        }
        //生成适配器的Item和动态数组对应的元素
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,//数据源 
            R.layout.list_items,//ListItem的XML实现
            //动态数组与ImageItem对应的子项        
            new String[] {"ItemImage","ItemTitle", "ItemText"}, 
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID
            new int[] {R.id.ItemImage,R.id.ItemTitle,R.id.ItemText}
        );
       
        //添加并且显示
        list.setAdapter(listItemAdapter);
        
        //添加点击
        list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				setTitle("点击第"+arg2+"个项目");
			}
		});
        
      //添加长按点击
        list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
				menu.setHeaderTitle("长按菜单-ContextMenu");   
				menu.add(0, 0, 0, "弹出长按菜单0");
				menu.add(0, 1, 0, "弹出长按菜单1");   
			}
		}); 
    }
	
	
	/*
	Android 的activity中
	onCreateOptionsMenu 
	onMenuItemSelected 
	onOptionsItemSelected 
	onCreateContextMenu 
	onContextItemSelected 五个方法的使用：
	
	onCreateOptionsMenu ：此方法为创建菜单方法，这个菜单就是你在点击手机menu键时会看到的菜单。
	onCreateContextMenu ：此方法为创建菜单方法，你还需要对此菜单进行注册Activity.registerForContextMenu(View view),这个菜单是在你长按前面注册的view时看到的菜单。
	
	onMenuItemSelected ：当你选择上面两种菜单任意一种时都会触发这个事件。
	onOptionsItemSelected ：这个方法只在onCreateOptionsMenu 创建的菜单被选中时才会被触发。
	onContextItemSelected ：这个方法只在onCreateContextMenu 创建的菜单被选中时才会被触发。
	*/
	
	/*因为在一个Activity中都使用onMenuItemSelected和onContextItemSelected时会冲突，
	也就是说，当有长按弹出菜单的时候，点击菜单的选项响应的是onMenuItemSelected（），如果被
	点击的Item Id不在 onMenuItemSelected中，事件就不会响应，此时不会再调用onContextItemSelected。
	这样的话，我们在onContextItemSelected中定义的事件就不会执行，这就是很多人遇到的“onContextItemSelected失效”的原因.
	这个时候，需要在onContextItemSelected做相应的判断，如果执行的事件不在onMenuItemSelected中，就执行onContextItemSelected。
	*/
	
	
	
	
	//长按菜单响应函数
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		setTitle("点击了长按菜单里面的第"+item.getItemId()+"个项目"); 
		return super.onContextItemSelected(item);
	}
}