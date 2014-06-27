package com.zzw.day140620_gallery;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class SlowSpeedGallery extends Gallery {

	public SlowSpeedGallery(Context context) {
		super(context);
	}
	
	public SlowSpeedGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public SlowSpeedGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if(e1 == null || e2 == null) {
			return false;
		}
		try {
			float result = e2.getX() - e1.getX();
			if(result > 20){ 
				//Check if scrolling left     
				onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
				return true;
			}else if(result < -20){
			    //Otherwise scrolling right    
			    onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
			    return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
}
