package com.whswzz.prfluroanalyzer.entity.ah;

public class ResponseProducts
{
    private int code;

    private ProductData data;

    private String message;

    public void setCode(int code){
        this.code = code;
    }
    public int getCode(){
        return this.code;
    }
    public void setData(ProductData data){
        this.data = data;
    }
    public ProductData getData(){
        return this.data;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
}
