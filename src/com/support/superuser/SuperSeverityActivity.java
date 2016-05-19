package com.support.superuser;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appolissupport.R;
import com.support.custom.CustomProgressBar;
import com.support.dragdrop.DragSortListView;
import com.support.network.HttpClient;
import com.support.objects.CaseSeverity;
import com.support.superadapters.SuperSeverityAdapter;
import com.support.utilities.Constants;
import com.support.utilities.DataParser;
import com.support.utilities.SharedPreferenceManager;
import com.support.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SuperSeverityActivity extends Activity{

    public static DragSortListView lvSeverity;
    public static ArrayList<CaseSeverity> listSeverity = new ArrayList<>();
    private static SuperSeverityAdapter spinnerArrayAdapter4;
    private ArrayList<String> arraySeverity = new ArrayList<>();
    private static ArrayList<CaseSeverity> copy;
    private SharedPreferenceManager spm;
    private static EditText et;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_severity);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        spm = new SharedPreferenceManager(this);
        lvSeverity = (DragSortListView)findViewById(R.id.super_severity_list);
        SeverityAsyncCall sa = new SeverityAsyncCall(this);
        sa.execute(String.valueOf(spm.getInt("CompanyID", 0)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_severity, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case  R.id.action_add_severity:
                SuperSeverityAdapter.etSeverity.clearFocus();
                lvSeverity.setSelection(lvSeverity.getCount() - 1);
                CaseSeverity cs = new CaseSeverity(0, "", "-1");
                listSeverity.add(cs);
                spinnerArrayAdapter4.notifyDataSetChanged();
                break;
            case R.id.action_reset:
                SeverityAsyncCall sa = new SeverityAsyncCall(this);
                sa.execute(String.valueOf(spm.getInt("CompanyID", 0)));
                spinnerArrayAdapter4.notifyDataSetChanged();
                break;
            case android.R.id.home:
                if(listSeverity.size() > 0) {
                    SuperSeverityAdapter.etSeverity.requestFocus();
                    SuperSeverityAdapter.etSeverity.clearFocus();
                    Severity s = new Severity(this, copy, listSeverity);
                    s.execute();
                } else {
                    Toast.makeText(this, "Unable to delete, at least one severity is required.", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public class SeverityAsyncCall extends AsyncTask<String, Void, Void> {

        Context context;
        String response;

        public SeverityAsyncCall(Context mContext){
            this.context = mContext;
        }

        @Override
        protected void onPreExecute() {

            listSeverity.clear();
            super.onPreExecute();
            if(!isCancelled()){
                CustomProgressBar.showProgressBar(context, false);
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpClient client = new HttpClient();
            try {
                response = client.get(Constants.URL + "/api/Severity/GetSeverity?CompanyID=" + params[0] + "&IncludeSelect=False");
            } catch (Exception e) {
                e.printStackTrace();
            }
            listSeverity = DataParser.getCaseSeverity(response);
            copy = new ArrayList<>(listSeverity);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            spinnerArrayAdapter4 = new SuperSeverityAdapter (context, listSeverity);
            spinnerArrayAdapter4.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
            lvSeverity.setAdapter(spinnerArrayAdapter4);
            CustomProgressBar.hideProgressBar();
        }
    }

    @Override
    public void onBackPressed() {
        if(listSeverity.size() > 0) {
            SuperSeverityAdapter.etSeverity.requestFocus();
            SuperSeverityAdapter.etSeverity.clearFocus();
            Severity s = new Severity(this, copy, listSeverity);
            s.execute();
        } else {
            finish();
        }
    }


    public class Severity extends AsyncTask<String, Void, String> {

        Context context;
        ArrayList<CaseSeverity> OrigList;
        ArrayList<CaseSeverity> UpdList;
        String response;
        StringBuilder sb  = new StringBuilder();

        public Severity(Context mContext, ArrayList<CaseSeverity> mOrigList, ArrayList<CaseSeverity> mUpdList ){
            this.context = mContext;
            this.OrigList = mOrigList;
            this.UpdList = mUpdList;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!isCancelled()){
                CustomProgressBar.showProgressBar(context, false);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            boolean isDeleted;
            for(CaseSeverity orig : OrigList){
                isDeleted = true;
                for(CaseSeverity upd : UpdList){
                    if(upd.getCaseSeverityID()==orig.getCaseSeverityID()){
                        isDeleted = false;
                        break;
                    }
                }
                if(isDeleted)
                    try {
                        HttpClient client = new HttpClient();
                    if (!isCancelled()) {
                        response = client.delete(Constants.URL + "/api/Severity/DeleteSeverity?id=" + orig.getCaseSeverityID());
                        if(response.contains("REFERENCE constraint")){
                            Log.d("Error", orig.getCaseSeverityDesc());
                            response = "Error deleting Severity: "+ orig.getCaseSeverityDesc()+"\nCannot delete because it has support tickets referencing it.";
                            sb.append(response+"\n\n");

                        } else if(!response.contains("DeleteSuccess")){
                            Log.d("Error", orig.getCaseSeverityDesc());
                            sb.append(response+"\n\n");
                        }
                    }
                } catch (Exception e) {
                    response = e.getMessage();
                    break;
                }
            }
            int i = 0;
            for(CaseSeverity upd : UpdList){
                JSONObject json = new JSONObject();
                try {
                    json.put("CaseSeverityID", String.valueOf(upd.getCaseSeverityID()));
                    json.put("CompanyID", String.valueOf(spm.getInt("CompanyID",0)));
                    json.put("CaseSeverityDesc", upd.getCaseSeverityDesc());
                    json.put("Color", upd.getColor());
                    json.put("OrderBy", String.valueOf(i+1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                HttpClient client = new HttpClient();
                try {
                    response = client.post(Constants.URL + "/api/Severity/PostSeverity", json.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    return e.getMessage();
                }

                i++;
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            CustomProgressBar.hideProgressBar();
            if (result.equals("1")) {
                if(sb.length() > 0) {
                    SeverityAsyncCall sa = new SeverityAsyncCall(context);
                    sa.execute(String.valueOf(spm.getInt("CompanyID", 0)));
                    spinnerArrayAdapter4.notifyDataSetChanged();
                    Utilities.ShowDialog("Item Delete", sb.toString(), context);
                } else {
                    finish();
                }
            } else {
                Utilities.ShowDialog("Error", result, context);
                SeverityAsyncCall sa = new SeverityAsyncCall(context);
                sa.execute(String.valueOf(spm.getInt("CompanyID", 0)));
                spinnerArrayAdapter4.notifyDataSetChanged();
            }

        }
    }







}
