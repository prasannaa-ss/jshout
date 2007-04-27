package com.ourbunny.jshout;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;


// TODO: add id3 parsing here?
// read 10 bytes
// else unread 10 bytes
// http://jicyshout.sourceforge.net/oreilly-article/java-streaming-mp3-pt2/java-streaming-mp3-pt2.html

public class MP3Reader extends FileReader {
	private Segment carryOut;
	
	/**
	 * Create a new MP3Reader
	 * @param f the File to read from (should be an MP3)
	 * @param size the size (in bytes) of the Segments
	 * @throws FileNotFoundException
	 */
	public MP3Reader(File f, int size) throws FileNotFoundException {
		super(f, size);
		
		carryOut = new Segment(new byte[0], 0);
		
		this.is = new PushbackInputStream(new FileInputStream(f));
		this.toReturn = createNextSegment();
	}

	/**
	 * Create a new MP3Reader object with size = 4096 bytes
	 * @param f the File to read from (should be an MP3)
	 * @throws FileNotFoundException
	 */
	public MP3Reader(File f) throws FileNotFoundException {
		super(f);
		
		carryOut = new Segment(new byte[0], 0);
		
		this.is = new PushbackInputStream(new FileInputStream(f));
		this.toReturn = createNextSegment();
	}

	/**
	 * Returns the next full Segment from the source
	 */
	@Override
	public Segment next() {
		if (this.hasNext()) {
			// save the segment to return
			Segment returnMe = this.toReturn;
			
			// build the next one (if we can)
			this.toReturn = this.createNextSegment();
			
			// return the segment
			return returnMe;
		} else {
			throw new NoSuchElementException();
		}
	}
	
	/**
	 * Returns the next full Segment from the source
	 */
	private Segment createNextSegment() {
		// first retrieve the carry in stuff
		byte[] carryInData = this.carryOut.getData();
		long carryInTime = this.carryOut.getTime();
		
		
		// create the next segment
		ArrayList<Segment> nextFrames = new ArrayList<Segment>();
		
		// initialize the number of bytes needed (j) and the size of the loaded
		// frames with the carry in data
		int j = this.size - carryInData.length;			// bytes needed
		int frameSize = carryInData.length;				// basically the complement of j
		
		// load new frames until j > 0, no more bytes are needed
		// to create the Segment
		while (j > 0) {
			Segment addMe = this.getNextFrame();
			// check if there was a next frame;
			if (addMe == null) {
				if (nextFrames.size() == 0) {
					// no carry in data
					// no added frame data
					// nothing to make a packet with
					return null;
				} else {
					// no more frames to load
					// break this loop and build the segment
					// the segment will be less than the prefered size
					break;
				}
			}
			// save the frame
			nextFrames.add(addMe);
			
			// update the number of bytes still required
			j = j - addMe.getSize();
			
			// update the frameSize
			frameSize += addMe.getSize();
		}
		
		// build the Segment
		// if need be, make a carry out
		if (frameSize > this.size) {
			// need to make a carry out
			
			// Segment parameters
			byte[] theData = new byte[this.size];
			long theTime = 0;
			
			// index for adding data to theData
			int i = 0;
			
			// add from the previous carry out
			if (carryInData != null) {
				theTime += carryInTime;
				System.arraycopy(carryInData, 0, theData, 0, carryInData.length);
				i = carryInData.length;
			}
			
			// add each frame to the new Segment
			for (Segment frame : nextFrames) {
				if (frame.getSize() <= this.size - i) {
					// frame size is < or = to what is needed, just add it
					System.arraycopy(frame.getData(), 0, theData, i, frame.getSize());
					
					// update time
					theTime += frame.getTime();
					
					// update position
					i += frame.getSize();
				} else {
					// frame was more than what was needed
					System.arraycopy(frame.getData(), 0, theData, i, this.size - i);
					
					// build the carry out data array
					byte[] carryOutData = new byte[frame.getSize() - (this.size - i)];
					System.arraycopy(frame.getData(), this.size - i, carryOutData, 0, carryOutData.length);
					
					// figure out timing
					long carryOutTime = (long)(frame.getTime() * (carryOutData.length / (double)frame.getSize()));
					
					// update time
					theTime += (frame.getTime() - carryOutTime);
					
					// copy the rest into carryOut
					this.carryOut = new Segment(carryOutData, carryOutTime);
				}	
			}
			
			// return that motha!
			return new Segment(theData,theTime);
		} else {
			// fill up as much as possible
			// don't make a carry out (no overflow)
			
			// Segment parameters
			byte[] theData = new byte[frameSize];
			long theTime = 0;
			
			// index for adding data to theData
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
					System.arraycopy(frame.getData(), 0, theData, i, frame.getSize());
					
					// update time
					theTime += frame.getTime();
					
					// update position
					i += frame.getSize();
			}
			
			// return that motha!
			return new Segment(theData,theTime);
		}
	}
	
	/**
	 * Returns the next MP3 frame from the source
	 */
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
			
			// return that motha!
			return new Segment(frame, (int)h.ms_per_frame());
			
		} catch (Exception e) {
			// something bad happened
			return null;
		}
	}

}
