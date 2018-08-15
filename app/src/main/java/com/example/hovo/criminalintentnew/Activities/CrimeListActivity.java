package com.example.hovo.criminalintentnew.Activities;

import android.content.Intent;
import android.support.v4.app.Fragment;


import com.example.hovo.criminalintentnew.Activities.Fragments.CrimeFragment;
import com.example.hovo.criminalintentnew.Activities.Fragments.CrimeListFragment;
import com.example.hovo.criminalintentnew.Activities.Model.Crime;
import com.example.hovo.criminalintentnew.R;

public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.CallBacks,CrimeFragment.CallBacks{

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
    @Override
    protected int getLayoutResId(){
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if(findViewById(R.id.detail_fragment_container) == null){
            Intent intent = CrimePagerActivity.newIntent(this,crime.getId());
            startActivity(intent);
        }else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container,newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment =(CrimeListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
