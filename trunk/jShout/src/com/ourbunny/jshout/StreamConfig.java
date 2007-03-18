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
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getGenre() {
		return genre;
	}
	
	public void setGenre(String genre) {
		this.genre = genre;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public int getHostport() {
		return hostport;
	}
	
	public void setHostport(int hostport) {
		this.hostport = hostport;
	}
	
	public boolean isPublic() {
		return isPublic;
	}
	
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	
	public String getMount() {
		return mount;
	}
	
	public void setMount(String mount) {
		this.mount = mount;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getAgent() {
		return agent;
	}
	
	public String getBase64Auth() {
		String i = getUsername() + ":" + getPassword();
		return Base64.encodeBytes(i.getBytes());
	}
	
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
	 * ID3 Header, get/set get HttpRequest()
	 */
}
