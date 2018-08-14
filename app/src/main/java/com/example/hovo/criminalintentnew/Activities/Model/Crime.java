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
    private String mSuspect;
    private int hour;
    private int minute;

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
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
        this(UUID.randomUUID());

    }
    public Crime(UUID id){
        mId = id;
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

    public String getPhotoFileName(){
        return "IMG_" + getId().toString() + ".jpg";
    }
}
