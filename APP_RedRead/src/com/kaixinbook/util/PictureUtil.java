package com.kaixinbook.util;

import java.io.File;
import java.util.ArrayList;

public class PictureUtil {
	
	public static ArrayList<String> GetImageFiles(String Path){
		ArrayList<String> lstFile = new ArrayList<String>();  //��� List
	    File[] files = new File(Path).listFiles();
	  
	    for (int i = 0; i < files.length; i++){
	        File f = files[i];
	        if (f.isFile()){
	        	  //�ж���չ��
	            if (f.getPath().substring(f.getPath().length() - "jpg".length()).equals("jpg")||
	            		f.getPath().substring(f.getPath().length() - "png".length()).equals("png")||
	            		f.getPath().substring(f.getPath().length() - "bmp".length()).equals("bmp")){
	                lstFile.add(f.getPath());
	            }
	        }
	    }
	    return lstFile;
	}

}
