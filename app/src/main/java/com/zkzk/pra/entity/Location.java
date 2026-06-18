package com.zkzk.pra.entity;

import java.io.Serializable;

/**
 * 一个用于封装自己需要的字段的实体类。
 * @author Jemen   
 *如果存数据库的话可能以单个字段更好一些，后期可以实现 数据库的按检测位置查询功能。
 */
public class Location {
	/**
	 * 
	 */
	double latitude=30.48542;
	double longitude=114.276582 ;
	String describe="武汉市农科院环安所";
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public Location(double latitude, double longitude, String describe) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.describe = describe;
	}
	public Location() {
		super();
	}
	@Override
	public String toString() {
		return "Location [latitude=" + latitude + ", longitude=" + longitude + ", describe=" + describe + "]";
	}
	
}
