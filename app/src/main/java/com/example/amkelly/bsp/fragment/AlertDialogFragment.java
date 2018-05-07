package com.example.amkelly.bsp.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Adam on 04/03/2018.
 *
 * This whole class will be used to create a alert dialog which will warn the user
 */

public class AlertDialogFragment extends DialogFragment
{
    @Override
    //The below function will build the alert dialog
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to save the changes?")
                .setTitle("Are you sure")
                .setCancelable(false)
                //Positive button functions are defined below ie save
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        //Perform save action
                        //This will close the task
                        //Toast creation and show
                        Toast.makeText(getActivity(), "Saved!", Toast.LENGTH_SHORT).show();
                    }
                })
                //Negative button functions are defined below ie cancel
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}
