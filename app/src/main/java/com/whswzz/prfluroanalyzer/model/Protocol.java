package com.whswzz.prfluroanalyzer.model;

import java.util.Arrays;

import com.whswzz.prfluroanalyzer.app.Build;
import com.whswzz.prfluroanalyzer.entity.AbsorbancyBin;
import com.whswzz.prfluroanalyzer.entity.PowerEntity;
import com.whswzz.prfluroanalyzer.enzyme.EnzymeActivity.Enzyme;
import com.whswzz.prfluroanalyzer.enzyme.entity.EnzymeData;
import com.whswzz.prfluroanalyzer.param.Params;

import de.greenrobot.event.EventBus;
import top.jemen.utils.LogUtil;

public class Protocol {

	public static void sum(byte[] data) {
		if(null==data||data.length<2) {
			return;
		}
		int sum=0;
		for(int i=0;i<data.length-1;i++) {
			sum+=data[i];
		}
		data[data.length-1]=(byte) sum;
	}

	public static void parseBs(byte[] bs) {
		if(null==bs||bs.length<6) {
			return;
		}
		EventBus.getDefault().post(bs); //暂且抛出去各自解析
		 //新板子协议解析 ,传过来的bs已经是5AA5开头了。
		switch(bs[3]) {
//		case 0x00://握手
//			EventBus.getDefault().post(bs);
//			break;
		case 0x06://电量采集
			EventBus.getDefault().post(new PowerEntity(bs));
			break;
//		case 0x07://板内温度采集
//			break;
//		case 0x08://板外温度采集
//
//			break;
		case 0x0B://板子版本号
			Build.BOARD=String.format("%d",(bs[5]<<8&0xff)|(bs[6]&0xff));
			break;
//		case 0x50://1-12通道的开启或关闭   这个指令没有回复
//			break;
//		case 0x51://13-24通道开机或关闭   这个指令没有回复
//			break;
//		case 0x52://1-12通道温度设置
//		case 0x53://13-24通道温度设置
//		case 0x54://设置酶片加热温度
//			EventBus.getDefault().post(bs);
//			break;
		case 0x55://读取酶片温度
			Params.TEMP_ENZYME=((bs[5]<<8&0xff00)|(bs[6]&0xff))/10F;
			EventBus.getDefault().post(Enzyme.TEMP);
//			LogUtil.d("酶片温度："+Params.TEMP_ENZYME);
			break;
		case 0x56://定时反馈1-12通道吸光度   //长度30（48），		//改成24个通道一起返回了
		case 0x57://定时反馈13-24通道吸光度 //改成24个通道一起返回了
			float[] fs=new float[24];
			for(int i=0;i<fs.length;i++) {
				int bits=(bs[5+i*4]&0xff)<<0|(bs[6+i*4]&0xff)<<8|(bs[7+i*4]&0xff)<<16|(bs[8+i*4]&0xff)<<24;
				fs[i]=Float.intBitsToFloat(bits);
				if (fs[i] < 0)
					fs[i] *= -1;// 将负数转换为正数显示  光度-->吸光度
			}
//			LogUtil.d(Arrays.toString(fs));
			EventBus.getDefault().post(new AbsorbancyBin(bs[2]-1, fs));
			break;
//		case 0x58://胶体金光源开/关
//			EventBus.getDefault().post(bs);
//			break;
		case 0x5a://酶片的盖子合上
			EventBus.getDefault().post(Enzyme.ENZYME_COVER);
			break;
			
		default:

			break;
		}
		
		
	}

}
