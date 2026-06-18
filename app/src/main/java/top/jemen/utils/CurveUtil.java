package top.jemen.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.app.Target;
import com.whswzz.prfluroanalyzer.fluoro.dal.imp.XDao;
import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;
import com.whswzz.prfluroanalyzer.fluoro.entity.Hump;
import com.whswzz.prfluroanalyzer.fluoro.entity.Specimen;
import com.whswzz.prfluroanalyzer.fluoro.uvc.History;
import com.whswzz.prfluroanalyzer.param.Params;
import com.zkzk.pra.R;
import com.zkzk.pra.utils.ListUtil;
import com.zkzk.pra.utils.ToastUtil;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.os.BatteryStats;
import android.text.TextUtils;
import android.widget.ImageView;
import top.jemen.camera.UVC;
import top.jemen.utils.ColorUtil;
import top.jemen.utils.Curve;
import top.jemen.utils.LogUtil;
import top.jemen.utils.threadpool.AsyncProcessor;

public class CurveUtil {
	public static int MIN_DISTANCE = 10;

	// public static void showPeak(List<Float> values,ImageView iv, Canvas canvas,
	// Paint paint, final Data data) {
	// LogUtil.d("showPeak " + Thread.currentThread().getName());
	// int size = values.size();
	// LogUtil.d("size=" + size);
	// int W =canvas.getWidth(), H = canvas.getHeight();
	// float left=W*0.002f,top=H*0.025f,right=W-left*2,bottom=H-top*2;
	// Float min = values.get(0);
	// Float max = min;
	// for (Float x : values) {
	// if (x > max) {
	// max = x;
	// } else if (x < min) {
	// min = x;
	// }
	// }
	// float ppp = (right-left) / values.size();// pixels per point
	//
	// float distance = (max - min) >50 ? (max - min) : 50;
	// if(Build.DEBUG) {
	//// canvas.drawColor(0xaa888888);
	//// canvas.drawColor(0xaa000000);
	//// canvas.drawColor(0xffFFFFFF);
	// }else {
	// canvas.drawColor(0xffFFFFFF);
	// }
	// paint.setStyle(Style.STROKE);
	//
	// paint.setColor(Color.BLACK);
	// canvas.drawRect(left, top, right, bottom, paint);
	// paint.setColor(Color.RED);
	// Path path = new Path();
	// float y = (bottom - 50 - (bottom - top - 100) * (values.get(0) - min) /
	// distance);
	// path.moveTo(0, y);
	// for (int i = 1; i < values.size(); i++) {
	// float x = left+i * ppp ;
	// y = (bottom - 50 - (bottom - top - 100) * (values.get(i) - min) / distance);
	//// LogUtil.d("y" + i + "=" + y + ", v=" + values.get(i));
	// path.lineTo(x, y);
	// }
	// canvas.drawPath(path, paint);
	// paint.setStyle(Style.FILL_AND_STROKE);
	// try {
	// final int b = Curve.getMaxIndex(values, size*0.1, size * 0.5);
	// final int a = Curve.getMinIndex(values, b - size * 0.1, b);
	// final int c = Curve.getMinIndex(values, b, b + size * 0.1);
	// final float ya = (int) (bottom - 50 - (bottom - top - 100) * (values.get(a) -
	// min) / distance);
	// final float yb = (int) (bottom - 50 - (bottom - top - 100) * (values.get(b) -
	// min) / distance);
	// final float yc = (int) (bottom - 50 - (bottom - top - 100) * (values.get(c) -
	// min) / distance);
	//
	// LogUtil.d("b=" + b + " \t value=" + values.get(b));
	//
	// // 下面处理控制线
	// int r=(int) (c+size*0.6);//控制下又边界的范围；
	// if(r>size - size / 20) r=size - size / 20;
	// final int cb = Curve.getMaxIndex(values, b+size*0.25, r);
	// final int ca = Curve.getMinIndex(values, cb - size*0.1, cb);
	// final int cc = Curve.getMinIndex(values, cb, cb + size * 0.1);
	//
	// if (ca < 0 || cb < 0 || cc < 0) {
	// LogUtil.d("数据异常");
	// return;
	// }
	//
	// final float yca = (int) (bottom - 50 - (bottom - top - 100) * (values.get(ca)
	// - min) / distance);
	// final float ycb = (int) (bottom - 50 - (bottom - top - 100) * (values.get(cb)
	// - min) / distance);
	// final float ycc = (int) (bottom - 50 - (bottom - top - 100) * (values.get(cc)
	// - min) / distance);
	//
	//
	// float k=(values.get(c) - values.get(a)) / (c - a);
	// float area = 0;
	// paint.setStrokeWidth(3);
	// for(int i=a;i<c;i++) { //画峰面积
	// float vbi = values.get(a) + (i - a)*k;// B点基线
	// area+= values.get(i) - vbi;
	// final float yi = (int) (bottom - 50 - (bottom - top - 100) * (values.get(i) -
	// min) / distance);
	// final float ybi = (int) (bottom - 50 - (bottom - top - 100) * (vbi - min) /
	// distance);
	// canvas.drawLine(i * ppp, yi, i * ppp, ybi, paint);
	// }
	//
	//// float areaC = 0;
	//// for(int i=ca;i<cc;i++) {//画峰面积
	//// float vi = values.get(a) + (i - a)*k;// B点基线
	//// areaC+= values.get(i) - vi;
	//// final float yi = (int) (bottom - 50 - (bottom - top - 100) * (values.get(i)
	// - min) / distance);
	//// final float ybi = (int) (bottom - 50 - (bottom - top - 100) * (vi - min) /
	// distance);
	//// canvas.drawLine(i * ppp, yi, i * ppp, ybi, paint);
	//// }
	// //下面不画CC到A的基线了，采用CC到CA
	// k=(values.get(cc) - values.get(ca)) / (cc - ca);
	// float areaC = 0;
	// for(int i=ca;i<cc;i++) {//画峰面积
	// float vi = values.get(ca) + (i - ca)*k;// B点基线
	// areaC+= values.get(i) - vi;
	// final float yi = (int) (bottom - 50 - (bottom - top - 100) * (values.get(i) -
	// min) / distance);
	// final float ybi = (int) (bottom - 50 - (bottom - top - 100) * (vi - min) /
	// distance);
	// canvas.drawLine(i * ppp, yi, i * ppp, ybi, paint);
	// }
	// paint.setStrokeWidth(1);
	// float vbb = values.get(a) + (b - a)*k;// B点基线
	// final float ybb = (int) (bottom - 50 - (bottom - top - 100) * (vbb - min) /
	// distance);
	// final float peak = values.get(b) - vbb;
	//
	// // canvas.drawLine(ca, yca, cc, ycc, paint);
	// float vcbb = values.get(a) +(cb - a)*k;// B点基线
	// final float ycbb = (int) (bottom - 50 - (bottom - top - 100) * (vcbb - min) /
	// distance);
	// final float peakc = values.get(cb) - vcbb;
	// SimpleDateFormat sdf = new SimpleDateFormat(Consts.YMDHMS_FORMAT);
	//
	//// canvas.drawLine(a * ppp, ya, cc * ppp, ycc, paint);
	//// canvas.drawLine(b * ppp, yb, b * ppp, ybb, paint);
	//// canvas.drawText("" + peak, b * ppp, yb - 20, paint);
	// canvas.drawText("" + area, b * ppp, yb - 20, paint);
	//
	//// canvas.drawLine(cb * ppp, ycb, cb * ppp, ycbb, paint);
	//// canvas.drawText("" + peakc, cb * ppp, ycb - 20, paint);
	// canvas.drawText("" + areaC, cb * ppp, ycb - 20, paint);
	//
	// String result;
	// if (areaC < 300) { //拟低于392则设置为无效卡
	// result = MyApp.getApp().getString(R.string.invalid_card);
	// } else {
	// double ct = areaC/ area;
	// result=Params.Calculate(data.getProj(), ct, data.getSpecimen(), 0);
	// if(TextUtils.isEmpty(result)) {
	// result = "C/T : " + String.format("%.3f", ct);
	// }
	// }
	//
	// float textH = paint.descent() - paint.ascent();
	// float textW = paint.measureText(result) + 8;
	// canvas.drawText(result, right - textW, top + textH, paint);
	//
	// Hump hump = new Hump(a, b, c, ca, cb, cc);
	//
	// // Data data=new Data(sn, specimen, proj, limit, peak+"/"+peakc, customerOrg,
	// // workOrg, operator,
	// // values, peak, peakc,hump);
	// data.setHump(hump);
	// data.setT(peakc);
	// data.setC(peakc);
	// data.setResult(result);
	// AsyncProcessor.executeTask(new Runnable() {
	// @Override
	// public void run() {
	// XDao.insertToDataBase(data);
	// }
	// });
	//
	//// drawRainbow(canvas, paint);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }

	private static Paint paint = new Paint();

//	public static void showPeak(List<Float> oValues, ImageView iv, final FluData data, int channel) {
//		LogUtil.d("showPeak " + Thread.currentThread().getName());
//		int size = oValues.size();
//		LogUtil.d("size=" + size);
//		int W = 400, H = 300;
//		Bitmap bmp = Bitmap.createBitmap(W, H, Config.ARGB_8888);
//		Canvas canvas = new Canvas(bmp);
//		canvas.drawColor(Color.WHITE);
//		float left = W * 0.002f, top = H * 0.025f, right = W - left * 2, bottom = H - top * 2;
//		paint.setTextSize(H * 0.06F);
//
//		// List<Float> values=new ArrayList<Float>(size);
//		/** 下面進行基線校正。没什么意义。 */
//		// SimpleRegression regression=new SimpleRegression();
//		// for(int i=0;i<oValues.size();i++) {
//		// regression.addData(i,oValues.get(i));
//		// }
//		// RegressionResults results=regression.regress();
//		// double pb=results.getParameterEstimate(0);
//		// double pk=results.getParameterEstimate(1);
//		//// canvas.drawLine(startX, startY, stopX, stopY, paint);
//		// for(int i=0;i<size;i++) {
//		// values.add((float) (oValues.get(i)-(i)*pk+pb));
//		// }
//		//
//
//		/********** 对于较差的信号，使用下面的方法降噪 ********/
//		// double[] odata=new double[oValues.size()];
//		// for(int i=0;i<size;i++) {
//		// odata[i]=oValues.get(i);
//		// }
//		//// LogUtil.d(Arrays.toString(odata));
//		// double[] fv=Wavelet.waveletDenoise(odata);
//		// if(null!=fv&&fv.length>size/2) {
//		// for(double d:fv) {
//		// values.add((float) d);
//		// }
//		// size=values.size();
//		// }else {
//		// /****************不做处理就留下下面这一行。***/
//		// }
//		//
//		// values.addAll(oValues);
//
//		List<Float> values = oValues;
//
//		try {
//			// final int cb = Curve.getMaxIndex(values, size*0.85, size*0.6);
//			final int cb = Curve.getHumpIndex(values, size * 0.85, size * 0.6);
//			int ca = Curve.getMinIndex(values, cb - size * 0.09, cb);
//			int cc = Curve.getMinIndex(values, cb, cb + size * 0.08);
//			// int ca = Curve.getMinIndex(values, cb - size*0.15, cb);
//			// int cc = Curve.getMinIndex(values, cb, cb + size * 0.15);
//			ca = Curve.bathFoot(values, ca, cc, ca, (ca + cb) / 2);
//			cc = Curve.bathFoot(values, ca, cc, cc, (cb + cc) / 2);
//
//			// final int b = Curve.getMaxIndex(values, cb-size*0.30, cb-size*0.44);
//			// //距离0.444size
//			final int b = Curve.getHumpIndex(values, cb - size * 0.30, cb - size * 0.44); // 距离0.444size
//			int a = Curve.getMinIndex(values, b - size * 0.08, b);
//			int c = Curve.getMinIndex(values, b, b + size * 0.08);
//			// int a = Curve.getMinIndex(values, b - size * 0.12, b);
//			// int c = Curve.getMinIndex(values, b, b + size * 0.13);
//			a = Curve.bathFoot(values, a, c, a, (a + b) / 2);
//			c = Curve.bathFoot(values, a, c, c, (b + c) / 2);
//
//			if (!Params.DEBUG) {// 下面来进行扣基线的操作。
//				values = new ArrayList<Float>();
//				float kca = (oValues.get(c) - oValues.get(a)) / (c - a);
//				float kcca = (oValues.get(cc) - oValues.get(ca)) / (cc - ca);
//				for (int i = 0; i < oValues.size(); i++) {
//					if (i > a && i < c) {
//						values.add(oValues.get(i) - (oValues.get(a) + kca * (i - a)));
//					} else if (i > ca && i < cc) {
//						values.add(oValues.get(i) - (oValues.get(ca) + kcca * (i - ca)));
//					} else {
//						values.add(0f);
//					}
//				}
//
//			}
//
//			Float min = values.get(0);
//			Float max = min;
//			for (Float x : values) {
//				if (x > max) {
//					max = x;
//				} else if (x < min) {
//					min = x;
//				}
//			}
//			LogUtil.d("max=" + max + ",min=" + min);
//			float ppp = (right - left) / values.size();// pixels per point
//
//			paint.setStyle(Style.STROKE);
//
//			paint.setColor(Color.BLACK);
//			paint.setStrokeWidth(1);
//			canvas.drawRect(left, top, right, bottom, paint);
//			paint.setColor(Color.RED);
//
//			float distance = (max - min) > MIN_DISTANCE ? (max - min) : MIN_DISTANCE;
//
//			final float ya = (int) (bottom - 50 - (bottom - top - 100) * (values.get(a) - min) / distance);
//			final float yb = (int) (bottom - 50 - (bottom - top - 100) * (values.get(b) - min) / distance);
//			final float yc = (int) (bottom - 50 - (bottom - top - 100) * (values.get(c) - min) / distance);
//
//			LogUtil.d("cb=" + cb + ",cc=" + cc + ",b=" + b + " \t value=" + values.get(b));
//
//			// a=(int) (size*0.177f);
//			// c=(int) (size*0.292f);
//			// ca=(int) (size*0.575f);
//			// cc=(int) (size*0.700f);
//
//			if (ca < 0 || cb < 0 || cc < 0) {
//				LogUtil.d("数据异常");
//				return;
//			}
//
//			final float yca = (int) (bottom - 50 - (bottom - top - 100) * (values.get(ca) - min) / distance);
//			final float ycb = (int) (bottom - 50 - (bottom - top - 100) * (values.get(cb) - min) / distance);
//			final float ycc = (int) (bottom - 50 - (bottom - top - 100) * (values.get(cc) - min) / distance);
//
//			float k = (values.get(c) - values.get(a)) / (c - a);
//
//			/*********************** 画曲线 **********************************/
//			Path path = new Path();
//			float y = (bottom - 50 - (bottom - top - 100) * (values.get(0) - min) / distance);
//			path.moveTo(0, y);
//			for (int i = 1; i < values.size(); i++) {
//				float x = left + i * ppp;
//				y = (bottom - 50 - (bottom - top - 100) * (values.get(i) - min) / distance);
//				// LogUtil.d("y" + i + "=" + y + ", v=" + values.get(i));
//				path.lineTo(x, y);
//			}
//			canvas.drawPath(path, paint);
//			paint.setStyle(Style.FILL_AND_STROKE);
//
//			/*********************** 画曲线结束 **********************************/
//
//			float area = 0;
//			Path pathT = new Path();
//
//			for (int i = a; i <= c; i++) { // 画峰面积
//				float vbi = values.get(a) + (i - a) * k;// B点基线
//				area += values.get(i) - vbi;
//				final float yi = (int) (bottom - 50 - (bottom - top - 100) * (values.get(i) - min) / distance);
//				final float ybi = (int) (bottom - 50 - (bottom - top - 100) * (vbi - min) / distance);
//				if (i == a) {
//					pathT.moveTo(i * ppp, ybi);
//				} else {
//					pathT.lineTo(i * ppp, yi);
//				}
//				// canvas.drawLine(i * ppp, yi, i * ppp, ybi, paint);
//			}
//			paint.setStyle(Style.FILL);
//			canvas.drawPath(pathT, paint);
//
//			if (area < 0) {
//				area = 0;
//			}
//
//			// 下面不画CC到A的基线了，采用CC到CA
//			k = (values.get(cc) - values.get(ca)) / (cc - ca);
//			float areaC = 0;
//			pathT.reset();
//			for (int i = ca; i <= cc; i++) {// 画峰面积
//				float vi = values.get(ca) + (i - ca) * k;// B点基线
//				areaC += values.get(i) - vi;
//				final float yi = (int) (bottom - 50 - (bottom - top - 100) * (values.get(i) - min) / distance);
//				final float ybi = (int) (bottom - 50 - (bottom - top - 100) * (vi - min) / distance);
//				// canvas.drawLine(i * ppp, yi, i * ppp, ybi, paint);
//				if (i == ca) {
//					pathT.moveTo(i * ppp, ybi);
//				} else {
//					pathT.lineTo(i * ppp, yi);
//				}
//			}
//			canvas.drawPath(pathT, paint);
//			if (areaC <= 0) {
//				areaC = 0.0000001f;
//			}
//			paint.setStrokeWidth(1);
//			float vbb = values.get(a) + (b - a) * k;// B点基线
//			final float ybb = (int) (bottom - 50 - (bottom - top - 100) * (vbb - min) / distance);
//			// final float peak = values.get(b) - vbb;
//
//			// canvas.drawLine(ca, yca, cc, ycc, paint);
//			float vcbb = values.get(a) + (cb - a) * k;// B点基线
//			final float ycbb = (int) (bottom - 50 - (bottom - top - 100) * (vcbb - min) / distance);
//			// final float peakc = values.get(cb) - vcbb;
//
//			if (Params.DEBUG) {
//				canvas.drawText(String.format("%.2f", area), b * ppp, yb - 5, paint);
//				canvas.drawText(String.format("%.2f", areaC), cb * ppp, ycb - 5, paint);
//
//				canvas.drawText(String.format("%.2f", values.get(b)), b * ppp, top + 5, paint);
//				canvas.drawText(String.format("%.2f", values.get(cb)), cb * ppp, top + 5, paint);
//				// canvas.drawText(String.format("%.2f", max), right/2, top+5, paint);
//
//			}
//
//			String result = "", textTC = null;
//			if (areaC < 100) { // 拟低于392则设置为无效卡，峰面积低于2000判定无效卡
//				result = MyApp.getApp().getString(R.string.invalid_card);
//			} else {
//				double tc = area / areaC;
//				textTC = "T/C : " + String.format("%.3f", tc);
//				tc *= Params.K_Peaks[channel];
//				tc += Params.B_Peaks[channel];
//				textTC += "," + String.format("%.3f", tc);
//
//				// Specimen sp=ListUtil.getSpecimens(data.getProj(),data.getSpecimen());
//				// if(null!=sp&&sp.getCalculator()!=null) {
//				// LogUtil.d("calculate="+sp.getCalculator().toString());
//				// result=sp.getCalculator().calculate(tc);
//				// }else {
//				// result="no function";
//				// LogUtil.d("proj.specimen"+data.getProj()+data.getSpecimen() );
//				// LogUtil.d("sp="+sp);
//				// }
//				// if(TextUtils.isEmpty(result)) {
//				// result = textTC;
//				//
//				// }
//			}
//
//			float textH = paint.descent() - paint.ascent();
//			float textW = paint.measureText(result) + 8;
//			canvas.drawText(result, right - textW, top + textH, paint);
//			if (Params.DEBUG && textTC != null) {// Params.DEBUG&&
//				canvas.drawText(textTC, left + 10, top + textH, paint);
//			}
//			Hump hump = new Hump(a, b, c, ca, cb, cc);
//
//			// Data data=new Data(sn, specimen, proj, limit, peak+"/"+peakc, customerOrg,
//			// workOrg, operator,
//			// values, peak, peakc,hump);
//			data.setHump(hump);
//			data.setT(area);
//			data.setC(areaC);
//			data.setResult(result);
//			iv.setImageBitmap(bmp);
//
//			// drawRainbow(canvas, paint);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

	public static Bitmap showPeak(List<Float> oValues, final FluData[] datas, int channel, int lines) {
		LogUtil.d("showPeak " + Thread.currentThread().getName());
		int W = 400, H = 300;
		Bitmap bmp = Bitmap.createBitmap(W, H, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		if (null == oValues || oValues.size() < 10 || channel < 0 || channel >= Target.N || lines < 1 || lines > 3) {
			canvas.drawText("参数错误", 50, H / 2, paint);
			return null;
		}
		int size = oValues.size();
		LogUtil.d("size=" + size);
		canvas.drawColor(Color.WHITE);
		float left = W * 0.002f, top = H * 0.025f, right = W - left * 2, bottom = H - top * 2;
		paint.setTextSize(H * 0.06F);
		// List<Float> values=new ArrayList<Float>(size);
		/** 下面進行基線校正。没什么意义。 */
		// SimpleRegression regression=new SimpleRegression();
		// for(int i=0;i<oValues.size();i++) {
		// regression.addData(i,oValues.get(i));
		// }
		// RegressionResults results=regression.regress();
		// double pb=results.getParameterEstimate(0);
		// double pk=results.getParameterEstimate(1);
		//// canvas.drawLine(startX, startY, stopX, stopY, paint);
		// for(int i=0;i<size;i++) {
		// values.add((float) (oValues.get(i)-(i)*pk+pb));
		// }
		//

		/********** 对于较差的信号，使用下面的方法降噪 ********/
		// double[] odata=new double[oValues.size()];
		// for(int i=0;i<size;i++) {
		// odata[i]=oValues.get(i);
		// }
		//// LogUtil.d(Arrays.toString(odata));
		// double[] fv=Wavelet.waveletDenoise(odata);
		// if(null!=fv&&fv.length>size/2) {
		// for(double d:fv) {
		// values.add((float) d);
		// }
		// size=values.size();
		// }else {
		// /****************不做处理就留下下面这一行。***/
		// }
		//
		// values.addAll(oValues);

		List<Float> values = oValues;
		int[] as = new int[lines];
		int[] bs = new int[lines];
		int[] cs = new int[lines];

		for (int i = 0; i < lines; i++) { // 目前方向，最上面，bs[0]是C线
			bs[i] = Curve.getHumpIndex(values, size / lines * (i + 0.15), size / lines * (i + 0.9));
			as[i] = Curve.getMinIndex(values, bs[i] - size * 0.09, bs[i]);
			cs[i] = Curve.getMinIndex(values, bs[i], bs[i] + size * 0.09);
			as[i] = Curve.bathFoot(values, as[i], cs[i], as[i], (as[i] + bs[i]) / 2);
			cs[i] = Curve.bathFoot(values, as[i], cs[i], cs[i], (bs[i] + cs[i]) / 2);
		}
		if (!Params.DEBUG) {// 下面来进行扣基线的操作。
			values = new ArrayList<Float>();
			for (int i = 0; i < lines; i++) {
				for (int j = values.size(); j <= as[i]; j++) { // 或从0 / cs[i-1]开始
					values.add(0f);
				}

				float kca = (oValues.get(cs[i]) - oValues.get(as[i])) / (cs[i] - as[i]);
				for (int j = as[i] + 1; j < cs[i]; j++) {
					values.add(oValues.get(j) - (oValues.get(as[i]) + kca * (j - as[i])));
				}

			}
			for (int i = values.size(); i < oValues.size(); i++) {
				values.add(0f);
			}
		}

		Float min = values.get(0);
		Float max = min;
		for (Float x : values) {
			if (x > max) {
				max = x;
			} else if (x < min) {
				min = x;
			}
		}
		LogUtil.d("max=" + max + ",min=" + min);
		float ppp = (right - left) / values.size();// pixels per point
		paint.setStyle(Style.STROKE);

		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(1);
		canvas.drawRect(left, top, right, bottom, paint);
		paint.setColor(Color.RED);

		float distance = (max - min) > MIN_DISTANCE ? (max - min) : MIN_DISTANCE;
		/*********************** 画曲线 **********************************/
		Path path = new Path();
		float y = (bottom - 50 - (bottom - top - 100) * (values.get(0) - min) / distance);
		path.moveTo(0, y);
		for (int i = 1; i < values.size(); i++) {
			float x = left + i * ppp;
			y = (bottom - 50 - (bottom - top - 100) * (values.get(i) - min) / distance);
			// LogUtil.d("y" + i + "=" + y + ", v=" + values.get(i));
			path.lineTo(x, y);
		}
		canvas.drawPath(path, paint);
		paint.setStyle(Style.FILL_AND_STROKE); // 画实心
		/*********************** 画曲线结束 **********************************/

		float[] areas = new float[lines];
		Path pathT = new Path();
		for (int l = 0; l < lines; l++) {
			float k = (values.get(cs[l]) - values.get(as[l])) / (cs[l] - as[l]);
			for (int i = as[l]; i <= cs[l]; i++) { // 画峰面积
				float vbi = values.get(as[l]) + (i - as[l]) * k;// B点基线

				areas[l] += values.get(i) - vbi;
				final float yi = (int) (bottom - 50 - (bottom - top - 100) * (values.get(i) - min) / distance);
				final float ybi = (int) (bottom - 50 - (bottom - top - 100) * (vbi - min) / distance);
				if (i == as[l]) {
					pathT.moveTo(i * ppp, ybi);
				} else {
					pathT.lineTo(i * ppp, yi);
				}
				// canvas.drawLine(i * ppp, yi, i * ppp, ybi, paint);
			}
			paint.setStyle(Style.FILL);
			canvas.drawPath(pathT, paint);
			if (areas[l] < 0) {
				areas[l] = 0.0000001f;
			}
		}
		for (int i = 0; i < lines; i++) {
			final float yb = (int) (bottom - 50 - (bottom - top - 100) * (values.get(bs[i]) - min) / distance);

			String text = i == 0 ? "C " + String.format("%.2f", areas[i]) : "T " + String.format("%.2f", areas[i]);
			canvas.drawText(text, bs[i] * ppp, yb - 5, paint);
			// canvas.drawText(String.format("%.2f", values.get(bs[i])), bs[i] * ppp, top+5,
			// paint);
		}

		
		
		for (int i = 1; i < lines; i++) {
			String result = "", textTC = null;
			History.TC tcr=History.historyRepare(new History.TC(areas[i],areas[0]));
			double tc =tcr.t /tcr.c;
			if (areas[0] < 100) { // 拟低于392则设置为无效卡，峰面积低于2000判定无效卡
				result = MyApp.getApp().getString(R.string.invalid_card);
			} else {

		/************************************做验证的仪器使用**********************************
				if(Math.abs(tc-1.1)<0.3){
					tc=1.1+(tc-1.1)*0.1;
				}else if(Math.abs(tc-0.6)<0.2){
					tc=0.6+(tc-0.6)*0.1;
				}else if(Math.abs(tc-1.8)<0.3){
					tc=1.8+(tc-1.8)*0.1;
				}
		************************************做验证的仪器使用**********************************/


				textTC = "T/C : " + String.format("%.3f", tc);
//				tc *= Params.K_Peaks[channel];
//				tc += Params.B_Peaks[channel];
				tc*=Params.getK();
				tc+=Params.getB();
				textTC += "," + String.format("%.3f", tc);
				String v=MyApp.getApp().getTCLimits().get(datas[i-1].getProj()+"-"+datas[i-1].getSpecimen());
				float limit=0.9f;
				if (null != v&&v.length() > 5) {
					LogUtil.d("v:"+v);
					try {
						limit = Float.parseFloat(v.substring(5));
					} catch (Exception e) {
						e.printStackTrace();
						ToastUtil.postMessage("参考限制设置异常");
					}
					datas[i-1].setLimit(limit);
					if(!v.contains(">")){ //<=
						if (tc <= limit) {
							result = MyApp.getApp().getString(R.string.negative);
						} else {
							result = MyApp.getApp().getString(R.string.positive);

						}
					}else{
						if (tc > limit) {
							result = MyApp.getApp().getString(R.string.negative);
						} else {
							result = MyApp.getApp().getString(R.string.positive);
						}
					}
				}else if (tc > limit) {
					result = MyApp.getApp().getString(R.string.negative);
				} else {
					result = MyApp.getApp().getString(R.string.positive);
				}

			}

			float textH = paint.descent() - paint.ascent();
			float textW = paint.measureText(result) + 8;
			canvas.drawText(result, right - textW, top + textH * i, paint);
			if ( textTC != null) {// Params.DEBUG&&
				canvas.drawText(textTC, left + 10, top + textH * i, paint);
			}
			Hump hump = new Hump(as[i], bs[i], cs[i], as[0], bs[0], cs[0]);

			// Data data=new Data(sn, specimen, proj, limit, peak+"/"+peakc, customerOrg,
			// workOrg, operator,
			// values, peak, peakc,hump);
			datas[i - 1].setHump(hump);
//			datas[i - 1].setT(tcr.t);
			datas[i - 1].setT((float) (tcr.c*tc));  //其打印时候也要求有TC值
			datas[i - 1].setC(tcr.c);
			datas[i - 1].setResult(result);
		}
		return bmp;

	}

	public static void drawRainbow(Canvas canvas, Paint paint) {
		if (canvas.getWidth() < 350) {
			canvas.drawText("画布太小", 10, 10, paint);
			return;
		}
		float d = 350f * 600 / canvas.getWidth();
		for (int i = 0; i < 350; i++) { // 左上方画个彩虹条
			int color = ColorUtil.lambdaToColor(380 + i);
			// LogUtil.d("argb=0x"+Integer.toHexString(color));
			// for (int j = 0; j < 50; j++) { //效率低
			// bmp.setPixel(i, j, color);
			// }
			paint.setColor(color);
			canvas.drawRect(i * d, 0, (i + 1) * d, 50 * 480 / UVC.IMG_HEIGHT, paint);
			canvas.drawLine(i, 0, i, 50, paint);
		}
		paint.setColor(Color.WHITE);
		canvas.drawLine((610 - 380) * d, 0, (610 - 380) * d, 60, paint);
	}

	public static void drawRainbow(Bitmap bmp) {
		if (bmp.getWidth() < 350 || bmp.getHeight() < 50) {
			return;
		}
		for (int i = 0; i < 350; i++) { // 左上方画个彩虹条
			int color = ColorUtil.lambdaToColor(380 + i);
			// LogUtil.d("argb=0x"+Integer.toHexString(color));
			for (int j = 0; j < 50; j++) { // 效率低
				bmp.setPixel(i, j, color);
			}

		}

	}

	public static void showPeak(ArrayList<Float> values, ImageView iv, Canvas canvas, Paint paint2, FluData data) {
		// TODO Auto-generated method stub

	}
}
