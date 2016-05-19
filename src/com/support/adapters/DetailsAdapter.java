
package com.support.adapters;

import java.io.File;
import java.util.ArrayList;

import com.support.main.MainActivity;
import com.example.appolissupport.R;
import com.support.objects.Details;
import com.support.utilities.FileCache;
import com.support.utilities.FileUpload;
import com.support.utilities.ImageLoader;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class DetailsAdapter extends ArrayAdapter<Details> {
	private Context context;
	private ArrayList<Details> listItemInfos;
	private int  highResponse;
	public ImageLoader imageLoader;
	FileCache fileCache;


	public DetailsAdapter(Context context, ArrayList<Details> list) {
		super(context, R.layout.details_item);
		this.context = context;
		this.listItemInfos = list;
		imageLoader=new ImageLoader(context);
		fileCache=new FileCache(context);
	}
	
	@Override
	public boolean isEnabled(int position) {
	    return false;
	}
	
	@Override
	public int getCount() {
		if(null == listItemInfos){
			return 0;
		}
		
		return listItemInfos.size();
	}
	
	@Override
	public Details getItem(int position) {
		if(null == listItemInfos){
			return null;
		}
		return listItemInfos.get(position);
	}

	public void updateListReciver(ArrayList<Details> list){
		if(null != list){
			this.listItemInfos = new ArrayList<Details>();
			this.listItemInfos = list;
		}
	}
	
	private class ItemDetailHolder{
		TextView subject;
		TextView attach_date;
		TextView attach_name;
		ImageView imgAttach;
		LinearLayout ll_details;
		LinearLayout ll_attach;
	}
	
	@SuppressLint("InflateParams") 
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ItemDetailHolder itemDetailHolder;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		itemDetailHolder = new ItemDetailHolder();
		final Details item = getItem(position);
		if(null != item){

			if(item.getResponse().contains("SupportComment") || (item.getCommentTitle().trim().equals("Attachment") && item.getSupportuserid() > 0)){
					convertView = inflater.inflate(R.layout.details_item, null);
					itemDetailHolder.ll_details = (LinearLayout) convertView.findViewById(R.id.ll_case_detail);
					itemDetailHolder.ll_details.setBackground(context.getResources().getDrawable(R.drawable.appolis_blue));
					itemDetailHolder.subject = (TextView) convertView.findViewById(R.id.tv_res);
					String resp = item.getResponse().replace("<br/>", "\n");
					itemDetailHolder.subject.setText(resp.replaceAll("\\<.*?>", "").replace("||", "").replace("&nbsp;", " ").trim());
					itemDetailHolder.ll_attach = (LinearLayout) convertView.findViewById(R.id.ll_case_attach);
					itemDetailHolder.ll_attach.setBackground(context.getResources().getDrawable(R.drawable.appolis_blue));
					itemDetailHolder.imgAttach = (ImageView) convertView.findViewById(R.id.img_attach);
					if(item.getCommentTitle().trim().equals("Attachment")){
						itemDetailHolder.ll_attach.setVisibility(View.VISIBLE);
						itemDetailHolder.ll_details.setVisibility(View.GONE);

						android.view.ViewGroup.LayoutParams layoutParams = itemDetailHolder.imgAttach.getLayoutParams();

						if (item.getClientAddress().equals("90") || item.getClientAddress().equals("270")) {
							layoutParams.width = 450;
							layoutParams.height = 600;
							itemDetailHolder.imgAttach.setLayoutParams(layoutParams);
						} else {
							layoutParams.width = 600;
							layoutParams.height = 450;
							itemDetailHolder.imgAttach.setLayoutParams(layoutParams);
						}

						imageLoader.DisplayImage("https://support1985.blob.core.windows.net/images/" + item.getResponse(), itemDetailHolder.imgAttach);

						itemDetailHolder.imgAttach.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								File file=fileCache.getFile("https://support1985.blob.core.windows.net/images/" + item.getResponse());
								Uri path = Uri.fromFile(file);
								Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);

								pdfOpenintent.setDataAndType(path, "image/*");
								try {
									pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
									context.startActivity(pdfOpenintent);
								} catch (ActivityNotFoundException e) {

								}

							}
						});
					} else {
						itemDetailHolder.ll_attach.setVisibility(View.GONE);
						itemDetailHolder.ll_details.setVisibility(View.VISIBLE);
					}


				} else if(item.getResponse().contains("ClientComment") || (item.getCommentTitle().trim().equals("Attachment") && item.getUserID() > 0)) {
					convertView = inflater.inflate(R.layout.details_item3, null);
					itemDetailHolder.ll_details = (LinearLayout) convertView.findViewById(R.id.ll_case_detail);
					itemDetailHolder.ll_details.setBackground(context.getResources().getDrawable(R.drawable.appolis_orange));
					itemDetailHolder.subject = (TextView) convertView.findViewById(R.id.tv_res);
					String resp = item.getResponse().replace("<br/>", "\n");
					itemDetailHolder.subject.setText(resp.replaceAll("\\<.*?>", "").replace("||", "").replace("&nbsp;", " ").trim());
					itemDetailHolder.ll_attach = (LinearLayout) convertView.findViewById(R.id.ll_case_attach);
					itemDetailHolder.ll_attach.setBackground(context.getResources().getDrawable(R.drawable.appolis_orange));
					itemDetailHolder.imgAttach = (ImageView) convertView.findViewById(R.id.img_attach);
					if(item.getCommentTitle().trim().equals("Attachment")){
						itemDetailHolder.ll_attach.setVisibility(View.VISIBLE);
						itemDetailHolder.ll_details.setVisibility(View.GONE);
						android.view.ViewGroup.LayoutParams layoutParams = itemDetailHolder.imgAttach.getLayoutParams();

						if (item.getClientAddress().equals("90") || item.getClientAddress().equals("180")) {
							layoutParams.width = 450;
							layoutParams.height = 600;
							itemDetailHolder.imgAttach.setLayoutParams(layoutParams);
						} else {
							layoutParams.width = 600;
							layoutParams.height = 450;
							itemDetailHolder.imgAttach.setLayoutParams(layoutParams);
						}

						imageLoader.DisplayImage("https://support1985.blob.core.windows.net/images/" + item.getResponse(), itemDetailHolder.imgAttach);

						itemDetailHolder.imgAttach.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								File file=fileCache.getFile("https://support1985.blob.core.windows.net/images/" + item.getResponse());
								Uri path = Uri.fromFile(file);
								Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
								pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
								pdfOpenintent.setDataAndType(path, "image/*");
								try {
									context.startActivity(pdfOpenintent);
								} catch (ActivityNotFoundException e) {

								}
							}
						});
					} else {
						itemDetailHolder.ll_attach.setVisibility(View.GONE);
						itemDetailHolder.ll_details.setVisibility(View.VISIBLE);
					}

			} else if(item.getResponse().contains("MainComment")) {
				convertView = inflater.inflate(R.layout.details_item4, null);
				itemDetailHolder.ll_details = (LinearLayout) convertView.findViewById(R.id.ll_case_detail);
				itemDetailHolder.ll_details.setBackground(context.getResources().getDrawable(R.drawable.appolis_gray2));
				itemDetailHolder.subject = (TextView) convertView.findViewById(R.id.tv_res);
				String resp = item.getResponse().replace("<br/>", "\n");
				itemDetailHolder.subject.setText(resp.replaceAll("\\<.*?>", "").replace("||", "").replace("&nbsp;", " ").trim());
			}
		}

		if(item.getResponseID() >= highResponse) {
			MainActivity.spm2.saveInt("HighResponse", Integer.valueOf(item.getResponseID()));
		}

		highResponse = Integer.valueOf(item.getResponseID());
			
			return convertView;	
	}



}
