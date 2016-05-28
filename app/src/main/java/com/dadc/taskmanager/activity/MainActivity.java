package com.dadc.taskmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.adapter.TaskAdapter;
import com.dadc.taskmanager.model.Task;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_SUBMIT_TASK = "submit_task";
    private static final String KEY_SAVE_INSTANCE = "save_instance";

    private ArrayList<Task> mTaskArrayList;
    private TaskAdapter mTaskAdapter;
    private ListView mTaskListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {

            mTaskArrayList = (ArrayList<Task>) savedInstanceState.getSerializable(KEY_SAVE_INSTANCE);
        } else {
            mTaskArrayList = new ArrayList<>();
        }

        initUI();
    }

    // initialize widget and adapter
    public void initUI() {
        mTaskListView = (ListView) findViewById(R.id.listViewTask);
        mTaskAdapter = new TaskAdapter(this, mTaskArrayList);
        mTaskListView.setAdapter(mTaskAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode == RESULT_OK) {
            Task myTask = (Task) intent.getSerializableExtra(KEY_SUBMIT_TASK);
            mTaskArrayList.add(myTask);
            mTaskAdapter.notifyDataSetChanged();

        } else {
            Toast.makeText(this, getString(R.string.activity_no_result), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(KEY_SAVE_INSTANCE, mTaskArrayList);
    }

    // transition to  NewTaskActivity for new Task
    public void newTask(View view) {

        Intent intent = new Intent(MainActivity.this, NewTaskActivity.class);
        startActivityForResult(intent, 1);
    }
}

