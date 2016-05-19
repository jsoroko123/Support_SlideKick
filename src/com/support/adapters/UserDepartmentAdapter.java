package com.support.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.appolissupport.R;
import com.support.colorpicker.ColorPickerDialog;
import com.support.dragdrop.DragSortListView;
import com.support.objects.CaseSeverity;
import com.support.objects.UserDepartment;
import com.support.superuser.SuperSeverityActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.CheckedInputStream;

public class UserDepartmentAdapter extends ArrayAdapter<UserDepartment> {
    private Context context;
    private ArrayList<UserDepartment> listItemInfos;

    public UserDepartmentAdapter(Context context, ArrayList<UserDepartment> list) {
        super(context, R.layout.user_department_item);
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
    public UserDepartment getItem(int position) {

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
        View row=inflater.inflate(R.layout.user_department_item, parent, false);
        final TextView tvUserDepartment =(TextView)row.findViewById(R.id.tv_user_department);
        tvUserDepartment.setText(listItemInfos.get(position).getCommentTypeDesc());
        final CheckBox chkUserDept = (CheckBox)row.findViewById(R.id.chk_user_department);



        boolean isActive;
        isActive = listItemInfos.get(position).getIsActive() == 1;

        chkUserDept.setChecked(isActive);

        chkUserDept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                listItemInfos.get(position).setIsActive((chkUserDept.isChecked()) ? 1 : 0);
            }
        });

        return row;
    }
}
