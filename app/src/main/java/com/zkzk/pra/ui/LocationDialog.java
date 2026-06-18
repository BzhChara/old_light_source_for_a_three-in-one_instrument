package com.zkzk.pra.ui;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.zkzk.pra.R;
import com.zkzk.pra.entity.Location;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import top.jemen.utils.LogUtil;

public class LocationDialog extends BaseDialog{
	private Context mContext;
	private ImageButton ibClose;
	private MapView mapView;
	private LocationClient locationClient;
	private String oldCode=MyApp.getApp().getPref().getString(Consts.KEY_CITYCODE, "218");
	private BaiduMap baiduMap;
	private TextView tvResult;
	private Location location;
	
	public LocationDialog(Context context) {
		super(context,R.style.dialog);
    	mContext=context;
	}

	  @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.dialog_location);
	        init();
	        setListeners();
	        startLocation();
	    }

	private void setListeners() {
		ibClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor edit = MyApp.getApp().getPref().edit();
				edit.putFloat(Consts.KEY_LANGUAGE, (float) location.getLatitude());
				edit.putFloat(Consts.KEY_LONGITUDE, (float) location.getLongitude());
				edit.putString(Consts.KEY_DESCRIBE, location.getDescribe());
				edit.commit();
				dismiss();
			}
		});
	}

	private void init() {
		ibClose=(ImageButton) findViewById(R.id.ib_dialog_close);
		mapView=(MapView) findViewById(R.id.mapView_dialog);
		tvResult=(TextView) findViewById(R.id.tv_result);
		baiduMap = mapView.getMap();
		baiduMap.setMyLocationEnabled(true);
		location=MyApp.getApp().getLocation();
	}
	
	
	
	@Override
	protected void onStop() {
		locationClient.stop(); // ֹͣ��λ
		baiduMap.setMyLocationEnabled(false); // �˳���ǰ�ʱֹͣ��λ
		mapView.onDestroy();
		mapView = null;
		super.onStop();
	}
	
	private void startLocation() {
		locationClient = new LocationClient(mContext);
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
	
	
	/**
	 * @author Jemen Chen
	 *
	 */
	private class MyBDLocationListener extends BDAbstractLocationListener{
		private float zoomLevel=0;

		@Override
		public void onReceiveLocation(BDLocation bdLocation) { // 使用的百度坐标系
			if (bdLocation == null || mapView == null)
				return;
			baiduMap.clear();
			double latitude = bdLocation.getLatitude();
			double longitude = bdLocation.getLongitude();
			String describe=bdLocation.getLocationDescribe();
			String cityCode=bdLocation.getCityCode();
			
			LogUtil.d("citycode=" + cityCode);	//218
			LogUtil.d("District=" + bdLocation.getDistrict());
			LogUtil.d("Floor=" + bdLocation.getFloor());
			LogUtil.d("Addr=" + bdLocation.getAddrStr());
			
			LogUtil.d("LocationDescribe=" + describe);
			if(null!=cityCode&&!"".equals(cityCode)&&!oldCode.equals(cityCode))
				MyApp.getApp().getPref().edit().putString(Consts.KEY_CITYCODE, cityCode).commit();
			
			Log.d("jemen", "latitude=" + latitude + "\nlongitude=" + longitude);
			if (longitude < -180 || longitude > 180) { //
				longitude = 114.44299;
				latitude = 30.519942;
			}
			tvResult.setText("经度："+longitude+"\n纬度："+latitude+"\n描述："+describe);
			
			// 将GPS设备采集的原始GPS坐标转换成百度坐标
			CoordinateConverter converter = new CoordinateConverter();
			converter.from(CoordType.GPS);
			LatLng currentLocation = new LatLng(latitude, longitude);
			if(zoomLevel==0)
				zoomLevel=10;
			else
				zoomLevel = baiduMap.getMapStatus().zoom;
			MapStatusUpdate mapeStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(currentLocation, zoomLevel);
			baiduMap.animateMapStatus(mapeStatusUpdate);
			
			
			MarkerOptions markOptions = new MarkerOptions();
			markOptions.position(currentLocation); //
			BitmapDescriptor bitmapDescripter = BitmapDescriptorFactory.fromResource(R.drawable.location);
			markOptions.icon(bitmapDescripter); //
			baiduMap.addOverlay(markOptions); //
			location.setLatitude(latitude);
			location.setLongitude(longitude);
			location.setDescribe(describe);
		}

	}

}
