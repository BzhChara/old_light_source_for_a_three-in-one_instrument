package com.zkzk.pra.biz;

import java.util.List;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.zkzk.pra.app.Target;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.widget.Toast;
import top.jemen.biz.BdLocation;
import top.jemen.utils.LogUtil;
import top.jemen.utils.threadpool.AsyncProcessor;

/**
 * do something,used for location. 
 * @author Jemen Chen
 *
 */
public class LocBiz {
	public  void scanMac(Application context) {
		new Handler().postDelayed(new LocRunnable(context), 20000);
	}
	
	private class LocRunnable implements Runnable{
		private Context context;
		public LocRunnable(Context context) {
			this.context=context;
		}
		@Override
		public void run() {
			final WifiManager mWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			IntentFilter filter = new IntentFilter();
			filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
			filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
			context.registerReceiver(new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					final String action = intent.getAction();
					if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
						List<ScanResult> results = mWifi.getScanResults();
						Target.WIFI_MACS="";
						for(ScanResult r:results) {
							Target.WIFI_MACS+=r.BSSID+',';
						}
						LogUtil.d("macs="+Target.WIFI_MACS);
						context.unregisterReceiver(this);
					}if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
						int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
						switch (wifiState) {
						case WifiManager.WIFI_STATE_ENABLED:
							mWifi.startScan();
							break;
						}
					}
				}
				
			}, filter);

//			if(!mWifi.isWifiEnabled()) {
//				mWifi.setWifiEnabled(true);  //上成仪器不再使用
//			}else {
//				mWifi.startScan();
//			}

			if(mWifi.isWifiEnabled()){
				mWifi.startScan();
			}


			new BdLocation().location(MyApp.getApp());
		}
	};
	
	
}
