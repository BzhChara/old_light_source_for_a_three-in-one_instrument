package com.zkzk.pra.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.Thread.UncaughtExceptionHandler;

import com.whswzz.prfluroanalyzer.MainActivity;
import com.whswzz.prfluroanalyzer.app.MyApp;
import com.zkzk.pra.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

public class CrashHandler implements UncaughtExceptionHandler {
	MyApp myApplication;

	public CrashHandler(MyApp myApplication) {
		this.myApplication = myApplication;
	}

	@Override
	public void uncaughtException(final Thread thread, final Throwable ex) {
		Log.i("CrashHandler", "uncaughtException:" + ex.getMessage());
		ExceptionHandler.handleException(ex);
		new Thread() {
			public void run() {
				try {
					MyApp app = MyApp.getApp();
					String version=Tools.getCurrentVersion(app);
					String time=(String) DateFormat.format("yyyy-MM-dd,hh:mm",System.currentTimeMillis());
					String path=app.getFilesDir().getAbsolutePath()+"/cresh_log.txt";
//					LogUtil.d("crash_log path="+path);
					File file=new File(path);
					if(file.exists()&&file.length()>10000000)
						file.delete();
					FileOutputStream out=new FileOutputStream(file, true);
					String msg="Version:"+version+",time:"+time+",thread="+thread.getName()+"\r\ncresh message:"+ex.getMessage()+"\r\n";
					for(StackTraceElement stack:ex.getStackTrace()) {
						msg+=stack.toString();
						msg+="\n";
					}
					out.write(msg.getBytes("UTF-8"));
					out.close();
				} catch (Exception e) {
					ExceptionHandler.handleException(e);
				}
				Looper.prepare();
//				ToastUtil.showText(myApplication.getResources().getString(R.string.reboot_silent), Toast.LENGTH_SHORT);
				Looper.loop();
				
			};
		}.start();

		Intent intent = new Intent(myApplication, MainActivity.class);
		// myApplication.startActivity(intent);
		PendingIntent pendingIntent = PendingIntent.getActivity(myApplication,
				100, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

		// AlarmManager��ʱ��
		AlarmManager alarmManager = (AlarmManager) myApplication
				.getSystemService(Context.ALARM_SERVICE);
		// RTC:�����˲�ִ������
		alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1600,
				pendingIntent);

		try {
			//����sleep��toast������
			Thread.currentThread().sleep(2000);
		} catch (Exception e) {
			// TODO: handle exception
		}
		myApplication.finish();

	}
}
