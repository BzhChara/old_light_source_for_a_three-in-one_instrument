package com.whswzz.prfluroanalyzer.entity;

import java.util.Arrays;

public class PowerEntity {
	private byte[] bs;

	public PowerEntity(byte[] bs) {
		super();
		this.bs = bs;
	}

	public byte[] getBs() {
		return bs;
	}

	public void setBs(byte[] bs) {
		this.bs = bs;
	}

	@Override
	public String toString() {
		return "PowerEntity{" +
				"bs=" + Arrays.toString(bs) +
				'}';
	}
}
