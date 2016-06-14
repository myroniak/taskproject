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

public class SettingActivity extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = new Intent();
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new MyPreferenceFragment()).commit();
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

        setResult(2, intent);

        super.onBackPressed();
    }


    public static class MyPreferenceFragment extends PreferenceFragment {


        AmbilWarnaPreference colorDefault, colorStart, colorEnd;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.fragment_preference);

            colorDefault = (AmbilWarnaPreference) findPreference("colorDateDefault");
            colorStart = (AmbilWarnaPreference) findPreference("colorDateStart");
            colorEnd = (AmbilWarnaPreference) findPreference("colorDateEnd");


            Preference button = findPreference("default");
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    colorDefault.forceSetValue(0x4666ff00);
                    colorStart.forceSetValue(0x46ffcc00);
                    colorEnd.forceSetValue(0x46ff0000);

                    return true;
                }
            });
        }
    }
}