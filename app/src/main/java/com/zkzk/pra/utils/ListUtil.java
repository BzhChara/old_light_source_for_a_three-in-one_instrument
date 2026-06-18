package com.zkzk.pra.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.entity.Source;
import com.whswzz.prfluroanalyzer.entity.Species;
import com.whswzz.prfluroanalyzer.fluoro.entity.Specimen;
import com.whswzz.prfluroanalyzer.photometer.entity.PhotometerProj;
import com.zkzk.pra.entity.Project;

public class ListUtil {
	public static List<String> getProjs(List<Project> listProj) {
		List<String> projs = new LinkedList<String>();
		for (Project proj : listProj) {
			String value = proj.getProj();
			if (getPosition(projs, value) < 0)
				projs.add(value);
		}
		return projs;
	}

	public static Project getProject(List<Project> list, String name) throws Exception {
		if (null == name)
			throw new Exception("参数name不能为空");
		for (Project project : list) {
			if (name.equals(project.getProj()))
				return project;
		}
		return null;
	}

	public static int getPosition(List<String> list, String target) {
		for (int i = 0; i < list.size(); i++) {
			if (target.equals(list.get(i))) {
				return i;
			}
		}
		return -1;
	}


	

	public static List<String> getRootNames(List<Species> species) {
		List<String> names=new LinkedList<String>();
		if(null==species)
			return names;
		for(Species sp:species) {
			names.add(sp.getName());
		}
		return names;
	}

	/**
	 * 项目
	 * @param projs
	 * @return
	 */
	public static List<String> getRootNamesProj(List<PhotometerProj> projs) {
		List<String> names=new LinkedList<String>();
		if(null==projs)
			return names;
		for(PhotometerProj sp:projs) {
			names.add(sp.getName());
		}
		return names;
	}
	
	public static List<String> getSubNames(List<Species> species) {
		List<String> names=new LinkedList<String>();
		if(null==species||species.size()==0)
			return names;
		for(Species sp:species) {
			if(null!=sp.getSubSpecies()) {
				names.addAll(getRootNames(sp.getSubSpecies()));
			}
		}
		return names;
	}
	
	public static void fillSpeciesNames(List<Species> species,List<String> names,int layers) {
		if(null==species||species.size()==0)
			return ;
		if(layers==1) {
			for(Species sp:species)
				names.add(sp.getName());
		}else {
			for(Species sp:species)
				fillSpeciesNames(sp.getSubSpecies(), names, layers-1);
		}
	}
	
	
	public static List<String> getSpecieNames(List<Species> species,int layer) {
		List<String> names=new LinkedList<String>();
		if(null==species)
			return names;
		List<Species> buf=new LinkedList<Species>();
		for(int i=0;i<layer;i++) {
			
		}
		
		for(Species sp:buf) {
			names.add(sp.getName());
		}
		return names;
	}


	
	
	public static Species getSpecies(List<Species> species, String text) {
		if(null==species||text==null)
			return null;
		for(Species sp:species) {
			if(text.equals(sp.getName())) {
				return sp;
			}
		}
		return null;
	}

	public static PhotometerProj getProjects(List<PhotometerProj> projs, String text) {
		if(null==projs||text==null)
			return null;
		for(PhotometerProj sp:projs) {
			if(text.equals(sp.getName())) {
				return sp;
			}
		}
		return null;
	}

	public static String listNames(List<Species> subSpecies) {
		if(null==subSpecies||subSpecies.size()==0) {
			return "";
		}
		StringBuilder sb=new StringBuilder();
		for(Species sp:subSpecies) {
			sb.append(sp.getName()).append("  ");
		}
		return sb.toString();
	}

	public static String listNamesProj(List<PhotometerProj> subSpecies) {
		if(null==subSpecies||subSpecies.size()==0) {
			return "";
		}
		StringBuilder sb=new StringBuilder();
		for(PhotometerProj sp:subSpecies) {
			sb.append(sp.getName()).append("  ");
		}
		return sb.toString();
	}
	
	
	public static void recuisionNames(List<Species> species,List<String> r) {
		if(null==species||species.size()==0) {
			return ;
		}
		for(Species sp:species) {
			r.add(sp.getName());
			if(null!=sp.getSubSpecies()) {
				recuisionNames(sp.getSubSpecies(), r);
			}
		}
	}

	public static void delete(List<Species> lsSpecies, Species sp) {
		if(null==sp||null==lsSpecies) {
			return;
		}
		lsSpecies.remove(sp);
	}

	public static void deleteProj(List<PhotometerProj> projs, PhotometerProj ppj) {
		if(null==ppj||null==projs) {
			return;
		}
		projs.remove(ppj);
	}


	public static List<String> getSources(List<Source> sources) {
		List<String> result=new LinkedList<String>();
		if(null==sources||sources.size()==0) {
			return result;
		}
		for(Source s:sources) {
			result.add(s.toString());
		}
		return result;
	}
	
}
