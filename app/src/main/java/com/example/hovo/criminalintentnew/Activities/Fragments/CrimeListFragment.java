package com.example.hovo.criminalintentnew.Activities.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



import com.example.hovo.criminalintentnew.Activities.CrimePagerActivity;
import com.example.hovo.criminalintentnew.Activities.Model.Crime;
import com.example.hovo.criminalintentnew.Activities.Model.CrimeLab;
import com.example.hovo.criminalintentnew.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CrimeListFragment extends Fragment {
    private RecyclerView rv;
    private CrimeAdapter mAdapter;
    private UUID changedItemId;
    private boolean mSubtitleVisible;

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    public static final int REQUEST_CODE_FOR_LIST = 1;



    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_CODE_FOR_LIST) {
                changedItemId = (UUID) data.getSerializableExtra(CrimeFragment.RESULT_ID);
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list,container,false);
        rv = view.findViewById(R.id.crime_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(savedInstanceState!=null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        updateUI();
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE,mSubtitleVisible);
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if(mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            rv.setAdapter(mAdapter);
        }else {
            int channgedItem = 0;
            for (Crime crime:
                 crimes) {
                if(crime.getId() == changedItemId){
                    channgedItem = crimes.indexOf(crime);
                    break;
                }
            }
            mAdapter.notifyItemChanged(channgedItem);
        }
        updateSubtitle();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.getInstance(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(),crime.getId());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getString(R.string.subtitle_format,crimeCount);
        if(!mSubtitleVisible){
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitle;
        private TextView mDate;
        private Crime mCrime;
        private ImageView mImageView;

        private CrimeHolder(LayoutInflater inflater,ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime,parent,false));
            itemView.setOnClickListener(this);
            mTitle = itemView.findViewById(R.id.crime_title);
            mDate = itemView.findViewById(R.id.crime_date);
            mImageView = itemView.findViewById(R.id.crime_solved);
        }

        public void bind(Crime crime){
            mCrime = crime;
            mTitle.setText(mCrime.getTitle());
            String newDate = getString();
            mDate.setText(newDate);
            mImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        public String getString() {
            Date date = mCrime.getDate();
            return DateFormat.getDateInstance(DateFormat.FULL).format(date);
        }

        @Override
        public void onClick(View v) {
             Intent intent = CrimePagerActivity.newIntent(getActivity(),mCrime.getId());
             startActivityForResult(intent,REQUEST_CODE_FOR_LIST);
        }
    }


    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime> mCrimes;



        private CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

    }
}
