package com.zkzk.pra.model.imp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import org.xutils.image.AsyncDrawable;

import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.enzyme.entity.EnzymeData;
import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;
import com.whswzz.prfluroanalyzer.photometer.entity.PhotometerData;
import com.zkzk.pra.entity.Data;
import com.zkzk.pra.receiver.UsbReceiver;
import com.zkzk.pra.utils.Tools;

import android.os.AsyncTask;
import android.renderscript.Sampler;
import android.text.format.DateFormat;
import android.util.Log;
import top.jemen.interfaces.ICallback;
import top.jemen.utils.LogUtil;

public class FileModel {
	public static void saveUdisk(final List<Data> datas,final ICallback callback) {
		File dir=new File(UsbReceiver.PATH);
		if(!dir.exists()||dir.getUsableSpace()<100) {
			callback.onFailed("存储空间不足");
			return;
		}
		final Calendar c=Calendar.getInstance(Tools.getTimeZone());
		String path=dir.getAbsolutePath()+"/PRA-"+DateFormat.format(Consts.YMDHMS_FORMAT, c)+".xls";
		
		new AsyncTask<String,Void,String>() {
			final String SUCCESS="SUCCESS";
			@Override
			protected String doInBackground(String... params) {
					
					if(FileUtil.writeExcel(params[0], datas))
						return SUCCESS;
					else
						return "failed";
			}
			
			protected void onPostExecute(String result) {
				if(SUCCESS.equals(result)) {
					callback.onSuccess(SUCCESS);
				}else {
					callback.onFailed(result);
				}
				
			};
		}.execute(path);
	}
	
	
	public static void saveCollaurumUdisk(final List<FluData> datas,final ICallback callback) {
		File dir=new File(UsbReceiver.PATH);
		LogUtil.d(dir.getAbsolutePath());
		if(!dir.exists()||dir.getUsableSpace()<100) {
			callback.onFailed("存储空间不足。");
			return;
		}
		final Calendar c=Calendar.getInstance(Tools.getTimeZone());
		String path=dir.getAbsolutePath()+"/胶体金-"+DateFormat.format(Consts.YMDHMS_FORMAT, c)+".xls";
		
		new AsyncTask<String,Void,String>() {
			final String SUCCESS="SUCCESS";
			@Override
			protected String doInBackground(String... params) {
				
				if(FileUtil.writeCollaurumExcel(params[0], datas))
					return SUCCESS;
				else
					return "failed";
			}
			
			protected void onPostExecute(String result) {
				if(SUCCESS.equals(result)) {
					callback.onSuccess(SUCCESS);
				}else {
					callback.onFailed(result);
				}
				
			};
		}.execute(path);
	}
	public static void savePhotometerUdisk(final List<PhotometerData> datas,final ICallback callback) {
		File dir=new File(UsbReceiver.PATH);
		if(!dir.exists()||dir.getUsableSpace()<100) {
			callback.onFailed("存储空间不足");
			return;
		}
		final Calendar c=Calendar.getInstance(Tools.getTimeZone());
		String path=dir.getAbsolutePath()+"/分光-"+DateFormat.format(Consts.YMDHMS_FORMAT, c)+".xls";
		
		new AsyncTask<String,Void,String>() {
			final String SUCCESS="SUCCESS";
			@Override
			protected String doInBackground(String... params) {
				
				if(FileUtil.writePhotometerExcel(params[0], datas))
					return SUCCESS;
				else
					return "failed";
			}
			
			protected void onPostExecute(String result) {
				if(SUCCESS.equals(result)) {
					callback.onSuccess(SUCCESS);
				}else {
					callback.onFailed(result);
				}
				


			};
		}.execute(path);
	}
	
	
	
	
	public static void saveEnzymeUdisk(final List<EnzymeData> datas,final ICallback callback) {
		File dir=new File(UsbReceiver.PATH);
		if(!dir.exists()||dir.getUsableSpace()<100) {
			callback.onFailed("存储空间不足");
			return;
		}
		final Calendar c=Calendar.getInstance(Tools.getTimeZone());
		String path=dir.getAbsolutePath()+"/酶片-"+DateFormat.format(Consts.YMDHMS_FORMAT, c)+".xls";
		
		new AsyncTask<String,Void,String>() {
			final String SUCCESS="SUCCESS";
			@Override
			protected String doInBackground(String... params) {
				
				if(FileUtil.writeEnzymeExcel(params[0], datas))
					return SUCCESS;
				else
					return "failed";
			}
			
			protected void onPostExecute(String result) {
				if(SUCCESS.equals(result)) {
					callback.onSuccess(SUCCESS);
				}else {
					callback.onFailed(result);
				}
				
			};
		}.execute(path);
	}
	
	

	
}
