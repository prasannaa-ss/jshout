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
	
	/**
	 * Create a StreamSocket from a Socket.
	 * 
	 * The StreamSocket wrapper makes it easier to read and write data
	 * to the Socket.
	 * 
	 * @param s The Socket to build the StreamSocket off of
	 * @throws IOException
	 * @throws SocketException
	 */
	public StreamSocket(Socket s) throws IOException, SocketException {
		socket = s;
		socket.setTcpNoDelay(true);		// throws SocketException
		
		br = new BufferedReader(new InputStreamReader(s.getInputStream()));		// throws IOException
		os = s.getOutputStream();		// throws IOException
	}
	
	/**
	 * Close the Streams associated with the StreamSocket
	 */
	public void close() {
		try {
			socket.shutdownOutput();
			br.close();
			os.close();
			socket.close();
		} catch (IOException e) { }
	}
	
	/**
	 * Attempt to read a line from the Stream
	 * @return Data from the Stream or <var>null</var> if there is no data to read
	 */
	public String readLine() {
		try {
			return br.readLine();
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Write a String to the Stream then terminates the line.
	 * @param s The <var>String</var> to be written
	 * @throws IOException
	 */
	public void println(String s) throws IOException {
		os.write(s.getBytes());
		os.write("\r\n".getBytes());
	}
	
	/**
	 * Flushes the output Stream.
	 * Forces any buffered data to be written to the Stream.
	 * @throws IOException
	 */
	public void flush() throws IOException {
		os.flush();
	}
	
	/**
	 * Write bytes to the output Stream
	 * @param s an array of bytes to write to the Stream
	 * @throws IOException
	 */
	public void print(byte[] s) throws IOException {
		os.write(s);
	}
}
