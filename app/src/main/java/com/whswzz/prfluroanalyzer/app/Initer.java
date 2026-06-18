package com.whswzz.prfluroanalyzer.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.entity.Organization;
import com.whswzz.prfluroanalyzer.entity.Source;
import com.whswzz.prfluroanalyzer.entity.Species;
import com.whswzz.prfluroanalyzer.param.Params;
import com.whswzz.prfluroanalyzer.photometer.entity.Function;
import com.whswzz.prfluroanalyzer.photometer.entity.PhotometerProj;
import com.zkzk.pra.entity.Project;
import com.zkzk.pra.utils.ExceptionHandler;

import android.content.Context;
import android.os.AsyncTask;
import top.jemen.interfaces.ICallback;
import top.jemen.utils.LogUtil;
import top.jemen.utils.threadpool.AsyncProcessor;

public class Initer {
	public static void initSpecies(final ICallback callback) {
		AsyncProcessor.executeTask(new Runnable() {
			@Override
			public void run() {
				List<Species> lsSpecies = null;
				ObjectInputStream ois = null;
				FileInputStream is = null;
				try {
					is = MyApp.getApp().openFileInput(Consts.SPECIES_FN);
					ois = new ObjectInputStream(is);
					lsSpecies = (List<Species>) ois.readObject();
					is.close();
				} catch (Exception e) {
					ExceptionHandler.handleException(e);
					// createProjs();
				} finally {
					try {
						if (null != ois)
							ois.close();
						if (null != is) {
							is.close();
						}
					} catch (IOException e) {
						ExceptionHandler.handleException(e);
					}
					if (null == lsSpecies) {
						lsSpecies = new LinkedList<Species>();
						lsSpecies.add(new Species("水果", "橙子","苹果","草莓","梨","油桃","桃","毛桃","猕猴桃","柠檬","红柚","杧果","荔枝","柚子","香焦","葡萄","橘子","猕猴桃","枣"));
						lsSpecies.add(new Species("蔬菜","番茄","黄瓜","苦瓜","丝瓜","甜瓜","南瓜","胡萝卜","白萝卜","西葫芦","茄子","芹菜","韭菜","豇豆","四季豆","扁豆","刀豆","荷兰豆","菜豆","食荚豌豆","菠菜","莴苣叶","小白菜","小白菜秧子","大白菜","上海青","生菜","油麦菜","茼蒿","甘蓝","紫甘蓝","结球甘蓝","西兰花","花椰菜","花菜","青花菜","青椒","红椒","大红椒","葱","小葱","大蒜","生姜","小米椒","杭椒","土豆","马铃薯","红薯","洋葱","枸杞","豆芽","包菜","香菇","蒜苔","茶叶（干）/茶青","甜椒","毛豆","苋菜","洋葱","菜薹"));
						
						
						lsSpecies.add(new Species("块根", "红薯", "土豆", "地瓜", "萝卜", "胡萝卜"));
						lsSpecies.add(new Species("调料", "大葱", "小葱", "生姜", "大蒜", "花椒", "辣椒"));
						lsSpecies.add(new Species("叶菜", "小白菜", "毛白菜", "大白菜", "红菜苔", "白菜苔"));
						lsSpecies.add(new Species("瓜类", "冬瓜", "南瓜", "西瓜", "黄瓜", "香瓜"));
						lsSpecies.add(new Species("水产", "鱼","虾"));
						lsSpecies.add(new Species("液体", "水","可乐"));
						LogUtil.d(lsSpecies.toString());
					}else {
						if(!contain(lsSpecies, "水产")) {
							lsSpecies.add(new Species("水产", "鱼","虾"));
						}
						if(!contain(lsSpecies, "液体")) {
							lsSpecies.add(new Species("液体", "水","可乐","橙汁"));
						}
						if(!contain(lsSpecies, "蔬菜")) {
							lsSpecies.add(new Species("蔬菜","番茄","黄瓜","苦瓜","丝瓜","甜瓜","南瓜","胡萝卜","白萝卜","西葫芦","茄子","芹菜","韭菜","豇豆","四季豆","扁豆","刀豆","荷兰豆","菜豆","食荚豌豆","菠菜","莴苣叶","小白菜","小白菜秧子","大白菜","上海青","生菜","油麦菜","茼蒿","甘蓝","紫甘蓝","结球甘蓝","西兰花","花椰菜","花菜","青花菜","青椒","红椒","大红椒","葱","小葱","大蒜","生姜","小米椒","杭椒","土豆","马铃薯","红薯","洋葱","枸杞","豆芽","包菜","香菇","蒜苔","茶叶（干）/茶青","甜椒","毛豆","苋菜","洋葱","菜薹"));
						}
					}
					callback.onSuccess(lsSpecies);
				}

			}

		});

	}
	
	public static boolean contain(List<Species> species,String name) {
		if(null==species||null==name) {
			return false;
		}
		for(Species s:species) {
			if(name.contains(s.getName())) {
				return true;
			}
		}
		return false;
	}
	

	public static void initLimts(final ICallback callback) {
		AsyncProcessor.executeTask(new Runnable() {
			@Override
			public void run() {
				Map<String, Double> map = null;
				ObjectInputStream ois = null;
				FileInputStream is = null;
				try {
					is = MyApp.getApp().openFileInput(Consts.LIMITS_FN);
					ois = new ObjectInputStream(is);
					map = (Map<String, Double>) ois.readObject();
					is.close();
				} catch (Exception e) {
					ExceptionHandler.handleException(e);
				} finally {
					try {
						if (null != ois)
							ois.close();
						if (null != is) {
							is.close();
						}
					} catch (IOException e) {
						ExceptionHandler.handleException(e);
					}
					if (null == map) {
						map = new HashMap<>();
						try {
							parseLimits(map);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						
						map.put("克百威-小白菜", 0.005);
						map.put("灭多威-小白菜", 0.4);
						map.put("三唑磷-小白菜", 0.2);
						map.put("水胺硫磷-小白菜", 0.2);
						map.put("氟虫腈-小白菜", 0.04);
						map.put("甲氰菊酯-小白菜", 10.0);
						map.put("百菌清-小白菜", 2.0);
						map.put("阿维菌素-小白菜", 0.1);
						map.put("多菌灵-小白菜", 0.1);
						map.put("吡唑醚菌酯-小白菜", 0.4);
						map.put("灭蝇胺-小白菜", 0.25);
						map.put("氯虫苯甲酰胺-小白菜", 0.02);
						map.put("克百威-菠菜", 0.005);
						map.put("灭多威-菠菜", 0.4);
						map.put("三唑磷-菠菜", 0.2);
						map.put("水胺硫磷-菠菜", 0.2);
						map.put("氟虫腈-菠菜", 0.04);
						map.put("甲氰菊酯-菠菜", 10.0);
						map.put("百菌清-菠菜", 2.0);
						map.put("阿维菌素-菠菜", 0.1);
						map.put("多菌灵-菠菜", 0.1);
						map.put("吡唑醚菌酯-菠菜", 0.4);
						map.put("灭蝇胺-菠菜", 0.25);
						map.put("氯虫苯甲酰胺-菠菜", 0.02);
						map.put("克百威-大白菜", 0.005);
						map.put("灭多威-大白菜", 0.4);
						map.put("三唑磷-大白菜", 0.2);
						map.put("水胺硫磷-大白菜", 0.2);
						map.put("氟虫腈-大白菜", 0.04);
						map.put("甲氰菊酯-大白菜", 10.0);
						map.put("百菌清-大白菜", 2.0);
						map.put("阿维菌素-大白菜", 0.1);
						map.put("多菌灵-大白菜", 0.1);
						map.put("吡唑醚菌酯-大白菜", 0.4);
						map.put("灭蝇胺-大白菜", 0.25);
						map.put("氯虫苯甲酰胺-大白菜", 0.02);
						map.put("克百威-香蕉", 0.005);
						map.put("灭多威-香蕉", 0.4);
						map.put("三唑磷-香蕉", 0.2);
						map.put("水胺硫磷-香蕉", 0.2);
						map.put("氟虫腈-香蕉", 0.04);
						map.put("甲氰菊酯-香蕉", 10.0);
						map.put("百菌清-香蕉", 2.0);
						map.put("阿维菌素-香蕉", 0.1);
						map.put("多菌灵-香蕉", 0.1);
						map.put("吡唑醚菌酯-香蕉", 0.4);
						map.put("灭蝇胺-香蕉", 0.25);
						map.put("氯虫苯甲酰胺-香蕉", 0.02);
						map.put("克百威-苹果", 0.005);
						map.put("灭多威-苹果", 0.4);
						map.put("三唑磷-苹果", 0.2);
						map.put("水胺硫磷-苹果", 0.2);
						map.put("氟虫腈-苹果", 0.04);
						map.put("甲氰菊酯-苹果", 10.0);
						map.put("百菌清-苹果", 2.0);
						map.put("阿维菌素-苹果", 0.1);
						map.put("多菌灵-苹果", 0.1);
						map.put("吡唑醚菌酯-苹果", 0.4);
						map.put("灭蝇胺-苹果", 0.25);
						map.put("氯虫苯甲酰胺-苹果", 0.02);
						map.put("克百威-梨子", 0.005);
						map.put("灭多威-梨子", 0.4);
						map.put("三唑磷-梨子", 0.2);
						map.put("水胺硫磷-梨子", 0.2);
						map.put("氟虫腈-梨子", 0.04);
						map.put("甲氰菊酯-梨子", 10.0);
						map.put("百菌清-梨子", 2.0);
						map.put("阿维菌素-梨子", 0.1);
						map.put("多菌灵-梨子", 0.1);
						map.put("吡唑醚菌酯-梨子", 0.4);
						map.put("灭蝇胺-梨子", 0.25);
						map.put("氯虫苯甲酰胺-梨子", 0.02);
						map.put("克百威-橘子", 0.005);
						map.put("灭多威-橘子", 0.4);
						map.put("三唑磷-橘子", 0.2);
						map.put("水胺硫磷-橘子", 0.2);
						map.put("氟虫腈-橘子", 0.04);
						map.put("甲氰菊酯-橘子", 10.0);
						map.put("百菌清-橘子", 2.0);
						map.put("阿维菌素-橘子", 0.1);
						map.put("多菌灵-橘子", 0.1);
						map.put("吡唑醚菌酯-橘子", 0.4);
						map.put("灭蝇胺-橘子", 0.25);
						map.put("氯虫苯甲酰胺-橘子", 0.02);
						map.put("克百威-橙子", 0.005);
						map.put("灭多威-橙子", 0.4);
						map.put("三唑磷-橙子", 0.2);
						map.put("水胺硫磷-橙子", 0.2);
						map.put("氟虫腈-橙子", 0.04);
						map.put("甲氰菊酯-橙子", 10.0);
						map.put("百菌清-橙子", 2.0);
						map.put("阿维菌素-橙子", 0.1);
						map.put("多菌灵-橙子", 0.1);
						map.put("吡唑醚菌酯-橙子", 0.4);
						map.put("灭蝇胺-橙子", 0.25);
						map.put("氯虫苯甲酰胺-橙子", 0.02);
						map.put("克百威-油麦菜", 0.005);
						map.put("灭多威-油麦菜", 0.4);
						map.put("三唑磷-油麦菜", 0.2);
						map.put("水胺硫磷-油麦菜", 0.2);
						map.put("氟虫腈-油麦菜", 0.04);
						map.put("甲氰菊酯-油麦菜", 10.0);
						map.put("百菌清-油麦菜", 2.0);
						map.put("阿维菌素-油麦菜", 0.1);
						map.put("多菌灵-油麦菜", 0.05);
						map.put("吡唑醚菌酯-油麦菜", 0.4);
						map.put("灭蝇胺-油麦菜", 0.25);
						map.put("氯虫苯甲酰胺-油麦菜", 0.01);
						map.put("克百威-芹菜", 0.005);
						map.put("灭多威-芹菜", 0.4);
						map.put("三唑磷-芹菜", 0.2);
						map.put("水胺硫磷-芹菜", 0.2);
						map.put("氟虫腈-芹菜", 0.04);
						map.put("甲氰菊酯-芹菜", 10.0);
						map.put("百菌清-芹菜", 2.0);
						map.put("阿维菌素-芹菜", 0.1);
						map.put("多菌灵-芹菜", 0.05);
						map.put("吡唑醚菌酯-芹菜", 0.4);
						map.put("灭蝇胺-芹菜", 0.25);
						map.put("氯虫苯甲酰胺-芹菜", 0.01);
						map.put("克百威-番茄", 0.005);
						map.put("灭多威-番茄", 0.4);
						map.put("三唑磷-番茄", 0.2);
						map.put("水胺硫磷-番茄", 0.2);
						map.put("氟虫腈-番茄", 0.04);
						map.put("甲氰菊酯-番茄", 10.0);
						map.put("百菌清-番茄", 2.0);
						map.put("阿维菌素-番茄", 0.1);
						map.put("多菌灵-番茄", 0.05);
						map.put("吡唑醚菌酯-番茄", 0.4);
						map.put("灭蝇胺-番茄", 0.25);
						map.put("氯虫苯甲酰胺-番茄", 0.01);
						map.put("克百威-黄瓜", 0.005);
						map.put("灭多威-黄瓜", 0.4);
						map.put("三唑磷-黄瓜", 0.2);
						map.put("水胺硫磷-黄瓜", 0.2);
						map.put("氟虫腈-黄瓜", 0.04);
						map.put("甲氰菊酯-黄瓜", 10.0);
						map.put("百菌清-黄瓜", 2.0);
						map.put("阿维菌素-黄瓜", 0.1);
						map.put("多菌灵-黄瓜", 0.05);
						map.put("吡唑醚菌酯-黄瓜", 0.4);
						map.put("灭蝇胺-黄瓜", 0.25);
						map.put("氯虫苯甲酰胺-黄瓜", 0.01);
						map.put("克百威-西葫芦", 0.005);
						map.put("灭多威-西葫芦", 0.4);
						map.put("三唑磷-西葫芦", 0.2);
						map.put("水胺硫磷-西葫芦", 0.2);
						map.put("氟虫腈-西葫芦", 0.04);
						map.put("甲氰菊酯-西葫芦", 10.0);
						map.put("百菌清-西葫芦", 2.0);
						map.put("阿维菌素-西葫芦", 0.1);
						map.put("多菌灵-西葫芦", 0.05);
						map.put("吡唑醚菌酯-西葫芦", 0.4);
						map.put("灭蝇胺-西葫芦", 0.25);
						map.put("氯虫苯甲酰胺-西葫芦", 0.01);
						map.put("克百威-韭菜", 0.005);
						map.put("灭多威-韭菜", 0.2);
						map.put("三唑磷-韭菜", 0.1);
						map.put("水胺硫磷-韭菜", 0.1);
						map.put("氟虫腈-韭菜", 0.02);
						map.put("甲氰菊酯-韭菜", 10.0);
						map.put("百菌清-韭菜", 1.0);
						map.put("阿维菌素-韭菜", 0.05);
						map.put("多菌灵-韭菜", 0.05);
						map.put("吡唑醚菌酯-韭菜", 0.4);
						map.put("灭蝇胺-韭菜", 0.25);
						map.put("氯虫苯甲酰胺-韭菜", 0.01);
						map.put("克百威-青椒", 0.005);
						map.put("灭多威-青椒", 0.2);
						map.put("三唑磷-青椒", 0.1);
						map.put("水胺硫磷-青椒", 0.1);
						map.put("氟虫腈-青椒", 0.02);
						map.put("甲氰菊酯-青椒", 10.0);
						map.put("百菌清-青椒", 1.0);
						map.put("阿维菌素-青椒", 0.05);
						map.put("多菌灵-青椒", 0.05);
						map.put("吡唑醚菌酯-青椒", 0.4);
						map.put("灭蝇胺-青椒", 0.25);
						map.put("氯虫苯甲酰胺-青椒", 0.01);
						map.put("克百威-茄子", 0.005);
						map.put("灭多威-茄子", 0.2);
						map.put("三唑磷-茄子", 0.1);
						map.put("水胺硫磷-茄子", 0.1);
						map.put("氟虫腈-茄子", 0.02);
						map.put("甲氰菊酯-茄子", 10.0);
						map.put("百菌清-茄子", 1.0);
						map.put("阿维菌素-茄子", 0.05);
						map.put("多菌灵-茄子", 0.05);
						map.put("吡唑醚菌酯-茄子", 0.4);
						map.put("灭蝇胺-茄子", 0.25);
						map.put("氯虫苯甲酰胺-茄子", 0.01);
						map.put("克百威-豇豆", 0.005);
						map.put("灭多威-豇豆", 0.2);
						map.put("三唑磷-豇豆", 0.1);
						map.put("水胺硫磷-豇豆", 0.1);
						map.put("氟虫腈-豇豆", 0.02);
						map.put("甲氰菊酯-豇豆", 10.0);
						map.put("百菌清-豇豆", 1.0);
						map.put("阿维菌素-豇豆", 0.05);
						map.put("多菌灵-豇豆", 0.05);
						map.put("吡唑醚菌酯-豇豆", 0.4);
						map.put("灭蝇胺-豇豆", 0.25);
						map.put("氯虫苯甲酰胺-豇豆", 0.01);
						map.put("克百威-胡萝卜", 0.005);
						map.put("灭多威-胡萝卜", 0.2);
						map.put("三唑磷-胡萝卜", 0.1);
						map.put("水胺硫磷-胡萝卜", 0.1);
						map.put("氟虫腈-胡萝卜", 0.02);
						map.put("甲氰菊酯-胡萝卜", 10.0);
						map.put("百菌清-胡萝卜", 1.0);
						map.put("阿维菌素-胡萝卜", 0.05);
						map.put("多菌灵-胡萝卜", 0.05);
						map.put("吡唑醚菌酯-胡萝卜", 0.4);
						map.put("灭蝇胺-胡萝卜", 0.25);
						map.put("氯虫苯甲酰胺-胡萝卜", 0.01);
						map.put("克百威-生姜", 0.005);
						map.put("灭多威-生姜", 0.2);
						map.put("三唑磷-生姜", 0.1);
						map.put("水胺硫磷-生姜", 0.1);
						map.put("氟虫腈-生姜", 0.02);
						map.put("甲氰菊酯-生姜", 10.0);
						map.put("百菌清-生姜", 1.0);
						map.put("阿维菌素-生姜", 0.05);
						map.put("多菌灵-生姜", 0.05);
						map.put("吡唑醚菌酯-生姜", 0.4);
						map.put("灭蝇胺-生姜", 0.25);
						map.put("氯虫苯甲酰胺-生姜", 0.01);

						LogUtil.d(map.toString());
					}
					callback.onSuccess(map);
				}

			}

		});

	}
	public static void initTCLimts(final ICallback callback) {
		AsyncProcessor.executeTask(new Runnable() {
			@Override
			public void run() {
				Map<String, String> map = null;
				ObjectInputStream ois = null;
				FileInputStream is = null;
				try {
					is = MyApp.getApp().openFileInput(Consts.TC_LIMITS_FN);
					ois = new ObjectInputStream(is);
					map = (Map<String, String>) ois.readObject();
					is.close();
				} catch (Exception e) {
					ExceptionHandler.handleException(e);
				} finally {
					try {
						if (null != ois)
							ois.close();
						if (null != is) {
							is.close();
						}
					} catch (IOException e) {
						ExceptionHandler.handleException(e);
					}
					if (null == map) {
						map = new HashMap<>();
						map.put("克百威-小白菜", ">1");

//						LogUtil.d(map.toString());
					}
					callback.onSuccess(map);
				}

			}

		});

	}

	protected static void parseLimits(Map<String, Double> map) throws FileNotFoundException, IOException {
		String fileName="limits.xls";
		
		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		Workbook wb = getWorkbook(MyApp.getApp().getAssets().open(fileName), fileType);
		Sheet sheet = wb.getSheetAt(2);
		
		String proj = null;
		for(int i=1;i<sheet.getPhysicalNumberOfRows();i++) {
			Row row=sheet.getRow(i);
			String  p=row.getCell(1).toString();
			if(!"".equals(p)) {
				proj=p;
			}
			String s=row.getCell(2).toString();
			if(s.contains("(")||s.contains("（")||s.contains("除外")||s.contains("类")) {
				continue;
			}
			String[] kinds=s.split("[/、，]");
			
			double v = row.getCell(3).getNumericCellValue();
			for(String k:kinds) {
				map.put(proj+"-"+k, v);
			}
		}
		LogUtil.d("parse xml,map="+map.toString());
	}
    private static final String XLS = "xls";
    private static final String XLSX = "xlsx";
	public static Workbook getWorkbook(InputStream inputStream, String fileType) throws IOException {
	    Workbook workbook = null;
	    if (fileType.equalsIgnoreCase(XLS)) {
	        workbook = new HSSFWorkbook(inputStream);
	    } else if (fileType.equalsIgnoreCase(XLSX)) {
	        workbook = new XSSFWorkbook(inputStream);
	    }
	    return workbook;
	}

	/**
	 * 初始化样品来源信息
	 * 
	 * @param callback
	 */
	public static void initSources(final ICallback callback) {
		AsyncProcessor.executeTask(new Runnable() {
			@Override
			public void run() {
				List<Source> sources = null;
				ObjectInputStream ois = null;
				FileInputStream is = null;
				try {
					is = MyApp.getApp().openFileInput(Consts.SOURCES_FILE_NAME);
					ois = new ObjectInputStream(is);
					sources = (List<Source>) ois.readObject();
					callback.onSuccess(sources);
				} catch (Exception e) {
					ExceptionHandler.handleException(e);
					if (null != callback) {
						callback.onFailed(sources);
					}
				} finally {
					try {
						if (null != ois)
							ois.close();
						if (null != is) {
							is.close();
						}
					} catch (IOException e) {
						ExceptionHandler.handleException(e);
					}

				}

			}

		});

	}

	/**
	 * 初始化用户单位信息
	 * @param callback
	 */
	public static void initOrganizations(final ICallback callback) {
		AsyncProcessor.executeTask(new Runnable() {
			@Override
			public void run() {
				List<Organization> ls = null;
				ObjectInputStream ois = null;
				FileInputStream is = null;
				try {
					is = MyApp.getApp().openFileInput(Consts.ORGANIZATIONS_FILE_NAME);
					ois = new ObjectInputStream(is);
					ls = (List<Organization>) ois.readObject();
					is.close();
				} catch (Exception e) {
					ExceptionHandler.handleException(e);
				} finally {
					try {
						if (null != ois)
							ois.close();
						if(null!=is) {
							is.close();
						}
					} catch (IOException e) {
						ExceptionHandler.handleException(e);
					}
				}
				if(null==ls) {
					ls=new LinkedList<Organization>();
				
//					ls.add(new Organization("三里庵街道快检站", "武汉上成生物测试厂", "上成生物测试组", 
//							"13388886666", "minghua", "340104001",
//							"$2a$10$WVGpuDcaqJhS.KDzDDi/dedrMi0O5Fi7n/3268rgy0Fzrzg8cu8CS"));
//					
//					ls.add(new Organization("大桥镇快检站", "武汉上成生物测试厂", "上成生物测试组", 
//							"13388886666", "minghua", "341125115",
//							"$2a$10$1/me4l8GIQCQLXYqM0K0jOi085Uv9YOiH50zg5RiXFFxx8fxKXeaa"));
					
				}
					
					callback.onSuccess(ls);
			}
			
		});
		
	}

	/**
	 * 初始化用户单位信息
	 * 
	 * @param callback
	 */
	@SuppressWarnings("unchecked")
	public static void saveOrganizations(final List<Organization> ls, final ICallback callback) {
		new AsyncTask<List<Organization>, Void, String>() {
			@Override
			protected String doInBackground(List<Organization>... params) {
				ObjectOutputStream oo = null;
				String result;
				try {
					oo = new ObjectOutputStream(
							MyApp.getApp().openFileOutput(Consts.ORGANIZATIONS_FILE_NAME, Context.MODE_PRIVATE));
					oo.writeObject(ls);
					oo.flush();
					result = Consts.SUCCESS;
				} catch (IOException e) {
					e.printStackTrace();
					result = ("保存出错");
				} finally {
					if (null != oo) {
						try {
							oo.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return result;
			}

			protected void onPostExecute(String result) {
				if (null == callback) {
					return;
				}
				if (Consts.SUCCESS.equals(result)) {
					callback.onSuccess(result);
				} else {
					callback.onFailed(result);
				}
			};
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ls);
	}

	@SuppressWarnings("unchecked")
	public static void saveSources(final List<Source> ls, final ICallback callback) {
		new AsyncTask<List<Source>, Void, String>() {
			@Override
			protected String doInBackground(List<Source>... params) {
				ObjectOutputStream oo = null;
				String result;
				try {
					oo = new ObjectOutputStream(
							MyApp.getApp().openFileOutput(Consts.SOURCES_FILE_NAME, Context.MODE_PRIVATE));
					oo.writeObject(ls);
					oo.flush();
					result = Consts.SUCCESS;
				} catch (IOException e) {
					e.printStackTrace();
					result = ("保存出错");
				} finally {
					if (null != oo) {
						try {
							oo.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return result;
			}

			protected void onPostExecute(String result) {
				if (null == callback) {
					return;
				}
				if (Consts.SUCCESS.equals(result)) {
					callback.onSuccess(result);
				} else {
					callback.onFailed(result);
				}
			};
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ls);
	}

	@SuppressWarnings("unchecked")
	public static void saveSpecies(final List<Species> ls, final ICallback callback) {
		new AsyncTask<List<Species>, Void, String>() {
			@Override
			protected String doInBackground(List<Species>... params) {
				ObjectOutputStream oo = null;
				String result;
				try {
					oo = new ObjectOutputStream(MyApp.getApp().openFileOutput(Consts.SPECIES_FN, Context.MODE_PRIVATE));
					oo.writeObject(ls);
					oo.flush();
					result = Consts.SUCCESS;
				} catch (IOException e) {
					e.printStackTrace();
					result = ("保存出错");
				} finally {
					if (null != oo) {
						try {
							oo.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return result;
			}

			protected void onPostExecute(String result) {
				if (null == callback) {
					return;
				}
				if (Consts.SUCCESS.equals(result)) {
					callback.onSuccess(result);
				} else {
					callback.onFailed(result);
				}
			};
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ls);
	}

	@SuppressWarnings("unchecked")
	public static void saveLimits(final Map<String, Double> map, final ICallback callback) {
		new AsyncTask<Map<String, Double>, Void, String>() {
			@Override
			protected String doInBackground(Map<String, Double>... params) {
				ObjectOutputStream oo = null;
				String result;
				try {
					oo = new ObjectOutputStream(MyApp.getApp().openFileOutput(Consts.LIMITS_FN, Context.MODE_PRIVATE));
					oo.writeObject(map);
					oo.flush();
					result = Consts.SUCCESS;
				} catch (IOException e) {
					e.printStackTrace();
					result = ("保存出错");
				} finally {
					if (null != oo) {
						try {
							oo.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return result;
			}

			protected void onPostExecute(String result) {
				if (null == callback) {
					return;
				}
				if (Consts.SUCCESS.equals(result)) {
					callback.onSuccess(result);
				} else {
					callback.onFailed(result);
				}
			};
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, map);
	}
	public static void saveTCLimits(final Map<String, String> map, final ICallback callback) {
		new AsyncTask<Map<String, String>, Void, String>() {
			@Override
			protected String doInBackground(Map<String, String>... params) {
				ObjectOutputStream oo = null;
				String result;
				try {
					oo = new ObjectOutputStream(MyApp.getApp().openFileOutput(Consts.TC_LIMITS_FN, Context.MODE_PRIVATE));
					oo.writeObject(map);
					oo.flush();
					result = Consts.SUCCESS;
				} catch (IOException e) {
					e.printStackTrace();
					result = ("保存出错");
				} finally {
					if (null != oo) {
						try {
							oo.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return result;
			}

			protected void onPostExecute(String result) {
				if (null == callback) {
					return;
				}
				if (Consts.SUCCESS.equals(result)) {
					callback.onSuccess(result);
				} else {
					callback.onFailed(result);
				}
			};
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, map);
	}

	public static void initPorjs(final ICallback callback) {
		AsyncProcessor.executeTask(new Runnable() {
			@Override
			public void run() {
				List<Species> projs = null;
				ObjectInputStream ois = null;
				FileInputStream is = null;
				try {
					is = MyApp.getApp().openFileInput(Consts.PROJS_FN);
					ois = new ObjectInputStream(is);
					projs = (List<Species>) ois.readObject();
					is.close();
				} catch (Exception e) {
					ExceptionHandler.handleException(e);
					// createProjs();
				} finally {
					try {
						if (null != ois)
							ois.close();
						if (null != is) {
							is.close();
						}
					} catch (IOException e) {
						ExceptionHandler.handleException(e);
					}
					if (null == projs) {
						projs = new LinkedList<Species>();
//						projs.add(new Species("有机磷类", "乐果", "马拉硫磷", "甲拌磷", "内吸磷", "对硫磷", "保棉丰", "氧化乐果，甲基对硫磷", "二甲硫吸磷",
//								"敌敌畏", "亚胺磷敌百虫", "乐果", "氯硫磷", "乙基稻丰散"));
//						projs.add(new Species("氨基甲酸酯类", "涕灭威", "克百威", "丁硫克百威", "丙硫克百威", "异丙威", "仲丁威", "混灭威", "速灭威",
//								"抗蚜威", "茚虫威", "灭多威", "硫双威", "甲萘威", "苯氧威", "残杀威", "噁虫威"
						projs.add(new Species("农残", "乐果", "马拉硫磷", "甲拌磷", "内吸磷", "对硫磷", "保棉丰", "氧化乐果","甲基对硫磷", "二甲硫吸磷",
								"敌敌畏", "亚胺磷敌百虫","氯硫磷", "乙基稻丰散", "涕灭威", "丁硫克百威", "丙硫克百威", "异丙威", "仲丁威", "混灭威", "速灭威",
								"抗蚜威", "茚虫威", "灭多威", "硫双威", "甲萘威", "苯氧威", "残杀威", "噁虫威",
								"阿维菌素",
								"克百威",
								"多菌灵",
								"三唑磷",
								"吡虫啉",
								"噻虫胺",
								"啶虫脒",
								"噻虫嗪",
								"百菌清",
								"氟虫腈",
								"水胺硫磷",
								"4-氯苯氧乙酸钠(4-D)",
								"6-苄基腺嘌呤(6-BA)"
						));
						projs.add(new Species("兽残", "氟苯尼考","恩诺沙星","呋喃唑酮代谢物","呋喃它酮代谢物",
								"呋喃西林代谢物","呋喃妥因代谢物","氟虫腈","氯霉素","金刚烷胺","盐酸克伦特罗","莱克多巴胺",
								"沙丁胺醇","喹诺酮类","","四环素类","","磺胺类","","地塞米松","","地西泮","","五氯酚酸钠","硝基咪唑类",
								"尼卡巴嗪代谢物","地塞米松","头孢噻呋","大环内酯类","林可胺类","喹乙醇标志物",
								"孔雀石绿","","喹乙醇","","伊维菌素","氧氟沙星"));


						LogUtil.d(projs.toString());
					}
					callback.onSuccess(projs);
				}

			}

		});

	}

	@SuppressWarnings("unchecked")
	public static void saveProjs(final List<Species> ls, final ICallback callback) {
		new AsyncTask<List<Species>, Void, String>() {
			@Override
			protected String doInBackground(List<Species>... params) {
				ObjectOutputStream oo = null;
				String result;
				try {
					oo = new ObjectOutputStream(MyApp.getApp().openFileOutput(Consts.PROJS_FN, Context.MODE_PRIVATE));
					oo.writeObject(ls);
					oo.flush();
					result = Consts.SUCCESS;
				} catch (IOException e) {
					e.printStackTrace();
					result = ("保存出错");
				} finally {
					if (null != oo) {
						try {
							oo.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return result;
			}

			protected void onPostExecute(String result) {
				if (null == callback) {
					return;
				}
				if (Consts.SUCCESS.equals(result)) {
					callback.onSuccess(result);
				} else {
					callback.onFailed(result);
				}
			};
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ls);
	}

	public static float[][] initBorders() {
		float[][] borders = null;
		ObjectInputStream ois = null;
		FileInputStream is = null;
		try {
			is = MyApp.getApp().openFileInput(Consts.BORDERS_FN);
			ois = new ObjectInputStream(is);
			borders = (float[][]) ois.readObject();
			is.close();
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
			// createProjs();
		} finally {
			try {
				if (null != ois)
					ois.close();
				if (null != is) {
					is.close();
				}
			} catch (IOException e) {
				ExceptionHandler.handleException(e);
			}

		}
		if (null == borders) {
			borders = new float[][] { { 0.2879f, 0.4f, 0.083f, 0.59f, 0.023f }, // 六联卡、十二联卡
					{ 0.39f, 0.39f, 0.104f, 0.60f, 0.023f }, // 三联卡
					{ 0.49f, 0.40f, 0f, 0.60f, 0.023f }, // 单卡
					{ 0.2879f, 0.4f, 0.083f, 0.59f, 0.023f } // 十二联卡与六联卡目前相同。
			};
		}
		return borders;
	}

	public static void saveBorders(final float[][] newBorders, final ICallback callback) {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				ObjectOutputStream oo = null;
				String result;
				try {
					oo = new ObjectOutputStream(MyApp.getApp().openFileOutput(Consts.BORDERS_FN, Context.MODE_PRIVATE));
					oo.writeObject(newBorders);
					oo.flush();
					result = Consts.SUCCESS;
				} catch (IOException e) {
					e.printStackTrace();
					result = ("保存出错");
				} finally {
					if (null != oo) {
						try {
							oo.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return result;
			}

			protected void onPostExecute(String result) {
				if (null == callback) {
					return;
				}
				if (Consts.SUCCESS.equals(result)) {
					callback.onSuccess(result);
				} else {
					callback.onFailed(result);
				}
			};
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public static void savePhotometerProj(final List<PhotometerProj> projs, final ICallback callback) {
		AsyncProcessor.executeTask(new Runnable() {
			@Override
			public void run() {
				 boolean r=false;
				ObjectOutputStream oo = null;
				try {
					oo = new ObjectOutputStream(MyApp.getApp().openFileOutput(Consts.PHOTOMETER_FN, Context.MODE_PRIVATE));
					oo.writeObject(projs);
					oo.flush();
					r=true;
				} catch (IOException e) {
					e.printStackTrace();
					r=false;
				} finally {
					if (null != oo) {
						try {
							oo.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					if(null==callback) {
						return;
					}
					final boolean fr=r;
					Consts.handler.post(new Runnable() {
						@Override
						public void run() {
							if(fr) {
								callback.onSuccess(Consts.SUCCESS);
							}else {
								callback.onFailed("保存失败");
							}
						}
					});
				}
			}
		});
	}
	
	
	public static void initPhotometerProj(final ICallback callback) {
			AsyncProcessor.executeTask(new Runnable() {
				@Override
				public void run() {
					List<PhotometerProj> projs=null;
					ObjectInputStream ois = null;
					FileInputStream is = null;
					try {
						is = MyApp.getApp().openFileInput(Consts.PHOTOMETER_FN);
						ois = new ObjectInputStream(is);
						projs = (List<PhotometerProj>) ois.readObject();
						is.close();
					} catch (Exception e) {
						ExceptionHandler.handleException(e);
					} finally {
						try {
							if (null != ois)
								ois.close();
							if (null != is) {
								is.close();
							}
						} catch (IOException e) {
							ExceptionHandler.handleException(e);
						}
						if (null == projs) {
							projs=new LinkedList<PhotometerProj>();
							projs.add(new PhotometerProj("吊白块"));

							projs.add(new PhotometerProj("硝酸盐"));
							projs.add(new PhotometerProj("亚硝酸盐"));
							projs.add(new PhotometerProj("硼砂"));


							PhotometerProj projx = new PhotometerProj("甲醛");
							projx.addFunction("虾", new Function(new double[][] {
								{0.145,	0.149,	0.147,	0.17,	0.236,	0.305,	0.533,	0.924}, //吸光度
								{0,	0.025	,0.05,	0.1	,0.25,	0.5,	1,	2}}, "mg/kg")); //浓度

							projx.addFunction("水", new Function(new double[][] {
								{0.015,	0.101,	0.197,	0.371,	0.55,	0.74,	0.921},
								{0,	0.2,	0.4,	0.8,	1.2,	1.6	,2}}, "mg/kg"));
							projs.add(projx);
							projs.add(new PhotometerProj("铅离子").addFunction("",new Function(new double[][]{
									{0.015,0.024,0.056,0.122,0.0376,0.484},//吸光度
									{0.75,1,2.5,5,15,20} //浓度
							},"mg/kg")));
							projs.add(new PhotometerProj("六价铬").addFunction("",new Function(new double[][]{
									{0.017,0.033,0.071,0.149,0.375,0.75,1.389},//吸光度
									{0.2,0.5,1,2,5,10,20} //浓度
							},"mg/kg")));
							projs.add(new PhotometerProj("重金属镉").addFunction("",new Function(new double[][]{
									{0.034,0.07,0.21,0.415,0.687},//吸光度
									{0.2,0.4,1,2,4} //浓度
							},"mg/kg")));
							projs.add(new PhotometerProj("重金属汞").addFunction("",new Function(new double[][]{
									{0.015,0.024,0.056,0.122,0.376,0.484},//吸光度
									{0.75,1,2.5,5,15,20} //浓度
							},"mg/kg")));


							projs.add(new PhotometerProj("二氧化硫"));
							projs.add(new PhotometerProj("双氧水"));
							projs.add(new PhotometerProj("面中铝"));
							projs.add(new PhotometerProj("山梨酸钾"));
							projs.add(new PhotometerProj("工业火碱"));

							addFunction(projs,"吊白块","",0.0189,0.0301);
							addFunction(projs,"亚硝酸盐","",0.1615,0.0077);

							addFunction(projs,"食用油过氧化值","",0.0210	,0.0094);
							addFunction(projs,"蛋白质","",8.4183	,0.0394);
							addFunction(projs,"甲醛","",0.0189		,0.0301);
							addFunction(projs,"二氧化硫","",0.0189		,0.0301);
							addFunction(projs,"组胺","",0.0132		,0.0003);
							addFunction(projs,"双氧水","",0.0021		,0.0072);
							addFunction(projs,"硼砂","",0.0199		,0.0076);




							addFunctionRv(projs, "双氧水", "水", 0.0122, -0.0007);
							addFunctionRv(projs, "铝", "水", -0.2727, 0.3899);
							addFunctionRv(projs, "亚硝酸盐", "水", 0.4903,0.0123);
							addFunctionRv(projs, "甲醛", "水", 0.4532, 0.0121);
							addFunctionRv(projs, "硝酸盐", "水",0.0004, 0.0037);
							addFunctionRv(projs, "二氧化硫", "水",0.0023, 0.0037);

							projs.add(new PhotometerProj("挥发性盐基氮"));
							projs.add(new PhotometerProj("农药残留"));
							
						}
						
						LogUtil.d("非法添加项目"+projs.toString());




						callback.onSuccess(projs);
					}

				}

			});

	}
	
	/**
	 * 给非法添加内置标曲，正向插入，第一行吸光度值，第二行浓度值
	 * @param projs
	 * @param proj
	 * @param type
	 * @param k
	 * @param b
	 */
	public static void addFunction(List<PhotometerProj> projs,String proj,String type,double k,double b) {
		if(null==projs||null==proj||null==type) {
			return;
		}
		PhotometerProj target = null;
		for(PhotometerProj p:projs) {
			if(p.getName().equals(proj)) {
				target=p;
				break;
			}
		}
		if(null==target){
			target=new PhotometerProj(proj);
			projs.add(target);
		}

		target.addFunction(type, new Function(new double[][] {
				{0,10},
				{b,10*k+b} //吸光度
				}, "mg/kg")); //浓度
	}
	/**
	 * 给非法添加内置标曲，输入用浓度计算吸光度的k、b
	 * @param projs
	 * @param proj
	 * @param type
	 * @param k
	 * @param b
	 */
	public static void addFunctionRv(List<PhotometerProj> projs,String proj,String type,double k,double b) {
		if(null==projs||null==proj||null==type) {
			return;
		}
		PhotometerProj target = null;
		for(PhotometerProj p:projs) {
			if(p.getName().equals(proj)) {
				target=p;
				break;
			}
		}
		if(null==target){
			target=new PhotometerProj(proj);
			projs.add(target);
		}

		target.addFunction(type, new Function(new double[][] {
				{b,1000*k+b}, //吸光度
				{0,1000}}, "mg/kg")); //浓度
	}

	
	
	
	public static void saveBatchMap(final Map<String, String> batchMap) {
		AsyncProcessor.executeTask(new Runnable() {
			@Override
			public void run() {
				ObjectOutputStream oo = null;
				String result;
				try {
					oo = new ObjectOutputStream(MyApp.getApp().openFileOutput(Consts.BATCHMAP_FN, Context.MODE_PRIVATE));
					oo.writeObject(batchMap);
					oo.flush();
					result = Consts.SUCCESS;
				} catch (IOException e) {
					e.printStackTrace();
					result = ("保存出错");
				} finally {
					if (null != oo) {
						try {
							oo.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
	}
	
	public static void initBatchMap(final ICallback callback) {
			AsyncProcessor.executeTask(new Runnable() {
				@Override
				public void run() {
					HashMap<String,String> batchMap = null;
					ObjectInputStream ois = null;
					FileInputStream is = null;
					try {
						is = MyApp.getApp().openFileInput(Consts.BATCHMAP_FN);
						ois = new ObjectInputStream(is);
						batchMap = (HashMap<String,String>) ois.readObject();
						is.close();
					} catch (Exception e) {
						ExceptionHandler.handleException(e);
						// createProjs();
					} finally {
						try {
							if (null != ois)
								ois.close();
							if (null != is) {
								is.close();
							}
						} catch (IOException e) {
							ExceptionHandler.handleException(e);
						}
						if (null == batchMap) {
							batchMap = new HashMap<String,String>();
							batchMap.put("农户----张一帆大豆", "1219163903");
							batchMap.put("农户----张一帆黄大豆(籽粒)", "1106225606");
							LogUtil.d(batchMap.toString());
						}
						callback.onSuccess(batchMap);
					}

				}

			});

	}


	public static void initGBMap(final ICallback callback) {
		AsyncProcessor.executeTask(new Runnable() {
			@Override
			public void run() {
				Map<String, String> map = null;
				ObjectInputStream ois = null;
				FileInputStream is = null;
				try {
					is = MyApp.getApp().openFileInput(Consts.GB_MAP_FN);
					ois = new ObjectInputStream(is);
					map = (Map<String, String>) ois.readObject();
					is.close();
				} catch (Exception e) {
					ExceptionHandler.handleException(e);
				} finally {
					try {
						if (null != ois)
							ois.close();
						if (null != is) {
							is.close();
						}
					} catch (IOException e) {
						ExceptionHandler.handleException(e);
					}
					if (null == map) {
						map = new HashMap<>();
						map.put("农残", Params.GB);
//						LogUtil.d(map.toString());
					}
					callback.onSuccess(map);
				}

			}

		});

	}

	public static void saveGBMap(final Map<String, String> map, final ICallback callback) {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				ObjectOutputStream oo = null;
				String result;
				try {
					oo = new ObjectOutputStream(MyApp.getApp().openFileOutput(Consts.GB_MAP_FN, Context.MODE_PRIVATE));
					oo.writeObject(map);
					oo.flush();
					result = Consts.SUCCESS;
				} catch (IOException e) {
					e.printStackTrace();
					result = ("保存出错");
				} finally {
					if (null != oo) {
						try {
							oo.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return result;
			}

			protected void onPostExecute(String result) {
				if (null == callback) {
					return;
				}
				if (Consts.SUCCESS.equals(result)) {
					callback.onSuccess(result);
				} else {
					callback.onFailed(result);
				}
			};
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


	}
	
}
