package com.whswzz.prfluroanalyzer.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.entity.IData;
import com.whswzz.prfluroanalyzer.entity.Species;
import com.whswzz.prfluroanalyzer.enzyme.entity.EnzymeData;
import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;
import com.whswzz.prfluroanalyzer.param.Params;
import com.whswzz.prfluroanalyzer.photometer.entity.PhotometerData;
import com.zkzk.pra.entity.Data;
import com.zkzk.pra.utils.ExceptionHandler;
import com.zkzk.pra.utils.Tools;
import com.zkzk.pra.utils.Utils;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.w3c.dom.Text;

import top.jemen.interfaces.ICallback;
import top.jemen.model.ComM;
import top.jemen.utils.LogUtil;
import top.jemen.utils.Tone;
import top.jemen.utils.threadpool.AsyncProcessor;

/**
 * 杰普维打印机，使用75*65的热敏标签纸最好。
 * 
 * @author Administrator
 *
 */
public class PrinterJPW {

	static String version = Tools.getCurrentVersion(MyApp.getApp());

	/**
	 * 杰普维打印机适用
	 * 
	 * @param data
	 */
	public static void print(IData data, ICallback callback) {
		List<IData> ls = new ArrayList<>();
		ls.add(data);
		print(ls, callback);
	}

	public static void print(IData data) {
		print(data, null);
	}

	/**
	 * 
	 */
	public static synchronized void print(final List<IData> datas, final ICallback callback) {
		if (null == datas || datas.size() == 0) {
			callback.onFailed("请选择待打印数据");
			return;
		}

		int x = 0;
		for (IData data : datas) {
			if (!TextUtils.isEmpty(data.getResult())) {
				x++;
			}
		}
		if (x == 0) {
			callback.onFailed("无有效数据");
			return;
		}

		new AsyncTask<Void, Void, Boolean>() { // 如果用execute（）函数，即使多个对象也是同一个线程执行。
			@Override
			protected Boolean doInBackground(Void... params) {
				boolean result = false;
				if (!MyApp.getApp().printDetail()) {
					return printSummary(datas);
				}
				try {
					ComM.get().send(2, init);// 初始化打印机
					int W = 576, H = 500; //H从600改为800   910 460
					for (IData data : datas) {
						ComM.get().send(2, new byte[] { 0x1a, 0x5B, 0x01, 00, 00, 00, 00, (byte) W, (byte) (W >> 8),
								(byte) H, (byte) (H >> 8), 0 });// 页开始，左上角x偏移0，y偏移0，宽4002（576），高4001（320），旋转0
						printString(183, 10, 24, 0x2200, "检测结果"); // 2号字体24，33为3号字体
						int h = 70;

						h += printString(0, h, "通道号：" + data.getChannel());
						h += printString(0, h, "样品名称：" + data.getSpecimen());
//						h += printString(0, h, "样品编号：" + data.getSn());
						h += printString(0, h, "样品编号：" + (TextUtils.isEmpty(MyApp.globalCurrentSn) ? data.getSn() : MyApp.globalCurrentSn));
						printString(0, h, "样品产地：" + data.getSourceAddr());
						h += 30;
						printString(0, h, "被检单位：" + data.getSourceUnit());
						h += 30;
						h += printString(0, h, "检测项目：" + data.getProj());
						if (data instanceof FluData) {
//							h += printString(0, h, "检测标准：" + Params.GB);
							h += printString(0, h, "检测标准：" +getGB(data.getProj()));
							LogUtil.d("t/c:"+String.format("%.2f",((FluData)data).getT()/((FluData)data).getC()));
							h += printString(0, h, "T/C值："+String.format("%.2f",((FluData)data).getT()/((FluData)data).getC()));

							String key = data.getProj() + "-" + data.getSpecimen();
//							LogUtil.d(key);
							Double v = MyApp.getApp().getLimits().get(key);
							if (null != v) {
								h += printString(0, h, "检测限值：" + String.format("%.3f", v) + " mg/kg");
							} else {
							}

						}else if(data instanceof PhotometerData) {


						}else { //分光农残和酶片式酶抑制率
							h += printString(0, h, "检测标准：" + Params.GBT);
							h += printString(0, h, "检测限值：50%抑制率");
							if (data instanceof Data) {
								h += printString(0, h, "抑制率："
										+ String.format("%.2f", (((Data) data).getInhibitionRatio() * 100)) + "%");
							}
						}

						h += printString(0, h, "检测结果：" + data.getResult()); // 纵向间隔30挺好

						printString(0, h, "检测时间：" + Consts.SDFM.format(new Date(data.getTime()))); // 纵向间隔30挺好
						h += 30;
						h += printString(0, h, "检测单位：" + data.getUserName());
						h += printString(0, h, "检测人员：" + data.getOperator());

						if(!version.toLowerCase().contains("sak") && hasUserPhone(data))
							h += printString(0, h, "联系方式：" + data.getUserPhone());
						LogUtil.d("版本号："+version.toLowerCase());
						LogUtil.d("数据为："+data.getUserPhone()+":"+data.getId()+":"+data.getChannel()+":"+data.getSn()+":"+data.getProj());
						LogUtil.d("联系方式："+data.getUserPhone());
//						SystemClock.sleep(500);
//						printQR(20,h,generateQRString(data));
//						printQR(10,h,"中国万岁");

						ComM.get().send(2, new byte[] { 0x1a, 0x5d, 0x00, 0x1a, 0x4f, 0x00 });// 页结束和页打印
						Thread.sleep(300);
//						printCode("http://zy.jemen.top/code/wx/base.html?"+generateQRString(data));
						printCode(generateQRString(data));
						LogUtil.d("打印数据:"+generateQRString(data));
						SystemClock.sleep(300);
					} // for循环结束

					ComM.get().send(2, new byte[] { 0x1b, 0x69 });// 全切纸
					result = true;

				} catch (Exception e) {
					ExceptionHandler.handleException(e);
				} finally {
				}

				return result;
			}

			protected void onPostExecute(Boolean result) {
				if (null == callback) {
					return;
				}
				if (result)
					callback.onSuccess(result);
				else
					callback.onFailed(result);
			};
		}.executeOnExecutor(PRINT_EXECUTOR);
	}

	protected static Boolean printSummary(List<IData> datas) {
		ComM.get().send(2, new byte[] { 0x1b, 0x40 });// 初始化打印机
		int W = 576, H = 90+30*datas.size()+30*4+50;
		ComM.get().send(2, new byte[] { 0x1a, 0x5B, 0x01, 00, 00, 00, 00, (byte) W, (byte) (W >> 8), (byte) H,
				(byte) (H >> 8), 0 });// 页开始，左上角x偏移0，y偏移0，宽4002（576），高4001（320），旋转0
		int h = 20;
		h += printString(0, h, "      检测结果打印");
//		h += printString(0, h, "通道号     检测结果     检测时间");
//		for (IData data : datas) {
//			h += printString(0, h, "  " + data.getChannel() + "  " + data.getResult() + "  "
//					+ Consts.MDKM.format(new Date(data.getTime())));
//			SystemClock.sleep(30);
//		} // for循环结束

		h += printString(0, h, "通道号    样品名称    检测项目      检测结果 ");
		for (IData data : datas) {
			String s="  "+data.getChannel();
			if(data.getChannel().length()<2){
				s+=" ";
			}
			String specimen=TextUtils.isEmpty(data.getSpecimen())?"未选择 ":data.getSpecimen();
			int t=10+(specimen.length()-4);
			for(int i=getW(s);i<=t;i++){
				s+=" ";
			}
			s+=specimen;

			t=21;
			if(null!=data.getProj()){
				t-=(data.getProj().length()-4);
			}
			for(int i=getW(s);i<=t;i++){
				s+=" ";
			}
			s+=data.getProj();
			for(int i=getW(s);i<=37;i++){
				s+=" ";
			}
			s+=data.getResult();
			LogUtil.d(s);
			h += printString(0, h, s);
			SystemClock.sleep(30);
		} // for循环结束
		h += printString(0, h, "样品来源：" + datas.get(0).getSourceUnit());
		h += printString(0, h, "检测单位：" + datas.get(0).getUserName());
		h += printString(0, h, "检测人员：" + datas.get(0).getOperator());
		h +=  printString(0, h, "打印时间：" + Consts.SDFM.format(new Date())); // 纵向间隔30挺好

		ComM.get().send(2, new byte[] { 0x1a, 0x5d, 0x00, 0x1a, 0x4f, 0x00 });// 页结束和页打印
		ComM.get().send(2, new byte[] { 0x1b, 0x69 });// 全切纸
		return true;
	}

	private static  int getW(String s){
		if(TextUtils.isEmpty(s))
			return 0;
		int sum=0;
		for(int i=0;i<s.length();i++){
			sum+=s.charAt(i)>256?2:1;
		}
		return sum;
	}


	public static final Executor PRINT_EXECUTOR = Executors.newSingleThreadExecutor();

	/**
	 * 打印文字，一行最多24个中文。或者48个英文
	 * 
	 * @param x
	 * @param y
	 * @param text
	 * @return
	 */
	public static int printString(int x, int y, String text) {
		if (TextUtils.isEmpty(text)) {
			return 0;
		}
		// 字符串的字符数量
		int totalLength = text.length();

		// 字符串中中文（含中文符合）字符数量
		int chCharsLength = (text.getBytes().length - totalLength) / 2;		//中文占两个字节，英文占一个字节

		// 字符串中英文（含符号）、数字字符数量
		int enCharsLength = totalLength - chCharsLength;
		if (chCharsLength * 2 + enCharsLength <= 48) {
			byte[] cs = {};
			try {
				cs = text.getBytes("GBK");
			} catch (Exception e) {
				e.printStackTrace();
			}
			byte[] bs = new byte[8 + cs.length];
			bs[0] = 0x1a;
			bs[1] = 0x54;
			bs[2] = 0x00;
			bs[3] = (byte) (x & 0xff);
			bs[4] = (byte) (x >> 8 & 0xff);
			bs[5] = (byte) (y & 0xff);
			bs[6] = (byte) (y >> 8 & 0xff);
			for (int i = 0; i < cs.length; i++) {
				bs[7 + i] = cs[i];
			}
			bs[bs.length - 1] = 0x00; // 以00结束
//			LogUtil.d("print send:"+Arrays.toString(bs));
			ComM.get().send(2, bs);
			return 30;
		} else { //>48字符
			int h = 0, n = 0, i;
			for (i = 0; i < text.length(); i++) {
				int cx = text.charAt(i);
				if (cx > 256) {
					n += 2;
				} else {
					n++;
				}
				if (n == 48) {
					n = 0;
					h += printString(x, y + h, text.substring(0, i));
					h += printString(x, y + h, text.substring(i));
					return h;
				} else if (n > 48) {
					n = 0;
					i--;
					h += printString(x, y + h, text.substring(0, i));
					h += printString(x, y + h, text.substring(i));
					return h;
				}
			} // end of for
			return h;
		}
	}

	/**
	 * 
	 * @param x
	 *            定义文本起始位置 x 坐标，取值范围：[0, Page_Width-1]
	 * @param y
	 *            定义文本起始位置 y 坐标，取值范围：[0, Page_Height-1]；
	 * @param fontHeight
	 *            文本字符字体高度，有效值范围为{16, 24, 32, 48, 64, 80,
	 * @param fontType
	 *            文本字符特效，各位定义如下 [0] 字体加粗 ,[1]下划线,[2]反白标志位,[4] 旋转标志位： 0 旋转 0° ；1 旋转
	 *            90° [11:8] 位图宽度放大倍数。[15:12] 位图高度放大倍数。 0x2200二倍字体，0x3300三倍字体
	 * @param text
	 * @return
	 */
	public static void printString(int x, int y, int fontHeight, int fontType, String text) {
		byte[] cs = {};
		try {
			cs = text.getBytes("GBK");
		} catch (Exception e) {
			e.printStackTrace();
		}
		byte[] bs = new byte[12 + cs.length];
		bs[0] = 0x1a;
		bs[1] = 0x54;
		bs[2] = 0x01; // 1才生效
		bs[3] = (byte) (x & 0xff);
		bs[4] = (byte) (x >> 8 & 0xff);
		bs[5] = (byte) (y & 0xff);
		bs[6] = (byte) (y >> 8 & 0xff);
		bs[7] = (byte) (fontHeight & 0xff);
		bs[8] = (byte) (fontHeight >> 8 & 0xff);
		bs[9] = (byte) (fontType & 0xff); // 第8~·12位，更多位数无效。
		bs[10] = (byte) (fontType >> 8 & 0xff); // 第0~7位

		for (int i = 0; i < cs.length; i++) {
			bs[11 + i] = cs[i];
		}
		bs[bs.length - 1] = 0x00; // 以00结束
		ComM.get().send(2, bs);
	}

	/**
	 * 农产品生产者开具承诺达标合格证样式
	 * @param datas
	 * @param callback
	 */
	public static void printCert(final List<IData> datas, final ICallback callback) {
		if (null == datas || datas.size() == 0) {
			callback.onFailed("无数据");
			return;
		}
		new AsyncTask<Void, Void, Boolean>() { // 如果用execute（）函数，即使多个对象也是同一个线程执行。
			@Override
			protected Boolean doInBackground(Void... params) {
				boolean result = false;
				try {
					ComM.get().send(2, new byte[] { 0x1b, 0x40 });// 初始化打印机
					int W = 576, H = 550;
					for (IData data : datas) {
						ComM.get().send(2, new byte[] { 0x1a, 0x5B, 0x01, 00, 00, 00, 00, (byte) W, (byte) (W >> 8),
								(byte) H, (byte) (H >> 8), 0 });// 页开始，左上角x偏移0，y偏移0，宽4002（576），高4001（320），旋转0
						// 高度改成420，0X01A4
						printString(140, 10, 24, 0x2200, "承诺达标合格证"); // 2号字体，33为3号字体

						printString(70, 500, 24, 0x2000, "承诺达标合格证");//展示在二维码前的一行字体

						// 常规小字体一行最多24个汉字。
						int h = 70;
//						h += printString(0, h, "我承诺对生产销售的食用农产品：");
//						h += printString(0, h, "  不使用禁用农药兽药、停用兽药和非法添加物");
//						h += printString(0, h, "  常规农药兽药残留不超标");
//						h += printString(0, h, "  对承诺的真实性负责");
//						h += printString(0, h, "承诺依据：");
//						h += printString(0, h, "      □委托检测合格        □自行检测合格");
//						h += printString(0, h, "      □质量安全控制符合要求");
//						h += printString(0, h, "-------------------------------------------");
//						h += printString(0, h, "产品名称：" + data.getSpecimen());
////						h += printString(0, h, "数量（重量）：");
//
//						h += printString(0, h, "样本产地：" + data.getSourceAddr());
//						h += printString(0, h, "检测项目："+data.getProj());
//						h += printString(0, h, "检测单位：" + data.getUserName());
//						h += printString(0, h, "联系人：" + data.getUserContact());
//						h += printString(0, h, "联系方式：" + data.getUserPhone());
//						h += printString(0, h, "开具时间：" + Consts.SDFM.format(new Date(System.currentTimeMillis())));
						h += printString(0, h, "我承诺生产销售的食用农产品：\n");
						h += printString(0, h, "未使用禁用农药、兽药及其他化合物；\n");
						h += printString(0, h, "使用的常规农药、兽药残留不超标。\n");
						h += printString(0, h, "\n");
						h += printString(0, h, "承诺依据：\n");
						h += printString(0, h, "□质量安全控制符合要求  □自行检测合格  □委托检测合格\n");
						h += printString(0, h, "-------------------------------------------");
						h += printString(0, h, "产品名称：" + data.getSpecimen()+"\n");
						h += printString(0, h, "重量或数量："+"\n");
						h += printString(0, h, "产   地：" + data.getSourceAddr()+"\n");
						h += printString(0, h, "承诺主体：" + data.getUserName()+"\n");
						if (hasUserPhone(data)) {
							h += printString(0, h, "联系方式：" + data.getUserPhone()+"\n");
						}
						h += printString(0, h, "开具时间：" + Consts.SDFMND.format(new Date(System.currentTimeMillis())));
						ComM.get().send(2, new byte[] { 0x1a, 0x5d, 0x00, 0x1a, 0x4f, 0x00 });// 页结束和页打印
//						printQR(W/2,h,generateQRString(data));
						Thread.sleep(300);
//						String s="\n我承诺对生产销售的食用农产品："
//								+"\n  不使用禁用农药兽药、"
//								+"\n  停用兽药和非法添加物"
//								+"\n  常规农药兽药残留不超标"
//								+"\n对承诺的真实性负责"
//								+"\n产品名称：" + data.getSpecimen()
//								+"\n样本产地：" + data.getSourceAddr()
//								+"检测项目：" + data.getProj()
//								+"检测单位："+data.getUserName()
//								+"联系人：" + data.getUserContact()
//								+"\n联系方式：" + data.getUserPhone()
//								+"\n开具时间：" + Consts.SDFM.format(new Date(System.currentTimeMillis()));
//								+"\n" + data.getUserName();
						String phoneLine = hasUserPhone(data) ? "\n联系方式：" + data.getUserPhone() : "";
						String s="\n我承诺生产销售的食用农产品："
								+"\n未使用禁用农药、兽药及其他化合物；"
								+"\n使用的常规农药、兽药残留不超标。"
								+"\n承诺依据："
								+"\n□质量安全控制符合要求  □自行检测合格  □委托检测合格"
								+"\n产品名称：" + data.getSpecimen()
								+"\n重量或数量："
								+"\n产地：" + data.getSourceAddr()
								+"\n承诺主体："+data.getUserName()
								+ phoneLine
								+"\n开具时间：" + Consts.SDFMND.format(new Date(System.currentTimeMillis()));
//						printCode("http://zy.jemen.top/code/wx/base.html?"+s);
						printCode(buildQrUrl(QR_BASE_URL, s.trim()));
					} // for循环结束
					ComM.get().send(2, new byte[] { 0x1b, 0x69 });// 全切纸
					result = true;
				} catch (Exception e) {
					ExceptionHandler.handleException(e);
				} finally {
				}
				return result;
			}

			protected void onPostExecute(Boolean result) {
				if (null == callback) {
					return;
				}
				if (result)
					callback.onSuccess(result);
				else
					callback.onFailed(result);
			};
		}.executeOnExecutor(PRINT_EXECUTOR);
	}

	/**
	 * 农产品收购单位（个人）开具的承诺达标合格证样式
	 * @param datas
	 * @param callback
	 */
	public static void printCertPerson(final List<IData> datas, final ICallback callback) {
		if (null == datas || datas.size() == 0) {
			callback.onFailed("无数据");
			return;
		}
		new AsyncTask<Void, Void, Boolean>() { // 如果用execute（）函数，即使多个对象也是同一个线程执行。
			@Override
			protected Boolean doInBackground(Void... params) {
				boolean result = false;
				try {
					ComM.get().send(2, new byte[] { 0x1b, 0x40 });// 初始化打印机
					int W = 576, H = 550;
					for (IData data : datas) {
						ComM.get().send(2, new byte[] { 0x1a, 0x5B, 0x01, 00, 00, 00, 00, (byte) W, (byte) (W >> 8),
								(byte) H, (byte) (H >> 8), 0 });// 页开始，左上角x偏移0，y偏移0，宽4002（576），高4001（320），旋转0
						// 高度改成420，0X01A4
						printString(140, 10, 24, 0x2200, "承诺达标合格证"); // 2号字体，33为3号字体

						printString(70, 500, 24, 0x2000, "承诺达标合格证");//展示在二维码前的一行字体

						// 常规小字体一行最多24个汉字。
						int h = 70;
						h += printString(0, h, "我承诺销售的食用农产品：");
						h += printString(0, h, "已按规定收取并保存该批次农产品承诺达标合格证或者其他质量安全合格证明；");
						h += printString(0, h, "未违规使用保鲜剂、防腐剂、添加剂等。");
						h += printString(0, h, "\n");
						h += printString(0, h, "承诺依据：");
						h += printString(0, h, "□质量安全控制符合要求  □自行检测合格  □委托检测合格");
						h += printString(0, h, "-------------------------------------------");
						h += printString(0, h, "产品名称：" + data.getSpecimen());
						h += printString(0, h, "重量或数量：");
						h += printString(0, h, "承诺主体：" + data.getUserName());
						if (hasUserPhone(data)) {
							h += printString(0, h, "联系方式：" + data.getUserPhone());
						}
						h += printString(0, h, "开具时间：" + Consts.SDFMND.format(new Date(System.currentTimeMillis())));
						ComM.get().send(2, new byte[] { 0x1a, 0x5d, 0x00, 0x1a, 0x4f, 0x00 });// 页结束和页打印
						Thread.sleep(300);
						String phoneLine = hasUserPhone(data) ? "\n联系方式：" + data.getUserPhone() : "";
						String s="\n我承诺销售的食用农产品："
								+"\n已按规定收取并保存该批次农产品承诺达标合格证或者其他质量安全合格证明；"
								+"\n未违规使用保鲜剂、防腐剂、添加剂等。"
								+"\n承诺依据："
								+"\n□质量安全控制符合要求  □自行检测合格  □委托检测合格"
								+"\n产品名称：" + data.getSpecimen()
								+"\n重量或数量："
								+"\n承诺主体："+data.getUserName()
								+ phoneLine
								+"\n开具时间：" + Consts.SDFMND.format(new Date(System.currentTimeMillis()));
//						printCode("http://zy.jemen.top/code/wx/base.html?"+s);
						printCode(buildQrUrl(QR_BASE_URL, s.trim()));
					} // for循环结束
					ComM.get().send(2, new byte[] { 0x1b, 0x69 });// 全切纸
					result = true;
				} catch (Exception e) {
					ExceptionHandler.handleException(e);
				}
				return result;
			}

			protected void onPostExecute(Boolean result) {
				if (null == callback) {
					return;
				}
				if (result)
					callback.onSuccess(result);
				else
					callback.onFailed(result);
			};
		}.executeOnExecutor(PRINT_EXECUTOR);
	}
	public static void printCert(IData data, ICallback callback) {
		List<IData> datas = new LinkedList<IData>();
		datas.add(data);
		printCert(datas, callback);
	}
	public static void printCertPerson(IData data, ICallback callback) {
		List<IData> datas = new LinkedList<IData>();
		datas.add(data);
		printCertPerson(datas, callback);
	}

	public static void showPrintChoice(Activity context, final List<IData> data, final View btPrint) {
		final String[] items = { "打印常规结果", "打印承诺达标合格证（农产品生产者）","打印承诺达标合格证（农产品收购单位/个人）"};
		AlertDialog.Builder listDialog = new Builder(context);
		listDialog.setTitle("请选择打印类型");
		listDialog.setCancelable(false);
		listDialog.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					PrinterJPW.print(data, new ICallback() { // 此函数内部会开启新的线程以适配打印机的速度以防打印机缓存爆掉。
						@Override
						public void onSuccess(Object obj) {
							btPrint.setEnabled(true);
						}

						@Override
						public void onFailed(Object obj) {
							btPrint.setEnabled(true);
						}
					});
				} else if (which == 1){
					PrinterJPW.printCert(data, new ICallback() { // 此函数内部会开启新的线程以适配打印机的速度以防打印机缓存爆掉。
						@Override
						public void onSuccess(Object obj) {
							btPrint.setEnabled(true);
						}

						@Override
						public void onFailed(Object obj) {
							btPrint.setEnabled(true);
						}
					});
				} else {
					PrinterJPW.printCertPerson(data, new ICallback() { // 此函数内部会开启新的线程以适配打印机的速度以防打印机缓存爆掉。
						@Override
						public void onSuccess(Object obj) {
							btPrint.setEnabled(true);
						}

						@Override
						public void onFailed(Object obj) {
							btPrint.setEnabled(true);
						}
					});
				}
			}
		});
		listDialog.show();
		btPrint.setEnabled(false);
	}

	public static void showPrintChoice(Activity context, IData data, Button btPrint) {
		List<IData> ls = new ArrayList<IData>();
		ls.add(data);
		showPrintChoice(context, ls, btPrint);
	}

	private static final String QR_BASE_URL = "https://www.shangchengbios.cn/saoma/base.html?";

	private static String buildQrUrl(String baseUrl, String content) {
		if (content == null) {
			content = "";
		}
		try {
			String encoded = Base64.encodeToString(content.getBytes("UTF-8"), Base64.NO_WRAP);
			return baseUrl + URLEncoder.encode(encoded, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			ExceptionHandler.handleException(e);
			return baseUrl + content;
		}
	}

	/**
//	 * 打印二维码，初始化打印机命令就不弄了。
//	 * @param text
//	 */
	public static void printQR(int x,int y,String text){
		if (null == text) {
			return;
		}
		byte[] bs= new byte[0];
		try {
			bs = text.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		byte[] data=new byte[bs.length+12];
		data[0]=0x1a;
		data[1]=0x31;
		data[3]=3;//指定字符版本，0的时候打印机根据字符串长度自动计算版本号
		data[4]=3; //纠错登记【1,4】
		data[5]= (byte) x;
		data[6]= (byte) (x>>8);
		data[7]= (byte) y;
		data[8] = (byte) (y >> 8);
		data[9]=4;	//【1，4】，码块
		data[10]=0;	//旋转角度，0-3
		System.arraycopy(bs,0,data,11,bs.length);
		data[data.length-1]=0;

		ComM.get().send(2,data);

		LogUtil.d("x="+x+",y="+y);

//		ComM.get().send(2,new byte[]{0x1A ,0x31, 00, 03, 03, (byte) x, (byte) (x>>8), (byte) y, (byte) (y>>8), 04, 00, (byte) 0xD6, (byte) 0xD0,
//				(byte) 0xB9, (byte) 0xFA, (byte) 0xCD, (byte) 0xF2, (byte) 0xCB, (byte) 0xEA, 00});

	}

	private static boolean hasUserPhone(IData data) {
		if (data == null) {
			return false;
		}
		String phone = data.getUserPhone();
		if (TextUtils.isEmpty(phone)) {
			return false;
		}
		phone = phone.trim();
		return !TextUtils.isEmpty(phone)
				&& !"null".equalsIgnoreCase(phone)
				&& !"undefined".equalsIgnoreCase(phone)
				&& !"无".equals(phone);
	}
	private static  String 	generateQRString(IData data){
		String r="";
		r+=("通道号：" + data.getChannel());
		r+=("\n样品名称：" + data.getSpecimen());
//		r+=("\n样品编号：" + data.getSn());
		r+=("\n样品编号：" + (TextUtils.isEmpty(MyApp.globalCurrentSn) ? data.getSn() : MyApp.globalCurrentSn));
		r+=("\n样品产地：" + data.getSourceAddr());
		r+=( "\n被检单位：" + data.getSourceUnit());
		r+=("\n检测项目：" + data.getProj());
		if (data instanceof FluData) {
			r+=( "\n检测标准：" + Params.GB);
			String key = data.getProj() + "-" + data.getSpecimen();
			LogUtil.d(key);
			r+=("\nT/C值："+String.format("%.2f",((FluData)data).getT()/((FluData)data).getC()));
			Double v = MyApp.getApp().getLimits().get(key);
			if (null != v) {
				r+=( "\n检测限值：" + String.format("%.3f", v) + " mg/kg");
			} else {

			}
		}else if(data instanceof PhotometerData) {

		}else { //分光农残和酶片式酶抑制率
			r+=( "\n检测标准：" + Params.GBT);
			r+=( "\n检测限值：50%抑制率");
			if (data instanceof Data) {
				r+=( "\n抑制率："
						+ String.format("%.2f", (((Data) data).getInhibitionRatio() * 100)) + "%");
			}
		}

		r+=("\n检测结果：" + data.getResult()); // 纵向间隔30挺好

		r+=("\n检测时间：" + Consts.SDFM.format(new Date(data.getTime()))); // 纵向间隔30挺好
		r+=( "\n检测单位：" + data.getUserName());
		r+=("\n检测人员：" + data.getOperator());
		if (hasUserPhone(data)) {
			r+=( "\n联系方式：" + data.getUserPhone());
		}

		r+=("\n打印时间：" + Consts.SDFM.format(new Date(System.currentTimeMillis()))); // 纵向间隔30挺好
//		return "http://zy.jemen.top/code/wx/base.html?"+r;//陈明杰
		return buildQrUrl(QR_BASE_URL, r);//陈祥
	}

	public static String  getGB(String proj){
		List<Species> lsProj = MyApp.getApp().getLsProjs();
		if(null==lsProj||lsProj.size()==0 || TextUtils.isEmpty(proj) ){
			return "";
		}

		for(Species sp:lsProj){
			List<Species> sbs = sp.getSubSpecies();
			if(null==sbs||sbs.size()==0){
				continue;
			}
			for(Species sub:sbs){
				if(proj.equals(sub.getName())){
					return MyApp.getApp().getGBMap().get(sp.getName());
				}
			}
		}
		return "未设置";
	}

	private static final byte[] init={0x1b,0x40};
	private static byte[] bsCodeSize={0x1A	,0x5B	,0x01	,0x00	,0x00	,0x00	,0x00	, (byte)  0x40, 0x02	, (byte) 0x90,0x01	,0x00};

	/**
	 * 二维码打印
	 * @param msg
	 */
	private static void printCode(String msg) {
		if(TextUtils.isEmpty(msg)){
			return;
		}
//        ComM.get().send(2, init);// 初始化打印机
//        int W = 384, H = 250;//12-16改560为660
//        ComM.get().send(2, new byte[]{0x1a, 0x5B, 0x01, 00, 00, 00, 00, (byte) W, (byte) (W >> 8),
//                (byte) H, (byte) (H >> 8), 0});// 页开始，左上角x偏移0，y偏移0，宽4002（576），高4001（320），旋转0
		ComM.get().send(2,bsCodeSize);
//		修改后需要将二维码居中

//		先前的二维码打印
		try {
			byte[] bs = msg.getBytes("GBK");
			byte[] data=new byte[12+bs.length]; //前面11个字节，最后一个截止字节
			data[0]=0x1a;
			data[1]=0x31;

			//“1A 31 00”二维码打印，
			data[3]=0x05; //二维码版本，【0-20】越高越密集
			data[4]=0x02; //纠错等级
			//05 02”固定
			data[5]=0x15;
			data[7]=0x05;
			//“00 00 15 00”打印起始位置，
			data[9]=0x04; //二维码大小，1-7
			//“05”二维码大小，大小范围 01-08
			System.arraycopy(bs,0,data,11,bs.length);
			ComM.get().send(2, data);
			ComM.get().send(2,new byte[]{0x1a, 0x5d, 0x00, 0x1a, 0x4f, 0x00});
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
