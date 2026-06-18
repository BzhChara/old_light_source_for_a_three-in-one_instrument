package com.whswzz.prfluroanalyzer.entity;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.entity.IData;
import com.whswzz.prfluroanalyzer.entity.hb.DataHB;
import com.whswzz.prfluroanalyzer.entity.jx.DataJX;
import com.whswzz.prfluroanalyzer.entity.sak.DataSAK;
import com.whswzz.prfluroanalyzer.enzyme.entity.EnzymeData;
import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;
import com.whswzz.prfluroanalyzer.param.Params;
import com.whswzz.prfluroanalyzer.photometer.entity.PhotometerData;
import com.zkzk.pra.entity.Data;
import com.zkzk.pra.entity.Location;
import com.zkzk.pra.utils.Tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import top.jemen.utils.LogUtil;

/**
 * 进行json转换，不同平台的对接问题，数据类型也在这里转换。
 */
public class Data2Json {
    public static Gson g = new Gson();

    public static String to(Object obj) {
        if (null == obj) {
            return null;
        }



        String version = Tools.getCurrentVersion(MyApp.getApp()).toLowerCase();
        IData  data = (IData) obj;
        if (version.toUpperCase().contains("SAK")) { //也仅仅支持单条数据上传
//            LogUtil.d("data=" + data.toString());
            if (null == data.getResult() ) {
                return null;
            }
            LogUtil.d("d=" + data);
            DataSAK d = new DataSAK();
            d.setData_id(UUID.nameUUIDFromBytes(("" + Tools.getJemenId() + data.getTime()).getBytes()).toString());//检测数据唯一码，建议uuid
            d.setJgbh(data.getUserCode());  //机构编号      一个检测室对应一个机构编号
            d.setJgmc(data.getUserName());//机构名称
            d.setJcsj(Consts.SDFM.format(data.getTime()));
            d.setJcbh(data.getSn());//检测编号,说是尽量保证同机构同一天编号唯一
            d.setJcpc(data.getSourceCode());//检测批次码,暂且用样品编码
            d.setBjdw(data.getSourceUnit()); //被检单位，摊位或者档口。
//            d.setSccj(data.getSourceUnit()); //生产厂家
            d.setSccj(data.getSourceAddr());
            d.setJcxm(data.getProj());//检测项目
            d.setYpfl(data.getSpecimen());//样品分类
            d.setYpmc(data.getSpecimen());//样品名称
            if (data instanceof FluData) { //胶体金数据
                FluData da = (FluData) data;
                d.setJcz(String.format("%.2f", da.getT() / da.getC())); //检测值
//                String xz=MyApp.getApp().getTCLimits().get(data.getProj()+"-"+data.getSpecimen());    //限值  那边又要求显示是xxmg/kg
//                if(TextUtils.isEmpty(xz)){
//                    xz="<0.9";//默认是比色法
//                }
//                d.setXz(xz); //限值  那边又要求显示是xxmg/kg
            } else{// if(data instanceof PhotometerData) { //检测值不允许为空
//                PhotometerData phd = (PhotometerData) data;
//                phd.get/**/
                d.setJcz(data.getResult()); //检测值

            }


            Map<String, Double> limits = MyApp.getApp().getLimits();
            String key = data.getProj() + "-" + data.getSpecimen();
            Double v = limits.get(key); //limits初始化时候确保了不为空。
            if (null != v) {
                d.setXz(String.format("%.3f", v) + " mg/kg");
            } else {
                d.setXz("xxxmg/kg");
            }
            if(data instanceof FluData){
                if (data.getResult().contains("阴性")) {
                    d.setJcjg("合格");
                } else {
                    d.setJcjg("不合格");
                }
            }else{
                d.setJcjg(data.getResult());
            }

            d.setJcrbh(data.getUserPhone()); //检测人编号
            d.setJcr(data.getOperator());
//            d.setSbbh(Tools.getJemenId()); //设备编号
//            d.setSbbh("9942B01"); //食安康要求手工输入设备编号，哎
            d.setSbbh(data.getUserContact()); //设备编号


            d.setJcsb("全功能型食品安全检测仪");
            //一些备用字段就算了，采样时间不知道，

            d.setShr(data.getSourceUnit()); //售货人

            d.setShrlxdh(data.getSourcePhone()); //售货人联系电话

            d.generateSign(data.getUserToken()); //计算签名值，必须签名才可用。


//            LogUtil.d("sign_value:"+d.getSign_value());
            LogUtil.d("d=" + d);
            return g.toJson(d);


        } else if (version.toLowerCase().contains("ah")) { //只支持单条上传   安徽平台
            if (!(obj instanceof IData)) {
                return "error";
            }

            com.whswzz.prfluroanalyzer.entity.ah.DataAH d = new com.whswzz.prfluroanalyzer.entity.ah.DataAH();
            d.setJczmc(data.getUserName() + "");
            d.setJczbh(data.getUserCode());
            d.setJczToken(data.getUserToken());
            d.setEnterprisesName("" + data.getSourceUnit());
            d.setQydm(data.getSourceCode());
            Map<String, String> map = MyApp.getApp().getBatchMap();
            if (null != map) {
                String id = map.get(data.getSourceUnit() + data.getSpecimen());
                if (null != id) {
                    LogUtil.d("batchid=" + id);
                    d.setProductBatchId(id);
                } else {
                    LogUtil.e("id is null");
                }
            } else {
                LogUtil.e("batchid is null");
            }
            d.setYbmc(data.getSpecimen());
            d.setJcxm(data.getProj());

            d.setRwbh(data.getSn());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(new Date());
            d.setJcTime(dateStr);

            d.setJcz(String.format("%.4f", Math.random() * 0.01));
            d.setJczUnit("mg/kg");
            d.setJcjg("阳性".equals(data.getResult()) ? 1 : 0);

            if (data instanceof FluData) {
                d.setJcbz(Params.GB);
            } else {
                d.setJcbz(Params.GB);
            }
            d.setEquipmentId("SC-DGN2401");
            d.setType(data.getSourceOrgType());

            System.out.println(d.toString());

            String json = g.toJson(d);
            return json;
        }else if(version.toLowerCase().contains("jx")){ //对接江西平台，也以JSON方式
            DataJX d=new DataJX();
            d.setProjectname(data.getProj());
            d.setSampletype(data.getSpecimen());
            d.setTestnum(data.getSn());
            d.setDevice_code(MyApp.getApp().getJemenId());
            if(data instanceof EnzymeData){
                d.setTest_type("1"); //酶抑制率法
            }else if(data instanceof FluData){
                d.setTest_type("2"); //胶体金法

            }else if(data instanceof Data){
                d.setTest_type("1");//酶抑制率法
                Data d1= (Data) data;
                d.setDetection_result(d1.getResult());
                d.setValue(String.valueOf(d1.getInhibitionRatio()));

            }else if(data instanceof PhotometerData){ //主要测非法添加的
                d.setTest_type("11");//分光光度法
            }
             SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddkkmmss");
            d.setSample_date(sdf.format(data.getTime()));

            Location loc = MyApp.getApp().getLocation();
            d.setCheckaddress(loc.getDescribe());
            d.setLongitude(String.valueOf(loc.getLongitude()));
            d.setLatitude(String.valueOf(loc.getLatitude()));
            d.setSample_code(data.getSourceCode());
            d.setDetection_people(data.getOperator());
            String json = g.toJson(d);
            return json;
        }else if(version.contains("hb")){ //河北 仅支持单条数据，不支持json数组
//            LogUtil.d("data=" + data.toString());
            obj=new DataHB((IData) obj);
        }
        else if (version.contains("wh")) { //武汉平台还不支持json上传，使用x-www-form-urlencoded上传数据


        }

        return g.toJson(obj);
    }

    SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddkkmmss");
}
