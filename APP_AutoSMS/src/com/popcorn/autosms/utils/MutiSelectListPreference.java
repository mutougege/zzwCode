package com.popcorn.autosms.utils;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;

public class MutiSelectListPreference extends ListPreference {
	private static final String TAG = "popcorn";
	private static final String SEPARATOR = "popcorn";
	private boolean[] mClickedDialogEntryIndices;

	public MutiSelectListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		mClickedDialogEntryIndices = new boolean[getEntries().length];
	}

	@Override
	public void setEntries(CharSequence[] entries) {
		super.setEntries(entries);
		mClickedDialogEntryIndices = new boolean[entries.length];
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
					value.append(entryValues[i]).append(SEPARATOR);
				}
			}
			if (callChangeListener(value)) {
				String val = value.toString();
				if (val.length() > 0)
					val = val.substring(0, val.length() - SEPARATOR.length());
				setValue(val);
			}
		}
	}

	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		// TODO Auto-generated method stub
		CharSequence[] entries = getEntries();
		CharSequence[] entryValues = getEntryValues();
		Log.d(TAG, "onPrepareDialogBuilder entries = " + entries.toString()
				+ " entryValues = " + entryValues.toString());
		if (entries == null || entryValues == null
				|| entries.length != entryValues.length) {
			throw new IllegalStateException(
					"ListPreference requires an entries array and an entryValues array which are both the same length");
		}
		restoreCheckedEntries();
		builder.setMultiChoiceItems(entries, mClickedDialogEntryIndices,
				new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog, int which,
							boolean val) {
						mClickedDialogEntryIndices[which] = val;
					}
				});
	}

	public static String[] parseStoredValue(CharSequence val) {
		if ("".equals(val)||val==null)
			return null;
		else {
			Log.i("test", "val = " + val);
			return ((String) val).split(SEPARATOR);
		}

	}

	private void restoreCheckedEntries() {
		Log.i("test","restoreCheckedEntries");
		CharSequence[] entryValues = getEntryValues();
		String[] vals = parseStoredValue(getValue());
		if (vals != null) {
			for (int j = 0; j < vals.length; j++) {
				Log.d("popcorn-----","restoreCheckedEntries val without trim = " + vals[j]);
				String val = vals[j].trim();
				Log.d("popcorn-----", "restoreCheckedEntries val = " + val);
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
