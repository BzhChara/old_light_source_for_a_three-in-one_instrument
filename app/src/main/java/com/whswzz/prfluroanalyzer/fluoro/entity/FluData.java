package com.whswzz.prfluroanalyzer.fluoro.entity;
/**
 * 检测数据实体类
 *
 * @author Jemen Chen
 * 1.ID号：序号
 * 2.样品编号：SN序列号
 * 3.检测时间：
 * 4.样品名称：大米，水，土壤
 * 5.检测项目：铅和镉，有机磷农残
 * 6.检测限值：
 * 7.检测结果：
 * 8.送检单位：
 * 9.检测单位：
 * 10.检测人员：
 */


import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.entity.IData;
import com.whswzz.prfluroanalyzer.fluoro.dal.Database;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = Database.CollaurumData.TABLE_NAME)
public class FluData implements Serializable,IData {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = Database.CollaurumData.Columns.ID, isId = true, autoGen = true)
	@Expose(serialize = false, deserialize = false)
	private int id;

	@Column(name = Database.CollaurumData.Columns.SN)
	@Expose(serialize = true, deserialize = true)
	private String sn;// 样品编号
	

	@Column(name = Database.CollaurumData.Columns.TIME)
	@Expose(serialize = true, deserialize = true)
	private long time;

	@Column(name = Database.CollaurumData.Columns.SPECIMEN)
	@Expose(serialize = true, deserialize = true)
	private String specimen;// 4.样品名称：大米，水，土壤

	@Column(name = Database.CollaurumData.Columns.PROJ)
	@Expose(serialize = true, deserialize = true)
	private String proj="";// 5.检测项目：铅和镉，有机磷农残

	@Column(name = Database.CollaurumData.Columns.LIMIT)
	@Expose(serialize = true, deserialize = true)
	private float limit=1;// 6.检测限值：

	@Column(name = Database.CollaurumData.Columns.SOURCE_ORG)
	private String sourceOrg;

	@Column(name = Database.CollaurumData.Columns.SOURCE_ADDR)
	private String sourceAddr;

	@Column(name = Database.CollaurumData.Columns.SOURCE_CONTACT)
	private String sourceContact;

	@Column(name = Database.CollaurumData.Columns.SOURCE_PHONE)
	private String sourcePhone;
	
	@Column(name = Database.CollaurumData.Columns.SOURCE_ORG_CODE)
	private String sourceOrgCode;
	


	@Column(name = Database.CollaurumData.Columns.SOURCE_ORG_TYPE)
	private int sourceOrgType=1;
	
	

	@Column(name = Database.CollaurumData.Columns.RESULT)
	@Expose(serialize = true, deserialize = true)
	private String result;// 7.检测结果：

	@Column(name = Database.CollaurumData.Columns.USER_ORG)
	@Expose(serialize = true, deserialize = true)
	private String userOrg;// 9.检测单位

	@Column(name = Database.CollaurumData.Columns.USER_ADDR)
	private String userAddr;

	@Column(name = Database.CollaurumData.Columns.USER_CONTACT)
	private String userContact;

	@Column(name = Database.CollaurumData.Columns.USER_PHONE)
	private String userPhone;

	@Column(name = Database.CollaurumData.Columns.OPERATOR)
	@Expose(serialize = true, deserialize = true)
	private String operator;// 10.检测人员：

	@Column(name = Database.CollaurumData.Columns.VALUES)
	private ValueList values;//

	@Column(name = Database.CollaurumData.Columns.HUMP)
	private Hump hump;// 驼峰

	@Column(name = Database.CollaurumData.Columns.T)
	private float t;
	@Column(name = Database.CollaurumData.Columns.C)
	private float c=Float.MIN_VALUE;

	@Column(name = Database.CollaurumData.Columns.UPLOADED, autoGen = false)
	private boolean upLoded = false;

	
	@Column(name = Database.CollaurumData.Columns.CODE)
	private String userCode;
	@Column(name = Database.CollaurumData.Columns.TOKEN)
	private String token;
	

	
	
	private float temp;// 温度
	
	
	
	

	private transient boolean checked;// 仅仅用来标记是否选择，不向数据库保存，也不向网络传输。
	// private int index=1; //列表界面的序号，暂不详有何用途。

	@Column(name = Database.CollaurumData.Columns.CHANNEL)
	@Expose(serialize = true, deserialize = true)
	private String channel;

	public FluData() {
		super();
	}
	//
	// /**数据库查询封装用
	// * 构造函数，检测时间通过参数传入。
	// * @param sn
	// * @param specimen
	// * @param proj
	// * @param limit
	// * @param result
	// * @param customerOrg
	// * @param workOrg
	// * @param operator
	// * @param peaks
	// */
	// public Data(String sn, String specimen, String proj, float limit, String
	// result, String customerOrg,
	// String workOrg, String operator, List<Integer> values) {
	// super();
	// this.sn = sn;
	// this.time = System.currentTimeMillis();
	// this.specimen = specimen;
	// this.proj = proj;
	// this.limit = limit;
	// this.result = result;
	// this.customerOrg = customerOrg;
	// this.workOrg = workOrg;
	// this.operator = operator;
	// this.values=values;
	// }

	public FluData(int id, String sn, long time, String specimen, String proj, float limit, String sourceOrg,
			String sourceAddr, String sourceContact, String sourcePhone, String sourceOrgCode, int sourceOrgType,
			String result, String userOrg, String userAddr, String userContact, String userPhone, String operator,
			ValueList values, Hump hump, float t, float c, boolean upLoded, String code, String token, float temp,
			boolean checked, String channel) {
		super();
		this.id = id;
		this.sn = sn;
		this.time = time;
		this.specimen = specimen;
		this.proj = proj;
		this.limit = limit;
		this.sourceOrg = sourceOrg;
		this.sourceAddr = sourceAddr;
		this.sourceContact = sourceContact;
		this.sourcePhone = sourcePhone;
		this.sourceOrgCode = sourceOrgCode;
		this.sourceOrgType = sourceOrgType;
		this.result = result;
		this.userOrg = userOrg;
		this.userAddr = userAddr;
		this.userContact = userContact;
		this.userPhone = userPhone;
		this.operator = operator;
		this.values = values;
		this.hump = hump;
		this.t = t;
		this.c = c;
		this.upLoded = upLoded;
		this.userCode = code;
		this.token = token;
		this.temp = temp;
		this.checked = checked;
		this.channel = channel;
	}
	
	
	/**
	 * 用于初始构建
	 * 
	 * @param hump2
	 ***/
	public FluData(String sn, String specimen, String proj, float limit, String result, String sourceOrg,
			String workOrg, String operator, List<Float> values2, float peak, float peakc, Hump hump) {
		this.sn = sn;
		this.time = System.currentTimeMillis();
		this.specimen = specimen;
		this.proj = proj;
		this.limit = limit;
		this.result = result;
		this.sourceOrg = sourceOrg;
		this.userOrg = workOrg;
		this.operator = operator;
		this.values = new ValueList();
		if (null != values2)
			this.values.addAll(values2);
		this.t = peak;
		this.c = peakc;
		this.hump = hump;
	}

	public FluData(int id, String sn, long time, String specimen, String proj, float limit, String sourceOrg,
			String sourceAddr, String sourceContact, String sourcePhone, String result, String workOrg, String userAddr,
			String userContact, String userPhone, String operator, ValueList values, Hump hump, float t, float c,
			boolean upLoded, float temp, boolean checked, String channel) {
		super();
		this.id = id;
		this.sn = sn;
		this.time = time;
		this.specimen = specimen;
		this.proj = proj;
		this.limit = limit;
		this.sourceOrg = sourceOrg;
		this.sourceAddr = sourceAddr;
		this.sourceContact = sourceContact;
		this.sourcePhone = sourcePhone;
		this.result = result;
		this.userOrg = workOrg;
		this.userAddr = userAddr;
		this.userContact = userContact;
		this.userPhone = userPhone;
		this.operator = operator;
		this.values = values;
		this.hump = hump;
		this.t = t;
		this.c = c;
		this.upLoded = upLoded;
		this.temp = temp;
		this.checked = checked;
		this.channel = channel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * 获取样品类型
	 * 
	 * @return
	 */
	public String getSpecimen() {
		return specimen;
	}

	public void setName(String specimen) {
		this.specimen = specimen;
	}

	public String getProj() {
		return proj;
	}

	public void setProj(String proj) {
		this.proj = proj;
	}

	public float getLimit() {
		return limit;
	}

	public void setLimit(float limit) {
		this.limit = limit;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}


	public String getUserOrg() {
		return userOrg;
	}

	public void setWorkOrg(String workOrg) {
		this.userOrg = workOrg;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public ValueList getValues() {
		return values;
	}

	public void setValues(ValueList values) {
		this.values = values;
	}

	public float getTemp() {
		return temp;
	}

	public void setTemp(float temp) {
		this.temp = temp;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isUpLoded() {
		return upLoded;
	}

	public void setUpLoded(boolean upDated) {
		this.upLoded = upDated;
	}

	public float getT() {
		return t;
	}

	public void setT(float t) {
		this.t = t;
	}

	public float getC() {
		return c;
	}

	public void setC(float c) {
		this.c = c;
	}

	public void setHump(Hump hump) {
		this.hump = hump;
	}

	public Hump getHump() {
		return hump;
	}

	public String qrMsg() {
		return "样品编号:" + sn + ",检测时间:" + Consts.mydhmsFromat.format(new Date(time)) + ",样品类型:" + specimen + ",检测项目:"
				+ proj + ",送检方:" + sourceOrg + ",检测单位:" + userOrg + ",检测员:" + operator + ",检测结果:" + result;
	}

	public void SetChannelNum(String channel) {
		this.channel = channel;
	}

	public String getChannelNum() {
		return channel;
	}

	public String getSourceUnit() {
		return sourceOrg;
	}

	public void setSourceOrg(String sourceOrg) {
		this.sourceOrg = sourceOrg;
	}

	public String getSourceAddr() {
		return sourceAddr;
	}

	public void setSourceAddr(String sourceAddr) {
		this.sourceAddr = sourceAddr;
	}

	public String getSourceContact() {
		return sourceContact;
	}

	public void setSourceContact(String sourceContact) {
		this.sourceContact = sourceContact;
	}

	public String getSourcePhone() {
		return sourcePhone;
	}

	public void setSourcePhone(String sourcePhone) {
		this.sourcePhone = sourcePhone;
	}

	public String getUserAddr() {
		return userAddr;
	}

	public void setUserAddr(String userAddr) {
		this.userAddr = userAddr;
	}

	public String getUserContact() {
		return userContact;
	}



	public void setUserContact(String userContact) {
		this.userContact = userContact;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

	public void setSpecimen(String specimen) {
		this.specimen = specimen;
	}


	@Override
	public String toString() {
		return "FluData [id=" + id + ", sn=" + sn + ", time=" + time + ", specimen=" + specimen + ", proj=" + proj
				+ ", limit=" + limit + ", sourceOrg=" + sourceOrg + ", sourceAddr=" + sourceAddr + ", sourceContact="
				+ sourceContact + ", sourcePhone=" + sourcePhone + ", result=" + result + ", workOrg=" + userOrg
				+ ", userAddr=" + userAddr + ", userContact=" + userContact + ", userPhone=" + userPhone + ", operator="
				+ operator + ", values=" + values + ", hump=" + hump + ", t=" + t + ", c=" + c + ", upLoded=" + upLoded
				+ ", temp=" + temp + ", channel=" + channel + "]";
	}

	@Override
	public String getUserName() {
		return userOrg;
	}
	
	

	public void setUsrCode(String code) {
		this.userCode=code;
	}
	@Override
	public String getUserCode() {
		return userCode;
	}



	public void setToken(String token) {
		this.token=token;
	}
	@Override
	public String getUserToken() {
		return token;
	}

	@Override
	public String getSourceCode() {
		return sourceOrgCode;
	}
	public String getSourceOrgCode() {
		return sourceOrgCode;
	}
	public void setSourceOrgCode(String sourceOrgCode) {
		this.sourceOrgCode = sourceOrgCode;
	}
	public int getSourceOrgType() {
		return sourceOrgType;
	}
	public void setSourceOrgType(int sourceOrgType) {
		this.sourceOrgType = sourceOrgType;
	}
	public String getSourceOrg() {
		return sourceOrg;
	}
	public String getCode() {
		return userCode;
	}
	public String getToken() {
		return token;
	}
	public void setUserOrg(String userOrg) {
		this.userOrg = userOrg;
	}

	
}
