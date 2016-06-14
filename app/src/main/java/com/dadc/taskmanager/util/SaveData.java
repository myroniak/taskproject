package com.dadc.taskmanager.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.adapter.TaskAdapter;
import com.dadc.taskmanager.model.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by bomko on 14.06.16.
 */
public class SaveData {
    private static final String APP_PREFERENCES = "setting";
    private static final String KEY_VALUE = "key";
    private static final String DEFAULT_COLOR_DATE = "colorDateDefault";
    private static final String START_COLOR_DATE = "colorDateStart";
    private static final String END_COLOR_DATE = "colorDateEnd";
    private static final String CHECKED_ITEM = "checked";
    private SharedPreferences.Editor mEditor;
    public SharedPreferences mSettings, mDefaultSetting;

    GsonBuilder builder;
    Context mContext;
    Gson gson;

    public SaveData(Context mContext) {

        this.mContext = mContext;

        builder = new GsonBuilder();
        gson = builder.create();
        mSettings = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        mDefaultSetting = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mSettings.edit();
    }

    public void saveData(ArrayList<Task> taskArrayList) {

        String jsonStr = gson.toJson(taskArrayList);
        mEditor.putString(KEY_VALUE, jsonStr);
        mEditor.apply();

    }

    public ArrayList<Task> loadData() {

        ArrayList<Task> taskArrayList = new ArrayList<>();
        Type collectionType = new TypeToken<ArrayList<Task>>() {
        }.getType();

        if (mSettings.contains(KEY_VALUE)) {
            taskArrayList = gson.fromJson(mSettings.getString(KEY_VALUE, ""), collectionType);
        }

        return taskArrayList;
    }

    public void clearData(ArrayList<Task> mTaskArrayList, TaskAdapter mTaskAdapter) {

        mEditor.remove(KEY_VALUE);
        mEditor.clear();
        mEditor.commit();

        mTaskArrayList.clear();
        mTaskAdapter.notifyDataSetChanged();

    }

    public void chacked(MenuItem item) {

        mEditor.putString(CHECKED_ITEM, item.toString());
        mEditor.commit();
    }

    public MenuItem getItemChecked(Menu menu) {

        MenuItem menuItem = null;

        String isCk = mSettings.getString(CHECKED_ITEM, "");

        if (isCk.contains(mContext.getResources().getString(R.string.sort_a_z))) {
            menuItem = menu.findItem(R.id.action_a_z).setChecked(true);

        } else if (isCk.contains(mContext.getResources().getString(R.string.sort_z_a))) {
            menuItem = menu.findItem(R.id.action_z_a).setChecked(true);

        } else if (isCk.contains(mContext.getResources().getString(R.string.sort_date_ascending))) {
            menuItem = menu.findItem(R.id.action_first_end).setChecked(true);

        } else if (isCk.contains(mContext.getResources().getString(R.string.sort_date_descending))) {
            menuItem = menu.findItem(R.id.action_end_first).setChecked(true);

        }
        return menuItem;
    }

    public int getDateDefaultColor() {
        return mDefaultSetting.getInt(DEFAULT_COLOR_DATE, 0);
    }

    public int getDateStartColor() {
        return mDefaultSetting.getInt(START_COLOR_DATE, 0);
    }

    public int getDateEndColor() {
        return mDefaultSetting.getInt(END_COLOR_DATE, 0);
    }

    public void updateColorDateTask(ArrayList<Task> mTaskArrayList) {


        for (int i = 0; i < mTaskArrayList.size(); i++) {
            if (mTaskArrayList.get(i).getStartDateTask() != 0 && mTaskArrayList.get(i).getStopDateTask() == 0) {
                mTaskArrayList.get(i).setTaskColor(getDateStartColor());
            } else if (mTaskArrayList.get(i).getStartDateTask() != 0 && mTaskArrayList.get(i).getStopDateTask() != 0) {
                mTaskArrayList.get(i).setTaskColor(getDateEndColor());
            } else {
                mTaskArrayList.get(i).setTaskColor(getDateDefaultColor());
            }
        }
    }

}
