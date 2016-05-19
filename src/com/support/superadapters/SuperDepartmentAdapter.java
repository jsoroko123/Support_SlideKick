package com.support.superadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.appolissupport.R;
import com.support.dragdrop.DragSortListView;
import com.support.objects.CaseType;
import com.support.superuser.SuperDepartmentActivity;

import java.util.ArrayList;
import java.util.Collections;

public class SuperDepartmentAdapter extends ArrayAdapter<CaseType> implements  DragSortListView.DragSortListener {
    private Context context;
    private ArrayList<CaseType> listItemInfos;
    private int[] colors = new int[] { R.color.white, R.color.Gray23 };
    public static EditText etDept;

    public SuperDepartmentAdapter(Context context, ArrayList<CaseType> list) {
        super(context, R.layout.department_item);
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

    public View getCustomView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.department_item, parent, false);
        etDept=(EditText)row.findViewById(R.id.et_super_department);
        etDept.setText(listItemInfos.get(position).getCommentTypeDesc());
        etDept.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(listItemInfos.size() > 0) {
                        EditText dept = (EditText) v.findViewById(R.id.et_super_department);
                        CaseType cs = new CaseType(listItemInfos.get(position).getCommentTypeID(), dept.getText().toString());
                        listItemInfos.set(position, cs);
                    }
                }
            }
        });

        int width= context.getResources().getDisplayMetrics().widthPixels;
        etDept.setMinimumWidth(width-200);
        etDept.setMaxWidth(width - 200);
        ImageButton delete = (ImageButton)row.findViewById(R.id.btn_super_trash);

        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                etDept.clearFocus();
                SuperDepartmentActivity.listDepartments.remove(position);
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
                    Collections.swap(listItemInfos, i, i-1);
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
