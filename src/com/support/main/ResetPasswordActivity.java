package com.support.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class ResetPasswordActivity extends Activity implements OnClickListener {

    private Button btnCancel, btnOk;
    private static LinearLayout llPassword, llEmail, llCode;
    private String ResetUserID;

    private EditText etUser, etCode, etPassword, etConfirmPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
        setContentView(R.layout.activity_reset_password);
        btnCancel = (Button) findViewById(R.id.bt_cancel);
        btnCancel.setOnClickListener(this);
        btnOk = (Button) findViewById(R.id.bt_ok);
        btnOk.setOnClickListener(this);
        llPassword = (LinearLayout) findViewById(R.id.ll_new_password);
        llEmail = (LinearLayout) findViewById(R.id.ll_email);
        llCode = (LinearLayout) findViewById(R.id.ll_code);
        etUser = (EditText) findViewById(R.id.etEmailorUser);
        etCode = (EditText) findViewById(R.id.etVerify);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_cancel:
                finish();
                break;
            case R.id.bt_ok:
                if(llEmail.getVisibility() == View.VISIBLE){
                    if(etUser.getText().toString().isEmpty()){
				        Utilities.ShowDialog("Error","Please Enter Username or Email", this);
                    } else {
                        VerifyUser vu = new VerifyUser(this);
                        vu.execute(etUser.getText().toString().trim());
                    }
                } else if(llCode.getVisibility() == View.VISIBLE){
                    ValidateCode vc = new ValidateCode(this);
                    vc.execute(ResetUserID, etCode.getText().toString());
                }
                else if(llPassword.getVisibility() == View.VISIBLE){
                    if(!etPassword.getText().toString().trim().equals(etConfirmPassword.getText().toString().trim())) {
                        Utilities.ShowDialog("Error", "Passwords do not match.", this);
                    } else {
                        UpdatePassword up = new UpdatePassword(this, ResetUserID, etPassword.getText().toString());
                        up.execute();
                    }
                }
                break;
            default:
                break;
        }
    }

    public void showPopUpForReset(final Context mContext, String txt) {
        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.dialog_reset, null);

        TextView tvReset = (TextView) promptsView.findViewById(R.id.tv_reset);
        tvReset.setText(txt);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);

        alertDialogBuilder.setTitle("Success");

        alertDialogBuilder.setView(promptsView)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                etUser.setEnabled(false);
                                llEmail.setVisibility(View.GONE);
                                Animation out = AnimationUtils.loadAnimation(ResetPasswordActivity.this, R.anim.slide_in_from_bottom);
                                llCode.setVisibility(View.VISIBLE);
                                llCode.startAnimation(out);

                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public class ValidateCode extends AsyncTask<String, Void, String> {

        Context context;
        String response;

        public ValidateCode(Context mContext){
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
                response = client.getLogin(Constants.URL + "/api/User/GetValidCode?UserID=" + params[0] +"&Code=" + params[1]);
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
            if(result.equals("0")){
                Utilities.ShowDialog("Error", "Invalid Code. Please try again.", context);
            } else {
                etCode.setEnabled(false);
                llCode.setVisibility(View.GONE);
                Animation out = AnimationUtils.loadAnimation(ResetPasswordActivity.this, R.anim.slide_in_from_bottom);
                llPassword.setVisibility(View.VISIBLE);
                llPassword.startAnimation(out);
            }

            CustomProgressBar.hideProgressBar();
        }
    }

    private class UpdatePassword extends AsyncTask<String, Void, Integer> {
        Context context;
        String userID;
        String newPassword;
        String response;


        public UpdatePassword(Context mContext, String mUserID, String mNewPassword){
            this.context = mContext;
            this.userID = mUserID;
            this.newPassword = mNewPassword;
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
                json.put("UserID", String.valueOf(userID));
                json.put("UserPassword", newPassword);
            } catch (JSONException e) {
                return 2;
            }

            HttpClient client = new HttpClient();
            try {
                response = client.putLogin(Constants.URL + "/api/User/PutNewPassword", json.toString() );
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
                Toast.makeText(context, "Password successfully updated.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class VerifyUser extends AsyncTask<String, Void, String> {

        Context context;
        String response;

        public VerifyUser(Context mContext){
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
                response = client.getLogin(Constants.URL + "/api/User/GetVerifyUser?username=" + params[0]);
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
            if(result.equals("null")){
                Utilities.ShowDialog("Error", "User does not exist. Please try again or contact your administrator.", context);
            }
            else if(result.contains("Error")|| result.isEmpty()) {
                Utilities.ShowDialog("Error", "User does not exist. Please try again or contact your administrator.", context);
            } else {
                String[] emailAndCode = result.replace("\"", "").split("\\|");
                ResetUserID = emailAndCode[2];
                SendVerificationEmail sve = new SendVerificationEmail(context, emailAndCode[0],"SlideKick Verification Code","Your SlideKick Verification Code is "+ emailAndCode[1]);
                sve.execute();
            }

            CustomProgressBar.hideProgressBar();
        }
    }

    public class SendVerificationEmail extends AsyncTask<String, Void, String> {

        Context context;
        String response;
        String ToEmail;
        String Subject;
        String Body;

        public SendVerificationEmail(Context mContext, String mToEmail, String mSubject, String mBody){
            this.context = mContext;
            this.ToEmail = mToEmail;
            this.Subject = mSubject;
            this.Body = mBody;
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
            JSONObject json = new JSONObject();
            try {
                json.put("ToEmail", ToEmail);
                json.put("Subject", Subject);
                json.put("Body", Body);
            } catch (JSONException e) {
                e.printStackTrace();
                return "Error";
            }
            HttpClient client = new HttpClient();
            try {
                response = client.postCompany(Constants.URL + "/api/Email/PostEmail", json.toString());
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
                Utilities.ShowDialog("Error", "Error sending verification email. Please try again.", context);
            } else {
                showPopUpForReset(context, "Verification Code has been sent to "+ ToEmail);
            }

            CustomProgressBar.hideProgressBar();
        }
    }
}
