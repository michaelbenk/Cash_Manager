package com.pr.se.cash_manager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FilterActivity extends AppCompatActivity {
    private List<Filter> filters = new ArrayList<>();
    private HashMap<String, List<Filter>> filterHashMap;
    private List<String> parent;
    private SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_filter_toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ExpandableListView listView = (ExpandableListView) this.findViewById(R.id.activity_filter_ex_list);
        filterHashMap = new HashMap<>();
        parent = new ArrayList<>();
        parent.add(this.getString(R.string.filter_expenseType));
        parent.add(this.getString(R.string.filter_period));
        parent.add(this.getString(R.string.filter_categories));

        this.prefs = getSharedPreferences("com.pr.se.cash_manager", MODE_PRIVATE);

        if (prefs.getBoolean("firstFilter", true)) {
            firstFilter();
        } else {
            updateFilterList();
        }

        ExpandableListAdapterFilter expandableListAdapterFilter = new ExpandableListAdapterFilter(this, filterHashMap, parent);
        listView.setAdapter(expandableListAdapterFilter);
        listView.setGroupIndicator(null);

        for (int i = 0; i < expandableListAdapterFilter.getGroupCount(); i++) {
            listView.expandGroup(i);
        }

        TextView save = (TextView) this.findViewById(R.id.activity_filter_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RW.writeFilter(FilterActivity.this, filters, "filters");

                Intent intent = new Intent(FilterActivity.this, MainActivity.class);
                startActivity(intent);
            }

        });
    }

    private void updateFilterList() {
        filters = RW.readFilter(this, "filters");

        filterHashMap.put(this.getString(R.string.filter_expenseType), filters.get(0).getSubfilter());
        filterHashMap.put(this.getString(R.string.filter_period), filters.get(1).getSubfilter());
        if (prefs.getBoolean("CategorieChange", true)){
            categoryFilter();
            filters.remove(2);
            filters.add(new Filter(getString(R.string.filter_categories), false, filterHashMap.get(this.getString(R.string.filter_categories))));
            RW.writeFilter(this, filters, "filters");
            this.prefs.edit().putBoolean("CategorieChange", false).apply();
        }else
            filterHashMap.put(this.getString(R.string.filter_categories), filters.get(2).getSubfilter());


    }

    private void firstFilter() {
        List<Filter> expenseType = new ArrayList<>();
        expenseType.add(new Filter(this.getString(R.string.filter_all), true));
        expenseType.add(new Filter(this.getString(R.string.view_recurring), false));
        expenseType.add(new Filter(this.getString(R.string.view_non_recurring), false));
        filterHashMap.put(this.getString(R.string.filter_expenseType), expenseType);

        List<Filter> period = new ArrayList<>();
        period.add(new Filter(this.getString(R.string.filter_all), true));
        period.add(new Filter(this.getString(R.string.filter_lastWeek), false));
        period.add(new Filter(this.getString(R.string.filter_lastMonth), false));
        period.add(new Filter(this.getString(R.string.filter_lastYear), false));
        filterHashMap.put(this.getString(R.string.filter_period), period);

        categoryFilter();

        filters.add(new Filter(getString(R.string.filter_expenseType), false, filterHashMap.get(this.getString(R.string.filter_expenseType))));
        filters.add(new Filter(getString(R.string.filter_period), false, filterHashMap.get(this.getString(R.string.filter_period))));
        filters.add(new Filter(getString(R.string.filter_categories), false, filterHashMap.get(this.getString(R.string.filter_categories))));
        RW.writeFilter(this, filters, "filters");
        this.prefs.edit().putBoolean("firstFilter", false).apply();
    }

    private void categoryFilter() {
        List<Category> categories = RW.readCategories(this, "categories");
        List<Filter> kat = new ArrayList<>();
        kat.add(new Filter(this.getString(R.string.filter_all), true));
        for (Category c : categories) {
            kat.add(new Filter(c.toString(), false));
            for (Category sub : c.getSubCategories()) {
                kat.add(new Filter(sub.toString(), false));
            }
        }
        filterHashMap.put(this.getString(R.string.filter_categories), kat);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FilterActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
