package com.gmoawirt.flightassistant;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;


public  class LogListFragment extends ListFragment {
	
	private LogManager datasource;
	private LogAdapter adapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Log.i("LogListFragment", "(onCreate)");		
		Log.i("LogListFragment", "Given Timestamp: " + getArguments().getString("log_day"));
		
		//Bundle bundle = getArguments();
		//Log.i("LogViewer", "Bundle = " + bundle.getString("log_day"));			
		//Log.i("LogViewer", "Rewriting with Date = " + tab.getTag().toString());
		
		datasource = new LogManager(getActivity());
		datasource.open();		
		ArrayList<Flightlog> valuesForDate = (ArrayList<Flightlog>) datasource.getLogsFromDate(Long.parseLong(getArguments().getString("log_day")));
		datasource.close();
		adapter = new LogAdapter(valuesForDate);
		setListAdapter(adapter);
		//update();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.i("FragmentList", "Item clicked: " + id);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i("LogListFragment", "(onAttach)");	
		//setListAdapter(adapter);
	}

	public void update() {			
		//adapter.notifyDataSetChanged();
		setListAdapter(adapter);
	}
}