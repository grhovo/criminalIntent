package com.example.hovo.criminalintentnew.Activities.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hovo.criminalintentnew.Activities.database.CrimeBaseHelper;
import com.example.hovo.criminalintentnew.Activities.database.CrimeCursorWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.hovo.criminalintentnew.Activities.database.CrimeDbSchema.*;

public class CrimeLab {
    private static CrimeLab ourInstance;
    private Context mContext;
    private SQLiteDatabase mDatabase;


    public static CrimeLab getInstance(Context context) {
        if(ourInstance == null){
            ourInstance = new CrimeLab(context);
        }
        return ourInstance;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();

    }
    public void addCrime(Crime c){
        ContentValues values = getContentValues(c);

        mDatabase.insert(CrimeTable.NAME,null,values);
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursorWrapper = queryCrimes(null,null);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()){
                crimes.add(cursorWrapper.getCrime());
                cursorWrapper.moveToNext();
            }
        }finally {
            cursorWrapper.close();
        }
        return crimes;
    }

    public void removeItem(Crime crime){
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + " = ?",new String[]{crime.getId().toString()});
    }

    public Crime getCrime(UUID id){
        CrimeCursorWrapper cursor = queryCrimes(CrimeTable.Cols.UUID + " = ?",new String[] { id.toString() } );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();    }
    }
    private static ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID,crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE,crime.getTitle());
        values.put(CrimeTable.Cols.DATE,crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED,crime.isSolved()?1:0);
        values.put(CrimeTable.Cols.HOUR,crime.getHour());
        values.put(CrimeTable.Cols.MINUTE,crime.getMinute());

        return values;
    }

    public void updateCrime(Crime crime){
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME,values,CrimeTable.Cols.UUID + " = ?",new String[]{uuidString});
    }
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null,
                null
        );

        return new CrimeCursorWrapper(cursor);
    }
}
