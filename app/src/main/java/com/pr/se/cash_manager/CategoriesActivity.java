package com.pr.se.cash_manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoriesActivity extends AppCompatActivity {
    private ArrayList<Category> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_categories_toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.activity_categories_ex_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, CategoryDetailsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });

        updateList();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.updateList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateList() {
        list = RW.readCategories(this, "categories");
        HashMap<Category, ArrayList<Category>> categoryArrayListHashMap = new HashMap<>();

        //TODO Was ist mit Kategorien in Kategorien in Kategorien ?
        for (Category e : list) {
            categoryArrayListHashMap.put(e, (ArrayList<Category>) e.getCategories());
        }

        final ExListAdapter adapter = new ExListAdapter(this, list, categoryArrayListHashMap);
        final ExpandableListView listView = (ExpandableListView) this.findViewById(R.id.activity_categories_ex_list);
        listView.setAdapter(adapter);
        listView.setGroupIndicator(null);

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if(parent.isGroupExpanded(groupPosition)) {
                    parent.collapseGroup(groupPosition);
                } else {
                    parent.expandGroup(groupPosition);
                }

                return true;
            }
        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //TODO Wo zur Hölle wird definiert das bei Click aufn Parent de DetailsActivity aufgrufen wird? Do nemli ned

                Intent intent = new Intent(CategoriesActivity.this, CategoryDetailsActivity.class);

                intent.putExtra("categories_name",list.get(groupPosition).getCategories().get(childPosition).getName());
                CategoriesActivity.this.startActivity(intent);
                return true;
            }

        });



        for (int i = 0; i < adapter.getGroupCount(); i++) {
            listView.expandGroup(i);
        }
    }
}
