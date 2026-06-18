package com.zkzk.pra.parser;
/**
 * 解析自定义的字符串格式
 * @author Jemen Chen
 */
import java.util.HashMap;
import java.util.Map;
public class JemenParser {
	public static Map<String, String> parse(String text){
		if(null==text)	return null;
		try {
			Map<String, String> map=new HashMap<>();
			String[] ss=text.split(";");
			for(String s:ss) {
				String[] kv=s.split(":");
				if(kv.length>=2) {
					String v=kv[1];
					for(int i=2;i<kv.length;i++) {
						v+=":"+kv[i];
					}
					map.put(kv[0], v);
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
