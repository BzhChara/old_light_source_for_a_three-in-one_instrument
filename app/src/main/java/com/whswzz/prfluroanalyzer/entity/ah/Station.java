package com.whswzz.prfluroanalyzer.entity.ah;

public class Station
{
    private String checkStationToken;

    private String checkStationBh;

    private String enterprisesName="";

    private String qydm="";

    
    
    
    public Station(String checkStationToken, String checkStationBh, String enterprisesName, String qydm) {
		super();
		this.checkStationToken = checkStationToken;
		this.checkStationBh = checkStationBh;
		this.enterprisesName = enterprisesName;
		this.qydm = qydm;
	}
	public Station() {
		super();
		// TODO Auto-generated constructor stub
	}
	public void setCheckStationToken(String checkStationToken){
        this.checkStationToken = checkStationToken;
    }
    public String getCheckStationToken(){
        return this.checkStationToken;
    }
    public void setCheckStationBh(String checkStationBh){
        this.checkStationBh = checkStationBh;
    }
    public String getCheckStationBh(){
        return this.checkStationBh;
    }
    public void setEnterprisesName(String enterprisesName){
        this.enterprisesName = enterprisesName;
    }
    public String getEnterprisesName(){
        return this.enterprisesName;
    }
    public void setQydm(String qydm){
        this.qydm = qydm;
    }
    public String getQydm(){
        return this.qydm;
    }
	@Override
	public String toString() {
		return "Station [checkStationToken=" + checkStationToken + ", checkStationBh=" + checkStationBh
				+ ", enterprisesName=" + enterprisesName + ", qydm=" + qydm + "]";
	}
    
    
}
