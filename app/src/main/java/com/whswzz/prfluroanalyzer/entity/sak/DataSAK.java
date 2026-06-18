package com.whswzz.prfluroanalyzer.entity.sak;

import com.whswzz.prfluroanalyzer.param.Params;

public class DataSAK {
    private String sign_value;

    private String api_version="2.0";

    private String data_id;

    private String jgbh; //机构编号

    private String jcpc;

    private String jgmc;

    private String jcsj;

    private String jcbh;

    private String bjdw;

    private String sccj;

    private String jcxm;

    private String ypfl;

    private String ypmc;

    private String jcz;

    private String xz;

    private String jcjg;

    private String jcrbh;

    private String jcr;

    private String sbbh;

    private String jcsb="多参数农产品快速检测仪SC-DGN2401";//检测设备

    private String bz;

    private String by1;

    private String by2;

    private String by3;

    private String by4;

    private String by5;

    private String cysj;

    private String cyry;

    private String shr;

    private String shrlxdh;


    public DataSAK() {
    }

    public void generateSign(String key){
        if (null == key) {
            key= Params.SAK_KEY;
        }
        String content = ""+jgbh+sbbh+jcbh+jcsj+jcxm+jcz;
        content = content.replaceAll("null", ""); // 去除空值
        this.sign_value= HMAC.hmacsha1(content,key);
    }


    public void setSign_value(String sign_value){
        this.sign_value = sign_value;
    }
    public String getSign_value(){
        return this.sign_value;
    }
    public void setApi_version(String api_version){
        this.api_version = api_version;
    }
    public String getApi_version(){
        return this.api_version;
    }
    public void setData_id(String data_id){
        this.data_id = data_id;
    }
    public String getData_id(){
        return this.data_id;
    }
    public void setJgbh(String jgbh){
        this.jgbh = jgbh;
    }
    public String getJgbh(){
        return this.jgbh;
    }
    public void setJcpc(String jcpc){
        this.jcpc = jcpc;
    }
    public String getJcpc(){
        return this.jcpc;
    }
    public void setJgmc(String jgmc){
        this.jgmc = jgmc;
    }
    public String getJgmc(){
        return this.jgmc;
    }
    public void setJcsj(String jcsj){
        this.jcsj = jcsj;
    }
    public String getJcsj(){
        return this.jcsj;
    }
    public void setJcbh(String jcbh){
        this.jcbh = jcbh;
    }
    public String getJcbh(){
        return this.jcbh;
    }
    public void setBjdw(String bjdw){
        this.bjdw = bjdw;
    }
    public String getBjdw(){
        return this.bjdw;
    }
    public void setSccj(String sccj){
        this.sccj = sccj;
    }
    public String getSccj(){
        return this.sccj;
    }
    public void setJcxm(String jcxm){
        this.jcxm = jcxm;
    }
    public String getJcxm(){
        return this.jcxm;
    }
    public void setYpfl(String ypfl){
        this.ypfl = ypfl;
    }
    public String getYpfl(){
        return this.ypfl;
    }
    public void setYpmc(String ypmc){
        this.ypmc = ypmc;
    }
    public String getYpmc(){
        return this.ypmc;
    }
    public void setJcz(String jcz){
        this.jcz = jcz;
    }
    public String getJcz(){
        return this.jcz;
    }
    public void setXz(String xz){
        this.xz = xz;
    }
    public String getXz(){
        return this.xz;
    }
    public void setJcjg(String jcjg){
        this.jcjg = jcjg;
    }
    public String getJcjg(){
        return this.jcjg;
    }
    public void setJcrbh(String jcrbh){
        this.jcrbh = jcrbh;
    }
    public String getJcrbh(){
        return this.jcrbh;
    }
    public void setJcr(String jcr){
        this.jcr = jcr;
    }
    public String getJcr(){
        return this.jcr;
    }
    public void setSbbh(String sbbh){
        this.sbbh = sbbh;
    }
    public String getSbbh(){
        return this.sbbh;
    }
    public void setJcsb(String jcsb){
        this.jcsb = jcsb;
    }
    public String getJcsb(){
        return this.jcsb;
    }
    public void setBz(String bz){
        this.bz = bz;
    }
    public String getBz(){
        return this.bz;
    }
    public void setBy1(String by1){
        this.by1 = by1;
    }
    public String getBy1(){
        return this.by1;
    }
    public void setBy2(String by2){
        this.by2 = by2;
    }
    public String getBy2(){
        return this.by2;
    }
    public void setBy3(String by3){
        this.by3 = by3;
    }
    public String getBy3(){
        return this.by3;
    }
    public void setBy4(String by4){
        this.by4 = by4;
    }
    public String getBy4(){
        return this.by4;
    }
    public void setBy5(String by5){
        this.by5 = by5;
    }
    public String getBy5(){
        return this.by5;
    }
    public void setCysj(String cysj){
        this.cysj = cysj;
    }
    public String getCysj(){
        return this.cysj;
    }
    public void setCyry(String cyry){
        this.cyry = cyry;
    }
    public String getCyry(){
        return this.cyry;
    }
    public void setShr(String shr){
        this.shr = shr;
    }
    public String getShr(){
        return this.shr;
    }
    public void setShrlxdh(String shrlxdh){
        this.shrlxdh = shrlxdh;
    }
    public String getShrlxdh(){
        return this.shrlxdh;
    }


    @Override
    public String toString() {
        return "DataSAK{" +
                "sign_value='" + sign_value + '\'' +
                ", api_version='" + api_version + '\'' +
                ", data_id='" + data_id + '\'' +
                ", jgbh='" + jgbh + '\'' +
                ", jcpc='" + jcpc + '\'' +
                ", jgmc='" + jgmc + '\'' +
                ", jcsj='" + jcsj + '\'' +
                ", jcbh='" + jcbh + '\'' +
                ", bjdw='" + bjdw + '\'' +
                ", sccj='" + sccj + '\'' +
                ", jcxm='" + jcxm + '\'' +
                ", ypfl='" + ypfl + '\'' +
                ", ypmc='" + ypmc + '\'' +
                ", jcz='" + jcz + '\'' +
                ", xz='" + xz + '\'' +
                ", jcjg='" + jcjg + '\'' +
                ", jcrbh='" + jcrbh + '\'' +
                ", jcr='" + jcr + '\'' +
                ", sbbh='" + sbbh + '\'' +
                ", jcsb='" + jcsb + '\'' +
                ", bz='" + bz + '\'' +
                ", by1='" + by1 + '\'' +
                ", by2='" + by2 + '\'' +
                ", by3='" + by3 + '\'' +
                ", by4='" + by4 + '\'' +
                ", by5='" + by5 + '\'' +
                ", cysj='" + cysj + '\'' +
                ", cyry='" + cyry + '\'' +
                ", shr='" + shr + '\'' +
                ", shrlxdh='" + shrlxdh + '\'' +
                '}';
    }
}
