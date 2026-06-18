package com.whswzz.prfluroanalyzer.entity.hb;


import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Printer;

import com.whswzz.prfluroanalyzer.entity.IData;
import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;
import com.whswzz.prfluroanalyzer.photometer.entity.PhotometerData;
import com.whswzz.prfluroanalyzer.utils.PrinterJPW;
import com.zkzk.pra.utils.Tools;

/**对接河北平台的实体类       http://jg.hebny.cn:9089/checkRecord/api/uploads/checkRecord
 * @author swzz
 * @date 2021-09-02 16:22:35
 */
public class DataHB {

    private String companyName;

    private String checkItem;

    private String checkMethod;

    private String checkSample;

    private String checkValue;

    private String checkResult;

    private String checkPlace;

    private String checkPerson;

    private String sampleNumber;

    private String equipUnique;

    private String onlyCode;

    private String token;

    private String checkTime;
    private String uscc;
    private final String phone;

    public DataHB(IData data) {
        this.companyName = data.getSourceUnit();
        this.checkItem=data.getProj();
        this.checkSample=data.getSpecimen();
        if(data instanceof FluData){
            FluData fd = (FluData) data;
            this.checkValue= String.valueOf(fd.getT()/fd.getC());
        }else if(data instanceof PhotometerData){
            PhotometerData pd = (PhotometerData) data;
            this.checkValue= String.valueOf(pd.getResult());
        }

        this.checkResult=data.getResult();
        this.checkPlace=data.getUserName();
        this.checkPerson=data.getOperator();
        this.sampleNumber=data.getSn();
        this.equipUnique= Tools.getJemenId();
//        this.token= Params.getToken();
        this.token=data.getUserToken();

        this.checkTime= DateFormat.format("yyyy-MM-dd KK:mm:ss", data.getTime()).toString();
//        this.onlyCode=String.format("%010d",(long)(Math.random()*9999999999l));
        this.onlyCode="";
        this.uscc= TextUtils.isEmpty(data.getUserCode())?null:data.getUserCode();
        this.phone=data.getUserPhone();
        this.checkMethod= PrinterJPW.getGB(data.getProj());

    }

    public void setCompanyName(String companyName){
        this.companyName = companyName;
    }
    public String getCompanyName(){
        return this.companyName;
    }
    public void setCheckItem(String checkItem){
        this.checkItem = checkItem;
    }
    public String getCheckItem(){
        return this.checkItem;
    }
    public void setCheckMethod(String checkMethod){
        this.checkMethod = checkMethod;
    }
    public String getCheckMethod(){
        return this.checkMethod;
    }
    public void setCheckSample(String checkSample){
        this.checkSample = checkSample;
    }
    public String getCheckSample(){
        return this.checkSample;
    }
    public void setCheckValue(String checkValue){
        this.checkValue = checkValue;
    }
    public String getCheckValue(){
        return this.checkValue;
    }
    public void setCheckResult(String checkResult){
        this.checkResult = checkResult;
    }
    public String getCheckResult(){
        return this.checkResult;
    }
    public void setCheckPlace(String checkPlace){
        this.checkPlace = checkPlace;
    }
    public String getCheckPlace(){
        return this.checkPlace;
    }
    public void setCheckPerson(String checkPerson){
        this.checkPerson = checkPerson;
    }
    public String getCheckPerson(){
        return this.checkPerson;
    }
    public void setSampleNumber(String sampleNumber){
        this.sampleNumber = sampleNumber;
    }
    public String getSampleNumber(){
        return this.sampleNumber;
    }
    public void setEquipUnique(String equipUnique){
        this.equipUnique = equipUnique;
    }
    public String getEquipUnique(){
        return this.equipUnique;
    }
    public void setOnlyCode(String onlyCode){
        this.onlyCode = onlyCode;
    }
    public String getOnlyCode(){
        return this.onlyCode;
    }
    public void setToken(String token){
        this.token = token;
    }
    public String getToken(){
        return this.token;
    }
    public void setCheckTime(String checkTime){
        this.checkTime = checkTime;
    }
    public String getCheckTime(){
        return this.checkTime;
    }

    public String getUscc() {
        return uscc;
    }

    public void setUscc(String uscc) {
        this.uscc = uscc;
    }

    @Override
    public String toString() {
        return "DataHB{" +
                "companyName='" + companyName + '\'' +
                ", checkItem='" + checkItem + '\'' +
                ", checkMethod='" + checkMethod + '\'' +
                ", checkSample='" + checkSample + '\'' +
                ", checkValue='" + checkValue + '\'' +
                ", checkResult='" + checkResult + '\'' +
                ", checkPlace='" + checkPlace + '\'' +
                ", checkPerson='" + checkPerson + '\'' +
                ", sampleNumber='" + sampleNumber + '\'' +
                ", equipUnique='" + equipUnique + '\'' +
//                ", onlyCode='" + onlyCode + '\'' +
                ", onlyCode='" + "" + '\'' +
                ", token='" + token + '\'' +
                ", checkTime='" + checkTime + '\'' +
                '}';
    }

    public String getPhone() {
        return phone;
    }
}
