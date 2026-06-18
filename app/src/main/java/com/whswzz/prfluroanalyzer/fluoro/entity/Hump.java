package com.whswzz.prfluroanalyzer.fluoro.entity;

import java.io.Serializable;

public class Hump implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int a, b, c,ca, cb, cc;
	public Hump() {
		super();
	}
	
	

	public int getA() {
		return a;
	}



	public void setA(int a) {
		this.a = a;
	}



	public int getB() {
		return b;
	}



	public void setB(int b) {
		this.b = b;
	}



	public int getC() {
		return c;
	}



	public void setC(int c) {
		this.c = c;
	}



	public int getCa() {
		return ca;
	}



	public void setCa(int ca) {
		this.ca = ca;
	}



	public int getCb() {
		return cb;
	}



	public void setCb(int cb) {
		this.cb = cb;
	}



	public int getCc() {
		return cc;
	}



	public void setCc(int cc) {
		this.cc = cc;
	}



	public Hump(int a, int b, int c, int ca, int cb, int cc) {
		super();
		this.a = a;
		this.b = b;
		this.c = c;
		this.ca = ca;
		this.cb = cb;
		this.cc = cc;
	}
	


	@Override
	public String toString() {
		return "Peak [a=" + a + ", b=" + b + ", c=" + c + ", ca=" + ca + ", cb=" + cb + ", cc=" + cc + "]";
	}



	
}
