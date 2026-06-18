package top.jemen.interfaces;

import top.jemen.utils.LogUtil;

public abstract class ACallback implements ICallback{
	@Override
	public void onFailed(Object obj) {
		LogUtil.d("failed:"+obj);
	}
}
