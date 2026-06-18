package top.jemen.utils;

import android.graphics.Color;

public class ColorUtil {

	public static int lambdaToColor(double lambda) {
		return lambdaToColor(lambda, 0.8, 255);
	}
	
	
	/**
	 * 将波长转换为颜色值
	 * @param lambda	波长
	 * @param gamma		伽马射线
	 * @param intensityMax	照明强度
	 * @return
	 */
	public static int lambdaToColor(double lambda, double gamma,double intensityMax) {
	    double r, g, b, alpha; // double
	    if (lambda >= 380.0 && lambda < 440.0) {
	        r = -1.0 * (lambda - 440.0) / (440.0 - 380.0);
	        g = 0.0;
	        b = 1.0;
	    }else if (lambda >= 440.0 && lambda < 490.0) {
	        r = 0.0;
	        g = (lambda - 440.0) / (490.0 - 440.0);
	        b = 1.0;
	    }else if (lambda >= 490.0 && lambda < 510.0) {
	        r = 0.0;
	        g = 1.0;
	        b = -1.0 * (lambda - 510.0) / (510.0 - 490.0);
	    }else if (lambda >= 510.0 && lambda < 580.0) {
	        r = (lambda - 510.0) / (580.0 - 510.0);
	        g = 1.0;
	        b = 0.0;
	    }else if (lambda >= 580.0 && lambda < 645.0) {
	        r = 1.0;
	        g = -1.0 * (lambda - 645.0) / (645.0 - 580.0);
	        b = 0.0;
	    }else if (lambda >= 645.0 && lambda <= 780.0) {
	        r = 1.0;
	        g = 0.0;
	        b = 0.0;
	    }else {
	        r = 0.0;
	        g = 0.0;
	        b = 0.0;
	    }
		// 在可见光谱的边缘处强度较低。
	    if (lambda >= 380.0 && lambda < 420.0) {
	        alpha = 0.30 + 0.70 * (lambda - 380.0) / (420.0 - 380.0);
	    }else if (lambda >= 420.0 && lambda < 701.0) {
	        alpha = 1.0;
	    }else if (lambda >= 701.0 && lambda < 780.0) {
	        alpha = 0.30 + 0.70 * (780.0 - lambda) / (780.0 - 700.0);
	    }else {
	        alpha = 0.0;
	    }
	    
	    
		// 1953年在引入NTSC电视时,计算具有荧光体的监视器的亮度公式如下
	    double Y = (0.212671*r + 0.715160*g + 0.072169*b); // Math.round
		// 伽马射线 gamma
	    // 照明强度 intensityMax
	    int R =  (int) Math.round(intensityMax * Math.pow(r * alpha, gamma));
	    int G = (int) (g == 0.0 ? 0 : Math.round(intensityMax * Math.pow(g * alpha, gamma)));
	    int B = (int) (b == 0.0 ? 0 : Math.round(intensityMax * Math.pow(b * alpha, gamma)));
	    int A = (int) (alpha*intensityMax); // Math.round
		// return
	    return Color.argb(A, R, G, B);
	}
	
	
	/**
	 * 根据颜色值求610波长的强度，最大值为392.31
	 * @param color
	 * @return
	 */
//	public static int Value610(int color) {
////		  r = 1.0;
////	        g = -1.0 * (lambda - 645.0) / (645.0 - 580.0);	//580-645nm
////	        b = 0.0;
//		int v=(color>>16)&0xff;
//		v+=((color>>8)&0xff)*(645.0-610)/(645-580);
//		return v;
//	}
	
	public static float Value610(int color) {
		int r=color>>16&0xff;
	    int g=color>>8&0xff;
	    int b=color&0xff;
	    
		int v=(color>>16)&0xff;	//红色
		v+=g*(645.0-610)/(645-580);//绿色的一定比例0.53846
		float  Brightness = 0.00001f+0.299f * r + 0.587f * g + 0.114f *b;
		return v/Brightness*100;
	}
	public static float absorb530(int color) {
		int r=color>>16&0xff;
		int g=color>>8&0xff;
		int b=color&0xff;
		
		float v=g;	//绿色
		v+=r*0.2857;//(530-510)/(580-510);
		float  Brightness = 0.00001f+0.299f * r + 0.587f * g + 0.114f *b;
		v=328.8535f-v;
		return v/Brightness*100;
		
	}
	
	public static float ValueWine(int  color) {
		int r=color>>16&0xff;
	    int g=color>>8&0xff;
	    int b=color&0xff;
	    
		int v=(int) (r+0.8f*b);
//		int v=r;
		float  Brightness = 0.299f * r + 0.587f * g + 0.114f *b+0.00001f;//防止出现分母为0的情况.主要用绿色做对比
//		float  Brightness = r +g + b;//尚可。
//		float  Brightness =Math.max(r, Math.max(g, b)); //跌宕起伏，效果很差,不可取。
//		float  Brightness =1;//这受亮度影响就太大了，没法用。
//		float  Brightness =g;//
		return v/Brightness*100;
	}
	
	
	public static float ValueAusoub(int color) {//用吸光的方法效果也不理想。
		int r=color>>16&0xff;
	    int g=color>>8&0xff;
	    int b=color&0xff;
		return 255*3-r-g-b;

	}
	public static float HSB_B(int color) {	//单纯使用饱和度区分情况较差，
		int r=color>>16&0xff;
		int g=color>>8&0xff;
		int b=color&0xff;
		float max=Math.max(r, Math.max(g, b));
		if(max==0) {
			return 0;
		}
		float min=Math.min(r, Math.min(g, b));
		return 1-min/max;
		
	}
	
}
