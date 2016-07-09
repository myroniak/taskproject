package com.dadc.taskmanager.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TabHost;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.adapter.ExpandableAdapter;
import com.dadc.taskmanager.adapter.TaskAdapter;
import com.dadc.taskmanager.dialog.ClearDialogFragment;
import com.dadc.taskmanager.enumstate.ButtonType;
import com.dadc.taskmanager.migration.Migration;
import com.dadc.taskmanager.model.LoadSortTask;
import com.dadc.taskmanager.model.Statistic;
import com.dadc.taskmanager.model.Task;
import com.dadc.taskmanager.util.ManagerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity implements ClearDialogFragment.ClearTasks {

    private static final String KEY_SUBMIT_TASK = "submit_task";
    private static final String KEY_SAVE_INSTANCE = "save_instance";
    private static final String KEY_POSITION_ITEM = "position_item";
    private static final int REQUEST_CODE_TASK = 1;
    private static final int REQUEST_CODE_TASK_EDIT = 2;
    private static final int REQUEST_CODE_SETTING = 3;
    private static Context sContext;
    private ExpandableAdapter adapter;
    private FloatingActionButton mFloatingActionButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ExpandableListView expandableListView;
    private RecyclerView mRecyclerView;
    private ManagerData mManagerData;

    private HashMap<String, List<Statistic>> сhildDataList;
    private ArrayList<Statistic> mStatisticArrayList, arrayList1, arrayList2, arrayList3, arrayList4, arrayList5, arrayList6, arrayList7, arrayList8, arrayList9, arrayList10, arrayList11, arrayList12;
    private ArrayList<Task> mTaskArrayList;
    private ArrayList<String> mGroupsArray;
    private TaskAdapter mTaskAdapter;

    private boolean mDoubleBackToExitPressedOnce = false;
    private int mDefaultTaskColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initToolBar();

        realmConfiguration();
        PreferenceManager.setDefaultValues(this, R.xml.fragment_preference, false);
        MainActivity.sContext = this;


        if (savedInstanceState != null) {
            mTaskArrayList = savedInstanceState.getParcelableArrayList(KEY_SAVE_INSTANCE);
        } else {
            mTaskArrayList = new ArrayList<>();
        }

        mManagerData = ManagerData.getInstance(this);
        mDefaultTaskColor = mManagerData.getDateDefaultColor();

        initTabHost();
        initView();
        initExpandable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        LoadSortTask loadSortTask = mManagerData.loadSortTask(menu, mTaskArrayList);
        MenuItem menuItem = loadSortTask.getMenuItem(); //Load checkedItemSort from SharedPreferences
        if (menuItem != null)
            mTaskArrayList = loadSortTask.getArrayList();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case R.id.action_a_z:
                mTaskArrayList = mManagerData.sortAZ(mTaskArrayList);
                saveSortTask(item);
                return true;

            case R.id.action_z_a:
                mTaskArrayList = mManagerData.sortZA(mTaskArrayList);
                saveSortTask(item);
                return true;

            case R.id.action_date_ascending:
                mTaskArrayList = mManagerData.sortDateAscending(mTaskArrayList);
                saveSortTask(item);
                return true;

            case R.id.action_date_descending:
                mTaskArrayList = mManagerData.sortDateDescending(mTaskArrayList);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SETTING) {

            //Update color
            mDefaultTaskColor = mManagerData.getDateDefaultColor();
            mManagerData.updateColorDateTask();

        } else if (resultCode == RESULT_OK && intent != null) {

            Task myTask = intent.getParcelableExtra(KEY_SUBMIT_TASK);
            int position = intent.getIntExtra(KEY_POSITION_ITEM, -1);

            switch (requestCode) {

                case REQUEST_CODE_TASK:
                    mTaskArrayList.add(0, myTask);
                    break;

                case REQUEST_CODE_TASK_EDIT:
                    mTaskArrayList.set(position, myTask); //Update item position in listView
                    break;

                default:
                    break;
            }

        }
        mManagerData.savePreferenceDataTask(mTaskArrayList); //Save data in SharedPreferences
        mTaskAdapter.notifyDataSetChanged();

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
        openSnackbar(getResources().getString(R.string.title_exit_twice));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(KEY_SAVE_INSTANCE, mTaskArrayList);
    }

    @Override
    public void clearTaskList() {
        mManagerData.deleteAllRealm(mTaskArrayList, mTaskAdapter);
    }


    private void initToolBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getResources().getString(R.string.main_activity_name));
        }

    }

    private void initTabHost() {
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");

        tabSpec.setContent(R.id.relative1);
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.ic_assignment_lime_400_48dp));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.relative2);
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.ic_assessment_lime_400_48dp));
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);
    }

    // initialize widget and adapter
    private void initView() {

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.addNewTaskFAB);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewTask);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mTaskArrayList = mManagerData.loadPreferenceDataTask();
                mTaskAdapter = new TaskAdapter(MainActivity.this, mTaskArrayList, mStatisticArrayList);
                mRecyclerView.setAdapter(mTaskAdapter);
            }
        }, 0);

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

    // Transition to TaskActivity for new Task
    public void onClickNewTask(View view) {
        Intent intent = new Intent(MainActivity.this, TaskActivity.class);
        startActivityForResult(intent, REQUEST_CODE_TASK);
    }

    //Calculation height item ListView
    private void addManyTasks() {
        if (mTaskArrayList.size() == 0) {
            mTaskArrayList.add(0, new Task(getUUID(), getResources().getString(R.string.title_task) + 1,
                    getResources().getString(R.string.description_task) + 1,

                    mDefaultTaskColor, 0, 0, mManagerData.defaultTime(), ButtonType.PLAY.name(), ""));

            mTaskAdapter.notifyDataSetChanged();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Random random = new Random();

                float heightItemListView = mRecyclerView.getChildAt(0).getHeight();
                float taskListViewHeight = mRecyclerView.getHeight();
                int countItem = ((int) (taskListViewHeight / heightItemListView) * 3) - 1;

                for (int i = 0; i < countItem; i++) {
                    int randomNumber = random.nextInt((100 - 2) + 2) + 2;
                    mTaskArrayList.add(0, new Task(getUUID(), getResources().getString(R.string.title_task) +
                            randomNumber, getResources().getString(R.string.description_task) + randomNumber,
                            mDefaultTaskColor, 0, 0, mManagerData.defaultTime(), ButtonType.PLAY.name(), ""));
                }
                mTaskAdapter.notifyDataSetChanged();
                mManagerData.savePreferenceDataTask(mTaskArrayList); //Save data in SharedPreferences
            }
        }, 10);
    }

    private void saveSortTask(MenuItem item) {
        item.setChecked(true);
        mManagerData.saveCheckedItem(item); //Save checkedItem in SharedPreferences
        mTaskAdapter.notifyDataSetChanged();
        mManagerData.savePreferenceDataTask(mTaskArrayList);  //Save data in SharedPreferences
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    private void openSnackbar(CharSequence title) {
        Snackbar.make(mRecyclerView, title, Snackbar.LENGTH_LONG).show();
    }

    public static Context getAppContext() {
        return MainActivity.sContext;
    }

    public void stopTaskUpdateUI(int position) {
        mTaskAdapter.stopTaskAlarm(position);
    }

    public void realmConfiguration() {

        RealmConfiguration mRealmConfiguration = new RealmConfiguration.Builder(this)
                .schemaVersion(3)
                .migration(new Migration())
                .build();
        Realm.setDefaultConfiguration(mRealmConfiguration);
    }

    /**
     * Statistic Tab
     */


    public void initExpandable() {

        mGroupsArray = new ArrayList<>();
        сhildDataList = new HashMap<>();
        mStatisticArrayList = mManagerData.loadStatistic();

        mGroupsArray.add("Січень");
        mGroupsArray.add("Лютий");
        mGroupsArray.add("Березень");
        mGroupsArray.add("Квітень");
        mGroupsArray.add("Травень");
        mGroupsArray.add("Червень");
        mGroupsArray.add("Липень");
        mGroupsArray.add("Серпень");
        mGroupsArray.add("Вересень");
        mGroupsArray.add("Жовтень");
        mGroupsArray.add("Листопад");
        mGroupsArray.add("Грудень");

        arrayList1 = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        arrayList3 = new ArrayList<>();
        arrayList4 = new ArrayList<>();
        arrayList5 = new ArrayList<>();
        arrayList6 = new ArrayList<>();
        arrayList7 = new ArrayList<>();
        arrayList8 = new ArrayList<>();
        arrayList9 = new ArrayList<>();
        arrayList10 = new ArrayList<>();
        arrayList11 = new ArrayList<>();
        arrayList12 = new ArrayList<>();

        refreshChildExpandable();

        adapter = new ExpandableAdapter(this, mGroupsArray, сhildDataList);

        expandableListView = (ExpandableListView) findViewById(R.id.expListView);

        if (expandableListView != null) {
            expandableListView.setAdapter(adapter);
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (arrayList7.size() != 0)
                    сhildDataList.get("Липень").removeAll(arrayList7);
                refreshChildExpandable();
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void refreshChildExpandable() {
        for (int i = 0; i < mStatisticArrayList.size(); i++) {

            switch (mStatisticArrayList.get(i).getMonth()) {
                case 1:
                    arrayList1.add(mStatisticArrayList.get(i));
                    break;
                case 2:
                    arrayList2.add(mStatisticArrayList.get(i));
                    break;
                case 3:
                    arrayList3.add(mStatisticArrayList.get(i));
                    break;
                case 4:
                    arrayList4.add(mStatisticArrayList.get(i));
                    break;
                case 5:
                    arrayList5.add(mStatisticArrayList.get(i));
                    break;
                case 6:
                    arrayList6.add(mStatisticArrayList.get(i));
                    break;
                case 7:
                    arrayList7.add(mStatisticArrayList.get(i));
                    break;
                case 8:
                    arrayList8.add(mStatisticArrayList.get(i));
                    break;
                case 9:
                    arrayList9.add(mStatisticArrayList.get(i));
                    break;
                case 10:
                    arrayList10.add(mStatisticArrayList.get(i));
                    break;
                case 11:
                    arrayList11.add(mStatisticArrayList.get(i));
                    break;
                case 12:
                    arrayList12.add(mStatisticArrayList.get(i));
                    break;
                default:
                    break;
            }
        }

        сhildDataList.put(mGroupsArray.get(0), arrayList1);
        сhildDataList.put(mGroupsArray.get(1), arrayList2);
        сhildDataList.put(mGroupsArray.get(2), arrayList3);
        сhildDataList.put(mGroupsArray.get(3), arrayList4);
        сhildDataList.put(mGroupsArray.get(4), arrayList5);
        сhildDataList.put(mGroupsArray.get(5), arrayList6);
        сhildDataList.put(mGroupsArray.get(6), arrayList7);
        сhildDataList.put(mGroupsArray.get(7), arrayList8);
        сhildDataList.put(mGroupsArray.get(8), arrayList9);
        сhildDataList.put(mGroupsArray.get(9), arrayList10);
        сhildDataList.put(mGroupsArray.get(10), arrayList11);
        сhildDataList.put(mGroupsArray.get(11), arrayList12);
    }

}