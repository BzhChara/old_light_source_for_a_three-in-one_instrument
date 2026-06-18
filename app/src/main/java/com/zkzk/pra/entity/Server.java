package com.zkzk.pra.entity;
/**
 * 服务端交互返回的信息
 * @author Administrator
 *
 */
public class Server {
	private String protocalVersion;
	private int code;
	private String message;
	
	
	public Server(String protocalVersion, int code, String message) {
		super();
		this.protocalVersion = protocalVersion;
		this.code = code;
		this.message = message;
	}
	public Server() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getProtocalVersion() {
		return protocalVersion;
	}
	public void setProtocalVersion(String protocalVersion) {
		this.protocalVersion = protocalVersion;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "Server [protocalVersion=" + protocalVersion + ", code=" + code + ", message=" + message + "]";
	}
	
	
	
}
