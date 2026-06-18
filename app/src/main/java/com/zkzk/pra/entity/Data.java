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

import org.xutils.db.annotation.Column;

import com.whswzz.prfluroanalyzer.entity.IData;
import com.whswzz.prfluroanalyzer.entity.Organization;
import com.whswzz.prfluroanalyzer.entity.Source;
import com.whswzz.prfluroanalyzer.fluoro.dal.Database;
/**
 * 农残检测结果实体类
 * @author Jemen Chen
 *2018-12-20 去掉了 implements Serializable，网络用gson，本地用数据库，本程序也不需要Intent传递，所以不需要序列化了。
 */
public class Data implements IData{
	private transient boolean checked;
	private int id;
	private String channelNum;//通道号
	private String sn;//样品编号
	private String proj="农药残留（有机磷/氨基甲酸酯）";//检测项目，初期仅有农残
	private String specimen;//样品名称
	
	
	private Source source;
	
//	private String sourceOrg;//被检单位
//	private String sourceAddr;
//	private String sourceContact;
//	private String sourcePhone;
	
	

	private float absorbancy;//吸光度
	private float inhibitionRatio;//抑制率
	private String limit;//限值
	private String result;//检测结果
	private Long time;//检测时间
	private boolean uploaded;	//有没有上传
	
	private Organization user;
	
//	private String userName;	//检测单位
//	private String userAddr;
//	private String userContact;
//	private String userPhone;
	
	private Location location;	//网络上传采用的是gson，并且本地保存已经拆成两个double，所以也不需要序列化了。
	
	public Data() {
		super();
	}

	

	public Data(boolean checked, int id, String channelNum, String sn, String proj, String specimen, Source source,
			float absorbancy, float inhibitionRatio, String limit, String result, Long time, boolean uploaded,
			Organization user,  Location location) {
		super();
		this.checked = checked;
		this.id = id;
		this.channelNum = channelNum;
		this.sn = sn;
		this.proj = proj;
		this.specimen = specimen;
		this.source = source;
		this.absorbancy = absorbancy;
		this.inhibitionRatio = inhibitionRatio;
		this.limit = limit;
		this.result = result;
		this.time = time;
		this.uploaded = uploaded;
		this.user = user;
		this.location = location;
	}










	public String getChannel() {
		return channelNum;
	}
	public void setChannel(String channelNum) {
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

	
	public long getTime() {
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





	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}


	public boolean isChecked() {
		return checked;
	}


	public void setChecked(boolean checked) {
		this.checked = checked;
	}


	public Source getSource() {
		return source;
	}



	public void setSource(Source source) {
		this.source = source;
	}




	public Organization getUser() {
		return user;
	}



	public void setUser(Organization user) {
		this.user = user;
	}








	public String toString2() {
		return "Data [id=" + id + ", channelNum=" + channelNum + ", sn=" + sn + ", proj=" + proj + ", specimen="
				+ specimen + ", source=" + source + ", absorbancy=" + absorbancy + ", inhibitionRatio="
				+ inhibitionRatio + ", limit=" + limit + ", result=" + result + ", time=" + time + ", uploaded="
				+ uploaded + ", user=" + user + ", location=" + location + "]";
	}

	@Override
	public String toString() {
		return "Data{" +
				"checked=" + checked +
				", id=" + id +
				", channelNum='" + channelNum + '\'' +
				", sn='" + sn + '\'' +
				", proj='" + proj + '\'' +
				", specimen='" + specimen + '\'' +
				", source=" + source +
				", absorbancy=" + absorbancy +
				", inhibitionRatio=" + inhibitionRatio +
				", limit='" + limit + '\'' +
				", result='" + result + '\'' +
				", time=" + time +
				", uploaded=" + uploaded +
				", user=" + user +
				", location=" + location +
				'}';
	}

	@Override
	public String getSourceAddr() {
		if(null==source)
			return null;
		return source.getAddr();
	}



	@Override
	public String getSourcePhone() {
		if(null==source)
			return null;
		return source.getPhone();
	}



	@Override
	public String getUserName() {
		if(null==user)
		return null;
		return user.getName();
	}



	@Override
	public String getUserPhone() {
		if(null!=user) {
			return user.getPhone();
		}
		return null;
	}



	@Override
	public String getOperator() {
		if(null==user)
			return null;
		return user.getOperator();
	}



	@Override
	public String getSourceUnit() {
		if(null==source)
			return null;
		return source.getUnit();
	}

	
	public void setCode(String code) {
		if(null==user) {
			user=new Organization();
		}
		user.setCode(code);
	}
	@Override
	public String getUserCode() {
		if(null==user) {
			return null;
		}
		return user.getCode();
	}


	
	public void setToken(String token) {
		if(null==user) {
			user=new Organization();
		}
		user.setToken(token);
	}
	@Override
	public String getUserToken() {
		if(null==user) {
			return null;
		}
		return user.getToken();
	}



	@Override
	public String getSourceCode() {
		if(null==source)
		return null;
		return source.getCode();
	}



	@Override
	public int getSourceOrgType() {
		if(null==source)
		return 0;
		return source.getType();
	}
	public String getUserContact() {
		if(null!=user){
			return  user.getContact();
		}
		return null;
	}




}
