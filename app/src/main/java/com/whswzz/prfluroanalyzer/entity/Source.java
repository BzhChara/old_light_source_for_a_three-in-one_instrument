package com.whswzz.prfluroanalyzer.entity;

import java.io.Serializable;

import com.whswzz.prfluroanalyzer.parameSet.IConcat;

/**
 * 标识样品来源
 * @author Administrator
 *
 */
public class Source implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String unit;
	private String addr;
	private String contact;
	private String phone;
	private String code;//用以保存企业代码
	private int type=1;////检测类型（1 代表种植业，2 代表畜禽业，3 代表水产业）
	
	public Source() {
		super();
	}
	public Source(String unit, String addr, String contact, String phone) {
		super();
		this.unit = unit;
		this.addr = addr;
		this.contact = contact;
		this.phone = phone;
	}
	
	
	
	
	
	public Source(String unit, String addr, String contact, String phone, String code, int type) {
		super();
		this.unit = unit;
		this.addr = addr;
		this.contact = contact;
		this.phone = phone;
		this.code = code;
		this.type = type;
	}
	
	
	
	public Source(String unit) {
		this.unit=unit;
	}
	public Source(String unit,String code) {
		this.unit=unit;
		this.code=code;
	}
	
	
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String toString() {
		return unit+" "+addr+" "+contact+" "+phone;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
}
