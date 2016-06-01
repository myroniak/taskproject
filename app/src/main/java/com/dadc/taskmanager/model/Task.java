package com.dadc.taskmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bomko on 27.05.16.
 */
public class Task implements Parcelable {

    private String mTitle;
    private String mDescription;

    public Task(String mTitle, String mDescription) {

        this.mTitle = mTitle;
        this.mDescription = mDescription;

    }

    protected Task(Parcel in) {
        mTitle = in.readString();
        mDescription = in.readString();
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

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
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
