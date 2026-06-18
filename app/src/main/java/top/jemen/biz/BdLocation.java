package top.jemen.biz;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.whswzz.prfluroanalyzer.app.Build;
import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.consts.Consts;

import android.app.Application;
import android.content.Context;
import android.media.audiofx.LoudnessEnhancer;
import top.jemen.utils.LogUtil;

public class BdLocation {
	public static String ADDR=null;
	public static double LAT=0;
	public static double LON=0;
	public static String COUNTRY;    //获取国家
	public static String PROVINCE;
	public static String CITY;

	
	
	//原有BDLocationListener接口暂时同步保留。具体介绍请参考后文中的说明
	private static LocationClient mLocationClient ;
	public void location(Context context) {
		//BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口
		    //声明LocationClient类
		mLocationClient = new LocationClient(context.getApplicationContext());     
		
		mLocationClient.registerLocationListener(myListener);
		    //注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setIsNeedAddress(true);
		//可选，是否需要地址信息，默认为不需要，即参数为false
		//如果开发者需要获得当前点的地址信息，此处必须为true
		option.setIgnoreKillProcess(false);
		option.setCoorType("BD09ll");
		mLocationClient.setLocOption(option);
		//mLocationClient为第二步初始化过的LocationClient对象
		//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
		//更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
		mLocationClient.start();
	}
	
	private static void stop() {
		if(null!=mLocationClient) {
			if(null!=myListener) {
				mLocationClient.unRegisterLocationListener(myListener);
			}
			if(mLocationClient.isStarted()) {
				mLocationClient.stop();
				mLocationClient=null;
			}
		}
	}
	
	
	 private static BDAbstractLocationListener myListener=new  BDAbstractLocationListener(){
	    @Override
	    public void onReceiveLocation(BDLocation location){
	        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
	        //以下只列举部分获取地址相关的结果信息
	        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
				
	        String addr = location.getAddrStr();    //获取详细地址信息
	        COUNTRY = location.getCountry();    //获取国家
			PROVINCE = location.getProvince();    //获取省份
			CITY = location.getCity();    //获取城市
//	        String country = location.getCountry();    //获取国家
//	        String province = location.getProvince();    //获取省份
//	        String city = location.getCity();    //获取城市
//	        String district = location.getDistrict();    //获取区县
//	        String street = location.getStreet();    //获取街道信息
//	        LogUtil.e("各项数据："+country+province+city+district+street);
	        LogUtil.e("addr="+addr+",lat="+location.getLatitude()+",lon="+location.getLongitude());
	        double lat=location.getLatitude();
	        double lon=location.getLongitude();
	        int x=0;
	        if(addr!=null) {
	        	BdLocation.ADDR=addr;
	        	MyApp.getApp().getPref().edit().putString(Consts.KEY_DESCRIBE,ADDR).apply();
				MyApp.getApp().getLocation().setDescribe(addr);
	        	x++;
	        }
	        if(Math.abs(lat)>0.01) {
				MyApp.getApp().getLocation().setLatitude(lat);
				MyApp.getApp().getPref().edit().putFloat(Consts.KEY_LATITUDE, (float) lat).apply();
	        	x++;
	        }
	        if(Math.abs(lon)>0.1) {
	        	BdLocation.LON=lon;
				MyApp.getApp().getLocation().setLongitude(lon);
				MyApp.getApp().getPref().edit().putFloat(Consts.KEY_LONGITUDE, (float) lon).apply();;
	        	x++;
	        }
	        if(x>=3) {
	        	mLocationClient.unRegisterLocationListener(this);
	        	mLocationClient.stop();
	        }


	        
	        //addr=中国湖北省武汉市洪山区白沙四路,lat=30.48518,lon=114.27669
	        if(x>=2) {
	        	MyApp.getApp().getPref().edit().putLong(Consts.LOC_TIME, System.currentTimeMillis()).apply();;
	        	if(lat<30.48||lat>30.49||lon<114.27||lon>114.283) {
	        		MyApp.getApp().getPref().edit().putBoolean(Consts.DEBUG,false).apply();
	        		MyApp.getApp().getPref().edit().putBoolean(Consts.RELEASED,true).apply();
	        		Build.DEBUG=false;
	        	}
	        }
	    }
	    
	    public void onLocDiagnosticMessage(int arg0, int arg1, String arg2) {};
	};
}
