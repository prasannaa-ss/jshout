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
	
	public Shouter(StreamConfig c) {
		this.config = c;
	}
	
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
			connected = false;
			return false;
		}
	}
	
	public boolean send(Segment s) throws Exception{
		if (connected) {
			
			mysocket.print(s.getData()); // IO exception
			mysocket.flush();
			
			try {
				Thread.sleep(s.getTime());
			} catch (Exception e) {
				System.out.println(e + "error");
				return false;
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	// add a close function
}
