
package com.support.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.example.appolissupport.R;
import com.support.objects.UserInfo;

import java.util.ArrayList;


public class ClientUserAdapter extends ArrayAdapter<UserInfo> {
	private Context context;
	private ArrayList<UserInfo> listClientUsers;
	private int[] colors = new int[] { R.color.white, R.color.Gray23 };

	public ClientUserAdapter(Context context, ArrayList<UserInfo> list) {
		super(context, R.layout.client_user_item);
		this.context = context;
		this.listClientUsers = list;
	}
	
	@Override
	public int getCount() {
		if(null == listClientUsers){
			return 0;
		}
		
		return listClientUsers.size();
	}
	
	@Override
	public UserInfo getItem(int position) {
		if(null == listClientUsers){
			return null;
		}
		return listClientUsers.get(position);
	}

	public void updateListReciver(ArrayList<UserInfo> list){
		if(null != list){
			this.listClientUsers = new ArrayList<UserInfo>();
			this.listClientUsers = list;
		}
	}
	
	private class ItemDetailHolder{
		TextView UserName;
		TextView Email;
		TextView Phone;
		TextView Admin;
	}
	
	@SuppressLint("InflateParams") 
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ItemDetailHolder itemDetailHolder;


		LayoutParams params = new LayoutParams(
		        LayoutParams.WRAP_CONTENT,      
		        LayoutParams.WRAP_CONTENT
		);
		
		
		if(null == convertView){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.client_user_item, null);
			itemDetailHolder = new ItemDetailHolder();

			itemDetailHolder.UserName = (TextView) convertView.findViewById(R.id.tv_cu_username);
			itemDetailHolder.Email = (TextView) convertView.findViewById(R.id.tv_cu_email);
			itemDetailHolder.Phone = (TextView) convertView.findViewById(R.id.tv_cu_phone);
			itemDetailHolder.Admin = (TextView) convertView.findViewById(R.id.tv_cu_admin);

			convertView.setTag(itemDetailHolder);
		} else {
			itemDetailHolder = (ItemDetailHolder) convertView.getTag();
		}
		UserInfo item = getItem(position);
				itemDetailHolder.UserName.setText(item.getFirstName() + " " + item.getLastName());
				itemDetailHolder.Email.setText(item.getEmail());
		if (item.getPhone().contains("anyType")) {
			itemDetailHolder.Phone.setText("# Unavailable");
		} else {
			itemDetailHolder.Phone.setText(item.getPhone());
		}

		String Admin;

		if(item.isClientAdmin()){
			Admin = "Yes";
		} else {
			Admin = "No";
		}

		if(!item.isActiveInd()){

		} else {

		}

		itemDetailHolder.Admin.setText("Administrator: "+ Admin);



			int colorPos = position % colors.length;
			convertView.setBackgroundResource(colors[colorPos]);

		if(!item.isActiveInd()) {
			convertView.setBackgroundResource(R.color.Red1);
		}

				return convertView;



	}
}
