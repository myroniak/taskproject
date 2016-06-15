package com.dadc.taskmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dadc.taskmanager.R;

import yuku.ambilwarna.widget.AmbilWarnaPreference;

/**
 * Created by bomko on 11.05.16.
 */

public class SettingActivity extends AppCompatActivity {

    private static final String KEY_COLOR_DEFAULT = "colorDateDefault";
    private static final String KEY_COLOR_START = "colorDateStart";
    private static final String KEY_COLOR_END = "colorDateEnd";
    private static final String KEY_PREF_BUTTON = "defaultButton";
    private static final int SETTING_RESULT_CODE = 2;

    Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mIntent = new Intent();

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

        setResult(SETTING_RESULT_CODE, mIntent);

        super.onBackPressed();
    }


    public static class TaskPreferenceFragment extends PreferenceFragment {

        AmbilWarnaPreference mColorDefault, mColorStart, mColorEnd;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.fragment_preference);

            mColorDefault = (AmbilWarnaPreference) findPreference(KEY_COLOR_DEFAULT);
            mColorStart = (AmbilWarnaPreference) findPreference(KEY_COLOR_START);
            mColorEnd = (AmbilWarnaPreference) findPreference(KEY_COLOR_END);

            Preference mPrefButton = findPreference(KEY_PREF_BUTTON);

            mPrefButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    mColorDefault.forceSetValue(getResources().getColor(R.color.colorDefaultTask));
                    mColorStart.forceSetValue(getResources().getColor(R.color.colorStartTask));
                    mColorEnd.forceSetValue(getResources().getColor(R.color.colorEndTask));

                    return true;
                }
            });
        }
    }
}