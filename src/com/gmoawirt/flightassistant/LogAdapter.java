package com.gmoawirt.flightassistant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

//Manages Logs
//Creates new logs and provides datasource for logviewer from sqlite database
public class LogAdapter extends BaseAdapter {

	private ArrayList<Flightlog> logs = new ArrayList<Flightlog>();

	public LogAdapter(ArrayList<Flightlog> logs) {
		this.logs = logs;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return logs.size();
	}

	@Override
	public Object getItem(int index) {
		// TODO Auto-generated method stub
		return logs.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int index, View view, ViewGroup parent) {

		if (view == null) {

			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.view_list_item, parent, false);

		}

		Flightlog log = logs.get(index);

		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
		String time = timeFormat.format(new Date(log.getTimestamp()));

		TextView textViewTime = (TextView) view.findViewById(R.id.view_list_time);
		textViewTime.setText(time + " ");

		TextView textViewEvent = (TextView) view.findViewById(R.id.view_list_event);
		textViewEvent.setText(log.getEvent() + " ");

		TextView textViewWaypoint = (TextView) view.findViewById(R.id.view_list_waypoint);
		textViewWaypoint.setText(log.getWaypoint());

		return view;
	}

}
