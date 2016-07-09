package com.dadc.taskmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


/**
 * Created by bomko on 27.05.16.
 */
public class Task extends RealmObject implements Parcelable, Comparable<Task> {


    @PrimaryKey
    private String mId;

    private String mTitle;
    private String mDescription;
    private String mUrl;
    private long mStartDateTask;
    private long mStopDateTask;
    private long maxTime;

    private long mPauseStart;
    private long mPauseStop;
    private long mPauseDifferent;

    private int mTaskColor;
    private boolean isSelected;


    private String mButtonType;

    public Task() {
    }

    public Task(String mId, String mTitle, String mDescription, int mTaskColor, long mStartDateTask, long mStopDateTask, long maxTime, String mButtonType, String mUrl) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mTaskColor = mTaskColor;
        this.mStartDateTask = mStartDateTask;
        this.mStopDateTask = mStopDateTask;
        this.maxTime = maxTime;
        this.mButtonType = mButtonType;
        this.mUrl=mUrl;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public long getStartDateTask() {
        return mStartDateTask;
    }

    public void setStartDateTask(long mStartDateTask) {
        this.mStartDateTask = mStartDateTask;
    }

    public long getStopDateTask() {
        return mStopDateTask;
    }

    public void setStopDateTask(long mStopDateTask) {
        this.mStopDateTask = mStopDateTask;
    }

    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public int getTaskColor() {
        return mTaskColor;
    }

    public void setTaskColor(int mTaskColor) {
        this.mTaskColor = mTaskColor;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public long getPauseStart() {
        return mPauseStart;
    }

    public void setPauseStart(long pauseStart) {
        mPauseStart = pauseStart;
    }

    public long getPauseStop() {
        return mPauseStop;
    }

    public void setPauseStop(long pauseStop) {
        mPauseStop = pauseStop;
    }

    public long getPauseDifferent() {
        return mPauseDifferent;
    }

    public void setPauseDifferent(long pauseDifferent) {
        mPauseDifferent = pauseDifferent;
    }

    public String getButtonType() {
        return mButtonType;
    }

    public void setButtonType(String buttonType) {
        mButtonType = buttonType;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
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
        mId = in.readString();
        mTitle = in.readString();
        mDescription = in.readString();
        mTaskColor = in.readInt();
        mStartDateTask = in.readLong();
        mStopDateTask = in.readLong();
        maxTime = in.readLong();
        mButtonType = in.readString();
        mUrl = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeInt(mTaskColor);
        dest.writeLong(mStartDateTask);
        dest.writeLong(mStopDateTask);
        dest.writeLong(maxTime);
        dest.writeString(mButtonType);
        dest.writeString(mUrl);
    }


    public static Comparator<Task> TaskTitleComparator = new Comparator<Task>() {

        public int compare(Task task1, Task task2) {
            return task1.getTitle().compareTo(task2.getTitle());
        }
    };

    public static Comparator<Task> TaskReverseTitleComparator = new Comparator<Task>() {

        public int compare(Task task1, Task task2) {
            return task2.getTitle().compareTo(task1.getTitle());
        }
    };

    public static Comparator<Task> TaskDateComparator = new Comparator<Task>() {

        public int compare(Task task1, Task task2) {
            return Long.compare(task1.getStartDateTask(), task2.getStartDateTask());
        }
    };

    public static Comparator<Task> TaskReverseDateComparator = new Comparator<Task>() {

        public int compare(Task task1, Task task2) {
            return Long.compare(task2.getStartDateTask(), task1.getStartDateTask());
        }
    };

    @Override
    public int compareTo(Task compareTask) {

        return 0;

    }


}
