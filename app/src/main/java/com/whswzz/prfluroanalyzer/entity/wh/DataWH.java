package com.whswzz.prfluroanalyzer.entity.wh;

public class DataWH {
    private String RecID;    //检测记录编号，非必须


    private String ItemIndex;    //项目编号，必须，0农残，1甲醛

    private String ItemName;	//项目名称，必须，农药残留

    private String ChannelID;	//通道号，非必须

    private String SampleName;	//样品名称，必须，茶叶

    private String AbsorbX;		//吸光度，String 保留3为小数，非必须

    private String CalcResult;	//计算结果数值，String,浓度或抑制率数值，不包含单位。

    private String TestLimit;	//检测限值，String,<50,非必须

    private String CheckResult;		//检测结果返回值,String,1 合格，0不合格;

    private String TxtResult;		//检测结果，String ,合格/不合格	必须

    private String UintName;		//检测结果单位，String,  农残为“抑制率”，非农残为浓度，非必须

    private String Uint;		//计算结果的数值单位，String,农残为%,非农残为具体单位，如mg/L，非必须

    private String Supplier;	//检测单位，String, 为空时自动填充“检测单位未设置"	,必须

    private String ProducePlace;	//被检单位，String 	非必须

    private String UploadFlag;	// 上传标志,说明文档上面并无，用来在数据库中作标记吧。

    private String CheckPerson;	//检测员，String	,必须

    private String CodeId="String GB 2763-2014";	//适用国标，String GB 2763-2014,非必须

    private String TestTime;	//检测时间，String, 11:20:00.000	，必须

    private String TestDate;	//检测日期,String, 2016-03-04	，必须

    private String NcItemName;	//备用字段，String ,非必须

    private String NcValue;		//备用字段

    private String Temperature;		//温度，String,非必须

    private String Humidity;	//湿度，String ,非必须

    private String GPS;		//GPS经纬度，String	，非必须

    private String Result;		//备用字段，String, 非必须

    private String TypeName;	//备用中低端

    private String DTI_ID;	//任务号，String, 非必须

    public void setRecID(String RecID) {
        this.RecID = RecID;
    }

    public String getRecID() {
        return this.RecID;
    }

    public void setItemIndex(String ItemIndex) {
        this.ItemIndex = ItemIndex;
    }

    public String getItemIndex() {
        return this.ItemIndex;
    }


    public void setItemName(String ItemName) {
        this.ItemName = ItemName;
    }

    public String getItemName() {
        return this.ItemName;
    }

    public void setChannelID(String ChannelID) {
        this.ChannelID = ChannelID;
    }

    public String getChannelID() {
        return this.ChannelID;
    }

    public void setSampleName(String SampleName) {
        this.SampleName = SampleName;
    }

    public String getSampleName() {
        return this.SampleName;
    }

    public void setAbsorbX(String AbsorbX) {
        this.AbsorbX = AbsorbX;
    }

    public String getAbsorbX() {
        return this.AbsorbX;
    }

    public void setCalcResult(String CalcResult) {
        this.CalcResult = CalcResult;
    }

    public String getCalcResult() {
        return this.CalcResult;
    }

    public void setTestLimit(String TestLimit) {
        this.TestLimit = TestLimit;
    }

    public String getTestLimit() {
        return this.TestLimit;
    }

    public void setCheckResult(String CheckResult) {
        this.CheckResult = CheckResult;
    }

    public String getCheckResult() {
        return this.CheckResult;
    }

    public void setTxtResult(String TxtResult) {
        this.TxtResult = TxtResult;
    }

    public String getTxtResult() {
        return this.TxtResult;
    }

    public void setUintName(String UintName) {
        this.UintName = UintName;
    }

    public String getUintName() {
        return this.UintName;
    }

    public void setUint(String Uint) {
        this.Uint = Uint;
    }

    public String getUint() {
        return this.Uint;
    }

    public void setSupplier(String Supplier) {
        this.Supplier = Supplier;
    }

    public String getSupplier() {
        return this.Supplier;
    }

    public void setProducePlace(String ProducePlace) {
        this.ProducePlace = ProducePlace;
    }

    public String getProducePlace() {
        return this.ProducePlace;
    }

    public void setUploadFlag(String UploadFlag) {
        this.UploadFlag = UploadFlag;
    }

    public String getUploadFlag() {
        return this.UploadFlag;
    }

    public void setCheckPerson(String CheckPerson) {
        this.CheckPerson = CheckPerson;
    }

    public String getCheckPerson() {
        return this.CheckPerson;
    }

    public void setCodeId(String CodeId) {
        this.CodeId = CodeId;
    }

    public String getCodeId() {
        return this.CodeId;
    }

    public void setTestTime(String TestTime) {
        this.TestTime = TestTime;
    }

    public String getTestTime() {
        return this.TestTime;
    }

    public void setTestDate(String TestDate) {
        this.TestDate = TestDate;
    }

    public String getTestDate() {
        return this.TestDate;
    }

    public void setNcItemName(String NcItemName) {
        this.NcItemName = NcItemName;
    }

    public String getNcItemName() {
        return this.NcItemName;
    }

    public void setNcValue(String NcValue) {
        this.NcValue = NcValue;
    }

    public String getNcValue() {
        return this.NcValue;
    }

    public void setTemperature(String Temperature) {
        this.Temperature = Temperature;
    }

    public String getTemperature() {
        return this.Temperature;
    }

    public void setHumidity(String Humidity) {
        this.Humidity = Humidity;
    }

    public String getHumidity() {
        return this.Humidity;
    }

    public void setGPS(String GPS) {
        this.GPS = GPS;
    }

    public String getGPS() {
        return this.GPS;
    }

    public void setResult(String Result) {
        this.Result = Result;
    }

    public String getResult() {
        return this.Result;
    }

    public void setTypeName(String TypeName) {
        this.TypeName = TypeName;
    }

    public String getTypeName() {
        return this.TypeName;
    }

    public void setDTI_ID(String DTI_ID) {
        this.DTI_ID = DTI_ID;
    }

    public String getDTI_ID() {
        return this.DTI_ID;
    }
}
