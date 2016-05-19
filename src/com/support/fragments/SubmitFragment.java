package com.support.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.support.adapters.CaseAreaAdapter;
import com.support.adapters.CaseTypeAdapter;
import com.support.adapters.ClientAdapter;
import com.support.adapters.SeverityAdapter;
import com.support.adapters.UserAdapter;
import com.support.custom.CustomProgressBar;
import com.support.network.HttpClient;
import com.support.main.MainActivity;
import com.example.appolissupport.R;
import com.support.objects.CaseArea;
import com.support.objects.CaseSeverity;
import com.support.objects.CaseType;
import com.support.objects.ClientDrop;
import com.support.objects.UserDrop;
import com.support.utilities.Constants;
import com.support.utilities.DataParser;
import com.support.utilities.SharedPreferenceManager;
import com.support.utilities.Utilities;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;

public class SubmitFragment extends Fragment implements OnClickListener, OnItemClickListener {

    private ArrayList<ClientDrop> listClients = new ArrayList<>();
    private ArrayList<UserDrop> listClientUsers = new ArrayList<>();
    private ArrayList<CaseArea> listCaseReasons = new ArrayList<>();
    private ArrayList<CaseSeverity> listSeverity = new ArrayList<>();
    private ArrayList<CaseType> listCaseType = new ArrayList<>();
    public static ArrayList<String> listAttachments = new ArrayList<>();
    public static ListView lvAttachList;
    private static Spinner spinnerClients;
    private static Spinner spinnerClientUsers;
    private static Spinner spinnerSeverity;
    private static Spinner spinnerArea;
    private static Spinner spinnerCaseType;
    public static EditText Subject;
    public static EditText MainComments;
    public static int SpinUserID = 0;
    public static int SpinReasonID = 0;
    public static int SpinClientID = 0;
    public static int SpinCaseTypeID = 0;
    public static int SpinSeverityID = 0;
    private static SharedPreferenceManager spm;
    public static String uniqueID;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	public SubmitFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MainActivity.FragPageTitle = "Create Support Case";
        uniqueID = UUID.randomUUID().toString();
        listAttachments.clear();
        spm = new SharedPreferenceManager(getActivity());
        MainActivity.clientUserID = spm.getInt("UserID",0);
		View rootView = inflater.inflate(R.layout.fragment_submit, container,
				false);

        lvAttachList = (ListView)rootView.findViewById(R.id.lvAttachments);
        lvAttachList.setOnItemClickListener(this);
        TextView tvClient = (TextView) rootView.findViewById(R.id.tvClients);
        TextView tvUsers = (TextView) rootView.findViewById(R.id.tvUsers);
        spinnerClients=(Spinner)rootView.findViewById(R.id.spinnerClients);

        spinnerClientUsers = (Spinner) rootView.findViewById(R.id.spinnerUsers);
        spinnerClientUsers.setEnabled(false);
        spinnerClientUsers.setClickable(false);
        spinnerSeverity = (Spinner) rootView.findViewById(R.id.spSeverity);
        spinnerArea = (Spinner) rootView.findViewById(R.id.spArea);
        spinnerCaseType = (Spinner) rootView.findViewById(R.id.spinnerEnvironment);
        Subject = (EditText)rootView.findViewById(R.id.etSubject);
        MainComments = (EditText)rootView.findViewById(R.id.etResp);
        MainComments.setSelection(0);


        if(spm.getBoolean("IsSupport", false)){
            spinnerClients.setVisibility(View.VISIBLE);
            spinnerClientUsers.setVisibility(View.VISIBLE);
            spinnerSeverity.setVisibility(View.VISIBLE);
            tvClient.setVisibility(View.VISIBLE);
            tvUsers.setVisibility(View.VISIBLE);
        } else {
            spinnerClients.setVisibility(View.GONE);
            spinnerClientUsers.setVisibility(View.GONE);
            tvClient.setVisibility(View.GONE);
            tvUsers.setVisibility(View.GONE);
        }

        spinnerClients.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if (listClients.get(position).getClientName().contains("Select Client")) {
                    spinnerClientUsers.setSelection(0);
                    spinnerClientUsers.setEnabled(false);
                    spinnerCaseType.setEnabled(false);
                    spinnerCaseType.setSelection(0);
                    SpinClientID = 0;
                } else {
                    UserDropAsyncCallWS2 as = new UserDropAsyncCallWS2(getActivity());
                    as.execute(String.valueOf(listClients.get(position).getClientID()));
                    ClientDrop s = (ClientDrop) parentView.getItemAtPosition(position);
                    SpinClientID = s.getClientID();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });


        spinnerClientUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (listClientUsers.get(position).getUsername().contains("Select User")) {
                    SpinUserID = 0;
                    spinnerCaseType.setEnabled(false);
                    spinnerCaseType.setSelection(0);

                } else {
                    UserDrop s = (UserDrop) parentView.getItemAtPosition(position);
                    SpinUserID = s.getUserID();
                    DepartmentAsyncCall dac = new DepartmentAsyncCall(getActivity());
                    dac.execute(String.valueOf(spm.getInt("CompanyID",0)),String.valueOf(SpinUserID));
                    spinnerCaseType.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        spinnerSeverity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (listSeverity.get(position).getCaseSeverityDesc().contains("Select Severity")) {
                    SpinSeverityID = 0;
                } else {
                    CaseSeverity s = (CaseSeverity) parentView.getItemAtPosition(position);
                    SpinSeverityID = s.getCaseSeverityID();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (listCaseReasons.get(position).getReasonDesc().contains("Select Area")) {
                    SpinReasonID = 0;
                } else {
                    CaseArea s = (CaseArea) parentView.getItemAtPosition(position);
                    SpinReasonID = s.getCaseReasonID();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        spinnerCaseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (listCaseType.get(position).getCommentTypeDesc().contains("Select Department")) {
                    SpinCaseTypeID = 0;
                    spinnerArea.setSelection(0);
                    spinnerArea.setEnabled(false);
                } else {
                    CaseType s = (CaseType) parentView.getItemAtPosition(position);
                    SpinCaseTypeID = s.getCommentTypeID();
                    AreaDropAsyncCallWS2 as = new AreaDropAsyncCallWS2(getActivity());
                    as.execute(String.valueOf(spm.getInt("CompanyID",0)),String.valueOf(SpinCaseTypeID));
                    spinnerArea.setEnabled(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActivity().getActionBar().setDisplayShowTitleEnabled(true);
        getActivity().getActionBar().setTitle(MainActivity.FragPageTitle);

        ClientsDropAsyncCall as = new ClientsDropAsyncCall(getActivity());
        as.execute(String.valueOf(spm.getInt("CompanyID", 0)), "False", "-1");
		return rootView;
	}


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private class ClientsDropAsyncCall extends AsyncTask<String, Void, Integer> {

		Context context;
        String response;

		public ClientsDropAsyncCall(Context mContext){
			this.context = mContext;
		}

		@Override
		protected void onPreExecute() {
			listClients.clear();
            listClientUsers.clear();
            listSeverity.clear();
            listCaseType.clear();

			super.onPreExecute();
            if(!isCancelled()){
                CustomProgressBar.showProgressBar(context, false);
            }
		}

		@Override
		protected Integer doInBackground(String... params) {
            HttpClient client = new HttpClient();
            if(MainActivity.isSupport) {
                try {
                    response = client.get(Constants.URL + "/api/Client/GetClientDrop?CompanyID=" + params[0] + "&IncludeAll="+params[1]);
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
                listClients = DataParser.getClientDrop(response);

                try {
                    response = client.get(Constants.URL + "/api/User/GetUserDrop?ClientID=" + params[2]);
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
                listClientUsers = DataParser.getUserDrop(response);
            }

            if(!MainActivity.isSupport) {
                try {
                    response = client.get(Constants.URL + "/api/Case/GetCaseTypes?CompanyID=" + params[0] + "&IncludeAll=True&UserID=" + spm.getInt("UserID",0));
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
            }

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
            CustomProgressBar.hideProgressBar();
            if (result == 1) {
                Utilities.ShowDialog("Network Error", Constants.ERROR_CONNECTION, context);
            } else if (result == 2) {
                Utilities.ShowDialog("Error", Constants.DEFAULT_ERROR_MSG, context);
            } else {
                if (spm.getBoolean("IsSupport", false)) {
                    ClientAdapter spinnerArrayAdapter = new ClientAdapter(getActivity(), listClients);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerClients.setAdapter(spinnerArrayAdapter);

                    UserAdapter spinnerArrayAdapter2 = new UserAdapter(getActivity(), listClientUsers);
                    spinnerArrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerClientUsers.setAdapter(spinnerArrayAdapter2);
                }
                if (MainActivity.isSupport) {
                    CaseType ct = new CaseType(0, "-- Select Department --");
                    listCaseType.add(ct);
                }

                CaseTypeAdapter spinnerArrayAdapter56 = new CaseTypeAdapter(getActivity(), listCaseType);
                spinnerArrayAdapter56.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCaseType.setAdapter(spinnerArrayAdapter56);
                if (MainActivity.isSupport) {
                    spinnerCaseType.setEnabled(false);
                } else {
                    spinnerCaseType.setEnabled(true);
                }

                SeverityAdapter spinnerArrayAdapter3 = new SeverityAdapter(getActivity(), listSeverity);
                spinnerArrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSeverity.setAdapter(spinnerArrayAdapter3);

                CaseArea caseArea = new CaseArea(0, "-- Select Area --");
                listCaseReasons.add(caseArea);
                CaseAreaAdapter spinnerArrayAdapter4 = new CaseAreaAdapter(getActivity(), listCaseReasons);
                spinnerArrayAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerArea.setAdapter(spinnerArrayAdapter4);
                spinnerArea.setEnabled(false);
            }
        }
    }

    private class UserDropAsyncCallWS2 extends AsyncTask<String, Void, Integer> {

        Context context;
        String response;

        public UserDropAsyncCallWS2(Context mContext){
            this.context = mContext;
        }

        @Override
        protected void onPreExecute() {
            listClientUsers.clear();
            super.onPreExecute();
            if(!isCancelled()){
            }
         }

        @Override
        protected Integer doInBackground(String... params) {
            //Create request
           HttpClient client = new HttpClient();
            try {
                response = client.get(Constants.URL + "/api/User/GetUserDrop?ClientID=" + params[0]);
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
            listClientUsers = DataParser.getUserDrop(response);
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                Utilities.ShowDialog("Network Error", Constants.ERROR_CONNECTION, context);
            } else if (result == 2) {
                Utilities.ShowDialog("Error", Constants.DEFAULT_ERROR_MSG, context);
            } else {
                UserAdapter spinnerArrayAdapter = new UserAdapter(getActivity(), listClientUsers);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerClientUsers.setAdapter(spinnerArrayAdapter);
                spinnerClientUsers.setEnabled(true);
                spinnerClientUsers.setClickable(true);
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
                CaseAreaAdapter spinnerArrayAdapter = new CaseAreaAdapter(getActivity(), listCaseReasons);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerArea.setAdapter(spinnerArrayAdapter);
                spinnerArea.setEnabled(true);
                spinnerArea.setClickable(true);
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
                CaseTypeAdapter adapter = new CaseTypeAdapter(context, listCaseType);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCaseType.setAdapter(adapter);
                CustomProgressBar.hideProgressBar();
            }
        }
    }


    public void InsertNewCase(Context mContext, int mUserID, int mCompanyID, int mSeverity, String mCommentTitle, String mComments, int mCommentTypeID ,String mTempID, int mReason) {
        InsertCase mLoadDataTask = new InsertCase(mContext, mUserID, mCompanyID, mSeverity, mCommentTitle, mComments, mCommentTypeID, mTempID, mReason);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mLoadDataTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mLoadDataTask.execute();
        }
    }

    public class InsertCase extends AsyncTask<String, Void, String> {

        Context context;
        String response;
        int userID;
        int companyID;
        int severityID;
        int commentTypeID;
        String commentTitle;
        String comments;
        String tempID;
        int reasonID;


        public InsertCase(Context mContext, int mUserID, int mCompanyID, int mSeverity, String mCommentTitle, String mComments, int mCommentTypeID, String mTempID, int mReason) {
            this.context = mContext;
            this.userID = mUserID;
            this.severityID = mSeverity;
            this.commentTitle = mCommentTitle;
            this.comments = mComments;
            this.tempID = mTempID;
            this.reasonID = mReason;
            this.companyID = mCompanyID;
            this.commentTypeID = mCommentTypeID;
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
                json.put("UserID", String.valueOf(userID));
                json.put("CompanyID", String.valueOf(companyID));
                json.put("Comments", comments);
                json.put("CaseSeverityID", String.valueOf(severityID));
                json.put("CommentTitle", commentTitle);
                json.put("CaseReasonID", String.valueOf(reasonID));
                json.put("CommentTypeID", String.valueOf(commentTypeID));
                json.put("TempCommentID", tempID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            HttpClient client = new HttpClient();
            try {
                if (spm.getBoolean("CaseApproval", false)) {
                    response = client.post(Constants.URL + "/api/PendingCase/Post", json.toString());
                } else {
                    response = client.post(Constants.URL + "/api/Case/PostCase", json.toString());
                }
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
            if (result.contains("Error")) {
                Utilities.ShowDialog("Error", result, context);
            } else {
                if (spm.getBoolean("CaseApproval", false)) {
                    Utilities.ShowDialog("Case Submitted", "Case Successfully Submitted for Approval", context);
                } else {
                    Utilities.ShowDialog("Case Submitted", "Case Successfully Submitted", context);
                }
                spinnerClients.setSelection(0);
                spinnerClientUsers.setSelection(0);
                spinnerClientUsers.setEnabled(false);
                spinnerSeverity.setSelection(0);
                spinnerArea.setSelection(0);
                spinnerCaseType.setSelection(0);
                Subject.setText("");
                MainComments.setText("");
                listAttachments.clear();

                CustomProgressBar.hideProgressBar();
            }
        }
    }

        public class SendEmail extends AsyncTask<String, Void, Void> {

            Context context;
            int caseUserID;
            int caseNumber;
            String severity;
            String commentTitle;
            String comments;

            public SendEmail(Context mContext, int mUserID, int mCaseID, String mSeverity, String mCommentTitle, String mComments) {
                this.context = mContext;
                this.caseUserID = mUserID;
                this.caseNumber = mCaseID;
                this.severity = mSeverity;
                this.commentTitle = mCommentTitle;
                this.comments = mComments;

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (!isCancelled()) {
                    CustomProgressBar.showProgressBar(context, false);
                }
            }

            @Override
            protected Void doInBackground(String... params) {

//            SoapObject request2 = new SoapObject(Constants.NAMESPACE, METHOD_NAME8);
//            //Property which holds input parameters
//            PropertyInfo supportCasesPI2 = new PropertyInfo();
//            supportCasesPI2.setName("iUserID");
//            supportCasesPI2.setValue(caseUserID);
//            supportCasesPI2.setType(Integer.class);
//            request2.addProperty(supportCasesPI2);
//
//            supportCasesPI2 = new PropertyInfo();
//            supportCasesPI2.setName("iCommentID");
//            supportCasesPI2.setValue(returnedCaseID);
//            supportCasesPI2.setType(Integer.class);
//            request2.addProperty(supportCasesPI2);
//
//            supportCasesPI2 = new PropertyInfo();
//            supportCasesPI2.setName("comments");
//            supportCasesPI2.setValue("Issue: " + commentTitle + "\n" + comments + "\n");
//            supportCasesPI2.setType(String.class);
//            request2.addProperty(supportCasesPI2);
//
//            supportCasesPI2 = new PropertyInfo();
//            supportCasesPI2.setName("severity");
//            supportCasesPI2.setValue(severity);
//            supportCasesPI2.setType(String.class);
//            request2.addProperty(supportCasesPI2);
//
//            SoapSerializationEnvelope envelope2 = new SoapSerializationEnvelope(
//                    SoapEnvelope.VER11);
//            envelope2.dotNet = true;
//            //Set output SOAP object
//            envelope2.setOutputSoapObject(request2);
//            //Create HTTP call object
//            HttpTransportSE androidHttpTransport2 = new HttpTransportSE(Constants.URL);
//
//            try {
//                //Invole web service
//                androidHttpTransport2.call(SOAP_ACTION8, envelope2);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }


                return null;
            }


            @Override
            protected void onPostExecute(Void result) {
                CustomProgressBar.hideProgressBar();


            }
        }
    }