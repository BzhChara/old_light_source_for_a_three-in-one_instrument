package com.zkzk.pra.entity;

import java.io.Serializable;
import java.util.TreeMap;

public class Project implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isCommon;
	private String proj;//测试项目
	private float limit;
	private float contrast;	//对照值  ,检测多重物质的时候这个就必须用了。
	private String method;//检测方法

	/**
	 * 
	 * @param isCommon
	 * @param detectProj
	 * @param method
	 * @param limit
	 * @param contrast 对照值，检测多重物质的时候这个就必须用了。
	 */
	public Project(boolean isCommon, String detectProj,  String method,float limit,float contrast) {
		super();
		this.isCommon = isCommon;
		this.proj = detectProj;
		this.setLimit(limit);
		this.setContrast(contrast);
		this.method = method;
	}


	public Project() {
		super();
		// TODO Auto-generated constructor stub
	}


	public boolean isCommon() {
		return isCommon;
	}


	public void setCommon(boolean isCommon) {
		this.isCommon = isCommon;
	}


	public String getProj() {
		return proj;
	}


	public void setProj(String detectProj) {
		this.proj = detectProj;
	}




	public String getMethod() {
		return method;
	}


	public void setMethod(String method) {
		this.method = method;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public float getLimit() {
		return limit;
	}


	public void setLimit(float limit) {
		this.limit = limit;
	}


	public float getContrast() {
		return contrast;
	}


	public void setContrast(float contrast) {
		this.contrast = contrast;
	}


	@Override
	public String toString() {
		return "Project [isCommon=" + isCommon + ", proj=" + proj + ", limit=" + limit + ", contrast=" + contrast
				+ ", method=" + method + "]";
	}

	
	
	
}
