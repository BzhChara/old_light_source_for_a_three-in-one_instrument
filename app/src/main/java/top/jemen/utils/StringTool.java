package top.jemen.utils;

import java.nio.charset.Charset;

import android.util.Base64;

public class StringTool {
	private static int[] has= {73, 110, 115, 116, 105, 116, 117, 116, 101, 32, 111, 102, 32, 101, 110, 118, 105, 114, 111, 110, 109, 101, 110, 116, 32, 97, 110, 100, 32, 115, 97, 102, 101, 116, 121, 44, 32, 119, 117, 104, 97, 110, 32, 97, 99, 97, 100, 101, 109, 121, 32, 111, 102, 32, 97, 103, 114, 105, 99, 117, 108, 116, 117, 114, 97, 108, 32, 115, 99, 105, 101, 110, 99, 101, 115};
	public static String getString(byte[] bs,int x) {
		if(null==bs||bs.length<=0) {
			return null;
		}
		for(int i=0;i<bs.length;i++) {
			bs[i]=(byte)( (~bs[i])-x);
		}
		return new String(bs);
	}
	
	public static String encrypt(String s) {
		if(null==s||s.isEmpty()) {
			return s;
		}
		byte[] bs=s.getBytes(Charset.forName("UTF-8"));
		for(int i=0;i<bs.length;i++) {
			bs[i]=(byte)(~((bs[i]<<4&0xf0)|(bs[i]>>4&0xf)));
			bs[i]=(byte) (bs[i]^has[i%has.length]);
		}
		return Base64.encodeToString(bs, Base64.NO_WRAP|Base64.NO_PADDING);
	}
	
	
	
}	
