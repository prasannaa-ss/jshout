package com.ourbunny.jshout.testing;

import java.io.*;
import com.ourbunny.jshout.*;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StreamConfig sc = new StreamConfig();
		sc.setHostname("192.168.1.35");
		sc.setHostport(8000);
		sc.setUsername("source");
		sc.setPassword("hackme");
		sc.setMount("bob");
		sc.setName("helllooo");
		
		Shouter sout = new Shouter(sc);
		
		//new Thread(sout).start();
		
		File f1 = new File("cake.mp3");
		
		try {
			MP3Audio m3a = new MP3Audio(f1);
			
			if (!sout.connect()) {
				System.out.println("connection error");
			}
			Segment sendData;
			
			System.out.println("sending data...");
			int i = 0;
			while ((sendData = m3a.getNextFrame()) != null) {
				sout.send(sendData);
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
			System.out.println(e);
		}

	}

}
