package com.zkzk.pra.model.imp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.entity.Organization;
import com.whswzz.prfluroanalyzer.enzyme.entity.EnzymeData;
import com.whswzz.prfluroanalyzer.fluoro.data.FluDataActivity;
import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;
import com.whswzz.prfluroanalyzer.photometer.entity.PhotometerData;
import com.zkzk.pra.R;
import com.zkzk.pra.db.Database;
import com.zkzk.pra.entity.Data;
import com.zkzk.pra.utils.Tools;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateFormat;
import top.jemen.utils.LogUtil;

/**
 * 由于使用的jar包是针对java开发环境的，android使用时无法混淆，否则易出错,本应用在内部使用，则不再进行混淆。
 * 
 * @author Administrator
 */
public class FileUtil {
	@SuppressWarnings("resource")
	public static boolean writeExcel(String path, List<Data> datas) {
		boolean result = false;
		// 第一步，创建一个workbook对应一个excel文件
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 第二部，在workbook中创建一个sheet对应excel中的sheet
		HSSFSheet sheet = workbook.createSheet("农残检测");
		// 第三部，在sheet表中添加表头第0行，老版本的poi对sheet的行列有限制
		int rowIndex = 0;
		HSSFRow row = sheet.createRow(rowIndex++);
		// 第四步，创建单元格，设置表头
		Resources r = MyApp.getApp().getResources();
		HSSFCell cell = row.createCell(0);
		
		
		
		cell.setCellValue(1);
		
		
		cell.setCellValue("ID");
		
		cell = row.createCell(1);
		cell.setCellValue("样品编号");
		cell = row.createCell(2);
		cell.setCellValue("检测时间");
		cell = row.createCell(3);
		cell.setCellValue("样品项目");
		cell = row.createCell(4);
		cell.setCellValue("样品");
		cell = row.createCell(5);
		cell.setCellValue("通道号");
		cell = row.createCell(6);
		cell.setCellValue("送检单位");
		cell = row.createCell(7);
		cell.setCellValue("吸光度");
		cell = row.createCell(8);
		cell.setCellValue("抑制率");
		cell = row.createCell(9);
		cell.setCellValue("参考限值");
		cell = row.createCell(10);
		cell.setCellValue("检测结果");
		cell = row.createCell(11);
		cell.setCellValue("是否上传");
		cell = row.createCell(12);
		cell.setCellValue(r.getString(R.string.detect_org));
		cell = row.createCell(13);
		cell.setCellValue(r.getString(R.string.operator));

		CellStyle style = workbook.createCellStyle();	//create use in Android,a error uccored,NoClassDefFoundError
		HSSFDataFormat format = workbook.createDataFormat();
		style.setDataFormat(format.getFormat("yyyy年m月d日"));
		//have no choice ,use callendar
		Calendar calendar=Calendar.getInstance(Tools.getTimeZone());
		
		// step5，写入实体数据，实际应用中这些数据从数据库得到,对象封装数据，集合包对象。对象的属性值对应表的每行的值
		for (int i = 0; i < datas.size(); i++) {
			Data d = datas.get(i);
			HSSFRow row1 = sheet.createRow(rowIndex++);
			// 创建单元格设值
			row1.createCell(0).setCellValue(d.getId());
			row1.createCell(1).setCellValue(d.getSn());
//			calendar.setTimeInMillis(d.getTime());
//			row1.createCell(2).setCellValue(""+DateFormat.format("yyyy-MM-dd hh:mm", calendar));
			
			HSSFCell c = row1.createCell(2);
			c.setCellValue(new Date(d.getTime()));
			c.setCellStyle(style);
			
			
			
			row1.createCell(3).setCellValue(d.getProj());
			row1.createCell(4).setCellValue(d.getSpecimen());
			row1.createCell(5).setCellValue(d.getChannel());
			row1.createCell(6).setCellValue(d.getSource().getUnit());
			row1.createCell(7).setCellValue(d.getAbsorbancy());
			float inhibition = d.getInhibitionRatio();
			String sInhibitionRatio;
			if (inhibition > Consts.ALL) {
				sInhibitionRatio = "100%";
			} else {
				sInhibitionRatio = (int) (inhibition * 100) + "%";
			}

			row1.createCell(8).setCellValue(sInhibitionRatio);
			row1.createCell(9).setCellValue(d.getLimit());
			row1.createCell(10).setCellValue(d.getResult());
			row1.createCell(11).setCellValue(d.isUploaded() ? "是" : "否");
			Organization user = d.getUser();
			if(null!=user) {
				row1.createCell(12).setCellValue(user.getName());
				row1.createCell(13).setCellValue(user.getOperator());
			}
		}
		// 将文件保存到指定的位置
		FileOutputStream fos = null;
		try {
			File file = new File(path);
			LogUtil.d("full path=" + file.getAbsolutePath());
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			if (file.getParentFile().getFreeSpace() <500* 1024) { // 可用空间小于100M
				throw new Exception("存储空间不足");
			}
			file.createNewFile();
			fos = new FileOutputStream(file);
			workbook.write(fos);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			if (null != fos) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}
	
	
	public static boolean writeCollaurumExcel(String path, List<FluData> datas) {
		boolean result = false;
		// 第一步，创建一个workbook对应一个excel文件
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 第二部，在workbook中创建一个sheet对应excel中的sheet
		HSSFSheet sheet = workbook.createSheet("农残检测");
		// 第三部，在sheet表中添加表头第0行，老版本的poi对sheet的行列有限制
		int rowIndex = 0;
		HSSFRow row = sheet.createRow(rowIndex++);
		// 第四步，创建单元格，设置表头
		Resources r = MyApp.getApp().getResources();
		HSSFCell cell = row.createCell(0);
		cell.setCellValue("ID");
		cell = row.createCell(1);
		cell.setCellValue("样品编号");
		cell = row.createCell(2);
		cell.setCellValue("检测时间");
		cell = row.createCell(3);
		cell.setCellValue("样品项目");
		cell = row.createCell(4);
		cell.setCellValue("样品");
		cell = row.createCell(5);
		cell.setCellValue("通道号");
		cell = row.createCell(6);
		cell.setCellValue("送检单位");
		
		cell = row.createCell(7);
		cell.setCellValue("T");
		
		cell = row.createCell(8);
		cell.setCellValue("T/C");
		
		cell = row.createCell(9);
		cell.setCellValue("参考限值");
		cell = row.createCell(10);
		cell.setCellValue("检测结果");
		cell = row.createCell(11);
		cell.setCellValue("产地");
		cell = row.createCell(12);
		cell.setCellValue(r.getString(R.string.detect_org));
		cell = row.createCell(13);
		cell.setCellValue(r.getString(R.string.operator));

		CellStyle style = workbook.createCellStyle();	//create use in Android,a error uccored,NoClassDefFoundError
		HSSFDataFormat format = workbook.createDataFormat();
		style.setDataFormat(format.getFormat("yyyy年m月d日"));
		//have no choice ,use callendar
		Calendar calendar=Calendar.getInstance(Tools.getTimeZone());
		
		// step5，写入实体数据，实际应用中这些数据从数据库得到,对象封装数据，集合包对象。对象的属性值对应表的每行的值
		for (int i = 0; i < datas.size(); i++) {
			FluData d = datas.get(i);
			HSSFRow row1 = sheet.createRow(rowIndex++);
			// 创建单元格设值
			row1.createCell(0).setCellValue(d.getId());
			row1.createCell(1).setCellValue(d.getSn());
//			calendar.setTimeInMillis(d.getTime());
//			row1.createCell(2).setCellValue(""+DateFormat.format("yyyy-MM-dd hh:mm", calendar));
			
			HSSFCell c = row1.createCell(2);
			c.setCellValue(new Date(d.getTime()));
			c.setCellStyle(style);
			
			row1.createCell(3).setCellValue(d.getProj());
			row1.createCell(4).setCellValue(d.getSpecimen());
			row1.createCell(5).setCellValue(d.getChannelNum());
			row1.createCell(6).setCellValue(d.getSourceUnit());
			
			row1.createCell(7).setCellValue(d.getT());
			

			row1.createCell(8).setCellValue(d.getT()/d.getC());
			row1.createCell(9).setCellValue(d.getLimit());
			row1.createCell(10).setCellValue(d.getResult());
			row1.createCell(11).setCellValue(d.getSourceAddr());
				row1.createCell(12).setCellValue(d.getUserOrg());
				row1.createCell(13).setCellValue(d.getOperator());
		}
		// 将文件保存到指定的位置
		FileOutputStream fos = null;
		try {
			File file = new File(path);
			LogUtil.d("full path=" + file.getAbsolutePath());
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			if (file.getParentFile().getFreeSpace() < 500  * 1024) { // 可用空间小于100M
				throw new Exception("存储空间不足");
			}
			file.createNewFile();
			fos = new FileOutputStream(file);
			workbook.write(fos);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			if (null != fos) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	
	
	
	
	
	public static boolean writeEnzymeExcel(String path, List<EnzymeData> datas) {
		boolean result = false;
		// 第一步，创建一个workbook对应一个excel文件
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 第二部，在workbook中创建一个sheet对应excel中的sheet
		HSSFSheet sheet = workbook.createSheet("农残检测");
		// 第三部，在sheet表中添加表头第0行，老版本的poi对sheet的行列有限制
		int rowIndex = 0;
		HSSFRow row = sheet.createRow(rowIndex++);
		// 第四步，创建单元格，设置表头
		Resources r = MyApp.getApp().getResources();
		HSSFCell cell = row.createCell(0);
		cell.setCellValue("ID");
		cell = row.createCell(1);
		cell.setCellValue("样品编号");
		cell = row.createCell(2);
		cell.setCellValue("检测时间");
		cell = row.createCell(3);
		cell.setCellValue("样品项目");
		cell = row.createCell(4);
		cell.setCellValue("样品");
		cell = row.createCell(5);
		cell.setCellValue("通道号");
		cell = row.createCell(6);
		cell.setCellValue("来源单位");
		
		cell = row.createCell(7);
		cell.setCellValue("来源单位联系人");
		
		cell = row.createCell(8);
		cell.setCellValue("来源单位联系电话");
		
		cell = row.createCell(9);
		cell.setCellValue("参考限值");
		cell = row.createCell(10);
		cell.setCellValue("检测结果");
		cell = row.createCell(11);
		cell.setCellValue("产地");
		cell = row.createCell(12);
		cell.setCellValue(r.getString(R.string.detect_org));
		cell = row.createCell(13);
		cell.setCellValue(r.getString(R.string.operator));

		CellStyle style = workbook.createCellStyle();	//create use in Android,a error uccored,NoClassDefFoundError
		HSSFDataFormat format = workbook.createDataFormat();
		style.setDataFormat(format.getFormat("yyyy年m月d日"));
		//have no choice ,use callendar
		Calendar calendar=Calendar.getInstance(Tools.getTimeZone());
		
		// step5，写入实体数据，实际应用中这些数据从数据库得到,对象封装数据，集合包对象。对象的属性值对应表的每行的值
		for (int i = 0; i < datas.size(); i++) {
			EnzymeData d = datas.get(i);
			HSSFRow row1 = sheet.createRow(rowIndex++);
			// 创建单元格设值
			row1.createCell(0).setCellValue(d.getId());
			row1.createCell(1).setCellValue(d.getSn());
//			calendar.setTimeInMillis(d.getTime());
//			row1.createCell(2).setCellValue(""+DateFormat.format("yyyy-MM-dd hh:mm", calendar));
			
			HSSFCell c = row1.createCell(2);
			c.setCellValue(new Date(d.getTime()));
			c.setCellStyle(style);
			
			row1.createCell(3).setCellValue(d.getProj());
			row1.createCell(4).setCellValue(d.getSpecimen());
			row1.createCell(5).setCellValue(d.getChannelNum());
			row1.createCell(6).setCellValue(d.getSourceUnit());
			
			row1.createCell(7).setCellValue(d.getSourceContact());
			

			row1.createCell(8).setCellValue(d.getSourcePhone());
			row1.createCell(9).setCellValue(d.getLimit());
			row1.createCell(10).setCellValue(d.getResult());
			row1.createCell(11).setCellValue(d.getSourceAddr());
				row1.createCell(12).setCellValue(d.getUserOrg());
				row1.createCell(13).setCellValue(d.getOperator());
		}
		// 将文件保存到指定的位置
		FileOutputStream fos = null;
		try {
			File file = new File(path);
			LogUtil.d("full path=" + file.getAbsolutePath());
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			if (file.getParentFile().getFreeSpace() <500* 1024) { // 可用空间小于100M
				throw new Exception("存储空间不足");
			}
			file.createNewFile();
			fos = new FileOutputStream(file);
			workbook.write(fos);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			if (null != fos) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	

	public static boolean makeDir(String path) {
		File file = new File(path);
		return file.mkdir();
	}

	public static void markDelete(final Context context) {
		new Thread() {
			public void run() {
				Calendar c = Calendar.getInstance(Tools.getTimeZone());
				String path = context.getFilesDir().getAbsolutePath() + "/delet-all-record.txt";
				FileOutputStream out = null;
				try {
					out = new FileOutputStream(path, true);
					String text = "delete all,data:" + DateFormat.format(Consts.YMDHMS_FORMAT, c) + "\r\n";
					out.write(text.getBytes());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						out.flush();
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	
	public static boolean writePhotometerExcel(String path, List<PhotometerData> datas) {
		boolean result = false;
		// 第一步，创建一个workbook对应一个excel文件
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 第二部，在workbook中创建一个sheet对应excel中的sheet
		HSSFSheet sheet = workbook.createSheet("农残检测");
		// 第三部，在sheet表中添加表头第0行，老版本的poi对sheet的行列有限制
		int rowIndex = 0;
		HSSFRow row = sheet.createRow(rowIndex++);
		// 第四步，创建单元格，设置表头
		Resources r = MyApp.getApp().getResources();
		HSSFCell cell = row.createCell(0);
		cell.setCellValue("ID");
		cell = row.createCell(1);
		cell.setCellValue("样品编号");
		cell = row.createCell(2);
		cell.setCellValue("检测时间");
		cell = row.createCell(3);
		cell.setCellValue("样品项目");
		cell = row.createCell(4);
		cell.setCellValue("样品");
		cell = row.createCell(5);
		cell.setCellValue("通道号");
		cell = row.createCell(6);
		cell.setCellValue("送检单位");
		cell = row.createCell(7);
		cell.setCellValue("样品产地");
		cell = row.createCell(8);
		cell.setCellValue("检测单位");
		cell = row.createCell(9);
		cell.setCellValue("参考限值");
		cell = row.createCell(10);
		cell.setCellValue("检测结果");
		cell = row.createCell(11);
		cell.setCellValue("是否上传");
		cell = row.createCell(12);
		cell.setCellValue(r.getString(R.string.detect_org));
		cell = row.createCell(13);
		cell.setCellValue(r.getString(R.string.operator));

		CellStyle style = workbook.createCellStyle();	//create use in Android,a error uccored,NoClassDefFoundError
		HSSFDataFormat format = workbook.createDataFormat();
		style.setDataFormat(format.getFormat("yyyy年m月d日"));
		//have no choice ,use callendar
		Calendar calendar=Calendar.getInstance(Tools.getTimeZone());
		
		// step5，写入实体数据，实际应用中这些数据从数据库得到,对象封装数据，集合包对象。对象的属性值对应表的每行的值
		for (int i = 0; i < datas.size(); i++) {
			PhotometerData d = datas.get(i);
			HSSFRow row1 = sheet.createRow(rowIndex++);
			// 创建单元格设值
			row1.createCell(0).setCellValue(d.getId());
			row1.createCell(1).setCellValue(d.getSn());
//			calendar.setTimeInMillis(d.getTime());
//			row1.createCell(2).setCellValue(""+DateFormat.format("yyyy-MM-dd hh:mm", calendar));
			
			HSSFCell c = row1.createCell(2);
			c.setCellValue(new Date(d.getTime()));
			c.setCellStyle(style);
			
			
			
			row1.createCell(3).setCellValue(d.getProj());
			row1.createCell(4).setCellValue(d.getSpecimen());
			row1.createCell(5).setCellValue(d.getChannel());
			row1.createCell(6).setCellValue(d.getSourceOrg());
			row1.createCell(7).setCellValue(d.getSourceAddr());

			row1.createCell(8).setCellValue(d.getUserName());
			row1.createCell(9).setCellValue(d.getLimit());
			row1.createCell(10).setCellValue(d.getResult());
			row1.createCell(11).setCellValue(d.isUpLoded() ? "是" : "否");
				row1.createCell(12).setCellValue(d.getUserName());
				row1.createCell(13).setCellValue(d.getOperator());
		}
		// 将文件保存到指定的位置
		FileOutputStream fos = null;
		try {
			File file = new File(path);
			LogUtil.d("full path=" + file.getAbsolutePath());
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			if (file.getParentFile().getFreeSpace() <500 * 1024) { // 可用空间小于100M
				throw new Exception("存储空间不足");
			}
			file.createNewFile();
			fos = new FileOutputStream(file);
			workbook.write(fos);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			if (null != fos) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}
}
