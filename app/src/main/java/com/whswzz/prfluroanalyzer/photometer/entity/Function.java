package com.whswzz.prfluroanalyzer.photometer.entity;

import java.io.Serializable;
import java.util.Arrays;

public class Function implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double[][] params; //第一行存吸光度值，第二行存浓度值。
	private String unit;
	
	public Function() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Function(double[][] params, String unit) {
		super();
		this.params = params;
		this.unit = unit;
	}


    public double[][] getParams() {
		return params;
	}
	public void setParams(double[][] params) {
		this.params = params;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	
	@Override
	public String toString() {
		return "Function [params=" + Arrays.toString(params) + ", unit=" + unit + "]";
	}
	
	
	
	
}
