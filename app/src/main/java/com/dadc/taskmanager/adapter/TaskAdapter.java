package com.dadc.taskmanager.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.activity.TaskActivity;
import com.dadc.taskmanager.broadcastResiever.Receiver;
import com.dadc.taskmanager.model.Task;
import com.dadc.taskmanager.util.ControlDataTask;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class TaskAdapter extends RecyclerSwipeAdapter<TaskAdapter.MyViewHolder> {

    private static final String KEY_POSITION_ITEM = "position_item";
    private static final String KEY_EDIT_ITEM = "edit_item";
    private static final String KEY_TITLE_ACTIVITY = "title_edit_task";
    private static final String KEY_TITLE = "title";
    private static final String KEY_POSITION = "position";
    private static final String KEY_ALARM_TIME = "timeAlarm";
    private static final String KEY_ACTION = "action";
    private static final int REQUEST_CODE_TASK = 1;

    private ControlDataTask mControlDataTask;
    private ArrayList<Task> mTaskArrayList;
    private Context mContext;
    private DateFormat mDateFormatFull, mDateFormatShort;
    private String mStartDate, mStopDate, mElapsedDate;
    private Resources mResources;
    private AlarmManager mAlarmManager;
    private PendingIntent mAlarmSender;

    private int mStartTaskColor, mEndTaskColor, mDefaultTaskColor;
    private long time;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageButton mButtonStart, mButtonStop, mSwipeReset;
        ImageButton mSwipeEdit, mSwipeDelete, mSwipeResetEnd;
        SwipeLayout swipeLayout;
        TextView mTextViewTitle, mTextViewDate, mTextViewDescription;

        public MyViewHolder(View view) {
            super(view);

            mTextViewTitle = (TextView) view.findViewById(R.id.textViewTaskTitle);
            mTextViewDescription = (TextView) view.findViewById(R.id.textViewTaskDescription);
            mTextViewDate = (TextView) view.findViewById(R.id.textViewTaskDate);

            mButtonStart = (ImageButton) view.findViewById(R.id.btnStartTask);
            mButtonStop = (ImageButton) view.findViewById(R.id.btnStopTask);

            mSwipeEdit = (ImageButton) view.findViewById(R.id.btnSwipeEditTask);
            mSwipeDelete = (ImageButton) view.findViewById(R.id.btnSwipeDeleteTask);
            mSwipeReset = (ImageButton) view.findViewById(R.id.btn_swipe_reset);
            mSwipeResetEnd = (ImageButton) view.findViewById(R.id.btn_swipe_reset_end);

            swipeLayout = (SwipeLayout) view.findViewById(R.id.swipe);

        }
    }


    public TaskAdapter(Context mContext, ArrayList<Task> mTaskArrayList) {
        this.mContext = mContext;
        this.mTaskArrayList = mTaskArrayList;


        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public int getItemCount() {
        return mTaskArrayList.size();
    }

    public Task getItem(int position) {
        return mTaskArrayList.get(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_task_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        mResources = mContext.getResources();

        mDateFormatFull = new SimpleDateFormat(mResources.getString(R.string.fullFormat));
        mDateFormatShort = new SimpleDateFormat(mResources.getString(R.string.shortFormat));
        mDateFormatShort.setTimeZone(TimeZone.getTimeZone(mResources.getString(R.string.timeZone)));

        mStartDate = mDateFormatFull.format(getItem(position).getStartDateTask());
        mStopDate = mDateFormatFull.format(getItem(position).getStopDateTask());
        mElapsedDate = mDateFormatShort.format(getItem(position).getStopDateTask() - getItem(position).getStartDateTask());

        mControlDataTask = new ControlDataTask(mContext);

        mDefaultTaskColor = mControlDataTask.getDateDefaultColor();
        mStartTaskColor = mControlDataTask.getDateStartColor();
        mEndTaskColor = mControlDataTask.getDateEndColor();

        holder.itemView.setBackgroundColor(getItem(position).getTaskColor());

        holder.mTextViewTitle.setText(getItem(position).getTitle());
        holder.mTextViewDescription.setText(getItem(position).getDescription());
        holder.mTextViewDate.setVisibility(View.GONE);

        if (getItem(position).isSelected() && getItem(position).getStartDateTask() > 0 && getItem(position).getStopDateTask() == 0) {
            String mBeginDate = mStartDate + mResources.getString(R.string.hyphen) + mResources.getString(R.string.noSetDate);
            holder.mTextViewDate.setText(mBeginDate);
            holder.mTextViewDate.setVisibility(View.VISIBLE);

        } else if (getItem(position).isSelected() && getItem(position).getStopDateTask() > 0) {
            String mFinishDate = mStartDate + mResources.getString(R.string.hyphen) + mStopDate + mResources.getString(R.string.spaceValue) + mElapsedDate;
            holder.mTextViewDate.setText(mFinishDate);
            holder.mTextViewDate.setVisibility(View.VISIBLE);

        } else {
            holder.mTextViewDate.setVisibility(View.GONE);
        }


        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        //Add drag edge
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bird_bottom));
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.swipeLayout.findViewById(R.id.layout_right));

        holder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                //   YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {

            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });

        holder.mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem(position).getStartDateTask() == 0) {

                    openSnackbar(holder.swipeLayout, mResources.getString(R.string.titleStartTaskSnackBar));

                    setNotification(position);
                    //Start task
                    getItem(position).setTaskColor(mStartTaskColor);
                    getItem(position).setSelected(true);
                    //Set date to TextView after start
                    getItem(position).setStartDateTask(startDateTask(position));
                    mControlDataTask.savePreferenceDataTask(mTaskArrayList); //Save data in SharedPreferences
                } else if (getItem(position).getStopDateTask() != 0) {

                    openSnackbar(holder.swipeLayout, mContext.getResources().getString(R.string.title_task_end));

                } else {

                    openSnackbar(holder.swipeLayout, mContext.getResources().getString(R.string.title_task_start));


                }
            }
        });

        holder.mButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem(position).getStopDateTask() == 0) {

                    openSnackbar(holder.swipeLayout, mResources.getString(R.string.titleEndTaskSnackBar));

                    stopDateTask(position);

                    stopAlarmManager();
                } else {

                    openSnackbar(holder.swipeLayout, mResources.getString(R.string.title_task_end));
                }

            }
        });

        holder.mSwipeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Edit task item
                Intent intent = new Intent(mContext, TaskActivity.class);
                intent.putExtra(KEY_EDIT_ITEM, getItem(position));
                intent.putExtra(KEY_POSITION_ITEM, position);
                intent.putExtra(KEY_TITLE_ACTIVITY, mResources.getString(R.string.activity_edit_task));
                ((AppCompatActivity) mContext).startActivityForResult(intent, REQUEST_CODE_TASK);

                stopAlarmManager();
            }
        });

        holder.mSwipeDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemManger.removeShownLayouts(holder.swipeLayout);
                mTaskArrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
                mItemManger.closeAllItems();

                mControlDataTask.savePreferenceDataTask(mTaskArrayList); //Save data in SharedPreferences

                stopAlarmManager();
            }
        });

        holder.mSwipeReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openSnackbar(holder.swipeLayout, mResources.getString(R.string.titleResetTaskSnackBar));
                if (getItem(position).getStopDateTask() == 0 && getItem(position).getStartDateTask() != 0) {
                    //Start task
                    getItem(position).setTaskColor(mDefaultTaskColor);
                    getItem(position).setSelected(false);

                    //Set date to TextView after ending
                    getItem(position).setStopDateTask(0);
                    getItem(position).setStartDateTask(0);
                    mItemManger.removeShownLayouts(holder.swipeLayout);
                    mItemManger.closeAllItems();

                    notifyDataSetChanged();


                    mControlDataTask.savePreferenceDataTask(mTaskArrayList); //Save data in SharedPreferences
                } else if (getItem(position).getStopDateTask() != 0) {

                    openSnackbar(holder.swipeLayout, mContext.getResources().getString(R.string.title_task_end));

                } else {

                    openSnackbar(holder.swipeLayout, mContext.getResources().getString(R.string.title_task_no_start));


                }
            }
        });

        holder.mSwipeResetEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem(position).getStopDateTask() != 0) {


                    setNotification(position);

                    openSnackbar(holder.swipeLayout, mResources.getString(R.string.titleResetEndTaskSnackBar));
                    getItem(position).setTaskColor(mStartTaskColor);
                    getItem(position).setSelected(true);
                    getItem(position).setStopDateTask(0);
                    mItemManger.removeShownLayouts(holder.swipeLayout);
                    mItemManger.closeAllItems();
                    notifyDataSetChanged();


                    mControlDataTask.savePreferenceDataTask(mTaskArrayList); //Save data in SharedPreferences
                } else if (getItem(position).getStartDateTask() != 0) {

                    openSnackbar(holder.swipeLayout, mContext.getResources().getString(R.string.title_task_start));

                } else {

                    openSnackbar(holder.swipeLayout, mContext.getResources().getString(R.string.title_task_no_start));


                }
            }
        });

        mItemManger.bindView(holder.itemView, position);

    }

    public long startDateTask(int position) {

        getItem(position).setStopDateTask(0);
        notifyDataSetChanged();

        return System.currentTimeMillis();
    }


    public void stopDateTask(int position) {

        mControlDataTask = new ControlDataTask(mContext);
        if (getItem(position).getStartDateTask() != 0) {

            getItem(position).setTaskColor(mEndTaskColor);
            getItem(position).setSelected(true);
            getItem(position).setStopDateTask(System.currentTimeMillis());
            notifyDataSetChanged();

            mControlDataTask.savePreferenceDataTask(mTaskArrayList); //Save data in SharedPreferences

        }
        notifyDataSetChanged();

    }


    public void openSnackbar(SwipeLayout rv, CharSequence title) {
        Snackbar.make(rv, title, Snackbar.LENGTH_SHORT).show();
    }

    public Intent createIntent(String action, String title, int position) {

        Intent mIntent = new Intent(mContext, Receiver.class);
        mIntent.setAction(action);
        mIntent.putExtra(KEY_TITLE, title);
        mIntent.putExtra(KEY_POSITION, position);

        return mIntent;
    }

    public void setNotification(int mPosition) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        time = preferences.getLong(KEY_ALARM_TIME, 0);

        Intent mIntent = createIntent(KEY_ACTION + mPosition, mTaskArrayList.get(mPosition).getTitle(), mPosition);

        mAlarmSender = PendingIntent.getBroadcast(mContext, mPosition, mIntent, 0);

        mAlarmManager.setExact(AlarmManager.RTC, System.currentTimeMillis() + time, mAlarmSender);

        Log.d("myLog","time:" + time);

    }

    public void stopAlarmManager() {

        if (mAlarmSender != null) {
            mAlarmManager.cancel(mAlarmSender);
        }

    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

}