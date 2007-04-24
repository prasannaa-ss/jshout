package com.ourbunny.jshout;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;


// TODO: add id3 parsing here?
// read 10 bytes
// else unread 10 bytes
// http://jicyshout.sourceforge.net/oreilly-article/java-streaming-mp3-pt2/java-streaming-mp3-pt2.html

public class MP3Reader extends FileReader {
	private Segment carryOut;
	
	public MP3Reader(File f, int size) throws FileNotFoundException {
		super(f, size);
		
		this.is = new PushbackInputStream(new FileInputStream(f));
		this.toReturn = createNextSegment();
	}

	public MP3Reader(File f) throws FileNotFoundException {
		this(f, 4096);
	}

	@Override
	public Segment next() {
		// save the segment to return
		Segment returnMe = this.toReturn;
		
		// build the next one (if we can)
		this.createNextSegment();
		
		// return the segment
		return returnMe;
	}
	
	private Segment createNextSegment() {
		// first retrieve the carry in stuff
		byte[] carryInData = this.carryOut.getData();
		long carryInTime = this.carryOut.getTime();
		
		
		// create the next segment
		ArrayList<Segment> nextFrames = new ArrayList<Segment>();
		
		int j = this.size - carryInData.length;			// bytes needed
		int frameSize = carryInData.length;				// basically the complement of j
		
		// keep getting frames
		while (j < 0) {
			Segment addMe = this.getNextFrame();
			// check if there was a next frame;
			if (addMe == null) {
				if (nextFrames.size() == 0) {
					return null;
				} else {
					// end of file reached
					break;
				}
			}
			nextFrames.add(addMe);
			j = j - addMe.getSize();
			frameSize += addMe.getSize();
		}
		
		if (frameSize >= this.size) {
			// might need to make a carry out
			
			byte[] theData = new byte[this.size];
			long theTime = 0;
			
			int i = 0;
			
			// add from the previous carry out
			if (carryInData != null) {
				theTime += carryInTime;
				System.arraycopy(carryInData, 0, theData, 0, carryInData.length);
				i = carryInData.length;
			}
			
			// loop building the next segment
			for (Segment frame : nextFrames) {
				if (frame.getSize() <= this.size - i) {
					// frame size is < or = to what is needed, just add it
					System.arraycopy(frame, 0, theData, i, frame.getSize());
					
					// update time
					theTime += frame.getTime();
					
					// update position
					i += frame.getSize();
				} else {
					// frame was more than what was needed
					System.arraycopy(frame, 0, theData, i, this.size - i);
					
					byte[] carryOutData = new byte[frame.getSize() - (this.size - i)];
					System.arraycopy(frame, this.size - i, carryOutData, 0, carryOutData.length);
					
					long carryOutTime = (long)(frame.getTime() * (carryOutData.length / (double)frame.getSize()));
					
					// update time
					theTime += (frame.getTime() - carryOutTime);
					
					// copy the rest into carryOut
					this.carryOut = new Segment(carryOutData, carryOutTime);
				}	
			}
			
			return new Segment(theData,theTime);
		} else {
			// fill up as much as possible, no carry out
			
			byte[] theData = new byte[frameSize];
			long theTime = 0;
			
			int i = 0;
			
			// add from the previous carry out
			if (carryInData != null) {
				theTime += carryInTime;
				System.arraycopy(carryInData, 0, theData, 0, carryInData.length);
				i = carryInData.length;
			}
			
			// loop building the next segment
			for (Segment frame : nextFrames) {
					// frame size is < or = to what is needed, just add it
					System.arraycopy(frame, 0, theData, i, frame.getSize());
					
					// update time
					theTime += frame.getTime();
					
					// update position
					i += frame.getSize();
			}
			return new Segment(theData,theTime);
		}
	}
	
	private Segment getNextFrame() {
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
