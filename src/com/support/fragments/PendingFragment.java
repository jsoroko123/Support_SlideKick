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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.appolissupport.R;
import com.support.adapters.CaseAreaAdapter;
import com.support.adapters.CaseTypeAdapter;
import com.support.adapters.PendingAttachAdapter;
import com.support.adapters.PendingCasesAdapter;
import com.support.adapters.SeverityAdapter;
import com.support.custom.CustomProgressBar;
import com.support.network.HttpClient;
import com.support.main.MainActivity;
import com.support.objects.CaseArea;
import com.support.objects.CaseSeverity;
import com.support.objects.CaseType;
import com.support.objects.PendingAttachments;
import com.support.objects.PendingCases;
import com.support.utilities.Constants;
import com.support.utilities.DataParser;
import com.support.utilities.SharedPreferenceManager;
import com.support.utilities.Utilities;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class PendingFragment extends Fragment implements OnItemClickListener {

	private ArrayList<PendingCases> listItemInfo = new ArrayList<>();
	private ListView lvPendingCases;
	private PendingCasesAdapter pendingCasesAdapter = null;
	private TextView tvNoCases;
	private SharedPreferenceManager spm;

	private static ListView lvAttachList;
	private ArrayList<CaseArea> listCaseReasons = new ArrayList<>();
	private ArrayList<CaseSeverity> listSeverity = new ArrayList<>();
	private ArrayList<CaseType> listCaseType = new ArrayList<>();
	public static ArrayList<PendingAttachments> listAttachments = new ArrayList<>();

	private int commentTypeID;
	private int areaID;
	private int severityID;
	private String commentTitle, comments;
	private static String tempCommentID;

	public PendingFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MainActivity.FragPageTitle = "Pending Cases";
		View rootView = inflater.inflate(R.layout.fragment_pending, container,
				false);
		lvPendingCases = (ListView) rootView.findViewById(R.id.list_pending_cases);
		lvPendingCases.setOnItemClickListener(this);
		tvNoCases = (TextView) rootView.findViewById(R.id.tvNoPendingCases);
		spm = new SharedPreferenceManager(getActivity());
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActivity().getActionBar().setDisplayShowTitleEnabled(true);
        getActivity().getActionBar().setTitle(MainActivity.FragPageTitle);
		ListCasesAsyncCallWS as = new ListCasesAsyncCallWS(getActivity(), spm.getInt("ClientID",0));
		as.execute();
		return rootView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		DepartmentAsyncCall dac = new DepartmentAsyncCall(getActivity());
		dac.execute(String.valueOf(spm.getInt("CompanyID",0)),"0");

		tempCommentID = String.valueOf(listItemInfo.get(position).getPendingCommentID());
		commentTypeID = listItemInfo.get(position).getCommentTypeID();
		areaID = listItemInfo.get(position).getCaseReasonID();
		severityID = listItemInfo.get(position).getCaseSeverityID();
		commentTitle = listItemInfo.get(position).getCommentTitle();
		comments = listItemInfo.get(position).getComments();
	}

	private class ListCasesAsyncCallWS extends AsyncTask<String, Void, Integer> {

		Context context;
		int ClientID;
		String response;

		public ListCasesAsyncCallWS(Context mContext, int mClientID) {
			this.context = mContext;
			this.ClientID = mClientID;
		}

		@Override
		protected void onPreExecute() {
			listItemInfo.clear();
			super.onPreExecute();
			if (!isCancelled()) {
				CustomProgressBar.showProgressBar(context, false);
			}

		}

		@Override
		protected Integer doInBackground(String... params) {
			HttpClient client = new HttpClient();
			try {
				response = client.get(Constants.URL + "/api/PendingCase/Get?ClientID=" + String.valueOf(ClientID));

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
			listItemInfo = DataParser.getPendingCases(response);
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
				pendingCasesAdapter = new PendingCasesAdapter(context,
						listItemInfo);
				lvPendingCases.setAdapter(pendingCasesAdapter);
				pendingCasesAdapter.notifyDataSetChanged();

				if (listItemInfo.isEmpty()) {
					tvNoCases.setVisibility(View.VISIBLE);
				} else {
					tvNoCases.setVisibility(View.GONE);
				}
			}
		}
	}

	public void showPopUpForCaseApproval(final Context mContext) {
		LayoutInflater li = LayoutInflater.from(mContext);
		View promptsView = li.inflate(R.layout.dialogscanner10, null);

		final Spinner spinnerDept=(Spinner)promptsView.findViewById(R.id.spinnerEnvironment);
		CaseTypeAdapter spinnerArrayAdapter3 = new CaseTypeAdapter(mContext, listCaseType);
		spinnerArrayAdapter3.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		spinnerDept.setAdapter(spinnerArrayAdapter3);

		final Spinner spinnerArea=(Spinner)promptsView.findViewById(R.id.spArea);
		CaseAreaAdapter spinnerArrayAdapter2 = new CaseAreaAdapter(mContext, listCaseReasons);
		spinnerArrayAdapter2.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		spinnerArea.setAdapter(spinnerArrayAdapter2);

		final Spinner spinnerSeverity=(Spinner)promptsView.findViewById(R.id.spSeverity);
		SeverityAdapter spinnerArrayAdapter1 = new SeverityAdapter(mContext, listSeverity);
		spinnerArrayAdapter1.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		spinnerSeverity.setAdapter(spinnerArrayAdapter1);

		final EditText etCommentTitle=(EditText) promptsView.findViewById(R.id.etSubject);
		final EditText etComments=(EditText) promptsView.findViewById(R.id.etResp);

		final ListView lvPendingAttach=(ListView) promptsView.findViewById(R.id.lvPendingAttachments);
		PendingAttachAdapter pendAdapter = new PendingAttachAdapter(mContext, listAttachments);
		pendAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		lvPendingAttach.setAdapter(pendAdapter);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				mContext);

		alertDialogBuilder.setView(promptsView)
				.setPositiveButton("Approve",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

							}
						})
				.setNeutralButton("Delete",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();

							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();

							}
						});

		SetTypeSelection(spinnerDept, listCaseType, commentTypeID);
		SetAreaSelection(spinnerArea, listCaseReasons, areaID);
		SetSeveritySelection(spinnerSeverity, listSeverity, severityID);
		etComments.setText(comments);
		etCommentTitle.setText(commentTitle);

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}


	public void SetTypeSelection(Spinner spinner, ArrayList<CaseType> array, int commentTypeID) {
		for(int i=0;i<array.size();i++) {
			if(array.get(i).getCommentTypeID() == commentTypeID) {
				spinner.setSelection(i);
				break;
			}
		}
	}

	public void SetAreaSelection(Spinner spinner, ArrayList<CaseArea> array, int areaID) {
		for(int i=0;i<array.size();i++) {
			if(array.get(i).getCaseReasonID() == areaID) {
				spinner.setSelection(i);
				break;
			}
		}
	}

	public void SetSeveritySelection(Spinner spinner, ArrayList<CaseSeverity> array, int severityID) {
		for(int i=0;i<array.size();i++) {
			if(array.get(i).getCaseSeverityID() == severityID) {
				spinner.setSelection(i);
				break;
			}
		}
	}


	public class DepartmentAsyncCall extends AsyncTask<String, Void, Integer> {

		Context context;
		String response;

		public DepartmentAsyncCall(Context mContext){
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
		protected Integer doInBackground(String... params) {
			HttpClient client = new HttpClient();
			try {
				response = client.get(Constants.URL + "/api/Case/GetCaseTypes?CompanyID=" + params[0] + "&IncludeAll=True&UserID=" + params[1]);
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
			listCaseType = DataParser.getCaseTypes(response);
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == 1) {
				Utilities.ShowDialog("Network Error", Constants.ERROR_CONNECTION, context);
			} else if (result == 2) {
				Utilities.ShowDialog("Error", Constants.DEFAULT_ERROR_MSG, context);
			} else {
				AreaDropAsyncCallWS2 adac = new AreaDropAsyncCallWS2(context);
				adac.execute(String.valueOf(spm.getInt("CompanyID",0)),String.valueOf(commentTypeID));
			}
		}
	}

	public class PendingAttachmentAsync extends AsyncTask<String, Void, Integer> {

		Context context;
		String response;
		String PendingAttachmentID;

		public PendingAttachmentAsync(Context mContext, String mPendingAttachmentID){
			this.context = mContext;
			this.PendingAttachmentID = mPendingAttachmentID;
		}

		@Override
		protected void onPreExecute() {

			listAttachments.clear();
			super.onPreExecute();
			if(!isCancelled()){
				CustomProgressBar.showProgressBar(context, false);
			}
		}

		@Override
		protected Integer doInBackground(String... params) {
			HttpClient client = new HttpClient();
			try {
				response = client.get(Constants.URL + "/api/Attachment/GetPendingAttachments?CaseID=" + PendingAttachmentID);
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
			listAttachments = DataParser.getPendingAttachments(response);
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == 1) {
				Utilities.ShowDialog("Network Error", Constants.ERROR_CONNECTION, context);
			} else if (result == 2) {
				Utilities.ShowDialog("Error", Constants.DEFAULT_ERROR_MSG, context);
			} else {
				showPopUpForCaseApproval(context);
			}
		}
	}


	private class AreaDropAsyncCallWS2 extends AsyncTask<String, Void, Integer> {

		Context context;
		String response;

		public AreaDropAsyncCallWS2(Context mContext){
			this.context = mContext;
		}

		@Override
		protected void onPreExecute() {
			listCaseReasons.clear();
			super.onPreExecute();
			if(!isCancelled()){
			}
		}

		@Override
		protected Integer doInBackground(String... params) {
			HttpClient client = new HttpClient();
			try {
				response = client.get(Constants.URL + "/api/Area/GetArea?CompanyID=" + params[0] + "&IncludeSelect=True&CommentTypeID=" + params[1]);
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
			listCaseReasons = DataParser.getCaseArea(response);
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == 1) {
				Utilities.ShowDialog("Network Error", Constants.ERROR_CONNECTION, context);
			} else if (result == 2) {
				Utilities.ShowDialog("Error", Constants.DEFAULT_ERROR_MSG, context);
			} else {

				SeverityDropAsyncCallWS2 adac = new SeverityDropAsyncCallWS2(context);
				adac.execute(String.valueOf(spm.getInt("CompanyID",0)));
			}


		}

	}

	private class SeverityDropAsyncCallWS2 extends AsyncTask<String, Void, Integer> {

		Context context;
		String response;

		public SeverityDropAsyncCallWS2(Context mContext){
			this.context = mContext;
		}

		@Override
		protected void onPreExecute() {
			listSeverity.clear();
			super.onPreExecute();
			if(!isCancelled()){
			}
		}

		@Override
		protected Integer doInBackground(String... params) {
			HttpClient client = new HttpClient();
			try {
				response = client.get(Constants.URL + "/api/Severity/GetSeverity?CompanyID=" + params[0] + "&IncludeSelect=True");
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
			listSeverity = DataParser.getCaseSeverity(response);
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == 1) {
				Utilities.ShowDialog("Network Error", Constants.ERROR_CONNECTION, context);
			} else if (result == 2) {
				Utilities.ShowDialog("Error", Constants.DEFAULT_ERROR_MSG, context);
			} else {

				PendingAttachmentAsync paa = new PendingAttachmentAsync(context, tempCommentID);
				paa.execute();
			}

			CustomProgressBar.hideProgressBar();
		}

	}


}
