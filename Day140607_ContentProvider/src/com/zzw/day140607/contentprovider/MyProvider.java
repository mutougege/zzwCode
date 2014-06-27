package com.zzw.day140607.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class MyProvider extends ContentProvider {

	DBHelper mDBHelper;
	SQLiteDatabase db;

	private static final UriMatcher sMatcher;

	static {
		sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sMatcher.addURI(StuInfos.AUTOHORITY, StuInfos.TNAME, StuInfos.ITEM);
		sMatcher.addURI(StuInfos.AUTOHORITY, StuInfos.TNAME + "/#",StuInfos.ITEM_ID);
	}
	
	@Override
	public boolean onCreate() {
		this.mDBHelper = new DBHelper(this.getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		switch (sMatcher.match(uri)) {
		case StuInfos.ITEM:
			return StuInfos.CONTENT_TYPE;
		case StuInfos.ITEM_ID:
			return StuInfos.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI" + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		db = mDBHelper.getWritableDatabase();
		long rowId;
		if (sMatcher.match(uri) != StuInfos.ITEM) {
			throw new IllegalArgumentException("Unknown URI" + uri);
		}
		
		rowId = db.insert(StuInfos.TNAME, StuInfos.ID, values);
		
		if (rowId > 0) {
			Uri noteUri = ContentUris.withAppendedId(StuInfos.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
		}
		
		throw new IllegalArgumentException("Unknown URI" + uri);
	}

	

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		db = mDBHelper.getWritableDatabase();
		int count = 0;
		switch (sMatcher.match(uri)) {
		case StuInfos.ITEM:
			count = db.delete(StuInfos.TNAME, selection, selectionArgs);
			break;
		case StuInfos.ITEM_ID:
			String id = uri.getPathSegments().get(1);
			count = db.delete(
					StuInfos.TNAME,
					StuInfos.ID + "=" + id
					+ (!TextUtils.isEmpty(StuInfos.ID = "?") ? "AND("
					+ selection + ')' : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI" + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	
	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		return 0;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		db = mDBHelper.getWritableDatabase();
		Cursor c;
		
		switch (sMatcher.match(uri)) {
		
		case StuInfos.ITEM:
			c = db.query(StuInfos.TNAME, projection, selection, selectionArgs, null, null, null);
			break;
		case StuInfos.ITEM_ID:
			String id = uri.getPathSegments().get(1);
			c = db.query(StuInfos.TNAME, projection, StuInfos.ID
					+ "="+ id
					+ (!TextUtils.isEmpty(selection) ? "AND(" + selection + ')'
							: ""), selectionArgs, null, null, sortOrder);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI" + uri);
		}
		
		//c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
}