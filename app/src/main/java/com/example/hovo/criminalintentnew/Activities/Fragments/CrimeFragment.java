package com.example.hovo.criminalintentnew.Activities.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;


import com.example.hovo.criminalintentnew.Activities.CrimePagerActivity;
import com.example.hovo.criminalintentnew.Activities.Model.CrimeLab;
import com.example.hovo.criminalintentnew.R;

import com.example.hovo.criminalintentnew.Activities.Model.Crime;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CrimeFragment extends Fragment {


    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mCheckBox;
    private Button mTimeButton;
    private ImageButton mDelete;


    private static final String DIALOG_DATE = "Dialog Date";
    private static final String ARG_CRIME_ID = "crime id";
    public static final String RESULT_ID = "Crime result";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final String DIALOG_TIME = "DIALOG TIME" ;


    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.getInstance(getActivity()).getCrime(crimeId);
        returnResult();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime,container,false);

        mTitleField = v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = v.findViewById(R.id.crime_date);
        final Date date1 = mCrime.getDate();
        String date = DateFormat.getDateInstance(DateFormat.FULL).format(date1);
        updateDate(date);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(date1);
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
                dialog.show(fm,DIALOG_DATE);
            }
        });

        final int hour = mCrime.getHour();
        final int minute = mCrime.getMinute();
        mTimeButton = v.findViewById(R.id.crime_time);
        updateTime(hour,minute);
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(hour,minute);
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_TIME);
                dialog.show(fm,DIALOG_TIME);
            }
        });

        mCheckBox = v.findViewById(R.id.crime_solved);
        mCheckBox.setChecked(mCrime.isSolved());
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mDelete = v.findViewById(R.id.delete_crime);
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrimeLab.getInstance(getActivity()).removeItem(mCrime);
                getActivity().finish();
            }
        });

        return v;
    }

    private void updateTime(int hour, int minute) {
        String time;
        if(hour < 10){
            time = "0" + hour + ":";
        }else time = hour + ":";
        if(minute < 10){
            time += "0" + minute;
        }else time+=minute;
        mTimeButton.setText(time);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.DATE);
            mCrime.setDate(date);
            String date1 = DateFormat.getDateInstance(DateFormat.FULL).format(date);
            updateDate(date1);
        }
        if(requestCode == REQUEST_TIME){
            int hour = data.getIntExtra(TimePickerFragment.HOUR,mCrime.getHour());
            int minute = data.getIntExtra(TimePickerFragment.MINUTE,mCrime.getMinute());
            mCrime.setHour(hour);
            mCrime.setMinute(minute);
            updateTime(hour,minute);

        }
    }

    private void updateDate(String date1) {
        mDateButton.setText(date1);
    }

    public void returnResult(){
        Intent intent = new Intent();
        intent.putExtra(RESULT_ID,mCrime.getId());
        getActivity().setResult(Activity.RESULT_OK,intent);
    }
}
