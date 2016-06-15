package com.dadc.taskmanager.activity;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.dadc.taskmanager.dialog.ClearDialogFragment;
import com.dadc.taskmanager.model.Task;
import com.dadc.taskmanager.util.ControlDataTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ClearDialogFragment.ClearTasks {

    private static final String KEY_SUBMIT_TASK = "submit_task";
    private static final String KEY_SAVE_INSTANCE = "save_instance";
    private static final String KEY_POSITION_ITEM = "position_item";
    private static final String KEY_EDIT_ITEM = "edit_item";
    private static final String KEY_TITLE_ACTIVITY = "title_edit_task";
    private static final int REQUEST_CODE_SETTING = 2;
    private static final int REQUEST_CODE_TASK = 1;

    private AlertDialog.Builder mAlertDialogReloadDateTask;
    private CoordinatorLayout mRelativeLayoutMainActivity;
    private ListView mTaskListView;

    private ArrayList<Task> mTaskArrayList;
    private TaskAdapter mTaskAdapter;
    private ControlDataTask mControlDataTask;

    private int mDefaultTaskColor, mStartTaskColor, mEndTaskColor;
    private boolean mDoubleBackToExitPressedOnce = false;
    MenuItem item1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getResources().getString(R.string.main_activity_name));


        if (savedInstanceState != null) {
            mTaskArrayList = savedInstanceState.getParcelableArrayList(KEY_SAVE_INSTANCE);
        } else {
            mTaskArrayList = new ArrayList<>();
        }

        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; This adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mControlDataTask.loadCheckedItem(menu); //Load checkedItemSort from SharedPreferences
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        item1 = item;
        switch (id) {
            case R.id.action_a_z:

                Collections.sort(mTaskArrayList, Task.TaskTitleComparator);
                saveSortTask(item);

                return true;

            case R.id.action_z_a:

                Collections.sort(mTaskArrayList, Task.TaskReverseTitleComparator);
                saveSortTask(item);

                return true;

            case R.id.action_first_end:

                Collections.sort(mTaskArrayList, Task.TaskDateComparator);
                saveSortTask(item);

                return true;


            case R.id.action_end_first:

                Collections.sort(mTaskArrayList, Task.TaskReverseDateComparator);
                saveSortTask(item);

                return true;

            case R.id.action_setting:

                //Go to Setting activity
                Intent intentSetting = new Intent(MainActivity.this, SettingActivity.class);
                startActivityForResult(intentSetting, REQUEST_CODE_SETTING);

                return true;

            case R.id.action_add:

                //Go to Task activity
                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                startActivityForResult(intent, REQUEST_CODE_TASK);

                return true;

            case R.id.action_add_items:

                addManyTasks();

                return true;
            case R.id.action_delete_items:

                //DialogFragment for clear Task data
                new ClearDialogFragment().show(getFragmentManager(), "");

                return true;

            case R.id.action_exit:

                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // initialize widget and adapter
    public void initUI() {
        mControlDataTask = new ControlDataTask(this);

        mDefaultTaskColor = mControlDataTask.getDateDefaultColor();
        mStartTaskColor = mControlDataTask.getDateStartColor();
        mEndTaskColor = mControlDataTask.getDateEndColor();

        mRelativeLayoutMainActivity = (CoordinatorLayout) findViewById(R.id.relativeLayoutMainActivity);
        mTaskListView = (ListView) findViewById(R.id.listViewTask);

        mTaskArrayList = new ArrayList<>();
        mTaskAdapter = new TaskAdapter(this, mTaskArrayList);
        mTaskListView.setAdapter(mTaskAdapter);


        // LoadData from SharedPreferences in Thread
        new Thread(new Runnable() {
            public void run() {
                mTaskArrayList = mControlDataTask.loadPreferenceDataTask();
                mTaskAdapter = new TaskAdapter(MainActivity.this, mTaskArrayList);
                mTaskListView.setAdapter(mTaskAdapter);
            }
        }).start();

        mTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, final View v, final int position, long id) {
                final Task mTask = mTaskAdapter.getItem(position);

                //Get Start date on current item
                long _mStartDate = mTaskAdapter.getItem(position).getStartDateTask();

                //Get Stop date on current item
                long _mStopDate = mTaskAdapter.getItem(position).getStopDateTask();

                if (_mStartDate == 0) {
                    mTask.setTaskColor(mStartTaskColor);
                    mTaskArrayList.get(position).setSelected(true);

                    //Set date to TextView after start
                    mTask.setStartDateTask(startDateTask(position));

                } else if (_mStopDate == 0) {
                    mTask.setTaskColor(mEndTaskColor);
                    mTaskArrayList.get(position).setSelected(true);

                    //Set date to TextView after ending
                    mTask.setStopDateTask(stopDateTask());

                } else {

                    //Reload dateTask
                    mAlertDialogReloadDateTask = new AlertDialog.Builder(MainActivity.this);
                    mAlertDialogReloadDateTask.setTitle(getResources().getString(R.string.titleAlertDialog));
                    mAlertDialogReloadDateTask.setMessage(getResources().getString(R.string.messageAlertDialog));
                    mAlertDialogReloadDateTask.setPositiveButton(getResources().getString(R.string.posBtnAlertDialog), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            mTask.setTaskColor(mStartTaskColor);
                            mTaskArrayList.get(position).setSelected(true);

                            //Set start date
                            mTaskAdapter.getItem(position).setStartDateTask(startDateTask(position));
                            mControlDataTask.savePreferenceDataTask(mTaskArrayList); //Save data in SharedPreferences
                        }
                    });
                    mAlertDialogReloadDateTask.setNegativeButton(getResources().getString(R.string.negBtnAlertDialog), null);
                    mAlertDialogReloadDateTask.setCancelable(false);
                    mAlertDialogReloadDateTask.show();
                }

                mControlDataTask.savePreferenceDataTask(mTaskArrayList); //Save data in SharedPreferences
            }
        });

        //Edit Task item
        mTaskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> p, View v, final int position, long id) {

                Task title = mTaskAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                intent.putExtra(KEY_EDIT_ITEM, title);
                intent.putExtra(KEY_POSITION_ITEM, position);
                intent.putExtra(KEY_TITLE_ACTIVITY, getResources().getString(R.string.activity_edit_task));
                startActivityForResult(intent, REQUEST_CODE_TASK);

                return true;
            }
        });
    }


    // Transition to  NewTaskActivity for new Task
    public void onClickNewTask(View view) {

        Intent intent = new Intent(MainActivity.this, TaskActivity.class);
        startActivityForResult(intent, REQUEST_CODE_TASK);
    }


    public long startDateTask(int position) {

        openSnackbar(getResources().getString(R.string.titleStartTaskSnackBar));
        mTaskAdapter.getItem(position).setStopDateTask(0);
        mTaskAdapter.notifyDataSetChanged();

        return System.currentTimeMillis();
    }

    public long stopDateTask() {

        //Show state DateTask
        openSnackbar(getResources().getString(R.string.titleEndTaskSnackBar));
        mTaskAdapter.notifyDataSetChanged();

        return System.currentTimeMillis();
    }

    //Calculation height item ListView
    private int getHeightItemListView(ListView listView, TaskAdapter listAdapter) {

        int heightItem;
        View listItem = listAdapter.getView(0, null, listView);
        listItem.measure(0, 0);
        heightItem = listItem.getMeasuredHeight() + listView.getDividerHeight(); //ListView item height

        return heightItem;
    }

    /*
     * This method auto generated and include 3 times more task than fits on the screen.
     */
    public void addManyTasks() {

        mTaskArrayList.add(0, new Task(getResources().getString(R.string.titleTask) + 1,
                getResources().getString(R.string.descriptionTask) + 1,
                mDefaultTaskColor, 0, 0));

        float heightItemListView = getHeightItemListView(mTaskListView, mTaskAdapter);
        float mTaskListViewHeight = mTaskListView.getHeight();
        int mCountItem = ((int) (mTaskListViewHeight / heightItemListView) * 3) - 1;

        for (int i = 0; i < mCountItem; i++) {
            Random mRandom = new Random();
            int mRandomNumber = mRandom.nextInt((100 - 2) + 2) + 2;
            mTaskArrayList.add(0, new Task(getResources().getString(R.string.titleTask) + mRandomNumber, getResources().getString(R.string.descriptionTask) + mRandomNumber,
                    mDefaultTaskColor, 0, 0));
        }

        mTaskAdapter.notifyDataSetChanged();
        mControlDataTask.savePreferenceDataTask(mTaskArrayList); //Save data in SharedPreferences
    }


    public void openSnackbar(CharSequence title) {
        Snackbar.make(mRelativeLayoutMainActivity, title, Snackbar.LENGTH_LONG).show();
    }


    public void saveSortTask(MenuItem item) {

        item.setChecked(true);
        mControlDataTask.saveCheckedItem(item); //Save checkedItem in SharedPreferences
        mTaskAdapter.notifyDataSetChanged();
        mControlDataTask.savePreferenceDataTask(mTaskArrayList);  //Save data in SharedPreferences

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode == RESULT_OK) {
            Task myTask = intent.getParcelableExtra(KEY_SUBMIT_TASK);
            int position = intent.getIntExtra(KEY_POSITION_ITEM, -1);

            if (position > -1) {
                mTaskArrayList.set(position, myTask); //Update item position in listView
            } else {
                // Add element to position 0 in mTaskListView
                mTaskArrayList.add(0, myTask);
            }

        } else if (resultCode == REQUEST_CODE_SETTING) {

            //Update color
            mDefaultTaskColor = mControlDataTask.getDateDefaultColor();
            mStartTaskColor = mControlDataTask.getDateStartColor();
            mEndTaskColor = mControlDataTask.getDateEndColor();

            mControlDataTask.updateColorDateTask(mTaskArrayList); //Update data in mTaskArrayList

        }

        mTaskAdapter.notifyDataSetChanged();
        mControlDataTask.savePreferenceDataTask(mTaskArrayList); //Save data in SharedPreferences

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelableArrayList(KEY_SAVE_INSTANCE, mTaskArrayList);
    }

    @Override
    public void onBackPressed() {
        if (mDoubleBackToExitPressedOnce) {
            super.onBackPressed();
        }

        this.mDoubleBackToExitPressedOnce = true;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mDoubleBackToExitPressedOnce = false;
            }
        }, 2000);

        openSnackbar(getResources().getString(R.string.titleExitTwice));
    }

    @Override
    public void clearTaskList() {
        mControlDataTask.clearPreferenceDataTask(mTaskArrayList, mTaskAdapter);
    }
}