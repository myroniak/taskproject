package com.dadc.taskmanager.activity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.widgets.TaskPreference;
import com.dadc.taskmanager.widgets.TimePreference;


/**
 * Created by bomko on 11.05.16.
 */

public class SettingActivity extends AppCompatActivity {

    private static final String KEY_COLOR_DEFAULT = "colorDateDefault";
    private static final String KEY_COLOR_START = "colorDateStart";
    private static final String KEY_COLOR_END = "colorDateEnd";
    private static final String KEY_PREF_DEFAULT = "defaultButton";
    private static final String KEY_PREF_TIME = "timeAlarm";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        initToolBar();

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new TaskPreferenceFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, null);
        super.onBackPressed();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class TaskPreferenceFragment extends PreferenceFragment {

        TaskPreference mColorDefault, mColorStart, mColorEnd;
        TimePreference mTimePreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.fragment_preference);

            mColorDefault = (TaskPreference) findPreference(KEY_COLOR_DEFAULT);
            mColorStart = (TaskPreference) findPreference(KEY_COLOR_START);
            mColorEnd = (TaskPreference) findPreference(KEY_COLOR_END);
            mTimePreference = (TimePreference) findPreference(KEY_PREF_TIME);

            Preference mPrefButton = findPreference(KEY_PREF_DEFAULT);

            mPrefButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    mColorDefault.setColorDefault(getResources().getColor(R.color.colorDefaultTask));
                    mColorStart.setColorDefault(getResources().getColor(R.color.colorStartTask));
                    mColorEnd.setColorDefault(getResources().getColor(R.color.colorEndTask));
                    mTimePreference.setDefaultTime(1, 0);

                    return true;
                }
            });
        }
    }
}