package com.zkzk.pra.service;

import java.util.List;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.entity.Organization;
import com.whswzz.prfluroanalyzer.entity.jx.ParamJX;
import com.whswzz.prfluroanalyzer.model.HttpModel;

import top.jemen.utils.NetUtil;
import com.zkzk.pra.utils.Tools;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import org.xutils.http.RequestParams;
import org.xutils.x;

public class NetService extends Service {
	private NetReceiver netReceiver;
	public static boolean connected = false;// I don't know if this params will be used in the future

	public static boolean isConnected() {
		return connected;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		NetUtil.isConnected(this);
		netReceiver = new NetReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		registerReceiver(netReceiver, filter);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(netReceiver);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	class NetReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {// 如果断开连接
					connected = false;
				} else if (NetworkInfo.State.CONNECTED.equals(info.getState())) {
					connected = true;
					loadBdOfflineCache();

					String versionName=Tools.getCurrentVersion(null);

					if(versionName.contains("JX")){ //对接江西的平台需要仪器校验。
						x.http().get(new RequestParams("http://qt.debaninspect.com/qt/instrument/check?"+new ParamJX().getParams()),null);
					}else if(versionName.contains("AH")){ //安徽平台
						loadEnterprises();
					}
				}
			}
		}
	}

	private void loadBdOfflineCache() {
		if (!connected)
			return;
		// MKOfflineMap mOffline = new MKOfflineMap();
		// mOffline.init(new MKOfflineMapListener() {
		// @Override
		// public void onGetOfflineMapState(int arg0, int arg1) { //下载离线地图.
		// LogUtil.d("onGetOfflineMapStat,arg0="+arg0+",arg1="+arg1);
		// }
		// });// 传入接口事件，离线地图更新会触发该回调
		// mOffline.start(218);//218代表武汉

	}


	/**
	 * 安徽平台对接使用
	 */
	private void loadEnterprises() {
		List<Organization> users = MyApp.getApp().getOrganizations();
		if(null==users||users.size()==0) {
			return;
		}
		Organization u=users.get(0);
		((HttpModel)HttpModel.get()).getSourceAH(u);
	}
}
