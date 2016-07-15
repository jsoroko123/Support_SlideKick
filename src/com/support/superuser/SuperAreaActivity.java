package com.support.superuser;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.Spinner;
import android.widget.Toast;

import com.example.appolissupport.R;
import com.support.adapters.CaseTypeAdapter;
import com.support.custom.CustomProgressBar;
import com.support.dragdrop.DragSortListView;
import com.support.network.HttpClient;
import com.support.objects.CaseArea;
import com.support.objects.CaseType;
import com.support.superadapters.SuperAreaAdapter;
import com.support.utilities.Constants;
import com.support.utilities.DataParser;
import com.support.utilities.SharedPreferenceManager;
import com.support.utilities.Utilities;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class SuperAreaActivity extends Activity {

    public static DragSortListView lvAreas;
    public static ArrayList<CaseArea> listAreas= new ArrayList<>();
    private static ArrayList<CaseArea> copy;
    private static ArrayList<CaseType> listCaseType = new ArrayList<>();
    private static SuperAreaAdapter adapter;
    private Spinner spinner;
    private SharedPreferenceManager spm;
    private static int CommentTypeID = 0;
    private static int LastCommentTypeID = 0;

    private static boolean FirstLoad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_area);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        spm = new SharedPreferenceManager(this);
        FirstLoad = true;
        lvAreas = (DragSortListView)findViewById(R.id.super_area_list);
        spinner = (Spinner)findViewById(R.id.spinDepartments);
        CaseTypeAsyncCall ct = new CaseTypeAsyncCall(this);
        ct.execute(String.valueOf(spm.getInt("CompanyID", 0)));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(CommentTypeID != 0){
                    LastCommentTypeID = CommentTypeID;
                }


                if (listCaseType.get(position).getCommentTypeID() != 0) {
                    CommentTypeID = listCaseType.get(position).getCommentTypeID();
                    if (SuperAreaAdapter.etDArea != null) {
                        SuperAreaAdapter.etDArea.clearFocus();
                    }
                    if (!FirstLoad) {
                        Area s = new Area(SuperAreaActivity.this, copy, listAreas, false, LastCommentTypeID);
                        s.execute();
                    } else {
                        AsyncCall sa = new AsyncCall(SuperAreaActivity.this);
                        sa.execute(String.valueOf(spm.getInt("CompanyID", 0)), String.valueOf(CommentTypeID));
                    }
                } else {
                    AsyncCall sa = new AsyncCall(SuperAreaActivity.this);
                    sa.execute(String.valueOf(spm.getInt("CompanyID", 0)), "0");
                }

                FirstLoad = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_severity, menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        if(listAreas.size()>0) {
            SuperAreaAdapter.etDArea.clearFocus();
            SuperAreaAdapter.etDArea.requestFocus();
            Area s = new Area(this, copy, listAreas, true, listCaseType.get(spinner.getSelectedItemPosition()).getCommentTypeID());
            s.execute();
        } else {
            Toast.makeText(this, "Unable to delete, at least one case area is required.", Toast.LENGTH_LONG).show();
            finish();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case  R.id.action_add_severity:
                SuperAreaAdapter.etDArea.clearFocus();
                lvAreas.setSelection(lvAreas.getCount() - 1);
                adapter.notifyDataSetChanged();
                CaseArea cs = new CaseArea(0, "");
                listAreas.add(cs);
                adapter.notifyDataSetChanged();
                break;
            case R.id.action_reset:
                AsyncCall sa = new AsyncCall(this);
                sa.execute(String.valueOf(spm.getInt("CompanyID", 0)), String.valueOf(CommentTypeID));
                adapter.notifyDataSetChanged();
                break;
            case android.R.id.home:
                if(listAreas.size()>0) {
                    SuperAreaAdapter.etDArea.clearFocus();
                    SuperAreaAdapter.etDArea.requestFocus();
                    Area s = new Area(this, copy, listAreas, true, listCaseType.get(spinner.getSelectedItemPosition()).getCommentTypeID());
                    s.execute();
                } else {
                    Toast.makeText(this, "Unable to delete, at least one case area is required.", Toast.LENGTH_LONG).show();
                   finish();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public class AsyncCall extends AsyncTask<String, Void, Integer> {

        Context context;
        String response;

        public AsyncCall(Context mContext){
            this.context = mContext;
        }

        @Override
        protected void onPreExecute() {

            listAreas.clear();
            super.onPreExecute();
            if(!isCancelled()){
                CustomProgressBar.showProgressBar(context, false);
            }
        }

        @Override
        protected Integer doInBackground(String... params) {
            HttpClient client = new HttpClient();
            try {
                response = client.get(Constants.URL + "/api/Area/GetArea?CompanyID=" + params[0] + "&IncludeSelect=False&CommentTypeID=" + params[1]);
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof SocketException
                        || e instanceof UnknownHostException
                        || e instanceof SocketTimeoutException
                        || e instanceof ConnectTimeoutException
                        || e instanceof ClientProtocolException) {
                    return 1;

                } else {
                    return 2;
                }
            }
            listAreas = DataParser.getCaseArea(response);
            copy = new ArrayList<>(listAreas);
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            CustomProgressBar.hideProgressBar();
            if(result == 1){
                Utilities.ShowDialog("Network Error", Constants.ERROR_CONNECTION, context);
            } else if (result == 2){
                Utilities.ShowDialog("Error", Constants.DEFAULT_ERROR_MSG, context);
            } else {
                adapter = new SuperAreaAdapter(context, listAreas);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lvAreas.setAdapter(adapter);
            }
        }
    }

    private class CaseTypeAsyncCall extends AsyncTask<String, Void, Void> {

        Context context;
        String response;

        public CaseTypeAsyncCall(Context mContext){
            this.context = mContext;
        }

        @Override
        protected void onPreExecute() {
            listCaseType.clear();

            super.onPreExecute();
            if(!isCancelled()){
                CustomProgressBar.showProgressBar(context, false);
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpClient client = new HttpClient();
            try {
                response = client.get(Constants.URL + "/api/Case/GetCaseTypes?CompanyID=" + params[0] + "&IncludeAll=True&UserID=0");
            } catch (Exception e) {
                e.printStackTrace();
            }
            listCaseType = DataParser.getCaseTypes(response);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            CustomProgressBar.hideProgressBar();

            CaseTypeAdapter spinnerArrayAdapter56 = new CaseTypeAdapter(context,  listCaseType);
            spinnerArrayAdapter56.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerArrayAdapter56);
        }
    }

    public class Area extends AsyncTask<String, Void, String> {

        Context context;
        ArrayList<CaseArea> OrigList;
        ArrayList<CaseArea> UpdList;
        String response;
        StringBuilder sb  = new StringBuilder();
        boolean saveAndClose;
        int commentTypeID;

        public Area(Context mContext, ArrayList<CaseArea> mOrigList, ArrayList<CaseArea> mUpdList, boolean mSaveAndClose, int mCommentTypeID){
            this.context = mContext;
            this.OrigList = mOrigList;
            this.UpdList = mUpdList;
            this.saveAndClose = mSaveAndClose;
            this.commentTypeID = mCommentTypeID;
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
            for(CaseArea orig : OrigList){
                isDeleted = true;
                for(CaseArea upd : UpdList){
                    if(upd.getCaseReasonID()==orig.getCaseReasonID()){
                        isDeleted = false;
                        break;
                    }
                }
                if(isDeleted)
                    try {
                        HttpClient client = new HttpClient();
                        if (!isCancelled()) {
                            response = client.delete(Constants.URL + "/api/Area/DeleteArea?id=" + orig.getCaseReasonID());

                            if(response.contains("REFERENCE constraint")){
                                Log.d("Error", orig.getReasonDesc());
                                response = "Error deleting Area: "+ orig.getReasonDesc()+"\nCannot delete because it has support tickets referencing it.";
                                sb.append(response+"\n\n");

                            } else if(!response.contains("DeleteSuccess")){
                                Log.d("Error", orig.getReasonDesc());
                                sb.append(response+"\n\n");
                            }
                        }
                    } catch (Exception e) {
                        response = e.getMessage();
                        break;
                    }
            }
            int i = 0;
            for(CaseArea upd : UpdList){
                JSONObject json = new JSONObject();
                try {
                    json.put("CaseReasonID", String.valueOf(upd.getCaseReasonID()));
                    json.put("CompanyID", String.valueOf(spm.getInt("CompanyID", 0)));
                    json.put("ReasonDesc", upd.getReasonDesc());
                    json.put("Priority", i);
                    json.put("CommentTypeID", String.valueOf(commentTypeID));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpClient client = new HttpClient();
                try {
                    response = client.post(Constants.URL + "/api/Area/PostArea", json.toString());
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
                    Utilities.ShowDialog("Item Delete", sb.toString(), context);
                    AsyncCall sa = new AsyncCall(context);
                    sa.execute(String.valueOf(spm.getInt("CompanyID", 0)), String.valueOf(CommentTypeID));
                    adapter.notifyDataSetChanged();
                } else {
                    if(saveAndClose) {
                        finish();
                    } else {
                        AsyncCall sa = new AsyncCall(SuperAreaActivity.this);
                        sa.execute(String.valueOf(spm.getInt("CompanyID", 0)), String.valueOf(CommentTypeID));
                    }
                }
            } else {
                Utilities.ShowDialog("Error", result, context);
                AsyncCall sa = new AsyncCall(context);
                sa.execute(String.valueOf(spm.getInt("CompanyID", 0)), String.valueOf(CommentTypeID));
                adapter.notifyDataSetChanged();
            }
        }
    }
}
