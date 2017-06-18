package com.pr.se.cash_manager;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


 class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;

     ExpandableListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return ((Category) this.getGroup(groupPosition)).getSubCategories().get(childPosititon);
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
        final ProgressBar elementprogressbar = (ProgressBar) convertView.findViewById((R.id.content_categories_list_element_progressbar));
        delListChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Category> categories = RW.readCategories(context, "categories");
                Category c;
                for (int i = 0; i < categories.size(); i++) {
                    c = categories.get(i);
                    if (c.getName().equals(categories.get(groupPosition).getName())) {
                        Category s;
                        for (int j = 0; j < c.getSubCategories().size(); j++) {
                            s = c.getSubCategories().get(j);
                            if (s.getName().equals(categories.get(groupPosition).getSubCategories().get(childPosition).getName())) {
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

        if(((Category) getChild(groupPosition, childPosition)).getLimit() > 0){
            elementprogressbar.setVisibility(View.VISIBLE);

        }
        setProgressBar(elementprogressbar, ((Category) getChild(groupPosition, childPosition)));
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return RW.readCategories(this.context, "categories").get(groupPosition).getSubCategories().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return RW.readCategories(this.context, "categories").get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return RW.readCategories(this.context, "categories").size();
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

        final List<Category> categories = RW.readCategories(context, "categories");

        final TextView lblListHeader = (TextView) convertView.findViewById(R.id.content_categories_list_header_text);

        final ProgressBar headerprogressbar = (ProgressBar) convertView.findViewById(R.id.content_categories_list_header_progressbar);

        lblListHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CategoryDetailsActivity.class);
                intent.putExtra("cat", categories.get(groupPosition).getId());
                context.startActivity(intent);
            }
        });
        final ImageView delListChild = (ImageView) convertView.findViewById(R.id.content_categories_delete);
        if (categories.get(groupPosition).getName().equals("Others")) {
            delListChild.setVisibility(View.GONE);
        }
        delListChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category c;
                for (int i = 0; i < categories.size(); i++) {
                    c = categories.get(i);
                    if (c.getName().equals(categories.get(groupPosition).getName())) {
                        categories.remove(i);
                    }
                }
                RW.writeCategories(context, categories, "categories");

                Intent intent = new Intent(context, CategoriesActivity.class);
                context.startActivity(intent);
            }
        });

        lblListHeader.setText(headerTitle);
        setProgressBar(headerprogressbar, ((Category) getGroup(groupPosition)));
        return convertView;
    }

    public void setProgressBar(ProgressBar progressBar, Category c){
        try{
            progressBar.setVisibility(View.INVISIBLE);
            if(c.getLimit() != 0){
                progressBar.setMax(100);
                progressBar.setVisibility(View.VISIBLE);
            }
            int progressvalue = (int)(c.getSum()/(c.getLimit()/100));
            if(progressvalue < 60)
                progressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            else if(progressvalue < 80)
                progressBar.getProgressDrawable().setColorFilter(Color.parseColor("#ffa500"), PorterDuff.Mode.SRC_IN);
            else
                progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            progressBar.setProgress(progressvalue);
        }catch(Exception ex){
            ex.printStackTrace();
            progressBar.setProgress(0);
        }
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
