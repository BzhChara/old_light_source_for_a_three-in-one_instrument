package com.zkzk.pra.utils;


import com.whswzz.prfluroanalyzer.app.MyApp;
import com.zkzk.pra.R;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtil {
	public static final int SHORT = Toast.LENGTH_SHORT;
	public static final int LONG = Toast.LENGTH_LONG;
	private static TextView tv;

	/**jemen:
	 * Make a standard toast that just contains a text view.运行在主线程
	 *
	 * @param text
	 *            The text to show. Can be formatted text.
	 * @param duration
	 *            How long to display the message. Either {@link #LENGTH_SHORT} or
	 *            {@link #LENGTH_LONG}
	 */
	public static synchronized void showText(CharSequence text, int duration) {
		if (null == toast) {
			Context context = MyApp.getApp();
			View toastRoot = LayoutInflater.from(context).inflate(R.layout.mytoast, null);
			toast = new Toast(context);
			toast.setView(toastRoot);
			tv = (TextView) toastRoot.findViewById(R.id.msg);
		}
		tv.setText(text);
		toast.setDuration(duration);
		toast.show();

	}

	/**jemen:
	 * 主线程显示一个对toast提示
	 * 
	 * @param textId
	 *            The id of the text to show. Can be formatted text.
	 * @param duration
	 *            How long to display the message. Either {@link #LENGTH_SHORT} or
	 *            {@link #LENGTH_LONG}
	 *
	 */
	public static void showText(int textId, int duration) {
		String text = MyApp.getApp().getResources().getString(textId);
		showText(text,duration);
	}
	
	
	
	
	
	
    private static Handler handler = new Handler(Looper.getMainLooper());  
    
    private static Toast toast = null;  
      
    private static Object synObj = new Object();  
  
    /** * 子线程显示一个tost
     * @param act
     * @param msg
     */
    public static void postMessage( final String msg) {  
        postMessage( msg, Toast.LENGTH_SHORT);  
    }  
  
    /**
     *  * 子线程显示一个tost
     * @param act
     * @param msg
     */
    public static void postMessage( final int msg) {  
        postMessage(MyApp.getApp().getString(msg), Toast.LENGTH_SHORT);  
    }  
  
    /**
     * 子线程显示一个tost
     * @param act
     * @param msg
     * @param len
     */
    public static void postMessage( final String msg,  
            final int len) {  
                handler.post(new Runnable() {  
                    @Override  
                    public void run() {  
                        synchronized (synObj) {  
                            if (toast != null) {  
//                                toast.cancel();  
                            } else {  
                            	Context context = MyApp.getApp();
                    			View toastRoot = LayoutInflater.from(context).inflate(R.layout.mytoast, null);
                    			toast = new Toast(context);
                    			toast.setView(toastRoot);
                    			tv = (TextView) toastRoot.findViewById(R.id.msg);
                            }  
                            tv.setText(msg);
                            toast.setDuration(len);
                            toast.show();  
                        }  
                    }  
                });  
    }  
  
  
	
	
	
	
	
	
	

}
