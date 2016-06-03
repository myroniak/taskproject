package com.dadc.taskmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bomko on 27.05.16.
 */
public class Task implements Parcelable {


    private String mTitle;
    private String mDescription;
    private String mFullDate;

    long mStartTask;
    private boolean isSelected;


    public Task(String mTitle, String mDescription) {

        this.mTitle = mTitle;
        this.mDescription = mDescription;

    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getFullDate() {
        return mFullDate;
    }

    public void setFullDate(String time) {
        mFullDate = time;
    }

    public long getStartTask() {
        return mStartTask;
    }

    public void setStartTask(long startTask) {
        mStartTask = startTask;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }


    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    protected Task(Parcel in) {
        mTitle = in.readString();
        mDescription = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mDescription);
    }
}
