package com.pr.se.cash_manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {
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
    public void onBackPressed() {
        Intent intent = new Intent(CategoriesActivity.this, MainActivity.class);
        startActivity(intent);
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

    private void updateList() {
        final List<Category> list = RW.readCategories(this, "categories");
        final ExpandableListAdapter adapter = new ExpandableListAdapter(this);
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
                Intent intent = new Intent(CategoriesActivity.this, CategoryDetailsActivity.class);

                intent.putExtra("cat", list.get(groupPosition).getId());
                intent.putExtra("subCat", list.get(groupPosition).getSubCategories().get(childPosition).getId());
                CategoriesActivity.this.startActivity(intent);
                return true;
            }

        });

        for (int i = 0; i < adapter.getGroupCount(); i++) {
            listView.expandGroup(i);
        }
    }
}
