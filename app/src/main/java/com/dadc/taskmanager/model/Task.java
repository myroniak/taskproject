package com.dadc.taskmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bomko on 27.05.16.
 */
public class Task implements Parcelable {


    private String mTitle;
    private String mDescription;

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_COLOR = "color";
    private static final String KEY_SELECTED = "selected";
    private static final String KEY_START_TIME_TASK = "start_task";
    private static final String KEY_STOP_TIME_TASK = "stop_task";

    private long mStartTimeTask;
    private long mStopTimeTask;
    private int mTaskColor;
    private boolean isSelected;


    public Task(String mTitle, String mDescription, int mTaskColor, long mStartTimeTask, long mStopTimeTask) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mTaskColor = mTaskColor;
        this.mStartTimeTask = mStartTimeTask;
        this.mStopTimeTask = mStopTimeTask;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public long getStartTimeTask() {
        return mStartTimeTask;
    }

    public void setStartTimeTask(long startTimeTask) {
        mStartTimeTask = startTimeTask;
    }

    public long getStopTimeTask() {
        return mStopTimeTask;
    }

    public void setStopTimeTask(long stopTimeTask) {
        mStopTimeTask = stopTimeTask;
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
        mStartTimeTask = in.readLong();
        mStopTimeTask = in.readLong();
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
        dest.writeLong(mStartTimeTask);
        dest.writeLong(mStopTimeTask);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(KEY_TITLE, mTitle);
            jsonObject.put(KEY_DESCRIPTION, mDescription);
            jsonObject.put(KEY_COLOR, mTaskColor);
            jsonObject.put(KEY_START_TIME_TASK, mStartTimeTask);
            jsonObject.put(KEY_STOP_TIME_TASK, mStopTimeTask);
            jsonObject.put(KEY_SELECTED, isSelected);

            return jsonObject;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Task(JSONObject jsonObject) throws JSONException {

        mTitle = jsonObject.getString(KEY_TITLE);
        mDescription = jsonObject.getString(KEY_DESCRIPTION);
        mTaskColor = jsonObject.getInt(KEY_COLOR);
        mStartTimeTask = jsonObject.getLong(KEY_START_TIME_TASK);
        mStopTimeTask = jsonObject.getLong(KEY_STOP_TIME_TASK);
        isSelected = jsonObject.getBoolean(KEY_SELECTED);
    }

}
