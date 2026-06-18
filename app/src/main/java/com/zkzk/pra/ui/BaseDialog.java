package com.zkzk.pra.ui;


import com.whswzz.prfluroanalyzer.app.MyApp;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;

public class BaseDialog extends Dialog{

	public BaseDialog(Context context) {
		super(context);
	}
	
	public BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}





	public BaseDialog(Context context, int theme) {
		super(context, theme);
	}





	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(ev.getAction()==MotionEvent.ACTION_UP) {
			if( MyApp.getApp().lightenScreen())//是否已经点亮在函数里边会进行判断。
				return true;
		}
		if(null!=this&&null!=ev)//the monkey test found a NullpointerException
			return super.dispatchTouchEvent(ev);
		return false;
	}

}
