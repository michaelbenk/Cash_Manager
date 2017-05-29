package com.pr.se.cash_manager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Category> listDataHeader;
    private HashMap<Category, List<Category>> listDataChild;

    public ExpandableListAdapter(Context context, List<Category> listDataHeader, HashMap<Category, List<Category>> listDataChild) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = getChild(groupPosition, childPosition).toString();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.content_categories_list_element, null);
        }

        final TextView txtListChild = (TextView) convertView.findViewById(R.id.content_categories_list_element_text);
        final ImageView delListChild = (ImageView) convertView.findViewById(R.id.content_categories_delete);
        delListChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Category> categories = RW.readCategories(context, "categories");
                Category c;
                for (int i = 0; i < categories.size(); i++) {
                    c = categories.get(i);
                    if (c.getName().equals(listDataHeader.get(groupPosition).getName())) {
                        Category s;
                        for (int j = 0; j < c.getSubCategories().size(); j++) {
                            s = c.getSubCategories().get(j);
                            if (s.getName().equals(listDataHeader.get(groupPosition).getSubCategories().get(j).getName())) {
                                categories.get(i).getSubCategories().remove(j);
                            }
                        }
                    }
                }
                RW.writeCategories(context, categories, "categories");

                Intent intent = new Intent(context, CategoriesActivity.class);
                context.startActivity(intent);
            }
        });

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = getGroup(groupPosition).toString();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.content_categories_list_header, null);
        }

        ImageView img_selection = (ImageView) convertView.findViewById(R.id.content_categories_arrow);
        int imageResourceId = isExpanded ? android.R.drawable.arrow_down_float : android.R.drawable.arrow_up_float;
        img_selection.setImageResource(imageResourceId);

        final TextView lblListHeader = (TextView) convertView.findViewById(R.id.content_categories_list_header_text);
        lblListHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CategoryDetailsActivity.class);
                intent.putExtra("cat", listDataHeader.get(groupPosition).getName());
                context.startActivity(intent);
            }
        });
        final ImageView delListChild = (ImageView) convertView.findViewById(R.id.content_categories_delete);
        delListChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Category> categories = RW.readCategories(context, "categories");
                Category c;
                for (int i = 0; i < categories.size(); i++) {
                    c = categories.get(i);
                    if (c.getName().equals(listDataHeader.get(groupPosition).getName())) {
                        categories.remove(i);
                    }
                }
                RW.writeCategories(context, categories, "categories");

                Intent intent = new Intent(context, CategoriesActivity.class);
                context.startActivity(intent);
                //TODO es sollte nicht möglich sein alle Kategorien zu löschen
                //TODO Die weiteren Folgen von gelöschter Kategorie wurden noch nicht überlegt (Wenn die Kategorie in einem Expense verwendet wurde)
            }
        });

        lblListHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
