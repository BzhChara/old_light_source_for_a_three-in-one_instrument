package com.zkzk.pra.utils;

import java.lang.reflect.Method;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.util.Log;
import top.jemen.utils.LogUtil;

public class Utils {
	   static IMountService iMountService;  
	   
	    static {  	//jemen:其实本人编译使用了android源码，可以不依赖于反射。
	        Log.v("DWXD", android.os.Environment.getExternalStorageDirectory().toString());  
	        try {  
	            Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);  
	            IBinder binder = (IBinder) method.invoke(null, "mount");  
	            iMountService = IMountService.Stub.asInterface(binder);  
	            
//	            ServiceManager.getService("mount");//引入源码
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	    }  
	  
	    /**
	     * 
	     */
	    public static void unMount() {  
	        try {  
	            iMountService.unmountVolume(android.os.Environment.getExternalStorageDirectory().toString(), true, true);  
	        } catch (RemoteException e) {  
	            e.printStackTrace();  
	        }  
	    }  
	  
	    /**
	     * 
	     */
	    public static void unMount(String path) {  
	        try {  
	            iMountService.unmountVolume(path, true, true);  
	            LogUtil.d("执行unmount操作");
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	    } 
	    
	    /**
	     * 挂载外部存储设备
	     */
	    static void mount(){  
	        try {  
	            iMountService.mountVolume(android.os.Environment.getExternalStorageDirectory().toString());  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	    }  
}
