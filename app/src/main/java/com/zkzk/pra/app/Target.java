package com.zkzk.pra.app;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.consts.Consts;

public class Target {
	public static boolean mapTag=false;
	public static String WIFI_MACS;
	static {
		mapTag=MyApp.getApp().getPref().getBoolean(Consts.KEY_MAP_TAG, mapTag);
	}
}
