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

import javazoom.jl.decoder.*;
import java.io.*;

public class MP3Audio {
	private PushbackInputStream is;
	
	public MP3Audio(File f) throws FileNotFoundException {
		is = new PushbackInputStream(new FileInputStream(f));
		
		// add id3 parsing here
		// read 10 bytes
		// else unread 10 bytes
	}
	
	public Segment getNextFrame() {
		try {
			// read 4 bytes of data (Frame Header)
			byte[] head = new byte[4];
			is.read(head);
			
			// create an input stream for the header
			InputStream bais = new ByteArrayInputStream(head);
			Bitstream bs = new Bitstream(bais);
			
			// "read" the frame (find out size)
			Header h = bs.readFrame();
			
			// create an array to hold the frame data
			byte[] frame = new byte[4 + h.calculate_framesize()];
			
			// add header bytes to the frame
			for (int i = 0; i < 4; i++) {
				frame[i] = head[i];
			}
			
			// read the rest of the frame data
			try {
				is.read(frame, 4, (frame.length-4));
			} catch (Exception e) {
				System.out.println(e);
			}
			
			return new Segment(frame, (int)h.ms_per_frame());
		} catch (Exception e) {
			return null;
		}
	}
}
