package com.zzw.mullistpreferencetest;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;

public class MutiSelectListPreference extends ListPreference {

	private final static String SEP = "z&z&w";
	private boolean[] mClickedDialogEntryIndices;

	public MutiSelectListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(mClickedDialogEntryIndices == null){
			mClickedDialogEntryIndices = new boolean[getEntries().length];
		}
	}

	@Override
	public void setEntries(CharSequence[] entries) {
		super.setEntries(entries);
		if(mClickedDialogEntryIndices == null){
			mClickedDialogEntryIndices = new boolean[entries.length];
		}
	}

	public MutiSelectListPreference(Context context) {
		this(context, null);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		// TODO Auto-generated method stub
		CharSequence[] entryValues = getEntryValues();
		if (positiveResult && entryValues != null) {
			
			StringBuffer value = new StringBuffer();
			for (int i = 0; i < entryValues.length; i++) {
				if (mClickedDialogEntryIndices[i]) {
					value.append(entryValues[i]).append(SEP);
				}
			}

			if (callChangeListener(value)) {
				String val = value.toString();
				if (val.length() > 0)
					val = val.substring(0, val.length() - SEP.length());
				setValue(val);
			}
		}

	}

	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		// TODO Auto-generated method stub
		CharSequence[] entries = getEntries();
		CharSequence[] entryValues = getEntryValues();
		
		if (entries == null || entryValues == null
				|| entries.length != entryValues.length) {
			throw new IllegalStateException("value error");
		}

		restoreCheckedEntries();
		final boolean[] tmpIndices = mClickedDialogEntryIndices;
		builder.setMultiChoiceItems(entries, mClickedDialogEntryIndices,
				new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog, int which,boolean val) {
						mClickedDialogEntryIndices[which] = val;
					}
				});
		
		builder.setOnCancelListener(new OnCancelListener(){
			@Override
			public void onCancel(DialogInterface arg0) {
				mClickedDialogEntryIndices = tmpIndices;
			}
		});
	}

	public static String[] parseStoredValue(CharSequence val) {
		
		Log.d("zzw", "parseStoredValue val = " + val.toString());
		if ("".equals(val))
			return null;
		else
			return val.toString().split(SEP);
	}

	private void restoreCheckedEntries() {
		CharSequence[] entryValues = getEntryValues();

		String tmpstr = "";
		tmpstr = getValue();
		if(tmpstr==null||tmpstr==""){
			return ;
		}
		
		String[] vals = parseStoredValue(tmpstr);

		if (vals != null) {
			//Log.d("zzw", "restoreCheckedEntries vals = " + vals);
			
			for (int j = 0; j < vals.length; j++) {
				
				String val = vals[j].trim();
				
				for (int i = 0; i < entryValues.length; i++) {
					CharSequence entry = entryValues[i];
					if (entry.equals(val)) {
						mClickedDialogEntryIndices[i] = true;
						break;
					}
				}
			}
		}
	}

}