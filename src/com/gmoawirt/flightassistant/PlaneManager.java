package com.gmoawirt.flightassistant;

import java.util.ArrayList;

import android.util.Log;

public class PlaneManager {
	
	private ArrayList<Plane> planes = new ArrayList<Plane>();;
	private Plane plane;
	private Plane activePlane;
	private static final PlaneManager planeManagerInstance = new PlaneManager();
	
	private PlaneManager(){		
	}
	
	public static synchronized PlaneManager getInstance(){
		return planeManagerInstance;
	}
	
	public void addPlane(boolean airborne, String name, double takeoffGroundspeed, double takeoffDeltaAltitude, double landingGroundspeed, double landingDeltaMargin){		
				
		plane = new Plane(airborne, name, takeoffGroundspeed, takeoffDeltaAltitude, landingGroundspeed, landingDeltaMargin);
		planes.add(plane);
		Log.i("PlaneManager","Added Plane");
		
	}
	
	public void setupPlane(){
		//set Plane from Plane List (Options)
		//TODO: Use data from options
		setActivePlane(0);
		
	}
	
	private void setActivePlane(int planeId){
		//set Plane from Plane List (Options)
		//TODO: Use data from options
		activePlane = planes.get(planeId);
		Log.i("PlaneManager", "SetActivePlane to ID " + planeId);
	}
	
	public Plane getActivePlane(){
		return activePlane;
	}
	
	private int getPlaneCount(){
		return planes.size();
	}
	
	private Plane getPlaneById(int planeId){		
		
		return planes.get(planeId);
		
	}

}
