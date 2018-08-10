package com.example.hovo.criminalintentnew.Activities.Model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab ourInstance;
    private List<Crime> mCrimes;
    public static CrimeLab getInstance(Context context) {
        if(ourInstance == null){
            ourInstance = new CrimeLab(context);
        }
        return ourInstance;
    }

    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();
    }
    public void addCrime(Crime c){
        mCrimes.add(c);
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public void removeItem(Crime crime){
        if(mCrimes.contains(crime)) {
            mCrimes.remove(crime);
        }
    }

    public Crime getCrime(UUID id){
        for (Crime crime: mCrimes
             ) {
            if(crime.getId().equals(id)){
                return crime;
            }
        }
        return null;
    }
}
