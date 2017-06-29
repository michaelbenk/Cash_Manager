package com.pr.se.cash_manager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import au.com.bytecode.opencsv.CSVWriter;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

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

    private String TAG = "MainActivity";

    private SharedPreferences prefs = null;
    private boolean isListSelected = false;
    private List<Expense> list;
    private ArrayAdapter adapter;
    private ListView listView;
    private Toolbar toolbar;
    private List<Filter> filter;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    private GregorianCalendar gregorianCalendar = new GregorianCalendar();
    private Bitmap image;
    private GregorianCalendar gc;
    private List<Expense> limitList = new LinkedList<>();
    private List<Category> cat = new LinkedList<>();
    private List<Category> catout = new LinkedList<>();

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
        Option in der Toolbar mit Menü: Export, Setting, Logout
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_export) {
            exportDialog();
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
     * builds and shows export dialog
     */
    public void exportDialog(){
        final String[] dList = {"All Expenses","Current Filter"};
        final ArrayList<String> dialogList = new ArrayList();


        dialogList.add("all");
        dialogList.add("current");


        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Choose export option")
                .setSingleChoiceItems(dList, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg0) {
                        ListView lv = ((AlertDialog) dialog).getListView();
                        lv.setTag(arg0);
                    }
                })

                .setNeutralButton("Cancel",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.cancel();
                            }
                        })


                .setPositiveButton(
                        "CSV",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                ListView lw = ((AlertDialog)dialog).getListView();
                                int selected = -1;
                                if(lw.getTag() != null){
                                    selected = (Integer) lw.getTag();
                                }

                                exportExpensesCSV(selected);
                            }
                        })

                .setNegativeButton(
                        "XLS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ListView lw = ((AlertDialog)dialog).getListView();
                                int selected = -1;
                                if(lw.getTag() != null){
                                    selected = (Integer) lw.getTag();
                                }

                                exportExpensesXLS(selected);
                            }
                        }
                )
                .create();
        alertDialog.show();
    }

    /**
     * Exports all or currently displayed expenses to CSV file
     * @param mode a int that decides whether all expenses (= 0) or the currently displayed expenses (= 1) should be exported
     */
    public void exportExpensesCSV(int mode) {

        List<Expense> exportList = this.list;

        Date dateInstance = Calendar.getInstance().getTime();

        // Display a date in day, month, year, hour, minutes, seconds format
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_hh:mm:ss");
        String today = formatter.format(dateInstance);

        final String fileName = "Cash_Manager_" + today + ".csv";

        //Saving file in external storage
        File sdCard = null;
        File directory = null;
        if(isExternalStorageWritable()) {
            sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            directory = new File(sdCard.getAbsolutePath() + "/Cash Manager");
        } else {
            directory = new File("/Documents/Cash Manager");
        }

        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }

        //file path
        File file = new File(directory, fileName);

        //write list into directory
        if (mode == 0) {  //unfiltered list

            try{
                exportList = RW.readExpenses(this, "expenses");

                CSVWriter writer = new CSVWriter(new FileWriter(file));

                String firstLine = "date,category,price,description";
                writer.writeNext(firstLine.split(","));

                // feed in your array (or convert your data to an array)
                String[] entries;
                int row = 1;



                for (Expense ex : exportList) {
                    String date = ex.getDate();
                    String category = ex.getCategory();
                    String price = ex.getSum() + "";
                    String description = ex.getDescription();
                    String line = date + "," + category + "," + price + "," + description;
                    entries = line.split(",");
                    writer.writeNext(entries);
                    row++;
                }

                writer.close();

                Toast.makeText(MainActivity.this, "Saved " + fileName + " to Documents/Cash Manager!", Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                Log.e(TAG, "Couldn't write unfiltered CSV-File", e);
            }
        } else if(mode == 1){ //filtered list
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(file));

                String firstLine = "date,category,price,description";
                writer.writeNext(firstLine.split(","));

                // feed in your array (or convert your data to an array)
                String[] entries;
                int row = 1;

                for (Expense ex : exportList) {
                    String date = ex.getDate();
                    String category = ex.getCategory();
                    String price = ex.getSum() + "";
                    String description = ex.getDescription();
                    String line = date + "," + category + "," + price + "," + description;
                    entries = line.split(",");
                    writer.writeNext(entries);
                    row++;
                }

                writer.close();

                Toast.makeText(MainActivity.this, "Saved " + fileName + " to Documents/Cash Manager!", Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                Log.e(TAG, "Couldn't write filtered CSV-File", e);
            }
        } else {
            Toast.makeText(MainActivity.this, "Please choose what data you would like to export!", Toast.LENGTH_LONG).show();
            exportDialog();
        }
    }

    /**
     * Exports all or currently displayed expenses to XLS file
     * @param mode a int that decides whether all expenses (= 0) or the currently displayed expenses (= 1) should be exported
     */
    public void exportExpensesXLS(int mode){

        List<Expense> exportList = this.list;

        Date dateInstance = Calendar.getInstance().getTime();

        // Display a date in day, month, year, hour, minutes, seconds format
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_hh:mm:ss");
        String today = formatter.format(dateInstance);

        //Saving file in external storage
        File sdCard = null;
        File directory = null;
        if(isExternalStorageWritable()) {
            sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            directory = new File(sdCard.getAbsolutePath() + "/Cash Manager");
        } else {
            directory = new File("/Documents/Cash Manager");
        }


        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }

        final String fileName = "Cash_Manager_" + today + ".xls";

        //file path
        File file = new File(directory, fileName);

        //write list into directory
        if (mode == 0) { //all

            exportList = RW.readExpenses(this, "expenses");

            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;

            try {
                workbook = Workbook.createWorkbook(file, wbSettings);
                //Excel sheet name. 0 represents first sheet
                WritableSheet sheet = workbook.createSheet("Expenses", 0);

                try {
                    sheet.addCell(new Label(0, 0, "Date")); // column and row
                    sheet.addCell(new Label(1, 0, "Category"));
                    sheet.addCell(new Label(2, 0, "Price"));
                    sheet.addCell(new Label(3, 0, "Description"));
                    int row = 1;
                    for (Expense ex : exportList) {

                        String date = ex.getDate();
                        String category = ex.getCategory();
                        String price = ex.getSum() + "";
                        String description = ex.getDescription();

                        sheet.addCell(new Label(0, row, date)); // column and row
                        sheet.addCell(new Label(1, row, category));
                        sheet.addCell(new Label(2, row, price));
                        sheet.addCell(new Label(3, row, description));
                        row++;
                    }

                } catch (RowsExceededException e) {
                    Log.e(TAG, "Too many rows when trying to create XLS for all expenses", e);
                } catch (WriteException e) {
                    Log.e(TAG, "Couldn't write into XLS for all expenses", e);
                }
                workbook.write();

                Toast.makeText(MainActivity.this, "Saved " + fileName + " to Documents/Cash Manager!", Toast.LENGTH_LONG).show();

                try {
                    workbook.close();
                } catch (WriteException e) {
                    Log.e(TAG, "Couldn't close XLS file for all expenses", e);
                }
            } catch (IOException e) {
                Log.e(TAG, "Couldn't create XLS file for all expenses", e);
            }
        } else if(mode == 1){    //filted

            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;

            try {
                workbook = Workbook.createWorkbook(file, wbSettings);
                //Excel sheet name. 0 represents first sheet
                WritableSheet sheet = workbook.createSheet("Expenses", 0);

                try {
                    //add titles
                    sheet.addCell(new Label(0, 0, "Date")); // column and row
                    sheet.addCell(new Label(1, 0, "Category"));
                    sheet.addCell(new Label(2, 0, "Price"));
                    sheet.addCell(new Label(3, 0, "Description"));

                    //add values
                    int row = 1;
                    for (Expense ex : exportList) {

                        String date = ex.getDate();
                        String category = ex.getCategory();
                        String price = ex.getSum() + "";
                        String description = ex.getDescription();

                        sheet.addCell(new Label(0, row, date)); // column and row
                        sheet.addCell(new Label(1, row, category));
                        sheet.addCell(new Label(2, row, price));
                        sheet.addCell(new Label(3, row, description));
                        row++;
                    }

                } catch (RowsExceededException e) {
                    Log.e(TAG, "Too many rows when trying to create XLS for filtered expenses", e);;
                } catch (WriteException e) {
                    Log.e(TAG, "Couldn't write XLS for filtered expenses", e);
                }
                workbook.write();

                Toast.makeText(MainActivity.this, "Saved " + fileName + " to Documents/Cash Manager!", Toast.LENGTH_LONG).show();

                try {
                    workbook.close();
                } catch (WriteException e) {
                    Log.e(TAG, "Coulnd't close XLS for filtered expenses", e);
                }
            } catch (IOException e) {
                Log.e(TAG, "Couldn't create XLS for filtered expenses", e);
            }
        } else{
            Toast.makeText(MainActivity.this, "Please choose what data you would like to export!", Toast.LENGTH_LONG).show();
            exportDialog();
        }

    }

    /**
     * checks if there is permission to write to external storage
     * @return true if permission to write to external storage; false if no permission
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    /**
     * Ausgaben werden sortiert
     * indicateChart aufruf => erstellen des Diagramms
     * listView wird erstellt:
     *      kurzer Klick: ShowDetails
     *      langer Klick: setSelected
     */
    public void updateList() {
        this.list = RW.readExpenses(this, "expenses");
        updateRecurringExpenses(list);
        this.list = RW.readExpenses(this, "expenses");

        // Check limit ----------------------------------------------------------------------------------------------------------------------------
        List<Category> cate = new LinkedList<>();
        gc = new GregorianCalendar();



        if(cat.size() == 0) {
            for(Expense ex : list){
                try{
                    if(gc.getTime().getMonth() == sdf.parse(ex.getDate()).getMonth() && gc.getTime().getYear() == sdf.parse(ex.getDate()).getYear()){
                        limitList.add(ex);
                    }
                }
                catch(Exception e){
                    Log.e(TAG, "Couldn't get current time when updating list", e);
                }
            }
            this.cat = RW.readCategories(this, "categories");
            for (Category ca : cat) {
                if(ca.getLimit() != 0){
                    ca.zeroSum();
                    for(Expense e : limitList){
                        if(e.getCategory().equals(ca.getName()))
                            ca.addSum(e.getSum());
                    }
                    if(ca.getLimit() < ca.getSum())
                        Toast.makeText(MainActivity.this, getString(R.string.view_exceeded)+ca.getName(), Toast.LENGTH_SHORT).show();
                }
                for (Category c : ca.getSubCategories()) {
                    if(c.getLimit() != 0){
                        c.zeroSum();
                        for(Expense e : limitList){
                            if(e.getCategory().equals(c.getName()))
                                c.addSum(e.getSum());
                        }
                        if(c.getLimit() < c.getSum())
                            Toast.makeText(MainActivity.this, getString(R.string.view_exceeded)+c.getName(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        RW.writeCategories(MainActivity.this, cat, "categories");

        this.filter = RW.readFilter(this, "filters");
        if (filter.size() != 0) { // Wenn Filter gesetzt wurde
            String recurringOrNot = "";
            for (Filter subfilter : filter.get(0).getSubfilter()) {
                if (subfilter.isCheck()) {
                    recurringOrNot = subfilter.getFilter();

                }
            }
            String period = "";
            for (Filter subfilter : filter.get(1).getSubfilter()) {
                if (subfilter.isCheck()) {
                    period = subfilter.getFilter();

                }
            }
            List<String> categorieFilter = new ArrayList<>();
            for (Filter subfilter : filter.get(2).getSubfilter()) {
                if (subfilter.isCheck()) {
                    categorieFilter.add(subfilter.getFilter());
                }
            }
            if (!recurringOrNot.equals(getString(R.string.filter_all))) {
                List<Expense> hilf = new ArrayList<>();
                for (Expense ex : list) {
                    if (recurringOrNot.equals(getString(R.string.view_recurring)) && (ex instanceof RecurringExpense)) {
                        hilf.add(ex);
                    } else if (recurringOrNot.equals(getString(R.string.view_non_recurring)) && !(ex instanceof RecurringExpense)) {
                        hilf.add(ex);
                    }
                }
                list = hilf;
            }

            if (!period.equals(getString(R.string.filter_all))) {
                List<Expense> hilf = new ArrayList<>();
                Date today = new Date();
                gregorianCalendar.setTime(today);

                if (period.equals(getString(R.string.filter_lastWeek))) {
                    gregorianCalendar.add(Calendar.WEEK_OF_YEAR, -1);
                    gregorianCalendar.add(Calendar.MONTH, 1);
                } else if (period.equals(getString(R.string.filter_lastMonth)))
                    gregorianCalendar.add(Calendar.MONTH, -1);
                else if (period.equals(getString(R.string.filter_lastYear)))
                    gregorianCalendar.add(Calendar.YEAR, -1);

                Date time = gregorianCalendar.getTime();

                for (Expense ex : list) {
                    try {
                        Date date = sdf.parse(ex.getDate());
                        if (time.compareTo(date) <= 0 && date.compareTo(today) <= 0) {
                            hilf.add(ex);
                        }
                    } catch (ParseException e) {
                        Log.e(TAG, "Couldn't parse date when filtering 'All'", e);
                    }
                }
                list = hilf;
            }
            if (!categorieFilter.get(0).equals(getString(R.string.filter_all))) {
                List<Expense> hilf = new ArrayList<>();
                for (Expense ex : list) {
                    for (String cat:categorieFilter){
                        if (ex.getCategory().equals(cat)){
                            hilf.add(ex);
                        }
                    }
                }
                list = hilf;
            }
        }

        this.indicateChart();

        this.adapter = new ListArrayAdapter(MainActivity.this, this.list);
        this.listView = (ListView) this.findViewById(android.R.id.list);
        this.listView.setAdapter(this.adapter);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ShowDetails.class);
                intent.putExtra("update", true);
                intent.putExtra("id", list.get(position).getId());
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

    private void updateRecurringExpenses(List<Expense> list) {
        for (Expense ex : list) {
            if (ex instanceof RecurringExpense) {
                try {
                    Date nextDate = sdf.parse(((RecurringExpense) ex).getDate_next());
                    if (nextDate.compareTo(new Date()) <= 0) {
                        //Recurring Expense next Date aktuallisieren und neue Expenses erstellen
                        list.remove(ex);
                        list.add(newRecurringExpensesBeforeToday((RecurringExpense) ex));
                    }
                } catch (ParseException e) {
                    Log.e(TAG, "Couldn't parse date when updating reccuring expensees", e);
                }
            }
        }
        RW.writeExpenses(MainActivity.this, list, "expenses");
    }

    // Erstellt alle Ausgaben die laut wiederkehrender Ausgabe vor bzw. heute getätigt wurden
    private RecurringExpense newRecurringExpensesBeforeToday(RecurringExpense element) {
        Expense newExpense;
        Date today = new Date();

        try {
            if (sdf.parse(element.getDate()).compareTo(today) <= 0) { //Wenn die Ausgabe in der Vergangenheit liegt bzw. heute ist
                newExpense = new Expense(element.getSum(), element.getDate(), element.getCategory(), element.getDescription());
                newExpense.setRecurring_id(element.getId());
                try{
                    if (element.getImages().get(0) != null)
                        image = BitmapFactory.decodeByteArray(element.getImages().get(0), 0, element.getImages().get(0).length);
                    newExpense.addImage(image);
                }catch (Exception e){
                    //Wenn noch kein Rechnungsfoto gespeichert wurde soll nichts gemacht werden
                }
                list.add(newExpense);
            }
        } catch (ParseException e) {
            Log.e(TAG, "Couldn't parse  when creating new reccuring expense before today", e);
        }
        //Restliche vergangene Ausgaben erzeugen
        try {
            while (sdf.parse(element.getDate_next()).compareTo(today) <= 0) {
                newExpense = new Expense(element.getSum(), element.getDate_next(), element.getCategory(), element.getDescription());
                newExpense.setRecurring_id(element.getId());
                try{
                    if (element.getImages().get(0) != null)
                        image = BitmapFactory.decodeByteArray(element.getImages().get(0), 0, element.getImages().get(0).length);
                    newExpense.addImage(image);
                }catch (Exception e){
                    //Wenn noch kein Rechnungsfoto gespeichert wurde soll nichts gemacht werden
                }
                list.add(newExpense);

                element.setDate_next(sdf.format(calculateNextDate(sdf.parse(element.getDate_next()), element.getIntervall())));
            }
        } catch (ParseException e) {
            Log.e(TAG, "Couldn't parse  when adding past expenses", e);
        }
        RW.writeExpenses(MainActivity.this, list, "expenses");
        return element;
    }

    private Date calculateNextDate(Date date, String intervall) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);

        switch (intervall) {
            case "yearly":
                gregorianCalendar.add(Calendar.YEAR, 1);
                break;
            case "monthly":
                gregorianCalendar.add(Calendar.MONTH, 1);
                break;
            case "quarterly":
                gregorianCalendar.add(Calendar.MONTH, 3);
                break;
            case "daily":
                gregorianCalendar.add(Calendar.DAY_OF_YEAR, 1);
                break;
            case "weekly":
                gregorianCalendar.add(Calendar.WEEK_OF_YEAR, 1);
                break;
        }

        return gregorianCalendar.getTime();
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
        new File(this.getFilesDir(), "log");

        try {
            ArrayList<Category> categories = new ArrayList<>();
            Category cat1 = new Category("Food", 0,  true);
            Category sub1 = new Category("Restaurant", 0, false);

            Category cat3 = new Category("Car", 0, true);
            Category sub2 = new Category("Fuel", 0, false);
            Category sub3 = new Category("Insurance", 0, false);

            Category cat4 = new Category("Rent", 0, true);

            Category cat2 = new Category("Others", 500, true);

            cat1.addSubCategory(sub1);
            categories.add(cat1);

            cat3.addSubCategory(sub2);
            cat3.addSubCategory(sub3);
            categories.add(cat3);

            categories.add(cat4);
            categories.add(cat2);

            ArrayList<Expense> expenses = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, 2017);
            calendar.set(Calendar.MONTH, 5);
            calendar.set(Calendar.DAY_OF_MONTH, 1);

            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(Calendar.YEAR, 2020);
            calendar2.set(Calendar.MONTH, 5);
            calendar2.set(Calendar.DAY_OF_MONTH, 28);

            gregorianCalendar.setTime(calendar.getTime());
            Expense e1 = new Expense(76.23, sdf.format(gregorianCalendar.getTime()), sub1.toString(), "Birthday");
            expenses.add(e1);
            gregorianCalendar.add(Calendar.MONTH, -1);
            Expense e2 = new Expense(46.23, sdf.format(gregorianCalendar.getTime()), sub2.toString(), "450 km");
            expenses.add(e2);
            gregorianCalendar.add(Calendar.WEEK_OF_YEAR, -1);
            Expense e3 = new Expense(26.23, sdf.format(gregorianCalendar.getTime()), cat1.toString(), "");
            expenses.add(e3);
            gregorianCalendar.add(Calendar.WEEK_OF_YEAR, 3);
            Expense e4 = new Expense(6.23, sdf.format(gregorianCalendar.getTime()), cat1.toString(), "");
            expenses.add(e4);
            gregorianCalendar.add(Calendar.MONTH, -1);
            Expense e5 = new RecurringExpense(410.50, sdf.format(gregorianCalendar.getTime()), cat4.toString(), "flat", sdf.format(calendar2.getTime()), Interval.monthly.name());
            gregorianCalendar.add(Calendar.MONTH, 1);
            ((RecurringExpense) e5).setDate_next(sdf.format(gregorianCalendar.getTime()));
            expenses.add(e5);

            RW.writeCategories(this, categories, "categories");
            RW.writeExpenses(this, expenses, "expenses");
        } catch (Exception e) {
            Log.e(TAG, "Something went wrong in first run", e);
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
