package com.support.superuser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.appolissupport.R;
import com.support.adapters.ClientAdapter;
import com.support.adapters.ClientUserAdapter;
import com.support.adapters.ExpandableClientUserAdapter;
import com.support.adapters.UserDepartmentAdapter;
import com.support.custom.CustomProgressBar;
import com.support.network.HttpClient;
import com.support.objects.Client;
import com.support.objects.ClientDrop;
import com.support.objects.UserDepartment;
import com.support.objects.UserInfo;
import com.support.utilities.Constants;
import com.support.utilities.DataParser;
import com.support.utilities.SharedPreferenceManager;
import com.support.utilities.Utilities;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.Spinner;
import android.widget.Switch;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class SuperClientUserActivity extends Activity implements OnClickListener, OnItemClickListener {

    private String updateType;
    private static ExpandableListView lvExpandList;
    private static ExpandableClientUserAdapter expList;

    private ArrayList<UserInfo> listClientUsers = new ArrayList<>();
    private ArrayList<Client> theParentList = new ArrayList<>();
    private ArrayList<Client> parentList = new ArrayList<>();
    private ArrayList<ClientDrop> clientDrop = new ArrayList<>();
    private ArrayList<UserDepartment> listUserDepts = new ArrayList<>();
    private ClientUserAdapter cuAdapter = null;
    private UserDepartmentAdapter userDeptAdapter = null;
    public static SharedPreferenceManager spm;
    private ImageView imgAdd, imgAdd2;
    private RotateAnimation ra;
    private static final float ROTATE_FROM = 0.0f;
    private static final float ROTATE_TO = 359.0f;
    private AsyncCallWSUserClient aswu = null;
    private static String clientSpinner;
    private static int clientSpinnerID;
    private static int NEWCLIENTID;
    private static EditText et6;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_client_user);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        spm = new SharedPreferenceManager(this);
        lvExpandList = (ExpandableListView) findViewById(R.id.lv_expand_client_user);
        lvExpandList.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);
        lvExpandList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    lvExpandList.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        lvExpandList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                Client headerInfo = parentList.get(groupPosition);
                username = headerInfo.getChildList().get(childPosition).getUserName();
                userID = headerInfo.getChildList().get(childPosition).getUserID();
                firstName = headerInfo.getChildList().get(childPosition).getFirstName();
                lastName = headerInfo.getChildList().get(childPosition).getLastName();
                email = headerInfo.getChildList().get(childPosition).getEmail();
                phone = headerInfo.getChildList().get(childPosition).getPhone();
                ext = headerInfo.getChildList().get(childPosition).getExtension();
                cell = headerInfo.getChildList().get(childPosition).getCellPhone();
                password = headerInfo.getChildList().get(childPosition).getUserPassword();
                isAdmin = headerInfo.getChildList().get(childPosition).isClientAdmin();
                isActive = headerInfo.getChildList().get(childPosition).isActiveInd();
                caseApproval = headerInfo.getChildList().get(childPosition).isCaseApproval();

                UserDepartments ud = new UserDepartments(SuperClientUserActivity.this);
                ud.execute(String.valueOf(userID), String.valueOf(spm.getInt("CompanyID", 0)));

                return false;
            }
        });

        lvExpandList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    int itemType = ExpandableListView.getPackedPositionType(id);
                    Client headerInfo = parentList.get(position);
                    String clientName = headerInfo.getClientName();
                    NEWCLIENTID = headerInfo.getClientID();
                    String address = headerInfo.getClientAddress();
                    String city = headerInfo.getClientCity();
                    String state = headerInfo.getClientState();
                    String zip = headerInfo.getClientZip();
                    String email = headerInfo.getClientEmail();
                    String phone = headerInfo.getClientPhone();
                    String password = headerInfo.getPassword();
                    boolean individualPassword = headerInfo.isIndividualPassword();
                    boolean active = headerInfo.isActiveInd();

                    if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                        showPopUpForClientInfo(SuperClientUserActivity.this, clientName, address, city, state, zip, email, phone, password, individualPassword, active, false);
                    }
                return false;
            }
        });


        imgAdd = (ImageView) findViewById(R.id.img_add);
        imgAdd.setOnClickListener(this);
        imgAdd2 = (ImageView) findViewById(R.id.img_add2);
        imgAdd2.setOnClickListener(this);

            aswu = new AsyncCallWSUserClient(this,"0", String.valueOf(spm.getInt("CompanyID", 0)), "False");
            aswu.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_add2:
                ra = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                ra.setDuration(1000);
                imgAdd2.startAnimation(ra);
                ClientNameDrop cnd = new ClientNameDrop(this);
                cnd.execute(String.valueOf(spm.getInt("CompanyID", 0)), "False");
                showPopUpForUpdateType(this);
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        username = cuAdapter.getItem(position).getUserName();
        firstName = cuAdapter.getItem(position).getFirstName();
        lastName = cuAdapter.getItem(position).getLastName();
        email = cuAdapter.getItem(position).getEmail();
        phone = cuAdapter.getItem(position).getPhone();
        ext = cuAdapter.getItem(position).getExtension();
        cell = cuAdapter.getItem(position).getCellPhone();
        password = cuAdapter.getItem(position).getUserPassword();
        userID = cuAdapter.getItem(position).getUserID();
        isAdmin = cuAdapter.getItem(position).isClientAdmin();
        isActive = cuAdapter.getItem(position).isActiveInd();
        caseApproval = cuAdapter.getItem(position).isCaseApproval();

        UserDepartments ud = new UserDepartments(SuperClientUserActivity.this);
        ud.execute(String.valueOf(userID), String.valueOf(spm.getInt("CompanyID",0)));
    }

    private class AsyncCallWSUserClient extends AsyncTask<String, Void, Integer> {

        Context context;
        private String ClientID;
        private String CompanyID;
        private String IsSupport;
        String response;

        public AsyncCallWSUserClient(Context mContext, String mClientID, String mCompanyID, String mIsSupport ){
            this.context = mContext;
            this.ClientID = mClientID;
            this.CompanyID = mCompanyID;
            this.IsSupport = mIsSupport;
        }

        @Override
        protected void onPreExecute() {
            theParentList.clear();
            parentList.clear();
            listClientUsers.clear();
            clientDrop.clear();
            super.onPreExecute();
            if(!isCancelled()){
                CustomProgressBar.showProgressBar(context, false);
            }
        }

        @Override
        protected Integer doInBackground(String... params) {

            HttpClient client = new HttpClient();
            try {
                response = client.get(Constants.URL + "/api/Client/GetClients?CompanyID=" + CompanyID);
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

            theParentList = DataParser.getClients(response);

            try {
                response = client.get(Constants.URL + "/api/User/GetUsersInfo?CompanyID=" + CompanyID + "&ClientID=" + ClientID + "&IsSupport=" + IsSupport);
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

            listClientUsers = DataParser.getUserInfo(response);

            for (int a = 0; a < theParentList.size(); a++) {
                ArrayList<UserInfo> theChildList = new ArrayList<>();
                if (theParentList.get(a).getClientName() != null) {
                    for (int b = 0; b < listClientUsers.size(); b++) {

                        if (listClientUsers.get(b).getClientID() != 0) {
                            if (listClientUsers.get(b).getClientID() == theParentList.get(a).getClientID()) {
                                UserInfo ch = new UserInfo(listClientUsers.get(b).getUserID(), listClientUsers.get(b).getUserName(), listClientUsers.get(b).getLastName(),
                                        listClientUsers.get(b).getFirstName(), listClientUsers.get(b).getEmail(), listClientUsers.get(b).getPhone(), listClientUsers.get(b).getExtension(),
                                        listClientUsers.get(b).getCellPhone(), listClientUsers.get(b).getClientID(), listClientUsers.get(b).isClientAdmin(), listClientUsers.get(b).isActiveInd(),
                                        listClientUsers.get(b).isEmailErrors(), listClientUsers.get(b).isReadAcknowledgement(), listClientUsers.get(b).getUserPassword(),
                                        listClientUsers.get(b).isCaseApproval(), listClientUsers.get(b).isSupportAdmin(), listClientUsers.get(b).isSuperUser());
                                theChildList.add(ch);
                            }
                        }
                    }

                    Client p = new Client(theParentList.get(a).getClientID(), theParentList.get(a).getClientName(), theParentList.get(a).getClientAddress(),
                            theParentList.get(a).getClientCity(), theParentList.get(a).getClientState(), theParentList.get(a).getClientZip(),
                            theParentList.get(a).getClientEmail(), theParentList.get(a).getPassword(), theParentList.get(a).getClientPhone(),
                            theParentList.get(a).isIndividualPassword(), theChildList);
                    parentList.add(p);
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
            } else {
                expList = new ExpandableClientUserAdapter(context, parentList);
                expList.notifyDataSetChanged();
                lvExpandList.setAdapter(expList);
                ClientDrop d = new ClientDrop(0, "Select Client");
                clientDrop.add(0, d);
            }
        }
    }

    public class InsertNewClientUser extends AsyncTask<String, Void, String> {

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


        public InsertNewClientUser(Context mContext, int mClientID, String mUsername, String mFirstName, String mLastName, String mEmail,
                                   String mPhone, String mExtension, String mCellPhone, String mUserPassword, boolean mAdmin,
                                   boolean mActive, boolean mCaseApproval){
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
            if(!isCancelled()){
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
                json.put("SupportAdmin", "False");
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
                    InsertUserDepartment iud = new InsertUserDepartment(SuperClientUserActivity.this, result,String.valueOf(listUserDepts.get(a).getCommentTypeID()));
                    iud.execute();
                } else {
                    DeleteUserDepartment iud = new DeleteUserDepartment(SuperClientUserActivity.this, result,String.valueOf(listUserDepts.get(a).getCommentTypeID()));
                    iud.execute();
                }
            }

                aswu = new AsyncCallWSUserClient(context,"0", String.valueOf(spm.getInt("CompanyID", 0)), "False");
                aswu.execute();
        }
    }

    public class InsertNewClient extends AsyncTask<String, Void, String> {

        Context context;
        int ClientID;
        String clientName;
        int CompanyID;
        String address;
        String city;
        String state;
        String zip;
        String email;
        String phone;

        String response;

        public InsertNewClient(Context mContext, int mClientID, String mClientName, int mCompanyID, String mAddress, String mCity, String mState, String mZip, String mEmail, String mPhone) {
            this.context = mContext;
            this.ClientID = mClientID;
            this.clientName = mClientName;
            this.CompanyID = mCompanyID;
            this.address = mAddress;
            this.city = mCity;
            this.state = mState;
            this.zip = mZip;
            this.email = mEmail;
            this.phone = mPhone;
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
                json.put("ClientID", ClientID);
                json.put("ClientName", clientName);
                json.put("CompanyID", CompanyID);
                json.put("ClientAddress", address);
                json.put("ClientCity", city);
                json.put("ClientState", state);
                json.put("ClientZip", zip);
                json.put("ClientEmail", email);
                json.put("ClientPhone", phone);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            HttpClient client = new HttpClient();
            try {
                response = client.post(Constants.URL + "/api/Client/PostClient", json.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }


            return null;
        }


        @Override
        protected void onPostExecute(String result) {

            CustomProgressBar.hideProgressBar();

                aswu = new AsyncCallWSUserClient(context,"0", String.valueOf(spm.getInt("CompanyID", 0)), "False");
                aswu.execute();

            if(NEWCLIENTID == 0) {
                UserDepartments ud = new UserDepartments(SuperClientUserActivity.this);
                ud.execute("0", String.valueOf(CompanyID));
            }
        }
    }

    public class ClientNameDrop extends AsyncTask<String, Void, Void> {

        Context context;
        String response;

        public ClientNameDrop(Context mContext){
            this.context = mContext;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpClient client = new HttpClient();
            try {
                    response = client.get(Constants.URL + "/api/Client/GetClientDrop?CompanyID=" + params[0] + "&IncludeAll=" + params[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            clientDrop = DataParser.getClientDrop(response);
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {

        }
    }


    public void showPopUpForClientInfo(final Context mContext, String clientName, String address, String city, String state, String zip,
                                       String email, String phone,String password, boolean individualPasswords,  boolean isActive, boolean UserEnabled) {
        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.dialogscanner8, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);
        final EditText et1=(EditText)promptsView.findViewById(R.id.et_client_name);
        final EditText et2=(EditText)promptsView.findViewById(R.id.et_client_address);
        final EditText et3=(EditText)promptsView.findViewById(R.id.et_client_city);
        final EditText et4=(EditText)promptsView.findViewById(R.id.et_client_state);
        final EditText et5=(EditText)promptsView.findViewById(R.id.et_client_zip);
        final EditText et6=(EditText)promptsView.findViewById(R.id.et_client_email);
        final EditText et7=(EditText)promptsView.findViewById(R.id.et_client_phone);
        final EditText et8=(EditText)promptsView.findViewById(R.id.et_client_password);
        final LinearLayout llPassword = (LinearLayout)promptsView.findViewById(R.id.llPassword);


        if(phone.contains("anyType")){
            phone = "";
        }
        if(individualPasswords){
            llPassword.setVisibility(View.GONE);
        }else {
            llPassword.setVisibility(View.VISIBLE);
        }

        et1.setText(clientName);
        et2.setText(address);
        et3.setText(city);
        et4.setText(state);
        et5.setText(zip);
        et6.setText(email);
        et7.setText(phone);
        et8.setText(password);

        et1.setEnabled(UserEnabled);

        alertDialogBuilder.setView(promptsView)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(et1.getText().toString().isEmpty() || et2.getText().toString().isEmpty() || et3.getText().toString().isEmpty() ||
                                        et4.getText().toString().isEmpty() || et5.getText().toString().isEmpty() || et6.getText().toString().isEmpty() ||
                                            et7.getText().toString().isEmpty() || et8.getText().toString().isEmpty()) {
                                    Utilities.ShowDialog("Warning!", "Please fill in required fields.", SuperClientUserActivity.this);
                                } else {

                                    InsertNewClient inc = new InsertNewClient(SuperClientUserActivity.this, NEWCLIENTID, et1.getText().toString(), spm.getInt("CompanyID", 0), et2.getText().toString(),
                                            et3.getText().toString(), et4.getText().toString(), et5.getText().toString(), et6.getText().toString(),
                                            et7.getText().toString());
                                    inc.execute();
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


    public void showPopUpForClientUsers(final Context mContext, String username, String firstName, String lastName, String email,
                                            String phone, String ext, String cell, String password, boolean isAdmin, boolean isActive,
                                                boolean CaseApproval , boolean enableUser, final boolean showClients) {

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
        et6=(EditText)promptsView.findViewById(R.id.etpop_username);
        et6.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(et6.isEnabled()) {
                    if (!hasFocus) {
                        DoesUserExist does = new DoesUserExist(SuperClientUserActivity.this);
                        does.execute(et6.getText().toString());
                    }
                }
            }
        });
        final EditText et7=(EditText)promptsView.findViewById(R.id.etpop_ext);
        final EditText et8=(EditText)promptsView.findViewById(R.id.etpop_cell);
        final Switch s1= (Switch)promptsView.findViewById(R.id.switch1);
        final Switch s2= (Switch)promptsView.findViewById(R.id.switch2);
        final Switch s3= (Switch)promptsView.findViewById(R.id.switch3);
        final ListView lvUserDept = (ListView)promptsView.findViewById(R.id.lvUserDept);
        final ImageButton imgArrow= (ImageButton)promptsView.findViewById(R.id.btn_arrow);

        imgArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(lvUserDept.getVisibility() == View.VISIBLE){
                    lvUserDept.setVisibility(View.GONE);
                } else {
                    lvUserDept.setVisibility(View.VISIBLE);
                }

            }


        });

        final ClientAdapter spinnerArrayAdapter = new ClientAdapter(mContext, clientDrop);
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        sp1.setAdapter(spinnerArrayAdapter);

        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                clientSpinner = clientDrop.get(position).getClientName();
                clientSpinnerID = clientDrop.get(position).getClientID();
                for (int a = 0; a < theParentList.size(); a++) {
                    if(clientSpinner.contains(theParentList.get(a).getClientName())){

                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        if(phone.contains("anyType")){
            phone = "";
        }
        if(password.contains("anyType")){
            password = "";
        }


            if(showClients) {
                sp1.setVisibility(View.VISIBLE);
            } else {
                sp1.setVisibility(View.GONE);
            }


        userDeptAdapter = new UserDepartmentAdapter(this,
                listUserDepts);

        lvUserDept.setAdapter(userDeptAdapter);
        userDeptAdapter.notifyDataSetChanged();

        et1.setText(firstName);
        et2.setText(lastName);
        et3.setText(email);
        et4.setText(phone);
        et5.setText(password);
        et6.setText(username);
        et7.setText(ext);
        et8.setText(cell);
        s1.setChecked(isAdmin);
        s2.setChecked(isActive);
        s3.setChecked(CaseApproval);
        et6.setEnabled(enableUser);

        alertDialogBuilder.setView(promptsView)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                int newClient = 0;
                                if (showClients) {
                                    if (clientSpinner.contains("Select Client")) {
                                        Utilities.ShowDialog("Error", "Please Select Client", mContext);
                                    } else {
                                        newClient = clientSpinnerID;
                                    }
                                } else {
                                    newClient = clientSpinnerID;
                                }

                                if(et1.getText().toString().isEmpty() || et2.getText().toString().isEmpty() || et3.getText().toString().isEmpty() ||
                                        et4.getText().toString().isEmpty() || et5.getText().toString().isEmpty() || et6.getText().toString().isEmpty()) {
                                    Utilities.ShowDialog("Warning!", "Please fill in required fields.", SuperClientUserActivity.this);
                                } else {
                                    InsertNewClientUser incu = new InsertNewClientUser(
                                            SuperClientUserActivity.this, newClient, et6.getText().toString(), et1.getText().toString(),
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

    public void showPopUpForUpdateType(final Context mContext) {
        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.dialogscanner9, null);
        UserDepartments ud = new UserDepartments(SuperClientUserActivity.this);
        ud.execute("0", String.valueOf(spm.getInt("CompanyID", 0)));
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);

        alertDialogBuilder.setTitle("Add Client/User");
        final ListView lv=(ListView)promptsView.findViewById(R.id.list_update);
        ArrayList<String> statuses = new ArrayList<>();
        statuses.add("Client");
        statuses.add("User");

        final ArrayAdapter adapter = new ArrayAdapter(mContext,
                android.R.layout.simple_list_item_activated_1, statuses);
        lv.setAdapter(adapter);




        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Object o = lv.getItemAtPosition(position);
                updateType = o.toString();
            }
        });

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (updateType.equals("Client")) {
                                    NEWCLIENTID = 0;
                                    showPopUpForClientInfo(SuperClientUserActivity.this,"","","","","","","","",false, true, true);
                                } else {

                                    showPopUpForClientUsers(SuperClientUserActivity.this, "", "", "", "", "", "", "", "", true, true, false, true, true);
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
                response = client.get(Constants.URL + "/api/User/GetUserDepartments?UserID=" + params[0]+"&CompanyID=" + params[1]);
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
                showPopUpForClientUsers(context, username, firstName, lastName, email, phone, ext, cell, password, isAdmin, isActive, caseApproval, false, false);
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
