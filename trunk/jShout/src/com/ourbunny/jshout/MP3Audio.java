package com.ourbunny.jshout;

import javazoom.jl.decoder.*;

import java.io.*;

public class MP3Audio {
	private InputStream is;
	
	public MP3Audio(File f) throws FileNotFoundException {
		is = new BufferedInputStream(new FileInputStream(f));
		
	}
	
	public Segment getNextFrame() {
		try {
			byte[] head = new byte[4];
			is.read(head);
			
			InputStream bais = new ByteArrayInputStream(head);
			Bitstream bs = new Bitstream(bais);
			
			Header h = bs.readFrame();
			
			byte[] frame = new byte[4 + h.calculate_framesize()];
			for (int i = 0; i < 4; i++) {
				frame[i] = head[i];
			}
			
			
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
