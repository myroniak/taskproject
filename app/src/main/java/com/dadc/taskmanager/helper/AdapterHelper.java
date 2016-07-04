package com.dadc.taskmanager.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.activity.TaskActivity;
import com.dadc.taskmanager.adapter.TaskAdapter;
import com.dadc.taskmanager.enumstate.ButtonType;
import com.dadc.taskmanager.model.Statistic;
import com.dadc.taskmanager.model.Task;
import com.dadc.taskmanager.receiver.Receiver;
import com.dadc.taskmanager.util.ManagerData;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by bomko on 04.07.16.
 */
public class AdapterHelper {


    private static final String KEY_POSITION_ITEM = "position_item";
    private static final String KEY_EDIT_ITEM = "edit_item";
    private static final String KEY_TITLE_ACTIVITY = "title_edit_task";
    private static final String KEY_TITLE = "title";
    private static final String KEY_POSITION = "position";
    private static final String KEY_ACTION = "action";
    private static final int REQUEST_CODE_TASK_EDIT = 2;
    private TaskAdapter adapter;
    private Context mContext;
    private ArrayList<Statistic> mStatisticArrayList;
    private ArrayList<Task> mTaskArrayList;
    private int mStartTaskColor, mEndTaskColor, mDefaultTaskColor;
    private PendingIntent mAlarmSender;
    private AlarmManager mAlarmManager;
    private SwipeItemRecyclerMangerImpl mItemManger;
    private ManagerData mControlDataTask;

    public AdapterHelper(ArrayList<Statistic> mStatisticArrayList, ArrayList<Task> mTaskArrayList, Context mContext,
                         int mStartTaskColor, int mEndTaskColor, int mDefaultTaskColor, AlarmManager mAlarmManager,
                         SwipeItemRecyclerMangerImpl mItemManger, ManagerData mControlDataTask, TaskAdapter adapter) {
        this.mStatisticArrayList = mStatisticArrayList;
        this.mTaskArrayList = mTaskArrayList;
        this.mContext = mContext;
        this.mStartTaskColor = mStartTaskColor;
        this.mEndTaskColor = mEndTaskColor;
        this.mDefaultTaskColor = mDefaultTaskColor;
        this.mAlarmManager = mAlarmManager;
        this.mItemManger = mItemManger;
        this.mControlDataTask = mControlDataTask;
        this.adapter = adapter;
    }

    public Task getItem(int position) {
        return mTaskArrayList.get(position);
    }

    public void stopDateTask(int position) {

        getItem(position).setSelected(true);
        getItem(position).setTaskColor(mEndTaskColor);
        getItem(position).setStopDateTask(System.currentTimeMillis());

        long time;
        if (getItem(position).getPauseStop() != 0) {
            time = getItem(position).getPauseDifferent();

        } else {
            time = getItem(position).getPauseDifferent() + (getItem(position).getStopDateTask() - getItem(position).getPauseStart());
        }

        getItem(position).setButtonType(ButtonType.PLAY.name());
        getItem(position).setPauseDifferent(time);

        /**
         * Add data to statistic
         * */
        Task task = getItem(position);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getItem(position).getStartDateTask());


        int month = calendar.get(Calendar.MONTH) + 1;

        if (mStatisticArrayList.size() == 0) {
            mStatisticArrayList.add(new Statistic(task.getId(), task.getTitle(), month, task.getPauseDifferent()));
        } else {
            for (int i = 0; i < mStatisticArrayList.size(); i++) {
                if (mStatisticArrayList.get(i).getId().contains(task.getId())) {

                    mStatisticArrayList.get(i).setDifferentTime(mStatisticArrayList.get(i).getDifferentTime() + task.getPauseDifferent());
                    break;
                } else {
                    mStatisticArrayList.add(new Statistic(task.getId(), task.getTitle(), month, task.getPauseDifferent()));

                }
            }
        }
        /**
         * End set data to statistic
         * */

        notifyDataSetChanged();
        mControlDataTask.savePreferenceDataTask(mTaskArrayList); //Save data in SharedPreferences
    }

    public void swipeResetTask(int position, SwipeLayout swipeLayout) {
        stopAlarmManager();

        getItem(position).setTaskColor(mDefaultTaskColor);
        getItem(position).setSelected(false);
        getItem(position).setStopDateTask(0);
        getItem(position).setStartDateTask(0);

        mItemManger.removeShownLayouts(swipeLayout);
        mItemManger.closeAllItems();

        notifyDataSetChanged();
        mControlDataTask.savePreferenceDataTask(mTaskArrayList); //Save data in SharedPreferences

    }

    public void swipeResetTaskEnd(int position, SwipeLayout swipeLayout) {
        stopAlarmManager();
        setNotification(position, getItem(position).getMaxTime());

        getItem(position).setTaskColor(mStartTaskColor);
        getItem(position).setSelected(true);
        getItem(position).setStopDateTask(0);

        mItemManger.removeShownLayouts(swipeLayout);
        mItemManger.closeAllItems();

        notifyDataSetChanged();
        mControlDataTask.savePreferenceDataTask(mTaskArrayList); //Save data in SharedPreferences

    }

    public void swipeEitTask(int position, SwipeLayout swipeLayout) {
        //Edit task item
        Intent intent = new Intent(mContext, TaskActivity.class);
        intent.putExtra(KEY_EDIT_ITEM, getItem(position));
        intent.putExtra(KEY_POSITION_ITEM, position);
        intent.putExtra(KEY_TITLE_ACTIVITY, mContext.getResources().getString(R.string.activity_edit_task));
        ((AppCompatActivity) mContext).startActivityForResult(intent, REQUEST_CODE_TASK_EDIT);
        mItemManger.removeShownLayouts(swipeLayout);
        mItemManger.closeAllItems();
        stopAlarmManager();
    }


    public void openSnackbar(SwipeLayout rv, CharSequence title) {
        Snackbar.make(rv, title, Snackbar.LENGTH_SHORT).show();
    }

    public Intent intentToReceiver(String action, String title, int position) {
        Intent mIntent = new Intent(mContext, Receiver.class);
        mIntent.setAction(action);
        mIntent.putExtra(KEY_TITLE, title);
        mIntent.putExtra(KEY_POSITION, position);
        return mIntent;
    }

    public void setNotification(int position, long timeMillis) {

        Intent intent = intentToReceiver(KEY_ACTION + position, mTaskArrayList.get(position).getTitle(), position);
        mAlarmSender = PendingIntent.getBroadcast(mContext, position, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.setExact(AlarmManager.RTC, System.currentTimeMillis() + timeMillis, mAlarmSender);
    }

    public void stopAlarmManager() {
        if (mAlarmSender != null) {
            mAlarmManager.cancel(mAlarmSender);
        }
    }

    public void buttonEvent(ButtonType mButtonType, int position) {

        long differentTime = getItem(position).getPauseDifferent();
        if (mButtonType == ButtonType.PAUSE) {

            getItem(position).setPauseStart(0);
            getItem(position).setPauseStop(0);

            getItem(position).setPauseStart(System.currentTimeMillis());

            if (getItem(position).getStartDateTask() == 0) {
                setNotification(position, getItem(position).getMaxTime());

                getItem(position).setSelected(true);
                getItem(position).setTaskColor(mStartTaskColor);
                getItem(position).setStartDateTask(System.currentTimeMillis());
                getItem(position).setStopDateTask(0);

                notifyDataSetChanged();
                mControlDataTask.savePreferenceDataTask(mTaskArrayList); //Save data in SharedPreferences
            }


        } else if (mButtonType == ButtonType.RESUME) {
            getItem(position).setPauseStop(System.currentTimeMillis());

            long pauseStart = getItem(position).getPauseStart();
            long pauseStop = System.currentTimeMillis();

            differentTime += different(pauseStart, pauseStop);
            getItem(position).setPauseDifferent(differentTime);

        }

    }

    public long different(long pauseStart, long pauseStop) {
        return pauseStop - pauseStart;
    }

    public void submitDrawable(ImageButton imageButton, int position) {

        switch (ButtonType.valueOf(getItem(position).getButtonType())) {

            case PLAY:
                imageButton.setImageResource(R.drawable.ic_pause_white_48dp);
                getItem(position).setButtonType(ButtonType.PAUSE.name());
                break;

            case PAUSE:
                imageButton.setImageResource(R.drawable.ic_restore_white_48dp);
                getItem(position).setButtonType(ButtonType.RESUME.name());
                break;

            case RESUME:
                imageButton.setImageResource(R.drawable.ic_pause_white_48dp);
                getItem(position).setButtonType(ButtonType.PAUSE.name());
                break;
            default:
                break;
        }
    }

    public void updateDrawable(ImageButton imageButton, int position) {
        ButtonType mButtonType = ButtonType.valueOf(getItem(position).getButtonType());
        switch (mButtonType) {
            case PLAY:
                imageButton.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                break;

            case PAUSE:
                imageButton.setImageResource(R.drawable.ic_pause_white_48dp);
                break;

            case RESUME:
                imageButton.setImageResource(R.drawable.ic_restore_white_48dp);
                break;
            default:
                break;
        }
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }
}
