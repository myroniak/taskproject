package com.dadc.taskmanager.broadcastResiever;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.activity.MainActivity;
import com.dadc.taskmanager.adapter.TaskAdapter;
import com.dadc.taskmanager.model.Task;
import com.dadc.taskmanager.util.ControlDataTask;

import java.util.ArrayList;

public class Receiver extends BroadcastReceiver {

    NotificationManager nm;
    TaskAdapter mTaskAdapter;

    @Override
    public void onReceive(final Context ctx, Intent intent) {


        ControlDataTask mControlDataTask = new ControlDataTask(ctx);

        ArrayList<Task> mTaskArrayList = mControlDataTask.loadPreferenceDataTask();
        int mEndTaskColor = mControlDataTask.getDateEndColor();

        int pos = intent.getIntExtra("position", 0);

        Intent mIntent = new Intent(ctx, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 100, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        nm = (NotificationManager) ctx.getSystemService(ctx.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(ctx);

        builder.setAutoCancel(true);
        builder.setContentTitle(intent.getStringExtra("extra"));
        builder.setContentText("Завдання автоматично завершене");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.build();
        }

        mTaskAdapter = new TaskAdapter(mTaskArrayList);
        mTaskAdapter.stopDateTask(pos, mEndTaskColor);
        mTaskAdapter.notifyDataSetChanged();
        mControlDataTask.savePreferenceDataTask(mTaskArrayList); //Save data in SharedPreferences

        Notification myNotification = builder.getNotification();
        nm.notify(pos, myNotification);

    }

}