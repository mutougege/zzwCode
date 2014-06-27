package com.zzw.day140607.contentprovider;

import android.net.Uri;

public class StuInfos {
 
        public static final String DBNAME = "contentdb"; 
        public static final String TNAME = "contenttable";
        public static final int VERSION = 1;
         
        public static String ID = "_id";
        public static final String USERNAME = "username";
        public static final String DATE = "date";
        public static final String SEX = "sex";
         
         
        public static final String AUTOHORITY = "com.zzw.contenttest";
        public static final int ITEM = 1;
        public static final int ITEM_ID = 2;
        public static final int ITEM_NAME = 3;
         
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.zzw.contenttest";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.zzw.contenttest";
         
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTOHORITY + "/contenttable");
		
}