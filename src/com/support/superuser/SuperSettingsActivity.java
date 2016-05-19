package com.support.superuser;

import android.app.Activity;
import android.os.Bundle;

import com.example.appolissupport.R;

public class SuperSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_settings);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
