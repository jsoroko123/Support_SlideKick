package com.support.fragments;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.support.adapters.StatusListAdapter;
import com.support.adapters.SupportUserAdapter;
import com.support.custom.CustomProgressBar;
import com.support.network.HttpClient;
import com.support.main.MainActivity;
import com.example.appolissupport.R;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshSwipeListView;
import com.support.adapters.CasesAdapter;
import com.support.objects.Notes;
import com.support.objects.SupportCases;
import com.support.objects.SupportUser;
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

public class CasesFragment extends Fragment implements OnClickListener, OnItemClickListener {

	public static String TAG = "PGGURU";
	private static PullToRefreshSwipeListView lvSupportCasesList;
	private ArrayList<SupportCases> listSupportCases = new ArrayList<>();
	public static SwipeListView swipeList;
	private ArrayList<SupportCases> listItemInfo = new ArrayList<SupportCases>();
	public static ArrayList<SupportUser> listItemSupportUsers = new ArrayList<>();
	public static ArrayList<Notes> listNotes = new ArrayList<>();
	public static CasesAdapter casesAdapter = null;
    public static SharedPreferenceManager spm;
	public static String ClientName;
	public static int caseStatusID = 0;
    public static int SupportUserID = 0;
	public static String phoneNum = "";
	private String search;
	private int statusID;
	private int clientID;
	private int caseTypeID;
	private String fDate;
	private String tDate;
	private static String notes;
	private int UserID;
	private EditText etNotes;
	private static TextView noCases;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
		MainActivity.FragPageTitle = "Support Cases";
        super.onActivityCreated(savedInstanceState);
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActivity().getActionBar().setDisplayShowTitleEnabled(true);
        getActivity().getActionBar().setTitle(MainActivity.FragPageTitle);
		final ListSupportUsers up = new ListSupportUsers(getActivity());
		up.execute(String.valueOf(spm.getInt("CompanyID", 0)));
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        spm = new SharedPreferenceManager(getActivity());
		View rootView = inflater.inflate(R.layout.fragment_cases, container,
				false);
		lvSupportCasesList = (PullToRefreshSwipeListView) rootView.findViewById(R.id.lv_support_cases);
		lvSupportCasesList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		lvSupportCasesList.setOnItemClickListener(this);
		swipeList = lvSupportCasesList.getRefreshableView();
		noCases = (TextView) rootView.findViewById(R.id.tvNoCases);
		setListData();

		lvSupportCasesList
				.setOnRefreshListener(new OnRefreshListener2<SwipeListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<SwipeListView> refreshView) {
						// TODO Auto-generated method stub
						String label = DateUtils.formatDateTime(
								getActivity(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						//  Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						CheckSharedPrefs();

						refreshData(getActivity(), statusID, clientID, caseTypeID, spm.getInt("CompanyID", 0), search, fDate, tDate, UserID, true, spm.getBoolean("IsSupport", false));
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<SwipeListView> refreshView) {
					}
				});
        return rootView;
	}

	private void setListData() {
		CheckSharedPrefs();
		refreshData(getActivity(), statusID, clientID, caseTypeID, spm.getInt("CompanyID", 0), search, fDate, tDate, UserID, false, spm.getBoolean("IsSupport", false));
		swipeList.setSwipeListViewListener(new BaseSwipeListViewListener() {
			@Override
			public void onClickFrontView(final int position) {
				Fragment newFragment = new DetailsFragment();

				Bundle args = new Bundle();
				args.putString("CaseID", casesAdapter.getItem(position - 1).getCommentID());
				args.putString("ClientContact", casesAdapter.getItem(position - 1).getUsername());
				args.putString("Severity", casesAdapter.getItem(position - 1).getCaseSeverityDesc());
				args.putString("Status", casesAdapter.getItem(position - 1).getCaseStatusDesc());
				args.putString("Username", casesAdapter.getItem(position - 1).getCUser());
				args.putString("SupportUsername", casesAdapter.getItem(position - 1).getSUser());
				args.putString("Color", casesAdapter.getItem(position - 1).getColor());
				args.putString("Date", casesAdapter.getItem(position - 1).getDt_created());
				newFragment.setArguments(args);
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.linear, newFragment);
				transaction.addToBackStack(null);
				transaction.commit();
			}

			@Override
			public void onClickBackView(final int position) {

			}

			@Override
			public void onOpened(int position, boolean toRight) {
				// TODO Auto-generated method stub
				super.onOpened(position - 1, toRight);
			}

			@Override
			public void onMove(int position, float x) {
				// TODO Auto-generated method stub
				super.onMove(position - 1, x);
			}

			@Override
			public int onChangeSwipeMode(int position) {
				// TODO Auto-generated method stub
				return SwipeListView.SWIPE_MODE_DEFAULT;
			}

			@Override
			public void onStartOpen(int position, int action, boolean right) {
				// TODO Auto-generated method stub
				super.onStartOpen(position - 1, action, right);

			}
		});
		try{
			Display display = getActivity().getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			if(MainActivity.isSupport) {
				swipeList.setOffsetLeft(0);
			} else {
				swipeList.setOffsetLeft(width / 2);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void refreshData(Context context, int caseStatusID, int clientID, int TypeID, int CompanyID, String search, String from, String to, int userID, boolean isrefresh,  boolean isSupport) {
		ListCasesAsyncCallWS mLoadDataTask = new ListCasesAsyncCallWS(context, caseStatusID, clientID, TypeID, CompanyID, search, from, to, userID, isrefresh, isSupport);
		mLoadDataTask.execute();
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	@Override
	 public void onClick(View v) {
	}


	public void showPopUpForStatus(final Context mContext, final int mCaseStatusID) {
		LayoutInflater li = LayoutInflater.from(mContext);
		View promptsView = li.inflate(R.layout.dialogscanner3, null);
		caseStatusID = 0;

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(

				mContext);

		alertDialogBuilder.setTitle("Update Case Status");
		final ListView lv=(ListView)promptsView.findViewById(R.id.list_status);
		final StatusListAdapter adapter = new StatusListAdapter(mContext, MainActivity.statuses, mCaseStatusID);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				for(int a = 0; a < arg0.getChildCount(); a++) {
					arg0.getChildAt(a).setBackgroundColor(mContext.getResources().getColor(R.color.Gray22));
				}
				arg1.setBackgroundColor(mContext.getResources().getColor(R.color.Blue19));
				caseStatusID = adapter.getItem(position).getCaseStatusID();
							}
		});

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (caseStatusID != 0) {
									final UpdateCaseStatus up = new UpdateCaseStatus(mContext, CasesAdapter.caseID, caseStatusID);
									up.execute();
								} else {
									if(mCaseStatusID != 0){
										Toast.makeText(mContext,"Case already set to this status.", Toast.LENGTH_LONG).show();
									} else {
										Toast.makeText(mContext,"Status was not selected.", Toast.LENGTH_LONG).show();
									}
								}
							}
						})

				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								swipeList.closeOpenedItems();
								swipeList.refreshDrawableState();
							}
						});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
	}

	public void showPopUpForSupportUsers(final Context mContext, final int sUserID) {
			LayoutInflater li = LayoutInflater.from(mContext);
		View promptsView = li.inflate(R.layout.dialogscanner4, null);
		SupportUserID = -1;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				mContext);

		alertDialogBuilder.setTitle("Assign Support User");

		final ListView lv=(ListView)promptsView.findViewById(R.id.list_users);
		final SupportUserAdapter adapter = new SupportUserAdapter(mContext, listItemSupportUsers, sUserID);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				for (int a = 0; a < arg0.getChildCount(); a++) {
					arg0.getChildAt(a).setBackgroundColor(mContext.getResources().getColor(R.color.Gray22));
				}
				arg1.setBackgroundColor(mContext.getResources().getColor(R.color.Blue19));
				SupportUserID = adapter.getItem(position).getUserID();
			}
		});

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (SupportUserID != -1) {
									final UpdateCaseAssignment up = new UpdateCaseAssignment(mContext, CasesAdapter.caseID, SupportUserID);
									up.execute();

								} else {
									if(sUserID != -1){
										Toast.makeText(mContext, "User is already assigned to Case: "+CasesAdapter.caseID+".", Toast.LENGTH_LONG).show();
									} else
										Toast.makeText(mContext, " Please select user to assign to case.", Toast.LENGTH_LONG).show();
								}
							}
						})

				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								swipeList.closeOpenedItems();
								swipeList.refreshDrawableState();
							}
						});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	public void showPopUpForPhoneNumbers(final Context mContext, ArrayList<String> phones, String contact) {
		LayoutInflater li = LayoutInflater.from(mContext);
		View promptsView = li.inflate(R.layout.dialogscanner5, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				mContext);
		phoneNum = "";

		final ListView lv=(ListView)promptsView.findViewById(R.id.list_phone);

		final ArrayAdapter adapter = new ArrayAdapter(mContext,
				android.R.layout.simple_list_item_activated_1, phones);
		lv.setAdapter(adapter);

		if(phones.size()>0) {
			lv.setClickable(true);
			alertDialogBuilder.setTitle("Contact " + contact + "?");

			alertDialogBuilder.setView(promptsView)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									if (!phoneNum.isEmpty()) {
										Intent intent = new Intent("android.intent.action.DIAL");
										intent.setData(Uri.parse("tel: " + phoneNum.substring(0, 10) + ""));
										mContext.startActivity(intent);
									} else {
									Toast.makeText(mContext," Please select phone number.", Toast.LENGTH_LONG).show();
									}
								}
							})

					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
									swipeList.closeOpenedItems();
									swipeList.refreshDrawableState();
								}
							});
		} else {
			lv.setClickable(false);
			phones.add("No contact #'s found for \n" + contact + ".");

			alertDialogBuilder.setView(promptsView)

					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});
		}
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

					Object o = lv.getItemAtPosition(position);
					phoneNum = Utilities.stripNonDigits(o.toString());


				}
			});
        AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}


	public void showPopUpForNotes(final Context mContext, int CaseNumber) {
		CaseNotes cn = new CaseNotes(mContext, CaseNumber);
		cn.execute(String.valueOf(spm.getInt("UserID", 0)));
	}

	private class ListCasesAsyncCallWS extends AsyncTask<String, Void, Integer> {

		Context context;
		int ClientID, CommentTypeID, CompanyID, CaseStatusID, SupportUserID;
		String search, BeginDate, EndDate, response;
		boolean isSupportUser, isRefresh;

		public ListCasesAsyncCallWS(Context mContext, int mcaseStatusID, int mClientID, int mTypeID, int mCompanyID,
									String mSearch, String mFrom, String mTo, int mUserID,
									boolean mIsRefresh, boolean mSupport){
			this.context = mContext;
			this.CaseStatusID = mcaseStatusID;
			this.ClientID = mClientID;
			this.CompanyID = mCompanyID;
			this.CommentTypeID = mTypeID;
			this.search = mSearch;
			this.BeginDate = mFrom;
			this.EndDate = mTo;
			this.SupportUserID = mUserID;
			this.isRefresh = mIsRefresh;
			this.isSupportUser = mSupport;

		}

		@Override
		protected void onPreExecute() {
			listItemInfo.clear();
			super.onPreExecute();
			if(!isCancelled()){
				if(!isRefresh) {
					CustomProgressBar.showProgressBar(context, false);
				}
			}

		}
		@Override
		protected Integer doInBackground(String... params) {
			HttpClient client = new HttpClient();
			try {
				response = client.get(Constants.URL + "/api/Case/GetCases?ClientID=" + String.valueOf(ClientID) + "&CommentTypeID=" + String.valueOf(CommentTypeID)
						+ "&CompanyID=" + String.valueOf(CompanyID) + "&CaseStatusID=" + String.valueOf(CaseStatusID)+ "&Search=" + search
						+ "&SupportUserID=" + String.valueOf(SupportUserID)+ "&isSupportUser=" + String.valueOf(isSupportUser)
						+ "&BeginDate=" + BeginDate + "&EndDate=" + EndDate + "&UserID=" + String.valueOf(spm.getInt("UserID",0)));
			} catch (Exception e) {
				e.printStackTrace();
				CustomProgressBar.hideProgressBar();
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
			listSupportCases = DataParser.getSupportCases(response);
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {

			if(result == 1){
				Utilities.ShowDialog("Network Error", Constants.ERROR_CONNECTION, context);
			} else if (result == 2){
				Utilities.ShowDialog("Error", Constants.DEFAULT_ERROR_MSG, context);
			} else {
				Log.i(TAG, "onPostExecute");
				swipeList.invalidateViews();
				swipeList.closeOpenedItems();
				swipeList.refreshDrawableState();
				lvSupportCasesList.refreshDrawableState();
				casesAdapter = new CasesAdapter(context,
						listSupportCases);

				lvSupportCasesList.setAdapter(casesAdapter);
				casesAdapter.notifyDataSetChanged();

				if (listSupportCases != null) {
					if (listSupportCases.isEmpty()) {
						noCases.setVisibility(View.VISIBLE);
						noCases.setText("No Cases Found.");
					} else {
						noCases.setVisibility(View.GONE);
					}
				} else {
					noCases.setVisibility(View.VISIBLE);
					noCases.setText("No Cases Found.");
				}
			}
			CustomProgressBar.hideProgressBar();
			lvSupportCasesList.onRefreshComplete();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			Log.i(TAG, "onProgressUpdate");
		}
		}



	public class UpdateCaseStatus extends AsyncTask<String, Void, Integer> {

		Context context;
		int caseNumber;
		int caseStatusID;
		String response;


		public UpdateCaseStatus(Context mContext, int mCaseNumber, int mcaseStatusID) {
			this.context = mContext;
			this.caseNumber = mCaseNumber;
			this.caseStatusID = mcaseStatusID;
		}


		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!isCancelled()) {
				CustomProgressBar.showProgressBar(context, false);
			}
		}

		@Override
		protected Integer doInBackground(String... params) {

			HttpClient client = new HttpClient();
			try {
				if (!isCancelled()) {
					response = client.post(Constants.URL + "/api/Case/UpdateCaseStatus?CaseID=" + caseNumber + "&caseStatusID="
							+ caseStatusID + "&userID=" + spm.getInt("UserID", 0), "");
				}
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
			if(result == 1){
				Utilities.ShowDialog("Network Error", Constants.ERROR_CONNECTION, context);
			} else if (result == 2){
				Utilities.ShowDialog("Error", Constants.DEFAULT_ERROR_MSG, context);
			}
			else {
				Log.i(TAG, "onPostExecute");
				CheckSharedPrefs();
				refreshData(context, statusID, clientID, caseTypeID, spm.getInt("CompanyID", 0), search, fDate, tDate, UserID, true, spm.getBoolean("IsSupport", false));
			}
		}
	}

	private class ListSupportUsers extends AsyncTask<String, Void, Integer> {

		Context context;
		String response;

		public ListSupportUsers(Context mContext) {
			this.context = mContext;
		}

		@Override
		protected void onPreExecute() {
			listItemSupportUsers.clear();
			super.onPreExecute();
			if (!isCancelled()) {
			}
		}

		@Override
		protected Integer doInBackground(String... params) {
			HttpClient client = new HttpClient();
			try {
				response = client.get(Constants.URL + "/api/User/GetSupportUserDrop?CompanyID=" + params[0]);
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
			listItemSupportUsers = DataParser.getSupportUserDrop(response);
			SupportUser su = new SupportUser(0, "Unassigned");
			listItemSupportUsers.add(0, su);
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == 1) {
				Utilities.ShowDialog("Network Error", Constants.ERROR_CONNECTION, context);
			} else if (result == 2) {
				Utilities.ShowDialog("Error", Constants.DEFAULT_ERROR_MSG, context);
			}
		}
	}



	private class CaseNotes extends AsyncTask<String, Void, Integer> {
		Context context;
		int CaseID;
		String response;

		public CaseNotes(Context mContext, int mCaseID){
			this.context = mContext;
			this.CaseID = mCaseID;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			listNotes.clear();
			if(!isCancelled()){
			}
			CustomProgressBar.showProgressBar(context, false);
		}

		@Override
		protected Integer doInBackground(String... params) {
			HttpClient client = new HttpClient();
			try {
				response = client.get(Constants.URL + "/api/Notes/GetReminder?CaseID=" + CaseID + "&UserID=" + params[0]);
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
			listNotes = DataParser.getNotes(response);
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
				LayoutInflater li = LayoutInflater.from(context);
				View promptsView = li.inflate(R.layout.dialogscanner6, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);

				alertDialogBuilder.setTitle("Case Notes");

				StringBuilder build = new StringBuilder();
				for (Notes notes : listNotes) {
					build.append(notes.getReminder()+"\n");
				}

				etNotes=(EditText)promptsView.findViewById(R.id.etNotes);
				etNotes.setText(build.toString());

				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(promptsView)
						.setPositiveButton("Save",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {

										InsertReminder ir = new InsertReminder(context, CaseID, spm.getInt("UserID", 0), etNotes.getText().toString());
										ir.execute();
									}
								})

						.setNeutralButton("Delete",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {

										DeleteReminder ir = new DeleteReminder(context, CaseID, spm.getInt("UserID",0));
										ir.execute();
									}
								})

						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
										swipeList.closeOpenedItems();
										swipeList.refreshDrawableState();
									}
								});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		}
	}

    private class UpdateCaseAssignment extends AsyncTask<String, Void, Integer> {

		Context context;
		int caseNumber;
		int userID;
		String response;


		public UpdateCaseAssignment(Context mContext, int mCaseNumber, int mUserID) {
			this.context = mContext;
			this.caseNumber = mCaseNumber;
			this.userID = mUserID;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!isCancelled()) {
				CustomProgressBar.showProgressBar(context, false);
			}
		}

		@Override
		protected Integer doInBackground(String... params) {
			HttpClient client = new HttpClient();
			try {
				if (!isCancelled()) {
					response = client.post(Constants.URL + "/api/Case/UpdateCaseAssignment?CaseID=" + caseNumber + "&UserID=" + userID, "");
				}
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
				Log.i(TAG, "onPostExecute");
				CheckSharedPrefs();
				refreshData(context, statusID, clientID, caseTypeID, spm.getInt("CompanyID", 0), search, fDate, tDate, UserID, true, spm.getBoolean("IsSupport", false));
			}
		}
    }

	public class InsertReminder extends AsyncTask<String, Void, Integer> {
		Context context;
		int caseID;
		int userID;
		String notes;
		String response;

		public InsertReminder(Context mContext, int mCaseID, int mUserID, String mNotes){
			this.context = mContext;
			this.caseID = mCaseID;
			this.userID = mUserID;
			this.notes = mNotes;
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
				json.put("CaseID", String.valueOf(caseID));
				json.put("UserID", String.valueOf(userID));
				json.put("Reminder", notes);
				json.put("SendText", "False");
				json.put("SendEmail", "False");
				json.put("ShowAllUsers", "False");
			} catch (JSONException e) {
				return 2;
			}

			HttpClient client = new HttpClient();
			try {
				response = client.post(Constants.URL + "/api/Notes/PostReminder", json.toString());
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
				Log.i(TAG, "onPostExecute");
				CheckSharedPrefs();
				refreshData(context, statusID, clientID, caseTypeID, spm.getInt("CompanyID", 0), search, fDate, tDate, UserID, true, spm.getBoolean("IsSupport", false));
			}
		}
	}

	public class DeleteReminder extends AsyncTask<String, Void, Integer> {

		Context context;
		int caseID;
		int userID;
		String response;

		public DeleteReminder(Context mContext, int mCaseID, int mUserID){
			this.context = mContext;
			this.caseID = mCaseID;
			this.userID = mUserID;
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
			HttpClient client = new HttpClient();
			try {
				if (!isCancelled()) {
					response = client.delete(Constants.URL + "/api/Notes/DeleteReminder?CaseID=" + caseID + "&userID="+userID);
				}
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

			if (result == 1) {
				Utilities.ShowDialog("Network Error", Constants.ERROR_CONNECTION, context);
			} else if (result == 2) {
				Utilities.ShowDialog("Error", Constants.DEFAULT_ERROR_MSG, context);
			} else {
				Log.i(TAG, "onPostExecute");
				CheckSharedPrefs();
				refreshData(context, statusID, clientID, caseTypeID, spm.getInt("CompanyID", 0), search, fDate, tDate, UserID, true, spm.getBoolean("IsSupport", false));
			}
		}
	}

	public void InsertTime(Context ctx, int CaseID, int UserID, int StatusID){
		InsertSupportTime ist = new InsertSupportTime(ctx, CaseID, UserID, StatusID);
		ist.execute();
	}

	public void UpdateTime(Context ctx, int CaseID, int UserID, int StatusID){
		UpdateSupportTime ist = new UpdateSupportTime(ctx, CaseID, UserID, StatusID);
		ist.execute();
	}



	private class InsertSupportTime extends AsyncTask<String, Void, Integer> {
		Context context;
		int caseID;
		int userID;
		int caseStatusID;
		String response;


		public InsertSupportTime(Context mContext, int mCaseID, int mUserID, int mCaseStatusID){
			this.context = mContext;
			this.caseID = mCaseID;
			this.userID = mUserID;
			this.caseStatusID = mCaseStatusID;
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
				json.put("CommentID", String.valueOf(caseID));
				json.put("UserID", String.valueOf(userID));
				json.put("CaseStatusID", String.valueOf(caseStatusID));
			} catch (JSONException e) {
				return 2;
			}

			HttpClient client = new HttpClient();
			try {
				response = client.post(Constants.URL + "/api/Time/Post", json.toString());
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
				Toast.makeText(context, "Time started for Case: "+ caseID, Toast.LENGTH_LONG).show();
				CheckSharedPrefs();
				refreshData(context, statusID, clientID, caseTypeID, spm.getInt("CompanyID", 0), search, fDate, tDate, UserID, true, spm.getBoolean("IsSupport", false));
			}
		}
	}



	private class UpdateSupportTime extends AsyncTask<String, Void, Integer> {
		Context context;
		int caseID;
		int userID;
		int caseStatusID;
		String response;


		public UpdateSupportTime(Context mContext, int mCaseID, int mUserID, int mCaseStatusID){
			this.context = mContext;
			this.caseID = mCaseID;
			this.userID = mUserID;
			this.caseStatusID = mCaseStatusID;
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
				json.put("CommentID", String.valueOf(caseID));
				json.put("UserID", String.valueOf(userID));
				json.put("CaseStatusID", String.valueOf(caseStatusID));
			} catch (JSONException e) {
				return 2;
			}

			HttpClient client = new HttpClient();
			try {
				response = client.put(Constants.URL + "/api/Time/Put", json.toString() );
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
				Toast.makeText(context, "Time stopped for Case: "+ caseID, Toast.LENGTH_LONG).show();
				CheckSharedPrefs();
				refreshData(context, statusID, clientID, caseTypeID, spm.getInt("CompanyID", 0), search, fDate, tDate, UserID, true, spm.getBoolean("IsSupport", false));
			}
		}
	}

	public void CheckSharedPrefs(){
		if(!spm.getString("Search", "").isEmpty()){
			search = spm.getString("Search", "");
		} else {
			search = "";
		}

		if(spm.getInt("Status", 0) != 0){
			statusID = spm.getInt("Status", 0);
		} else {
			statusID = spm.getInt("CaseStatusID", 0);
		}

		if(spm.getInt("Client", 0)!=0){
			clientID = spm.getInt("Client", 0);
		} else {
			if(MainActivity.isSupport) {
				clientID = 0;
			} else {
				clientID = spm.getInt("ClientID", 0);
			}
		}

		if(spm.getInt("Type", 0) != 0) {
			caseTypeID = spm.getInt("Type", 0);
		} else {
			caseTypeID = 0;
		}

		if (spm.getBoolean("MyCases", false)) {
			UserID = spm.getInt("UserID", 0);
		} else {
			UserID = 0;
		}

		if (spm.getBoolean("DateRange", false)) {
			fDate = spm.getString("FromDate", "");
			tDate = spm.getString("ToDate", "");
		} else {

			fDate = "1900/03/01T10:00:00-5:00";
			tDate = "3001/03/01T10:00:00-5:00";
		}
	}



}