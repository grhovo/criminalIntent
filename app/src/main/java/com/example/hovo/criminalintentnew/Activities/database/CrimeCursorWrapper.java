package com.example.hovo.criminalintentnew.Activities.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.hovo.criminalintentnew.Activities.Model.Crime;

import java.util.Date;
import java.util.UUID;

import static com.example.hovo.criminalintentnew.Activities.database.CrimeDbSchema.*;

public class CrimeCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    public Crime getCrime(){
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.DATE));
        int hour = getInt(getColumnIndex(CrimeTable.Cols.HOUR));
        int minute = getInt(getColumnIndex(CrimeTable.Cols.MINUTE));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved!=0);
        crime.setHour(hour);
        crime.setMinute(minute);
        crime.setSuspect(suspect);

        return crime;
    }
}
