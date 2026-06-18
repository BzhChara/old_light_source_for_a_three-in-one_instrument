package top.jemen.ui;


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
//			if( MyApp.getApp().lightenScreen())//是否已经点亮在函数里边会进行判断。
//				return true;
		}
		return super.dispatchTouchEvent(ev);
	}

}
