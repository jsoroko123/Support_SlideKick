package com.support.superadapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.appolissupport.R;
import com.support.dragdrop.DragSortListView;
import com.support.objects.CaseArea;
import com.support.objects.CaseType;
import com.support.superuser.SuperAreaActivity;
import com.support.superuser.SuperDepartmentActivity;

import java.util.ArrayList;
import java.util.Collections;

public class SuperAreaAdapter extends ArrayAdapter<CaseArea> implements  DragSortListView.DragSortListener {
    private Context context;
    private ArrayList<CaseArea> listItemInfos;
    public static EditText etDArea;
    private int[] colors = new int[] { R.drawable.background_blue2_button, R.drawable.background_gray_button };

    public SuperAreaAdapter(Context context, ArrayList<CaseArea> list) {
        super(context, R.layout.area_item);
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
    public CaseArea getItem(int position) {

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

    public View getCustomView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row=inflater.inflate(R.layout.area_item, parent, false);
        etDArea=(EditText)row.findViewById(R.id.et_super_area);
        etDArea.setText(listItemInfos.get(position).getReasonDesc());
        etDArea.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (listItemInfos.size() > 0) {
                        EditText area = (EditText) v.findViewById(R.id.et_super_area);
                        CaseArea cs = new CaseArea(listItemInfos.get(position).getCaseReasonID(), area.getText().toString());
                        listItemInfos.set(position, cs);
                    }
                }
            }
        });


        ImageButton delete = (ImageButton)row.findViewById(R.id.btn_super_trash);

        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                etDArea.clearFocus();
                SuperAreaActivity.listAreas.remove(position);
                notifyDataSetChanged();
            }
        });
        return row;
    }


    @Override
    public void drag(int from, int to) {

    }

    @Override
    public void drop(int from, int to) {
        if (from != to) {
            if (from > to) {
                for (int i = from; i > to; --i) {
                    Collections.swap(listItemInfos, i, i - 1);
                }
            } else {
                for (int i = from; i < to; ++i) {
                    Collections.swap(listItemInfos, i, i+1);
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public void remove(int which) {
        notifyDataSetChanged();
    }
}
