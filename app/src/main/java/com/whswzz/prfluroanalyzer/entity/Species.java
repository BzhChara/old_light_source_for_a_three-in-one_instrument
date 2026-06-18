package com.whswzz.prfluroanalyzer.entity;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Species implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private List<Species> subSpecies;
	public Species() {
		super();
	}
	public Species(String name, List<Species> subSpecies) {
		super();
		this.name = name;
		this.subSpecies = subSpecies;
	}
	public Species(String name) {
		super();
		this.name = name;
	}
	public Species(String name,String...subNames) {
		super();
		this.name = name;
		subSpecies=new LinkedList<>();
		for(String n:subNames) {
			subSpecies.add(new Species(n));
		}
	}
	
	public void addSubspecies(String... subNames) {
		if(null==subSpecies) {
			subSpecies=new LinkedList<Species>();
		}
		for(String n:subNames) {
			subSpecies.add(new Species(n));
		}
	}
	
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Species> getSubSpecies() {
		return subSpecies;
	}
	public void setSubSpecies(List<Species> subSpecies) {
		this.subSpecies = subSpecies;
	}
	@Override
	public String toString() {
		return "Species [name=" + name + ", subSpecies=" + subSpecies + "]";
	}
	
	
}
