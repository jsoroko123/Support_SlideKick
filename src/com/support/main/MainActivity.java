package com.support.main;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.appolissupport.R;
import com.support.adapters.CaseStatusAdapter;
import com.support.adapters.CaseTypeAdapter;
import com.support.adapters.ClientAdapter;
import com.support.custom.CustomProgressBar;
import com.support.fragments.CasesFragment;
import com.support.fragments.ClientsFragment;
import com.support.fragments.ErrorsFragment;
import com.support.fragments.PendingFragment;
import com.support.fragments.StatsFragment;
import com.support.fragments.SubmitFragment;
import com.support.fragments.SupportTimeFragment;
import com.support.fragments.SupportUserFragment;
import com.support.network.HttpClient;
import com.support.objects.CaseStatus;
import com.support.objects.CaseType;
import com.support.objects.ClientDrop;
import com.support.utilities.Constants;
import com.support.utilities.DataParser;
import com.support.utilities.FileUpload;
import com.support.utilities.SharedPreferenceManager;
import com.support.utilities.Utilities;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends Activity implements FragmentManager.OnBackStackChangedListener,
															NavigationDrawerFragment.NavigationDrawerCallbacks {

	private NavigationDrawerFragment mNavigationDrawerFragment;
	public static String FragPageTitle = "";
	public static ArrayList<CaseStatus> statuses = new ArrayList<>();
	private static ArrayList<ClientDrop> clientDrop = new ArrayList<>();
	public static ArrayList<CaseType> caseTypes = new ArrayList<>();
    public static SharedPreferenceManager spm2;
	public static final int SELECT_PHOTO = 100;
    public static boolean isSupport;
	private static boolean mIsInForegroundMode;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	public static int clientUserID;
	public static boolean userIsInteracting = false;
	public static int clientList;
    private Boolean loaded = false;
	private static int currOrientation;
	private static Context baseContext;
	public static String[] TIME_ARRAY = {"Today","Yesterday","This Week","Last Week","This Month","Last Month","This Year","Last Year", "Show All"};
	public static String[] ORDER_BY_ARRAY = {"Begin Time ASC","Begin Time DESC", "End Time ASC", "End Time DESC","Client Name ASC", "Client Name DESC", "Case Number ASC", "Case Number DESC"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		baseContext = getBaseContext();
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		spm2 = new SharedPreferenceManager(this);
		if(LoginActivity.AUTHORIZE == null) {
			String basicAuthHeader = spm2.getString("UserName", "") + ":" + spm2.getString("Password", "");
			try {
				basicAuthHeader = Base64.encodeToString(basicAuthHeader.getBytes("UTF-8"), Base64.NO_WRAP);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			LoginActivity.AUTHORIZE = basicAuthHeader;
		}

		CompanyStatuses cs = new CompanyStatuses(this, false);
		cs.execute(String.valueOf(spm2.getInt("CompanyID", 0)), String.valueOf(spm2.getBoolean("IsSupport", false)));

		ClientNameDrop cd = new ClientNameDrop(this, false);
		cd.execute(String.valueOf(spm2.getInt("CompanyID", 0)), "true");

		Intent intent = getIntent();
		String reset = intent.getStringExtra("Reset");
		if (reset != null){
			if (reset.contains("1")){
				spm2.remove("Search");
				spm2.remove("Status");
				spm2.remove("Client");
				spm2.remove("Type");
				spm2.remove("MyCases");
				spm2.remove("DateRange");
				spm2.remove("DtFromD");
				spm2.remove("DtFromM");
				spm2.remove("DtFromY");
				spm2.remove("DtToD");
				spm2.remove("DtToM");
				spm2.remove("DtToY");
			}
			else{
				intent.removeExtra("Reset");
			}
		}

		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		getFragmentManager().addOnBackStackChangedListener(this);
		isSupport = spm2.getBoolean("IsSupport", false);
		this.loaded=true;
	}

	public static Context getCustomAppContext(){
		return baseContext;
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {

        if (this.loaded) {
            // update the main content by replacing fragments
            Fragment fragment;
            FragmentManager fragmentManager = getFragmentManager(); // For AppCompat use getSupportFragmentManager


            switch (NavigationDrawerFragment.dataList.get(position).getIndex()) {
                default:
                case 0:
                    fragment = new CasesFragment();
                    if (MainActivity.FragPageTitle.matches(".*\\d+.*")) {
                        this.onBackPressed();
                    }
                    break;
                case 1:
                    fragment = new SubmitFragment();
                    if (MainActivity.FragPageTitle.matches(".*\\d+.*")) {
                        this.onBackPressed();
                    }
                    break;
                case 2:
                    fragment = new ErrorsFragment();
                    if (MainActivity.FragPageTitle.matches(".*\\d+.*")) {
                        this.onBackPressed();
                    }
                    break;
                case 3:
                    fragment = new ClientsFragment();
                    if (MainActivity.FragPageTitle.matches(".*\\d+.*")) {
                        this.onBackPressed();
                    }
                    break;
				case 4:
					fragment = new SupportUserFragment();
					if (MainActivity.FragPageTitle.matches(".*\\d+.*")) {
						this.onBackPressed();
					}
					break;
                case 5:
                    fragment = new PendingFragment();
                    if (MainActivity.FragPageTitle.matches(".*\\d+.*")) {
                        this.onBackPressed();
                    }
                    break;
                case 6:
				fragment = new StatsFragment();
                if (MainActivity.FragPageTitle.matches(".*\\d+.*")) {
                    this.onBackPressed();
                }
                	break;
				case 7:
				fragment = new SupportTimeFragment();
				if (MainActivity.FragPageTitle.matches(".*\\d+.*")) {
					this.onBackPressed();
				}

            }
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }

        else {
            Fragment fragment = new CasesFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			MenuItem item = menu.findItem(R.id.action_example);
			MenuItem item2 = menu.findItem(R.id.action_attach);
			MenuItem item3 = menu.findItem(R.id.action_send);
			MenuItem item4 = menu.findItem(R.id.action_logout);
			MenuItem item5 = menu.findItem(R.id.action_settings);
			if(MainActivity.FragPageTitle.equals("Support Cases")) {
				item.setVisible(true);
				item2.setVisible(false);
				item3.setVisible(false);
				item4.setVisible(true);
				item5.setVisible(true);
			} else if(MainActivity.FragPageTitle.equals("Create Support Case")) {
				item.setVisible(false);
				item2.setVisible(true);
				item3.setVisible(true);
				item4.setVisible(true);
				item5.setVisible(true);
			} else if(MainActivity.FragPageTitle.equals("Time Log")) {
				item.setVisible(true);
				item2.setVisible(false);
				item3.setVisible(false);
				item4.setVisible(false);
				item5.setVisible(true);
			} else {
				item.setVisible(false);
				item2.setVisible(false);
				item3.setVisible(false);
				item4.setVisible(true);
				item5.setVisible(true);
			}
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
			return true;
		} else if (id == R.id.action_logout) {
			SharedPreferenceManager spm = new SharedPreferenceManager(MainActivity.this);
			spm.clearAll();
			Intent mainScreenIntent = new Intent(getApplicationContext(),LoginActivity.class);
			startActivity(mainScreenIntent);
			finish();
			return true;
		} else if (id == R.id.action_example) {
			if(MainActivity.FragPageTitle.equals("Support Cases")) {
				RefreshData(this, String.valueOf(spm2.getInt("CompanyID", 0)), String.valueOf(spm2.getInt("UserID", 0)));
			} else {
				showPopUpForTimeFilter(this);
			}
		} else if (id == R.id.action_send) {
			if(isSupport) {
				if (SubmitFragment.SpinClientID == 0 || SubmitFragment.SpinUserID == 0 || SubmitFragment.SpinSeverityID == 0
						|| SubmitFragment.SpinReasonID == 0 || SubmitFragment.Subject.getText().toString().isEmpty()
							|| SubmitFragment.MainComments.getText().toString().isEmpty()) {
					Toast.makeText(getApplication(), "All information must be filled in.", Toast.LENGTH_LONG).show();
				} else {
					SubmitFragment sf = new SubmitFragment();
					sf.InsertNewCase(this, SubmitFragment.SpinUserID, spm2.getInt("CompanyID", 0), SubmitFragment.SpinSeverityID,
							SubmitFragment.Subject.getText().toString(), SubmitFragment.MainComments.getText().toString(),
								SubmitFragment.SpinCaseTypeID,  SubmitFragment.uniqueID, SubmitFragment.SpinReasonID);
				}
			} else {
				if (SubmitFragment.SpinCaseTypeID == 0 || SubmitFragment.SpinSeverityID == 0 || SubmitFragment.SpinReasonID == 0
							|| SubmitFragment.Subject.getText().toString().isEmpty() || SubmitFragment.MainComments.getText().toString().isEmpty()) {
					Toast.makeText(getApplication(), "All information must be filled in.", Toast.LENGTH_LONG).show();
				} else {
					SubmitFragment sf = new SubmitFragment();
					sf.InsertNewCase(this, MainActivity.clientUserID, spm2.getInt("CompanyID", 0), SubmitFragment.SpinSeverityID,
							SubmitFragment.Subject.getText().toString(), SubmitFragment.MainComments.getText().toString(),
								SubmitFragment.SpinCaseTypeID, SubmitFragment.uniqueID, SubmitFragment.SpinReasonID);
				}
			}

		} else if (id == R.id.action_attach) {
			Intent pickIntent = new Intent();
			pickIntent.setType("image/*");
			pickIntent.setAction(Intent.ACTION_GET_CONTENT);

			Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			String pickTitle = "Select Image or Take a Picture"; // Or get from strings.xml
			Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
			chooserIntent.putExtra
					(
							Intent.EXTRA_INITIAL_INTENTS,
							new Intent[] { takePhotoIntent }
					);

			startActivityForResult(chooserIntent, SELECT_PHOTO);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode == RESULT_OK) {
			if (requestCode == SELECT_PHOTO){
				Uri selectedImage = data.getData();
				currOrientation = Utilities.getOrientation(this, selectedImage);
				Bitmap bitmap = null;
				try {
					bitmap = FileUpload.decodeUri(selectedImage, this);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				if (selectedImage.getPath().contains("external")) {
					bitmap = FileUpload.RotateBitmap(bitmap, currOrientation);
				}

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
				final byte[] bitmapdata = stream.toByteArray();
				UploadFile up = new UploadFile(this, bitmapdata);
				up.execute();
			}
		}
	}
	
	public void showPopUpForFilter(Context mContext) {
		LayoutInflater li = LayoutInflater.from(mContext);
		View promptsView = li.inflate(R.layout.dialogscanner, null);
		final LinearLayout llPanel1=(LinearLayout)promptsView.findViewById(R.id.ll1);
		final LinearLayout llPanel2=(LinearLayout)promptsView.findViewById(R.id.ll2);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				mContext);


		final Spinner spinner=(Spinner)promptsView.findViewById(R.id.spinner1);
		CaseStatusAdapter adapter = new CaseStatusAdapter (this, statuses);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	    final Spinner spinner2=(Spinner)promptsView.findViewById(R.id.spinner2);
			final ClientAdapter spinnerArrayAdapter2 = new ClientAdapter(this, clientDrop);
	    spinnerArrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item );
	    spinner2.setAdapter(spinnerArrayAdapter2);

		final Spinner spinner3=(Spinner)promptsView.findViewById(R.id.spinner3);
	    CaseTypeAdapter spinnerArrayAdapter3 = new CaseTypeAdapter(this, caseTypes);
	    spinnerArrayAdapter3.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinner3.setAdapter(spinnerArrayAdapter3);

		final EditText txt=(EditText)promptsView.findViewById(R.id.etSearch);
		final CheckBox chk1 = (CheckBox) promptsView.findViewById(R.id.checkBox2);
		final LinearLayout ll123 = (LinearLayout)promptsView.findViewById(R.id.ll123);

		if(!spm2.getBoolean("IsSupport", false)){
			ll123.setVisibility(View.GONE);
			chk1.setVisibility(View.GONE);
        }

		txt.addTextChangedListener(new TextWatcher() {

	        @Override
	        public void onTextChanged(CharSequence s, int start, int before,
	                int count) {
	            // TODO Auto-generated method stub
				if(isSupport) {
					if (TextUtils.isEmpty(s.toString().trim())) {

						spinner.setSelection(0);
						spinner.setEnabled(true);
					} else {

						spinner.setSelection(4);
						spinner.setEnabled(false);

					}
				}
	        }

	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count,
	                int after) {
	            // TODO Auto-generated method stub

	        }

	        @Override
	        public void afterTextChanged(Editable s) {

	        }
	    });
		final DatePicker dtFrom=(DatePicker)promptsView.findViewById(R.id.dpResult);
		final DatePicker dtTo=(DatePicker)promptsView.findViewById(R.id.dpResult2);

		final CheckBox[] chk = {(CheckBox) promptsView.findViewById(R.id.checkBox1)};

		if(!spm2.getString("Search", "").isEmpty()){
			txt.setText(spm2.getString("Search", ""));
		} else {
			txt.setText("");
		}

		if(spm2.getInt("Status", 0) != 0){
			SetStatusSelection(spinner, statuses, spm2.getInt("Status", 0));
		} else {
			spinner.setSelection(0);
		}

		if(spm2.getInt("Client", 0) != 0) {
			SetClientSelection(spinner2, clientDrop, spm2.getInt("Client", 0));
		} else {
			spinner2.setSelection(0);
		}

		if(spm2.getInt("Type", 0)!=0){
			SetTypeSelection(spinner3, caseTypes,spm2.getInt("Type", 0) );
		} else {
			spinner3.setSelection(0);
		}

		chk1.setChecked(spm2.getBoolean("MyCases", false));

		chk[0].setChecked(spm2.getBoolean("DateRange", false));

		if (spm2.getBoolean("DateRange", false)) {
			llPanel1.setVisibility(View.VISIBLE);
			llPanel2.setVisibility(View.VISIBLE);

			dtFrom.updateDate(spm2.getInt("DtFromY", 0), spm2.getInt("DtFromM", 0),spm2.getInt("DtFromD", 0));
			dtTo.updateDate(spm2.getInt("DtToY", 0), spm2.getInt("DtToM", 0),spm2.getInt("DtToD", 0));

		} else {
			llPanel1.setVisibility(View.GONE);
			llPanel2.setVisibility(View.GONE);
		}


		chk[0].setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					llPanel1.setVisibility(View.VISIBLE);
					llPanel2.setVisibility(View.VISIBLE);

				} else {
					llPanel1.setVisibility(View.GONE);
					llPanel2.setVisibility(View.GONE);
				}

			}
		});

		alertDialogBuilder.setView(promptsView);
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							if (spm2.getBoolean("IsSupport", false)) {
								if (spinner2.getSelectedItem().toString().equals("All Clients")) {
									clientList = 0;
								} else {
									clientList = clientDrop.get(spinner2.getSelectedItemPosition()).getClientID();
								}
							}
							else {
								clientList = spm2.getInt("ClientID",-1);
							}
							String fDate;
							String tDate;
							String fMonth;
							String fDay;
							String tMonth;
							String tDay;
							fDay = String.valueOf(dtFrom.getDayOfMonth());
							if (fDay.length() < 2) {
								fDay = "0" + fDay;
							}
							fMonth = String.valueOf(dtFrom.getMonth() + 1);
							if (fMonth.length() < 2) {
								fMonth = "0" + fMonth;
							}
							int fYear = dtFrom.getYear();

							tDay = String.valueOf(dtTo.getDayOfMonth());
							if (tDay.length() < 2) {
								tDay = "0" + tDay;
							}
							tMonth = String.valueOf(dtTo.getMonth() + 1);
							if (tMonth.length() < 2) {
								tMonth = "0" + tMonth;
							}
							int tYear = dtTo.getYear();

							if (chk[0].isChecked()) {
								fDate = fYear + "-" + fMonth + "-" + fDay;
								tDate = tYear + "-" + tMonth + "-" + tDay;
							} else {

								fDate = "1900/03/01T10:00:00-5:00";
								tDate = "3001/03/01T10:00:00-5:00";
							}
							int userid;
							if(chk1.isChecked()){
								userid = spm2.getInt("UserID", 0);
								spm2.saveBoolean("MyCases", true);
							} else {
								userid = 0;
								spm2.saveBoolean("MyCases", false);
							}

							spm2.saveString("Search", txt.getText().toString());
							spm2.saveInt("Status", statuses.get(spinner.getSelectedItemPosition()).getCaseStatusID());
							if(isSupport) {
								spm2.saveInt("Client", clientDrop.get(spinner2.getSelectedItemPosition()).getClientID());
							}
							spm2.saveInt("Type", caseTypes.get(spinner3.getSelectedItemPosition()).getCommentTypeID());
							spm2.saveBoolean("MyCases", chk1.isChecked());
							spm2.saveBoolean("DateRange", chk[0].isChecked());
							spm2.saveInt("DtFromD", dtFrom.getDayOfMonth());
							spm2.saveInt("DtFromM", dtFrom.getMonth());
							spm2.saveInt("DtFromY", dtFrom.getYear());
							spm2.saveInt("DtToD", dtTo.getDayOfMonth());
							spm2.saveInt("DtToM", dtTo.getMonth());
							spm2.saveInt("DtToY", dtTo.getYear());
							spm2.saveString("FromDate", fDate);
							spm2.saveString("ToDate", tDate);


							CasesFragment cs = new CasesFragment();

							cs.refreshData(MainActivity.this, statuses.get(spinner.getSelectedItemPosition()).getCaseStatusID(), clientList, caseTypes.get(spinner3.getSelectedItemPosition()).getCommentTypeID(),
									spm2.getInt("CompanyID", 0), txt.getText().toString(), fDate, tDate, userid, false, spm2.getBoolean("IsSupport", false));
							CasesFragment.casesAdapter.notifyDataSetChanged();
						}
					})

			.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					})
				.setNeutralButton("Reset",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								spm2.remove("Search");
								spm2.remove("Status");
								spm2.remove("Client");
								spm2.remove("Type");
								spm2.remove("MyCases");
								spm2.remove("DateRange");
								spm2.remove("DtFromD");
								spm2.remove("DtFromM");
								spm2.remove("DtFromY");
								spm2.remove("DtToD");
								spm2.remove("DtToM");
								spm2.remove("DtToY");

								int clientID;
								if(MainActivity.isSupport){
									clientID = 0;
								} else {
									clientID = spm2.getInt("ClientID", 0);
								}
								CasesFragment cs = new CasesFragment();
								cs.refreshData(MainActivity.this,spm2.getInt("CaseStatusID", 0), clientID, spm2.getInt("CaseTypeID", 0), spm2.getInt("CompanyID", 0), "", "1893-01-01", "2456-07-31", 0, false, spm2.getBoolean("IsSupport", false));
								CasesFragment.casesAdapter.notifyDataSetChanged();
							}
						});


		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}


	public void showPopUpForTimeFilter(final Context mContext) {
		LayoutInflater li = LayoutInflater.from(mContext);
		View promptsView = li.inflate(R.layout.dialogscanner_time, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				mContext);

		final Spinner spinTimePeriod = (Spinner)promptsView.findViewById(R.id.sp_time_period);
		ArrayAdapter<String> adapterTimePeriod = new ArrayAdapter<>(mContext,
				android.R.layout.simple_spinner_item, new ArrayList<>(Arrays.asList(TIME_ARRAY)));
		adapterTimePeriod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinTimePeriod.setAdapter(adapterTimePeriod);

		final Spinner spinnerClients=(Spinner)promptsView.findViewById(R.id.sp_client_time);
		final ClientAdapter spinnerArrayAdapterClientTime = new ClientAdapter(this, clientDrop);
		spinnerArrayAdapterClientTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item );
		spinnerClients.setAdapter(spinnerArrayAdapterClientTime);

		final Spinner spinOrderBy = (Spinner)promptsView.findViewById(R.id.sp_order_time);
		ArrayAdapter<String> adapterOrderBy = new ArrayAdapter<>(mContext,
				android.R.layout.simple_spinner_item, new ArrayList<>(Arrays.asList(ORDER_BY_ARRAY)));
		adapterOrderBy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinOrderBy.setAdapter(adapterOrderBy);

		if(spm2.getInt("TimeClientID", 0) != 0){
			SetClientSelection(spinnerClients, clientDrop, spm2.getInt("TimeClientID", 0));
		} else {
			spinnerClients.setSelection(0);
		}

		if(spm2.getInt("TimeOrderByPosition", 0) != 0){
			spinOrderBy.setSelection(spm2.getInt("TimeOrderByPosition", 0));
		} else {
			spinOrderBy.setSelection(0);
		}

		if(spm2.getInt("TimePeriodPosition", 0) != 0){
			spinTimePeriod.setSelection(spm2.getInt("TimePeriodPosition", 0));
		} else {
			spinTimePeriod.setSelection(0);
		}

		alertDialogBuilder.setView(promptsView);
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								spm2.saveInt("TimeClientID", clientDrop.get(spinnerClients.getSelectedItemPosition()).getClientID());
								spm2.saveInt("TimeOrderByPosition", spinOrderBy.getSelectedItemPosition());
								spm2.saveInt("TimePeriodPosition", spinTimePeriod.getSelectedItemPosition());
								SupportTimeFragment stf = new SupportTimeFragment();
								stf.RefreshTime(mContext, String.valueOf(spm2.getInt("CompanyID", 0)), String.valueOf(spm2.getInt("UserID", 0)),
															String.valueOf(clientDrop.get(spinnerClients.getSelectedItemPosition()).getClientID()),
																ORDER_BY_ARRAY[spinOrderBy.getSelectedItemPosition()].replace(" ","").replace("ASC"," ASC").replace("DESC"," DESC").replace("CaseNumber", "c.CommentID"),
										TIME_ARRAY[spinTimePeriod.getSelectedItemPosition()].replace(" ",""));
							}
						})

				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						})
				.setNeutralButton("Reset",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								spm2.remove("TimeClientID");
								spm2.remove("TimeOrderByPosition");
								spm2.remove("TimePeriodPosition");

								SupportTimeFragment stf = new SupportTimeFragment();
								stf.RefreshTime(mContext, String.valueOf(spm2.getInt("CompanyID", 0)), String.valueOf(spm2.getInt("UserID", 0)),
										"0",
										"Begin Time ASC",
										"Today");

						}
						});


		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}


	@Override
	public void onBackStackChanged() {
		if (!MainActivity.FragPageTitle.matches(".*\\d+.*")) {

			getActionBar().setTitle("Support Cases");
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mIsInForegroundMode = true;
			AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			Intent i = new Intent(this, CaseService.class);
			PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
			am.cancel(pi);
			// by my own convention, minutes <= 0 means notifications are disabled
			if (1 > 0) {
				am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
						SystemClock.elapsedRealtime() + 1 * 30 * 1000,
						1 * 30 * 1000, pi);
			}
		}

	@Override
	protected void onPause() {
		super.onPause();
		mIsInForegroundMode = false;
		//isVisible = false;
	}

	public static boolean isInForeground() {
		return mIsInForegroundMode;
	}

	private Toast toast;
	private long lastBackPressTime = 0;
	@Override
	public void onBackPressed() {
		int count = getFragmentManager().getBackStackEntryCount();
		if (count == 0 || MainActivity.FragPageTitle.contains("Support Cases") || MainActivity.FragPageTitle.contains("Submit")
							|| MainActivity.FragPageTitle.contains("Support Users") || MainActivity.FragPageTitle.contains("Errors")
								|| MainActivity.FragPageTitle.contains("Client List") || MainActivity.FragPageTitle.contains("Pending")){
			if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
				toast = Toast.makeText(this, "Press back again to close application.", Toast.LENGTH_LONG);
				toast.show();
				this.lastBackPressTime = System.currentTimeMillis();
			} else {
				if (toast != null) {
					toast.cancel();
				}
				finish();
			}
		} else {
			getFragmentManager().popBackStack();
			if (MainActivity.FragPageTitle.matches(".*\\d+.*")) {
				setTitle("Support Cases");
				MainActivity.FragPageTitle="Support Cases";
			}
		}
	}

	@Override
	public void onUserInteraction() {
		super.onUserInteraction();
		userIsInteracting = true;
	}


	public void SetClientSelection(Spinner spinner, ArrayList<ClientDrop> array, int clientID) {
		for(int i=0;i<array.size();i++) {
			if(array.get(i).getClientID() == clientID) {
				spinner.setSelection(i);
				break;
			}
		}
	}

	public void SetTypeSelection(Spinner spinner, ArrayList<CaseType> array, int commentTypeID) {
		for(int i=0;i<array.size();i++) {
			if(array.get(i).getCommentTypeID() == commentTypeID) {
				spinner.setSelection(i);
				break;
			}
		}
	}

	public void SetStatusSelection(Spinner spinner, ArrayList<CaseStatus> array, int caseStatusID) {
		for(int i=0;i<array.size();i++) {
			if(array.get(i).getCaseStatusID() == caseStatusID) {
				spinner.setSelection(i);
				break;
			}
		}
	}


	public class CompanyStatuses extends AsyncTask<String, Void, Integer> {

		Context context;
		String response;
		boolean ShowPopup;

		public CompanyStatuses(Context mContext, boolean mShowPopup){
			this.context = mContext;
			this.ShowPopup = mShowPopup;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			HttpClient client = new HttpClient();
			try {
				response = client.get(Constants.URL + "/api/Status/GetStatus?CompanyID=" + params[0] + "&IsSupport=" + params[1]);
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

			statuses = DataParser.getCaseStatuses(response);
			statuses.add(statuses.size(),new CaseStatus(-1, "All"));


			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == 1) {
				Utilities.ShowDialog("Network Error", Constants.ERROR_CONNECTION, context);
				CustomProgressBar.hideProgressBar();
			} else if (result == 2) {
				Utilities.ShowDialog("Error", Constants.DEFAULT_ERROR_MSG, context);
				CustomProgressBar.hideProgressBar();
			} else {
				if(ShowPopup) {
					ClientNameDrop cd = new ClientNameDrop(context, true);
					cd.execute(String.valueOf(spm2.getInt("CompanyID", 0)), "true");
				}
			}
		}
	}

	public class ClientNameDrop extends AsyncTask<String, Void, Integer> {

		Context context;
		String response;
		boolean showPop;

		public ClientNameDrop(Context mContext, boolean showPopUp){
			this.context = mContext;
			this.showPop = showPopUp;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			HttpClient client = new HttpClient();
			try {
				response = client.get(Constants.URL + "/api/Client/GetClientDrop?CompanyID=" + params[0] + "&IncludeAll=" + params[1]);
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
			clientDrop = DataParser.getClientDrop(response);
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == 1) {
				Utilities.ShowDialog("Network Error", Constants.ERROR_CONNECTION, context);
				CustomProgressBar.hideProgressBar();
			} else if (result == 2) {
				Utilities.ShowDialog("Error", Constants.DEFAULT_ERROR_MSG, context);
				CustomProgressBar.hideProgressBar();
			} else if(showPop) {
				showPopUpForFilter(context);
				CustomProgressBar.hideProgressBar();

			}
		}

	}

	public void RefreshData(Context mContext, String mCompanyID, String mUserID){
		CaseTypes ct = new CaseTypes(mContext);
		ct.execute(mCompanyID, mUserID);
	}


	public class CaseTypes extends AsyncTask<String, Void, Integer> {

		Context context;
		String response;

		public CaseTypes(Context mContext){
			this.context = mContext;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CustomProgressBar.showProgressBar(context, false);
		}

		@Override
		protected Integer doInBackground(String... params) {
			HttpClient client = new HttpClient();
			try {
				response = client.get(Constants.URL + "/api/Case/GetCaseTypes?CompanyID=" + params[0] + "&IncludeAll=True&UserID="
																								+ params[1]);
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
			caseTypes = DataParser.getCaseTypes(response);
			CaseType ct = new CaseType(0,"All Departments");
			caseTypes.set(0,ct);
			return 0;
		}


		@Override
		protected void onPostExecute(Integer result) {
			if (result == 1) {
				Utilities.ShowDialog("Network Error", Constants.ERROR_CONNECTION, context);
				CustomProgressBar.hideProgressBar();
			} else if (result == 2) {
				Utilities.ShowDialog("Error", Constants.DEFAULT_ERROR_MSG, context);
				CustomProgressBar.hideProgressBar();
			} else {
				CompanyStatuses cs = new CompanyStatuses(context, true);
				cs.execute(String.valueOf(spm2.getInt("CompanyID", 0)), String.valueOf(spm2.getBoolean("IsSupport", false)));
			}
		}
	}


	public void sendPush(final String pns, final String userTag, final String message)
			throws IOException {
		new AsyncTask<Object, Object, Object>() {
			@Override
			protected Object doInBackground(Object... params) {
				try {

					String uri = Constants.URL + "/api/notifications/PostNotification" +
							"";
					uri += "?pns=" + pns;
					uri += "&to_tag=" + userTag;

					int response = post(uri, "="+message);

					if (response != HttpStatus.SC_OK) {
						throw new RuntimeException("Error sending notification");
					}
				} catch (Exception e) {

					return e;
				}

				return null;
			}
		}.execute(null, null, null);
	}


	public static final MediaType JSON
			= MediaType.parse("application/x-www-form-urlencoded");

	OkHttpClient client = new OkHttpClient();

	public int post(String url, String json) throws IOException {
		RequestBody body = RequestBody.create(JSON, ""+json);
		Request request = new Request.Builder()
				.header("Authorization","Basic "+LoginActivity.AUTHORIZE )
				.url(url)
				.post(body)
				.build();

		Response response = client.newCall(request).execute();
		return response.code();
	}


	private class UploadFile extends AsyncTask<String, Void, String> {

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
		protected String doInBackground(String... params) {
			final String[] response2 = {null};

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
						InsertAttachment ia = new InsertAttachment(context, "-1", response.body().string().replace("\"", ""), SubmitFragment.uniqueID, spm2.getInt("UserID", 0), String.valueOf(currOrientation));
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
					response2[0] = Constants.ERROR_CONNECTION;

				} else {
					response2[0] = Constants.DEFAULT_ERROR_MSG;
				}
			}
			return response2[0];
		}

		@Override
		protected void onPostExecute(String result) {

		}
	}

	private class InsertAttachment extends AsyncTask<String, Void, String> {
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
		protected String doInBackground(String... params) {
			JSONObject json = new JSONObject();


				try {
					if (spm2.getBoolean("CaseApproval", false)) {
						json.put("PendingCommentID", caseNumber);
					} else {
						json.put("CommentID", caseNumber);
					}
					json.put("Path", Path);
					json.put("tempCommentID", tempCommentID);
					json.put("CompanyID", String.valueOf(spm2.getInt("CompanyID", 0)));
					json.put("UserID", String.valueOf(userID));
					json.put("Orientation", orientation);
				} catch (JSONException e) {
					e.printStackTrace();
				}




			HttpClient client = new HttpClient();
			try {
				if (spm2.getBoolean("CaseApproval", false)) {
					response = client.post(Constants.URL + "/api/Attachment/PostPendingAttachment", json.toString());
				} else {
					response = client.post(Constants.URL + "/api/Attachment/PostAttachment", json.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (e instanceof SocketException
						|| e instanceof UnknownHostException
						|| e instanceof SocketTimeoutException
						|| e instanceof ConnectTimeoutException
						|| e instanceof ClientProtocolException) {
					return Constants.ERROR_CONNECTION;

				} else {
					return Constants.DEFAULT_ERROR_MSG;
				}
			}

			return Path;
		}

		@Override
		protected void onPostExecute(String result) {
			if(result.contains("Error")){
				Utilities.ShowDialog("Error", result, context);
			} else {
				CustomProgressBar.hideProgressBar();
				SubmitFragment.listAttachments.add(result);
				ArrayAdapter<String> attachArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, SubmitFragment.listAttachments);
				SubmitFragment.lvAttachList.setAdapter(attachArrayAdapter);
				attachArrayAdapter.notifyDataSetChanged();
			}
		}
	}
}