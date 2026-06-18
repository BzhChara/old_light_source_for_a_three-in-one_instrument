package top.jemen.serial;

import java.util.Arrays;

import top.jemen.utils.LogUtil;

public class Serial {
	private int fd=-1;
	private native int OPEN(String path, int rate, int noblock);
	
	public native int 	Close(int fd);
	public native byte[] Read(int fd);//实验返回byte【】貌似不会造成泄露
	public native int	Write(int fd,byte[] buffer,int len);
	
	
	public int write(byte[] buffer,int len) {
		LogUtil.d("tty send:"+Arrays.toString(buffer));
		return Write(fd, buffer, len);
	}
	public int close() {
		return Close(fd);
	}
	
	public int open(int com,int rate) {
		fd=OPEN("/dev/ttyS"+com, rate, 00004000);
		return fd;
	}
	public int openBlock(int comx, int rate) {
		fd= OPEN("/dev/ttyS"+comx, rate,00000000);
		return fd;
	}
}
