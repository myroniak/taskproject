package com.dadc.taskmanager.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.adapter.TaskAdapter;
import com.dadc.taskmanager.model.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_SUBMIT_TASK = "submit_task";
    private static final String KEY_SAVE_INSTANCE = "save_instance";
    private static final String KEY_POSITION_ITEM = "position_item";
    private static final String KEY_EDIT_ITEM = "edit_item";

    private ArrayList<Task> mTaskArrayList;
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

    // initialize widget and adapter
    public void initUI() {
        mRelativeLayoutMainActivity = (CoordinatorLayout) findViewById(R.id.relativeLayoutMainActivity);
        mTaskListView = (ListView) findViewById(R.id.listViewTask);
        mTaskAdapter = new TaskAdapter(this, mTaskArrayList);
        mTaskListView.setAdapter(mTaskAdapter);

        mTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, final View v, final int position, long id) {


                //get start time on current item
                long _mStartTime = mTaskAdapter.getItem(position).getStartTask();
                //set start time
                mTaskAdapter.getItem(position).setStartTask(startTimeTask());

                if (_mStartTime == 0) {

                    mTaskArrayList.get(position).setSelected(true);
                    //set date to TextView after start
                    mTaskAdapter.getItem(position).setFullDate(newDateTask(mTaskAdapter.getItem(position).getStartTask()));

                } else if (_mStartTime > 0 && mTaskAdapter.getItem(position).getFullDate().contains("*")) {

                    mTaskArrayList.get(position).setSelected(true);

                    //set date to TextView after end
                    mTaskAdapter.getItem(position).setFullDate(endDateTask(_mStartTime));

                } else {
                    // reload newDateTask
                    mAlertDialogReloadDateTask = new AlertDialog.Builder(MainActivity.this);
                    mAlertDialogReloadDateTask.setTitle(getResources().getString(R.string.titleAlertDialog));
                    mAlertDialogReloadDateTask.setMessage(getResources().getString(R.string.messageAlertDialog));
                    mAlertDialogReloadDateTask.setPositiveButton(getResources().getString(R.string.posBtnAlertDialog), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            mTaskArrayList.get(position).setSelected(true);
                            //set start time
                            mTaskAdapter.getItem(position).setStartTask(startTimeTask());
                            mTaskAdapter.getItem(position).setFullDate(newDateTask(mTaskAdapter.getItem(position).getStartTask()));
                        }
                    });
                    mAlertDialogReloadDateTask.setNegativeButton(getResources().getString(R.string.negBtnAlertDialog), null);
                    mAlertDialogReloadDateTask.setCancelable(false);
                    mAlertDialogReloadDateTask.show();
                }

            }
        });

        //edit Task
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
        return System.currentTimeMillis();
    }

    public String newDateTask(long mTimeStart) {
        //format date
        DateFormat mDateFormat = new SimpleDateFormat(getResources().getString(R.string.fullFormat));

        //generate String for TextView
        String mNewDateTask = mDateFormat.format(mTimeStart) + getResources().getString(R.string.hyphen) + getResources().getString(R.string.noSetDate);

        //show state DateTask
        Snackbar.make(mRelativeLayoutMainActivity, getResources().getString(R.string.titleStartTaskSnackBar), Snackbar.LENGTH_LONG).show();
        mTaskAdapter.notifyDataSetChanged();
        return mNewDateTask;
    }

    public String endDateTask(long mTimeStart) {

        long mTimeEnd = System.currentTimeMillis();
        long mTimeComplete = mTimeEnd - mTimeStart;

        //format date
        DateFormat mDateFormatFull = new SimpleDateFormat(getResources().getString(R.string.fullFormat));
        DateFormat mDateFormatShort = new SimpleDateFormat(getResources().getString(R.string.shortFormat));
        mDateFormatShort.setTimeZone(TimeZone.getTimeZone(getResources().getString(R.string.timeZone)));

        //generate String for TextView
        String mEndDateTask = mDateFormatFull.format(mTimeStart) + getResources().getString(R.string.hyphen) + mDateFormatFull.format(mTimeEnd) + getResources().getString(R.string.spaceValue) + mDateFormatShort.format(mTimeComplete);

        //show state DateTask
        Snackbar.make(mRelativeLayoutMainActivity, getResources().getString(R.string.titleEndTaskSnackBar), Snackbar.LENGTH_SHORT).show();
        mTaskAdapter.notifyDataSetChanged();

        return mEndDateTask;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode == RESULT_OK) {
            Task myTask = intent.getParcelableExtra(KEY_SUBMIT_TASK);
            int position = intent.getIntExtra(KEY_POSITION_ITEM, -1);

            if (position > -1) {
                // update item listView
                mTaskArrayList.set(position, myTask);
            } else {
                // add element to position 0 in mTaskListView
                mTaskArrayList.add(0, myTask);

            }
            mTaskAdapter.notifyDataSetChanged();

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

