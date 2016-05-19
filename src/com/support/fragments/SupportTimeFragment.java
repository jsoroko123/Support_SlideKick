package com.support.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.appolissupport.R;
import com.support.adapters.SupportTimeAdapter;
import com.support.custom.CustomProgressBar;
import com.support.main.MainActivity;
import com.support.network.HttpClient;
import com.support.objects.SupportTime;
import com.support.utilities.Constants;
import com.support.utilities.DataParser;
import com.support.utilities.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SupportTimeFragment extends Fragment implements OnItemClickListener, OnClickListener {
	private static ListView lvSupportTime;
	private static ArrayList<SupportTime> listSupportTime = new ArrayList<>();
	private String totalTime = "";
	private SharedPreferenceManager spm;
	private static TextView etTime;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			MainActivity.FragPageTitle = "Time Log";

		View rootView = inflater.inflate(R.layout.fragment_support_time, container,
				false);
		spm = new SharedPreferenceManager(getActivity());

		lvSupportTime = (ListView) rootView.findViewById(R.id.lv_support_time);
		etTime = (TextView) rootView.findViewById(R.id.tvTimeTotal);

		String clientID;
		String timePeriod;
		String orderBy;

		if(spm.getInt("TimeClientID", 0) != 0){
			clientID = String.valueOf(spm.getInt("TimeClientID", 0));
		} else {
			clientID = "0";
		}

		if(spm.getInt("TimeOrderByPosition", 0) != 0){
			orderBy = MainActivity.ORDER_BY_ARRAY[spm.getInt("TimeOrderByPosition", 0)].replace(" ","").replace("ASC"," ASC").replace("DESC"," DESC").replace("CaseNumber", "c.CommentID");
		} else {
			orderBy = "BeginTime";
		}

		if(spm.getInt("TimePeriodPosition", 0) != 0){
			timePeriod = MainActivity.TIME_ARRAY[spm.getInt("TimePeriodPosition", 0)];
		} else {
			timePeriod = "Today";
		}


		AsyncCallWS2 as = new AsyncCallWS2(getActivity(), String.valueOf(spm.getInt("CompanyID", 0)), String.valueOf(spm.getInt("UserID", 0)), clientID, orderBy, timePeriod);
		as.execute();


//		new ArrayList<String>(Arrays.asList(timeArray));


		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActivity().getActionBar().setDisplayShowTitleEnabled(true);
        getActivity().getActionBar().setTitle(MainActivity.FragPageTitle);
		return rootView;
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}



	@Override
	public void onClick(View v) {

	}

	public void RefreshTime(Context ctx, String CompanyID, String UserID, String ClientID, String Order, String TimeSpan){
		AsyncCallWS2 as = new AsyncCallWS2(ctx, CompanyID, UserID, ClientID, Order, TimeSpan);
		as.execute();
	}

	private class AsyncCallWS2 extends AsyncTask<String, Void, Void> {
		Context context;
		private String CompanyID;
		private String UserID;
		private String ClientID;
		private String OrderBy;
		private String TimeSpan;
		String response;

		public AsyncCallWS2(Context mContext, String mCompanyID, String mUserID, String mClientID, String mOrderBy, String mTimeSpan ){
			this.context = mContext;
			this.CompanyID = mCompanyID;
			this.UserID = mUserID;
			this.ClientID = mClientID;
			this.OrderBy = mOrderBy;
			this.TimeSpan = mTimeSpan;
		}

		@Override
		protected void onPreExecute() {
			listSupportTime.clear();

			super.onPreExecute();
			if(!isCancelled()){
				CustomProgressBar.showProgressBar(context, false);
			}
		}

		@Override
		protected Void doInBackground(String... params) {

			HttpClient client = new HttpClient();
			try {
				response = client.get(Constants.URL + "/api/Time/Get?CompanyID=" + CompanyID + "&UserID=" + UserID +
														"&ClientID=" + ClientID + "&OrderBy=" + OrderBy + "&TimeSpan=" + TimeSpan);
			} catch (Exception e) {
				e.printStackTrace();
			}

			listSupportTime = DataParser.getSupportTime(response);

			int time = 0;
			if(listSupportTime.size()==0){
				totalTime = "Total Time: 0hrs 0mins";
			}
			else {
				for (SupportTime st : listSupportTime) {
					time += st.getDuration();
				}

				int hours = time / 60;
				int minutes = time % 60;
				totalTime = "Total Time: " + hours + "hrs " + minutes + "mins";
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			CustomProgressBar.hideProgressBar();
			SupportTimeAdapter cuAdapter = new SupportTimeAdapter(context,
					listSupportTime);
			lvSupportTime.setAdapter(cuAdapter);
			etTime.setText(totalTime);
			cuAdapter.notifyDataSetChanged();
		}
	}
}