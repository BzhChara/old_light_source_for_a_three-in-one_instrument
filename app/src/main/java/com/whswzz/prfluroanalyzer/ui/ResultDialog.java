//package com.whswzz.prfluroanalyzer.ui;
//
//import java.util.Calendar;
//import java.util.List;
//
//import org.xutils.x;
//
//import com.baidu.location.BDAbstractLocationListener;
//import com.baidu.location.BDLocation;
//import com.baidu.location.LocationClient;
//import com.baidu.location.LocationClientOption;
//import com.baidu.mapapi.map.BaiduMap;
//import com.baidu.mapapi.map.BitmapDescriptor;
//import com.baidu.mapapi.map.BitmapDescriptorFactory;
//import com.baidu.mapapi.map.MapStatusUpdate;
//import com.baidu.mapapi.map.MapStatusUpdateFactory;
//import com.baidu.mapapi.map.MapView;
//import com.baidu.mapapi.map.MarkerOptions;
//import com.baidu.mapapi.model.LatLng;
//import com.baidu.mapapi.utils.CoordinateConverter;
//import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
//import com.whswzz.prfluroanalyzer.app.MyApp;
//import com.whswzz.prfluroanalyzer.consts.Consts;
//import com.whswzz.prfluroanalyzer.entity.Source;
//import com.zkzk.pra.R;
//import com.zkzk.pra.activity.DetectActivity;
//import com.zkzk.pra.app.Target;
//import com.zkzk.pra.entity.Data;
//import com.zkzk.pra.entity.Location;
//import com.zkzk.pra.utils.ListUtil;
//import com.zkzk.pra.utils.Tools;
//
//import android.content.Context;
//import android.content.SharedPreferences.Editor;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.text.format.DateFormat;
//import android.view.View;
//import android.view.ViewGroup.LayoutParams;
//import android.view.WindowManager;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.AdapterView.OnItemSelectedListener;
//import top.jemen.ui.BaseDialog;
//import top.jemen.utils.LogUtil;
//
//public class ResultDialog extends BaseDialog {
//	private TextView  tvChannel, tvAbsorbancy, tvInhabition, tvLimit, tvResult, tvSn, tvSpecimen, tvTargetUnit,
//			tvProj;
//	private Button btPrint;
//	private MapView mapView;
//	private LocationClient locationClient;
//	private String oldCode = MyApp.getApp().getPref().getString(Consts.KEY_CITYCODE, "218");
//	private BaiduMap map;
//	private TextView tvLocation;
//	private Location location;
//
//	private Data data;
//	public static final String COLON = "：";
//
//	private Context context;
//	private TextView tvTime;
//	private FrameLayout fl;
//
//	public ResultDialog(Context context, Data data) {
//		super(context, R.style.dialog);
//		this.context = context;
//		this.data = data;
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		setContentView(R.layout.dialog_result);
//		initView();
//		initValue();
//		if (context instanceof DetectActivity) { // 样品信息设置null == data.getResult()
//			setListeners();
//		}
//	}
//
//	@Override
//	protected void onStart() {
//		super.onStart();
//		if (!Target.mapTag) {
//			TextView tv = new TextView(getContext());
//			tv.setText(getString(R.string.map_on_guide));
//			fl.addView(tv);
//			return;
//		}
//	}
//
//	private void setListeners() {
//		btPrint.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//			}
//		});
//
//	}
//
//	/**
//	 * 初始化控件
//	 */
//	private void initView() {
//		tvChannel = (TextView) findViewById(R.id.tv_channel);
//		tvProj = (TextView) findViewById(R.id.tv_proj);
//		tvAbsorbancy = (TextView) findViewById(R.id.tv_user);
//		tvInhabition = (TextView) findViewById(R.id.tv_inhibition_ratio);
//		tvLimit = (TextView) findViewById(R.id.tv_limit);
//		tvResult = (TextView) findViewById(R.id.tv_result);
//		btPrint = (Button) findViewById(R.id.bt_print);
//		fl = (FrameLayout) findViewById(R.id.fl);
//		// mapView = (MapView) findViewById(R.id.mapView_dialog);
//		// tvLocation = (TextView) findViewById(R.id.tv_location);
//		tvTime = (TextView) findViewById(R.id.tv_time);
//
//		location = MyApp.getApp().getLocation();
//
//		tvSn = (TextView) findViewById(R.id.tv_sn);
//		tvSpecimen = (TextView) findViewById(R.id.tv_specimen);
//		tvTargetUnit = (TextView) findViewById(R.id.tv_source);
//
//	}
//
//	/**
//	 * jemen：根据使用场景不同而加载不同的控件
//	 */
//	private void initValue() {
//		tvChannel.setText(getString(R.string.channel_number) + COLON + data.getChannel());
//		tvProj.setText(getString(R.string.detect_proj) + COLON + data.getProj());
//		btPrint.setVisibility(View.GONE);
//		tvSn.setText(getString(R.string.specimen_sn_) + data.getSn());
//		tvSpecimen.setText(getString(R.string.specimen_name_) + data.getSpecimen());
//		tvTargetUnit.setText(getString(R.string.customer_org_) + data.getSource().getUnit());
//		Calendar c = Calendar.getInstance(Tools.getTimeZone());
//		c.setTimeInMillis(data.getTime());
//		tvTime.setText(getString(R.string.detect_time) + "：" + DateFormat.format("yyyy-MM-dd k:mm", c));
//
//		tvAbsorbancy.setText(getString(R.string.absorbancy) + COLON + String.format("%.3f", data.getAbsorbancy()));
//		float inhibition = data.getInhibitionRatio();
//		String name = getString(R.string.inhibition_ratio) + COLON;
//		if (inhibition > Consts.ALL) {
//			tvInhabition.setText(name + "100%");
//		} else {
//			tvInhabition.setText(name + (int) (inhibition * 100) + "%");
//		}
//		// tvInhabition.setText(name+ String.format("%.1f", data.getInhibitionRatio() *
//		// 100) + "%");
//		tvLimit.setText(getString(R.string.limit_range) + COLON + data.getLimit());
//		tvResult.setText(getString(R.string.result) + COLON + data.getResult());
//	}
//
//	private String getString(int id) {
//		return getContext().getString(id);
//	}
//
//	@Override
//	protected void onStop() {
//		if (data.getResult() == null && null != locationClient) {// 样品信息设置。
//			locationClient.stop(); //
//		}
//		if (null != map)
//			map.setMyLocationEnabled(false); //
//		if (null != mapView)
//			mapView.onDestroy();
//		mapView = null;
//		super.onStop();
//	}
//
//	private void startLocation() {
//		locationClient = new LocationClient(MyApp.getApp());
//		locationClient.registerLocationListener(new MyBDLocationListener());// 定位监听
//		LocationClientOption option = new LocationClientOption();
//		// option.setCoorType("GCJ02");//2.
//		// GCJ02：是由中国国家测绘局制订的地理信息系统的坐标系统，是由WGS84坐标系经加密后的坐标系；
//		option.setCoorType("bd09ll"); // BD09：百度坐标系，在GCJ02坐标系基础上再次加密。其中BD09ll表示百度经纬度坐标，
//		option.setNeedDeviceDirect(true); //
//		option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//		option.setScanSpan(10000);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
//		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
//		option.setLocationNotify(true);// 可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
//		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//		option.setIsNeedLocationPoiList(false);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//		option.setIgnoreKillProcess(false);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
//		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
//		option.setIsNeedAltitude(false);// 不需要高度
//
//		locationClient.setLocOption(option);
//
//		locationClient.start();
//
//	}
//
//	/**
//	 * @author Jemen Chen
//	 *
//	 */
//	private class MyBDLocationListener extends BDAbstractLocationListener {
//		private float zoomLevel = 0;
//
//		@Override
//		public void onReceiveLocation(BDLocation bdLocation) { // 使用的百度坐标系
//			if (bdLocation == null || mapView == null)
//				return;
//			map.clear();
//			double latitude = bdLocation.getLatitude();
//			double longitude = bdLocation.getLongitude();
//			if (Math.abs(longitude) > 180 || Math.abs(latitude) < 0.001 || Math.abs(latitude) > 90) { //
//				longitude = 114.44299;
//				latitude = 30.519942;
//				return;
//			}
//			String describe = bdLocation.getLocationDescribe();
//			String cityCode = bdLocation.getCityCode();
//			String city = bdLocation.getCity();
//			// LogUtil.d("city="+city);
//
//			if (null != cityCode && !"".equals(cityCode) && !oldCode.equals(cityCode))
//				MyApp.getApp().getPref().edit().putString(Consts.KEY_CITYCODE, cityCode).commit();
//
//			tvLocation.setText("经度：" + longitude + "\n纬度：" + latitude + "\n描述：");
//			if (null != city)
//				tvLocation.append(city);
//			if (null != describe)
//				tvLocation.append(describe);
//			// 将GPS设备采集的原始GPS坐标转换成百度坐标
//			CoordinateConverter converter = new CoordinateConverter();
//			converter.from(CoordType.GPS);
//			LatLng currentLocation = new LatLng(latitude, longitude);
//			if (zoomLevel == 0)
//				zoomLevel = 13;
//			else
//				zoomLevel = map.getMapStatus().zoom;
//			MapStatusUpdate mapeStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(currentLocation, zoomLevel);
//			map.animateMapStatus(mapeStatusUpdate);
//
//			MarkerOptions markOptions = new MarkerOptions();
//			markOptions.position(currentLocation); //
//			BitmapDescriptor bitmapDescripter = BitmapDescriptorFactory.fromResource(R.drawable.location);
//			markOptions.icon(bitmapDescripter); //
//			map.addOverlay(markOptions); //
//			location.setLatitude(latitude);
//			location.setLongitude(longitude);
//			if (null != city && null != describe && !"".equals(describe))
//				location.setDescribe(city + describe);
//		}
//
//	}
//}
