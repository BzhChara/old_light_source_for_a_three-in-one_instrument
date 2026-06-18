package com.whswzz.prfluroanalyzer.entity.jx;

/**
 * 第一版对接的江西的平台
 */

public class DataJX {

    private String projectname; //检测项目
    private String sample_code; //样品编号
    private String sample_date; //采样日期
    private String detection_people; //检测员
    private String sampletype;  //样品类型
    private String longitude;   //经度数据
    private String latitude;    //维度
    private String checkaddress;    //检测地址
    private String testnum; //检测编号
    private String test_type;   ////快检类型，采用数字编码。1 : 酶抑制率法 2 : 胶体金方法
    private String detection_result; //检测结果判定，1阴性，0阳性
    private String value;   //检测结果值
    private String device_code;     //检测设备编号

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setSample_code(String sample_code) {
        this.sample_code = sample_code;
    }

    public String getSample_code() {
        return sample_code;
    }

    public void setSample_date(String sample_date) {
        this.sample_date = sample_date;
    }

    public String getSample_date() {
        return sample_date;
    }

    public void setDetection_people(String detection_people) {
        this.detection_people = detection_people;
    }

    public String getDetection_people() {
        return detection_people;
    }

    public void setSampletype(String sampletype) {
        this.sampletype = sampletype;
    }

    public String getSampletype() {
        return sampletype;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setCheckaddress(String checkaddress) {
        this.checkaddress = checkaddress;
    }

    public String getCheckaddress() {
        return checkaddress;
    }

    public void setTestnum(String testnum) {
        this.testnum = testnum;
    }

    public String getTestnum() {
        return testnum;
    }

    public void setTest_type(String test_type) {
        this.test_type = test_type;
    }

    public String getTest_type() {
        return test_type;
    }

    public void setDetection_result(String detection_result) {
        this.detection_result = detection_result;
    }

    public String getDetection_result() {
        return detection_result;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setDevice_code(String device_code) {
        this.device_code = device_code;
    }

    public String getDevice_code() {
        return device_code;
    }

}
