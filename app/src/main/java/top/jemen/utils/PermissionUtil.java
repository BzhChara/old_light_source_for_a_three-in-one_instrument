package top.jemen.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class PermissionUtil {
	public static boolean chmodPermisson(String path) {
		try {
			Process pro = Runtime.getRuntime().exec("su");
			OutputStream out = pro.getOutputStream();
			out.write(("chmod 666 "+path).getBytes());
			out.flush();
			out.close();
			pro.waitFor();
			BufferedReader reader=new BufferedReader(new InputStreamReader(pro.getInputStream()));
			String line;
			while((line=reader.readLine())!=null) {
				LogUtil.d(line);
			}
			
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		
	}

}
