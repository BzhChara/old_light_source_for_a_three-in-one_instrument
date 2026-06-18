package com.zkzk.pra.activity;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.base.BaseActivity;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.zkzk.pra.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import top.jemen.utils.LogUtil;

/**
 * @author Administrator
 *
 */
public class MapActivity extends BaseActivity {
	@ViewInject(R.id.mapView)
	private MapView mapView;
	// @ViewInject(R.id.bt_title_back)
	// private Button btBack;
	// @ViewInject(R.id.bt_title_edit)
	// private Button btEdit;
	private BaiduMap baiduMap;

	private LocationClient locationClient;
	private String oldCode=MyApp.getApp().getPref().getString(Consts.KEY_CITYCODE, "218");
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		MyApp.activityList.add(this);

		x.view().inject(this);
		baiduMap = mapView.getMap();
		baiduMap.setMyLocationEnabled(true);
		startLocation();
		setListeners();
	}

	private void startLocation() {
		locationClient = new LocationClient(MyApp.getApp());
		locationClient.registerLocationListener(new MyBDLocationListener());// 定位监听
		LocationClientOption option = new LocationClientOption();
		// option.setCoorType("GCJ02");//2.
		// GCJ02：是由中国国家测绘局制订的地理信息系统的坐标系统，是由WGS84坐标系经加密后的坐标系；
		option.setCoorType("bd09ll"); // BD09：百度坐标系，在GCJ02坐标系基础上再次加密。其中BD09ll表示百度经纬度坐标，
		option.setNeedDeviceDirect(true); // ���ö�λ��������ֻ���ͷ���֣�


		option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setScanSpan(10000);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(false);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
		option.setIsNeedAltitude(false);//不需要高度
		
		locationClient.setLocOption(option);
		
		locationClient.start();

	}

	private void setListeners() {
		// btBack.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// finish();
		// }
		// });
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onDestroy() {
		locationClient.stop(); // ֹͣ��λ
		baiduMap.setMyLocationEnabled(false); // �˳���ǰ�ʱֹͣ��λ
		mapView.onDestroy();
		mapView = null;
		MyApp.activityList.remove(this);
		super.onDestroy();
	}
	
	
	/**
	 * @author Jemen Chen
	 *
	 */
	public class MyBDLocationListener extends BDAbstractLocationListener {
		@Override
		public void onReceiveLocation(BDLocation bdLocation) { // 使用的百度坐标系
			if (bdLocation == null || mapView == null)
				return;
			baiduMap.clear();
			double latitude = bdLocation.getLatitude();
			double longitude = bdLocation.getLongitude();
			String cityCode=bdLocation.getCityCode();
			LogUtil.d("citycode=" + cityCode);	//218
			LogUtil.d("District=" + bdLocation.getDistrict());
			LogUtil.d("Floor=" + bdLocation.getFloor());
			LogUtil.d("Addr=" + bdLocation.getAddrStr());
			LogUtil.d("LocationDescribe=" + bdLocation.getLocationDescribe());
			if(null!=cityCode&&!"".equals(cityCode)&&!oldCode.equals(cityCode))
				MyApp.getApp().getPref().edit().putString(Consts.KEY_CITYCODE, cityCode).commit();
			Log.d("jemen", "latitude=" + latitude + "\nlongitude=" + longitude);
			if (longitude < -180 || longitude > 180) { //
				longitude = 114.44299;
				latitude = 30.519942;
			}
			// 将GPS设备采集的原始GPS坐标转换成百度坐标
			CoordinateConverter converter = new CoordinateConverter();
			converter.from(CoordType.GPS);
			LatLng currentLocation = new LatLng(latitude, longitude);
			float zoomLevel = baiduMap.getMapStatus().zoom;
			MapStatusUpdate mapeStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(currentLocation, zoomLevel);
			baiduMap.animateMapStatus(mapeStatusUpdate);
			MarkerOptions markOptions = new MarkerOptions();
			markOptions.position(currentLocation); //
			BitmapDescriptor bitmapDescripter = BitmapDescriptorFactory.fromResource(R.drawable.location);
			markOptions.icon(bitmapDescripter); //
			baiduMap.addOverlay(markOptions); //
		}

	}

}
