package com.zzw.day140530_download;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

public class DownloadManager {
	public static final String DOWNLOAD_TYPE_DOWN = "type_download";
    public static final String DOWNLOAD_TYPE_UPDATE = "type_update";
	
    private static final String TAG = "DownloadManager";
    
	private static DownloadManager sInstance;
	private final Context mContext;
	private int MAX_DOWNLOAD_NUMBER;

	private DownloadManager(Context context) {
		mContext = context;
	}
	
	public static DownloadManager getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new DownloadManager(context);
		}
		return sInstance;
	}
	
	public synchronized boolean isReachCountLimit() {
		int downloadingNumber = PackageInfos.getDownloadingCount(mContext.getContentResolver());
		MAX_DOWNLOAD_NUMBER = NumberUtil.toInt(PreferenceUtil.getString(mContext, MarketConstants.DOWNLOADING_APP_LIMIT, "2"));
	
		return downloadingNumber >= MAX_DOWNLOAD_NUMBER;
	}
	
	/**
	 * 默认方法适应于单个下载，根据网络情况弹出对话�?
	 * @param asset
	 */
	public synchronized void scheduleDownload(final Asset asset) { 
		if (TextUtils.isEmpty(asset.pkgName)) {
			GlobalUtil.shortToast(mContext, mContext.getString(R.string.msg_downlod_error, asset.name));
			return;
		}
		
		final boolean update  = PackageInfoUtil.isTypeUpdate(asset.installed);
		
		checkWifi(asset, update);
	}
	
	/**
	 * 通过flag判断是否要弹出网络提示对话框
	 * @param asset
	 * @param noRemindNetwork
	 */
	public synchronized void scheduleDownload(final Asset asset, boolean noRemindNetwork) { 
		if (TextUtils.isEmpty(asset.pkgName)) {
			GlobalUtil.shortToast(mContext, mContext.getString(R.string.msg_downlod_error, asset.name));
			return;
		}
		
		final boolean update  = PackageInfoUtil.isTypeUpdate(asset.installed);
		
		if(noRemindNetwork){
			assignDownload(asset, update);
		} else {			
            checkWifi(asset, update);		
		}	
	}
	
	/**
	 * 将下载任务分发到等待队列或直接下�?
	 * @param asset
	 * @param update
	 */
	public synchronized void assignDownload(final Asset asset, boolean update) { 
		if(isReachCountLimit()) {
			waitDownload(asset, update);
		} else {
			startDownload(asset, update);
		}
	}
	
	private void waitDownload(Asset asset, boolean update) {
		ContentResolver contentResolver = mContext.getContentResolver();

		PackageInfos packageInfos = PackageInfos.getPackageInfo(contentResolver, asset.pkgName);
        if (packageInfos == null) {
        	packageInfos = PackageInfos.createNew(contentResolver, asset.pkgName, asset.name,asset.versionCode);
        }
        PackageState packageState = packageInfos.getState();
        
        if (packageState.isWaiting() || packageState.isDownloading()) {
        	Log.e(TAG, "State \"" + packageState + "\" is wrong when add wait download.");
        	return;
        }
        
        if(packageState.isDownloadingPaused() || packageState.isDownloadFailed()) {
        	changeToWaitDownload(contentResolver, packageInfos.getDownloadUri());
        	
        	packageInfos.setState(update ? PackageState.UPDATE_DOWNLOADING_PAUSED_WAIT : PackageState.INSTALL_DOWNLOADING_PAUSED_WAIT);
    	} else if(packageState.isWaitingIgnored()) {
    		resumeIgnoredWait(asset);
    	} else {
    		//chendy 20140117 modify  for PROD102521855 start
    		String appendedUrl=null;
    		if(asset.iconUrl!=null&&asset.iconUrl.contains("anzhi")){
        		appendedUrl = appendAnzhiDownloadUrl(asset.id);
        	}else{
        		//chendy modify start
        		/*
        		if(asset.incrementUpdateSize>0){
        			appendedUrl = appendUrl(asset.incrementUpdateDownloadUrl, update, asset.attr,asset.downloadPage);
        		}else{
        		    appendedUrl = appendUrl(asset.apkUrl, update, asset.attr, asset.downloadPage);
        		}
        		*/
        		appendedUrl=asset.apkUrl;
        		//chendy modify end
        	}
    		//chendy 20140117 modify  for PROD102521855 end
        	if(appendedUrl != null) {
        		Uri downloadUri = insertDownload(contentResolver, appendedUrl, asset.name, asset.iconUrl, asset.pkgName, 
                		asset.size, true);
        		if (downloadUri == null) {
        			GlobalUtil.shortToast(mContext, mContext.getString(R.string.msg_downlod_error, asset.name));
        			return;
                }
        		packageInfos.setDownloadUri(downloadUri);
        		packageInfos.setState(update ? PackageState.UPDATE_DOWNLOAD_WAIT:PackageState.INSTALL_DOWNLOAD_WAIT);
        	} else {
        		GlobalUtil.shortToast(mContext, mContext.getString(R.string.msg_downlod_error, asset.name));
    			return;
        	}
    	}
        packageInfos.commitChange(contentResolver);
	}
	
	private void startDownload(final Asset asset, final boolean update) {
		final String channel = GlobalUtil.getChannel(mContext.getApplicationContext());
		
		ContentResolver contentResolver = mContext.getContentResolver();
		PackageInfos packageInfos = PackageInfos.getPackageInfo(contentResolver, asset.pkgName);
        if (packageInfos == null) {
        	packageInfos = PackageInfos.createNew(contentResolver, asset.pkgName, asset.name,asset.versionCode);
        }
        PackageState packageState = packageInfos.getState();
		
		if(packageState == PackageState.INITIAL || packageState.isInitWaiting() || packageState.isIntallFailed()) {
			if (asset.incrementUpdateSize > 0) {
				excuteDownload(asset.incrementUpdateDownloadUrl, asset.id,
						asset.pkgName, asset.name, asset.incrementUpdateSize, channel, update,true,
						asset.iconUrl, asset.attr, asset.downloadPage,asset.versionCode);
			} else {
				excuteDownload(asset.apkUrl, asset.id, asset.pkgName,
						asset.name, asset.size, channel, update,false, asset.iconUrl,
						asset.attr, asset.downloadPage,asset.versionCode);
			}
		} else if(packageState.isDownloadFailed()) {
			retry(asset.pkgName);
		} else if(packageState.isDownloadingPaused() || packageState.isPausedWaiting()) {
			resume(asset.pkgName);
		} else if(packageState.isWaitingIgnored()){
			resumeIgnoredWait(asset);
		} else {
			Log.w(TAG, "State \"" + packageState + "\" is wrong when start download");
		}
	}
	
	/**
	 * �?��当前网络是否为wifi，若是wifi直接下载，若不是弹出网络提示�?
	 * @param asset
	 * @param update
	 */
	public void checkWifi(Asset asset, boolean update){
		if(NetworkUtil.isNetworkAvailable(mContext)) {
			boolean isRemindNetwork = PreferenceUtil.getBoolean(mContext, MarketConstants.NETWORK_REMIND, true);
			if(!NetworkUtil.isWifiNetworkAvailable(mContext)) {				
				if(isRemindNetwork){
					Intent intent = new Intent();
					intent.setClassName(mContext, ActivityWarningNetwork.class.getName());
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra(MarketConstants.EXTRA_DETAILED_ASSET, asset);
					mContext.startActivity(intent);
				}else{
					assignDownload(asset, update);
				}
			} else {//wifi情况�?
				if(asset.isXpk  && !update){//xpk下载，�?且不是更�?
					long needSize = asset.xpkSize+MarketConstants.SAVE_SIZE+asset.size;
					long freeSize = 0;
					
					//根据安装途径判断剩余的大�?
					if(FileUtil.isSDCardMounted()){
						freeSize = FileUtil.getSDCardFreespace();
					}else{
						freeSize = FileUtil.getSystemFreespace();
					}
					
					if(needSize >= freeSize){//xpk解压空间不够。�?有一定的误差率，基本保证正确
						Intent intent = new Intent();
						intent.setClassName(mContext, ActivityWarningXpk.class.getName());
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra(MarketConstants.EXTRA_DETAILED_ASSET, asset);
						mContext.startActivity(intent);
					}else{//直接下载
						assignDownload(asset, update);
					}
				}else{
					Log.d(TAG, "checkWifi A");
					assignDownload(asset, update);
				}				
			}
		} else {
			GlobalUtil.shortToast(mContext, R.string.download_network_error);
		}
	}

	private void excuteDownload(final String url, final int appId, final String packageName, final String title, final int size,
			final String channel, final boolean update, final boolean incrementalUpdate,final String iconUrl, String attr, String downloadPage,int versionCode) {
		ContentResolver contentResolver = mContext.getContentResolver();

		PackageInfos packageInfos = PackageInfos.getPackageInfo(contentResolver, packageName);
        if (packageInfos == null) {
        	packageInfos = PackageInfos.createNew(contentResolver, packageName, title, versionCode);
        }

        Uri uri = packageInfos.getDownloadUri();
        Log.d(TAG, "excuteDownload uri="+uri);
        if(uri != null) {
        	startWaitDownload(contentResolver, uri);
        } else {
        	//chendy 20131111 mod for anzhi download start
        	//String appendedUrl = appendUrl(url, update, attr, downloadPage);
        	String appendedUrl = null;
        	if(iconUrl!=null&&iconUrl.contains("anzhi")){
        		//appendedUrl = appendAnzhiDownloadUrl(appId);
        		if(url != null) {
       			 appendedUrl = url; 
       		 	}else{
       			 appendedUrl = appendAnzhiDownloadUrl(appId); 
       		 	}
        	}else{
        		//appendedUrl = appendUrl(url, update, attr, downloadPage);
        		appendedUrl = url;
        	}       	
        	Log.d(TAG, "excuteDownload appendedUrl="+appendedUrl);
        	//chendy 20131111 mod for anzhi download start        	
        	if(appendedUrl != null) {
        		uri = insertDownload(contentResolver, appendedUrl, title, iconUrl, packageName, size, false);
        		if (uri == null) {
        			GlobalUtil.shortToast(mContext, R.string.msg_downlod_error);
        			return;
                }
        		packageInfos.setDownloadUri(uri);
        	} else {
        		GlobalUtil.shortToast(mContext, R.string.msg_downlod_error);
        		return;
        	}
        }
      
        packageInfos.setState(update ? PackageState.UPDATE_DOWNLOADING:PackageState.INSTALL_DOWNLOADING);  
		packageInfos.commitChange(contentResolver);
		
		if(incrementalUpdate){
			ClientLogger.addInfoDLStartLog(mContext, packageName, NormalDownBtnHandler.BUTTON_ACTION_INCREMENTAL_UPDATE);
		}else{
			ClientLogger.addInfoDLStartLog(mContext, packageName, update);
		}
		
	}
	/*
	private String appendUrl(String url, boolean update, String attr, String downloadPage) {
		if(url != null) {
			  Log.d(TAG, "appendUrl url="+url);
			if (url.contains("downloadAppForOpen")) {		//MM专区的下载地�?��不做更改  	--by huangxin
				return url;
//            }else if(url.contains(".xpk")){ //xpk下载 不做更改 --jingwei
//	            return url;
			} else if (url.contains("diff")) { // 增量更新的下载地�?
                 //do nothing
			} else {
				if(url.contains("?")) {
					if(url.contains("ug") && url.contains("uid") && url.contains("channel")) {
						return url;
					} else {
						url = url.substring(0, url.indexOf("?"));
					}
				} 

			}
			
			
			StringBuilder sb = new StringBuilder();
			sb.append(url);
			if (url.contains("diff")) { 
				sb.append("&");
			}else{
				sb.append("?");
			}
			sb.append("channel=").append(GlobalUtil.getChannel(mContext))
					.append("&ug=").append(update ? 1 : 0).append("&ct=")
					.append(System.currentTimeMillis());

			//下载添加attr的参�?
			if(!StringUtil.isEmpty(attr)) {
				sb.append("&attr=").append(attr);
			}
			//下载添加download page信息
			if(!StringUtil.isEmpty(downloadPage)) {
				sb.append("&").append(downloadPage);
			}
			
			Log.d(TAG, "appended downloadUrl: " + sb.toString());
			
			return sb.toString();
		} else {
			  Log.d(TAG, "appendUrl return url");
			return null;
		}
	}
	*/
	protected synchronized void startDownloadForWaits() {
		if(isReachCountLimit()) {
			return;
		}
		
		ContentResolver contentResolver = mContext.getContentResolver();
		
		PackageInfos packageInfos = PackageInfos.getOldestWaitPackage(contentResolver);
		if(packageInfos != null) {
			PackageState packageState = packageInfos.getState();
			if(packageState == PackageState.INSTALL_DOWNLOAD_WAIT || packageState == PackageState.INSTALL_DOWNLOADING_PAUSED_WAIT) {
				packageInfos.setState(PackageState.INSTALL_DOWNLOADING);
			} else {
				packageInfos.setState(PackageState.UPDATE_DOWNLOADING);
			}
			packageInfos.commitChange(contentResolver);
			
			Log.e(TAG, "startDownloadForWaits, appName: " + packageInfos.getString(PackageInfos.APP_NAME));
			startWaitDownload(contentResolver, packageInfos.getDownloadUri());
		}
	}
	
	private synchronized void startWaitDownload(ContentResolver contentResolver, Uri uri) {
		ContentValues values = new ContentValues();
		values.put(Downloads.Impl.COLUMN_VISIBILITY, Downloads.Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_PENDING);
		values.put(Downloads.Impl.COLUMN_CONTROL, Downloads.Impl.CONTROL_RUN);
		contentResolver.update(uri, values, null, null);
	}
	
	private void ignoreWaitDownload(ContentResolver contentResolver, Uri uri) {
		ContentValues values = new ContentValues();
		values.put(Downloads.Impl.COLUMN_VISIBILITY, Downloads.Impl.VISIBILITY_HIDDEN);
		values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_WAITING_LIMIT_IGNORE);
		values.put(Downloads.Impl.COLUMN_CONTROL, Downloads.Impl.CONTROL_PAUSED);
		contentResolver.update(uri, values, null, null);
	}
	
	private void changeToWaitDownload(ContentResolver contentResolver, Uri uri) {
		ContentValues values = new ContentValues();
		values.put(Downloads.Impl.COLUMN_VISIBILITY, Downloads.Impl.VISIBILITY_HIDDEN);
		values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_WAITING_LIMIT);
		contentResolver.update(uri, values, null, null);
	}

	private Uri insertDownload(ContentResolver contentResolver, String url, String title, String iconUrl, 
			String packageName, int size, boolean isWait){
		ContentValues values = new ContentValues();
		values.put(Downloads.Impl.COLUMN_MIME_TYPE, MarketConstants.MIMETYPE_APK);
        values.put(Downloads.Impl.COLUMN_TITLE, title);
        values.put(Downloads.Impl.COLUMN_DESCRIPTION, iconUrl);
        values.put(Downloads.Impl.COLUMN_URI, url);
        values.put(Downloads.Impl.COLUMN_NO_INTEGRITY ,true);
//        values.put(Downloads.Impl.COLUMN_VISIBILITY, Downloads.Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        values.put(Downloads.Impl.COLUMN_NOTIFICATION_PACKAGE, MarketConstants.PACKAGE_NAME_PNAME);
        values.put(Downloads.Impl.COLUMN_NOTIFICATION_CLASS, DownloadExtReceiver.class.getName());
        values.put(Downloads.Impl.COLUMN_NOTIFICATION_EXTRAS, packageName);
		if (FileUtil.isSDCardMounted()) {
			values.put(Downloads.Impl.COLUMN_DESTINATION, Downloads.Impl.DESTINATION_EXTERNAL);
		} else {
			values.put(Downloads.Impl.COLUMN_DESTINATION, Downloads.Impl.DESTINATION_CACHE_PARTITION_PURGEABLE);
		}
		if (isWait) {
			//chendy delete for PROD102868857 start
			//values.put(Downloads.Impl.COLUMN_TOTAL_BYTES, size);
			//chendy delete for PROD102868857 end
			values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_WAITING_LIMIT);
			values.put(Downloads.Impl.COLUMN_VISIBILITY, Downloads.Impl.VISIBILITY_HIDDEN);
		} else {
			values.put(Downloads.Impl.COLUMN_VISIBILITY, Downloads.Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		}
		//chendy add for PROD102868857 start
		values.put(Downloads.Impl.COLUMN_TOTAL_BYTES, size);
		//chendy add for PROD102868857 end
		Uri uri = contentResolver.insert(Downloads.CONTENT_URI, values);
		return uri;
	}
	
	private void deleteDownload(ContentResolver contentResolver, Uri uri) {
		contentResolver.delete(uri, null, null);
	}
	
	private void pauseDownload(ContentResolver contentResolver, Uri uri) {
        ContentValues values = new ContentValues();
		values.put(Downloads.Impl.COLUMN_VISIBILITY, Downloads.Impl.VISIBILITY_HIDDEN);
        values.put(Downloads.Impl.COLUMN_CONTROL, Downloads.Impl.CONTROL_PAUSED);
        values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_PAUSED_BY_APP);
		contentResolver.update(uri, values, null, null);
	}
	
	private void resumeDownload(ContentResolver contentResolver, Uri uri) {
		ContentValues values = new ContentValues();
		values.put(Downloads.Impl.COLUMN_VISIBILITY, Downloads.Impl.VISIBILITY_VISIBLE);
		values.put(Downloads.Impl.COLUMN_CONTROL, Downloads.Impl.CONTROL_RUN);
		values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_RESUMED_BY_APP);
		contentResolver.update(uri, values, null, null);
	}
	
	private void retryDownload(ContentResolver contentResolver, Uri uri) {
		ContentValues values = new ContentValues();
		values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_RUNNING);
		values.put(Downloads.Impl.COLUMN_CONTROL, Downloads.Impl.CONTROL_RUN);
		contentResolver.update(uri, values, null, null);
	}
	
//	private String queryDownloadData(ContentResolver contentResolver, Uri uri) {
//		String data = null;
//        Cursor cursor = contentResolver.query(uri, new String[] {Downloads.Impl._DATA }, null, null, null);
//		if (cursor != null) {
//			if (cursor.moveToFirst()) {
//				data = cursor.getString(0);
//			}
//			cursor.close();
//		}
//		
//		return data;
//	}

	public void pause(String packageName) {
		ContentResolver contentResolver = mContext.getContentResolver();
		PackageInfos packageInfos = PackageInfos.getPackageInfo(contentResolver, packageName);
		if(packageInfos!=null){
	        Uri uri = packageInfos.getDownloadUri();      
	        pauseDownload(contentResolver, uri);
	        if (packageInfos.getState() == PackageState.INSTALL_DOWNLOADING)
	        	packageInfos.setState(PackageState.INSTALL_DOWNLOADING_PAUSED);
	        else //if (packageInfos.getState() == PackageState.UPDATE_DOWNLOADING)
	        	packageInfos.setState(PackageState.UPDATE_DOWNLOADING_PAUSED);
	        
	        packageInfos.commitChange(contentResolver);
		}
        ClientLogger.addInfoDLPauseLog(mContext, packageName);
        
        startDownloadForWaits();
	}
	
	private void resume(String packageName) {
		ContentResolver contentResolver = mContext.getContentResolver();
		PackageInfos packageInfos = PackageInfos.getPackageInfo(contentResolver, packageName);
		
        if (packageInfos.getState() == PackageState.INSTALL_DOWNLOADING_PAUSED 
        		|| packageInfos.getState() == PackageState.INSTALL_DOWNLOADING_PAUSED_WAIT)
        	packageInfos.setState(PackageState.INSTALL_DOWNLOADING);
        else //if (packageInfos.getState() == PackageState.UPDATE_DOWNLOADING_PAUSED)
        	packageInfos.setState(PackageState.UPDATE_DOWNLOADING);
        packageInfos.commitChange(contentResolver);
        
        Uri uri = packageInfos.getDownloadUri();
        if(isNotifyDownloadSuccess(uri)) {
        	return;
        } else {
        	resumeDownload(contentResolver, uri);
        }
        
		ClientLogger.addInfoDLResumeLog(mContext, packageName);
	}
	
	private void retry(String packageName) {
		Log.d(TAG, "retry packageName: " + packageName);
		ContentResolver contentResolver = mContext.getContentResolver();
		PackageInfos packageInfos = PackageInfos.getPackageInfo(contentResolver, packageName);
		Log.d(TAG, "retry packageInfos: " + packageInfos);
		if (packageInfos == null)
			return;

		PackageState state = PackageInfos.getPackageState(mContext, packageName);
        if (state == PackageState.INSTALL_DOWNLOAD_FAILED)
        	packageInfos.setState(PackageState.INSTALL_DOWNLOADING);
        else //if (state == PackageState.UPDATE_DOWNLOAD_FAILED)
        	packageInfos.setState(PackageState.UPDATE_DOWNLOADING);
        packageInfos.commitChange(contentResolver);
		
		Uri uri = packageInfos.getDownloadUri();
		Log.d(TAG, "retry DownloadUri: " + uri);
		if(isNotifyDownloadSuccess(uri)) {
			return;
		} else {
			retryDownload(contentResolver, uri);
		}
		
		ClientLogger.addInfoDLRetryLog(mContext, packageName);
	}
	
	public void deletePackage(String packageName) {
		// Delete package file and related info from file system, downloads.db and packages.db
		ContentResolver contentResolver = mContext.getContentResolver();
		PackageInfos packageInfos = PackageInfos.getPackageInfo(contentResolver, packageName);
		Log.d(TAG, "deletePackage packageInfos: " + packageInfos);
		if (packageInfos == null)
			return;
		
        Uri uri = packageInfos.getDownloadUri();
        Log.d(TAG, "deletePackage DownloadUri: " + uri);
        File downloadFile = packageInfos.getDownloadedApk(contentResolver);
		if (downloadFile != null) {
			downloadFile.delete();
		}
		//chendy modify for PROD102721999 start
		//deleteDownload(contentResolver, uri);
		try{
			if(uri!=null){
				deleteDownload(contentResolver, uri);	
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
		//chendy modify for PROD102721999 end

		PackageInfos.deletePackageInfo(contentResolver, packageName);

		return;
	}
	
	public void cancel(final String packageName) {
		Log.d(TAG, "cancel packageName: " + packageName);
		deletePackage(packageName);
        ClientLogger.addInfoDLCancelLog(mContext, packageName);
        
        startDownloadForWaits();
	}
	
	public void ignoreWait(String packageName) {
		ContentResolver contentResolver = mContext.getContentResolver();
		PackageInfos packageInfos = PackageInfos.getPackageInfo(contentResolver, packageName);
		if (packageInfos == null)
			return;
		
		PackageState state = packageInfos.getState();
		if (state == PackageState.INSTALL_DOWNLOAD_WAIT) {
			packageInfos.setState(PackageState.INSTALL_DOWNLOAD_WAIT_IGNORE);
		} else if (state == PackageState.UPDATE_DOWNLOAD_WAIT) {
        	packageInfos.setState(PackageState.UPDATE_DOWNLOAD_WAIT_IGNORE);
		} else if (state == PackageState.INSTALL_DOWNLOADING_PAUSED_WAIT) {
			packageInfos.setState(PackageState.INSTALL_DOWNLOADING_PAUSED_WAIT_IGNORE);
		} else if(state == PackageState.UPDATE_DOWNLOADING_PAUSED_WAIT) {
			packageInfos.setState(PackageState.UPDATE_DOWNLOADING_PAUSED_WAIT_IGNORE);
		}
		packageInfos.commitChange(contentResolver);
		
        ignoreWaitDownload(contentResolver, packageInfos.getDownloadUri());
	}
	
	public void resumeIgnoredWait(Asset asset) {
		ContentResolver contentResolver = mContext.getContentResolver();
		PackageInfos packageInfos = PackageInfos.getPackageInfo(contentResolver, asset.pkgName);
		if (packageInfos == null)
			return;
		
		PackageState state = packageInfos.getState();
		if (state == PackageState.INSTALL_DOWNLOAD_WAIT_IGNORE) {
			packageInfos.setState(PackageState.INSTALL_DOWNLOAD_WAIT);
		} else if (state == PackageState.UPDATE_DOWNLOAD_WAIT_IGNORE) {
        	packageInfos.setState(PackageState.UPDATE_DOWNLOAD_WAIT);
		} else if (state == PackageState.INSTALL_DOWNLOADING_PAUSED_WAIT_IGNORE) {
			packageInfos.setState(PackageState.INSTALL_DOWNLOADING_PAUSED_WAIT);
		} else if(state == PackageState.UPDATE_DOWNLOADING_PAUSED_WAIT_IGNORE) {
			packageInfos.setState(PackageState.UPDATE_DOWNLOADING_PAUSED_WAIT);
		}
		packageInfos.commitChange(contentResolver);
		
		changeToWaitDownload(contentResolver, packageInfos.getDownloadUri());
		
		scheduleDownload(asset, true);
	}
	
	public int pauseAllDownloads() {
		PackageInfos[] packageInfoList = PackageInfos.getPackageInfoWithStates(mContext.getContentResolver(), 
				new PackageState[]{ PackageState.INSTALL_DOWNLOADING, PackageState.UPDATE_DOWNLOADING,
								PackageState.INSTALL_DOWNLOAD_WAIT, PackageState.UPDATE_DOWNLOAD_WAIT,
								PackageState.INSTALL_DOWNLOADING_PAUSED_WAIT, PackageState.UPDATE_DOWNLOADING_PAUSED_WAIT}, null);

		if(packageInfoList != null) {
			for(int i = 0; i < packageInfoList.length; ++i) {
				String packageName = packageInfoList[i].getPackageName();
				exitPauseAll(packageName);
			}
			
			return packageInfoList.length;
		}
		
		return 0;
	}
	
	
	public int resumeAllDownloads(){
		
		PackageInfos[] packageInfoList = PackageInfos.getPackageInfoWithStates(mContext.getContentResolver(), 
				new PackageState[]{ PackageState.INSTALL_DOWNLOADING_PAUSED, PackageState.UPDATE_DOWNLOADING_PAUSED,
								PackageState.INSTALL_DOWNLOAD_WAIT_IGNORE, PackageState.UPDATE_DOWNLOAD_WAIT_IGNORE,
								PackageState.INSTALL_DOWNLOADING_PAUSED_WAIT_IGNORE, PackageState.UPDATE_DOWNLOADING_PAUSED_WAIT_IGNORE}, null);
		
		
		if(packageInfoList != null) {
			
			if(packageInfoList.length>0){
				GlobalUtil.setFromAutoReumeDownload(true);
			}
			
			for(int i = 0; i < packageInfoList.length; ++i) {
				String packageName = packageInfoList[i].getPackageName();
				restrartAllResumed(packageName);
			}
			
			return packageInfoList.length;
		}
		
		return 0;
	}
	
	public void exitPauseAll(String packageName) {
		ContentResolver contentResolver = mContext.getContentResolver();
		PackageInfos packageInfos = PackageInfos.getPackageInfo(contentResolver, packageName);
        Uri uri = packageInfos.getDownloadUri();
        PackageState state = packageInfos.getState();
        
        if(state.isDownloading()) {
        	if (packageInfos.getState() == PackageState.INSTALL_DOWNLOADING)
            	packageInfos.setState(PackageState.INSTALL_DOWNLOADING_PAUSED);
            else //if (packageInfos.getState() == PackageState.UPDATE_DOWNLOADING)
            	packageInfos.setState(PackageState.UPDATE_DOWNLOADING_PAUSED);
        	
        	pauseDownload(contentResolver, uri);
        } else if(state.isWaiting()) {
        	if (state == PackageState.INSTALL_DOWNLOAD_WAIT) {
    			packageInfos.setState(PackageState.INSTALL_DOWNLOAD_WAIT_IGNORE);
    		} else if (state == PackageState.UPDATE_DOWNLOAD_WAIT) {
            	packageInfos.setState(PackageState.UPDATE_DOWNLOAD_WAIT_IGNORE);
    		} else if (state == PackageState.INSTALL_DOWNLOADING_PAUSED_WAIT) {
    			packageInfos.setState(PackageState.INSTALL_DOWNLOADING_PAUSED_WAIT_IGNORE);
    		} else if(state == PackageState.UPDATE_DOWNLOADING_PAUSED_WAIT) {
    			packageInfos.setState(PackageState.UPDATE_DOWNLOADING_PAUSED_WAIT_IGNORE);
    		}
        	
        	ignoreWaitDownload(contentResolver, uri);
        }
        packageInfos.commitChange(contentResolver);
	}
	
	
	
	public void restrartAllResumed(String packageName) {
		
		ContentResolver contentResolver = mContext.getContentResolver();
		PackageInfos packageInfos = PackageInfos.getPackageInfo(contentResolver, packageName);
        Uri uri = packageInfos.getDownloadUri();
        PackageState state = packageInfos.getState();
        
        if(state.isDownloadingPaused()) {
        	if (packageInfos.getState() == PackageState.INSTALL_DOWNLOADING_PAUSED)
            	packageInfos.setState(PackageState.INSTALL_DOWNLOADING);
            else if (packageInfos.getState() == PackageState.UPDATE_DOWNLOADING_PAUSED)
            	packageInfos.setState(PackageState.UPDATE_DOWNLOADING);
        	
        	resumeDownload(contentResolver, uri);
        } else if(state.isWaitingIgnored()) {
        	if (state == PackageState.INSTALL_DOWNLOAD_WAIT_IGNORE) {
    			packageInfos.setState(PackageState.INSTALL_DOWNLOAD_WAIT);
    		} else if (state == PackageState.UPDATE_DOWNLOAD_WAIT_IGNORE) {
            	packageInfos.setState(PackageState.UPDATE_DOWNLOAD_WAIT);
    		} else if (state == PackageState.INSTALL_DOWNLOADING_PAUSED_WAIT_IGNORE) {
    			packageInfos.setState(PackageState.INSTALL_DOWNLOADING_PAUSED_WAIT);
    		} else if(state == PackageState.UPDATE_DOWNLOADING_PAUSED_WAIT_IGNORE) {
    			packageInfos.setState(PackageState.UPDATE_DOWNLOADING_PAUSED_WAIT);
    		}
        	changeToWaitDownload(contentResolver,uri);
        }else if(state.isDownloadFailed()){
        	
        	if(isReachCountLimit()) {
        		if (state == PackageState.INSTALL_DOWNLOAD_FAILED) {
        			packageInfos.setState(PackageState.INSTALL_DOWNLOADING);
        		} else if (state == PackageState.UPDATE_FAILED) {
        			packageInfos.setState(PackageState.UPDATED_INSTALLING);
        		}
        		resumeDownload(contentResolver, uri);
        	}else{

        		if (state == PackageState.INSTALL_DOWNLOAD_FAILED) {
        			packageInfos.setState(PackageState.INSTALL_DOWNLOADING_PAUSED_WAIT);
        		} else if (state == PackageState.UPDATE_FAILED) {
        			packageInfos.setState(PackageState.UPDATE_DOWNLOADING_PAUSED_WAIT);
        		}
        		
        		changeToWaitDownload(contentResolver,uri);
        	}
        	
        }
        
        packageInfos.commitChange(contentResolver);
	}
	
	private boolean isNotifyDownloadSuccess(Uri uri) {
		boolean isSuccess = false;
		
		Cursor cursor = mContext.getContentResolver().query(uri, new String[] {
							Downloads.Impl.COLUMN_STATUS,
							Downloads.Impl.COLUMN_NOTIFICATION_PACKAGE,
							Downloads.Impl.COLUMN_NOTIFICATION_CLASS,
							Downloads.Impl.COLUMN_NOTIFICATION_EXTRAS,
							Downloads.Impl.COLUMN_CURRENT_BYTES,
							Downloads.Impl.COLUMN_TOTAL_BYTES
							}, null, null, null);

		if(cursor != null && cursor.moveToFirst()) {
			int status = cursor.getInt(0);
			int currentBytes = cursor.getInt(4);
			int totalBytes = cursor.getInt(5);
			
			if(Downloads.Impl.isStatusSuccess(status)) {
				notifyDownloadComplete(cursor, uri);
		        
		        isSuccess = true;
			} else {
				if(totalBytes != -1 && currentBytes == totalBytes) {
					ContentValues values = new ContentValues();
					values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_SUCCESS);
					mContext.getContentResolver().update(uri, values, null, null);
					
					notifyDownloadComplete(cursor, uri);
					
					isSuccess = true;
				}
			}
			
			cursor.close();
		}
		
		return isSuccess;
	}
	
	private void notifyDownloadComplete(Cursor cursor, Uri uri) {
		String nofityPackage = cursor.getString(1);
		String notifyClass = cursor.getString(2);
		String notifyExtra = cursor.getString(3);
		
		Intent intent = new Intent(Downloads.ACTION_DOWNLOAD_COMPLETED);
        intent.setClassName(nofityPackage, notifyClass);
        if (notifyExtra != null) {
            intent.putExtra(Downloads.Impl.COLUMN_NOTIFICATION_EXTRAS, notifyExtra);
        }
        // We only send the content: URI, for security reasons. Otherwise, malicious
        //     applications would have an easier time spoofing download results by
        //     sending spoofed intents.
        intent.setData(uri);
        mContext.sendBroadcast(intent);
	}

	public void trimDatabase() {
		_trimDatabase();
        removeSpuriousFiles();
	}

	/**
     * Drops old rows from the database to prevent it from growing too large
     */
    private void _trimDatabase() {
        Cursor cursor = mContext.getContentResolver().query(Downloads.CONTENT_URI,
                new String[] { Downloads.Impl._ID },
                Downloads.Impl.COLUMN_STATUS + " >= '200'", null,
                Downloads.Impl.COLUMN_LAST_MODIFICATION);
        if (cursor == null) {
            // This isn't good - if we can't do basic queries in our database, nothing's gonna work
            Log.e(TAG, "null cursor in trimDatabase");
            return;
        }
        if (cursor.moveToFirst()) {
            int numDelete = cursor.getCount() - com.yingyonghui.downloads.Constants.MAX_DOWNLOADS;
            int columnId = cursor.getColumnIndexOrThrow(Downloads.Impl._ID);
            while (numDelete > 0) {
                mContext.getContentResolver().delete(
                        ContentUris.withAppendedId(Downloads.CONTENT_URI, cursor.getLong(columnId)),
                        null, null);
                if (!cursor.moveToNext()) {
                    break;
                }
                numDelete--;
            }
        }
        cursor.close();
    }
    
    /**
     * Removes files that may have been left behind in the cache directory
     */
    private void removeSpuriousFiles() {
        File[] files = Environment.getDownloadCacheDirectory().listFiles();
        if (files == null) {
            // The cache folder doesn't appear to exist (this is likely the case
            // when running the simulator).
            return;
        }
        HashSet<String> fileSet = new HashSet<String>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().equals(com.yingyonghui.downloads.Constants.KNOWN_SPURIOUS_FILENAME)) {
                continue;
            }
            if (files[i].getName().equalsIgnoreCase(com.yingyonghui.downloads.Constants.RECOVERY_DIRECTORY)) {
                continue;
            }
            fileSet.add(files[i].getPath());
        }

        Cursor cursor = mContext.getContentResolver().query(Downloads.CONTENT_URI,
                new String[] { Downloads.Impl._DATA }, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    fileSet.remove(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        Iterator<String> iterator = fileSet.iterator();
        while (iterator.hasNext()) {
            String filename = iterator.next();
            if (com.yingyonghui.downloads.Constants.LOGV) {
                Log.v(TAG, "deleting spurious file " + filename);
            }
            new File(filename).delete();
        }
    }
    
    //chendy 20131111 add start
    private String appendAnzhiDownloadUrl(int appId) {
		
		StringBuffer sb = new StringBuffer("http://m.anzhi.com/interface/index.php?");
		sb.append("action=download&softid="+appId);			
		sb.append("&channel=").append("536acbc6e6182bb1ada29264c9ad03bbb29f83ee");		
		Log.d(TAG, "appendDownloadUrl qiu: " + sb.toString());		
		return sb.toString();
	
    }
  //chendy 20131111 add end
}