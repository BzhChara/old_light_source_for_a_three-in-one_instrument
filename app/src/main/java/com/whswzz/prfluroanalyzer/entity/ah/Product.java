package com.whswzz.prfluroanalyzer.entity.ah;

public class Product
{
    private String filingDate;

    private String batchId;

    private String productName;

    public void setFilingDate(String filingDate){
        this.filingDate = filingDate;
    }
    public String getFilingDate(){
        return this.filingDate;
    }
    public void setBatchId(String batchId){
        this.batchId = batchId;
    }
    public String getBatchId(){
        return this.batchId;
    }
    public void setProductName(String productName){
        this.productName = productName;
    }
    public String getProductName(){
        return this.productName;
    }
}