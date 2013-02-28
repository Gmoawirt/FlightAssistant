package com.gmoawirt.flightassistant;

import java.util.ArrayList;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class WaypointViewer extends ListActivity implements EditDeleteDialogFragment.EditDeleteDialogListener, DeleteDialogFragment.DeleteDialogListener,
		LocationListener {
	private WaypointManager datasource = new WaypointManager(this);	
	private ArrayList<Waypoint> values;
	private Location location;
	WaypointAdapter adapter;
	private String orderBy;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waypoint_viewer);
		setTitle("Edit Waypoints");		
		
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.i(this.getClass().getName(), "(onStart)");			
		
		if (getIntent().getExtras() != null) {
			if (getIntent().getExtras().getString("orderby") != null) {
				orderBy = getIntent().getExtras().getString("orderby");
			} else {
				orderBy = MySQLiteHelper.COLUMN_AIRFIELD;
			}
			
			if (((Location) getIntent().getExtras().get("location") != null) && (location == null)) {
				location = (Location) getIntent().getExtras().get("location");
				Log.i("Waypoint Viewer", "Resetting Location");
			}			
		} else {
			orderBy = MySQLiteHelper.COLUMN_AIRFIELD;
		}
		
		values = getWaypoints(orderBy);		
		adapter = new WaypointAdapter(values);
		setListAdapter(adapter);
		adapter.notifyDataSetChanged();		
	}

	private ArrayList<Waypoint> getWaypoints(String orderBy){

		datasource.open();

		if (orderBy.equals("distance")) {
			if (location != null) {				
				values = GpsHelper.getSortedByDistance(location.getLongitude(), location.getLatitude(), this);
			} else {
				Toast.makeText(getApplicationContext(), "No GPS connection, sorting by ICAO", Toast.LENGTH_LONG).show();
				values = (ArrayList<Waypoint>) datasource.getAllWaypoints(MySQLiteHelper.COLUMN_ICAO);
			}
		} else {
			values = (ArrayList<Waypoint>) datasource.getAllWaypoints(orderBy);
		}
		
		datasource.close();
		Log.i("Waypoint Viewer", "Returning values with orderBy= " + orderBy);
		return values;
		
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {

		DialogFragment edf = new EditDeleteDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString("waypoint_icao", values.get(position).getIcao());
		bundle.putInt("waypoint_position", position);
		edf.setArguments(bundle);
		edf.show(getFragmentManager(), "edit_delete_waypoint");

	}

	@Override
	public void onDialogEditDeleteClick(DialogFragment dialog, int which) {

		switch (which) {
		// Clicked on "Edit Waypoint"
		case 0:
			Intent intent = new Intent(this, WaypointDialogActivity.class);
			intent.putExtra("waypoint_id", values.get(dialog.getArguments().getInt("waypoint_position")).getId());
			startActivity(intent);
			break;

		// Clicked on "Delete Waypoint"
		case 1:
			DialogFragment ddf = new DeleteDialogFragment();
			Bundle bundle = new Bundle();
			bundle.putString("waypoint_icao", values.get(dialog.getArguments().getInt("waypoint_position")).getIcao());
			bundle.putLong("waypoint_id", values.get(dialog.getArguments().getInt("waypoint_position")).getId());
			ddf.setArguments(bundle);
			ddf.show(getFragmentManager(), "delete_waypoint");
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.waypoint_viewer, menu);

		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){		

		// Remove "sort by distance" menu option if there's no location data
		//if ((getIntent().getExtras() == null) || ((Location) getIntent().getExtras().get("location") == null)) {
		if (location == null) {
			Log.i("WaypointViewer", "location == null, removing menu item sort by distance");
			menu.removeItem(R.id.waypoint_sortby_distance);
		}
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {

		// Clicked on add new Waypoint
		case R.id.waypoint_viewer_add:
			Intent intent = new Intent(this, WaypointDialogActivity.class);
			intent.putExtra("isNew", true);
			startActivity(intent);
			return true;

			// Clicked on Sort by ICAO
		case R.id.waypoint_sortby_icao:
			//Intent intent2 = new Intent(this, WaypointViewer.class);
			//intent2.putExtra("orderby", MySQLiteHelper.COLUMN_ICAO);
			//finish();
			//startActivity(intent2);
			orderBy = MySQLiteHelper.COLUMN_ICAO;
			values = getWaypoints(orderBy);
			adapter = new WaypointAdapter(values);
			setListAdapter(adapter);
			return true;

			// Clicked on Sort by Distance
		case R.id.waypoint_sortby_distance:
			orderBy = "distance";
			values = getWaypoints(orderBy);
			adapter = new WaypointAdapter(values);
			setListAdapter(adapter);
			return true;

			// Clicked on Sort by Name
		case R.id.waypoint_sortby_name:
			orderBy = MySQLiteHelper.COLUMN_AIRFIELD;
			values = getWaypoints(orderBy);
			adapter = new WaypointAdapter(values);
			setListAdapter(adapter);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog, long waypointId) {
		datasource.open();
		datasource.deleteWaypointById(waypointId);
		datasource.close();
		dialog.dismiss();
		recreate();
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

}