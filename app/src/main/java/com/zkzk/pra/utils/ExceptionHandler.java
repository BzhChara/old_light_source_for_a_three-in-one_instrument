package com.zkzk.pra.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.whswzz.prfluroanalyzer.app.Build;
import com.whswzz.prfluroanalyzer.app.MyApp;

import android.content.Context;
import android.util.Log;

public class ExceptionHandler {
	public static void handleException(Throwable e){
		if(!Build.DEBUG){	//是否已经发布
			try {
				StringWriter stringWriter=new StringWriter();//
				PrintWriter printWriter=new PrintWriter(stringWriter);
				e.printStackTrace(printWriter);
				String errorInfo=stringWriter.toString();
				File file=MyApp.getApp().getDir("err_log",Context.MODE_PRIVATE);
				 DataOutputStream out = new DataOutputStream(new FileOutputStream(file, true));
				out.writeUTF(errorInfo);
				out.close();
				//jemen：将收集的信息发送到服务器
				Log.w("jemen", "errorInfo="+errorInfo);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}else{
			//没有发布则
			e.printStackTrace();
		}
	}
}
