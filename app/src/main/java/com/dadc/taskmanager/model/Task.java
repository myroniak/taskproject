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
    private String mFullDate;
    private int mTaskColor;

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_COLOR = "color";
    private static final String KEY_SELECTED = "selected";
    private static final String KEY_DATE = "date";


    long mStartTask;
    private boolean isSelected;


    public Task(String mTitle, String mDescription, int mTaskColor, String mFullDate) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mTaskColor = mTaskColor;
        this.mFullDate=mFullDate;
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

    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(KEY_TITLE, mTitle);
            jsonObject.put(KEY_DESCRIPTION, mDescription);
            jsonObject.put(KEY_COLOR, mTaskColor);
            jsonObject.put(KEY_SELECTED,isSelected);
            jsonObject.put(KEY_DATE, mFullDate);

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
        isSelected = jsonObject.getBoolean(KEY_SELECTED);
        mFullDate = jsonObject.getString(KEY_DATE);
    }

}
