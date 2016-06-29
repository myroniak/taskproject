package com.dadc.taskmanager.broadcastResiever;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.activity.MainActivity;

public class Receiver extends BroadcastReceiver {

    private static final String KEY_TITLE = "title";
    private static final String KEY_POSITION = "position";

    private NotificationManager mNotificationManager;
    private String mTitle;
    private int mPosition;

    @Override
    public void onReceive(final Context mContext, Intent mParentIntent) {

        mTitle = mParentIntent.getStringExtra(KEY_TITLE);
        mPosition = mParentIntent.getIntExtra(KEY_POSITION, 0);

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent mIntent = new Intent(mContext, MainActivity.class);
        PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPosition, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification.Builder mNotificationBuilder = new Notification.Builder(mContext);

        mNotificationBuilder.setAutoCancel(true);
        mNotificationBuilder.setContentTitle(mTitle);
        mNotificationBuilder.setContentText(mContext.getResources().getString(R.string.content_text));
        mNotificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mNotificationBuilder.setContentIntent(mPendingIntent);

        MainActivity.mTaskAdapter.stopDateTask(mPosition);

        mNotificationManager.notify(mPosition, mNotificationBuilder.build());

    }

}