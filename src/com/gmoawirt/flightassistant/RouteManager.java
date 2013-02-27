package com.gmoawirt.flightassistant;

import java.util.ArrayList;

import android.util.Log;

public class RouteManager {

	private ArrayList<Route> routes;
	private Route route;
	private Plane plane = PlaneManager.getInstance().getActivePlane();
	private Route activeRoute;
	private static final RouteManager routeManagerInstance = new RouteManager();

	private RouteManager() {
		routes = new ArrayList<Route>();
	}

	public static RouteManager getInstance() {
		return routeManagerInstance;
	}

	public void addRoute(String name, Waypoint org, Waypoint dest) {

		route = new Route(name, org, dest);
		routes.add(route);
	}

	public void setupRoute() {
		// set Route from Route List (Options)
		// TODO: Use data from options
		setActiveRoute(0);
		Log.i("RouteManager", "Departure Airfield: " + activeRoute.getWaypoints().get(0).getName());

	}

	private void setActiveRoute(int routeId) {
		// set Route from Route List (Options)
		// TODO: Use data from options
		activeRoute = routes.get(routeId);
		Log.i("RouteManager", "Set active Route to route ID " + routeId);
	}

	public Route getActiveRoute() {
		return activeRoute;
	}

	private Route getRouteById(int planeId) {

		// @TODO: IndexOutOfBoundsExecption ?
		return routes.get(planeId);

	}

}
