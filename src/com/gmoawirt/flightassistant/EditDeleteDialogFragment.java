package com.gmoawirt.flightassistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


public class EditDeleteDialogFragment extends DialogFragment {
	
	public interface EditDeleteDialogListener {
        public void onDialogEditDeleteClick(DialogFragment dialog, int which);
    }
	
	EditDeleteDialogListener mListener;
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (EditDeleteDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getString("waypoint_icao"))
           .setItems(R.array.edit_array, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int which) {
  
            	   mListener.onDialogEditDeleteClick(EditDeleteDialogFragment.this, which);

               // The 'which' argument contains the index position
               // of the selected item
           }
           });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}