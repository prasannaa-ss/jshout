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

package com.ourbunny.jshout.testing;

import java.io.*;
import com.ourbunny.jshout.*;
import com.ourbunny.jshout.FileReader;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StreamConfig sc = new StreamConfig();
		sc.setHostname("172.26.17.19");
		sc.setHostport(8000);
		sc.setUsername("source");
		sc.setPassword("hackme");
		sc.setMount("bob");
		sc.setName("helllooo");
		
		Shouter sout = new Shouter(sc);
		
		File f1 = new File("cake.mp3");
		
		try {
			FileReader m3a = new MP3Reader(f1);
			
			if (!sout.connect()) {
				System.out.println("connection error");
			}
			
			System.out.println("sending data...");
			int i = 0;
			while (m3a.hasNext()) {
				sout.send(m3a.next());
				if (i < 40) {
					i++;
					System.out.print('.');
				} else {
					i = 0;
					System.out.println('.');
				}
			}
			System.out.println("done!");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
