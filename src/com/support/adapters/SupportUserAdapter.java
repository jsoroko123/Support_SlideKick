package com.support.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.appolissupport.R;
import com.support.objects.SupportUser;
import com.support.objects.UserDrop;

import java.util.ArrayList;


public class SupportUserAdapter extends ArrayAdapter<SupportUser> {
    private Context context;
    private ArrayList<SupportUser> listItemInfos;

    public SupportUserAdapter(Context context, ArrayList<SupportUser> list) {
        super(context, R.layout.details_item2);
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
    public SupportUser getItem(int position) {
        if(null == listItemInfos){
            return null;
        }
        return listItemInfos.get(position);
    }

    public void updateListReciver(ArrayList<SupportUser> list){
        if(null != list){
            this.listItemInfos = new ArrayList<SupportUser>();
            this.listItemInfos = list;
        }
    }

    private class ItemDetailHolder{
        TextView tvUserName;

    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ItemDetailHolder itemDetailHolder;
        final SupportUser item = getItem(position);
        if (null == convertView) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.details_item2, null);
            itemDetailHolder = new ItemDetailHolder();
            itemDetailHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_supportUser);
            convertView.setTag(itemDetailHolder);
        } else {
            itemDetailHolder = (ItemDetailHolder) convertView.getTag();
        }

        itemDetailHolder.tvUserName.setText(item.getUsername());


        return convertView;
    }


}

