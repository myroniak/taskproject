package com.dadc.taskmanager.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.adapter.TaskAdapter;
import com.dadc.taskmanager.model.Task;
import com.dadc.taskmanager.util.SaveData;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_SUBMIT_TASK = "submit_task";
    private static final String KEY_SAVE_INSTANCE = "save_instance";
    private static final String KEY_POSITION_ITEM = "position_item";
    private static final String KEY_EDIT_ITEM = "edit_item";

    SharedPreferences.Editor editor;
    public static final String APP_PREFERENCES = "setting";
    SharedPreferences mSettings;

    private ArrayList<Task> mTaskArrayList = new ArrayList<>();
    private TaskAdapter mTaskAdapter;
    private ListView mTaskListView;

    AlertDialog.Builder mAlertDialogReloadDateTask;
    CoordinatorLayout mRelativeLayoutMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {

            mTaskArrayList = savedInstanceState.getParcelableArrayList(KEY_SAVE_INSTANCE);
        } else {
            mTaskArrayList = new ArrayList<>();
        }

        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_items:
                if (mTaskArrayList.isEmpty()) {

                    mTaskArrayList.add(0, new Task(getResources().getString(R.string.titleTask), getResources().getString(R.string.descriptionTask), R.color.defaultTaskDate, 0, 0));
                    addManyTasks();
                } else {

                    addManyTasks();
                }
                // Save data SharedPreferences
                SaveData.saveSetting(MainActivity.this, mTaskArrayList, mSettings, APP_PREFERENCES, editor);

                return true;
            case R.id.action_delete_items:
                mTaskArrayList.clear();
                SaveData.clearData(editor);

                mTaskAdapter.notifyDataSetChanged();
                return true;
            default:
                return true;
        }
    }

    // initialize widget and adapter
    public void initUI() {

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = mSettings.edit();
        mTaskArrayList = new ArrayList<>();

        mRelativeLayoutMainActivity = (CoordinatorLayout) findViewById(R.id.relativeLayoutMainActivity);
        mTaskListView = (ListView) findViewById(R.id.listViewTask);
        mTaskAdapter = new TaskAdapter(this, mTaskArrayList);
        mTaskListView.setAdapter(mTaskAdapter);

        // LoadData from SharedPreferences in Thread
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                mTaskArrayList = SaveData.loadSetting(mSettings);
                mTaskAdapter = new TaskAdapter(MainActivity.this, mTaskArrayList);
                mTaskListView.setAdapter(mTaskAdapter);

            }
        }, 500);

        mTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, final View v, final int position, long id) {
                final Task mTask = mTaskAdapter.getItem(position);

                //get start time on current item
                long _mStartTime = mTaskAdapter.getItem(position).getStartTimeTask();
                long _mStopTime = mTaskAdapter.getItem(position).getStopTimeTask();
                //set start time
                //mTaskAdapter.getItem(position).setStartTimeTask(startTimeTask());

                if (_mStartTime == 0 && _mStopTime == 0) {
                    mTask.setTaskColor(R.color.startTaskDate);
                    mTaskArrayList.get(position).setSelected(true);
                    //set date to TextView after start
                    mTask.setStartTimeTask(startTimeTask());
                    mTaskAdapter.notifyDataSetChanged();

                } else if (_mStopTime == 0) {
                    mTask.setTaskColor(R.color.stopTaskTime);
                    mTaskArrayList.get(position).setSelected(true);

                    //set date to TextView after end
                    mTask.setStopTimeTask(stopTimeTask());
                    mTaskAdapter.notifyDataSetChanged();

                } else {
                    // reload newDateTask
                    mAlertDialogReloadDateTask = new AlertDialog.Builder(MainActivity.this);
                    mAlertDialogReloadDateTask.setTitle(getResources().getString(R.string.titleAlertDialog));
                    mAlertDialogReloadDateTask.setMessage(getResources().getString(R.string.messageAlertDialog));
                    mAlertDialogReloadDateTask.setPositiveButton(getResources().getString(R.string.posBtnAlertDialog), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            mTaskArrayList.get(position).setSelected(true);
                            //set start time
                            mTask.setTaskColor(R.color.startTaskDate);
                            mTaskAdapter.getItem(position).setStartTimeTask(startTimeTask());
                            mTaskAdapter.getItem(position).setStopTimeTask(0);
                            //  mTaskAdapter.getItem(position).setFullDate(newDateTask(mTaskAdapter.getItem(position).setStartTimeTask()));
                        }
                    });
                    mAlertDialogReloadDateTask.setNegativeButton(getResources().getString(R.string.negBtnAlertDialog), null);
                    mAlertDialogReloadDateTask.setCancelable(false);
                    mAlertDialogReloadDateTask.show();
                }

            }
        });

        //Edit Task
        mTaskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> p, View v, final int position, long id) {

                Task title = mTaskAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, NewTaskActivity.class);
                intent.putExtra(KEY_EDIT_ITEM, title);
                intent.putExtra(KEY_POSITION_ITEM, position);
                startActivityForResult(intent, 1);

                return true;
            }
        });
    }

    public long startTimeTask() {

        Snackbar.make(mRelativeLayoutMainActivity, getResources().getString(R.string.titleStartTaskSnackBar), Snackbar.LENGTH_LONG).show();
        mTaskAdapter.notifyDataSetChanged();
        SaveData.saveSetting(MainActivity.this, mTaskArrayList, mSettings, APP_PREFERENCES, editor);

        return System.currentTimeMillis();
    }

    public long stopTimeTask() {

        //Show state DateTask
        Snackbar.make(mRelativeLayoutMainActivity, getResources().getString(R.string.titleEndTaskSnackBar), Snackbar.LENGTH_SHORT).show();
        mTaskAdapter.notifyDataSetChanged();
        //Save data SharedPreferences
        SaveData.saveSetting(MainActivity.this, mTaskArrayList, mSettings, APP_PREFERENCES, editor);

        return System.currentTimeMillis();
    }


    public void addManyTasks() {

        float o = getHeightItemListView(mTaskListView, mTaskAdapter);
        float y = mTaskListView.getHeight();
        int k = ((int) (y / o) * 3) - 1;

        for (int i = 0; i < k; i++) {
            mTaskArrayList.add(0, new Task(getResources().getString(R.string.titleTask), getResources().getString(R.string.descriptionTask), R.color.defaultTaskDate, 0, 0));
        }
        mTaskAdapter.notifyDataSetChanged();

    }

    private int getHeightItemListView(ListView listView, TaskAdapter listAdapter) {

        int heightItem = 0;//it is the ListView Height
        View listItem = listAdapter.getView(0, null, listView);
        listItem.measure(0, 0);
        heightItem = listItem.getMeasuredHeight() + listView.getDividerHeight();//item height

        return heightItem;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode == RESULT_OK) {
            Task myTask = intent.getParcelableExtra(KEY_SUBMIT_TASK);
            int position = intent.getIntExtra(KEY_POSITION_ITEM, -1);

            if (position > -1) {
                // Update item listView
                mTaskArrayList.set(position, myTask);
            } else {
                // Add element to position 0 in mTaskListView
                mTaskArrayList.add(0, myTask);

            }

            mTaskAdapter.notifyDataSetChanged();

            // Save data SharedPreferences
            SaveData.saveSetting(this, mTaskArrayList, mSettings, APP_PREFERENCES, editor);

        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(KEY_SAVE_INSTANCE, mTaskArrayList);
    }

    // transition to  NewTaskActivity for new Task
    public void newTask(View view) {

        Intent intent = new Intent(MainActivity.this, NewTaskActivity.class);
        startActivityForResult(intent, 1);
    }
}