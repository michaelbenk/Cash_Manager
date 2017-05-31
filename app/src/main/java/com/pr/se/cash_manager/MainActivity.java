package com.pr.se.cash_manager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The MainActivity is the main activity of the application. It implements the whole layout.
 * <br><br>
 * The main activity implements all necessary classes and methods as well as all further parts
 * of the layout called by the user.
 * test commit
 * @author Team 1
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences prefs = null;
    private boolean isListSelected = false;
    private List<Expense> list;
    private ArrayAdapter adapter;
    private ListView listView;
    private Toolbar toolbar;
    private List<Filter> filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        /*
            Start the application with LoginActivity if PassordLogin is activated
            Default: PasswordLogin is activated
         */
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isChecked = preferences.getBoolean("pref_password_login", false);
        if (isChecked) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        /*
            Delete Button rechts oben
            Selektierte Ausgabe werden gelöscht
         */
        ImageView delete = (ImageView) this.findViewById(R.id.activity_main_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Expense> expenses = RW.readExpenses(MainActivity.this, "expenses");
                Boolean[] selectedItems = ((ListArrayAdapter) adapter).getSelectedItems();
                for (int i = 0; i < selectedItems.length; i++) {
                    if (selectedItems[i]) {
                        for (Expense e : expenses){
                            if (e.equals(list.get(i))){
                                expenses.remove(e);
                                break;
                            }
                        }
                    }
                }
                RW.writeExpenses(MainActivity.this, expenses, "expenses");
                updateList();
                isListSelected = false;
                setToolbar();
            }
        });

        this.setToolbar();

        // Hauptmenü - Toggle Button in Toolbar links
        DrawerLayout drawer = (DrawerLayout) this.findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, this.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // NavigationView im Hauptmenü
        NavigationView navigationView = (NavigationView) this.findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*
            + Button rechts unten
            AddEditActivity wird aufgerufen bei Klick
         */
        FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.content_main_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });

        /*
            Kategorien Button in Toolbar
            CategoriesActivity wird geöffnet bei Klick
          */
        ImageView categories = (ImageView) this.findViewById(R.id.activity_main_categories);
        categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoriesActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });


        ImageView filter = (ImageView) this.findViewById(R.id.activity_main_filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FilterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });


        this.prefs = getSharedPreferences("com.pr.se.cash_manager", MODE_PRIVATE);

        if (prefs.getBoolean("firstrun", true)) {
            this.firstRun();
        } else {
            this.updateList();
        }
    }

    /*
        Diagramm neu erstellen und Liste neu laden
     */
    @Override
    public void onResume() {
        super.onResume();
        this.updateList();
    }

    /*
        Hauptmenü: Export, Setting Logout
        Zurück Button
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (this.isListSelected) {
            isListSelected = false;
            ((ListArrayAdapter) adapter).setSelected(false, 0);
            adapter.notifyDataSetChanged();
            this.setToolbar();
        } else {
            super.onBackPressed();
        }
    }

    /*
        Option in der Toolbar mit Menü: Add, Delete
        Keine Funktionalität dahinter!
        Wird das überhaupt gebraucht?
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*
        Option in der Toolbar mit Menü: Add, Delete
        Keine Funktionalität dahinter!
        Wird das überhaupt gebraucht?
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            return true;
        }

        return id == R.id.action_delete || super.onOptionsItemSelected(item);
    }

    /*
        Option in der Toolbar mit Menü: Export, Setting, Logout
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_export) {
            // TODO ExportActivity, ExportLayout, etc.

        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        }

        DrawerLayout drawer = (DrawerLayout) this.findViewById(R.id.activity_main_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Ausgaben werden sortiert
     * indicateChart aufruf => erstellen des Diagramms
     * listView wird erstellt:
     *      kurzer Klick: ShowDetails
     *      langer Klick: setSelected
     */
    public void updateList() {
        //TODO Wiederkehrende Ausgaben sollen auch wiederkehrend sein
        this.list = RW.readExpenses(this, "expenses");

        this.filter = RW.readFilter(this, "filters");
        if (filter.size() != 0) { // Wenn Filter gesetzt wurde
            String recurringOrNot = "";
            for (Filter subfilter : filter.get(0).getSubfilter()) {
                if (subfilter.isCheck()) {
                    recurringOrNot = subfilter.getFilter();

                }
            }
            //TODO andere Filter
            List<Expense> hilf = new ArrayList<>();
            for (Expense ex : list) {
                if (recurringOrNot.equals("wiederkehrende Kosten") && (ex instanceof Recurring_Expense)) {
                    hilf.add(ex);
                } else if (recurringOrNot.equals("nicht wiederkehrende Kosten") && !(ex instanceof Recurring_Expense)) {
                    hilf.add(ex);
                } else if (recurringOrNot.equals("Alle")){
                    hilf.add(ex);
                }
            }
            list = hilf;
        }

        this.indicateChart();

        this.adapter = new ListArrayAdapter(MainActivity.this, this.list);
        this.listView = (ListView) this.findViewById(android.R.id.list);
        this.listView.setAdapter(this.adapter);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                byte[] byteArray = null;

                if (list.get(position).getImages().size() != 0){
                    byteArray = list.get(position).getImages().get(0);
                }

                Intent intent = new Intent(MainActivity.this, ShowDetails.class);
                intent.putExtra("category", list.get(position).getCategory());
                intent.putExtra("description", list.get(position).getDescription());
                intent.putExtra("date", list.get(position).getDate());
                intent.putExtra("sum", String.valueOf(list.get(position).getSum()));
                intent.putExtra("update", true);
                intent.putExtra("id", list.get(position).getId());
                if (byteArray != null)
                    intent.putExtra("image", byteArray);
                if (list.get(position) instanceof Recurring_Expense){
                    intent.putExtra("dateto", ((Recurring_Expense)list.get(position)).getDate_to());
                    intent.putExtra("intervall", ((Recurring_Expense)list.get(position)).getIntervall());
                }

                MainActivity.this.startActivity(intent);
            }
        });
        this.listView.setLongClickable(true);
        this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ((ListArrayAdapter) adapter).setSelected(true, position);
                adapter.notifyDataSetChanged();
                isListSelected = true;
                setToolbar();

                return false;
            }
        });
    }

    /*
        Buttons in Toolbar werden sichtbar bzw. unsichtbar gemacht
     */
    private void setToolbar() {
        if (this.toolbar == null) {
            this.toolbar = (Toolbar) this.findViewById(R.id.activity_categories_toolbar);
            setSupportActionBar(toolbar);
        }

        if (this.isListSelected) {
            this.findViewById(R.id.activity_main_delete).setVisibility(View.VISIBLE);
            this.findViewById(R.id.activity_main_categories).setVisibility(View.GONE);
            this.findViewById(R.id.activity_main_filter).setVisibility(View.GONE);
            this.toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        } else {
            this.findViewById(R.id.activity_main_delete).setVisibility(View.GONE);
            this.findViewById(R.id.activity_main_categories).setVisibility(View.VISIBLE);
            this.findViewById(R.id.activity_main_filter).setVisibility(View.VISIBLE);
            this.toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    /*
        Für den ersten Start werden Testkategorien erstellt: Essen, Restaurant, Lebensmittel, Sonstiges
        Wo wird das Stringarray descriptions angezeigt?
     */
    private void firstRun() {
        new File(this.getFilesDir(), "categories");
        new File(this.getFilesDir(), "expenses");

        try {
            ArrayList<Category> categories = new ArrayList<>();
            Category cat1 = new Category("Food", true);
            Category sub1 = new Category("Restaurant", false);
            Category sub2 = new Category("Sweets", false);
            Category cat2 = new Category("Others", true);
            cat1.addSubCategory(sub1);
            cat1.addSubCategory(sub2);
            categories.add(cat1);
            categories.add(cat2);

            String[] description = new String[]{"Haushalt", "Freizeit", "Wohnung", "Essen"};
            ArrayList<Expense> expenses = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, 2017);
            calendar.set(Calendar.MONTH, 5);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(Calendar.YEAR, 2020);
            calendar2.set(Calendar.MONTH, 5);
            calendar2.set(Calendar.DAY_OF_MONTH, 28);
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy");

            Expense e1 = new Expense(246.23, sdf.format(calendar.getTime()), sub1.toString(), description[0]);
            expenses.add(e1);
            Expense e2 = new Expense(46.23, sdf.format(calendar.getTime()), sub2.toString(), description[1]);
            expenses.add(e2);
            Expense e3 = new Expense(26.23, sdf.format(calendar.getTime()), cat1.toString(), description[2]);
            expenses.add(e3);
            Expense e4 = new Expense(6.23, sdf.format(calendar.getTime()), sub1.toString(), description[3]);
            expenses.add(e4);
            Expense e5 = new Recurring_Expense(400.50, sdf.format(calendar.getTime()), cat2.toString(), "Miete", sdf2.format(calendar2.getTime()), Intervall.monatlich.name());
            expenses.add(e5);

            RW.writeCategories(this, categories, "categories");
            RW.writeExpenses(this, expenses, "expenses");
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.prefs.edit().putBoolean("firstrun", false).apply();

        this.updateList();
    }

    /*
        Erstellen des Diagramms
     */
    private void indicateChart() {
        PieChart chart = (PieChart) findViewById(R.id.content_main_piechart);

        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(1, 1, 1, 1);
        chart.setDragDecelerationFrictionCoef(1f);
        chart.setCenterText("Expenses");
        chart.setCenterTextColor(Color.DKGRAY);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(0);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(40f);
        chart.setTransparentCircleRadius(42f);
        chart.setDrawCenterText(true);
        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        ArrayList<PieEntry> entries = new ArrayList<>();

        Map<String, Float> categories = new HashMap<>();

        for (Expense e : this.list) {
            double sum;
            if (categories.containsKey(e.getCategory())) {
                sum = categories.get(e.getCategory()).doubleValue();
                categories.remove(e.getCategory());
                categories.put(e.getCategory(), (float)(sum + e.getSum()));
            }else
                categories.put(e.getCategory(), (float)e.getSum());
        }

        for (Map.Entry<String, Float> e : categories.entrySet()){
            entries.add(new PieEntry(e.getValue(), e.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.DKGRAY);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();

        chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(12f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        chart.setDrawEntryLabels(false);
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTextSize(12f);
    }
}
