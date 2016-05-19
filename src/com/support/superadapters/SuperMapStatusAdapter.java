package com.support.superadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.appolissupport.R;
import com.support.adapters.CaseStatusAdapter;
import com.support.dragdrop.DragSortListView;
import com.support.objects.CaseStatus;
import com.support.superuser.SuperStatusActivity;

import java.util.ArrayList;
import java.util.Collections;

public class SuperMapStatusAdapter extends ArrayAdapter<CaseStatus> implements  DragSortListView.DragSortListener {
    private Context context;
    private ArrayList<CaseStatus> listItemInfos;
    public static EditText etDept;

    public SuperMapStatusAdapter(Context context, ArrayList<CaseStatus> list) {
        super(context, R.layout.super_status_item);
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
    public CaseStatus getItem(int position) {

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
        View row=inflater.inflate(R.layout.super_status_item, parent, false);
        etDept=(EditText)row.findViewById(R.id.et_super_status);
        etDept.setText(listItemInfos.get(position).getCaseStatusDesc());


        etDept.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (listItemInfos.size() > 0) {
                        EditText area = (EditText) v.findViewById(R.id.et_super_status);
                        CaseStatus cs = new CaseStatus(listItemInfos.get(position).getCaseStatusID(), area.getText().toString(),
                                                            listItemInfos.get(position).getClientCaseStatusID(),listItemInfos.get(position).getClientCaseStatusDesc());
                        listItemInfos.set(position, cs);
                    }
                }
            }
        });


        final Spinner spinner3=(Spinner)row.findViewById(R.id.sp_status);
        CaseStatusAdapter spinnerArrayAdapter3 = new CaseStatusAdapter(context, SuperStatusActivity.listStatuses);
        spinnerArrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(spinnerArrayAdapter3);
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position2, long id) {
                CaseStatus cs = new CaseStatus(listItemInfos.get(position).getCaseStatusID(), listItemInfos.get(position).getCaseStatusDesc(),
                        SuperStatusActivity.listStatuses.get(position2).getClientCaseStatusID(), SuperStatusActivity.listStatuses.get(position2).getClientCaseStatusDesc());
                listItemInfos.set(position, cs);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        for(int i=0;i<listItemInfos.size();i++) {
            for(int k=0;k<SuperStatusActivity.listStatuses.size();k++) {
                if(listItemInfos.get(position).getCaseStatusID() == 0) {
                    spinner3.setSelection(0);
                    break;
                }
                else if (Integer.valueOf(listItemInfos.get(position).getClientCaseStatusID()) == SuperStatusActivity.listStatuses.get(k).getCaseStatusID()) {
                    spinner3.setSelection(k);
                    break;
                }
            }
        }

        ImageButton delete = (ImageButton)row.findViewById(R.id.btn_super_trash);
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                etDept.clearFocus();
                SuperStatusActivity.listSupportStatuses.remove(position);
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
