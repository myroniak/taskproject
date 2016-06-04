package com.dadc.taskmanager.parser;

import com.dadc.taskmanager.model.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class JSONParser {

    private ArrayList<Task> mTaskArrayList;

    public String saveTask(ArrayList<Task> tasks) throws JSONException, IOException {
        JSONArray jsonArray = new JSONArray(mTaskArrayList);
        for (Task task : tasks) {
            jsonArray.put(task.toJson());
        }
        return jsonArray.toString();
    }

    public ArrayList<Task> loadTask(String string) throws IOException, JSONException {

        mTaskArrayList = new ArrayList<>();
        JSONObject jsonObject;

        JSONArray jsonArray = new JSONArray(string);
        for (int i = 0; i < jsonArray.length(); i++) {

            jsonObject = jsonArray.getJSONObject(i);
            Task task = new Task(jsonObject);
            mTaskArrayList.add(task);
        }

        return mTaskArrayList;
    }
}