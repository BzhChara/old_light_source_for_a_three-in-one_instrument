package com.whswzz.prfluroanalyzer.entity;

import java.io.Serializable;

public interface IData extends Serializable{
	/**
	 * 获取样品类型
	 * 
	 * @return
	 */
	public String getSpecimen();
	String getSourceAddr();
	String getSourcePhone();
	String getResult();
	public String getUserName();
	public long getTime();
	public String getUserPhone();
	public String getProj();
	public String getOperator();
	public String getSourceUnit();
	public String getSn();
	public String getUserCode();
	public String getUserToken();
	public String getSourceCode();
	public int getSourceOrgType();
	public String getChannel();
	public int getId();

	 String getUserContact();
}
