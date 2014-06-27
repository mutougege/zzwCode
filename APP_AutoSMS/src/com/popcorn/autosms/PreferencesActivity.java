package com.popcorn.autosms;

import java.util.ArrayList;

import com.popcorn.autosms.constant.IntentKey;
import com.popcorn.autosms.constant.TaskCode;
import com.popcorn.autosms.service.MainService;
import com.popcorn.autosms.utils.MutiSelectListPreference;




import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class PreferencesActivity extends PreferenceActivity{
	MutiSelectListPreference mlp;
	ArrayList contactsList = new ArrayList();
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	
    	
        super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.preference_layout);
		mlp = (MutiSelectListPreference) findPreference("select_contacts_list_preference");
		contactsList = getContactsList();
		CharSequence[] cs = (CharSequence[])contactsList.toArray(new CharSequence[contactsList.size()]);
		/*for(int i=0;i<contactsList.size();i++){
			Log.i("test","List = "+i+" "+contactsList.get(i));
		}*/
		mlp.setEntries(cs);
		mlp.setEntryValues(cs);
    }
    
    @Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if(preference.getKey().equals("auto_reply")){
			SharedPreferences pref=this.getSharedPreferences("com.popcorn.autosms_preferences", 0);
			if(pref.getBoolean("auto_reply", false)){
				
				Log.i("test", "auto_reply true");
				Intent intent=new Intent(this,MainService.class);
				intent.putExtra(IntentKey.TASK, TaskCode.TASK_START_OBSERVER);
				this.startService(intent);
				
			}else{
				Log.i("test", "auto_reply false");
				Intent intent=new Intent(this,MainService.class);
				intent.putExtra(IntentKey.TASK, TaskCode.TASK_STOP_OBSERVER);
				this.startService(intent);
				
			}
		}else if(preference.getKey().equals("select_contacts_list_preference")){
			
			/*Log.i("test","select_contacts_list_preference "+contactsList.size());
			SharedPreferences pref=this.getSharedPreferences("com.popcorn.autosms_preferences", 0);
			
			String testList = "";
			testList = pref.getString("select_contacts_list_preference", "");
		
			
			if(testList ==null || testList == ""){
				
				
				String strListValue = "";
				
				if(contactsList.size()>=1){
					strListValue = (String) contactsList.get(0);
				}
				for(int i=1;i<contactsList.size();i++){
					strListValue = strListValue+"popcorn"+(String)contactsList.get(i);
				}
				
				Editor editor = pref.edit();
				editor.putString("select_contacts_list_preference", strListValue);
				editor.commit();
			}*/
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}//end event onPeferenceTreeClick
    
    
    
    
    private ArrayList getContactsList() {
		Cursor cur = getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI,
				null,
				null,
				null,
				ContactsContract.Contacts.DISPLAY_NAME
						+ " COLLATE LOCALIZED ASC");

		while (cur.moveToNext()) {

			int idColumn = cur.getColumnIndex(ContactsContract.Contacts._ID);
			int displayNameColumn = cur
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

			String contactId = cur.getString(idColumn);
			String disPlayName = cur.getString(displayNameColumn);

			int phoneCount = cur
					.getInt(cur
							.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

			if (phoneCount > 0) {
				Cursor phones = getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactId, null, null);

				while (phones.moveToNext()) {
					String number = phones
							.getString(phones
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					number.replace("+86", "");
					if (TextUtils.isEmpty(number)) {
						
					} else {
						contactsList.add(disPlayName+" "+number);
					}
				}
				phones.close();
			}
		}
		return contactsList;
	}
}



