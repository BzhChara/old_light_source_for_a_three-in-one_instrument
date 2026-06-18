package com.zkzk.pra.biz;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.common.Callback.CancelledException;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.param.Params;
import com.zkzk.pra.R;
import com.zkzk.pra.app.Target;
import com.zkzk.pra.entity.VersionEntity;
import com.zkzk.pra.parser.UpdateParser;
import com.zkzk.pra.utils.ExceptionHandler;
import com.zkzk.pra.utils.Tools;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import top.jemen.biz.BdLocation;
import top.jemen.interfaces.ICallback;
import top.jemen.utils.LogUtil;

public class UpdateBiz {

	
	/**
	 * 下载文件，提供下载进度显示。
	 * @param handler
	 * @param apkUrl
	 */
	public static void downloadAPK2(final Handler handler, final String apkUrl) {
//		int index = apkUrl.lastIndexOf("/");
//		String fileName = apkUrl.substring(index + 1);
		String cacheRoot =MyApp.getApp().getCacheDir().getAbsolutePath();
		String filePath = cacheRoot + "/ecas.apk" ;
		LogUtil.d("path="+filePath);
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
		RequestParams sendData = new RequestParams(apkUrl);
		sendData.setSaveFilePath(filePath);
		x.http().get(sendData, new Callback.ProgressCallback<File>() {

			@Override
			public void onCancelled(CancelledException arg0) {
			}

			@Override
			public void onError(Throwable ex, boolean arg1) {
				ExceptionHandler.handleException(ex);
				Message msg=handler.obtainMessage();
				msg.what=Consts.MSG_ERROR;
				msg.obj="下载新版本不成功";
				handler.sendMessage(msg);
			}
			@Override
			public void onFinished() {
			}
			@Override
			public void onSuccess(File f) {
				String apkPath=f.getAbsolutePath();
				Log.i("apkPath", apkPath);
				Message msg=handler.obtainMessage();
				msg.what=Consts.MSG_INSTALL_APK;
				Bundle bundle=new Bundle();
				bundle.putString("apkPath", apkPath);
				msg.setData(bundle);
				handler.sendMessage(msg);
			}

			@Override
			public void onLoading(long total, long current, boolean isDownloading) {
//				LogUtil.e("total="+total+",current="+current+",进度："+(float)current/total);
				Message msg = handler.obtainMessage();
				msg.what=Consts.MSG_DOWNLOAD_PROGRESS;
				msg.arg1=(int) total;
				msg.arg2=(int) current;
				msg.sendToTarget();
			}

			@Override
			public void onStarted() {
				
				
			}

			@Override
			public void onWaiting() {
				
			}
		});
		
	}
	
	
	/**  
	 * @param updateDir  就是可以执行的文件  
	 *void  修改文件的权限，可读、可写、可执行   
	 * @date 2015年9月13日  
	 * @author liuyonghong  
	 */  
	private void setUpdateDir(File updateDir) {  
	    try {  
	        Process p = Runtime.getRuntime().exec("chmod 777 " +  updateDir );  
	        int status = p.waitFor();     
	    } catch (IOException e) {  
	        // TODO Auto-generated catch block  
	        e.printStackTrace();  
	    } catch (InterruptedException e) {  
	        // TODO Auto-generated catch block  
	        e.printStackTrace();  
	    }  
	      
	} 
	
	
	
	
	
	
	
	
	
	
	
	public static void getNewVersionInfo(final Handler handler) {
		String url ;
		if(MyApp.cmdUrl!=null&&!"".equals(MyApp.cmdUrl)) {
			url=MyApp.cmdUrl;
		}else {
			url="http://yiqi.whhas.cn:8181/Server/version?name="+Params.NAME;
		}
		String deviceId=MyApp.getApp().getPref().getString(Consts.KEY_ID, null);
		if(null!=deviceId) {
			url+="&deviceId="+deviceId;
		}
		
		String hardware=android.os.Build.HARDWARE;
		if(null!=hardware) {
			url+="&hardware="+hardware;
		}
		
		RequestParams sendData = new RequestParams(url);
		x.http().get(sendData, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				LogUtil.d("version:"+result);
				VersionEntity versionEntity = null;
				try {
					versionEntity = UpdateParser.parser(result);
					Message msg = handler.obtainMessage();
					msg.what =Consts.MSG_SHOW_VERSION;
					Bundle bundle = new Bundle();
					LogUtil.d("versionCode="+versionEntity.getVersionCode());
					bundle.putSerializable("data", versionEntity);
					msg.setData(bundle);
					handler.sendMessage(msg);
				} catch (Exception e) {
					ExceptionHandler.handleException(e);
					Message msg = handler.obtainMessage();
					msg.what =Consts.MSG_ERROR;
					msg.obj="服务器应答不正确";
					msg.sendToTarget();
				} 

			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				ExceptionHandler.handleException(ex);
				Message msg = handler.obtainMessage();
				msg.what = Consts.MSG_ERROR;
				msg.obj=MyApp.getApp().getString(R.string.connect_server_failed_try_again);
				handler.sendMessage(msg);
				getNewVersionInfoWidthURL(handler, "http://testapi.whnhs.com/index.php/Api/UpdateVison/chechvison?name=PRA");
			}
			@Override
			public void onCancelled(CancelledException cex) {
			}

			@Override
			public void onFinished() {
			}
		});

	}
	
	
	
	public static void getNewVersionInfoWidthURL(final Handler handler,String url) {
		if(!url.contains("deviceEd")) {
			String deviceId=MyApp.getApp().getPref().getString(Consts.KEY_ID, null);
			if(null!=deviceId) {
				url+="&deviceId="+deviceId;
			}
		}
		if(!url.contains("hardware")) {
			String hardware=android.os.Build.HARDWARE;
			if(null!=hardware) {
				url+="&hardware="+hardware;
			}
		}
		RequestParams sendData = new RequestParams(url);
		x.http().get(sendData, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				LogUtil.d("version:"+result);
				VersionEntity versionEntity = null;
				try {
					versionEntity = UpdateParser.parser(result);
					Message msg = handler.obtainMessage();
					msg.what =Consts.MSG_SHOW_VERSION;
					Bundle bundle = new Bundle();
					bundle.putSerializable("data", versionEntity);
					msg.setData(bundle);
					handler.sendMessage(msg);
				} catch (Exception e) {
					ExceptionHandler.handleException(e);
					Message msg = handler.obtainMessage();
					msg.what =Consts.MSG_ERROR;
					msg.obj="服务器应答不正确";
				} 
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				ExceptionHandler.handleException(ex);
				Message msg = handler.obtainMessage();
				msg.what = Consts.MSG_ERROR;
				msg.obj=MyApp.getApp().getString(R.string.connect_server_failed);
				handler.sendMessage(msg);
			}

			@Override
			public void onCancelled(CancelledException cex) {
			}

			@Override
			public void onFinished() {

			}
		});

	}
	
	
	
	public static void doBeat(final ICallback callback) {
		String url = "http://58.49.112.42:8181/Server/Beat?name="+Params.NAME;//
		String deviceId=MyApp.getApp().getPref().getString(Consts.KEY_ID, null);
		if(null!=deviceId) {
			url+="&deviceId="+deviceId;
		}
		String hardware=android.os.Build.HARDWARE;
		if(null!=hardware) {
			url+="&hardware="+hardware;
		}
		
		String version = Tools.getCurrentVersion(MyApp.getApp());
		if (null != version) {
			url += "&version=" + version;
		}
		if(Target.WIFI_MACS!=null) {
			url += "&wifi_macs=" + Target.WIFI_MACS;
		}
		if(BdLocation.ADDR!=null) {
			try {
				url+="&addr="+URLEncoder.encode(BdLocation.ADDR,"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if(Math.abs(BdLocation.LAT)>1) {
			url+="&lat="+BdLocation.LAT;
		}
		if(Math.abs(BdLocation.LON)>1) {
			url+="&lon="+BdLocation.LON;
		}
		
		
		RequestParams sendData = new RequestParams(url);
		x.http().get(sendData, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				callback.onSuccess(result);
			}
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
			}
			@Override
			public void onCancelled(CancelledException cex) {

			}
			@Override
			public void onFinished() {
			}
		});

	}
	
	
	
}
