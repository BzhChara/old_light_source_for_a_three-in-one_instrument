package com.zkzk.pra.utils;

import android.content.pm.IPackageInstallObserver;
import android.os.RemoteException;

public class PackageInstallObserver extends IPackageInstallObserver.Stub{
	@Override
	public void packageInstalled(String apackageName, int returnCode) throws RemoteException {
		
	}
}
