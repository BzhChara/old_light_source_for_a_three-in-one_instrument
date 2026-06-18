package top.jemen.utils;

import java.util.List;

public class Arrays {
	public static String toHexString(byte[] bs) {
		if (bs == null) {
            return "null";
        }
        if (bs.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder(bs.length * 6);
        sb.append('[');
        String b=Integer.toHexString(bs[0]);
        if(b.length()>2) {
        	b=b.substring(b.length()-2);
        }
        sb.append(b);
        for (int i = 1; i < bs.length; i++) {
            sb.append(", ");
            b=Integer.toHexString(bs[i]);
            if(b.length()>2) {
            	b=b.substring(b.length()-2);
            }
            sb.append(b);
        }
        sb.append(']');
        return sb.toString();
	}
	
	public static String toString(double[] odata) {
		return java.util.Arrays.toString(odata);
	}

	public static String toHexString(int[] data) {
		if (data == null) {
            return "null";
        }
        if (data.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder(data.length * 6);
        sb.append('[');
        String b=Integer.toHexString(data[0]);
        if(b.length()>2) {
        	b=b.substring(b.length()-2);
        }
        sb.append(b);
        for (int i = 1; i < data.length; i++) {
            sb.append(", ");
            b=Integer.toHexString(data[i]);
            if(b.length()>2) {
            	b=b.substring(b.length()-2);
            }
            sb.append(b);
        }
        sb.append(']');
        return sb.toString();
	}


}
