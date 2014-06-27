package com.zzw.day140521_viewpage;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class MainActivity extends Activity {

	private View view1, view2, view3;//��Ҫ������ҳ��
	private ViewPager viewPager;//viewpager
	
	//������ʾ����
	private PagerTabStrip pagerTabStrip;//һ��viewpager��ָʾ����Ч������һ����Ĵֵ��»���
	
	//������List�ǳ��ؼ�,���Զ���Adapter����Ĺؼ�����
	private List<View> viewList;//����Ҫ������ҳ����ӵ����list��
	private List<String> titleList;//viewpager�ı���
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		pagerTabStrip=(PagerTabStrip) findViewById(R.id.pagertab);
		
		//�������
		pagerTabStrip.setTabIndicatorColor(Color.GREEN); 
		pagerTabStrip.setDrawFullUnderline(false);
		pagerTabStrip.setBackgroundColor(Color.YELLOW);
		pagerTabStrip.setTextSpacing(50);
		

		//����ҳ��
		LayoutInflater lf = getLayoutInflater().from(this);
		view1 = lf.inflate(R.layout.lay1, null);
		view2 = lf.inflate(R.layout.lay2, null);
		view3 = lf.inflate(R.layout.lay3, null);

		viewList = new ArrayList<View>();// ��Ҫ��ҳ��ʾ��Viewװ��������
		viewList.add(view1);
		viewList.add(view2);
		viewList.add(view3);

		titleList = new ArrayList<String>();// ÿ��ҳ���Title����
		titleList.add("wp");
		titleList.add("jy");
		titleList.add("jh");

		/**
		 * �������صķ���
		 * instantiateItem(ViewGroup, int)
		 * destroyItem(ViewGroup, int, Object)
		 * getCount()
		 * isViewFromObject(View, Object)
		 */
		PagerAdapter pagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {

				return arg0 == arg1;
			}

			@Override
			public int getCount() {

				return viewList.size();
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView(viewList.get(position));

			}

			@Override
			public int getItemPosition(Object object) {

				return super.getItemPosition(object);
			}

			@Override
			public CharSequence getPageTitle(int position) {

				return titleList.get(position);//ֱ��������������ɱ������ʾ�����Դ�������Կ���������û��ʹ��PagerTitleStrip����Ȼ�����ʹ�á�

			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				container.addView(viewList.get(position));
				return viewList.get(position);
			}

		};
		viewPager.setAdapter(pagerAdapter);
	}



}