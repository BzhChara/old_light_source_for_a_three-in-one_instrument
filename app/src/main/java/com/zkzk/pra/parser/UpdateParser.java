package com.zkzk.pra.parser;

import com.google.gson.Gson;
import com.zkzk.pra.entity.VersionEntity;

public class UpdateParser {

	public static VersionEntity parser(String jsonString)
	{
//		return new Gson().fromJson(jsonString, VersionEntity.class);
//		return JSON.parseObject(jsonString, VersionEntity.class);	//混淆后出错
		return new Gson().fromJson(jsonString, VersionEntity.class);
		
	}

}
