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
import com.support.objects.CaseType;
import com.support.superadapters.SuperDepartmentAdapter;
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

public class SuperDepartmentActivity extends Activity{

    public static DragSortListView lvDepartments;
    public static ArrayList<CaseType> listDepartments = new ArrayList<>();
    private ArrayList<String> arrayDept = new ArrayList<>();
    private static ArrayList<CaseType> copy;
    private static SuperDepartmentAdapter adapter;
    private SharedPreferenceManager spm;
    private static EditText et;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_department);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        spm = new SharedPreferenceManager(this);
        lvDepartments = (DragSortListView)findViewById(R.id.super_department_list);
        lvDepartments.setDropListener(onDrop);
        lvDepartments.setRemoveListener(onRemove);
        lvDepartments.setDragScrollProfile(ssProfile);
        AsyncCall sa = new AsyncCall(this);
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
                SuperDepartmentAdapter.etDept.clearFocus();
                lvDepartments.setSelection(lvDepartments.getCount() - 1);
                CaseType cs = new CaseType(0, "");
                listDepartments.add(cs);
                adapter.notifyDataSetChanged();
                break;
            case R.id.action_reset:
                AsyncCall sa = new AsyncCall(this);
                sa.execute(String.valueOf(spm.getInt("CompanyID", 0)));
                adapter.notifyDataSetChanged();
                break;
            case android.R.id.home:
                if (listDepartments.size() > 0) {
                    SuperDepartmentAdapter.etDept.clearFocus();
                    Department s = new Department(this, copy, listDepartments);
                    s.execute();
                } else {
                    Toast.makeText(this, "Unable to delete, at least one department is required.", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (listDepartments.size() > 0) {
            SuperDepartmentAdapter.etDept.clearFocus();
            Department s = new Department(this, copy, listDepartments);
            s.execute();
        } else {
            Toast.makeText(this, "Unable to delete, at least one department is required.", Toast.LENGTH_LONG).show();
            finish();
        }
    }


    public class AsyncCall extends AsyncTask<String, Void, String> {

        Context context;
        String response;

        public AsyncCall(Context mContext){
            this.context = mContext;
        }

        @Override
        protected void onPreExecute() {

            listDepartments.clear();
            super.onPreExecute();
            if(!isCancelled()){
                CustomProgressBar.showProgressBar(context, false);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            HttpClient client = new HttpClient();
            try {
                response = client.get(Constants.URL + "/api/Case/GetCaseTypes?CompanyID=" + params[0] + "&IncludeAll=False&UserID=0");
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof SocketException
                        || e instanceof UnknownHostException
                        || e instanceof SocketTimeoutException
                        || e instanceof ConnectTimeoutException
                        || e instanceof ClientProtocolException) {

                    return Constants.ERROR_CONNECTION;
                }  else {
                    return e.getMessage();
                }
            }
            listDepartments = DataParser.getCaseTypes(response);
            copy = new ArrayList<>(listDepartments);

            return "Success";
        }

        @Override
        protected void onPostExecute(String result) {
            if(result == "Success") {
                adapter = new SuperDepartmentAdapter(context, listDepartments);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lvDepartments.setAdapter(adapter);
            } else {

                Utilities.ShowDialog("Error",result, context);
                finish();
            }
            CustomProgressBar.hideProgressBar();
        }
    }



    public class Department extends AsyncTask<String, Void, String> {

        Context context;
        ArrayList<CaseType> OrigList;
        ArrayList<CaseType> UpdList;
        String response;
        StringBuilder sb  = new StringBuilder();

        public Department(Context mContext, ArrayList<CaseType> mOrigList, ArrayList<CaseType> mUpdList ){
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
        //    GetListItems();
        }

        @Override
        protected String doInBackground(String... params) {

            boolean isDeleted;
            for(CaseType orig : OrigList){
                isDeleted = true;
                for(CaseType upd : UpdList){
                    if(upd.getCommentTypeID()==orig.getCommentTypeID()){
                        isDeleted = false;
                        break;
                    }
                }
                if(isDeleted)
                    try {
                        HttpClient client = new HttpClient();
                        if (!isCancelled()) {
                            response = client.delete(Constants.URL + "/api/Case/DeleteCaseType?id=" + orig.getCommentTypeID());
                            if(response.contains("REFERENCE constraint")){
                                Log.d("Error", orig.getCommentTypeDesc());
                                response = "Error deleting Severity: "+ orig.getCommentTypeDesc()+"\nCannot delete because it has support tickets referencing it.";
                                sb.append(response+"\n\n");

                            } else if(!response.contains("DeleteSuccess")){
                                Log.d("Error", orig.getCommentTypeDesc());
                                sb.append(response+"\n\n");
                            }
                        }
                    } catch (Exception e) {
                        response = e.getMessage();
                        break;
                    }
            }
            int i = 0;
            for(CaseType upd : UpdList){
                JSONObject json = new JSONObject();
                try {
                    json.put("CommentTypeID", String.valueOf(upd.getCommentTypeID()));
                    json.put("CommentTypeDesc", upd.getCommentTypeDesc());
                    json.put("CompanyID", String.valueOf(spm.getInt("CompanyID", 0)));
                    json.put("ActiveInd", "True");
                    json.put("DefaultInd", i);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                HttpClient client = new HttpClient();
                try {
                    response = client.post(Constants.URL + "/api/Case/PostCaseType", json.toString());
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
                    AsyncCall sa = new AsyncCall(context);
                    sa.execute(String.valueOf(spm.getInt("CompanyID", 0)));
                    adapter.notifyDataSetChanged();
                    Utilities.ShowDialog("Item Delete", sb.toString(), context);
                } else {
                    finish();
                }
            } else {
                Utilities.ShowDialog("Error", result, context);
                AsyncCall sa = new AsyncCall(context);
                sa.execute(String.valueOf(spm.getInt("CompanyID", 0)));
                adapter.notifyDataSetChanged();
            }

        }
    }

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    CaseType item=adapter.getItem(from);
                    adapter.notifyDataSetChanged();
                    adapter.remove(item);
                    int a = listDepartments.size();
                    adapter.insert(item, to);

                }
            };

    private DragSortListView.RemoveListener onRemove =
            new DragSortListView.RemoveListener() {
                @Override
                public void remove(int which) {
                    adapter.remove(adapter.getItem(which));
                }
            };

    private DragSortListView.DragScrollProfile ssProfile =
            new DragSortListView.DragScrollProfile() {
                @Override
                public float getSpeed(float w, long t) {
                    if (w > 0.8f) {
                        // Traverse all views in a millisecond
                        return ((float) adapter.getCount()) / 0.001f;
                    } else {
                        return 10.0f * w;
                    }
                }
            };
}
