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

public class StreamConfig {
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
	
	/**
	 * Get the Stream's description
	 * @return the description of the Stream
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Set the Stream's description
	 * @param description the description of the Stream
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Get the Stream's genre
	 * @return the Stream's genre
	 */
	public String getGenre() {
		return genre;
	}
	
	/**
	 * Set the Stream's genre
	 * @param genre the Stream's genre
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}
	
	/**
	 * Get the hostname for the Stream to connect to
	 * @return the hostname of the server to connect to
	 */
	public String getHostname() {
		return hostname;
	}
	
	/**
	 * Sets the hostname for the Stream connect to
	 * @param hostname The hostname of the server to connect to
	 */
	public void setHostname(String hostname) {
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
	 */
	public void setHostport(int hostport) {
		this.hostport = hostport;
	}
	
	/**
	 * Check if the Stream should be listed as public
	 * @return whether or not the Stream is public
	 */
	public boolean isPublic() {
		return isPublic;
	}
	
	/**
	 * Set the Stream to be listed as public or not
	 * @param isPublic whether or not the Stream is public
	 */
	public void setPublic(boolean isPublic) {
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
	 */
	public void setMount(String mount) {
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
	 */
	public void setName(String name) {
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
	 */
	public void setPassword(String password) {
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
	 */
	public void setUsername(String username) {
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

	/*
	 * TODO:
	 * 		ID3 Header, get/set get HttpRequest()
	 */
}
