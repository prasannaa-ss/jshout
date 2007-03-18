/*
 *	Parts of this file are bassed off of the JRoar source code... GPL rocks
 *	Copyright (C) 2001,2002 ymnk, JCraft,Inc.
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
