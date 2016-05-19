
package com.support.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.appolissupport.R;
import com.support.objects.PendingCases;
import com.support.utilities.ShapeBuilder;

import java.util.ArrayList;


public class PendingCasesAdapter extends ArrayAdapter<PendingCases> {
	private Context context;
	private ArrayList<PendingCases> listItemInfos;


    public PendingCasesAdapter(Context context, ArrayList<PendingCases> list) {
		super(context, R.layout.pending_case_item);
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
	public PendingCases getItem(int position) {
		if(null == listItemInfos){
			return null;
		}
		return listItemInfos.get(position);
	}

	public void updateListReciver(ArrayList<PendingCases> list){
		if(null != list){
			this.listItemInfos = new ArrayList<PendingCases>();
			this.listItemInfos = list;
		}
	}
	
	private class ItemDetailHolder{
		TextView tvUserName;
		TextView tvIssue;
		TextView tvDate;
		TextView tvReason;
		LinearLayout llColor;

	}
	
	@SuppressLint("InflateParams") 
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ItemDetailHolder itemDetailHolder;
		final PendingCases item = getItem(position);
		if (null == convertView) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.pending_case_item, null);
			itemDetailHolder = new ItemDetailHolder();
			itemDetailHolder.tvIssue = (TextView) convertView.findViewById(R.id.tv_pissue);
			itemDetailHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_puser);
			itemDetailHolder.tvDate = (TextView) convertView.findViewById(R.id.tv_pdate);
			itemDetailHolder.tvReason = (TextView) convertView.findViewById(R.id.tv_preason);
			itemDetailHolder.llColor = (LinearLayout) convertView.findViewById(R.id.llColor);
			convertView.setTag(itemDetailHolder);
		} else {
			itemDetailHolder = (ItemDetailHolder) convertView.getTag();
		}

		if (null != item) {
			itemDetailHolder.tvIssue.setText(item.getCommentTitle());
			itemDetailHolder.tvUserName.setText(item.getUsername());
			String[] date = item.getDateCreated().split("T");
			itemDetailHolder.tvDate.setText(date[0].toString());
			itemDetailHolder.tvReason.setText("Area: "+ item.getReasonDesc());
		}

		itemDetailHolder.llColor.setBackground(ShapeBuilder.buildSelectorShapeFromColors(item.getColor(), "-1", "-1",
				item.getColor(), item.getColor(), item.getColor(), 4, 20));

		return convertView;
	}
}

