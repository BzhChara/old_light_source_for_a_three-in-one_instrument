package com.zkzk.pra.utils;


import android.content.Intent;
import top.jemen.interfaces.ICallback;
import top.jemen.utils.LogUtil;

/**
 * 迈冲
 * @author Jemen Chen
 *
 */
public class MC {
	public static void hideNavigation(final ICallback callback,final long delay){
			new Thread() {
				public void run() {
					try
					{	
						setName("removeStateBar");
						sleep(delay);
						String command;
						command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib service call activity 42 s16 com.android.systemui";
						Process proc = Runtime.getRuntime().exec(new String[] { "su",
						"-c", command });
						int result=proc.waitFor();
						LogUtil.e("result="+result);
						if(result==0){
//							BufferedReader bf=new BufferedReader(new InputStreamReader(pro.getInputStream(),"GBK"));
//							String line;
//							StringBuilder sb = new StringBuilder();
//							while((line=bf.readLine())!=null) {
//								sb.append(line).append("\n");
//							}
//							bf.close();
//							System.out.println(sb);
							if(null!=callback) {
								callback.onSuccess("");
							}
						}else {
							if(null!=callback)
								callback.onFailed("");
						}
						
					}catch(Exception ex){
						ExceptionHandler.handleException(ex);
						if(null!=callback)
							callback.onFailed("");
					}
				};
			}.start();
	}
	
}
