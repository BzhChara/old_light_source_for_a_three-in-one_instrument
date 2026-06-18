package com.zkzk.pra.entity;
/**
 * 检测数据实体类
 * @author Jemen Chen
 *1.ID号：序号
2.样品编号：SN序列号
3.检测时间：
4.样品名称：大米，水，土壤
5.检测项目：铅和镉，有机磷农残
6.检测限值：
7.检测结果：
8.送检单位：
9.检测单位：
10.检测人员：
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShowData implements Serializable{
	private int id;
	private String channelNum;//通道号
	private String sn;//样品编号
	private String proj;//检测项目，初期仅有农残
	private String specimen;//样品名称
	private String targetUnit;//被检单位
	private float absorbancy;//吸光度
	private float inhibitionRatio;//抑制率
	private String limit;//限值
	private String result;//检测结果
	private Long time;//检测时间
	private boolean uploaded;	//有没有上传
	private String WorkOrg;	//检测单位
	private String operator;//操作人员
	private Location location;
	
	private boolean checked;







	public ShowData() {
		super();
	}






	public ShowData(int id, String channelNum, String sn, String proj, String specimen, String targetUnit,
			float absorbancy, float inhibitionRatio, String limit, String result, Long time, boolean uploaded,
			String workOrg, String operator, Location location, boolean checked) {
		super();
		this.id = id;
		this.channelNum = channelNum;
		this.sn = sn;
		this.proj = proj;
		this.specimen = specimen;
		this.targetUnit = targetUnit;
		this.absorbancy = absorbancy;
		this.inhibitionRatio = inhibitionRatio;
		this.limit = limit;
		this.result = result;
		this.time = time;
		this.uploaded = uploaded;
		WorkOrg = workOrg;
		this.operator = operator;
		this.location = location;
		this.checked = checked;
	}









	public String getChannelNum() {
		return channelNum;
	}
	public void setChannelNum(String channelNum) {
		this.channelNum = channelNum;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getSpecimen() {
		return specimen;
	}
	public void setSpecimen(String specimen) {
		this.specimen = specimen;
	}
	public String getTargetUnit() {
		return targetUnit;
	}
	public void setTargetUnit(String targetUnit) {
		this.targetUnit = targetUnit;
	}
	public float getAbsorbancy() {
		return absorbancy;
	}
	public void setAbsorbancy(float absorbancy) {
		this.absorbancy = absorbancy;
	}
	public float getInhibitionRatio() {
		return inhibitionRatio;
	}
	public void setInhibitionRatio(float inhibitionRatio) {
		this.inhibitionRatio = inhibitionRatio;
	}
	public String getLimit() {
		return limit;
	}
	public void setLimit(String limit) {
		this.limit = limit;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

	
	public Long getTime() {
		return time;
	}
	
	
	
	public void setTime(Long time) {
		this.time = time;
	}




	public boolean isUploaded() {
		return uploaded;
	}



	public void setUploaded(boolean uploaded) {
		this.uploaded = uploaded;
	}



	public String getProj() {
		return proj;
	}



	public void setProj(String proj) {
		this.proj = proj;
	}



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}





	public String getWorkOrg() {
		return WorkOrg;
	}





	public void setWorkOrg(String workOrg) {
		WorkOrg = workOrg;
	}






	public String getOperator() {
		return operator;
	}


	public void setOperator(String operator) {
		this.operator = operator;
	}



	public Location getLocation() {
		return location;
	}



	public void setLocation(Location location) {
		this.location = location;
	}


	@Override
	public String toString() {
		return "Data [id=" + id + ", channelNum=" + channelNum + ", sn=" + sn + ", proj=" + proj + ", specimen="
				+ specimen + ", targetUnit=" + targetUnit + ", absorbancy=" + absorbancy + ", inhibitionRatio="
				+ inhibitionRatio + ", limit=" + limit + ", result=" + result + ", time=" + time + ", uploaded="
				+ uploaded + ", WorkOrg=" + WorkOrg + ", operator=" + operator + ", location=" + location + "]";
	}

	public boolean isChecked() {
		return checked;
	}


	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
