package com.pr.se.cash_manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapterFilter extends BaseExpandableListAdapter {
    private List<Filter> filters = new ArrayList<>();
    private Context context;
    private List<String> parentDataSource;
    private HashMap<String, List<Filter>> childDataSource;
    private SharedPreferences prefs = null;

    public ExpandableListAdapterFilter(Context context, HashMap<String, List<Filter>> filterHashMap, List<String> parent ) {
        this.context = context;
        this.parentDataSource = parent;
        this.childDataSource = filterHashMap;
    }

    @Override
    public int getGroupCount() {
        return this.parentDataSource.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.childDataSource.get(this.parentDataSource.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return parentDataSource.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return this.childDataSource.get(parentDataSource.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = getGroup(groupPosition).toString();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.content_filter_list_header, null);
        }

        ImageView img_selection = (ImageView) convertView.findViewById(R.id.content_filter_arrow);
        int imageResourceId = isExpanded ? android.R.drawable.arrow_down_float : android.R.drawable.arrow_up_float;
        img_selection.setImageResource(imageResourceId);

        final TextView lblListHeader = (TextView) convertView.findViewById(R.id.content_filter_list_header_text);

        lblListHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        Filter child = (Filter) getChild(groupPosition, childPosition);
        final String childText = child.getFilter();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.content_filter_list_element, null);
        }

        final TextView txtListChild = (TextView) convertView.findViewById(R.id.content_filter_list_element_text);
        txtListChild.setText(childText);

        CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.content_filter_checkbox);

        if (child.isCheck())
            checkbox.setChecked(true);

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CompoundButton) view).isChecked()){//Wenn ein Feld markiert wird sollen alle anderen Felder entmarkiert werden
                    if (groupPosition != 2) { //Ausgabentyp und Zeitraum
                        List<Filter> childs = childDataSource.get(parentDataSource.get(groupPosition));
                        for (int i = 0; i < childs.size(); i++) {
                            if (i != childPosition) {
                                childs.get(i).setCheck(false);
                            } else {
                                childs.get(i).setCheck(true);
                            }
                            childDataSource.get(parentDataSource.get(groupPosition)).set(i, childs.get(i));
                        }
                    } else { //Bei Kategorien besteht mehrfach Auswahl
                        childDataSource.get(parentDataSource.get(groupPosition)).get(childPosition).setCheck(true);

                        if (childPosition != 0) { //Wenn irgend ein Feld gedrückt wurde -> Alle uncheck
                            childDataSource.get(parentDataSource.get(groupPosition)).get(0).setCheck(false);

                        } else { //Wenn Alle gedrückt wurde -> alle Felder uncheck
                            for (Filter f : childDataSource.get(parentDataSource.get(2))) { //Alle anderen uncheck
                                if (!f.getFilter().equals("Alle"))
                                    f.setCheck(false);
                            }
                        }
                    }

                }else{ // Wenn ein Feld entmarkiert wird soll "Alle" gesetzt werden
                    childDataSource.get(parentDataSource.get(groupPosition)).get(childPosition).setCheck(false);  //Uncheck aktuelles Feld

                    if (groupPosition != 2) { //Ausgabetyp und Zeitraum
                        childDataSource.get(parentDataSource.get(groupPosition)).get(0).setCheck(true);
                    } else {//Kategorien
                        //Wenn alle Felder demarkiert wurden, soll Alle gesetzt werden
                        boolean keinFeldGesetzt = true;
                        for (Filter f : childDataSource.get(parentDataSource.get(2))) {
                            if (f.isCheck())
                                keinFeldGesetzt = false;
                        }
                        if (keinFeldGesetzt) {
                            childDataSource.get(parentDataSource.get(groupPosition)).get(0).setCheck(true);
                        }
                    }
                }
                filters.add(new Filter(context.getString(R.string.filter_Ausgabentyp), false, childDataSource.get("Ausgabentyp")));
                filters.add(new Filter(context.getString(R.string.filter_Zeitraum), false, childDataSource.get("Zeitraum")));
                filters.add(new Filter(context.getString(R.string.filter_kategorien), false, childDataSource.get("Kategorien")));
                RW.writeFilter(context, filters, "filters");

                Intent intent = new Intent(context, FilterActivity.class);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

}
