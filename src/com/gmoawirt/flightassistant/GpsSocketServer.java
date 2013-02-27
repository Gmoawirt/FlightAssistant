package com.gmoawirt.flightassistant;

import java.io.IOException;

public class GpsSocketServer {

	private static final GpsSocketServer gpsSocketServerInstance = new GpsSocketServer();

	private GpsSocketServer() {

		ServerThread t = new ServerThread();
		t.start();

	}
	
	public static GpsSocketServer getInstance(){
		return gpsSocketServerInstance;
	}

}
