package com.support.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.appolissupport.R;
import com.support.objects.CaseType;
import com.support.objects.ClientDrop;

import java.util.ArrayList;

/**
 * Created by Jeffery on 2/23/2016.
 */
public class CaseTypeAdapter extends ArrayAdapter<CaseType> {
    private Context context;
    private ArrayList<CaseType> listItemInfos;

    public CaseTypeAdapter(Context context, ArrayList<CaseType> list) {
        super(context, R.layout.spinner_item4);
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
    public CaseType getItem(int position) {

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
        View row=inflater.inflate(R.layout.spinner_item4, parent, false);
        TextView label=(TextView)row.findViewById(R.id.text4);
        label.setText(listItemInfos.get(position).getCommentTypeDesc());

        return row;
    }

}

