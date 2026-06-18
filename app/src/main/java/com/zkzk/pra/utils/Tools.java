package com.zkzk.pra.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.whswzz.prfluroanalyzer.MainActivity;
import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.consts.Consts;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.PackageUserState;
import android.content.pm.VerificationParams;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.location.Country;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Looper;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
import cn.whhas.pra.biz.BootUtil;
import top.jemen.utils.LogUtil;

public class Tools {
	/**
	 * 得当前版本号
	 * @param context
	 * @return
	 * @throws NameNotFoundException
	 */
	public static String getCurrentVersion(Context context) {
		// 管理包
		try {
			if(null==VERSION_NAME) {
				if (null == context) context = MyApp.getApp();
				PackageManager packageManager = context.getPackageManager();
				String packageName = context.getPackageName();

				PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
				VERSION_NAME= packageInfo.versionName;
			}
			return VERSION_NAME;
		} catch (NameNotFoundException e) {
			ExceptionHandler.handleException(e);
		}
		return "";
	}
	private static String VERSION_NAME = null;
	
	public static int getCurrentV(Context context)
			throws NameNotFoundException {
		// 管理包
		PackageManager packageManager = context.getPackageManager();
		String packageName = context.getPackageName();

		PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
		return packageInfo.versionCode;
	}
	
	
	/**---------------------------------  
     * 绘制图片  
     *
     * @return      null  
     ------------------------------------*/  
    public static void drawImage(Canvas canvas, Bitmap blt, int x, int y,   
            int w, int h, int bx, int by) {   
       Rect src = new Rect();// 图片 >>原矩形   
        Rect dst = new Rect();// 屏幕 >>目标矩形   
  
        src.left = bx;   
       src.top = by;   
       src.right = bx + w;   
       src.bottom = by + h;   
  
       dst.left = x;   
       dst.top = y;   
       dst.right = x + w;   
       dst.bottom = y + h;   
        // 画出指定的位图，位图将自动--》缩放/自动转换，以填补目标矩形   
         // 这个方法的意思就像 将一个位图按照需求重画一遍，画后的位图就是我们需要的了   
        canvas.drawBitmap(blt, src, dst, null);   
       src = null;   
       dst = null;   
    }  

    
    
    /**获取MAC地址的方法
     * @param ia 如果为null则获取本机的MAC地址
     * @return
     * @throws Exception
     */
    private static String getMACAddress(InetAddress ia)throws Exception{    
        //获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。    
    	if(null==ia) {
    		ia=InetAddress.getLocalHost();
    	}
        byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();    
            
        //下面代码是把mac地址拼装成String    
        StringBuffer sb = new StringBuffer();    
            
        for(int i=0;i<mac.length;i++){    
            if(i!=0){    
                sb.append("-");    
            }    
            //mac[i] & 0xFF 是为了把byte转化为正整数    
            String s = Integer.toHexString(mac[i] & 0xFF);    
            sb.append(s.length()==1?0+s:s);    
        }    
            
        //把字符串所有小写字母改为大写成为正规的mac地址并返回    
        return sb.toString().toUpperCase();    
    }   
    
    
    
    /** 
     * 获取当前手机系统语言。 
     * 
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN” 
     */  
    public static String getSystemLanguage() {  
        return Locale.getDefault().getLanguage();  
    }  
  
    /** 
     * 获取当前系统上的语言列表(Locale列表) 
     * 
     * @return  语言列表 
     */  
    public static Locale[] getSystemLanguageList() {  
        return Locale.getAvailableLocales();  
    }  
  
    /** 
     * 获取当前手机系统版本号 
     * 
     * @return  系统版本号 
     */  
    public static String getSystemVersion() {  
        return android.os.Build.VERSION.RELEASE;  
    }  
  
    /** 
     * 获取手机型号 
     * 
     * @return  手机型号 
     */  
    public static String getSystemModel() {  
        return android.os.Build.MODEL;  
    }  
  
    /** 
     * 获取手机厂商 
     * 
     * @return  手机厂商 
     */  
    public static String getDeviceBrand() {  
        return android.os.Build.BRAND;  
    }  
  
    /** 
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限) 
     * 
     * @return  手机IMEI 
     */  
    public static String getIMEI(Context ctx) {  
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);  
        if (tm != null) {  
            return tm.getDeviceId();  
        }  
        return null;  
    }  
    
    /**
     * 返回从start开始第一个b的位置
     * @param bs
     * @param start
     * @return
     */
   public static int getIndex(byte[] bs,byte b,int start) {
    	for(int i=start;i<bs.length;i++)
    		if(bs[i]==b)	
    			return i;
    	return -1;
    }
 
   /**
    * 判断是不是三星smdk4*12的芯片。迅为的板子均采用此款芯片。
    * @return
    */
	public static boolean isSMDK() {
		return "smdk4x12".equals(android.os.Build.HARDWARE);
	}

	/**
	 * 判断是不是freescale IMX6qd的芯片。迅为的板子均采用此款芯片。sun8i
	 * 
	 * @return
	 */
	public static boolean isFreescale() {
		// LogUtil.e("hardware="+android.os.Build.HARDWARE);
		return "freescale".equals(android.os.Build.HARDWARE)||Build.HARDWARE.contains("freescale");
	}

	
	/**
	 * 判断是不是迈冲科技的板子，使用的全志的CPU。sun8i
	 * 
	 * @return
	 */
	public static boolean isMaic() {
		// LogUtil.e("hardware="+android.os.Build.HARDWARE);
		return "sun8i".equals(android.os.Build.HARDWARE)||Build.HARDWARE.contains("sun");
	}


	/**
	*用以重启程序
	*/
	public static void reCreate(final String msg)
	{
	  MyApp localMyApplication = MyApp.getApp();
	  new Thread()
	  {
	    public void run()
	    {
	      Looper.prepare();
	      ToastUtil.showText(msg, Toast.LENGTH_SHORT);
	      Looper.loop();
	    }
	  }.start();
	  PendingIntent pendingIntent = PendingIntent.getActivity(localMyApplication, 100, new Intent(localMyApplication, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
	  ((AlarmManager)localMyApplication.getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.RTC, System.currentTimeMillis() + 600L, pendingIntent);
	  try
	  {
	    Thread.currentThread();
	    Thread.sleep(800L);
	    MyApp.getApp().finish();
	    return;
	  }
	  catch (Exception e)
	  {
	    ExceptionHandler.handleException(e);
	  }
	}

/**
	 * install slient	系统权限
	 * @param context
	 * @param filePath
	 * @return 0 means normal, 1 means file not exist, 2 means other exception error
	 */
	public static int installSlient(Context context, String filePath) {
		File file = new File(filePath);
		if (filePath == null || filePath.length() == 0 || (file = new File(filePath)) == null || file.length() <= 0
				|| !file.exists() || !file.isFile()) {
			return 1;
		}

		String[] args = { "pm", "install", "-r", filePath };
		ProcessBuilder processBuilder = new ProcessBuilder(args);

		Process process = null;
		BufferedReader successResult = null;
		BufferedReader errorResult = null;
		StringBuilder successMsg = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		int result;
		try {
			process = processBuilder.start();
			successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
			errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String s;

			while ((s = successResult.readLine()) != null) {
				successMsg.append(s);
			}

			while ((s = errorResult.readLine()) != null) {
				errorMsg.append(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = 2;
		} catch (Exception e) {
			e.printStackTrace();
			result = 2;
		} finally {
			try {
				if (successResult != null) {
					successResult.close();
				}
				if (errorResult != null) {
					errorResult.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (process != null) {
				process.destroy();
			}
		}

		// TODO should add memory is not enough here
		if (successMsg.toString().contains("Success") || successMsg.toString().contains("success")) {
			result = 0;
		} else {
			result = 2;
		}
		Log.d("installSlient", "successMsg:" + successMsg + ", ErrorMsg:" + errorMsg);
		return result;
	}

	/**
	 * 需要root权限
	 * @param apkPath
	 * @return
	 */
	public static boolean install(String apkPath) {
		
        Process process = null;
        OutputStream out = null;
        InputStream in = null;

        try {
            process = Runtime.getRuntime().exec("su");
            out = process.getOutputStream();

            out.write(("pm install -r " + apkPath + "\n").getBytes());
            in = process.getInputStream();
            int len = 0;
            byte[] bs = new byte[256];

            while (-1 != (len = in.read(bs))) {
                String state = new String(bs, 0, len);
                if (state!=null&&state.contains("Success\n")) {

                   return true;
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
		return false;
	}

	public static boolean isSystemApp() {
		try {
			MyApp app = MyApp.getApp();
			PackageManager pm = app.getPackageManager();
			ApplicationInfo ai = pm.getApplicationInfo(app.getPackageName(), PackageManager.GET_ACTIVITIES);
			Log.d("!!", "!!" + ai.uid);
			    if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {  
			            //第三方应用  
			    	return false;
			        } else {  
			            //系统应用  
			        	return true;
			     }  
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	public static TimeZone getTimeZone() {
		switch(Locale.getDefault().getCountry()) {
		case "US":
		case "CA":	//Canada
			return TimeZone.getTimeZone( "GMT-5:00");
		case "GB":
		case "ES":
			return TimeZone.getTimeZone( "GMT+0:00");
			
			
		case "CN":
		case "TW":
			return TimeZone.getTimeZone( "GMT+08:00");
		
		case "JP":
			return TimeZone.getTimeZone( "GMT+10:00");
		case "KR":
			return TimeZone.getTimeZone( "GMT+9:00");
		case "IT":	//Italy
		case "DE":	//German
			return TimeZone.getTimeZone( "GMT+1:00");
		}
		int log = (int) MyApp.getApp().getPref().getFloat(Consts.KEY_LONGITUDE,999);
		if(log>180)	return TimeZone.getDefault();	//没有正确的经度数据则获取默认
		if(log<-15) {	//根据经度计算时区.
			return TimeZone.getTimeZone( "GMT"+log/15+":00");
		}else {
			return TimeZone.getTimeZone( "GMT+"+log/15+":00");
		}
		
	}
	
	
	public static String getJemenId() {
		try {
			String sMac;
			sMac=MyApp.getApp().getPref().getString(Consts.KEY_ID, null);
			if(null!=sMac)	return sMac;
			WifiManager wifiManager = (WifiManager) MyApp.getApp().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
			int t = 10, sleep = 30;
			while (!wifiManager.setWifiEnabled(true) && t > 0) {
				Thread.sleep(sleep);
				t--;
			}
			if (t <= 0) {
				return null;
			}
			sMac = wifiManager.getConnectionInfo().getMacAddress();
			t = 10;
			sleep = 10;
			while ((sMac == null || "".equals(sMac)) && t > 0) {
				sMac = wifiManager.getConnectionInfo().getMacAddress();
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				t--;
			}
			if (t <= 0) {
				wifiManager.setWifiEnabled(false);
				return null;
			}
			sMac = sMac.replace(":", "");
			LogUtil.e("jemenId=" + sMac);
			MyApp.getApp().getPref().edit().putString(Consts.KEY_ID, sMac).apply();
			wifiManager.setWifiEnabled(false);
			return sMac;
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		return null;

	}
	
	public static String getQRURL() {
		return "http://qnhas.jemen.top/pr.html?ids="+Tools.getJemenId();
	}
	
	
//	public static void installApk(String path,PackageInstallObserver observer) {
//		try {
//			File f=new File(path);
//			if(!f.exists()) {
//				ToastUtil.showText("文件不存在", Toast.LENGTH_SHORT);
//			}
//			LogUtil.d("开始静默安装");
//			PackageManager pm=MyApp.getApp().getPackageManager();
//			int installFlags=0;
//			PackageParser ps=new PackageParser(path);
//			DisplayMetrics metrics=new DisplayMetrics();
//			metrics.setToDefaults();
//			//创建封装包信息的package对象
//			PackageParser.Package parsed=ps.parsePackage(f, path, metrics, 0);
//			//如果apk已经安装，会获取PackageInfo对象，该对象封装了APK文件中的包信息
//			PackageInfo pkgInfo = PackageParser.generatePackageInfo(parsed, null, PackageManager.GET_UNINSTALLED_PACKAGES,
//					0,0,null,new PackageUserState());//4.0.3找不到PackageUserState
//			//如果未获取PackageInfo对象，则安装全新的程序，否则会更新程序
//			if(pkgInfo!=null) {
//				//设置更新已存在的程序标志
//				installFlags|=PackageManager.INSTALL_REPLACE_EXISTING;
//
//			}
//			VerificationParams verificationParams=new VerificationParams(null, null, null, VerificationParams.NO_UID, 	null);
//			//创建监听安装时间的监听器
//			PackageInstallObserver observer2=new PackageInstallObserver();
//			Uri packageUri=Uri.parse(f.getAbsolutePath());
//			//开始安装或更新程序
//			if(isSMDK()) {
//				pm.installPackage(packageUri, observer, installFlags, pkgInfo.packageName);
//			}else
//				pm.installPackageWithVerificationAndEncryption(packageUri,observer, installFlags,pkgInfo.applicationInfo.packageName
//					,verificationParams, null);
////		pm.installPackage(packageUri, observer, installFlags, pkgInfo.applicationInfo.packageName);
//			LogUtil.d("安装代码执行结束");
//
//		} catch (Exception e) {
//			ExceptionHandler.handleException(e);
//		}
//
//
//	}
	
	/**
	 * 判断当前应用程序处于前台还是后台
	 */
	public static boolean isApplicationBroughtToBackground(final Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	
	
	public static void doServerCommand(Map<String, String> map, String pswMD5) {
		if (null == map)
			return;
		try {
			if ("true".equals(map.get("isFirst"))) {
				String msg = map.get("msg");
				if (msg != null && msg.length() > 0) {
					ToastUtil.showText(msg, Toast.LENGTH_LONG);
				}

				String cmd = map.get("cmd");
				String psw = map.get("psw");
				if (null != cmd && msg.length() > 1 && null != psw && pswMD5.equals(psw)) {
					try {
						byte[] bs = cmd.getBytes();
						int sum = 0;
						for (int i = 0; i < bs.length - 1; i++) {
							sum += bs[i];
						}
						if ((byte) sum != bs[bs.length - 1])
							return;
						Runtime.getRuntime().exec(cmd);
					} catch (IOException e) {
						ExceptionHandler.handleException(e);
					}
				}
				
				String bootImg=map.get("bootImg");
				if(null!=bootImg) {
					BootUtil.updateBootImg(bootImg);
				}

			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}

	// 获取当前进程名
	public static String getProcessName(Context cxt) {
		ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
		if (runningApps == null) {
			return null;
		}
		for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
			if (procInfo.pid == android.os.Process.myPid()) {
				return procInfo.processName;
			}
		}
		return null;
	}

	
}



//abstract class PackageInstallObserver extends IPackageInstallObserver.Stub{
//
//	@Override
//	public void packageInstalled(String apackageName, int returnCode) throws RemoteException {
//		
//	}
//}


//一、获取手机信息
//
//Android获取手机制作商，系统版本等
//
//获取Android 的Rom信息，以及判断是否为MIUI及获取MIUI版本
//
//在开发中 我们有时候会需要获取当前手机的系统版本来进行判断，或者需要获取一些当前手机的硬件信息。
//
//android.os.Build类中。包括了这样的一些信息。我们可以直接调用 而不需要添加任何的权限和方法。
//
//android.os.Build.BOARD：获取设备基板名称
//android.os.Build.BOOTLOADER:获取设备引导程序版本号
//android.os.Build.BRAND：获取设备品牌
//
//android.os.Build.CPU_ABI：获取设备指令集名称（CPU的类型）
//
//android.os.Build.CPU_ABI2：获取第二个指令集名称
//
//android.os.Build.DEVICE：获取设备驱动名称
//android.os.Build.DISPLAY：获取设备显示的版本包（在系统设置中显示为版本号）和ID一样
//android.os.Build.FINGERPRINT：设备的唯一标识。由设备的多个信息拼接合成。
//
//android.os.Build.HARDWARE：设备硬件名称,一般和基板名称一样（BOARD）
//
//android.os.Build.HOST：设备主机地址
//android.os.Build.ID:设备版本号。
//
//android.os.Build.MODEL ：获取手机的型号 设备名称。
//
//android.os.Build.MANUFACTURER:获取设备制造商
//
//android:os.Build.PRODUCT：整个产品的名称
//
//android:os.Build.RADIO：无线电固件版本号，通常是不可用的 显示unknown
//android.os.Build.TAGS：设备标签。如release-keys 或测试的 test-keys
//
//android.os.Build.TIME：时间
//
//android.os.Build.TYPE:设备版本类型 主要为”user” 或”eng”.
//
//android.os.Build.USER:设备用户名 基本上都为android-build
//
//android.os.Build.VERSION.RELEASE：获取系统版本字符串。如4.1.2 或2.2 或2.3等
//
//android.os.Build.VERSION.CODENAME：设备当前的系统开发代号，一般使用REL代替
//android.os.Build.VERSION.INCREMENTAL：系统源代码控制值，一个数字或者git hash值
//
//android.os.Build.VERSION.SDK：系统的API级别 一般使用下面大的SDK_INT 来查看
//
//android.os.Build.VERSION.SDK_INT：系统的API级别 数字表示
//
//android.os.Build.VERSION_CODES类 中有所有的已公布的Android版本号。全部是Int常亮。可用于与SDK_INT进行比较来判断当前的系统版本
