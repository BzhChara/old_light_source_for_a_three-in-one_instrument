package com.zkzk.pra.utils;

import java.util.List;

public interface CallBack<T> {
	void onSuccess(T t);
	void onFailed(String msg);
	
}
