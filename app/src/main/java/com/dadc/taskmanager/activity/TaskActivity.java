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
import com.dadc.taskmanager.util.ControlDataTask;

/**
 * Created by bomko on 27.05.16.
 */
public class TaskActivity extends AppCompatActivity {

    private static final String KEY_SUBMIT_TASK = "submit_task";
    private static final String KEY_POSITION_ITEM = "position_item";
    private static final String KEY_EDIT_ITEM = "edit_item";
    private static final String KEY_TITLE_ACTIVITY = "title_edit_task";

    private EditText mEditTextTitle, mEditTextDescription;
    private int mPosition, mDefaultTaskColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_task_activity);

        // ActionBar mActionBar = getSupportActionBar();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);

        mEditTextTitle = (EditText) findViewById(R.id.titleEditText);
        mEditTextDescription = (EditText) findViewById(R.id.descriptionEditText);

        // get data from intent for edit item content
        Task mTaskEdit = getIntent().getParcelableExtra(KEY_EDIT_ITEM);
        mPosition = getIntent().getIntExtra(KEY_POSITION_ITEM, -1);
        String mTitleActivity = getIntent().getStringExtra(KEY_TITLE_ACTIVITY);

        if (mTaskEdit != null) {
            mEditTextTitle.setText(mTaskEdit.getTitle());
            mEditTextDescription.setText(mTaskEdit.getDescription());
            getSupportActionBar().setTitle(mTitleActivity);
        }

        ControlDataTask mControlDataTask = new ControlDataTask(this);

        mDefaultTaskColor = mControlDataTask.getDateDefaultColor();

    }


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

                Task mTask = new Task(mTitle, mDescription, mDefaultTaskColor, 0, 0);

                Intent intent = new Intent();
                intent.putExtra(KEY_SUBMIT_TASK, mTask);

                if (mPosition >= 0) {
                    intent.putExtra(KEY_POSITION_ITEM, mPosition);
                }
                setResult(RESULT_OK, intent);

                finish();

                return true;

            case android.R.id.home:

                finish();

                return true;

            default:
                return true;
        }
    }

}