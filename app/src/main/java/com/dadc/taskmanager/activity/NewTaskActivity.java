package com.dadc.taskmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.model.Task;
import com.dadc.taskmanager.util.SaveData;

/**
 * Created by bomko on 27.05.16.
 */
public class NewTaskActivity extends AppCompatActivity {

    private static final String KEY_SUBMIT_TASK = "submit_task";
    private static final String KEY_POSITION_ITEM = "position_item";
    private static final String KEY_EDIT_ITEM = "edit_item";

    private EditText mEditTextTitle, mEditTextDescription;
    private int position, defaultTaskColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_task_activity);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mEditTextTitle = (EditText) findViewById(R.id.titleEditText);
        mEditTextDescription = (EditText) findViewById(R.id.descriptionEditText);

        // get data from intent for edit item content
        Task taskEdit = getIntent().getParcelableExtra(KEY_EDIT_ITEM);
        position = getIntent().getIntExtra(KEY_POSITION_ITEM, -1);

        if (taskEdit != null) {
            mEditTextTitle.setText(taskEdit.getTitle());
            mEditTextDescription.setText(taskEdit.getDescription());
        }

        SaveData mSaveDate = new SaveData(this);

        defaultTaskColor =   mSaveDate.getDateDefaultColor();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_new_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_done:

                // get String form editText: title, description
                String mTitle = mEditTextTitle.getText().toString();
                String mDescription = mEditTextDescription.getText().toString();

                Task mTask = new Task(mTitle, mDescription, defaultTaskColor, 0, 0);

                Intent intent = new Intent();
                intent.putExtra(KEY_SUBMIT_TASK, mTask);

                if (position >= 0) {
                    intent.putExtra(KEY_POSITION_ITEM, position);
                }
                setResult(RESULT_OK, intent);

                finish();

                return true;

            case R.id.action_discard:

                finish();

                return true;

            default:
                return true;
        }
    }

}