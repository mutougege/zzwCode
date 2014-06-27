package com.zzw.day140530_downloadcase.widget;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageButton;

public class DownloadBtn extends ImageButton{
	 private RectF mOvals;
	 private Paint mNormalPaint;
	 private Paint mOtherPaint;
	 private float mStart;
     private float mSweep;
     private static final int mOffsetValues = 3;
     public static final int OPEN_STATE = 1002;
     public static final int DOWNLOAD_STATE = 1003;
     public static final int UPDATE_STATE = 1004;
     public static final int DOWNLOADING_STATE = 1005;
     public static final int DOWNLOAD_PAUSE = 1006;
     public static final int DOWNLOAD_WAITING = 1007;
     private boolean mIntial = false;
     private int mState;     
    public DownloadBtn(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	mNormalPaint = new Paint();
    	mOtherPaint= new Paint();
		mNormalPaint.setStyle(Paint.Style.STROKE);
		Resources r = context.getResources();
		float widthPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());
		mNormalPaint.setStrokeWidth(widthPx);
		mNormalPaint.setColor(Color.GRAY);
		mNormalPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		mOtherPaint.setStyle(Paint.Style.STROKE);
		mOtherPaint.setStrokeWidth(widthPx);
		mOtherPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		mOtherPaint.setColor(Color.GREEN);
 	}
 
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(!mIntial){
			mOvals = new RectF(mOffsetValues,mOffsetValues,getWidth()-mOffsetValues,getHeight()-mOffsetValues);
			mIntial = true;
		}
		if(mState == OPEN_STATE){
			canvas.drawArc(mOvals, 0, 360, false, mOtherPaint);
		}else if(mState == UPDATE_STATE || mState ==DOWNLOAD_STATE ){
		    canvas.drawArc(mOvals, 0, 360, false, mNormalPaint);
		}else{
			canvas.drawArc(mOvals, 0, 360, false, mNormalPaint);
			canvas.drawArc(mOvals, mStart, mSweep, false, mOtherPaint);
		}
	}

	
	@Override
	public void setBackgroundDrawable(Drawable background) {
		// TODO Auto-generated method stub
		super.setBackgroundDrawable(null);
	}
    
	public void setProgress(int progress){
		mSweep = (float)3.6*progress;
		mState  = DOWNLOADING_STATE;
		invalidate();
	}
	
	public void setState(int state){
		mState = state;
		invalidate();
	}
}
