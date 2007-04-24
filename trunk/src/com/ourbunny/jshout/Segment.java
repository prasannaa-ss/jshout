/*
 * 
 *	=======================================================================
 * 	jShout - Stream audio from your program to an (ice/shout)cast server
 *	Copyright (C) 2007  Tommy Murphy
 *
 *	This program is free software; you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation; either version 2 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License along
 *	with this program; if not, write to the Free Software Foundation, Inc.,
 *	51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *	=======================================================================
 *
 */

package com.ourbunny.jshout;

public class Segment {
	// # of milliseconds the data represents
	private long time;
	private byte[] data;
	
	/**
	 * Create a new Segment object
	 * @param d byte array of media data
	 * @param t milliseconds to play the data
	 */
	public Segment(byte[] d, long t) {
		this.time = t;
		this.data = d;
	}
	
	/**
	 * Get Segment data
	 * @return data as an array of bytes
	 */
	public byte[] getData() {
		return data;
	}
	
	/**
	 * Set Segment data
	 * @param data byte array of media data
	 */
	public void setData(byte[] data) {
		this.data = data;
	}
	
	/**
	 * Get Segment time
	 * @return milliseconds to play data
	 */
	public long getTime() {
		return time;
	}
	
	/**
	 * Set Segment time
	 * @param time milliseconds to play data
	 */
	public void setTime(long time) {
		this.time = time;
	}
	
	/**
	 * Get Data size
	 * @return the number of bytes in the segment
	 */
	public int getSize() {
		return this.data.length;
	}
}
