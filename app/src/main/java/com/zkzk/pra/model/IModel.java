package com.zkzk.pra.model;

import java.util.List;

import top.jemen.interfaces.ICallback;

public interface IModel {
	 void loadContent(String url,ICallback callback);
	 void loadString(String url,ICallback callback);
	 
	 
}
