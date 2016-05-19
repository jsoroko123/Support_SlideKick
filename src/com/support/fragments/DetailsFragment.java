package com.support.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appolissupport.R;
import com.support.adapters.DetailsAdapter;
import com.support.custom.CustomProgressBar;
import com.support.main.MainActivity;
import com.support.network.HttpClient;
import com.support.objects.Details;
import com.support.utilities.Constants;
import com.support.utilities.DataParser;
import com.support.utilities.FileUpload;
import com.support.utilities.SharedPreferenceManager;
import com.support.utilities.Utilities;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class DetailsFragment extends Fragment implements OnClickListener, OnItemClickListener, CompoundButton.OnCheckedChangeListener {
    public static String TAG = "PGGURU";
    public static String caseNumber;
    private static ListView lvDetailsList;
    private ArrayList<Details> listItemInfo = new ArrayList<Details>();
    private ViewGroup hiddenPanel;
    private boolean isUP = true;
    private SharedPreferenceManager spm;
    private static final int SELECT_PHOTO = 100;
    private EditText etResponse;
    private static int currOrientation;
    private static String ClientUserName;
    private static String SupportUserName;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActivity().getActionBar().setDisplayShowTitleEnabled(true);
        getActivity().getActionBar().setTitle(MainActivity.FragPageTitle);
    }

    public DetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_details_fragment, container,
                false);

        hiddenPanel = (ViewGroup) rootView.findViewById(R.id.hidden_panel);

        lvDetailsList = (ListView) rootView.findViewById(R.id.case_details_list2);
        lvDetailsList.setOnItemClickListener(this);

        ImageView img = (ImageView) rootView.findViewById(R.id.img_ex);
        img.setOnClickListener(this);

        etResponse = (EditText) rootView.findViewById(R.id.txt_submit_response);

        TextView tvClientUser = (TextView) rootView.findViewById(R.id.tv_client_user);
        TextView tvSeverity = (TextView) rootView.findViewById(R.id.tv_severity);
        TextView tvStatus = (TextView) rootView.findViewById(R.id.tv_status);
        TextView tvDate = (TextView) rootView.findViewById(R.id.tv_date);


        Button btnSubmit = (Button) rootView.findViewById(R.id.btn_submit_response2);
        btnSubmit.setOnClickListener(this);

        caseNumber = getArguments().getString("CaseID");
        String severity = getArguments().getString("Severity");
        String clientContact = getArguments().getString("ClientContact");
        String date = getArguments().getString("Date");
        SupportUserName = getArguments().getString("SupportUsername");
        ClientUserName = getArguments().getString("Username");


        String status = getArguments().getString("Status");
        tvClientUser.setText(clientContact);
        tvSeverity.setText(severity);
        tvStatus.setText(status);
        tvDate.setText(Utilities.parseDate(date));
        MainActivity.FragPageTitle = "Case " + caseNumber;
        setHasOptionsMenu(true);
        spm = new SharedPreferenceManager(getActivity());
        refresh(getActivity(), caseNumber, true);
        return rootView;
    }

    private static final int FILE_SELECT_CODE = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == SELECT_PHOTO) {
                Uri selectedImage = data.getData();
                currOrientation = Utilities.getOrientation(getActivity(), selectedImage);
                Bitmap bitmap = null;
                try {
                    bitmap = FileUpload.decodeUri(selectedImage, getActivity());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                    bitmap = FileUpload.RotateBitmap(bitmap, currOrientation);


                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                final byte[] bitmapdata = stream.toByteArray();
                UploadFile up = new UploadFile(getActivity(), bitmapdata);
                up.execute();

            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_submit_response2:
                if (!etResponse.getText().toString().isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (Details dets : listItemInfo) {
                        sb.append(dets.getResponse());
                    }

                    InsertResponse inr = new InsertResponse(getActivity(), caseNumber, spm.getInt("UserID", 0), spm.getInt("CompanyID", 0), etResponse.getText().toString());
                    inr.execute();

                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    //Find the currently focused view, so we can grab the correct window token from it.
                    View view = getActivity().getCurrentFocus();
                    //If no view currently has focus, create a new one, just so we can grab a window token from it
                    if (view == null) {
                        view = new View(getActivity());
                    }
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } else {
                    Toast.makeText(getActivity(), "Response Cannot Be Blank.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.img_ex:
                int attachcount = 0;
                if (attachcount > 0) {
                    if (isUP) {
                        isUP = false;
                        Animation bottomUp = AnimationUtils.loadAnimation(getActivity(),
                                R.anim.bottom_up);

                        hiddenPanel.startAnimation(bottomUp);
                        hiddenPanel.setVisibility(View.VISIBLE);
                    } else {
                        isUP = true;
                        Animation bottomUp = AnimationUtils.loadAnimation(getActivity(),
                                R.anim.bottom_down);

                        hiddenPanel.startAnimation(bottomUp);
                        hiddenPanel.setVisibility(View.GONE);
                    }

                } else {
                    Intent pickIntent = new Intent();
                    pickIntent.setType("image/*");
                    pickIntent.setAction(Intent.ACTION_GET_CONTENT);

                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    String pickTitle = "Select Image or Take a Picture"; // Or get from strings.xml
                    Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
                    chooserIntent.putExtra
                            (
                                    Intent.EXTRA_INITIAL_INTENTS,
                                    new Intent[]{takePhotoIntent}
                            );

                    startActivityForResult(chooserIntent, SELECT_PHOTO);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), "Response Cannot Be Blank.", Toast.LENGTH_SHORT).show();

    }

    public void refresh(Context context, String caseNum, boolean ShowProgress) {
        AsyncResponsesCallWS mLoadDataTask = new AsyncResponsesCallWS(context, caseNum, ShowProgress);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mLoadDataTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mLoadDataTask.execute();
        }
    }

    private class InsertResponse extends AsyncTask<String, Void, Integer> {

        Context context;
        String caseNumber;
        int userID;
        int CompanyID;
        String comments;
        String response;

        public InsertResponse(Context mContext, String mCaseNumber, int mUserID, int mCompanyID, String mComments) {
            this.context = mContext;
            this.caseNumber = mCaseNumber;
            this.userID = mUserID;
            this.CompanyID = mCompanyID;
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
        protected Integer doInBackground(String... params) {
            String currentDateandTime = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z").format(new Date());
            JSONObject json = new JSONObject();
            try {
                json.put("UserID", String.valueOf(userID));
                json.put("CommentID", caseNumber);
                json.put("Response", currentDateandTime + "\n" + spm.getString("FullName", "") + " Wrote:\n" + comments);
                json.put("CompanyID", String.valueOf(CompanyID));
            } catch (JSONException e) {
                e.printStackTrace();
                return 2;
            }

            HttpClient client = new HttpClient();
            try {
                response = client.post(Constants.URL + "/api/Details/PostDetails", json.toString());
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
                MainActivity m = new MainActivity();
                try {
                    if (MainActivity.isSupport) {
                        m.sendPush("gcm", ClientUserName, etResponse.getText().toString());
                    } else {

                        m.sendPush("gcm", SupportUserName, etResponse.getText().toString());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                etResponse.setText("");
                refresh(getActivity(), caseNumber, true);


            }
        }
    }

    private class UploadFile extends AsyncTask<String, Void, Integer> {

        Context context;
        byte[] image;

        public UploadFile(Context mContext, byte[] mImage) {
            this.context = mContext;
            this.image = mImage;
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
            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"ic_launcher.png\""),
                                RequestBody.create(MediaType.parse("image/png"), image))
                        .build();

                Request request = new Request.Builder()
                        .header("Authorization", "Basic anNvcm9rbzphYmMxMjM=")

                        .url(Constants.URL + "/api/Upload/UploadFile")
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Looper.prepare();
                        InsertAttachment ia = new InsertAttachment(context, caseNumber, response.body().string().replace("\"", ""), "", spm.getInt("UserID", 0), String.valueOf(currOrientation));
                        ia.execute();
                        Looper.loop();

                    }
                });
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
            if(result == 1){
                Utilities.ShowDialog("Network Error", Constants.ERROR_CONNECTION, context);
            } else if (result == 2){
                Utilities.ShowDialog("Error", Constants.DEFAULT_ERROR_MSG, context);
            }
        }
    }

    private class AsyncResponsesCallWS extends AsyncTask<String, Void, Integer> {

        Context context;
        String caseNum;
        String response;
        boolean showSpinner;

        public AsyncResponsesCallWS(Context mContext, String mCaseNum,boolean mShowSpinner ) {
            this.context = mContext;
            this.caseNum = mCaseNum;
            this.showSpinner = mShowSpinner;

        }

        @Override
        protected void onPreExecute() {
            listItemInfo.clear();
            if(showSpinner) {
                if (!isCancelled()) {
                    CustomProgressBar.showProgressBar(context, false);
                }
            }
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            HttpClient client = new HttpClient();
            try {
                response = client.get(Constants.URL + "/api/Details/GetDetails?CaseID=" + caseNum);
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

            listItemInfo = DataParser.getCaseDetails(response);
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(showSpinner) {
                CustomProgressBar.hideProgressBar();
            }
            if (result == 1) {
                Utilities.ShowDialog("Network Error", Constants.ERROR_CONNECTION, context);
            } else if (result == 2) {
                Utilities.ShowDialog("Error", Constants.DEFAULT_ERROR_MSG, context);
            } else {
                Log.i(TAG, "onPostExecute");
                DetailsAdapter detailsAdapter = new DetailsAdapter(context,
                        listItemInfo);

                lvDetailsList.setAdapter(detailsAdapter);
                detailsAdapter.notifyDataSetChanged();

                lvDetailsList.setSelection(lvDetailsList.getCount() - 1);
            }
        }
    }

    private class InsertAttachment extends AsyncTask<String, Void, Integer> {
        Context context;
        String caseNumber;
        String Path;
        String tempCommentID;
        int userID;
        String orientation;

        String response;

        public InsertAttachment(Context mContext, String mCaseNumber, String mPath, String mTempCommentID, int mUserID, String mOrientation) {
            this.context = mContext;
            this.caseNumber = mCaseNumber;
            this.Path = mPath;
            this.tempCommentID = mTempCommentID;
            this.userID = mUserID;
            this.orientation = mOrientation;

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
            JSONObject json = new JSONObject();
            try {
                json.put("CommentID", caseNumber);
                json.put("Path", Path);
                json.put("tempCommentID", tempCommentID);
                json.put("CompanyID", String.valueOf(spm.getInt("CompanyID", 0)));
                json.put("UserID", String.valueOf(userID));
                json.put("Orientation", orientation);
            } catch (JSONException e) {
                e.printStackTrace();
                return 2;
            }

            HttpClient client = new HttpClient();
            try {
                response = client.post(Constants.URL + "/api/Attachment/PostAttachment", json.toString());
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
                etResponse.setText("");
                MainActivity m = new MainActivity();
                try {
                    if (MainActivity.isSupport) {
                        m.sendPush("gcm", ClientUserName, etResponse.getText().toString());
                    } else {
                        m.sendPush("gcm", SupportUserName, etResponse.getText().toString());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                refresh(getActivity(), caseNumber, true);
            }
        }
    }
}


