package com.support.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class RegisterActivity extends Activity implements OnClickListener {

    private Button btnNext;
    private Button btnCancel;
    private EditText etCompany, etAddress, etCity, etState, etZip, etEmail, etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
        setContentView(R.layout.activity_register);
        etCompany = (EditText) findViewById(R.id.et_company_name);
        etAddress = (EditText) findViewById(R.id.et_company_address);
        etCity = (EditText) findViewById(R.id.et_company_city);
        etState = (EditText) findViewById(R.id.et_company_state);
        etZip = (EditText) findViewById(R.id.et_company_zip);
        etEmail = (EditText) findViewById(R.id.et_company_email);
        etPhone = (EditText) findViewById(R.id.et_company_phone);
        btnNext = (Button) findViewById(R.id.bt_next_register);
        btnNext.setOnClickListener(this);
        btnCancel = (Button) findViewById(R.id.bt_cancel);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_next_register:
                if(etCompany.getText().toString().isEmpty() || etAddress.getText().toString().isEmpty() || etCity.getText().toString().isEmpty()
                        || etState.getText().toString().isEmpty() || etZip.getText().toString().isEmpty() || etEmail.getText().toString().isEmpty()
                            || etPhone.getText().toString().isEmpty()) {
                   Utilities.ShowDialog("Warning!", "All fields are required to continue.", this);
                } else {
                    InsertCompany ic = new InsertCompany(this, etCompany.getText().toString(), etAddress.getText().toString(), etCity.getText().toString(),
                            etState.getText().toString(), etZip.getText().toString(), etEmail.getText().toString(), etPhone.getText().toString());
                    ic.execute();
                }
                break;
            case R.id.bt_cancel:

                finish();
                break;
            default:
                break;
        }

    }

    public class InsertCompany extends AsyncTask<String, Void, Integer> {

        Context context;
        String response;
        String CompanyName;
        String CompanyAddress;
        String CompanyCity;
        String CompanyState;
        String CompanyZip;
        String CompanyEmail;
        String CompanyPhone;


        public InsertCompany(Context mContext, String mCompanyName, String mCompanyAddress,
                             String mCompanyCity, String mCompanyState, String mCompanyZip, String mCompanyEmail,
                             String mCompanyPhone){
            this.context = mContext;
            this.CompanyName = mCompanyName;
            this.CompanyAddress = mCompanyAddress;
            this.CompanyCity = mCompanyCity;
            this.CompanyState = mCompanyState;
            this.CompanyZip = mCompanyZip;
            this.CompanyEmail = mCompanyEmail;
            this.CompanyPhone = mCompanyPhone;
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
                json.put("CompanyName", CompanyName);
                json.put("CompanyAddress", CompanyAddress);
                json.put("CompanyCity", CompanyCity);
                json.put("CompanyState", CompanyState);
                json.put("CompanyZip", CompanyZip);
                json.put("CompanyEmail", CompanyEmail);
                json.put("CompanyPhone", CompanyPhone);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpClient client = new HttpClient();
            try {
                response = client.postCompany(Constants.URL + "/api/Company/PostCompany", json.toString());
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
                Intent mainScreenIntent = new Intent(getApplicationContext(), RegisterSuperUserActivity.class);
                mainScreenIntent.putExtra("CompanyID", response);
                startActivity(mainScreenIntent);
                finish();
            }
        }
    }
}
