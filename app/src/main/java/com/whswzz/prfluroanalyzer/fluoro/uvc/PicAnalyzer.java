package com.whswzz.prfluroanalyzer.fluoro.uvc;

import java.util.ArrayList;

import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.widget.ImageView;
import top.jemen.camera.UVC;
import top.jemen.interfaces.ICallback;
import top.jemen.utils.ColorUtil;
import top.jemen.utils.LogUtil;

public class PicAnalyzer {
//	private void resulveData(int index, Bitmap[] bmps, int n, ImageView iv, Data data, ICallback callback) {
//		Bitmap bitmap = bmps[bmps.length - 1];
//		Canvas canvas = canvass.get(index);
//		canvas.drawBitmap(bitmap, 0, 0, paint);
//		int left = 10, top = 190, w = 580, h = 90; // 内边框，取值范围。
//		int right = (int) (left + w * IMG_WIDTH / 640f);
//		Bitmap bmp = bitmaps.get(index);
//		if (right > bmp.getWidth() - 3) {
//			right = bmp.getWidth() - 3;
//		}
//
//		int v = Integer.MAX_VALUE, x = top;
//		for (int i = top - 50; i < top + 40; i++) {
//			int sum = 0;
//			for (int j = left; j < right; j += 3) {
//				int color = bmp.getPixel(j, i);
//				sum += (color & 0xff) + (color >> 8 & 0xff) + (color >> 16 & 0xff);
//			}
//			if (sum < v) {
//				v = sum;
//				x = i;
//			}
//		}
//		top = x + 10;
//		LogUtil.d("top=" + top);
//		left *= UVC.IMG_WIDTH / 640f;
//		top *= UVC.IMG_HEIGHT / 480f;
//
//		int bottom = (int) (top + h * IMG_HEIGHT / 480f);
//
//		paint.setTextSize(20);
//
//		paint.setColor(Color.WHITE);
//
//		ArrayList<Float> values = new ArrayList<Float>();
//		float min = ColorUtil.Value610(bmp.getPixel(left + 10, top + 10)), max = 0;
//		for (int i = left; i < right; i++) {
//			float sum = 0;
//
//			for (int j = top; j <= bottom; j += 10) {// 画的绿色框会干扰的吧，改成每5行取一个点,h/5=18
//				for (int p = bmps.length - 1; p >= 0 && p >= bmps.length - n; p--) {
//					sum += ColorUtil.Value610(bmps[p].getPixel(i, j));
//					sum += ColorUtil.Value610(bmps[p].getPixel(i - 1, j));
//					sum += ColorUtil.Value610(bmps[p].getPixel(i + 1, j));
//					sum += ColorUtil.Value610(bmps[p].getPixel(i - 2, j));
//					sum += ColorUtil.Value610(bmps[p].getPixel(i + 2, j));
//				}
//			}
//			float a = sum / ((bottom - top) / 10 * 5 * n);
//
//			if (a > max) {
//				max = a;
//			} else if (a < min) {
//				min = a;
//			}
//			values.add(a);
//		}
//		paint.setColor(Color.GREEN);
//		// canvas.drawRect(left-20, top-10, right+20, bottom+20, paint);// 画个方框
//		canvas.drawRect(left, top, right, bottom, paint);// 画个方框
//
//		// Bitmap card = Bitmap.createBitmap(bmp, left, top, right - left, bottom -
//		// top);
//
//		CurveUtil.showPeak(values, iv, canvas, paint, data);
//
//		// c.drawBitmap(card, 1, 1, paint);
//		LogUtil.d("doinbackground over");
//
//		iv.setImageBitmap(bmp);
//		// camera.release();
//		for (Bitmap bm : bmps) {
//			if (null != bm)
//				bm.recycle();
//		}
//		callback.onSuccess(null);
//	}
}
