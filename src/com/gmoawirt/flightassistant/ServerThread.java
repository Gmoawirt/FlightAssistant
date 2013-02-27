package com.gmoawirt.flightassistant;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class ServerThread extends Thread {
	private Socket client;

	public void run() {
		
		ServerSocket server;
		try {
			server = new ServerSocket(3141);
			Socket s = null;			
			s = server.accept();
			this.client = s;
			
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}		

		Log.i("FlightManager", "Handling Socket " + client.toString());

		ArrayList<Double> input = null;
		ObjectInputStream in = null;

		while (true) {

			try {
				in = new ObjectInputStream(client.getInputStream());
			} catch (StreamCorruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			try {
				input = (ArrayList<Double>) in.readObject();
			} catch (StreamCorruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				Log.i("ServerThread", "IOException e1");
				e1.printStackTrace();
				break;
				// TODO Auto-generated catch block
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Location location = new Location("testProvider");
			location.setLongitude(input.get(0));
			location.setLatitude(input.get(1));
			location.setAltitude(input.get(2));
			location.setSpeed(input.get(3).floatValue());
			location.setTime(System.currentTimeMillis()); 

			ProviderManager.getLocationManager().setTestProviderLocation("testProvider", location);
			//FlightManager.getLocationManager().setTestProviderLocation("testProvider", location);

			// String factor2 = in.nextLine();
			Log.i(this.getClass().getName(), "Socket message: (Longitude) " + input.get(0) +  " (Latitude) " + input.get(1));

		}

	}
}