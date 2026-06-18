package com.zkzk.pra.entity;

public class User {
	private String username;
	private String password;
	private String workOrg;
	private String operator;
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public User(String username, String password, String workOrg, String operator) {
		super();
		this.username = username;
		this.password = password;
		this.workOrg = workOrg;
		this.operator = operator;
	}

	public User(String username) {
		this.username=username;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getWorkOrg() {
		return workOrg;
	}
	public void setWorkOrg(String workOrg) {
		this.workOrg = workOrg;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
}
