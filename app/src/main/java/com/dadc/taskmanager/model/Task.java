package com.dadc.taskmanager.model;

import java.io.Serializable;

/**
 * Created by bomko on 27.05.16.
 */
public class Task implements Serializable {

    private String mTitle;
    private String mDescription;

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

}
