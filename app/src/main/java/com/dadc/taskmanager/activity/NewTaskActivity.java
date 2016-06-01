package com.dadc.taskmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.model.Task;

/**
 * Created by bomko on 27.05.16.
 */
public class NewTaskActivity extends AppCompatActivity {

    private static final String KEY_SUBMIT_TASK = "submit_task";

    private EditText mEditTextTitle, mEditTextDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_task_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarNewTask);
        setSupportActionBar(toolbar);


        mEditTextTitle = (EditText) findViewById(R.id.titleEditText);
        mEditTextDescription = (EditText) findViewById(R.id.descriptionEditText);

    }

    public void onSaveTaskButton(View view) {

        // get String form editText: title, description
        String mTitle = mEditTextTitle.getText().toString();
        String mDescription = mEditTextDescription.getText().toString();

        Task mTask = new Task(mTitle, mDescription);
        Intent intent = new Intent();
        intent.putExtra(KEY_SUBMIT_TASK, mTask);
        setResult(RESULT_OK, intent);
        finish();

    }

    public void onExitButtonClick(View view) {
        finish();
    }
}