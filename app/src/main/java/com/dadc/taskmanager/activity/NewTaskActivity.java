package com.dadc.taskmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.model.Task;

/**
 * Created by bomko on 27.05.16.
 */
public class NewTaskActivity extends AppCompatActivity {

    private static final String KEY_SUBMIT_TASK = "submit_task";
    private static final String KEY_POSITION_ITEM = "position_item";
    private static final String KEY_EDIT_ITEM = "edit_item";

    private EditText mEditTextTitle, mEditTextDescription;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_task_activity);

        mEditTextTitle = (EditText) findViewById(R.id.titleEditText);
        mEditTextDescription = (EditText) findViewById(R.id.descriptionEditText);

        // get data from intent for edit item content
        Task taskEdit = getIntent().getParcelableExtra(KEY_EDIT_ITEM);
        position = getIntent().getIntExtra(KEY_POSITION_ITEM, -1);

        if (taskEdit != null) {
            mEditTextTitle.setText(taskEdit.getTitle());
            mEditTextDescription.setText(taskEdit.getDescription());
        }

    }

    public void onSaveTaskButton(View view) {

        // get String form editText: title, description
        String mTitle = mEditTextTitle.getText().toString();
        String mDescription = mEditTextDescription.getText().toString();

        Task mTask = new Task(mTitle, mDescription, R.color.defaultTaskDate,"");

        Intent intent = new Intent();
        intent.putExtra(KEY_SUBMIT_TASK, mTask);

        if (position >= 0) {
            intent.putExtra(KEY_POSITION_ITEM, position);
        }
        setResult(RESULT_OK, intent);
        finish();

    }

    public void onExitButtonClick(View view) {
        finish();
    }
}