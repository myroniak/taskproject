package com.dadc.taskmanager.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.activity.TaskActivity;
import com.dadc.taskmanager.adapter.TaskAdapter;
import com.dadc.taskmanager.enumstate.ButtonType;
import com.dadc.taskmanager.model.Statistic;
import com.dadc.taskmanager.model.Task;
import com.dadc.taskmanager.receiver.Receiver;
import com.dadc.taskmanager.util.ManagerDataRealm;
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


    private ArrayList<Statistic> mStatisticArrayList;
    private ArrayList<Task> mTaskArrayList;

    private SwipeItemRecyclerMangerImpl mItemManger;
    private ManagerDataRealm mControlDataTask;
    private PendingIntent mAlarmSender;
    private AlarmManager mAlarmManager;
    private TaskAdapter adapter;
    private Context mContext;

    private int mStartTaskColor, mEndTaskColor, mDefaultTaskColor;

    public AdapterHelper(ArrayList<Statistic> mStatisticArrayList, ArrayList<Task> mTaskArrayList, Context mContext,
                         int mStartTaskColor, int mEndTaskColor, int mDefaultTaskColor, AlarmManager mAlarmManager,
                         SwipeItemRecyclerMangerImpl mItemManger, ManagerDataRealm mControlDataTask, TaskAdapter adapter) {

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

    // SPR - Start Pause Resume
    public void buttonEventSPR(ButtonType mButtonType, int position) {
        Task task = getItem(position);

        long differentTime = getItem(position).getPauseDifferent();
        if (mButtonType == ButtonType.PAUSE) {

            mTaskArrayList.set(position, mControlDataTask.pauseTask(task));
            if (getItem(position).getStartDateTask() == 0) {
                setNotification(position, task.getMaxTime());
                mTaskArrayList.set(position, mControlDataTask.startTask(task, mStartTaskColor));
            }

        } else if (mButtonType == ButtonType.RESUME) {

            long pauseStart = task.getPauseStart();
            long pauseStop = System.currentTimeMillis();

            differentTime += different(pauseStart, pauseStop);
            mTaskArrayList.set(position, mControlDataTask.resumeTask(task, differentTime, pauseStop));
        }

        mControlDataTask.savePreferenceDataTask(mTaskArrayList);
        notifyDataSetChanged();
    }

    public void stopDateTask(int position) {
        Task task = getItem(position);
        mTaskArrayList.set(position, mControlDataTask.stopTask(ButtonType.PLAY.name(), task, mEndTaskColor));


        /**
         * Add or set data to statistic
         * */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(task.getStartDateTask());


        int month = calendar.get(Calendar.MONTH) + 1;

        if (mStatisticArrayList.size() == 0) {
            mStatisticArrayList.add(new Statistic(task.getId(), task.getTitle(), month, task.getPauseDifferent()));
        } else {
            for (int i = 0; i < mStatisticArrayList.size(); i++) {
                if (mStatisticArrayList.get(i).getId().contains(task.getId())) {
                    mStatisticArrayList.set(i, mControlDataTask.diffStatistic(mStatisticArrayList.get(i), mStatisticArrayList.get(i).getDifferentTime() + task.getPauseDifferent()));
                    break;
                } else {
                    mStatisticArrayList.add(new Statistic(task.getId(), task.getTitle(), month, task.getPauseDifferent()));
                    break;
                }
            }
        }
        mControlDataTask.saveStatistic(mStatisticArrayList);

        /**
         * End add or set data to statistic
         * */

        mControlDataTask.savePreferenceDataTask(mTaskArrayList); //Save data in SharedPreferences
        notifyDataSetChanged();

    }

    public void swipeResetTask(int position, SwipeLayout swipeLayout) {
        stopAlarmManager();

        Task task = getItem(position);
        mTaskArrayList.set(position, mControlDataTask.swipeResetTask(task, mDefaultTaskColor));

        mItemManger.removeShownLayouts(swipeLayout);
        mItemManger.closeAllItems();

        mControlDataTask.savePreferenceDataTask(mTaskArrayList); //Save data in SharedPreferences
        notifyDataSetChanged();

    }

    public void swipeResetTaskEnd(int position, SwipeLayout swipeLayout) {
        stopAlarmManager();
        setNotification(position, getItem(position).getMaxTime());

        Task task = getItem(position);
        mTaskArrayList.set(position, mControlDataTask.swipeResetTaskEnd(task, mStartTaskColor));

        mItemManger.removeShownLayouts(swipeLayout);
        mItemManger.closeAllItems();

        mControlDataTask.savePreferenceDataTask(mTaskArrayList); //Save data in SharedPreferences
        notifyDataSetChanged();

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
    }

    public void submitDrawable(ImageButton imageButton, int position) {
        Task task = getItem(position);
        switch (ButtonType.valueOf(getItem(position).getButtonType())) {

            case PLAY:
                imageButton.setImageResource(R.drawable.ic_pause_white_48dp);
                mControlDataTask.updateEnum(ButtonType.PAUSE.name(), task);
                break;

            case PAUSE:
                imageButton.setImageResource(R.drawable.ic_restore_white_48dp);
                mControlDataTask.updateEnum(ButtonType.RESUME.name(), task);
                break;

            case RESUME:
                imageButton.setImageResource(R.drawable.ic_pause_white_48dp);
                mControlDataTask.updateEnum(ButtonType.PAUSE.name(), task);
                break;
            default:
                break;
        }
        mTaskArrayList.set(position, task);
        mControlDataTask.savePreferenceDataTask(mTaskArrayList);
        notifyDataSetChanged();
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

    public long different(long pauseStart, long pauseStop) {
        return pauseStop - pauseStart;
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    public void stopAlarmManager() {
        if (mAlarmSender != null) {
            mAlarmManager.cancel(mAlarmSender);
        }
    }

    public void openSnackbar(SwipeLayout rv, CharSequence title) {
        Snackbar.make(rv, title, Snackbar.LENGTH_SHORT).show();
    }
}
