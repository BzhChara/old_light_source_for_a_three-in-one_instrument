package org.xutils.app;

import android.app.Application;
import android.content.Context;

public abstract class JApp extends Application{
	protected static JApp app;
	@Override
	public void onCreate() {
		super.onCreate();
		
	}
	public  static  Context getApp() {
		return app;
	}

}
