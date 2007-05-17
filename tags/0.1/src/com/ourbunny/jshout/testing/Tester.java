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

package com.ourbunny.jshout.testing;

import java.io.*;
import com.ourbunny.jshout.*;
import com.ourbunny.jshout.FileReader;

public class Tester {

	public static void main(String[] args) {		
		try {
			Shouter sout = new Shouter();
			sout.setHostname("192.168.1.35");
			sout.setHostport(8000);
			sout.setUsername("source");
			sout.setPassword("hackme");
			sout.setMount("jShout");
			sout.setName("Java Stream");
			sout.updateMetadata("song", "Tommy Jam");
			
			// load the file
			File f1 = new File("cake.mp3");
			FileReader m3a = new MP3Reader(f1);
			
			// connect to the server
			if (!sout.connect()) {
				System.out.println("connection error");
			}
			
			// send metadata to server
			sout.setMetadata();
			
			// send data
			System.out.println("sending data...");
			while (m3a.hasNext()) {
				sout.send(m3a.next());
				sout.sync();
			}
			
			System.out.println("done!");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
