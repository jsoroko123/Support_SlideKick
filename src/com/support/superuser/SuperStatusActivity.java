package com.support.superuser;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appolissupport.R;
import com.support.custom.CustomProgressBar;
import com.support.dragdrop.DragSortListView;
import com.support.network.HttpClient;
import com.support.objects.CaseStatus;
import com.support.superadapters.SuperMapStatusAdapter;
import com.support.superadapters.SuperStatusAdapter;
import com.support.utilities.Constants;
import com.support.utilities.DataParser;
import com.support.utilities.SharedPreferenceManager;
import com.support.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SuperStatusActivity extends Activity implements View.OnClickListener {

    public static DragSortListView lvStatuses, lvStatuses2;
    public static ArrayList<CaseStatus> listStatuses = new ArrayList<>();
    public static ArrayList<CaseStatus> listSupportStatuses = new ArrayList<>();
    private ArrayList<String> arrayStatus = new ArrayList<>();
    private static ArrayList<CaseStatus> copy,copy2;
    private static SuperStatusAdapter adapter;
    private static SuperMapStatusAdapter adapterSupport;
    private SharedPreferenceManager spm;
    private static EditText et;
    private LinearLayout llMap1, llMap2;
    private TextView tvButton, tvButton2;
    private static boolean FirstLoad;
    private static boolean ClientMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_status);
        FirstLoad = false;
        ClientMap = true;
        getActionBar().setDisplayHomeAsUpEnabled(true);
        spm = new SharedPreferenceManager(this);
        lvStatuses = (DragSortListView) findViewById(R.id.super_status_list);
        lvStatuses2 = (DragSortListView) findViewById(R.id.super_status_list2);
        llMap1 = (LinearLayout) findViewById(R.id.ll_status_type1);
        llMap2 = (LinearLayout) findViewById(R.id.ll_status_type2);
        tvButton = (TextView) findViewById(R.id.tb_map);
        tvButton.setOnClickListener(this);
        tvButton2 = (TextView) findViewById(R.id.tb_map2);
        tvButton2.setOnClickListener(this);
        //Load Statuses
        final StatusAsyncCall ct = new StatusAsyncCall(this);
        ct.execute(String.valueOf(spm.getInt("CompanyID", 0)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_status:
                if(ClientMap) {
                    if(SuperStatusAdapter.etDept != null) {
                        SuperStatusAdapter.etDept.clearFocus();
                    }
                    lvStatuses.setSelection(lvStatuses.getCount() - 1);
                    adapter.notifyDataSetChanged();
                    CaseStatus cs = new CaseStatus(0, "");
                    listStatuses.add(cs);
                    adapter.notifyDataSetChanged();
                } else {
                    if(SuperMapStatusAdapter.etDept != null) {
                        SuperMapStatusAdapter.etDept.clearFocus();
                    }
                    lvStatuses2.setSelection(lvStatuses2.getCount() - 1);
                    adapterSupport.notifyDataSetChanged();
                    CaseStatus cs = new CaseStatus(0, "", listStatuses.get(0).getCaseStatusID(), listStatuses.get(0).getCaseStatusDesc());
                    listSupportStatuses.add(cs);
                    adapterSupport.notifyDataSetChanged();
                }
                break;
            case R.id.action_reset:
                StatusAsyncCall sa = new StatusAsyncCall(this);
                sa.execute(String.valueOf(spm.getInt("CompanyID", 0)));
                adapter.notifyDataSetChanged();
                break;
            case android.R.id.home:
                if(ClientMap) {
                    if(listStatuses.size() > 0) {
                        SuperStatusAdapter.etDept.clearFocus();
                        ClientStatus s = new ClientStatus(this, copy, listStatuses, true);
                        s.execute();
                    } else {
                        finish();
                    }
                } else {
                    if(listSupportStatuses.size() > 0) {
                        SuperMapStatusAdapter.etDept.clearFocus();
                        Status s = new Status(this, copy2, listSupportStatuses, true);
                        s.execute();
                    } else {
                        finish();
                    }
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())  {
            case R.id.tb_map:
                SuperStatusAdapter.etDept.clearFocus();
                ClientStatus s = new ClientStatus(this, copy, listStatuses, false);
                s.execute();
                ClientMap = false;
                break;
            case R.id.tb_map2:
                SuperMapStatusAdapter.etDept.clearFocus();
                Status s2 = new Status(this, copy2, listSupportStatuses, false);
                s2.execute();
                ClientMap = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(ClientMap) {
            if(listStatuses.size() > 0) {
                SuperStatusAdapter.etDept.clearFocus();
                ClientStatus s = new ClientStatus(this, copy, listStatuses, true);
                s.execute();
            } else {
                Toast.makeText(this, "Unable to delete, at least one case status is required.", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            if(listSupportStatuses.size() > 0) {
                SuperMapStatusAdapter.etDept.clearFocus();
                Status s = new Status(this, copy2, listSupportStatuses, true);
                s.execute();
            } else {
                finish();
            }
        }
    }

    private class StatusAsyncCall extends AsyncTask<String, Void, Void> {

        Context context;
        String response;

        public StatusAsyncCall(Context mContext){
            this.context = mContext;
        }

        @Override
        protected void onPreExecute() {
            listStatuses.clear();

            super.onPreExecute();
            if(!isCancelled()){
                CustomProgressBar.showProgressBar(context, false);
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpClient client = new HttpClient();
            try {
                response = client.get(Constants.URL + "/api/Status/GetStatus?CompanyID=" + params[0] + "&IsSupport=False");
            } catch (Exception e) {
                e.printStackTrace();
            }
            listStatuses = DataParser.getCaseStatuses(response);

            copy = new ArrayList<>(listStatuses);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            CustomProgressBar.hideProgressBar();
            adapter = new SuperStatusAdapter(context,  listStatuses);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lvStatuses.setAdapter(adapter);
            if(FirstLoad) {
                Animation out = AnimationUtils.loadAnimation(context, R.anim.grow_from_bottomleft_to_topright);
                llMap2.setVisibility(View.GONE);
                llMap1.startAnimation(out);
                llMap1.setVisibility(View.VISIBLE);
                tvButton2.setVisibility(View.GONE);
                tvButton.setVisibility(View.VISIBLE);
            }

            FirstLoad = true;
        }
    }



    private class SupportStatusAsyncCall extends AsyncTask<String, Void, Void> {

        Context context;
        String response;

        public SupportStatusAsyncCall(Context mContext){
            this.context = mContext;
        }

        @Override
        protected void onPreExecute() {
            listSupportStatuses.clear();

            super.onPreExecute();
            if(!isCancelled()){
                CustomProgressBar.showProgressBar(context, false);
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpClient client = new HttpClient();
            try {
                response = client.get(Constants.URL + "/api/Status/GetStatus?CompanyID=" + params[0] + "&IsSupport=True");
            } catch (Exception e) {
                e.printStackTrace();
            }
            listSupportStatuses = DataParser.getCaseStatuses(response);

            copy2 = new ArrayList<>(listSupportStatuses);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            CustomProgressBar.hideProgressBar();
            adapterSupport = new SuperMapStatusAdapter(context,  listSupportStatuses);
            adapterSupport.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lvStatuses2.setAdapter(adapterSupport);
            Animation in = AnimationUtils.loadAnimation(context, R.anim.grow_from_bottomright_to_topleft);
            llMap1.setVisibility(View.GONE);
            llMap2.startAnimation(in);
            llMap2.setVisibility(View.VISIBLE);
            tvButton.setVisibility(View.GONE);
            tvButton2.setVisibility(View.VISIBLE);
        }
    }



    public class Status extends AsyncTask<String, Void, String> {

        Context context;
        ArrayList<CaseStatus> OrigList;
        ArrayList<CaseStatus> UpdList;
        String response;
        StringBuilder sb = new StringBuilder();
        boolean saveAndClose;

        public Status(Context mContext, ArrayList<CaseStatus> mOrigList, ArrayList<CaseStatus> mUpdList, boolean mSaveAndClose) {
            this.context = mContext;
            this.OrigList = mOrigList;
            this.UpdList = mUpdList;
            this.saveAndClose = mSaveAndClose;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!isCancelled()) {
                CustomProgressBar.showProgressBar(context, false);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            boolean isDeleted;
            for (CaseStatus orig : OrigList) {
                isDeleted = true;
                for (CaseStatus upd : UpdList) {
                    if (upd.getCaseStatusID() == orig.getCaseStatusID()) {
                        isDeleted = false;
                        break;
                    }
                }
                if (isDeleted)
                    try {
                        HttpClient client = new HttpClient();
                        if (!isCancelled()) {
                            response = client.delete(Constants.URL + "/api/Status/DeleteStatus?id=" + orig.getCaseStatusID());
                            if (response.contains("REFERENCE constraint")) {
                                Log.d("Error", orig.getCaseStatusDesc());
                                response = "Error deleting Area: " + orig.getCaseStatusDesc() + "\nCannot delete because it has support items referencing it.";
                                sb.append(response + "\n\n");

                            } else if (!response.contains("DeleteSuccess")) {
                                Log.d("Error", orig.getCaseStatusDesc());
                                sb.append(response + "\n\n");
                            }
                        }
                    } catch (Exception e) {
                        response = e.getMessage();
                        break;
                    }
            }
            int i = 0;
            for (CaseStatus upd : UpdList) {
                JSONObject json = new JSONObject();
                try {
                    json.put("CaseStatusID", String.valueOf(upd.getCaseStatusID()));
                    json.put("CompanyID", String.valueOf(spm.getInt("CompanyID", 0)));
                    json.put("CaseStatusDesc", upd.getCaseStatusDesc());
                    json.put("ClientCaseStatusMapID", upd.getClientCaseStatusID());
                    json.put("Active", "True");
                    json.put("DefaultInd", i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpClient client = new HttpClient();
                try {
                    response = client.post(Constants.URL + "/api/Status/PostStatus", json.toString());
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
                if (sb.length() > 0) {
                    StatusAsyncCall sa = new StatusAsyncCall(context);
                    sa.execute(String.valueOf(spm.getInt("CompanyID", 0)));
                    adapter.notifyDataSetChanged();
                    Utilities.ShowDialog("Item Delete", sb.toString(), context);
                } else {
                    if(saveAndClose){
                        finish();
                    } else {
                        final StatusAsyncCall ct = new StatusAsyncCall(context);
                        ct.execute(String.valueOf(spm.getInt("CompanyID", 0)));
                    }

                }
            } else {
                Utilities.ShowDialog("Error", result, context);
                StatusAsyncCall sa = new StatusAsyncCall(context);
                sa.execute(String.valueOf(spm.getInt("CompanyID", 0)));
                adapter.notifyDataSetChanged();
            }
        }
    }



    public class ClientStatus extends AsyncTask<String, Void, String> {

        Context context;
        ArrayList<CaseStatus> OrigList;
        ArrayList<CaseStatus> UpdList;
        boolean saveAndClose;
        String response;
        StringBuilder sb = new StringBuilder();

        public ClientStatus(Context mContext, ArrayList<CaseStatus> mOrigList, ArrayList<CaseStatus> mUpdList, boolean mSaveAndClose) {
            this.context = mContext;
            this.OrigList = mOrigList;
            this.UpdList = mUpdList;
            this.saveAndClose = mSaveAndClose;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!isCancelled()) {
                CustomProgressBar.showProgressBar(context, false);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            boolean isDeleted;
            for (CaseStatus orig : OrigList) {
                isDeleted = true;
                for (CaseStatus upd : UpdList) {
                    if (upd.getCaseStatusID() == orig.getCaseStatusID()) {
                        isDeleted = false;
                        break;
                    }
                }
                if (isDeleted)
                    try {
                        HttpClient client = new HttpClient();
                        if (!isCancelled()) {
                            response = client.delete(Constants.URL + "/api/Status/DeleteClientStatus?id=" + orig.getCaseStatusID());
                            if (response.contains("REFERENCE constraint")) {
                                Log.d("Error", orig.getCaseStatusDesc());
                                response = "Error deleting Area: " + orig.getCaseStatusDesc() + "\nCannot delete because it has support items referencing it.";
                                sb.append(response + "\n\n");

                            } else if (!response.contains("DeleteSuccess")) {
                                Log.d("Error", orig.getCaseStatusDesc());
                                sb.append(response + "\n\n");
                            }
                        }
                    } catch (Exception e) {
                        response = e.getMessage();
                        break;
                    }
            }
            int i = 0;
            for (CaseStatus upd : UpdList) {
                JSONObject json = new JSONObject();
                try {
                    json.put("ClientCaseStatusID", String.valueOf(upd.getCaseStatusID()));
                    json.put("CompanyID", String.valueOf(spm.getInt("CompanyID", 0)));
                    json.put("ClientCaseStatusDesc", upd.getCaseStatusDesc());
                    json.put("Active", "True");
                    json.put("DefaultInd", i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpClient client = new HttpClient();
                try {
                    response = client.post(Constants.URL + "/api/Status/PostClientMap", json.toString());
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
                if (sb.length() > 0) {
                    StatusAsyncCall sa = new StatusAsyncCall(context);
                    sa.execute(String.valueOf(spm.getInt("CompanyID", 0)));
                    adapter.notifyDataSetChanged();
                    Utilities.ShowDialog("Item Delete", sb.toString(), context);
                } else {
                    if(saveAndClose){
                        finish();
                    } else {
                        final SupportStatusAsyncCall s = new SupportStatusAsyncCall(context);
                        s.execute(String.valueOf(spm.getInt("CompanyID", 0)));
                    }
                }
            } else {
                Utilities.ShowDialog("Error", result, context);
                StatusAsyncCall sa = new StatusAsyncCall(context);
                sa.execute(String.valueOf(spm.getInt("CompanyID", 0)));
                adapter.notifyDataSetChanged();
            }
        }
    }
}
