package com.support.main;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.appolissupport.R;
import com.support.custom.CustomProgressBar;
import com.support.network.HttpClient;
import com.support.utilities.Constants;
import com.support.utilities.Utilities;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class RegisterSuperUserActivity extends Activity implements  OnClickListener {

    private Button btnNext;
    private Button btnCancel;
    private EditText etUsername, etPassword, etConfirmPassword, etFirstName, etLastName, etPhone, etEmail;
    private int clientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
        setContentView(R.layout.activity_register_super_user);
        Bundle bundle = getIntent().getExtras();
        clientID = Integer.valueOf(bundle.getString("CompanyID", "")); //CompanyID = clientID
        btnNext = (Button) findViewById(R.id.bt_next_register);
        btnNext.setOnClickListener(this);
        btnCancel = (Button) findViewById(R.id.bt_cancel);
        btnCancel.setOnClickListener(this);
        etUsername = (EditText) findViewById(R.id.et_superuser);
        etUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(etUsername.isEnabled()) {
                    if (!hasFocus) {
                        DoesUserExist does = new DoesUserExist(RegisterSuperUserActivity.this);
                        does.execute(etUsername.getText().toString());
                    }
                }
            }
        });
        etPassword = (EditText) findViewById(R.id.et_superpassword);
        etConfirmPassword = (EditText) findViewById(R.id.et_super_confirm_password);
        etFirstName = (EditText) findViewById(R.id.et_super_firstname);
        etLastName = (EditText) findViewById(R.id.et_super_lastname);
        etPhone = (EditText) findViewById(R.id.et_super_phone);
        etEmail = (EditText) findViewById(R.id.et_super_email);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_next_register:
                if(etUsername.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty() || etConfirmPassword.getText().toString().isEmpty()
                        || etFirstName.getText().toString().isEmpty() || etLastName.getText().toString().isEmpty() || etPhone.getText().toString().isEmpty()
                        || etEmail.getText().toString().isEmpty()) {
                    Utilities.ShowDialog("Warning!", "All fields are required to continue.", this);
                } else if(!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())){
                    Utilities.ShowDialog("Warning!", "Passwords do not match.", this);
                } else {
                    InsertSuperUser isu = new InsertSuperUser(this,etUsername.getText().toString(), etPassword.getText().toString(), etLastName.getText().toString(),
                           etFirstName.getText().toString(), etEmail.getText().toString(), etPhone.getText().toString());
                    isu.execute();
                }
                break;
            case R.id.bt_cancel:
                DeleteCompany dc = new DeleteCompany(this, String.valueOf(clientID));
                dc.execute();
                finish();
                break;
            default:
                break;
        }
    }

    public class InsertSuperUser extends AsyncTask<String, Void, Integer> {

        Context context;
        String response;
        String UserName;
        String Password;
        String LastName;
        String FirstName;
        String Email;
        String Phone;

        public InsertSuperUser(Context mContext, String mUserName, String mPassword,
                             String mLastName, String mFirstName, String mEmail, String mPhone){
            this.context = mContext;
            this.UserName = mUserName;
            this.Password = mPassword;
            this.LastName = mLastName;
            this.FirstName = mFirstName;
            this.Email = mEmail;
            this.Phone = mPhone;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!isCancelled()){
                CustomProgressBar.showProgressBar(context, false);
            }
        }

        @Override
        protected Integer doInBackground(String... params) {

            JSONObject json = new JSONObject();
            try {
                json.put("UserName", UserName);
                json.put("LastName", LastName);
                json.put("FirstName", FirstName);
                json.put("Email", Email);
                json.put("Phone", Phone);
                json.put("Extension", "");
                json.put("CellPhone", "");
                json.put("ClientID", clientID);
                json.put("ClientAdmin", "False");
                json.put("ActiveInd", "True");
                json.put("EmailErrors", "False");
                json.put("ReadAcknowledgement", "False");
                json.put("UserPassword", Password);
                json.put("CaseApproval", "False");
                json.put("SupportAdmin", "False");
                json.put("SuperUser", "True");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            HttpClient client = new HttpClient();
            try {
                response = client.postCompany(Constants.URL + "/api/User/PostUser", json.toString());
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
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            CustomProgressBar.hideProgressBar();
            if (result == 1) {
                Utilities.ShowDialog("Network Error", Constants.ERROR_CONNECTION, context);
            } else if (result == 2) {
                Utilities.ShowDialog("Error", Constants.DEFAULT_ERROR_MSG, context);
            } else {
               finish();
            }
        }
    }

    public class DeleteCompany extends AsyncTask<String, Void, String> {

        Context context;
        String clientID;
        String response;

        public DeleteCompany(Context mContext, String mClientID){
            this.context = mContext;
            this.clientID = mClientID;
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
            HttpClient client = new HttpClient();
            try {
                if (!isCancelled()) {
                    response = client.deleteCompany(Constants.URL + "/api/Company/DeleteCompany?id=" + clientID );
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof SocketException
                        || e instanceof UnknownHostException
                        || e instanceof SocketTimeoutException
                        || e instanceof ConnectTimeoutException
                        || e instanceof ClientProtocolException) {

                    return response;
                }

                return response;
            }

            return "Success";
        }

        @Override
        protected void onPostExecute(String result) {
            CustomProgressBar.hideProgressBar();
            if (result.equals("Success")) {
                finish();
            } else {
                Utilities.ShowDialog("Error", result, context);
            }
        }
    }

    public class DoesUserExist extends AsyncTask<String, Void, String> {

        Context context;
        String response;

        public DoesUserExist(Context mContext){
            this.context = mContext;
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
            HttpClient client = new HttpClient();
            try {
                response = client.get(Constants.URL + "/api/User/GetUserExist?username=" + params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof SocketException
                        || e instanceof UnknownHostException
                        || e instanceof SocketTimeoutException
                        || e instanceof ConnectTimeoutException
                        || e instanceof ClientProtocolException) {
                    response = Constants.ERROR_CONNECTION;

                } else {
                    response = Constants.DEFAULT_ERROR_MSG;
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.contains("Error")) {
                Utilities.ShowDialog("Error", result, context);
            } else if(result.contains("Yes")){
                Utilities.ShowDialog("Error","Username already exists in SlideKick, Please try again.",context);
                etUsername.setText("");
                etUsername.requestFocus();
            }

            CustomProgressBar.hideProgressBar();
        }
    }
}
