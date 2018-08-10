package com.example.hovo.criminalintentnew.Activities.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import com.example.hovo.criminalintentnew.R;

public class TimePickerFragment extends DialogFragment {
    private TimePicker mTimePicker;
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour = getArguments().getInt(HOUR);
        int minute = getArguments().getInt(MINUTE);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time,null);
        mTimePicker = view.findViewById(R.id.dialog_time_picker);

        return new AlertDialog.Builder(getActivity()).setTitle("Time")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int hour = mTimePicker.getHour();
                        int minute = mTimePicker.getMinute();
                        sendResult(Activity.RESULT_OK,hour,minute);
                    }
                })
                .setView(mTimePicker)
                .create();
    }

    public static TimePickerFragment newInstance(int hour,int minute) {
        Bundle args = new Bundle();
        args.putInt(HOUR,hour);
        args.putInt(MINUTE,minute);

        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setArguments(args);
        return timePickerFragment;
    }

    private void sendResult(int resultCode,int hour,int minute) {
        if(getTargetFragment() == null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(HOUR,hour);
        intent.putExtra(MINUTE,minute);

        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
