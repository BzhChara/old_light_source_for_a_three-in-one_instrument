package com.whswzz.prfluroanalyzer.fluoro.entity;

public class TC {
	private int a,b,cb,cc;//c,ca,
	private double tc;
	public TC() {
		super();
	}
	public TC(int a, int b, int cb, int cc, double tc) {
		super();
		this.a = a;
		this.b = b;
		this.cb = cb;
		this.cc = cc;
		this.tc = tc;
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
	public double getTc() {
		return tc;
	}
	public void setTc(double tc) {
		this.tc = tc;
	}
	
}
