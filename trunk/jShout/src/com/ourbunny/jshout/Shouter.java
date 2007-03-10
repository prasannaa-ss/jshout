/**
 * 
 */
package com.ourbunny.jshout;

import java.net.*;

/**
 * @author tommy
 *
 */
public class Shouter{
	// stream config info
	private StreamConfig config;
	
	// networking variables
	private StreamSocket mysocket;
	private boolean connected = false;
	
	/**
	 * 
	 */
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
