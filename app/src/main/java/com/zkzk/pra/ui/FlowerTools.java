package com.zkzk.pra.ui;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class FlowerTools {

	public static float ra=17.0f;//大圆半径
	
	public static float rb=13.7f;//小圆半径
	
	public static float k=0.05f;//比例系数
	
	static float xa;//大圆坐标
 
	float ya;//大圆坐标
    
	static float xb;//小圆坐标
 
	static float yb;//小圆坐标
	
	static float rc=rb*k;//绕点半径
	
	static float radian_out;//弧度
	
	static float w=0.05f;//角速度
	
	static int t=0;//时间
	
	static float radian;//自转弧度
	
	static float[] c;//轨迹圆坐标
	private static final int TT=6000;
	static float[] points=new float[TT*2];
	/**		绘制繁花曲线,已经改良，尽可能的降低系统开销。
	 * @param canvas	
	 * @param paint	
	 * @param cx	中心点X坐标
	 * @param cy	中心点y坐标
	 * @param ra	大圆半径
	 */
	public static  void draw(Canvas canvas,Paint paint,float cx,float cy,float R) {
		if(k<0.9) {
			if(k<0.5)
				k+=0.03;
			else 
				k+=0.1;
		}else {
			k=0.05f;
		}
		float pp=R/ra;
		ra*=pp;
		rb*=pp;
		rc=rb*k;//绕点半径
		float r=ra-rb;
		t=0;
		while(t<TT) {
			radian_out=w*t;
			xb=(float) (r*(Math.cos(radian_out)));//减少方法调用，直接计算
			yb=(float) (r*(Math.sin(radian_out)));
			radian=-(ra/rb)*radian_out;
//			c=  get_center_in_circle( ra, rb, radian_out, xb, yb, rc);
			points[t*2]=(float) (cx+ (xb+rc*Math.cos(radian)));	//移到中心点
			points[t*2+1]=(float) (cy+yb+rc*(Math.sin(radian)));
			//更新参数
			t++;
		}
//		paint.setStrokeWidth(2);
		canvas.drawLines(points, paint);
		
		
	}
	
	/**
	 * 小圆圆心轨迹方程
	 * @param r
	 * @param radian_out
	 * @param i
	 * @return
	 */
	private static float get_center_in_circle(float r,float radian_out,int i) {
		
		float xb,yb;
		if(i==1) {
			xb=(float) (r*(Math.cos(radian_out)));
			return xb;
		}else {
			yb=(float) (r*(Math.sin(radian_out)));
			return  yb;
		}
	}
	
	/**
	 * 动点轨迹方程
	 * @param ra
	 * @param rb
	 * @param radian_out
	 * @param xb
	 * @param yb
	 * @param rc
	 * @return
	 */
	private static float[] get_center_in_circle(float ra,float rb,float radian_out,float xb,float yb,float rc) {
		
		float radian;
		radian=-(ra/rb)*radian_out;
		float[] cs =new float[2];
		cs[0]=(float) (xb+rc*(Math.cos(radian)));
		cs[1]=(float) (yb+rc*(Math.sin(radian)));
		return cs;
		
		
	}
 
}
