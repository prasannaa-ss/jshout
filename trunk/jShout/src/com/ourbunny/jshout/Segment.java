package com.ourbunny.jshout;

public class Segment {
	private long time;
	private byte[] data;
	
	public Segment(byte[] d, long t) {
		this.time = t;
		this.data = d;
	}
	
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
}
