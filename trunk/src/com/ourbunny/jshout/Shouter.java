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

import java.net.*;

public class Shouter{
	// stream config info
	private StreamConfig config;
	
	// networking variables
	private StreamSocket mysocket;
	private boolean connected = false;
	
	// timing variables
	private long sentTime = 0;
	private long sleepTime = 0;
	
	/**
	 * Creates a new Shouter based on the StreamConfig settings
	 * @param c the StreamConfig settings
	 */
	public Shouter(StreamConfig c) {
		this.config = c;
	}
	
	/**
	 * Connect to the icecast server 
	 * @return true if the connect is established
	 * @throws Exception
	 */
	public boolean connect() throws Exception{
		if (!connected) {
			Socket shoutSocket = new Socket(config.getHostname(), config.getHostport());  // unknown host exception
			mysocket = new StreamSocket (shoutSocket);  // throws io exception
			
			// send SOURCE request
			mysocket.print(config.getHttpRequest());
			
			// get response
			System.out.println("Waiting for reply...");
			String s;
			while((s = mysocket.readLine()) != null) {
				System.out.println(s);
				if (s.equals("HTTP/1.0 200 OK")){
					break;
				} else {
					connected = false;
					return false;
				}
			}
			
			connected = true;
			return true;
		} else {
			// fix logic here
			// connected = false;
			return false;
		}
	}
	
	/**
	 * Send a Segment of data to the server.
	 * @param s the Segment of data to send to the server
	 * @throws Exception
	 */
	public void send(Segment s) throws Exception{
		if (connected) {
			// write data
			mysocket.print(s.getData()); // IO exception
			mysocket.flush();
			
			// update timing
			sentTime = System.currentTimeMillis();
			sleepTime += s.getTime();
		}
	}
	
	/**
	 * Waits until more data is required for the stream
	 */
	public void sync() {
		if (sleepTime > 0 ) {
			try {
				Thread.sleep(sleepTime);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			sleepTime += sentTime - System.currentTimeMillis();
		}
	}
	
	/**
	 * Close the connection to the icecast server
	 */
	public void close() {
		this.connected = false;
		this.mysocket.close();
	}
	
	// add some functionality to publish changes to streamconfig
}
