package cn.whhas.pra.biz;

import java.io.DataOutputStream;
import java.io.File;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import android.util.Log;
import top.jemen.utils.LogUtil;
import top.jemen.utils.threadpool.AsyncProcessor;

public class BootUtil {

	public static void updateBootImg(String url) {
		
		RequestParams sendData = new RequestParams(url);
		final String filePath="/sdcard/xxx";
		sendData.setSaveFilePath(filePath);
		x.http().get(sendData, new Callback.ProgressCallback<File>() {
			@Override
			public void onCancelled(CancelledException arg0) {
			}
			@Override
			public void onError(Throwable arg0, boolean arg1) {
				LogUtil.e("ERROR:下载失败");
			}

			@Override
			public void onFinished() {
			}

			@Override
			public void onSuccess(File arg0) {
				LogUtil.e("下载成功");
				AsyncProcessor.executeTask(new Runnable() {
					@Override
					public void run() {	//系统编译时候关掉了root权限，在这板子上用不了。
						exusecmd("mount -o rw,remount /system");
						exusecmd("chmod 777 /system/media/");
						exusecmd("rm /system/media/bootanimation.zip");
						exusecmd("cp "+filePath+" /system/media/bootanimation.zip");
						exusecmd("chmod 777 /system/media/bootanimation.zip");
					}
				});
			}

			@Override
			public void onLoading(long arg0, long arg1, boolean arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStarted() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onWaiting() {
				// TODO Auto-generated method stub
				
			}
		
			
		});
		
		
		
		
	}
	
	
	// 翻译并执行相应的adb命令
	public static boolean exusecmd(String command) {
		Process process = null;
		DataOutputStream os = null;
		boolean r=false;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			Log.d("updateFile", "======000==writeSuccess======");
			process.waitFor();
			r=true;
		} catch (Exception e) {
			Log.d("updateFile", "======111=writeError======" + e.toString());
			r= false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (process != null) {
					process.destroy();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return r;
	}


}
