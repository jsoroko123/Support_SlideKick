package com.support.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.appolissupport.R;
import com.support.objects.SupportTime;
import com.support.objects.SupportUser;
import com.support.utilities.Utilities;

import java.util.ArrayList;


public class SupportTimeAdapter extends ArrayAdapter<SupportTime> {
    private Context context;
    private ArrayList<SupportTime> listItemInfos;
    private int[] colors = new int[] { R.color.white, R.color.Gray23 };

    public SupportTimeAdapter(Context context, ArrayList<SupportTime> list) {
        super(context, R.layout.support_time_item);
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
    public SupportTime getItem(int position) {
        if(null == listItemInfos){
            return null;
        }
        return listItemInfos.get(position);
    }

    public void updateListReciver(ArrayList<SupportTime> list){
        if(null != list){
            this.listItemInfos = new ArrayList<SupportTime>();
            this.listItemInfos = list;
        }
    }

    private class ItemDetailHolder{
      //  TextView tvClientName;
        TextView tvCaseID;
        TextView tvDuration;
        TextView tvStart;
        TextView tvEnd;

    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ItemDetailHolder itemDetailHolder;
        final SupportTime item = getItem(position);
        if (null == convertView) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.support_time_item, null);
            itemDetailHolder = new ItemDetailHolder();
           // itemDetailHolder.tvClientName = (TextView) convertView.findViewById(R.id.tv_clientname);
            itemDetailHolder.tvCaseID = (TextView) convertView.findViewById(R.id.tv_caseID);
            itemDetailHolder.tvDuration = (TextView) convertView.findViewById(R.id.tv_duration);
            itemDetailHolder.tvStart = (TextView) convertView.findViewById(R.id.tv_begin_time);
            itemDetailHolder.tvEnd = (TextView) convertView.findViewById(R.id.tv_end_time);
            convertView.setTag(itemDetailHolder);
        } else {
            itemDetailHolder = (ItemDetailHolder) convertView.getTag();
        }

       // itemDetailHolder.tvClientName.setText(item.getClientName());
        itemDetailHolder.tvCaseID.setText(item.getCommentID()+" - "+item.getClientName());
        itemDetailHolder.tvDuration.setText("Duration: "+item.getDuration()+ " mins");
        itemDetailHolder.tvStart.setText("Start: " + Utilities.parseDate(item.getBeginTime()));
        String time;
        if (item.getEndTime()==null){
            time = "In Process";
        } else {
            time = "End: " + Utilities.parseDate(item.getEndTime());
        }
        itemDetailHolder.tvEnd.setText(time);
        int colorPos = position % colors.length;
        convertView.setBackgroundResource(colors[colorPos]);
        return convertView;
    }


}

