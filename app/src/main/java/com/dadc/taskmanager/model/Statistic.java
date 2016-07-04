package com.dadc.taskmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bomko on 03.07.16.
 */
public class Statistic implements Parcelable {

    private String mId;
    private String mTitle;
    private int mMonth;
    private long mDifferentTime;

    public Statistic(String mId, String mTitle, int mMonth, long mDifferentTime) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mMonth = mMonth;
        this.mDifferentTime = mDifferentTime;
    }


    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getMonth() {
        return mMonth;
    }

    public void setMonth(int month) {
        mMonth = month;
    }

    public long getDifferentTime() {
        return mDifferentTime;
    }

    public void setDifferentTime(long differentTime) {
        mDifferentTime = differentTime;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeInt(mMonth);
        dest.writeLong(mDifferentTime);
    }

    protected Statistic(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mMonth = in.readInt();
        mDifferentTime = in.readLong();
    }

    public static final Creator<Statistic> CREATOR = new Creator<Statistic>() {
        @Override
        public Statistic createFromParcel(Parcel in) {
            return new Statistic(in);
        }

        @Override
        public Statistic[] newArray(int size) {
            return new Statistic[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
