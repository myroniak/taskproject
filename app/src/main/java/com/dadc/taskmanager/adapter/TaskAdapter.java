package com.dadc.taskmanager.adapter;

import android.app.AlarmManager;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.asynctask.ImageDownloaderImage;
import com.dadc.taskmanager.enumstate.ButtonType;
import com.dadc.taskmanager.helper.AdapterHelper;
import com.dadc.taskmanager.model.Statistic;
import com.dadc.taskmanager.model.Task;
import com.dadc.taskmanager.util.ManagerData;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class TaskAdapter extends RecyclerSwipeAdapter<TaskAdapter.MyViewHolder> {

    private ManagerData mControlDataTask;
    private ArrayList<Task> mTaskArrayList;
    private Context mContext;
    private String mStartDate, mStopDate, mElapsedDate;
    private Resources mResources;
    private AlarmManager mAlarmManager;
    private ArrayList<Statistic> mStatisticArrayList;
    private AdapterHelper mAdapterHelper;
    private int mStartTaskColor, mEndTaskColor, mDefaultTaskColor;

    public TaskAdapter(Context mContext, ArrayList<Task> mTaskArrayList, ArrayList<Statistic> mStatisticArrayList) {
        this.mContext = mContext;
        this.mTaskArrayList = mTaskArrayList;
        this.mStatisticArrayList = mStatisticArrayList;
        mControlDataTask = ManagerData.getInstance(mContext);
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
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

        initialData(position);

        mAdapterHelper.updateDrawable(holder.mButtonStart, position);

        new ImageDownloaderImage(holder.mImageAvatar).execute(mTaskArrayList.get(position).getUrl());

        final Task myTask = getItem(position);

        holder.itemView.setBackgroundColor(getItem(position).getTaskColor());

        holder.mTextViewTitle.setText(getItem(position).getTitle());
        holder.mTextViewDescription.setText(getItem(position).getDescription());
        holder.mTextViewDate.setVisibility(View.GONE);

        if (getItem(position).getSelected() && getItem(position).getStartDateTask() > 0 && getItem(position).getStopDateTask() == 0) {
            String mBeginDate = mStartDate + mResources.getString(R.string.hyphen) + mResources.getString(R.string.no_set_date);
            holder.mTextViewDate.setText(mBeginDate);
            holder.mTextViewDate.setVisibility(View.VISIBLE);

        } else if (getItem(position).getSelected() && getItem(position).getStopDateTask() > 0) {
            String mFinishDate = mStartDate + mResources.getString(R.string.hyphen) + mStopDate + mResources.getString(R.string.space_value) + mElapsedDate;
            holder.mTextViewDate.setText(mFinishDate);
            holder.mTextViewDate.setVisibility(View.VISIBLE);

        } else {
            holder.mTextViewDate.setVisibility(View.GONE);
        }

        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        //Add drag edge
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bird_bottom));
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.swipeLayout.findViewById(R.id.layout_right));

        holder.mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem(position).getStopDateTask() == 0) {

                    mAdapterHelper.submitDrawable(holder.mButtonStart, position);
                    mAdapterHelper.buttonEvent(ButtonType.valueOf(getItem(position).getButtonType()), position);

                } else {
                    mAdapterHelper.openSnackbar(holder.swipeLayout, mContext.getResources().getString(R.string.title_task_end));
                }
            }
        });

        holder.mButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem(position).getStopDateTask() == 0) {
                    mAdapterHelper.openSnackbar(holder.swipeLayout, mResources.getString(R.string.title_end_task_snackbar));

                    mAdapterHelper.stopDateTask(position);
                    mAdapterHelper.stopAlarmManager();

                } else if (getItem(position).getStopDateTask() != 0) {
                    mAdapterHelper.openSnackbar(holder.swipeLayout, mResources.getString(R.string.title_task_end));
                } else {
                    mAdapterHelper.openSnackbar(holder.swipeLayout, mResources.getString(R.string.title_task_no_start));
                }

            }
        });

        holder.mSwipeReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getItem(position).getStopDateTask() == 0 && getItem(position).getStartDateTask() != 0) {
                    mAdapterHelper.openSnackbar(holder.swipeLayout, mResources.getString(R.string.title_reset_task_snackbar));

                    mAdapterHelper.swipeResetTask(position, holder.swipeLayout);

                } else if (getItem(position).getStopDateTask() != 0) {
                    mAdapterHelper.openSnackbar(holder.swipeLayout, mContext.getResources().getString(R.string.title_task_end));
                } else {
                    mAdapterHelper.openSnackbar(holder.swipeLayout, mContext.getResources().getString(R.string.title_task_no_start));
                }
            }
        });

        holder.mSwipeResetEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem(position).getStopDateTask() != 0) {
                    mAdapterHelper.openSnackbar(holder.swipeLayout, mResources.getString(R.string.title_reset_end_task_snackbar));

                    mAdapterHelper.swipeResetTaskEnd(position, holder.swipeLayout);

                } else if (getItem(position).getStartDateTask() != 0) {
                    mAdapterHelper.openSnackbar(holder.swipeLayout, mContext.getResources().getString(R.string.title_task_start));
                } else {
                    mAdapterHelper.openSnackbar(holder.swipeLayout, mContext.getResources().getString(R.string.title_task_no_start));
                }
            }
        });

        holder.mSwipeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapterHelper.swipeEitTask(position, holder.swipeLayout);
            }
        });

        holder.mSwipeDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRemoveTask(position, holder.swipeLayout, myTask);
            }
        });

        mItemManger.bindView(holder.itemView, position);

    }

    public void swipeRemoveTask(int position, SwipeLayout swipeLayout, Task task) {

        mItemManger.removeShownLayouts(swipeLayout);
        mTaskArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
        mAdapterHelper.stopAlarmManager();
        mItemManger.closeAllItems();

        mControlDataTask.savePreferenceDataTask(mTaskArrayList);

    }

    public void stopTaskAlarm(int position) {
        mAdapterHelper.stopDateTask(position);

    }

    public void initialData(int position) {
        mAdapterHelper = new AdapterHelper(mStatisticArrayList, mTaskArrayList, mContext, mStartTaskColor,
                mEndTaskColor, mDefaultTaskColor, mAlarmManager, mItemManger, mControlDataTask, this);
        mResources = mContext.getResources();

        DateFormat mDateFormatFull = new SimpleDateFormat(mResources.getString(R.string.full_format));
        DateFormat mDateFormatShort = new SimpleDateFormat(mResources.getString(R.string.short_format));

        mDateFormatShort.setTimeZone(TimeZone.getTimeZone(mResources.getString(R.string.time_zone)));

        mStartDate = mDateFormatFull.format(getItem(position).getStartDateTask());
        mStopDate = mDateFormatFull.format(getItem(position).getStopDateTask());
        mElapsedDate = mDateFormatShort.format(getItem(position).getPauseDifferent());

        mDefaultTaskColor = mControlDataTask.getDateDefaultColor();
        mStartTaskColor = mControlDataTask.getDateStartColor();
        mEndTaskColor = mControlDataTask.getDateEndColor();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageButton mButtonStart, mButtonStop, mSwipeReset;
        ImageButton mSwipeEdit, mSwipeDelete, mSwipeResetEnd;
        ImageView mImageAvatar;
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

            mImageAvatar = (ImageView) view.findViewById(R.id.imageViewAvatarRec);

            swipeLayout = (SwipeLayout) view.findViewById(R.id.swipe);

        }
    }
}