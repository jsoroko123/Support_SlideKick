package com.support.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.appolissupport.R;
import com.support.superuser.SuperAreaActivity;
import com.support.superuser.SuperClientUserActivity;
import com.support.superuser.SuperDepartmentActivity;
import com.support.superuser.SuperSettingsActivity;
import com.support.superuser.SuperSeverityActivity;
import com.support.superuser.SuperStatusActivity;
import com.support.superuser.SuperSupportUserActivity;
import com.support.utilities.SharedPreferenceManager;

public class SuperUserActivity extends Activity implements OnClickListener {

    Button btnSeverity, btnSupportUsers, btnDepartments, btnArea, btnStatus, btnclientUsers, btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_user);

        btnSeverity = (Button)findViewById(R.id.btn_edit_severity);
        btnSeverity.setOnClickListener(this);
        btnSupportUsers = (Button)findViewById(R.id.btn_add_support);
        btnSupportUsers.setOnClickListener(this);
        btnDepartments = (Button)findViewById(R.id.btn_edit_case_types);
        btnDepartments.setOnClickListener(this);
        btnArea = (Button)findViewById(R.id.btn_edit_case_area);
        btnArea.setOnClickListener(this);
        btnStatus = (Button)findViewById(R.id.btn_edit_statuses);
        btnStatus.setOnClickListener(this);
        btnclientUsers = (Button)findViewById(R.id.btn_add_client);
        btnclientUsers.setOnClickListener(this);
        btnSettings = (Button)findViewById(R.id.btn_additonal);
        btnSettings.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
       if (id == R.id.action_logout) {
            SharedPreferenceManager spm = new SharedPreferenceManager(this);
            spm.clearAll();
            Intent mainScreenIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(mainScreenIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent mainScreenIntent;
        switch (v.getId()) {
            case R.id.btn_edit_severity:
                mainScreenIntent = new Intent(getApplicationContext(), SuperSeverityActivity.class);
                startActivity(mainScreenIntent);
                break;
            case R.id.btn_add_support:
                mainScreenIntent = new Intent(getApplicationContext(), SuperSupportUserActivity.class);
                startActivity(mainScreenIntent);
                break;
            case R.id.btn_edit_case_types:
                mainScreenIntent = new Intent(getApplicationContext(), SuperDepartmentActivity.class);
                startActivity(mainScreenIntent);
                break;
            case R.id.btn_edit_case_area:
                mainScreenIntent = new Intent(getApplicationContext(), SuperAreaActivity.class);
                startActivity(mainScreenIntent);
                break;
            case R.id.btn_edit_statuses:
                mainScreenIntent = new Intent(getApplicationContext(), SuperStatusActivity.class);
                startActivity(mainScreenIntent);
                break;
            case R.id.btn_add_client:
                mainScreenIntent = new Intent(getApplicationContext(), SuperClientUserActivity.class);
                startActivity(mainScreenIntent);
                break;
            case R.id.btn_additonal:
                mainScreenIntent = new Intent(getApplicationContext(), SuperSettingsActivity.class);
                startActivity(mainScreenIntent);
                break;


            default:
                break;
        }

    }
}
