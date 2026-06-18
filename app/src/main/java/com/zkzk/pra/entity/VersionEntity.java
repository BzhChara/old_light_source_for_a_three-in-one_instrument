package com.zkzk.pra.entity;

import java.io.Serializable;

public class VersionEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String status;

	private String msg;

	private String version;

	private String changeLog;

	private String apkUrl;
	private int versionCode;

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return this.version;
	}

	public void setChangeLog(String changeLog) {
		this.changeLog = changeLog;
	}

	public String getChangeLog() {
		return this.changeLog;
	}

	public void setApkUrl(String apkUrl) {
		this.apkUrl = apkUrl;
	}

	public String getApkUrl() {
		return this.apkUrl;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

}
