package com.support.superadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.appolissupport.R;
import com.support.colorpicker.ColorPickerDialog;
import com.support.dragdrop.DragSortListView;
import com.support.objects.CaseSeverity;
import com.support.superuser.SuperSeverityActivity;

import java.util.ArrayList;
import java.util.Collections;

public class SuperSeverityAdapter extends ArrayAdapter<CaseSeverity> implements  DragSortListView.DragSortListener {
    private Context context;
    private ArrayList<CaseSeverity> listItemInfos;
    public static EditText etSeverity;

    public SuperSeverityAdapter(Context context, ArrayList<CaseSeverity> list) {
        super(context, R.layout.severity_item);
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
    public CaseSeverity getItem(int position) {

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

    private class ItemDetailHolder{
        LinearLayout llSeverity;
        ImageButton imgSeverityColor, deleteColor;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {
        final ItemDetailHolder itemDetailHolder = new ItemDetailHolder();
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row=inflater.inflate(R.layout.severity_item, parent, false);
        etSeverity=(EditText)row.findViewById(R.id.et_super_severity);
        etSeverity.setText(listItemInfos.get(position).getCaseSeverityDesc());
        int width= context.getResources().getDisplayMetrics().widthPixels;
        etSeverity.setMinimumWidth(width-400);
        etSeverity.setMaxWidth(width - 400);
        etSeverity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (listItemInfos.size() > 0) {
                        EditText sever = (EditText) v.findViewById(R.id.et_super_severity);
                        CaseSeverity cs = new CaseSeverity(listItemInfos.get(position).getCaseSeverityID(), sever.getText().toString(), listItemInfos.get(position).getColor());
                        listItemInfos.set(position, cs);
                    }
                }
            }
        });
        itemDetailHolder.llSeverity = (LinearLayout)row.findViewById(R.id.llSeverity);
        itemDetailHolder.llSeverity.setBackgroundColor(Integer.valueOf(listItemInfos.get(position).getColor()));
        itemDetailHolder. imgSeverityColor = (ImageButton)row.findViewById(R.id.btn_super_color);
        itemDetailHolder.deleteColor = (ImageButton)row.findViewById(R.id.btn_super_trash);

        itemDetailHolder.imgSeverityColor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                showColorPickerDialogDemo(position, etSeverity.getText().toString());

            }
        });

        itemDetailHolder.deleteColor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                etSeverity.clearFocus();
                SuperSeverityActivity.listSeverity.remove(position);
                notifyDataSetChanged();
            }
        });
        return row;
    }


    private void showColorPickerDialogDemo(final int position, final String text) {

        int initialColor = Integer.valueOf(listItemInfos.get(position).getColor());

                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(context, initialColor, new ColorPickerDialog.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                int i = 0;
                etSeverity.requestFocus();
                etSeverity.clearFocus();
                CaseSeverity cs = new CaseSeverity(listItemInfos.get(position).getCaseSeverityID(), listItemInfos.get(position).getCaseSeverityDesc(), String.valueOf(color));
                SuperSeverityActivity.listSeverity.set(position, cs);
                notifyDataSetChanged();
            }


        });
        colorPickerDialog.show();

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
