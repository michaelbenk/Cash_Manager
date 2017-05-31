package com.pr.se.cash_manager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
        //TODO Toolbar zurück
        //TODO Keine Bewegung bei Update

        ExpandableListView listView = (ExpandableListView) this.findViewById(R.id.activity_filter_ex_list);
        filterHashMap = new HashMap<>();
        parent = new ArrayList<>();
        parent.add(this.getString(R.string.filter_Ausgabentyp));
        parent.add(this.getString(R.string.filter_Zeitraum));
        parent.add(this.getString(R.string.filter_kategorien));

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

        filterHashMap.put(this.getString(R.string.filter_Ausgabentyp), filters.get(0).getSubfilter());
        filterHashMap.put(this.getString(R.string.filter_Zeitraum), filters.get(1).getSubfilter());
        filterHashMap.put(this.getString(R.string.filter_kategorien), filters.get(2).getSubfilter());
    }

    private void firstFilter() {
        List<Filter> ausgabentypen = new ArrayList<>();
        ausgabentypen.add(new Filter(this.getString(R.string.filter_Alle), true));
        ausgabentypen.add(new Filter(this.getString(R.string.recurring), false));
        ausgabentypen.add(new Filter(this.getString(R.string.not_recurring), false));
        filterHashMap.put(this.getString(R.string.filter_Ausgabentyp), ausgabentypen);

        List<Filter> zeitraum = new ArrayList<>();
        zeitraum.add(new Filter(this.getString(R.string.filter_Alle), true));
        zeitraum.add(new Filter(this.getString(R.string.filter_letzteWoche), false));
        zeitraum.add(new Filter(this.getString(R.string.filter_letzterMonat), false));
        zeitraum.add(new Filter(this.getString(R.string.filter_letztesJahr), false));
        filterHashMap.put(this.getString(R.string.filter_Zeitraum), zeitraum);

        List<Category> kategorien = RW.readCategories(this, "categories");
        List<Filter> kat = new ArrayList<>();
        kat.add(new Filter(this.getString(R.string.filter_Alle), true));
        for (Category c : kategorien) {
            kat.add(new Filter(c.toString(), false));
            for (Category sub : c.getSubCategories()) {
                kat.add(new Filter(sub.toString(), false));
            }
        }
        filterHashMap.put(this.getString(R.string.filter_kategorien), kat);
        filters.add(new Filter(getString(R.string.filter_Ausgabentyp), false, ausgabentypen));
        filters.add(new Filter(getString(R.string.filter_Zeitraum), false, zeitraum));
        filters.add(new Filter(getString(R.string.filter_kategorien), false, kat));
        RW.writeFilter(this, filters, "filters");
        this.prefs.edit().putBoolean("firstFilter", false).apply();
    }

    private Boolean[] convertToBooleanArray(Object array) {
        Class ofArray = array.getClass().getComponentType();
        if (ofArray.isPrimitive()) {
            List ar = new ArrayList();
            int length = Array.getLength(array);
            for (int i = 0; i < length; i++) {
                ar.add(Array.get(array, i));
            }

            Boolean[] booleanArray = Arrays.copyOf(ar.toArray(), ar.toArray().length, Boolean[].class);
            return booleanArray;
        } else {
            Boolean[] booleanArray = Arrays.copyOf((Object[]) array, ((Object[]) array).length, Boolean[].class);
            return booleanArray;
        }
    }

    static String[] convertToStringArray(Object array) {
        Class ofArray = array.getClass().getComponentType();
        if (ofArray.isPrimitive()) {
            List ar = new ArrayList();
            int length = Array.getLength(array);
            for (int i = 0; i < length; i++) {
                ar.add(Array.get(array, i));
            }
            String[] stringArray = Arrays.copyOf(ar.toArray(), ar.toArray().length, String[].class);
            return stringArray;
        } else {
            String[] stringArray = Arrays.copyOf((Object[]) array, ((Object[]) array).length, String[].class);
            return stringArray;
        }
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
}
