package com.support.main;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.appolissupport.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import com.support.custom.CustomProgressBar;
import com.support.network.HttpClient;
import com.support.objects.User;
import com.support.utilities.Constants;
import com.support.utilities.DataParser;
import com.support.utilities.SharedPreferenceManager;
import com.support.utilities.Utilities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashSet;

public class LoginActivity extends Activity implements OnClickListener{

	public static EditText et_Username;
	public static EditText et_Password;
	private TextView tvRegister;
	private Button bt_SignIn;
	private User objUser;
	public static String AUTHORIZE;
	private String SENDER_ID = "1033605766068";
	private GoogleCloudMessaging gcm;
	private NotificationHub hub;
	private String HubName = "SlickKickHub";
	private String HubListenConnectionString = "Endpoint=sb://slickkick.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=pmLJzh2kDIkyNhSVTinNLM7eQoxQPj8tF97Y3HAxFEU=";
	private RegisterClient registerClient;
	private static final String BACKEND_ENDPOINT = "http://support-app.azurewebsites.net";
	private String ResetUserID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		SharedPreferenceManager spm = new SharedPreferenceManager(LoginActivity.this);
		MyHandler.mainActivity = this;
		NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);
		gcm = GoogleCloudMessaging.getInstance(this);
		//hub = new NotificationHub(HubName, HubListenConnectionString, this);
		registerClient = new RegisterClient(this, BACKEND_ENDPOINT);

		if(LoginActivity.AUTHORIZE == null) {
			String basicAuthHeader = spm.getString("UserName", "") + ":" + spm.getString("Password", "");
			try {
				basicAuthHeader = Base64.encodeToString(basicAuthHeader.getBytes("UTF-8"), Base64.NO_WRAP);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			LoginActivity.AUTHORIZE = basicAuthHeader;
		}

		if (!spm.getString("UserName", "").isEmpty()) {
			if(spm.getBoolean("IsSuperUser", false)) {
				Intent mainScreenIntent = new Intent(getApplicationContext(), SuperUserActivity.class);
				startActivity(mainScreenIntent);
				finish();
			} else {
				Intent mainScreenIntent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(mainScreenIntent);
				finish();
			}
		}

		et_Username = (EditText) findViewById(R.id.username);
		et_Password = (EditText) findViewById(R.id.password);
		tvRegister = (TextView) findViewById(R.id.bt_register);
		tvRegister.setOnClickListener(this);
		bt_SignIn = (Button) findViewById(R.id.bt_sign_in);
		bt_SignIn.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_password) {

			Intent mainScreenIntent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
			startActivity(mainScreenIntent);


			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_sign_in:
				LoginAsyncTask lgnTask = new LoginAsyncTask(LoginActivity.this);

				if (et_Username.getText().toString().isEmpty() && et_Password.getText().toString().isEmpty()) {
					Utilities.ShowDialog("Warning!", "Please Enter Username and Password.", LoginActivity.this);

				} else if (et_Username.getText().toString().isEmpty() && !et_Password.getText().toString().isEmpty()) {
					Utilities.ShowDialog("Warning!", "Please Enter Username", LoginActivity.this);

				} else if (!et_Username.getText().toString().isEmpty() && et_Password.getText().toString().isEmpty()) {
					Utilities.ShowDialog("Warning!", "Please Enter Password.", LoginActivity.this);
				} else {
					String basicAuthHeader = et_Username.getText().toString().trim()+":"+et_Password.getText().toString();
					try {
						basicAuthHeader = Base64.encodeToString(basicAuthHeader.getBytes("UTF-8"), Base64.NO_WRAP);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

					AUTHORIZE = basicAuthHeader;
					lgnTask.execute(et_Username.getText().toString().trim(), et_Password.getText().toString().trim());
				}
				break;
			case R.id.bt_register:
				Intent mainScreenIntent = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(mainScreenIntent);
				break;
			default:
				break;
		}
	}



	private class LoginAsyncTask extends AsyncTask<String, Void, Integer> {

		Context context;
		String response;

		public LoginAsyncTask(Context mContext){
			this.context = mContext;
		}


		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(!isCancelled()){
				CustomProgressBar.showProgressBar(context, false, "Validating");
			}
		}

		@Override
		protected Integer doInBackground(String... params) {
			try {
				if (!isCancelled()) {

					HttpClient client = new HttpClient();
					response = client.get(Constants.URL + "/api/User/GetUserLogin?username=" + params[0] + "&password="+params[1]);
					objUser = DataParser.getUser(response);

					if(response.isEmpty()){
						return  3;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (e instanceof SocketException
						|| e instanceof UnknownHostException
						|| e instanceof SocketTimeoutException
						|| e instanceof ConnectTimeoutException
						|| e instanceof ClientProtocolException) {

					return 2;
				}
			}

			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if(result == 2){
				Utilities.ShowDialog("Network Error", Constants.ERROR_CONNECTION, context);
			}
			else if (result == 3){
				Utilities.ShowDialog("Invalid Username/Password!", "Please Try Again.", context);
			}
			else {
				registerWithNotificationHubs(objUser.getUsername());
				SharedPreferenceManager spm = new SharedPreferenceManager(LoginActivity.this);
				spm.saveString("UserName", objUser.getUsername());
				spm.saveString("FullName", objUser.getFullName());
				spm.saveInt("UserID", objUser.getID());
				spm.saveString("CompanyName", objUser.getCompanyName());
				spm.saveString("Password", objUser.getPassword());
				spm.saveInt("CompanyID", objUser.getCompanyID());
				spm.saveString("ClientName", objUser.getClientName());
				spm.saveInt("ClientID", objUser.getClientID());
				spm.saveBoolean("IsSupport", objUser.isSupportAdmin());
				spm.saveBoolean("IsAdministrator", objUser.isAdministrator());
				spm.saveBoolean("IsSuperUser", objUser.isSuperUser());
				spm.saveBoolean("CaseApproval", objUser.isCaseApproval());
				spm.saveInt("TopTypeID", objUser.getCommentTypeID());
				spm.saveInt("CaseStatusID", objUser.getCaseStatusID());
				spm.saveBoolean("ShowErrors", objUser.isShowErrors());
				if(objUser.isSuperUser()){
					Intent mainScreenIntent = new Intent(getApplicationContext(), SuperUserActivity.class);
					startActivity(mainScreenIntent);
					finish();
				} else {
					Intent mainScreenIntent = new Intent(getApplicationContext(), MainActivity.class);
					startActivity(mainScreenIntent);
					finish();
				}
			}

			CustomProgressBar.hideProgressBar();
		}
	}



	private void registerWithNotificationHubs(final String username) {
		this.registerClient.setAuthorizationHeader(LoginActivity.AUTHORIZE);
		new AsyncTask() {
			@Override
			protected Object doInBackground(Object... params) {
				try {
					HashSet<String> strings = new HashSet<>();
					strings.add(username);
					String regid = gcm.register(SENDER_ID);
					registerClient.register(regid, strings);
				} catch (Exception e) {
					//ToastNotify("Registration Exception Message - " + e.getMessage());
					return e;
				}
				return null;
			}
		}.execute(null, null, null);
	}

}