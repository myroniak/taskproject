package com.dadc.taskmanager.model;

import android.view.MenuItem;
import java.util.ArrayList;

/**
 * Created by bomko on 09.07.16.
 */
public class LoadSortTask {

    private MenuItem mMenuItem;
    private ArrayList<Task> mTaskArrayList;

    public LoadSortTask(MenuItem mMenuItem, ArrayList<Task> mTaskArrayList) {
        this.mMenuItem = mMenuItem;
        this.mTaskArrayList = mTaskArrayList;
    }

    public MenuItem getMenuItem() {
        return mMenuItem;
    }

    public ArrayList<Task> getArrayList() {
        return mTaskArrayList;
    }
}
