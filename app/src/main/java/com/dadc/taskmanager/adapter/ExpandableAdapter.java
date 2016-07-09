package com.dadc.taskmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.model.Statistic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ExpandableAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<String> mListDataHeader; // header titles
    private HashMap<String, List<Statistic>> mListDataChild; // header child
    private static final String FORMAT_DATE = "HH:mm:ss";

    public ExpandableAdapter(Context mContext, List<String> mListDataHeader, HashMap<String, List<Statistic>> mListDataChild) {
        this.mContext = mContext;
        this.mListDataHeader = mListDataHeader;
        this.mListDataChild = mListDataChild;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {

        //to get children of the respective header(group).
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    // set the child view with value
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Statistic item = (Statistic) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.childrow, parent, false);
        }
        TextView title = (TextView) convertView.findViewById(R.id.textViewTitle);
        TextView hour = (TextView) convertView.findViewById(R.id.textViewHour);

        title.setText(item.getTitle());
        hour.setText(getFormatDate(item.getDifferentTime()));

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mListDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //set the header view with values
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.header, null);
        }
        TextView textViewTitle = (TextView) convertView.findViewById(R.id.exListGroup);
        textViewTitle.setText(headerTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public String getFormatDate(long milliseconds) {
        Date date = new Date();
        date.setTime(milliseconds);
        return new SimpleDateFormat(FORMAT_DATE).format(date);
    }

}