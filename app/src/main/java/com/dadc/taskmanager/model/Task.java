package com.dadc.taskmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;


/**
 * Created by bomko on 27.05.16.
 */
public class Task implements Parcelable, Comparable<Task> {


    private String mTitle;
    private String mDescription;

    private long mStartDateTask;
    private long mStopDateTask;
    private int mTaskColor;

    public boolean getIsStart() {
        return isStart;
    }

    public void setIsStart(boolean isStart) {
        this.isStart = isStart;
    }

    private boolean isStart;
    private boolean isSelected;

    public Task(String mTitle, String mDescription, int mTaskColor, long mStartDateTask, long mStopDateTask) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mTaskColor = mTaskColor;
        this.mStartDateTask = mStartDateTask;
        this.mStopDateTask = mStopDateTask;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public long getStartDateTask() {
        return mStartDateTask;
    }

    public void setStartDateTask(long startDateTask) {
        mStartDateTask = startDateTask;
    }

    public long getStopDateTask() {
        return mStopDateTask;
    }

    public void setStopDateTask(long stopDateTask) {
        mStopDateTask = stopDateTask;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public int getTaskColor() {
        return mTaskColor;
    }

    public void setTaskColor(int taskColor) {
        mTaskColor = taskColor;
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
        mTaskColor = in.readInt();
        mStartDateTask = in.readLong();
        mStopDateTask = in.readLong();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeInt(mTaskColor);
        dest.writeLong(mStartDateTask);
        dest.writeLong(mStopDateTask);
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
