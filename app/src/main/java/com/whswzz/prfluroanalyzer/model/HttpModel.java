package com.whswzz.prfluroanalyzer.model;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.entity.IData;
import com.whswzz.prfluroanalyzer.entity.Organization;
import com.whswzz.prfluroanalyzer.entity.Source;
import com.whswzz.prfluroanalyzer.entity.ah.DataAH;
import com.whswzz.prfluroanalyzer.entity.ah.Enterprise;
import com.whswzz.prfluroanalyzer.entity.ah.Product;
import com.whswzz.prfluroanalyzer.entity.ah.ResponceAH;
import com.whswzz.prfluroanalyzer.entity.ah.ResponseEnterpises;
import com.whswzz.prfluroanalyzer.entity.ah.ResponseProducts;
import com.whswzz.prfluroanalyzer.entity.ah.Station;
import com.whswzz.prfluroanalyzer.entity.wh.DataWH;
import com.whswzz.prfluroanalyzer.enzyme.entity.EnzymeData;
import com.whswzz.prfluroanalyzer.entity.Data2Json;
import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;
import com.whswzz.prfluroanalyzer.param.Params;
import com.whswzz.prfluroanalyzer.utils.HttpUtil;
import com.zkzk.pra.entity.Data;
import com.zkzk.pra.utils.Tools;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.text.TextUtils;

import top.jemen.interfaces.ICallback;
import top.jemen.utils.LogUtil;
import top.jemen.utils.threadpool.AsyncProcessor;

public class HttpModel implements IHttpModel {
    private static HttpModel model;

    private HttpModel() {

    }

    public static HttpModel get() {
        if (null == model) {
            synchronized (HttpModel.class) {
                if (null == model) {
                    model = new HttpModel();
                }

            }
        }
        return model;
    }

    public static Gson g = new Gson();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String dateStr = sdf.format(new Date());
    private int x = 0;


	public void sendJson(final Object obj, final ICallback callback) {
		if(null==obj) {
			callback.onFailed("待发送数据为空");
			return;
		}
		String path=Params.UPLOAD_URL;
		if(TextUtils.isEmpty(path)) {
			callback.onFailed("请先设置上传服务器接口");
			return;
		}
		AsyncProcessor.executeTask(new Runnable() {
			@Override
			public void run() {
//				String json = g.toJson(obj);
				String json = Data2Json.to(obj); //武汉的平台需要form格式，不通用
				String result=null==json?"数据异常":HttpUtil.postJson(path,json);
//				LogUtil.d(result);
                result = UicodeBackslashU.unicodeToCn(result);
//                LogUtil.d(result);
                String finalResult = result;
                Consts.handler.post(new Runnable() {
					@Override
					public void run() {
						if (null != finalResult && (finalResult.toLowerCase().contains("success") )) {
							callback.onSuccess("上传成功");
						} else {
							callback.onFailed(finalResult);
						}
					}
				});
			}
		});
	}




    public void send(final Object obj, final ICallback callback) {
        if (null == obj) {
            callback.onFailed("待发送数据为空");
        }
        String version = Tools.getCurrentVersion(MyApp.getApp()).toLowerCase();
        if(obj instanceof List){

            Collection<IData> datas= (Collection<IData>) obj;

            if(version.contains("wh")) {//武汉平台
                send2WH(datas,callback);
                return;
            }

            if(version.endsWith("sak")||version.endsWith("ah")||version.endsWith("hb")){
                if(datas.size()>1) {
                    callback.onFailed("该对接平台现仅支持单条上传");


                }else{
                    sendJson(datas.iterator().next(),callback); //上传条数据
                }
                return;
            }

            if(version.contains("wh")) {
                send2WH(datas,callback);
            }else {
                sendJson(datas, callback);

            }

            
        }else if(obj instanceof IData){
            IData data=(IData)obj;
            if(version.contains("wh")){
                send2WH(data,callback);
            }else{
                sendJson(data,callback);
            }
        }

    }

    /**
     * 上传到安徽的平台
     *
     */
    @SuppressLint("StaticFieldLeak")

    public void getSourceAH(final Organization u) {
        AsyncProcessor.executeTask(new Runnable() {
            @Override
            public void run() {
                String path = "https://service.ahjc.aielab.net/ah-dict-check-station/getEnterpriseByDevice?pageNum=1&pageSize=100";
                Station station = new Station(u.getToken(), u.getCode(), "", "");
                String json = g.toJson(station);
                System.out.println(json);
                String result = HttpUtil.postJson(path, json);
                LogUtil.d(result);
                ResponseEnterpises r = g.fromJson(result, ResponseEnterpises.class);
                if (null != r && r.getCode() != 200) {
                    return;
                }
                List<Enterprise> ls = r.getData().getData();
                if (null == ls || ls.size() == 0) {
                    return;
                }
                List<Source> sources = MyApp.getApp().getSources();
                for (Enterprise e : ls) {
                    int i = 0;
                    for (; i < sources.size(); i++) {
                        if (e.getEnterprisesName() != null && e.getEnterprisesName().equals(sources.get(i))) {
                            sources.get(i).setCode(e.getQydm());
                        }
                    }
                    if (i == sources.size()) {
                        sources.add(new Source(e.getEnterprisesName(), e.getQydm()));
                    }
                }
                MyApp.getApp().saveSources(sources, null);
                path = "https://service.ahjc.aielab.net/ah-dict-check-station/getProductBatchByDevice?pageNum=1&pageSize=10";
                for (Enterprise e : ls) {
                    station = new Station(u.getToken(), u.getCode(), e.getEnterprisesName(), e.getQydm());
                    json = g.toJson(station);
                    System.out.println(json);
                    result = HttpUtil.postJson(path, json);
                    LogUtil.d(result);
                    ResponseProducts rP = g.fromJson(result, ResponseProducts.class);
                    if (null != rP && rP.getCode() != 200) {
                        return;
                    }
                    List<Product> products = rP.getData().getData();
                    if (null == products || products.size() == 0) {
                        return;
                    }
                    Map<String, String> map = MyApp.getApp().getBatchMap();
                    for (Product p : products) {
                        map.put(u.getName() + p.getProductName(), p.getBatchId());
                    }
                    MyApp.getApp().saveBatchMap();
                }

            }
        });
    }


    /************************************对接武汉某平台
     * .其json上传接口有问题，必须使用form-data上传，否则会报错
     * *****************************/
    public void send2WH(final Collection<IData> datas, final ICallback callback) {
        if (null == datas || datas.size() < 1) {
            callback.onFailed("待上传数据位空");
            return;
        }

        List<DataWH> ls = new LinkedList<DataWH>();
        for (IData data : datas) {
            DataWH d = new DataWH();
            d.setRecID(String.valueOf(data.getId()));
            if (data instanceof Data) {
                d.setItemIndex("0");
                Data dpr = (Data) data;
                d.setCalcResult("" + dpr.getInhibitionRatio());
                d.setResult(dpr.getResult());
                d.setAbsorbX(String.valueOf(dpr.getAbsorbancy()));
                d.setUintName("抑制率");
                d.setUint("%");
            } else if (data instanceof EnzymeData) {
                d.setItemIndex("0");
                d.setResult(data.getResult());
                d.setUintName("抑制率");
                d.setUint("%");


            } else {
                if ("甲醛".equals(data.getProj())) {
                    d.setItemIndex("1");
                } else {
                    d.setItemIndex(String.format("%.0f", 10 + Math.random() * 20));
                }
                String result = data.getResult();  //包含了单位和数值
                if (!TextUtils.isEmpty(result)) {
                    int i = 0;
                    for (; i < result.length(); i++) {
                        if (result.charAt(i) > 57) {
                            break;
                        }
                    }
                    if (i > 0 && i < result.length()) {
                        d.setCalcResult(result.substring(0, i));
                        d.setUint(result.substring(i));
                    }
                }
                d.setUintName("浓度");
            }
            d.setItemName(data.getProj());
            d.setSampleName(data.getSpecimen());
            d.setChannelID(data.getChannel());
            d.setSupplier(data.getUserName());
            d.setProducePlace(data.getSourceUnit());
            d.setCheckPerson(data.getOperator());
            d.setTestDate(Consts.SDM.format(data.getTime()));
            d.setTestTime(Consts.STM.format(data.getTime()));
        }
        if (TextUtils.isEmpty(Params.UPLOAD_URL)) {
            callback.onFailed("请先设置上传服务器接口");
            return;
        }
        AsyncProcessor.executeTask(new Runnable() {
            @Override
            public void run() {
                String json = g.toJson(ls);
                String result = HttpUtil.postForm(Params.UPLOAD_URL, "MachineId", Tools.getJemenId(), "SampleNo", datas.iterator().next().getSpecimen(), "Sn", datas.iterator().next().getSn(), "RecordList", json);
                LogUtil.d(result);
                Consts.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != result && result.contains("success")) {

                            callback.onSuccess("上传成功");
                        } else {
                            callback.onFailed(result);
                        }
                    }

                });
            }
        });
    }

    public void send2WH(IData data, final ICallback callback) {
        if (null == data) {
            callback.onFailed("待上传数据位空");
            return;
        }
        List<IData> ls = new LinkedList<IData>();
        ls.add(data);
        send2WH(ls, callback);
    }

    /************************************对接武汉监管平台*****************************/
















}
