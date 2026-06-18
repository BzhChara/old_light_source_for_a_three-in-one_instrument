package com.whswzz.prfluroanalyzer.entity.ah;

public class ResponseEnterpises
{
    private int code;

    private EnterpriseData data;

    private String message;

    
    
    
    public ResponseEnterpises() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ResponseEnterpises(int code, EnterpriseData data, String message) {
		super();
		this.code = code;
		this.data = data;
		this.message = message;
	}
	public void setCode(int code){
        this.code = code;
    }
    public int getCode(){
        return this.code;
    }
    public void setData(EnterpriseData data){
        this.data = data;
    }
    public EnterpriseData getData(){
        return this.data;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
	@Override
	public String toString() {
		return "Response [code=" + code + ", data=" + data + ", message=" + message + "]";
	}
    
    
}
