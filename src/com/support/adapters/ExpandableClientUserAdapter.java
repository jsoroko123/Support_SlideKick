package com.support.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.appolissupport.R;
import com.support.objects.Client;
import com.support.objects.UserInfo;

import java.util.ArrayList;

public class ExpandableClientUserAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Client> parentList;
    private int[] colors = new int[] { R.color.white, R.color.Gray23 };


    public ExpandableClientUserAdapter(Context context, ArrayList<Client> mParentList) {
        this.context = context;
        this.parentList = new ArrayList<>();
        this.parentList.addAll(mParentList);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    public Object getGroup(int groupPosition) {
        return parentList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return parentList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<UserInfo> childList = parentList.get(groupPosition).getChildList();
        return childList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        if (childPosition == 0)
            return 0;

        return getChild(groupPosition, childPosition).hashCode();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View view, ViewGroup parent) {

        UserInfo child = (UserInfo) getChild(groupPosition, childPosition);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.client_user_item, null);

        TextView UserName = (TextView) view.findViewById(R.id.tv_cu_username);
        UserName.setText(child.getFirstName()+" "+child.getLastName()+" ("+child.getUserName()+")");
        TextView Email = (TextView) view.findViewById(R.id.tv_cu_email);
        Email.setText(child.getEmail());
        TextView Phone = (TextView) view.findViewById(R.id.tv_cu_phone);
        Phone.setText(child.getPhone());
        TextView Admin = (TextView) view.findViewById(R.id.tv_cu_admin);
        String AdminText;

        if(child.isClientAdmin()){
            AdminText = "Yes";
        } else {
            AdminText = "No";
        }

        Admin.setText("Administrator: "+ AdminText);

        if(!child.isActiveInd()) {
            view.setBackgroundResource(R.color.Red1);
        }



        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        ArrayList<UserInfo> ErrorList = parentList.get(groupPosition).getChildList();
        return ErrorList.size();

    }


    @Override
    public View getGroupView(final int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {

        final Client parentRow = (Client) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.parent_client_item, null);
        }

        TextView tvCLient = (TextView) view.findViewById(R.id.tvParentlistClientName);
        tvCLient.setText(parentRow.getClientName().trim());
        TextView tvDate = (TextView) view.findViewById(R.id.tvParentlistPhone);
        tvDate.setText(parentRow.getClientPhone().trim());

        int colorPos = groupPosition % colors.length;
        view.setBackgroundResource(colors[colorPos]);
        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }





    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}