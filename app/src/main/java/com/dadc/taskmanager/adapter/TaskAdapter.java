package com.dadc.taskmanager.adapter;

/**
 * Created by bomko on 27.05.16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.model.Task;

import java.util.ArrayList;


public class TaskAdapter extends BaseAdapter {
    Context _mContext;
    ArrayList<Task> _mTask;

    public TaskAdapter(Context context, ArrayList<Task> mTaskArrayList) {
        _mContext = context;
        _mTask = mTaskArrayList;
    }

    @Override
    public int getCount() {
        return _mTask.size();
    }

    @Override
    public Task getItem(int position) {
        return _mTask.get(position);
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

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) _mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.listview_task_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.textViewTaskTitle);
            viewHolder.mTextViewDescription = (TextView) convertView.findViewById(R.id.textViewTaskDescription);
            viewHolder.mTextViewTime = (TextView) convertView.findViewById(R.id.textViewTaskTime);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        convertView.setBackgroundColor(_mContext.getResources().getColor(getItem(position).getTaskColor()));


        viewHolder.mTextViewTitle.setText(getItem(position).getTitle());
        viewHolder.mTextViewDescription.setText(getItem(position).getDescription());

        viewHolder.mTextViewTime.setVisibility(View.GONE);
        viewHolder.mTextViewTime.setText(getItem(position).getFullDate());

        if (_mTask.get(position).isSelected()) {
            viewHolder.mTextViewTime.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mTextViewTime.setVisibility(View.GONE);
        }
        return convertView;
    }

}

