package com.zzw.day140616_mgnfonttest;

import android.content.Context;
import android.graphics.Typeface;

public class Utils {

	public static Typeface getDefaultTypeface(Context paramContext) {
		return getTypefaceByFontName(paramContext, "Mongolian White");
	}

	public static Typeface getTypefaceByFontName(Context paramContext,
			String paramString) {
		Typeface localTypeface;
		if ("Mongolian White".equals(paramString)) {
			localTypeface = Typeface.createFromAsset(paramContext.getAssets(),
					"fonts/MongolianWhite.ttf");
		} else if ("Mongolian Writing".equals(paramString)) {
			localTypeface = Typeface.createFromAsset(paramContext.getAssets(),
					"fonts/MongolianWriting.ttf");
		} else if ("Mongolian Art".equals(paramString)) {
			localTypeface = Typeface.createFromAsset(paramContext.getAssets(),
					"fonts/MongolianArt.ttf");
		} else {
			localTypeface = Typeface.createFromAsset(paramContext.getAssets(),
					"fonts/MongolianWhite.ttf");
		}
		return localTypeface;
	}
}