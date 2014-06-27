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
        //��Layout�����ListView
        ListView list = (ListView) findViewById(R.id.ListView01);
        
        //���ɶ�̬���飬��������
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<10;i++)
        {
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	map.put("ItemImage", R.drawable.ic_launcher);//ͼ����Դ��ID
        	map.put("ItemTitle", "Level "+i);
        	map.put("ItemText", "Finished in 1 Min 54 Secs, 70 Moves! ");
        	listItem.add(map);
        }
        //������������Item�Ͷ�̬�����Ӧ��Ԫ��
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,//����Դ 
            R.layout.list_items,//ListItem��XMLʵ��
            //��̬������ImageItem��Ӧ������        
            new String[] {"ItemImage","ItemTitle", "ItemText"}, 
            //ImageItem��XML�ļ������һ��ImageView,����TextView ID
            new int[] {R.id.ItemImage,R.id.ItemTitle,R.id.ItemText}
        );
       
        //��Ӳ�����ʾ
        list.setAdapter(listItemAdapter);
        
        //��ӵ��
        list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				setTitle("�����"+arg2+"����Ŀ");
			}
		});
        
      //��ӳ������
        list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
				menu.setHeaderTitle("�����˵�-ContextMenu");   
				menu.add(0, 0, 0, "���������˵�0");
				menu.add(0, 1, 0, "���������˵�1");   
			}
		}); 
    }
	
	
	/*
	Android ��activity��
	onCreateOptionsMenu 
	onMenuItemSelected 
	onOptionsItemSelected 
	onCreateContextMenu 
	onContextItemSelected ���������ʹ�ã�
	
	onCreateOptionsMenu ���˷���Ϊ�����˵�����������˵��������ڵ���ֻ�menu��ʱ�ῴ���Ĳ˵���
	onCreateContextMenu ���˷���Ϊ�����˵��������㻹��Ҫ�Դ˲˵�����ע��Activity.registerForContextMenu(View view),����˵������㳤��ǰ��ע���viewʱ�����Ĳ˵���
	
	onMenuItemSelected ������ѡ���������ֲ˵�����һ��ʱ���ᴥ������¼���
	onOptionsItemSelected ���������ֻ��onCreateOptionsMenu �����Ĳ˵���ѡ��ʱ�Żᱻ������
	onContextItemSelected ���������ֻ��onCreateContextMenu �����Ĳ˵���ѡ��ʱ�Żᱻ������
	*/
	
	/*��Ϊ��һ��Activity�ж�ʹ��onMenuItemSelected��onContextItemSelectedʱ���ͻ��
	Ҳ����˵�����г��������˵���ʱ�򣬵���˵���ѡ����Ӧ����onMenuItemSelected�����������
	�����Item Id���� onMenuItemSelected�У��¼��Ͳ�����Ӧ����ʱ�����ٵ���onContextItemSelected��
	�����Ļ���������onContextItemSelected�ж�����¼��Ͳ���ִ�У�����Ǻܶ��������ġ�onContextItemSelectedʧЧ����ԭ��.
	���ʱ����Ҫ��onContextItemSelected����Ӧ���жϣ����ִ�е��¼�����onMenuItemSelected�У���ִ��onContextItemSelected��
	*/
	
	
	
	
	//�����˵���Ӧ����
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		setTitle("����˳����˵�����ĵ�"+item.getItemId()+"����Ŀ"); 
		return super.onContextItemSelected(item);
	}
}