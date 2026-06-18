package com.whswzz.prfluroanalyzer.param;


import com.whswzz.prfluroanalyzer.app.Build;
import com.whswzz.prfluroanalyzer.app.Initer;
import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.app.Target;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.zkzk.pra.R;
import com.zkzk.pra.utils.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import top.jemen.interfaces.ICallback;
import top.jemen.utils.ExceptionHandler;

public class Params {

	public volatile static boolean DEBUG = Build.DEBUG;
	public volatile static float K_Peak = 1;
	public volatile static float B_Peak = 0;
	public volatile static float K_Peak2 = 1;
	public volatile static float B_Peak2 = 0;
	public volatile static int FREQUENCY = 15; // the fefault value is 15 now. delete latter if all parameter finalized.
	public volatile static int ENRICH_TIME = 200; // most time,it's 200
	public volatile static short WASH_E = -400; // 2019-4,defrient specimen has defrien wash electric potential and wash
												// time
	public volatile static short WASH_T = 20;
	public volatile static int pos = 100 / 4;// the position of the value that start to use;I'll package it in SWV
												// latter.

	public volatile static boolean TOOGLE_DEDUCT = false;
	public volatile static boolean JUMP;
	public volatile static String KIT_CODE = "NA";

	public volatile static float WATER_CONTENT = 0F;// As the scheme changes wlways,forgive me to use so many static
													// params
	public static volatile int T_TURN = 20;// 20s the interval time to change the direction of rotation
	public static boolean SMOOTH=true;
//	public  static float[] K_Peaks = new float[Target.N];
//	public  static float[] B_Peaks =new float[Target.N];
	public static int s=0;
	public static float TEMP_ENZYME=-1;
	public static boolean TEMP_UPDATE=true;

	private static final String FORMAT = "%.3f";// 浓度值的保存小数位
	public static final String NAME = "PRAIO2"; //辨别升级用
	public static final String GBT = "GB/T5009.199-2003"; //酶抑制法的国标
	public static final String GB = "GB2763-2021"; //胶体金法的国标
//	public static final String GB_SC = "GB5009-2003";	//国标兽残
	public static final String GB_SC2 = "GB 31650";	//国标兽残
	public static final String GB_SC="GB 31650-2019"; //胶体金测兽残国标
	public static  int LIGHT = 50;

	public static String SAK_KEY = "66666";


	public static final String[] lights = new String[]{"410nm", "460nm", "520nm", "550nm", "590nm", "630nm"};
	static SharedPreferences pref= MyApp.getApp().getSharedPreferences("params", Context.MODE_PRIVATE);
	
	
	
	/**
	 * 分别是六联卡、三联卡、单卡边界参数,分别是l,t,dx,b,lw
	 */
	private static float[][] borders;
	public static String UPLOAD_URL;
	private static float k=1,b=0; //用于胶体金的TC矫正
	
	

	static {
		SharedPreferences pref = MyApp.getApp().getPref();
		DEBUG = pref.getBoolean(Consts.DEBUG, DEBUG);
		TOOGLE_DEDUCT = pref.getBoolean(Consts.KEY_TOOGLE_DEDUCT, TOOGLE_DEDUCT);

		K_Peak = pref.getFloat(Consts.K_Peek, 1);
		B_Peak = pref.getFloat(Consts.B_Peek, 0);
		K_Peak2 = pref.getFloat(Consts.K_Peek2, 1);
		B_Peak2 = pref.getFloat(Consts.B_Peek2, 0);

		KIT_CODE = pref.getString(Consts.KIT_CODE, "NA");
//		for(int i=0;i<Target.N;i++) {
//			K_Peaks[i]= pref.getFloat(Consts.K_Peek+i, 1);
//			B_Peaks[i] = pref.getFloat(Consts.B_Peek+i, 0);
//		}
		borders=Initer.initBorders();
		

		LIGHT=pref.getInt(Consts.KEY_LIGHT, LIGHT);
		String version = Tools.getCurrentVersion(null).toLowerCase();
		if(version!=null) {
			if (version.contains("hb")) {
//				UPLOAD_URL="https://jg.hebny.cn:9089/checkRecord/api/uploads/checkRecord";
				UPLOAD_URL="https://jghebny.cn:9089/checkRecord/apiuploads/checkRecord";
			}
			else if(version.contains("ah")) {
				UPLOAD_URL = "https://service.ahjc.aielab.net/ah-check-station-record/add";
			}else if (version.contains("js")) {
				UPLOAD_URL = "http://qt.debaninspect.com/qt/result/upload?appid=PRAIO&appsecret=UVubNbngfyx3RY3U6x4k&sn=123456789&resultjson=";
			}

		}


		UPLOAD_URL=pref.getString(Consts.KEY_UPLOAD_URL,UPLOAD_URL);
	}

	
	public static float[][] getBorders(){
		return borders;
	}
	public static void saveBorders(float[][] newBorders,ICallback callback) {
		borders=newBorders;
		Initer.saveBorders(newBorders,callback);
	}
	

	/**
	 * 试验用
	 * @deprecated
	 * @param proj
	 * @param ip
	 * @param type
	 * @param snr
	 * @param labX
	 * @return
	 */
	public static String Calculate(String proj, double ip, String type, double snr, float labX) {
		String result = "";
		try {
			Resources r = MyApp.getApp().getResources();
			SharedPreferences pref = MyApp.getApp().getPref();
			double cValue = 0;
			float k = pref.getFloat(Consts.K + proj + type, -99);//
			float b = pref.getFloat(Consts.B + proj + type, -9999);
			if (k > 0 && k <= 2000 && b > -100 && b < 100) { // 自定义参数
				cValue = k * ip * (1e+6) + b*labX;
				if (ip < 0.003e-6 || cValue <= 0) // 电流高度小于平均噪声是否判定为未检测再与实验室商讨下。0.39
				{
					result = r.getString(R.string.undetected); // 最小检出限0.39(保留到0.4）
				} else {
					result = Target.formatResult(cValue, proj, type); // Cd
				}
			}
		} catch (Exception e) {
		}
		return "lab"+result;
	}

	/**
	 * 计算单一被检测物质。
	 * 
	 * @param proj
	 * @param ip 标准的国际单位制	,注意，
	 * @param type
	 * @param snr
	 *            信噪比
	 * @return
	 */
	public static String Calculate(String proj, double ip, String type, double snr) {
		String result = "";
		try {
			Resources r = MyApp.getApp().getResources();
			SharedPreferences pref = MyApp.getApp().getPref();
			double cValue = 0;
			float k = pref.getFloat(Consts.K + proj + type, -99);//
			float b = pref.getFloat(Consts.B + proj + type, -9999);
			if (k > 0 && k <= 100000 && b > -10000 && b < 10000) { // 自定义参数
				cValue = k * ip * (1e+6) + b;
				if (ip < 0.003e-6 || cValue <= 0) // 电流高度小于平均噪声是否判定为未检测再与实验室商讨下。0.39
				{
					result = r.getString(R.string.undetected); // 最小检出限0.39(保留到0.4）
				} else if(ip<0.05e-6){
					cValue = k * 0.05 + b;//0.05e-6*1e+6省略掉。
					cValue*=ip/0.05e-6;
					result = Target.formatResult(cValue, proj, type);
				}else {
					
					result = Target.formatResult(cValue, proj, type); // Cd
				}
				return result;
			}

		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}

		return result;
	}

	
    public static void setURL(String url) {
		UPLOAD_URL=url;
		MyApp.getApp().getPref().edit().putString(Consts.KEY_UPLOAD_URL,url).apply();
    }

	/**
	 * 胶体金灯光亮度
	 */
	public static void savaLight() {
		MyApp.getApp().getPref().edit().putInt(Consts.KEY_LIGHT,LIGHT).apply();
		
	}



	/**
	 * 原本用于
	 * @param channel
	 * @param light
	 */
	public static void savePhotometer(int channel, int light , float k, float b) {
		if(channel==24){
			for(int i=0;i<channel;i++){
				if(light==6){
					for(int j=0;j<light;j++){
						pref.edit().putFloat(Consts.KEY_SPECTRUM_K+i+'-'+j,k).apply();
						pref.edit().putFloat(Consts.KEY_SPECTRUM_B+i+'-'+j,b).apply();
					}
				}else{
					pref.edit().putFloat(Consts.KEY_SPECTRUM_K+i+'-'+light,k).apply();
				}
			}
		}else{
			if(light==6){
				for(int j=0;j<light;j++){
					pref.edit().putFloat(Consts.KEY_SPECTRUM_K+channel+'-'+j,k).apply();
					pref.edit().putFloat(Consts.KEY_SPECTRUM_B+channel+'-'+j,b).apply();
				}
			}else{
				pref.edit().putFloat(Consts.KEY_SPECTRUM_K+channel+'-'+light,k).apply();
			}
		}
	}
	public static float[] getPhotometer(int channel, int light) {
		float[] spectrum = new float[2];

		spectrum[0] = pref.getFloat(Consts.KEY_SPECTRUM_K + channel + '-' + light, 1);
		spectrum[1] =pref.getFloat(Consts.KEY_SPECTRUM_B + channel + '-' + light, 0);
		return spectrum;
	}


	public static boolean showAbnormal() {
		return pref.getBoolean(Consts.KEY_SHOW_ABNORMAL, true);
	}
	public static void setShowAbnormal(boolean show) {
		pref.edit().putBoolean(Consts.KEY_SHOW_ABNORMAL, show).apply();
	}

	public static void setBaseAbsorbance(String proj,float value){
		pref.edit().putFloat(Consts.KEY_BASE_ABSORBANCE+proj,value).apply();;
	}
	public static float getBaseAbsorbance(String proj){
		return pref.getFloat(Consts.KEY_BASE_ABSORBANCE+proj,0);
	}

	public static void setK(float k1) {
		k=k1;
		MyApp.getApp().getPref().edit().putFloat(Consts.K,k).apply();
	}
	public static void setB(float b1){
		b=b1;
		MyApp.getApp().getPref().edit().putFloat(Consts.B,b).apply();
	}
	public static float getK(){
		return k;
	}
	public static float getB(){
		return b;
	}


}
