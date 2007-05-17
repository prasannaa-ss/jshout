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

import java.io.*;
import java.util.Iterator;

public abstract class FileReader implements Iterator<Segment>{
	// the file to read
	protected File f;
	// will read the file
	protected PushbackInputStream is;
	// prefered segment size (in bytes)
	protected int size = 4096;
	// stores the next segment
	protected Segment toReturn;
	
	/**
	 * Create a new FileReader object
	 * @param f the File to read
	 * @param size the size (in bytes) of the Segments
	 * @throws FileNotFoundException
	 */
	public FileReader(File f, int size) throws FileNotFoundException {
		this.f = f;
		this.size = size;
	}
	
	/**
	 * Create a new FileReader object with size = 4096 bytes
	 * @param f the File to read
	 * @throws FileNotFoundException
	 */
	public FileReader(File f) throws FileNotFoundException {
		this.f = f;
		// size = 4096 bytes
	}
	
	/**
	 * Returns true if the FileReader has more Segments
	 */
	public boolean hasNext() {
		if (toReturn == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the next Segment from the FileReader
	 */
	public abstract Segment next();
	
	/**
	 * Not Implemented!
	 */
	public void remove() {
		// not implemented
	}

}