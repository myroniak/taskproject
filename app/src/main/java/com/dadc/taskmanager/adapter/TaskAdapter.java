package com.dadc.taskmanager.adapter;

/**
 * Created by bomko on 27.05.16.
 */

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.model.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;


public class TaskAdapter extends BaseAdapter {
    Context _mContext;
    ArrayList<Task> _mTaskArrayList;
    DateFormat mDateFormatFull, mDateFormatShort;
    String mStartTime, mStopTime, mElapsedTime;
    Resources mResources;

    public TaskAdapter(Context context, ArrayList<Task> mTaskArrayList) {
        _mContext = context;
        _mTaskArrayList = mTaskArrayList;
    }

    @Override
    public int getCount() {
        return _mTaskArrayList.size();
    }

    @Override
    public Task getItem(int position) {
        return _mTaskArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView mTextViewTitle;
        TextView mTextViewDescription;
        TextView mTextViewTime;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        Task mTask = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) _mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.listview_task_item, parent, false);

            mResources = _mContext.getResources();

            mDateFormatFull = new SimpleDateFormat(mResources.getString(R.string.fullFormat));
            mDateFormatShort = new SimpleDateFormat(mResources.getString(R.string.shortFormat));
            mDateFormatShort.setTimeZone(TimeZone.getTimeZone(mResources.getString(R.string.timeZone)));

            viewHolder = new ViewHolder();
            viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.textViewTaskTitle);
            viewHolder.mTextViewDescription = (TextView) convertView.findViewById(R.id.textViewTaskDescription);
            viewHolder.mTextViewTime = (TextView) convertView.findViewById(R.id.textViewTaskTime);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        mStartTime = mDateFormatFull.format(mTask.getStartTimeTask());
        mStopTime = mDateFormatFull.format(mTask.getStopTimeTask());
        mElapsedTime = mDateFormatShort.format(mTask.getStopTimeTask() - mTask.getStartTimeTask());

        convertView.setBackgroundColor(mResources.getColor(mTask.getTaskColor()));

        viewHolder.mTextViewTitle.setText(mTask.getTitle());
        viewHolder.mTextViewDescription.setText(mTask.getDescription());

        viewHolder.mTextViewTime.setVisibility(View.GONE);

//Log.d("myLog", "STOP: " + mTask.getStopTimeTask() + " START: " + mTask.getStopTimeTask());
        if (mTask.isSelected() && mTask.getStartTimeTask() > 0 && mTask.getStopTimeTask() == 0 ) {
            String mBeginDate = mStartTime + mResources.getString(R.string.hyphen) + mResources.getString(R.string.noSetDate);
            viewHolder.mTextViewTime.setText(mBeginDate);
            viewHolder.mTextViewTime.setVisibility(View.VISIBLE);

        } else if (mTask.isSelected() && mTask.getStopTimeTask() > 0) {
            String mFinishDate = mStartTime + mResources.getString(R.string.hyphen) + mStopTime + mResources.getString(R.string.spaceValue) + mElapsedTime;
            viewHolder.mTextViewTime.setText(mFinishDate);
            viewHolder.mTextViewTime.setVisibility(View.VISIBLE);

        } else {
            viewHolder.mTextViewTime.setVisibility(View.GONE);
        }
        return convertView;
    }

}

