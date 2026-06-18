package top.jemen.ui;


import com.whswzz.prfluroanalyzer.app.MyApp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
public class MyDialogFragment extends DialogFragment{
	private OnCustomDialogListener listener;
	private String title,msg;
	public MyDialogFragment(OnCustomDialogListener listener,String title,String msg) {
		super();
		this.listener=listener;
		this.title=title;
		this.msg=msg;
		
	}
	
	public MyDialogFragment(OnCustomDialogListener listener,int titleId,int  msgId) {
		super();
		this.listener=listener;
		this.title=MyApp.getApp().getString(titleId);
		this.msg=MyApp.getApp().getString(msgId);
		
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new MyDialog(getActivity(), listener, title, msg);
	}
	
}
