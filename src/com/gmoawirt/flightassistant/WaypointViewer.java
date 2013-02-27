package com.gmoawirt.flightassistant;

import java.util.ArrayList;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class WaypointViewer extends ListActivity implements EditDeleteDialogFragment.EditDeleteDialogListener, DeleteDialogFragment.DeleteDialogListener {
	private WaypointManager datasource = new WaypointManager(this);
	private String orderBy;
	private ArrayList<Waypoint> values;

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
			}
		} else {
			orderBy = MySQLiteHelper.COLUMN_AIRFIELD;
		}

		datasource.open();
		values = (ArrayList<Waypoint>) datasource.getAllWaypoints(orderBy);
		WaypointAdapter adapter = new WaypointAdapter(values);
		setListAdapter(adapter);
		datasource.close();
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {

		// Toast.makeText(getApplicationContext(),
		// values.get(position).getIcao() + " Longitude: " +
		// values.get(position).getLongitude(), Toast.LENGTH_LONG).show();

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

		Toast.makeText(getApplicationContext(), "Clicked on position " + which, Toast.LENGTH_LONG).show();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.waypoint_viewer, menu);
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
			Intent intent2 = new Intent(this, WaypointViewer.class);
			intent2.putExtra("orderby", MySQLiteHelper.COLUMN_ICAO);
			finish();
			startActivity(intent2);
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

}