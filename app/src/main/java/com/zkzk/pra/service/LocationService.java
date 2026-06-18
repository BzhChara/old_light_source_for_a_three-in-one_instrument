package com.zkzk.pra.service;

import java.util.List;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.zkzk.pra.utils.GpsToBaiDuXY;
import com.zkzk.pra.utils.ToastUtil;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.widget.Toast;
import top.jemen.utils.LogUtil;

public class LocationService extends Service {
	private LocationManager locationManager;
	private Handler handler;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		handler = new Handler();

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER	);
		if(null ==location){
			location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if(null==location){
			location=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		}
		if (location == null)
			location = locationManager.getLastKnownLocation(LocationManager.FUSED_PROVIDER);
		if (null != location) {
			MyApp.getApp().getLocation().setLatitude(location.getLatitude());
			MyApp.getApp().getLocation().setLatitude(location.getLongitude());
			LogUtil.e("locationServer:longitude" + "=" + location.getLongitude() + ",la=" + location.getLatitude());
		} else {
			LogUtil.e("LocationService,location=null");
		}

		startLocate();
	}

	private void startLocate() {
		LogUtil.d("开始定位");
		openGPS(true);
		List<String> providers = locationManager.getProviders(false);
		LogUtil.d("providers=" + providers);
		if (null == providers || providers.size() < 1)
			return;
		String provider = null;
		if (providers.contains(LocationManager.GPS_PROVIDER))
			provider = LocationManager.GPS_PROVIDER;
		else if (providers.contains(LocationManager.NETWORK_PROVIDER))
			provider = LocationManager.NETWORK_PROVIDER;
		else
			provider = providers.get(0);
		
	
//		provider=LocationManager.NETWORK_PROVIDER;

		if (null != provider)
			locationManager.requestLocationUpdates(provider, 1000, 5, locationListener);
	}

	@Override
	public void onDestroy() {
		if (null != locationManager) {
			locationManager.removeUpdates(locationListener);
		}
		openGPS(false);
		super.onDestroy();
	}

	private LocationListener locationListener = new LocationListener() {
		int t = 0;

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			LogUtil.d("location state chagede");
		}

		@Override
		public void onProviderEnabled(String provider) {
			LogUtil.d("location provider enabled");
		}

		@Override
		public void onProviderDisabled(String provider) {
			LogUtil.d(" location provider disabled");
		}

		@Override
		public void onLocationChanged(Location location) {	//此处获得的坐标与百度坐标不一致。在百度上需要转换.
			double lct[] =GpsToBaiDuXY.wgs2bd(location.getLatitude(), location.getLongitude());
			MyApp.getApp().getLocation().setLatitude(lct[0]);
			MyApp.getApp().getLocation().setLongitude(lct[1]);
			//			LogUtil.e("LocationService:longitude=" + location.getLongitude() + ",la=" + location.getLatitude());
			LogUtil.e("转换后:longitude=" +lct[0] + ",la=" + lct[1]);
			t++;
			if (t == 4) {
				t = 0;
				locationManager.removeUpdates(locationListener);
				handler.postDelayed(startRunnable, 600000);
			}
		}
	};

	Runnable startRunnable = new Runnable() {
		@Override
		public void run() {
			LogUtil.d("休眠结束");
			if(!isOpen()) {
				openGPS(true);
			}
			locationManager.requestLocationUpdates("gps", 1000, 5, locationListener);
		}
	};

	// 打开或者关闭gps
	public void openGPS(boolean open) {
		if (open) {

			/************* 此方案经过测试可强制打开定位功能 ******************************/
			Intent GPSIntent = new Intent();
			GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
			GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
			GPSIntent.setData(Uri.parse("custom:3"));
			try {
				PendingIntent.getBroadcast(this, 0, GPSIntent, 0).send();
			} catch (CanceledException e) {
				e.printStackTrace();
			}
		}else {
			 Settings.Secure.putInt(this.getContentResolver(),
			 Settings.Secure.LOCATION_MODE,
			 android.provider.Settings.Secure.LOCATION_MODE_OFF);
		}

		// if (Build.VERSION.SDK_INT <19) {
		// Secure.setLocationProviderEnabled(context.getContentResolver(),
		// LocationManager.GPS_PROVIDER, open);
		// }else{
		// if(!open){
		// Settings.Secure.putInt(context.getContentResolver(),
		// Settings.Secure.LOCATION_MODE,
		// android.provider.Settings.Secure.LOCATION_MODE_OFF);
		// }else{
		// Settings.Secure.putInt(context.getContentResolver(),
		// Settings.Secure.LOCATION_MODE,
		// android.provider.Settings.Secure.LOCATION_MODE_BATTERY_SAVING);
		// }
		// }
	}

	// 判断gps是否处于打开状态
	public boolean isOpen() {
		Context context = this;
		if (Build.VERSION.SDK_INT < 19) {
			LocationManager myLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			return myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} else {
			int state = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
					Settings.Secure.LOCATION_MODE_OFF);
			if (state == Settings.Secure.LOCATION_MODE_OFF) {
				return false;
			} else {
				return true;
			}
		}
	}

}
