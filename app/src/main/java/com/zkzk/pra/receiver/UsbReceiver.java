package com.zkzk.pra.receiver;

import java.io.File;
import com.zkzk.pra.R;
import com.zkzk.pra.utils.ExceptionHandler;
import com.zkzk.pra.utils.ToastUtil;
import com.zkzk.pra.utils.Tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import top.jemen.utils.LogUtil;
public class UsbReceiver extends BroadcastReceiver {
	public final static String ACTION ="android.hardware.usb.action.USB_STATE";
	public static boolean udiskExist=false;
	public static String PATH="/mnt/media_rw/udisk";
	static {
		if(Tools.isMaic()) { //迈冲屏默认位置
			PATH="/storage/usbhost1";
		}
	}
	
	public UsbReceiver() {
		super();
	}

	/**用于检测是否连接有U盘
	 * 广播放在静态代码块还是构造方法中都不会开机执行，所以用一个静态方法开机时检测
	 */
	public static void usbDetect() {
		try {
			File dir=new File(PATH);
			if(!dir.exists()||dir.getUsableSpace()<100) {
				udiskExist=false;
			}else {
				udiskExist=true;
			}
			LogUtil.d("U盘存在？："+udiskExist);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}
	




	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		LogUtil.d("action="+action);
		
		switch(action) {
		case "android.hardware.usb.action.USB_STATE":
			boolean connected = intent.getExtras().getBoolean("connected");
			ToastUtil.showText("aciton =" + connected,Toast.LENGTH_SHORT);
			if (connected) {
				ToastUtil.showText(R.string.usb_connected,Toast.LENGTH_SHORT);
			} else {
				ToastUtil.showText(R.string.usb_disconnected,Toast.LENGTH_SHORT);
			}
			break;
		case "android.intent.action.MEDIA_MOUNTED":
			ToastUtil.showText(R.string.media_mounted,Toast.LENGTH_SHORT);
			String path = intent.getDataString();
			if(TextUtils.isEmpty(path)){
				return;
			}

			String pathString = path.split("file://")[1];//U盘路径
//             Log.e("TAG", "U盘插入" + pathString);
			String usb_path = pathString + "/";//加一个反斜杠
			Log.e("getExternalPath--", usb_path);
			PATH=usb_path;
			udiskExist = true;
			break;
		case "android.intent.action.MEDIA_UNMOUNTED":
			ToastUtil.showText(R.string.media_unmounted,Toast.LENGTH_SHORT);
			break;
		case "android.hardware.usb.action.USB_DEVICE_ATTACHED"://UsbManager.ACTION_USB_DEVICE_ATTACHED://;
			ToastUtil.showText(R.string.media_mounted, Toast.LENGTH_SHORT);
			udiskExist=true;
			final UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
			if(null!=device) {
				Log.d("jemen","device:"+device.getDeviceName());
			}
			else {
				Log.d("jemen","device="+device);
			}
//			path = intent.getDataString();
//			LogUtil.d("ATTACHED 获取path："+path); //null
			break;
		case "android.hardware.usb.action.USB_DEVICE_DETACHED":// UsbManager.ACTION_USB_ACCESSORY_DETACHED://;
			ToastUtil.showText(R.string.media_unmounted, Toast.LENGTH_SHORT);
			udiskExist=false;
			break;
		
		}
	}


}
