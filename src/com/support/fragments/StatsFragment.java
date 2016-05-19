package com.support.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.support.custom.CustomProgressBar;
import com.support.network.HttpClient;
import com.support.main.MainActivity;
import com.example.appolissupport.R;
import com.support.objects.CasesByMonth;
import com.support.utilities.Constants;
import com.support.utilities.DataParser;
import com.support.utilities.SharedPreferenceManager;

import java.util.ArrayList;

public class StatsFragment extends Fragment {
	BarGraphSeries<DataPoint> series;
	private ArrayList<CasesByMonth> listCasesByMonth= new ArrayList<>();
	SharedPreferenceManager spm;
	GraphView graph1, graph2, graph3;
	LinearLayout llGraph1, llGraph2, llGraph3;
	public StatsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		MainActivity.FragPageTitle = "Statistics";
		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);
		spm = new SharedPreferenceManager(getActivity());
		AsyncCall as = new AsyncCall(getActivity(), String.valueOf(spm.getInt("ClientID", 0)), "2016");
		as.execute();
		graph1 = (GraphView) rootView.findViewById(R.id.graph);
		llGraph1 = (LinearLayout) rootView.findViewById(R.id.llGraph1);
		graph2 = (GraphView) rootView.findViewById(R.id.graph2);
		llGraph2 = (LinearLayout) rootView.findViewById(R.id.llGraph2);
		graph3 = (GraphView) rootView.findViewById(R.id.graph3);
		llGraph3 = (LinearLayout) rootView.findViewById(R.id.llGraph3);

		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getActivity().getActionBar().setDisplayShowTitleEnabled(true);
		getActivity().getActionBar().setTitle(MainActivity.FragPageTitle);
		return rootView;
	}


	private DataPoint[] CaseByMonth() {
		DataPoint[] values = new DataPoint[12];
		int i = 0;
		for (CasesByMonth cbm : listCasesByMonth) {
			double x = i;
			double y = cbm.getMonthCount();
			DataPoint v = new DataPoint(x, y);
			values[i] = v;
			i++;
		}
		return values;
	}


	private class AsyncCall extends AsyncTask<String, Void, Void> {
		Context context;
		private String ClientID;
		private String Year;
		String response;

		public AsyncCall(Context mContext, String mClientID, String mYear){
			this.context = mContext;
			this.ClientID = mClientID;
			this.Year = mYear;
		}

		@Override
		protected void onPreExecute() {
			listCasesByMonth.clear();
			super.onPreExecute();
			if(!isCancelled()){
				CustomProgressBar.showProgressBar(context, false);
			}
		}

		@Override
		protected Void doInBackground(String... params) {

			HttpClient client = new HttpClient();
			try {
				response = client.get(Constants.URL + "/api/Stats/GetClientByMonth?ClientID=" + ClientID + "&Year=" + Year);
			} catch (Exception e) {
				e.printStackTrace();
			}
			listCasesByMonth = DataParser.getCasesByMonth(response);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			series = new BarGraphSeries<>(CaseByMonth());
			Animation inFromLeft = AnimationUtils.loadAnimation(context, R.anim.slide_left_in);
			graph1.addSeries(series);
			series.setSpacing(50);
			series.setDrawValuesOnTop(true);
			series.setValuesOnTopColor(Color.RED);
			series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
				@Override
				public int get(DataPoint data) {
					return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
				}
			});

			StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph1);
			staticLabelsFormatter.setHorizontalLabels(new String[]{"J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D"});
			graph1.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
			llGraph1.startAnimation(inFromLeft);
			llGraph1.setVisibility(View.VISIBLE);
			CustomProgressBar.hideProgressBar();

			AsyncCall2 as = new AsyncCall2(context, String.valueOf(spm.getInt("ClientID", 0)), "2016");
			as.execute();

		}
	}

	private class AsyncCall2 extends AsyncTask<String, Void, Void> {
		Context context;
		private String ClientID;
		private String Year;
		String response;

		public AsyncCall2(Context mContext, String mClientID, String mYear){
			this.context = mContext;
			this.ClientID = mClientID;
			this.Year = mYear;
		}

		@Override
		protected void onPreExecute() {
			listCasesByMonth.clear();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {

			HttpClient client = new HttpClient();
			try {
				response = client.get(Constants.URL + "/api/Stats/GetClientByMonth?ClientID=" + ClientID + "&Year=" + Year);
			} catch (Exception e) {
				e.printStackTrace();
			}
			listCasesByMonth = DataParser.getCasesByMonth(response);


			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			series = new BarGraphSeries<>(CaseByMonth());
			Animation inFromRight = AnimationUtils.loadAnimation(context, R.anim.slide_right_in);
			graph2.addSeries(series);
			series.setSpacing(50);
			series.setDrawValuesOnTop(true);
			series.setValuesOnTopColor(Color.RED);
			series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
				@Override
				public int get(DataPoint data) {
					return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
				}
			});

			StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph2);
			staticLabelsFormatter.setHorizontalLabels(new String[]{"J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D"});
			graph2.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
			llGraph2.startAnimation(inFromRight);
			llGraph2.setVisibility(View.VISIBLE);

			AsyncCall3 as = new AsyncCall3(context, String.valueOf(spm.getInt("ClientID", 0)), "2016");
			as.execute();

		}
	}

	private class AsyncCall3 extends AsyncTask<String, Void, Void> {
		Context context;
		private String ClientID;
		private String Year;
		String response;

		public AsyncCall3(Context mContext, String mClientID, String mYear){
			this.context = mContext;
			this.ClientID = mClientID;
			this.Year = mYear;
		}

		@Override
		protected void onPreExecute() {
			listCasesByMonth.clear();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {

			HttpClient client = new HttpClient();
			try {
				response = client.get(Constants.URL + "/api/Stats/GetClientByMonth?ClientID=" + ClientID + "&Year=" + Year);
			} catch (Exception e) {
				e.printStackTrace();
			}
			listCasesByMonth = DataParser.getCasesByMonth(response);


			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			series = new BarGraphSeries<>(CaseByMonth());
			Animation inFromLeft = AnimationUtils.loadAnimation(context, R.anim.slide_left_in);
			graph3.addSeries(series);
			series.setSpacing(50);
			series.setDrawValuesOnTop(true);
			series.setValuesOnTopColor(Color.RED);
			series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
				@Override
				public int get(DataPoint data) {
					return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
				}
			});

			StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph3);
			staticLabelsFormatter.setHorizontalLabels(new String[]{"J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D"});
			graph3.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
			llGraph3.startAnimation(inFromLeft);
			llGraph3.setVisibility(View.VISIBLE);
			CustomProgressBar.hideProgressBar();
		}
	}



}