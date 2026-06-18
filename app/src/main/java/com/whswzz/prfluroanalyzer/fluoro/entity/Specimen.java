package com.whswzz.prfluroanalyzer.fluoro.entity;

import java.io.Serializable;

public class Specimen implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;//样品名称
	private double gain=1;//样品增益;
	private ICalculator calculator;  //计算方程
	private String tcShift;//tc值转换 null,log2,log10
	private String vShift;//浓度值转换 null,log2,log10;
	private String vType;//测量值 T1,T1/C,T2,T2/C,
	private String limit;//参考限值
	
	public Specimen(String name,ICalculator calculator) {
		this.name=name;
		this.calculator=calculator;
	}
	public Specimen(String name, double gain, ICalculator calculator, String tcShift, String vShift, String vType) {
		super();
		this.name = name;
		this.gain = gain;
		this.calculator = calculator;
		this.tcShift = tcShift;
		this.vShift = vShift;
		this.vType = vType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getGain() {
		return gain;
	}
	public void setGain(double gain) {
		this.gain = gain;
	}
	public ICalculator getCalculator() {
		return calculator;
	}
	public void setCalculator(ICalculator calculator) {
		this.calculator = calculator;
	}
	public String getTcShift() {
		return tcShift;
	}
	public void setTcShift(String tcShift) {
		this.tcShift = tcShift;
	}
	public String getvShift() {
		return vShift;
	}
	public void setvShift(String vShift) {
		this.vShift = vShift;
	}
	public String getvType() {
		return vType;
	}
	public void setvType(String vType) {
		this.vType = vType;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public String toString() {
		return "Specimen [name=" + name + ", gain=" + gain + ", calculator=" + calculator + ", tcShift=" + tcShift
				+ ", vShift=" + vShift + ", vType=" + vType + "]";
	}
	public String getLimit() {
		return limit;
	}
	public void setLimit(String limit) {
		this.limit = limit;
	}
	
	
	
	

}
