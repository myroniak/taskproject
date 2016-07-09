package com.dadc.taskmanager.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.adapter.TaskAdapter;
import com.dadc.taskmanager.model.Statistic;
import com.dadc.taskmanager.model.Task;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bomko on 02.07.16.
 */
public class ManagerData {

    private static ManagerData sInstance;

    private static final String DEFAULT_COLOR_DATE = "colorDateDefault";
    private static final String START_COLOR_DATE = "colorDateStart";
    private static final String END_COLOR_DATE = "colorDateEnd";
    private static final String APP_PREFERENCES = "setting";
    private static final String CHECKED_ITEM = "checked";
    private static final String KEY_VALUE = "key";

    private SharedPreferences mSettings, mDefaultSetting;
    private SharedPreferences.Editor mEditor;

    private Realm mRealm;
    private Context mContext;

    public static ManagerData getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ManagerData(context);
        }
        return sInstance;
    }

    private ManagerData(Context context) {
        mContext = context;
        mRealm = Realm.getDefaultInstance();
        mSettings = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        mDefaultSetting = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mSettings.edit();
    }

    public void saveStatistic(ArrayList<Statistic> arrayList) {
        mRealm.beginTransaction();
        for (Statistic statistic : arrayList) {
            mRealm.copyToRealmOrUpdate(statistic);
        }
        mRealm.commitTransaction();
    }

    public ArrayList<Statistic> loadStatistic() {
        mRealm.beginTransaction();
        ArrayList<Statistic> taskArrayList = new ArrayList<>();
        RealmResults<Statistic> realmResults = mRealm.where(Statistic.class).findAll();
        for (int i = 0; i < realmResults.size(); i++) {
            taskArrayList.add(0, realmResults.get(i));
        }
        mRealm.commitTransaction();
        return taskArrayList;
    }


    public void savePreferenceDataTask(ArrayList<Task> arrayList) {
        mRealm.beginTransaction();
        for (Task task : arrayList) {
            mRealm.copyToRealmOrUpdate(task);
        }
        mRealm.commitTransaction();
    }

    public ArrayList<Task> loadPreferenceDataTask() {
        mRealm.beginTransaction();
        ArrayList<Task> taskArrayList = new ArrayList<>();
        RealmResults<Task> realmResults = mRealm.where(Task.class).findAll();
        for (int i = 0; i < realmResults.size(); i++) {
            taskArrayList.add(0, realmResults.get(i));
        }
        mRealm.commitTransaction();
        return taskArrayList;
    }


    public Task startTask(Task task, int color) {
        mRealm.beginTransaction();
        Task taskUpdate = mRealm.where(Task.class).equalTo("mId", task.getId()).findFirst();
        taskUpdate.setSelected(true);
        taskUpdate.setTaskColor(color);
        taskUpdate.setStartDateTask(System.currentTimeMillis());
        taskUpdate.setStopDateTask(0);
        mRealm.commitTransaction();
        return taskUpdate;
    }

    public Task stopTask(String buttonType, Task task, int color) {
        mRealm.beginTransaction();
        Task taskUpdate = mRealm.where(Task.class).equalTo("mId", task.getId()).findFirst();
        taskUpdate.setSelected(true);
        taskUpdate.setTaskColor(color);
        taskUpdate.setStopDateTask(System.currentTimeMillis());

        long time;
        if (task.getPauseStop() != 0) {
            time = task.getPauseDifferent();

        } else {
            time = task.getPauseDifferent() + (task.getStopDateTask() - task.getPauseStart());
        }
        taskUpdate.setButtonType(buttonType);
        taskUpdate.setPauseDifferent(time);
        mRealm.commitTransaction();
        return taskUpdate;
    }

    public Task pauseTask(Task task) {
        mRealm.beginTransaction();
        Task taskUpdate = mRealm.where(Task.class).equalTo("mId", task.getId()).findFirst();
        taskUpdate.setPauseStart(0);
        taskUpdate.setPauseStop(0);
        taskUpdate.setPauseStart(System.currentTimeMillis());
        mRealm.commitTransaction();
        return taskUpdate;
    }

    public Task resumeTask(Task task, long differentTime, long pauseStop) {
        mRealm.beginTransaction();
        Task taskUpdate = mRealm.where(Task.class).equalTo("mId", task.getId()).findFirst();
        taskUpdate.setPauseDifferent(differentTime);
        taskUpdate.setPauseStop(pauseStop);
        mRealm.commitTransaction();
        return taskUpdate;
    }


    public Task swipeResetTask(Task task, int mDefaultTaskColor) {
        mRealm.beginTransaction();
        Task taskUpdate = mRealm.where(Task.class).equalTo("mId", task.getId()).findFirst();
        taskUpdate.setTaskColor(mDefaultTaskColor);
        taskUpdate.setSelected(false);
        taskUpdate.setStopDateTask(0);
        taskUpdate.setStartDateTask(0);
        mRealm.commitTransaction();
        return taskUpdate;
    }

    public Task swipeResetTaskEnd(Task task, int mStartTaskColor) {

        mRealm.beginTransaction();
        Task taskUpdate = mRealm.where(Task.class).equalTo("mId", task.getId()).findFirst();
        taskUpdate.setTaskColor(mStartTaskColor);
        taskUpdate.setSelected(true);
        taskUpdate.setStopDateTask(0);
        mRealm.commitTransaction();
        return taskUpdate;
    }


    public void deleteTaskFromRealm(String id) {
        mRealm.beginTransaction();
        Task task = mRealm.where(Task.class).equalTo("mId", id).findFirst();
        task.deleteFromRealm();
        Statistic statistic = mRealm.where(Statistic.class).equalTo("mId", id).findFirst();
        statistic.deleteFromRealm();
        mRealm.commitTransaction();
    }

    public void deleteAllRealm(ArrayList<Task> mTaskArrayList, TaskAdapter mTaskAdapter) {
        final RealmResults<Task> tasks = mRealm.where(Task.class).findAll();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                tasks.deleteAllFromRealm();
            }
        });
        final RealmResults<Statistic> statistic = mRealm.where(Statistic.class).findAll();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                statistic.deleteAllFromRealm();
            }
        });

        mEditor.remove(KEY_VALUE);
        mEditor.clear();
        mEditor.commit();

        mTaskArrayList.clear();
        mTaskAdapter.notifyDataSetChanged();
    }

    public Task updateEnum(String str, Task task) {
        mRealm.beginTransaction();
        Task taskUpdate = mRealm.where(Task.class).equalTo("mId", task.getId()).findFirst();
        taskUpdate.setButtonType(str);
        mRealm.commitTransaction();
        return taskUpdate;
    }

    public void updateColorDateTask() {
        mRealm.beginTransaction();
        RealmResults<Task> realmResults = mRealm.where(Task.class).findAll();

        for (Task anArrayList : realmResults) {
            if (anArrayList.getStartDateTask() == 0) {
                anArrayList.setTaskColor(getDateDefaultColor());
            } else if (anArrayList.getStopDateTask() == 0) {
                anArrayList.setTaskColor(getDateStartColor());
            } else {
                anArrayList.setTaskColor(getDateEndColor());
            }
        }
        mRealm.commitTransaction();
    }


    public long defaultTime() {
        return mDefaultSetting.getLong("timeAlarm", 0);
    }

    public int getDateDefaultColor() {
        return mDefaultSetting.getInt(DEFAULT_COLOR_DATE, 0);
    }

    public int getDateStartColor() {
        return mDefaultSetting.getInt(START_COLOR_DATE, 0);
    }

    public int getDateEndColor() {
        return mDefaultSetting.getInt(END_COLOR_DATE, 0);
    }

    public void saveCheckedItem(MenuItem item) {
        mEditor.putString(CHECKED_ITEM, item.toString());
        mEditor.commit();
    }

    public MenuItem loadCheckedItem(Menu menu) {

        MenuItem menuItem = null;

        String isCk = mSettings.getString(CHECKED_ITEM, "");

        if (isCk.contains(mContext.getResources().getString(R.string.sort_a_z))) {
            menuItem = menu.findItem(R.id.action_a_z).setChecked(true);

        } else if (isCk.contains(mContext.getResources().getString(R.string.sort_z_a))) {
            menuItem = menu.findItem(R.id.action_z_a).setChecked(true);

        } else if (isCk.contains(mContext.getResources().getString(R.string.sort_date_ascending))) {
            menuItem = menu.findItem(R.id.action_first_end).setChecked(true);

        } else if (isCk.contains(mContext.getResources().getString(R.string.sort_date_descending))) {
            menuItem = menu.findItem(R.id.action_end_first).setChecked(true);

        }
        return menuItem;
    }

}
