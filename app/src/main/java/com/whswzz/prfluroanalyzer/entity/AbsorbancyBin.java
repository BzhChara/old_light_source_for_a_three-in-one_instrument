package com.whswzz.prfluroanalyzer.entity;

public class AbsorbancyBin {
	private int channel;
	private float[] abs;
	public AbsorbancyBin(int channel, float[] abs) {
		super();
		this.channel = channel;
		this.abs = abs;
	}
	public int getChannel() {
		return channel;
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	public float[] getAbs() {
		return abs;
	}
	public void setAbs(float[] abs) {
		this.abs = abs;
	}
	
}
