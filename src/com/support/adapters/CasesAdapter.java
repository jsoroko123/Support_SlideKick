
package com.support.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.support.main.MainActivity;
import com.example.appolissupport.R;
import com.support.fragments.CasesFragment;
import com.support.objects.CaseStatus;
import com.support.objects.SupportCases;
import com.support.utilities.ShapeBuilder;
import com.support.utilities.Utilities;
import java.util.ArrayList;

public class CasesAdapter extends ArrayAdapter<SupportCases> {
	private Context context;
	private ArrayList<SupportCases> listItemInfos;
	public static int highCase;
	public static int caseID;

	public CasesAdapter(Context context, ArrayList<SupportCases> list) {
		super(context, R.layout.case_item);
		this.context = context;
		this.listItemInfos = list;
	}

    @Override
	public int getCount() {
		if(null == listItemInfos){
			return 0;
		}
		
		return listItemInfos.size();
	}

    @Override
	public SupportCases getItem(int position) {
		if(null == listItemInfos){
			return null;
		}
		return listItemInfos.get(position);
	}


	public void updateListReciver(ArrayList<SupportCases> list){
		if(null != list){
			this.listItemInfos = new ArrayList<SupportCases>();
			this.listItemInfos = list;
		}
	}
	
	private class ItemDetailHolder{
		TextView tvCaseNumber;
		TextView tvStatus;
		TextView tvClient;
		TextView tvIssue;
		TextView tvAssigned, tvassignmentTitle;
		TextView tvSubmitted, tvSubmittedTitle;
		TextView date;
		ImageView ivCircle;
		ImageView ivEx;
		ImageView ivAttach;
		ImageView ivSticky;
		ImageView ivClock;
		Button swipeBtStartTimer, swipeBtStopTimer, swipBtnContact, swipeBtAssign, swipeBtStatus, swipeBTNotes;
	}
	
	@SuppressLint("InflateParams") 
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ItemDetailHolder itemDetailHolder;
		final SupportCases item = getItem(position);
		caseID = Integer.valueOf(item.getCommentID());

		if(null == convertView){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.case_item, null);
			itemDetailHolder = new ItemDetailHolder();
			itemDetailHolder.tvCaseNumber = (TextView) convertView.findViewById(R.id.tv_case_number);
			itemDetailHolder.tvStatus = (TextView) convertView.findViewById(R.id.tv_status);
			itemDetailHolder.tvClient = (TextView) convertView.findViewById(R.id.tv_client);
			itemDetailHolder.tvIssue = (TextView) convertView.findViewById(R.id.tv_issue);
			itemDetailHolder.tvAssigned = (TextView) convertView.findViewById(R.id.tv_assignment);
			itemDetailHolder.tvassignmentTitle = (TextView) convertView.findViewById(R.id.tv_assignment_title);
			itemDetailHolder.tvSubmitted = (TextView) convertView.findViewById(R.id.tv_submittedby);
			itemDetailHolder.tvSubmittedTitle = (TextView) convertView.findViewById(R.id.tv_submittedby_title);
			itemDetailHolder.date = (TextView) convertView.findViewById(R.id.tv_date);
			itemDetailHolder.swipeBtStartTimer = (Button) convertView.findViewById(R.id.swipeBtStart);
			itemDetailHolder.swipeBtStopTimer = (Button) convertView.findViewById(R.id.swipeBtStop);
			itemDetailHolder.swipBtnContact = (Button) convertView.findViewById(R.id.swipeBtContact);
			itemDetailHolder.swipeBtAssign = (Button) convertView.findViewById(R.id.swipeBtAssign);
			itemDetailHolder.swipeBTNotes = (Button) convertView.findViewById(R.id.swipeBtNotes);
			itemDetailHolder.ivCircle = (ImageView) convertView.findViewById(R.id.img_case);
			itemDetailHolder.ivEx = (ImageView) convertView.findViewById(R.id.img_ex);
			itemDetailHolder.ivAttach = (ImageView) convertView.findViewById(R.id.img_attach);
			itemDetailHolder.ivSticky = (ImageView) convertView.findViewById(R.id.img_sticky);
			itemDetailHolder.ivClock = (ImageView) convertView.findViewById(R.id.img_clock);

			itemDetailHolder.swipeBtStatus = (Button) convertView.findViewById(R.id.swipeBtStatus);
			convertView.setTag(itemDetailHolder);
		} else {
			itemDetailHolder = (ItemDetailHolder) convertView.getTag();
		}
		
		itemDetailHolder.swipeBtStartTimer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CasesFragment cf = new CasesFragment();
				cf.InsertTime(context, Integer.valueOf(item.getCommentID()), MainActivity.spm2.getInt("UserID", 0), item.getCaseStatusID());

			}
		});
		itemDetailHolder.swipeBtStopTimer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CasesFragment cf = new CasesFragment();
				cf.UpdateTime(context, Integer.valueOf(item.getCommentID()), MainActivity.spm2.getInt("UserID", 0), item.getCaseStatusID());

			}
		});
		
		itemDetailHolder.swipeBtAssign.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                caseID = Integer.valueOf(item.getCommentID());
				CasesFragment cf = new CasesFragment();
                cf.showPopUpForSupportUsers(getContext());

			}
		});

		itemDetailHolder.swipeBTNotes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CasesFragment cf = new CasesFragment();
				cf.showPopUpForNotes(getContext(),Integer.valueOf(item.getCommentID()));
			}
		});
		
		itemDetailHolder.swipBtnContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(MainActivity.isSupport) {
					ArrayList<String> arr = new ArrayList<>();

					String split = item.getPHONE();
					String[] str = split.split("&");
					String ext = "";

					for (String a : str) {
						String append;
						if (a.length() >= 10) {
							if (a.contains("W")) {
								append = "Office #: ";
								for (String b : str) {
									if (b.contains("X:")) {
										ext = b;
									} else {
										ext = "";
									}
								}
							} else if (a.contains("M")) {
								append = "Mobile #: ";
							} else {
								append = "Company #: ";
							}

							String b = Utilities.stripNonDigits(a);
							if (!b.isEmpty()) {
								arr.add(append + Utilities.fmtPhone(b) + " " + ext);
							}
						}
					}

					CasesFragment cs = new CasesFragment();
					cs.showPopUpForPhoneNumbers(getContext(), arr, item.getUsername());
				}else {
					Intent intent = new Intent("android.intent.action.DIAL");
					intent.setData(Uri.parse("tel: 6123430404"));
					context.startActivity(intent);
				}
			}
		});
		
		itemDetailHolder.swipeBtStatus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				caseID = Integer.valueOf(item.getCommentID());
				int i = 0;
				for(CaseStatus a: MainActivity.statuses) {
					if(a.getCaseStatusID()==-1){
						MainActivity.statuses.remove(i);
						break;
					}
						i++;
				}
					CasesFragment cf = new CasesFragment();
					cf.showPopUpForStatus(getContext());
			}
		});
			
        if(null != item){
				itemDetailHolder.tvCaseNumber.setText(item.getCommentID());
				itemDetailHolder.tvStatus.setText(item.getCaseStatusDesc());

					itemDetailHolder.tvIssue.setText(item.getCommentTitle());
				itemDetailHolder.tvSubmitted.setText(item.getUsername());
				String[] date = item.getDt_created().split("T");
				itemDetailHolder.date.setText(date[0].toString());
				itemDetailHolder.tvAssigned.setText(item.getSupportUserName());

				if(item.getCLIENTREPLIED().contains("GREEN")){
					if(MainActivity.isSupport) {
						itemDetailHolder.ivEx.setVisibility(View.VISIBLE);
					} else {
						itemDetailHolder.ivEx.setVisibility(View.GONE);
					}
				}else{
					if(MainActivity.isSupport) {
						itemDetailHolder.ivEx.setVisibility(View.GONE);
					} else {
						itemDetailHolder.ivEx.setVisibility(View.VISIBLE);
					}
				}

				if(Boolean.valueOf(item.getHasAttachment())){
					itemDetailHolder.ivAttach.setVisibility(View.VISIBLE);
				}else{
					itemDetailHolder.ivAttach.setVisibility(View.GONE);
				}

				if(Boolean.valueOf(item.getHasNotes())){
					itemDetailHolder.ivSticky.setVisibility(View.VISIBLE);
				}else{
					itemDetailHolder.ivSticky.setVisibility(View.GONE);
				}

				if(Boolean.valueOf(item.getWorkingCase())){
					itemDetailHolder.ivClock.setVisibility(View.VISIBLE);
				}else{
					itemDetailHolder.ivClock.setVisibility(View.GONE);
				}

				itemDetailHolder.ivCircle.setImageDrawable(ShapeBuilder.buildSelectorShapeFromColors(item.getColor(), item.getColor(), item.getColor(),
					item.getColor(), item.getColor(), item.getColor(), 1, 90));

				if(Integer.valueOf(item.getCommentID()) > highCase ) {
					MainActivity.spm2.saveInt("HighCase",Integer.valueOf(item.getCommentID()));
					highCase = Integer.valueOf(item.getCommentID());
				}

				if(MainActivity.isSupport){
					itemDetailHolder.swipeBtStartTimer.setVisibility(View.VISIBLE);
					itemDetailHolder.swipeBtStopTimer.setVisibility(View.VISIBLE);
					itemDetailHolder.swipeBtAssign.setVisibility(View.VISIBLE);
					itemDetailHolder.swipBtnContact.setVisibility(View.VISIBLE);
					itemDetailHolder.swipeBTNotes.setVisibility(View.VISIBLE);
					itemDetailHolder.tvAssigned.setVisibility(View.VISIBLE);
					itemDetailHolder.tvassignmentTitle.setVisibility(View.VISIBLE);
					itemDetailHolder.tvSubmitted.setVisibility(View.GONE);
					itemDetailHolder.tvSubmittedTitle.setVisibility(View.GONE);
					itemDetailHolder.tvClient.setText(item.getClientName());
				} else {
					itemDetailHolder.swipeBtStartTimer.setVisibility(View.INVISIBLE);
					itemDetailHolder.swipeBtStopTimer.setVisibility(View.INVISIBLE);
					itemDetailHolder.swipeBtAssign.setVisibility(View.INVISIBLE);
					itemDetailHolder.tvAssigned.setVisibility(View.GONE);
					itemDetailHolder.tvassignmentTitle.setVisibility(View.GONE);
					itemDetailHolder.tvSubmitted.setVisibility(View.VISIBLE);
					itemDetailHolder.tvClient.setText(item.getCommentTypeDesc()+" - "+item.getReasonDesc());
					itemDetailHolder.tvSubmittedTitle.setVisibility(View.VISIBLE);
				}
		}
			return convertView;	
	}
}
