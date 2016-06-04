package com.dadc.taskmanager.util;

import android.content.Context;

import com.dadc.taskmanager.model.Task;
import com.dadc.taskmanager.parser.JSONParser;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by bomko on 03.06.16.
 */
public class SaveData {

    private static final String KEY_SETTING = "key";

    public static void saveSetting(Context context, ArrayList<Task> mTaskArrayList, android.content.SharedPreferences mSettings, String keySetting, android.content.SharedPreferences.Editor editor) {
        mSettings = context.getSharedPreferences(keySetting, Context.MODE_PRIVATE);
        JSONParser mTaskJSON = new JSONParser();
        editor = mSettings.edit();

        try {
            editor.putString(KEY_SETTING, mTaskJSON.saveTask(mTaskArrayList));
            editor.apply();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Task> loadSetting( android.content.SharedPreferences mSettings) {
        ArrayList<Task>  mTaskArrayList = new ArrayList<>();
        if (mSettings.contains(KEY_SETTING)) {
            JSONParser mTaskJSON = new JSONParser();
            try {
                  mTaskArrayList = mTaskJSON.loadTask(mSettings.getString(KEY_SETTING, ""));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        return mTaskArrayList;

    }

    public static void clearData(android.content.SharedPreferences.Editor editor) {

        editor.remove(KEY_SETTING);
        editor.clear();
        editor.commit();

    }

}
