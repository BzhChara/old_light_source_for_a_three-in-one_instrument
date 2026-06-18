package com.whswzz.prfluroanalyzer.photometer.entity;

import com.whswzz.prfluroanalyzer.entity.Species;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.jemen.utils.LogUtil;

public class PhotometerProj implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name; //检测项目名
	private int ms; //？检测时间
//	private double[][] params; //第一行存吸光度值，第二行存浓度值。
	private Map<String, Function> functions;
	
	public PhotometerProj() {
		super();
	}
	public PhotometerProj(String name) {
		this.name=name;
	}

	public PhotometerProj(String name, int ms) {
		super();
		this.name = name;
		this.ms = ms;
	}
	private List<PhotometerProj> projs;
	public List<PhotometerProj> getPhotometerProj() {
		return projs;
	}
	
	
	public String getName() {
		LogUtil.d("functions:"+functions);
		
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMs() {
		return ms=10000;
	}
	public void setMs(int ms) {
		this.ms = ms;
	}
	
	public PhotometerProj addFunction(String specimen,Function function) {
		if(null==specimen||null==function) {
			LogUtil.e("specimen="+specimen+",funciont="+function);
			return this;
		}
		if(functions==null) {
			functions=new HashMap<>();
		}
		functions.put(specimen, function);
		return this;
	}
	public Function getFunction(String specimen) {
		if(null==functions) {
			return null;
		}
		if(null==specimen){
			return getFunction();
		}
		Function r=functions.get(specimen);
		if (null == r) {
			r=functions.get("");
		}
		return r;
	}

	/**
	 * 2024-10月，上成要求标曲不与样品关联了，随便哪个样品使用同样的标曲，但是呢，又要加个稀释倍数问题。
	 * @return
	 */
	public Function getFunction( ) {

		if(null==functions||functions.values().size()<1) {
			return null;
		}
		return functions.values().iterator().next();
	}

	
	@Override
	public String toString() {
		return name;
	}
	
	
	
}
