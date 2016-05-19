
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
import com.support.objects.PendingAttachments;

import java.util.ArrayList;


public class PendingAttachAdapter extends ArrayAdapter<PendingAttachments> {
    private Context context;
    private ArrayList<PendingAttachments> listItemInfos;


    public PendingAttachAdapter(Context context, ArrayList<PendingAttachments> list) {
        super(context, R.layout.attach_item);
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
    public PendingAttachments getItem(int position) {
        if(null == listItemInfos){
            return null;
        }
        return listItemInfos.get(position);
    }


    /**
     * update list
     * @param list
     */
    public void updateListReciver(ArrayList<PendingAttachments> list){
        if(null != list){
            this.listItemInfos = new ArrayList<PendingAttachments>();
            this.listItemInfos = list;
        }
    }

    private class ItemDetailHolder{
        TextView filename;
        TextView date;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ItemDetailHolder itemDetailHolder;
        String response;
        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );


        if(null == convertView){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.attach_item, null);
            itemDetailHolder = new ItemDetailHolder();
            itemDetailHolder.filename = (TextView) convertView.findViewById(R.id.tv_filename);
            convertView.setTag(itemDetailHolder);
        } else {
            itemDetailHolder = (ItemDetailHolder) convertView.getTag();
        }
        PendingAttachments item = getItem(position);

        itemDetailHolder.filename.setText(item.getPath());



        return convertView;
    }
}
