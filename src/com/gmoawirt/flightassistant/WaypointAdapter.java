package com.gmoawirt.flightassistant;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

//Manages Waypoints
//Creates new logs and provides datasource for logviewer from sqlite database
public class WaypointAdapter extends BaseAdapter {

	private ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();

	public WaypointAdapter(ArrayList<Waypoint> waypoints) {
		this.waypoints = waypoints;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return waypoints.size();
	}

	@Override
	public Object getItem(int index) {
		// TODO Auto-generated method stub
		return waypoints.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int index, View view, ViewGroup parent) {

		if (view == null) {

			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.waypoint_list_item, parent, false);

		}

		Waypoint waypoint = waypoints.get(index);
		
		//ICAO, Airfieldname

		TextView textViewTime = (TextView) view.findViewById(R.id.waypoint_list_icao);
		textViewTime.setText(waypoint.getIcao());

		TextView textViewEvent = (TextView) view.findViewById(R.id.waypoint_list_name);
		textViewEvent.setText(waypoint.getName());

		return view;
	}

}
