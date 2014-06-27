package com.popcorn.wordfilter;



import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WordFilter {

	public int filterWord(String body){
		
		Log.i("test","body = "+body);
		
		FilteredResult result = null;
		result = WordFilterUtil.filterText(body, '*');
		Log.i("test","original:" + result.getOriginalContent());
		Log.i("test","result:" + result.getFilteredContent());
		Log.i("test","badWords:" + result.getBadWords());
		Log.i("test","level:" + result.getLevel());
		Log.i("test","count:" + result.getCount());

		//return result.getCount();
		return 5;
	}
}
