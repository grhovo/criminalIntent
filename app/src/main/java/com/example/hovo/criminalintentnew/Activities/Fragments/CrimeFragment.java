package com.example.hovo.criminalintentnew.Activities.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;


import com.example.hovo.criminalintentnew.Activities.Model.CrimeLab;
import com.example.hovo.criminalintentnew.Activities.PictureUtils;
import com.example.hovo.criminalintentnew.R;

import com.example.hovo.criminalintentnew.Activities.Model.Crime;

import java.io.File;
import java.net.URI;
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
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageButton mCameraButton;
    private ImageView mPhoto;
    private File mPhotoFile;


    private static final String DIALOG_DATE = "Dialog Date";
    private static final String ARG_CRIME_ID = "crime id";
    public static final String RESULT_ID = "Crime result";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final String DIALOG_TIME = "DIALOG TIME" ;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;


    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.getInstance(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.getInstance(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.getInstance(getActivity()).getPhotoFile(mCrime);
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

        mReportButton = v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = ShareCompat.IntentBuilder.from(getActivity())
                        .setText(getCrimeReport())
                        .setSubject("CriminalIntent Crime report")
                        .setType("text/plain")
                        .setChooserTitle(R.string.send_report)
                        .createChooserIntent();

                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });
        if(mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
        }

        //проверка есть ли контактное приложение
        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null){
            mSuspectButton.setEnabled(false);
        }

        mPhoto = v.findViewById(R.id.crime_photo);
        updatePhotoView();

        mCameraButton = v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mCameraButton.setEnabled(canTakePhoto);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.hovo.criminalintentnew.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity().getPackageManager()
                        .queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities
                        ) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage,REQUEST_PHOTO);
            }
        });
        return v;
    }
    private void updatePhotoView(){
        if(mPhotoFile==null||!mPhotoFile.exists()){
            mPhoto.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());
            mPhoto.setImageBitmap(bitmap);
        }
    }

    private String getCrimeReport(){
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        }else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE,MMM dd";
        String dateString = android.text.format.DateFormat.format(dateFormat,mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if(suspect == null){
            suspect = getString(R.string.crime_report_no_suspect);
        }else {
            suspect = getString(R.string.crime_report_suspect,suspect);
        }

        String report = getString(R.string.crime_report,mCrime.getTitle()
        ,dateString,solvedString,suspect
        );
        return report;
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
        else if(requestCode == REQUEST_TIME){
            int hour = data.getIntExtra(TimePickerFragment.HOUR,mCrime.getHour());
            int minute = data.getIntExtra(TimePickerFragment.MINUTE,mCrime.getMinute());
            mCrime.setHour(hour);
            mCrime.setMinute(minute);
            updateTime(hour,minute);

        }else if(requestCode == REQUEST_CONTACT && data != null){
            Uri contactUri = data.getData();
            // Определение полей, значения которых должны быть
            // возвращены запросом.
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            // Выполнение запроса - contactUri здесь выполняет функции
            // условия "where"
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri,queryFields,null,null,null);
            try{
                // Проверка получения результатов
                if (c.getCount() == 0) {
                    return;
                }
                // Извлечение первого столбца данных - имени подозреваемого.
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                Log.i("suspect",mCrime.getSuspect());
                mSuspectButton.setText(suspect);
            }finally {
                c.close();
            }
        } else if(requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.example.hovo.criminalintentnew.fileprovider",mPhotoFile);
            getActivity().revokeUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
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
