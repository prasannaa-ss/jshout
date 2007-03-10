/*
 * Parts of this file are copied from JRoar
 * Copyright (C) 2001,2002 ymnk, JCraft,Inc.
 * 
 * Licensed under the GNU General Public License
 */

package com.ourbunny.jshout;

import java.io.*;
import java.net.*;

public class StreamSocket {
	private Socket socket = null;
	private BufferedReader br = null;
	private OutputStream os = null;
	
	public StreamSocket(Socket s) throws IOException, SocketException {
		socket = s;
		socket.setTcpNoDelay(true);		// throws SocketException
		
		br = new BufferedReader(new InputStreamReader(s.getInputStream()));		// throws IOException
		os = s.getOutputStream();		// throws IOException
	}
	
	public void close() {
		try {
			socket.shutdownOutput();
			br.close();
			os.close();
			socket.close();
		} catch (IOException e) { }
	}
	
	public String readLine() {
		try {
			return br.readLine();
		} catch (IOException e) {
			return null;
		}
	}
	
	public void println(String s) throws IOException {
		os.write(s.getBytes());
		os.write("\r\n".getBytes());
	}
	
	public void flush() throws IOException {
		os.flush();
	}
	
	public void print(byte[] s) throws IOException {
		os.write(s);
	}
}
