package com.gmoawirt.flightassistant;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

public class LogListFragment extends ListFragment {

	private LogManager datasource;
	ArrayList<Flightlog> valuesForDate;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());

		Log.i("LogListFragment", "(onCreate)");
		Log.i("LogListFragment", "Given Timestamp: " + getArguments().getString("log_day"));
		
		datasource = new LogManager(getActivity());
		setListAdapter(makeAdapter());
	}
	
	private LogAdapter makeAdapter(){			
		datasource.open();
		valuesForDate = (ArrayList<Flightlog>) datasource.getLogsFromDate(Long.parseLong(getArguments().getString("log_day")));
		datasource.close();
		return new LogAdapter(valuesForDate);	
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.delete_log, menu);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		//Log.i("FragmentList", "Item clicked: " + id);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId()) {
		case R.id.delete_log:
			deleteLog(info.id);
			return true;
		case R.id.cancel:
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void deleteLog(long id) {		
		LogManager lm = new LogManager(getActivity().getApplicationContext());
		lm.open();
		lm.deleteLog(valuesForDate.get((int)id).getId());
		lm.close();
		setListAdapter(makeAdapter());		
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i("LogListFragment", "(onAttach)");
	}
}