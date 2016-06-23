package com.dadc.taskmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.adapter.TaskAdapter;
import com.dadc.taskmanager.dialog.ClearDialogFragment;
import com.dadc.taskmanager.model.Task;
import com.dadc.taskmanager.util.ControlDataTask;
import com.daimajia.swipe.util.Attributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ClearDialogFragment.ClearTasks {

    private static final String KEY_SUBMIT_TASK = "submit_task";
    private static final String KEY_SAVE_INSTANCE = "save_instance";
    private static final String KEY_POSITION_ITEM = "position_item";
    private static final int REQUEST_CODE_SETTING = 2;
    private static final int REQUEST_CODE_TASK = 1;

    private CoordinatorLayout mCoordinatorLayoutMain;
    private RecyclerView mRecyclerView;

    private ArrayList<Task> mTaskArrayList;
    private TaskAdapter mTaskAdapter;
    private ControlDataTask mControlDataTask;
    private FloatingActionButton mFloatingActionButton;

    private boolean mDoubleBackToExitPressedOnce = false;
    private int mDefaultTaskColor;

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

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.addNewTaskFAB);

        mCoordinatorLayoutMain = (CoordinatorLayout) findViewById(R.id.relativeLayoutMainActivity);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewTask);

        mTaskArrayList = new ArrayList<>();
        mTaskAdapter = new TaskAdapter(mRecyclerView, this, mTaskArrayList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        //  mTaskListView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mTaskAdapter.setMode(Attributes.Mode.Single);
        mRecyclerView.setAdapter(mTaskAdapter);

        // LoadData from SharedPreferences in Thread
        new Thread(new Runnable() {
            public void run() {
                mTaskArrayList = mControlDataTask.loadPreferenceDataTask();
                mTaskAdapter = new TaskAdapter(mRecyclerView, MainActivity.this, mTaskArrayList);
                mRecyclerView.setAdapter(mTaskAdapter);
            }
        }).start();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    mFloatingActionButton.hide();
                else if (dy < 0)
                    mFloatingActionButton.show();
            }
        });

    }


    // Transition to  NewTaskActivity for new Task
    public void onClickNewTask(View view) {

        Intent intent = new Intent(MainActivity.this, TaskActivity.class);
        startActivityForResult(intent, REQUEST_CODE_TASK);
    }

    public void addManyTasks() {

        mTaskArrayList.add(0, new Task(getResources().getString(R.string.titleTask) + 1,
                getResources().getString(R.string.descriptionTask) + 1,
                mDefaultTaskColor, 0, 0));

        float heightItemListView = getHeightItemListView(mRecyclerView);
        float mTaskListViewHeight = mRecyclerView.getHeight();
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

    //Calculation height item ListView
    private int getHeightItemListView(RecyclerView listView) {
        return listView.getChildAt(0).getHeight();
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

    public void openSnackbar(CharSequence title) {
        Snackbar.make(mCoordinatorLayoutMain, title, Snackbar.LENGTH_LONG

        ).show();

    }

}