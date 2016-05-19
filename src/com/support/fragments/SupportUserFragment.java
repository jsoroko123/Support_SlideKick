package com.support.fragments;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.appolissupport.R;
import com.support.adapters.ClientUserAdapter;
import com.support.adapters.UserDepartmentAdapter;
import com.support.custom.CustomProgressBar;
import com.support.network.HttpClient;
import com.support.main.MainActivity;
import com.support.objects.UserDepartment;
import com.support.objects.UserInfo;
import com.support.utilities.Constants;
import com.support.utilities.DataParser;
import com.support.utilities.SharedPreferenceManager;
import com.support.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SupportUserFragment extends Fragment implements OnItemClickListener, OnClickListener {
	private String updateType;
	private static ListView lvClientUserList;
	private ArrayList<UserInfo> listClientUsers = new ArrayList<>();
	private ArrayList<String> listCLientsForDropdown = new ArrayList<String>();
	private static ArrayList<UserDepartment> listUserDepts = new ArrayList<>();
	private UserDepartmentAdapter cuAdapter = null;
	SharedPreferenceManager spm;
	private static String clientSpinner;
	private ImageView imgAdd, imgAdd2;
	private static final float ROTATE_FROM = 0.0f;
	private static final float ROTATE_TO = 359.0f;
	public static  EditText et6;
	private RotateAnimation ra;

	String username;
	int userID;
	String firstName;
	String lastName;
	String email;
	String phone;
	String ext;
	String cell;
	String password;
	boolean isAdmin;
	boolean isActive;
	boolean caseApproval;
	public static String EXISTS;




	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			MainActivity.FragPageTitle = "Support Team";

		View rootView = inflater.inflate(R.layout.fragment_support_users, container,
				false);
		spm = new SharedPreferenceManager(getActivity());
		lvClientUserList = (ListView)rootView.findViewById(R.id.super_super_support_user_list);
		lvClientUserList.setOnItemClickListener(this);
		imgAdd = (ImageView)rootView.findViewById(R.id.img_add);
		imgAdd.setOnClickListener(this);
		imgAdd2 = (ImageView)rootView.findViewById(R.id.img_add2);
		imgAdd2.setOnClickListener(this);

		if(spm.getBoolean("IsAdministrator",false)) {
			imgAdd.setVisibility(View.VISIBLE);
			imgAdd2.setVisibility(View.VISIBLE);
		} else {
			imgAdd.setVisibility(View.GONE);
			imgAdd2.setVisibility(View.GONE);
		}

		AsyncCallWS2 as = new AsyncCallWS2(getActivity(), "0", String.valueOf(spm.getInt("CompanyID", 0)), "True" );
		as.execute();




		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActivity().getActionBar().setDisplayShowTitleEnabled(true);
        getActivity().getActionBar().setTitle(MainActivity.FragPageTitle);
		return rootView;
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		UserInfo info = listClientUsers.get(position);
		userID = info.getUserID();
		username = info.getUserName();
		firstName =  info.getFirstName();
		lastName =  info.getLastName();
		email =  info.getEmail();
		phone =  info.getPhone();
		ext =  info.getExtension();
		cell =  info.getCellPhone();
		password = info.getUserPassword();
		isAdmin = info.isClientAdmin();
		isActive =  info.isActiveInd();
		caseApproval =  info.isCaseApproval();

		if(spm.getBoolean("IsAdministrator",false)) {
			UserDepartments ud = new UserDepartments(getActivity());
			ud.execute(String.valueOf(userID));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.img_add2:
				ra = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				ra.setDuration(1000);
				imgAdd2.startAnimation(ra);
				UserDepartments ud = new UserDepartments(getActivity());
				ud.execute(String.valueOf(0));
			default:
				break;
		}

	}


	private class AsyncCallWS2 extends AsyncTask<String, Void, Void> {
		Context context;
		private String ClientID;
		private String CompanyID;
		private String IsSupport;
		String response;

		public AsyncCallWS2(Context mContext, String mClientID, String mCompanyID, String mIsSupport ){
			this.context = mContext;
			this.ClientID = mClientID;
			this.CompanyID = mCompanyID;
			this.IsSupport = mIsSupport;
		}

		@Override
		protected void onPreExecute() {
			listClientUsers.clear();

			super.onPreExecute();
			if(!isCancelled()){
				CustomProgressBar.showProgressBar(context, false);
			}
		}

		@Override
		protected Void doInBackground(String... params) {

			HttpClient client = new HttpClient();
			try {
				response = client.get(Constants.URL + "/api/User/GetUsersInfo?CompanyID=" + CompanyID + "&ClientID=" + ClientID + "&IsSupport=" + IsSupport);
			} catch (Exception e) {
				e.printStackTrace();
			}
			listClientUsers = DataParser.getUserInfo(response);


			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			CustomProgressBar.hideProgressBar();
			ClientUserAdapter cuAdapter = new ClientUserAdapter(context,
					listClientUsers);
			lvClientUserList.setAdapter(cuAdapter);
			cuAdapter.notifyDataSetChanged();
		}
	}


	public void showPopUpForSupportUsers(final Context mContext, String username, String firstName, String lastName, String email,
										 String phone, String ext, String cell, String password, boolean isAdmin, boolean isActive, boolean CaseApproval, boolean isEnabled) {

		LayoutInflater li = LayoutInflater.from(mContext);
		View promptsView = li.inflate(R.layout.dialogscanner7, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				mContext);
		final Spinner sp1=(Spinner)promptsView.findViewById(R.id.sp_pop_client);
		final EditText et1=(EditText)promptsView.findViewById(R.id.etpop_firstname);
		final EditText et2=(EditText)promptsView.findViewById(R.id.etpop_lastname);
		final EditText et3=(EditText)promptsView.findViewById(R.id.etpop_email);
		final EditText et4=(EditText)promptsView.findViewById(R.id.etpop_phone);
		final EditText et5=(EditText)promptsView.findViewById(R.id.etpop_password);
		et6 =(EditText)promptsView.findViewById(R.id.etpop_username);
		final EditText et7=(EditText)promptsView.findViewById(R.id.etpop_ext);
		final EditText et8=(EditText)promptsView.findViewById(R.id.etpop_cell);
		final Switch s1= (Switch)promptsView.findViewById(R.id.switch1);
		final Switch s2= (Switch)promptsView.findViewById(R.id.switch2);
		final Switch s3= (Switch)promptsView.findViewById(R.id.switch3);
		final ListView lvUserDept = (ListView)promptsView.findViewById(R.id.lvUserDept);
		final ImageButton imgArrow= (ImageButton)promptsView.findViewById(R.id.btn_arrow);

		imgArrow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if(lvUserDept.getVisibility() == View.VISIBLE){
					lvUserDept.setVisibility(View.GONE);
				} else {
					lvUserDept.setVisibility(View.VISIBLE);
				}

			}


		});

		final LinearLayout ll = (LinearLayout)promptsView.findViewById(R.id.ll_case_approval);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item2, listCLientsForDropdown);
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item );

		ll.setVisibility(View.GONE);
		sp1.setVisibility(View.GONE);

		if(phone.contains("anyType")){
			phone = "";
		}
		if(password.contains("anyType")){
			password = "";
		}

		et1.setText(firstName);
		et2.setText(lastName);
		et3.setText(email);
		et4.setText(phone);
		et5.setText(password);
		et6.setText(username);



		et6.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(et6.isEnabled()){
					if (!hasFocus) {
					DoesUserExist does = new DoesUserExist(getActivity());
					does.execute(et6.getText().toString());
					}
				}
			}
		});
		et7.setText(ext);
		et8.setText(cell);
		s1.setChecked(isAdmin);
		s2.setChecked(isActive);
		s3.setChecked(CaseApproval);

		et6.setEnabled(isEnabled);

		cuAdapter = new UserDepartmentAdapter(getActivity(),
				listUserDepts);

		lvUserDept.setAdapter(cuAdapter);
		cuAdapter.notifyDataSetChanged();


		alertDialogBuilder.setView(promptsView)
				.setPositiveButton("Save",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								if(et1.getText().toString().isEmpty() || et2.getText().toString().isEmpty() || et3.getText().toString().isEmpty() ||
										et4.getText().toString().isEmpty() || et5.getText().toString().isEmpty() || et6.getText().toString().isEmpty()) {
									Utilities.ShowDialog("Warning!", "Please fill in required fields.", getActivity());
									} else{
										InsertSupportUser incu = new InsertSupportUser(
												mContext, spm.getInt("ClientID", 0), et6.getText().toString(), et1.getText().toString(),
												et2.getText().toString(), et3.getText().toString(),
												et4.getText().toString(), et7.getText().toString(), et8.getText().toString(), et5.getText().toString(), s1.isChecked(), s2.isChecked(), s3.isChecked());
										incu.execute();

										ra = new RotateAnimation(ROTATE_TO, ROTATE_FROM, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
										ra.setDuration(1000);
										imgAdd2.startAnimation(ra);
									}

							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								ra = new RotateAnimation(ROTATE_TO, ROTATE_FROM, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
								ra.setDuration(1000);
								imgAdd2.startAnimation(ra);

							}
						});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

	}


	public class InsertSupportUser extends AsyncTask<String, Void, String> {

		Context context;
		int ClientID;
		String UserName;
		String FirstName;
		String LastName;
		String Email;
		String Phone;
		String Extension;
		String CellPhone;
		String userPassword;
		boolean Admin;
		boolean Active;
		boolean CaseApproval;
		String response;


		public InsertSupportUser(Context mContext, int mClientID, String mUsername, String mFirstName, String mLastName, String mEmail,
								 String mPhone, String mExtension, String mCellPhone, String mUserPassword, boolean mAdmin,
								 boolean mActive, boolean mCaseApproval) {
			this.context = mContext;
			this.ClientID = mClientID;
			this.UserName = mUsername;
			this.FirstName = mFirstName;
			this.LastName = mLastName;
			this.Email = mEmail;
			this.Phone = mPhone;
			this.Extension = mExtension;
			this.CellPhone = mCellPhone;
			this.userPassword = mUserPassword;
			this.Admin = mAdmin;
			this.Active = mActive;
			this.CaseApproval = mCaseApproval;

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
			JSONObject json = new JSONObject();
			try {
				json.put("UserName", UserName);
				json.put("LastName", LastName);
				json.put("FirstName", FirstName);
				json.put("Email", Email);
				json.put("Phone", Phone);
				json.put("Extension", Extension);
				json.put("CellPhone", CellPhone);
				json.put("ClientID", ClientID);
				json.put("ClientAdmin", Admin);
				json.put("ActiveInd", Active);
				json.put("EmailErrors", "False");
				json.put("ReadAcknowledgement", "False");
				json.put("UserPassword", userPassword);
				json.put("CaseApproval", CaseApproval);
				json.put("SupportAdmin", "True");
				json.put("SuperUser", "False");

			} catch (JSONException e) {
				e.printStackTrace();
			}

			HttpClient client = new HttpClient();
			try {
				response = client.post(Constants.URL + "/api/User/PostUser", json.toString());
			} catch (Exception e) {
				e.printStackTrace();

			}
			return response;
		}


		@Override
		protected void onPostExecute(String result) {

			for (int a = 0; a < listUserDepts.size(); a++) {
				if (listUserDepts.get(a).getIsActive() == 1) {
					InsertUserDepartment iud = new InsertUserDepartment(getActivity(), result,String.valueOf(listUserDepts.get(a).getCommentTypeID()));
					iud.execute();
				} else {
					DeleteUserDepartment iud = new DeleteUserDepartment(getActivity(), result,String.valueOf(listUserDepts.get(a).getCommentTypeID()));
					iud.execute();
				}
			}

			AsyncCallWS2 as = new AsyncCallWS2(context, "0", String.valueOf(spm.getInt("CompanyID", 0)), "True");
			as.execute();

		}
	}

	public class UserDepartments extends AsyncTask<String, Void, String> {

		Context context;
		String response;

		public UserDepartments(Context mContext){
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
				response = client.get(Constants.URL + "/api/User/GetUserDepartments?UserID=" + params[0]+"&CompanyID=" + spm.getInt("CompanyID",0));
			} catch (Exception e) {
				e.printStackTrace();
			}
			listUserDepts = DataParser.getUserDepartments(response);

			return params[0];
		}


		@Override
		protected void onPostExecute(String result) {
			CustomProgressBar.hideProgressBar();
			if(!result.equals("0")) {
				showPopUpForSupportUsers(context, username, firstName, lastName, email, phone, ext, cell, password, isAdmin, isActive, caseApproval, false);
			} else {
				showPopUpForSupportUsers(context, "","","","", "", "", "","", true, true, false, true);
			}
		}
	}



	public class InsertUserDepartment extends AsyncTask<String, Void, String> {

		Context context;
		String response;
		String UserID;
		String DepartmentID;

		public InsertUserDepartment(Context mContext, String mUserID, String mDepartmentID)
		{
			this.context = mContext;
			this.UserID = mUserID;
			this.DepartmentID = mDepartmentID;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			JSONObject json = new JSONObject();
			try {
				json.put("UserID", UserID);
				json.put("DepartmentID", DepartmentID);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			HttpClient client = new HttpClient();
			try {
				response = client.postCompany(Constants.URL + "/api/User/PostUserDepartment", json.toString());
			} catch (Exception e) {
				e.printStackTrace();
				return e.getMessage();
			}

			return response;
		}


		@Override
		protected void onPostExecute(String result) {
			CustomProgressBar.hideProgressBar();


		}
	}


	public class DeleteUserDepartment extends AsyncTask<String, Void, String> {

		Context context;
		String response;
		String UserID;
		String DepartmentID;

		public DeleteUserDepartment(Context mContext, String mUserID, String mDepartmentID)
		{
			this.context = mContext;
			this.UserID = mUserID;
			this.DepartmentID = mDepartmentID;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {


			HttpClient client = new HttpClient();
			try {
				response = client.delete(Constants.URL + "/api/User/DeleteUserDepartment?UserID=" + UserID + "&DepartmentID=" + DepartmentID);
			} catch (Exception e) {
				e.printStackTrace();
				return e.getMessage();
			}

			return response;
		}


		@Override
		protected void onPostExecute(String result) {
			CustomProgressBar.hideProgressBar();


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
			}


			return response;
		}

		@Override
		protected void onPostExecute(String result) {

			if(result.contains("Yes")){
				Utilities.ShowDialog("Error","Username already exists in SlideKick, Please try again.",context);
				et6.setText("");
				et6.requestFocus();
			}
			CustomProgressBar.hideProgressBar();
		}
	}


}