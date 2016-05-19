package com.support.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.appolissupport.R;
import com.support.objects.ClientDrop;
import com.support.objects.User;
import com.support.objects.UserDrop;

import java.util.ArrayList;


public class UserAdapter extends ArrayAdapter<UserDrop> {
    private Context context;
    private ArrayList<UserDrop> listItemInfos;

    public UserAdapter(Context context, ArrayList<UserDrop> list) {
        super(context, R.layout.spinner_item3);
        this.context = context;
        this.listItemInfos = list;
    }

    @Override
    public int getCount() {
        if (null == listItemInfos) {
            return 0;
        }

        return listItemInfos.size();
    }

    @Override
    public UserDrop getItem(int position) {

        return listItemInfos.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater=(LayoutInflater) context.getSystemService(  Context.LAYOUT_INFLATER_SERVICE );
        View row=inflater.inflate(R.layout.spinner_item5, parent, false);
        TextView label=(TextView)row.findViewById(R.id.text5);
        label.setText(listItemInfos.get(position).getUsername());

        return row;
    }

}

