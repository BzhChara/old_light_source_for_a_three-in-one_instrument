package com.whswzz.prfluroanalyzer.entity;

import java.io.Serializable;

public class Organization implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String addr; 
	private String contact;
	private String phone;
	private String operator;//检测单位的检测员，或者送检单位的送件员
	private String code; //组织机构代码，或者检测站编码
	private String token; //应对检测站要求的token
	


	public Organization() {
		super();
	}
	
	
	public Organization(String name, String addr, String contact, String phone, String operator) {
		super();
		this.name = name;
		this.addr = addr;
		this.contact = contact;
		this.phone = phone;
		this.operator = operator;
	}

	
	

	public Organization(String name, String addr, String contact, String phone, String operator, String code,
			String token) {
		super();
		this.name = name;
		this.addr = addr;
		this.contact = contact;
		this.phone = phone;
		this.operator = operator;
		this.code = code;
		this.token = token;
	}


	public Organization(String name) {
		this.name=name;
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	@Override
	public String toString() {
		return  name + "  " + addr + " " + contact + "  " + phone
				+ "  " + operator + "  " + code;
	}


	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	}

	
	
	
	
	
}
