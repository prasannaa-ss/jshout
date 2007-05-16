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

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Shouter{
	// stream config info
	private String		hostname = "localhost";
	private int			hostport = 8000;
	private String		username = "source";
	private String		password;
	private String		mount;
	private String		name;
	private String		genre;
	private String		agent = "jstreamer/0.1";
	private String		description;
	private boolean		isPublic = true;
	private HashMap<String, String> meta = new HashMap<String, String>();
	
	
	// networking variables
	private StreamSocket mysocket;
	private boolean connected = false;
	
	// timing variables
	private long sentTime = 0;
	private long sleepTime = 0;
	
	/**
	 * Connect to the icecast server 
	 * @return true if a connection is established
	 * @throws ShouterException if already connected
	 */
	public boolean connect() throws ShouterException, IOException, UnknownHostException{
		if (connected) {
			// already connected exception
			throw new ShouterException();
		}
		
		Socket shoutSocket = new Socket(this.getHostname(), this.getHostport());  // unknown host exception
		mysocket = new StreamSocket(shoutSocket);  // throws io exception
			
		// send SOURCE request
		mysocket.print(this.getHttpRequest());
			
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
	}
	
	/**
	 * Send a Segment of data to the server.
	 * @param s the Segment of data to send to the server
	 * @throws ShouterException if already connected
	 */
	public void send(Segment s) throws ShouterException, IOException{
		if (!connected) {
			// already connected exception
			throw new ShouterException();
		}
		
		// write data
		mysocket.print(s.getData()); // throws IO exception
		mysocket.flush(); // is this needed?
			
		// update timing
		sentTime = System.currentTimeMillis();
		sleepTime += s.getTime();
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
		connected = false;
		mysocket.close();
	}
	
	// add some functionality to publish changes to streamconfig
	
	/**
	 * Get the stream's description
	 * @return the description of the stream
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Set the stream's description
	 * @param description the description of the stream
	 * @throws ShouterException if already connected
	 */
	public void setDescription(String description) throws ShouterException {
		if (connected) {
			throw new ShouterException();
		}
		
		this.description = description;
	}
	
	/**
	 * Get the stream's genre
	 * @return the stream's genre
	 */
	public String getGenre() {
		return genre;
	}
	
	/**
	 * Set the stream's genre
	 * @param genre the stream's genre
	 * @throws ShouterException if already connected
	 */
	public void setGenre(String genre) throws ShouterException {
		if (connected) {
			throw new ShouterException();
		}
		
		this.genre = genre;
	}
	
	/**
	 * Get the hostname for the stream to connect to
	 * @return the hostname of the server to connect to
	 * @throws ShouterException if already connected
	 */
	public String getHostname() {
		return hostname;
	}
	
	/**
	 * Sets the hostname for the stream connect to
	 * @param hostname The hostname of the server to connect to
	 * @throws ShouterException if already connected
	 */
	public void setHostname(String hostname) throws ShouterException {
		if (connected) {
			throw new ShouterException();
		}
		
		this.hostname = hostname;
	}
	
	/**
	 * Get the port number on the host to connect to
	 * @return the port number on the host to connect to
	 */
	public int getHostport() {
		return hostport;
	}
	
	/**
	 * Set the port number on the host to connect to
	 * @param hostport the port number on the host to connect to
	 * @throws ShouterException if already connected
	 */
	public void setHostport(int hostport) throws ShouterException {
		if (connected) {
			throw new ShouterException();
		}
		
		this.hostport = hostport;
	}
	
	/**
	 * Check if the stream should be listed as public
	 * @return whether or not the stream is public
	 */
	public boolean isPublic() {
		return isPublic;
	}
	
	/**
	 * Set the stream to be listed as public or not
	 * @param isPublic whether or not the stream is public
	 * @throws ShouterException if already connected
	 */
	public void setPublic(boolean isPublic) throws ShouterException {
		if (connected) {
			throw new ShouterException();
		}
		
		this.isPublic = isPublic;
	}
	
	/**
	 * Get the mountpoint used to connect to the server
	 * @return the name of the mountpoint
	 */
	public String getMount() {
		return mount;
	}
	
	/**
	 * Set the mountpoint used to connect to the server
	 * @param mount the name of the mountpoint
	 * @throws ShouterException if already connected
	 */
	public void setMount(String mount) throws ShouterException {
		if (connected) {
			throw new ShouterException();
		}
		
		this.mount = mount;
	}
	
	/**
	 * Get the name of the stream
	 * @return the name of the stream
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the stream
	 * @param name the name of the stream
	 * @throws ShouterException if already connected
	 */
	public void setName(String name) throws ShouterException {
		if (connected) {
			throw new ShouterException();
		}
		
		this.name = name;
	}
	
	/**
	 * Get the password to connect to the server
	 * @return The password in plaintext
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Set the password to connect to the server
	 * @param password the password in plaintext
	 * @throws ShouterException if already connected
	 */
	public void setPassword(String password) throws ShouterException {
		if (connected) {
			throw new ShouterException();
		}
		
		this.password = password;
	}
	
	/**
	 * Get the username to connect to the server with
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Sets the username to connect to the server with
	 * @param username the username
	 * @throws ShouterException if already connected
	 */
	public void setUsername(String username) throws ShouterException {
		if (connected) {
			throw new ShouterException();
		}
		
		this.username = username;
	}
	
	/**
	 * Get the UserAgent string used in HTTP
	 * @return the UserAgent
	 */
	public String getAgent() {
		return agent;
	}
	
	// Private method to generate the username:password
	// combination used in the HTTP request.
	// Relies on the Base64.java library.
	private String getBase64Auth() {
		String i = getUsername() + ":" + getPassword();
		return Base64.encodeBytes(i.getBytes());
	}
	
	/**
	 * Get the HTTP Request byte array used to initiate
	 * A connection with the icecast server
	 * 
	 * @return the HTTP Request byte array
	 */
	public byte[] getHttpRequest() {
		String RN = "\r\n";
		String request = "SOURCE /" + getMount() + " HTTP/1.0" + RN;
		request += "Content-Type: audio/mpeg" + RN;
		request += "Authorization: Basic " + getBase64Auth() + RN;
		request += "User-Agent: " + getAgent() + RN;
		request += "ice-name: " + getName() + RN;
		// request += "ice-url: " + this.url + RN;
		request += "ice-genre: " + getGenre() + RN;
		// request += "ice-bitrate: " + this.bitrate + RN;
		
		request += "ice-public: ";
		if (isPublic()) {
			request += "1" + RN;
		} else {
			request += "0" + RN;
		}
		request += "ice-description: " + getDescription() + RN;
		request += RN;
		
		return request.getBytes();
	}

	public void setMetadata() throws IOException, UnknownHostException{
		// open new socket
		Socket shoutSocket = new Socket(this.getHostname(), this.getHostport());  // unknown host exception
		StreamSocket metaSock = new StreamSocket(shoutSocket);  // throws io exception
		
		// get data
		String data = this.convertMetadata();
		
		// get auth
		
		// build string
		String sendString = "GET /admin/metadata?mode=updinfo&mount=" + this.mount + "&" + data 
			+ " HTTP/1.0\r\nUser-Agent: " + this.agent + "\r\n" + this.getBase64Auth() + "\r\n";
		
		// send string
		metaSock.println(sendString);
		
		// close socket
		metaSock.close();
	}
	
	public void updateMetadata(String id, String value) {
		// TODO: clean id and value
		this.meta.put(id, value);
	}
	
	private String convertMetadata() {
		String returnMe = "";
		
		Set<String> keys =  this.meta.keySet();
		Iterator<String> itr = keys.iterator();
		
		while (itr.hasNext()) {
			String key = itr.next();
			returnMe += "&" + key + "=" + this.meta.get(key);
		}
		
		return returnMe;
	}
}
