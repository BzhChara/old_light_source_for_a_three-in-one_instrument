package com.whswzz.prfluroanalyzer.app;



import com.whswzz.prfluroanalyzer.param.Params;

import android.content.SharedPreferences;

public class Target {

	public static final int N = 6;
	public static final int NPB = 999; //均使用同一个串口

	public static double RSD=0.03;
	public static int MIN_STS=1;
	public static int MAX_STS=2;//scan times;
	public static String WIFI_MACS="";
	private static final String FORMAT = "%.3f";// 浓度值的保存小数位
	public static final String LARGE=">0.500ppm";
	public static final String UNIT = "ppm";
	public static final String C = "C";
	public static final String H = "H";
	public static  int MOTOR=1;
	static {
		SharedPreferences pref = MyApp.getApp().getPref();
	}
	
	
	public static String formatResult(double ppb,String proj,String type) {
		if(ppb<0) ppb=0;
		ppb/=(1-Params.WATER_CONTENT);
		return formatResultPPM(ppb, proj, type);
//		return formatResultPPB(ppb, proj, type);
	}
	
	public static String formatResultPPM(double ppb,String proj,String type) {
		String result="";
//		double ppm=ppb/1000;
//		if(ppm<0.002) {
//			result=String.format("%.4f", ppm) + UNIT;
//		}else {
//			result=String.format(FORMAT, ppm) + UNIT;
//		}
//		
//		if(null==proj||null==type||(type.contains("水")||type.contains("water"))) {
//			return result;
//		}
//		
//		Resources r = MyApp.getApp().getResources();
//		
//		List<Project> projs = MyApp.getApp().getProjs();
//		
//		float fLimit=0.2f;
//		try {
//			Project p=ListUtil.getProject(projs, proj);
//			if(null!=p) {
//				String limit=p.getSpecimens().get(type);//null 
//				String[] ss=limit.split("-");
//				if(proj.equals( r.getString(R.string.Iodine))&&type.contains( r.getString(R.string.salt))) {
//					return formatI(ppm,result,ss);
//				}
//				fLimit=Float.parseFloat(ss[1]);
//				if(fLimit<0.01||fLimit>10) {
//					fLimit=0.2f;
//				}
//			}else {
//				LogUtil.e("Target getProject null,proj="+proj);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if(ppm<=fLimit*0.9) {
//			result+=MyApp.getApp().getString(R.string._qualified_);
//		}else if(ppm<=fLimit*1.1) {
//			result+=MyApp.getApp().getString(R.string._suspect_);
//		}else if(ppm<10){
//			result+=MyApp.getApp().getString(R.string._oos_);
//		}else {
//			result+=MyApp.getApp().getString(R.string._abnormal_);
//		}
		return result;
	}
	
	
	
	
	
	
	
	
	
	
	
	public static String formatResultPPB(double ppb,String proj,String type) {
		String result=String.format("%.2f", ppb) + "ppb";
//		if(null!=type&&(type.contains("水")||type.contains("water"))) {
//			return result;
//		}
//		double ppm=ppb/1000;
//		if(null==proj||null==type||(type.contains("水")||type.contains("water"))) {
//			return result;
//		}
//		Resources r = MyApp.getApp().getResources();
//		List<Project> projs = MyApp.getApp().getProjs();
//		float fLimit=0.2f;
//		try {
//			Project p=ListUtil.getProject(projs, proj);
//			String limit=p.getSpecimens().get(type);
//			String[] ss=limit.split("-");
//			fLimit=Float.parseFloat(ss[1]);
//			if(fLimit<0.01||fLimit>1)fLimit=0.2f;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		if(ppm<=fLimit*0.9) {
//			result+=MyApp.getApp().getString(R.string._qualified_);
//		}else if(ppm<=fLimit*1.1) {
//			result+=MyApp.getApp().getString(R.string._suspect_);
//		}else if(ppm<3){
//			result+=MyApp.getApp().getString(R.string._oos_);
//		}else {
//			result+=MyApp.getApp().getString(R.string._abnormal_);
//		}
		return result;
	}
	
}
