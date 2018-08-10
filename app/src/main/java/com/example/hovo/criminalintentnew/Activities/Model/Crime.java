package com.example.hovo.criminalintentnew.Activities.Model;

import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private boolean mRequiredPolice;
    private int hour;
    private int minute;


    public boolean isRequiredPolice() {
        return mRequiredPolice;
    }

    public void setRequiredPolice(boolean requiredPolice) {
        mRequiredPolice = requiredPolice;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public Crime(){
        mId = UUID.randomUUID();
        mDate = new Date();
        hour = 0;
        minute = 0;

    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }
}
