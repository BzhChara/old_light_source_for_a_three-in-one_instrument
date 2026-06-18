package org.xutils;


import com.whswzz.prfluroanalyzer.app.MyApp;

import top.jemen.utils.NetUtil;

import android.os.Handler;
import android.os.Message;
import top.jemen.utils.LogUtil;

/**
 * this class is used for find when and where the ANR may happened.
 * @author Jemen Chen
 *
 */
public class ANRWatchDog {
	public static ANRWatchDog dog;
	
	public static final int MESSAGE_WATCHDOG_TIME_TICK = 0;
	/**
	 * 判定Activity发生了ANR的时间，必须要小于5秒，否则等弹出ANR，可能就被用户立即杀死了。
	 */
	public static final int ACTIVITY_ANR_TIMEOUT = 5000;

	private  int lastTimeTick = -1;
	private  int timeTick = 0;
	
	
	private ANRWatchDog() {
		
	}
	
	public static ANRWatchDog get() {
		if(null==dog) {
			dog=new ANRWatchDog();
		}
		return dog;
	}
	
	public void start() {
		watch.start();
	}


	private Handler watchDogHandler = new android.os.Handler() {
		@Override
		public void handleMessage(Message msg) {
			timeTick++;
//			timeTick = timeTick % Integer.MAX_VALUE;//没必要，浪费。
		}
	};

	
	Thread watch=new Thread(){
		@Override
		public void run() {
			setName("ANRWatchDog");
			while (true) {
				watchDogHandler.sendEmptyMessage(MESSAGE_WATCHDOG_TIME_TICK);
				try {
					Thread.sleep(ACTIVITY_ANR_TIMEOUT);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// 如果相等，说明过了ACTIVITY_ANR_TIMEOUT的时间后watchDogHandler仍没有处理消息，已经ANR了
				if (timeTick == lastTimeTick) {
					ANRException ex = new ANRException();
					String msg = "ANR message:" + ex.getMessage() + "\r\n";
					for (StackTraceElement stack : ex.getStackTrace()) {
						msg += stack.toString();
						msg += "\n";
					}
					if (NetUtil.isConnected(MyApp.getApp())) {
//						NetModel.getModel().uploadMsg(msg);
						timeTick++;//避免卡主时重复上报。
					}
					LogUtil.e("jemen:ANRWatchDog found some problem");
					throw ex;
				} else {
					lastTimeTick = timeTick;
				}
			}
		}
	};
	
	
	
}
