package com.dadc.taskmanager.receiver;

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
    MainActivity mActivity;

    @Override
    public void onReceive(Context mContext, Intent mParentIntent) {

        mActivity = (MainActivity) MainActivity.getAppContext();

        String title = mParentIntent.getStringExtra(KEY_TITLE);
        final int position = mParentIntent.getIntExtra(KEY_POSITION, 0);

        Notification.Builder mNotificationBuilder = new Notification.Builder(mContext);
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent mIntent = new Intent(mContext, MainActivity.class);
        PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, position, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotificationBuilder.setAutoCancel(true);
        mNotificationBuilder.setContentTitle(title);
        mNotificationBuilder.setContentText(mContext.getResources().getString(R.string.content_text));
        mNotificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mNotificationBuilder.setContentIntent(mPendingIntent);

        notificationManager.notify(position, mNotificationBuilder.build());

        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.stopTaskUpdateUI(position);
                }
            });

        }
    }

}